package com.m0useclicker.quickbaseadapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.m0useclicker.quickbaseadapter.qbActions.login.IAuthenticator;
import com.m0useclicker.quickbaseadapter.qbActions.login.QbLogin;

/**
 * A login screen that offers login
 */
public class LoginActivity extends AppCompatActivity {
    private String ticket;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask authTask = null;

    // UI references.
    private AutoCompleteTextView userIdView;
    private EditText passwordView;
    private EditText realmSubDomainView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        userIdView = (AutoCompleteTextView) findViewById(R.id.userIdView);
        realmSubDomainView = (EditText) findViewById(R.id.realmSubDomain);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (authTask != null) {
            return;
        }

        userIdView.setError(null);
        passwordView.setError(null);
        realmSubDomainView.setError(null);

        String realmSubDomain = realmSubDomainView.getText().toString();
        String userId = userIdView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(userId)) {
            userIdView.setError(getString(R.string.error_userId_required));
            focusView = userIdView;
            cancel = true;
        }

        if(!TextUtils.isEmpty(realmSubDomain)){
            realmSubDomainView.setError(getString(R.string.error_realmDomain_required));
            focusView = userIdView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            authTask = new UserLoginTask(realmSubDomain, userId, password, new QbLogin());
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
            return !ticket.equals(QbLogin.InvalidTicket);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            authTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                passwordView.setError(getString(R.string.error_unable_authenticate));
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            authTask = null;
            showProgress(false);
        }
    }
}

