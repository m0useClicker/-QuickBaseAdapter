package com.m0useclicker.quickbaseadapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.m0useclicker.quickbaseadapter.qbActions.login.IAuthenticator;
import com.m0useclicker.quickbaseadapter.qbActions.login.QbLogin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserLoginTask authTask = null;

        authTask = new UserLoginTask("team", "qwe", "qwe", (IAuthenticator) new QbLogin());
        authTask.execute((Void) null);    }

    private String ticket;
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

            if (success) {
                finish();
            } else {
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}
