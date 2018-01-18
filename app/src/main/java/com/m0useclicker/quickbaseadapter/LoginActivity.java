package com.m0useclicker.quickbaseadapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.m0useclicker.quickbaseadapter.qbActions.login.IAuthenticator;
import com.m0useclicker.quickbaseadapter.qbActions.login.QbLogin;

/**
 * A login screen that offers login
 */
public class LoginActivity extends AppCompatActivity {
    private final IAuthenticator authenticator = new QbLogin();
    private String ticket;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask authTask = null;

    // UI references.
    private AutoCompleteTextView userIdView;
    private EditText passwordView;
    private AutoCompleteTextView realmSubDomainView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        userIdView = (AutoCompleteTextView) findViewById(R.id.userIdView);
        realmSubDomainView = (AutoCompleteTextView) findViewById(R.id.realmSubDomain);
        passwordView = (EditText) findViewById(R.id.password);

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        userIdView.setError(null);
        passwordView.setError(null);
        realmSubDomainView.setError(null);

        boolean cancel = false;
        View focusView = userIdView;

        String password = passwordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        String userId = userIdView.getText().toString();
        if (TextUtils.isEmpty(userId)) {
            userIdView.setError(getString(R.string.error_userId_required));
            focusView = userIdView;
            cancel = true;
        }

        String realmSubDomain = realmSubDomainView.getText().toString();
        if(TextUtils.isEmpty(realmSubDomain)){
            realmSubDomainView.setError(getString(R.string.error_realmDomain_required));
            focusView = realmSubDomainView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            authTask = new UserLoginTask(realmSubDomain, userId, password, authenticator);
            authTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final IAuthenticator authenticator;
        private final String realmName;
        private final String userId;
        private final String password;

        UserLoginTask(String realmSubDomain, String userId, String password, IAuthenticator authenticator) {
            this.authenticator = authenticator;
            this.realmName = realmSubDomain;
            this.userId = userId;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ticket = authenticator.getTicket(realmName, userId, password);
            return !ticket.equals(QbLogin.invalidTicket);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            Log.i("TAG_LOGIN",String.valueOf(success));

            if (success) {
                finish();
            } else {
                authTask = null;
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}

