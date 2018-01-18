package com.m0useclicker.quickbaseadapter.qbActions.login;

import com.m0useclicker.quickbaseadapter.qbActions.login.responses.AuthResponse;

public interface IAuthenticator {
    String getTicket(final String realmName, final String userId, final String password);
    AuthResponse getAuthResponse(final String realmName, final String userId, final String password);
}
