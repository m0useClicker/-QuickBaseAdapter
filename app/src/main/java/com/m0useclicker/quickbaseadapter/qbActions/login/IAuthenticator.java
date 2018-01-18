package com.m0useclicker.quickbaseadapter.qbActions.login;

public interface IAuthenticator {
    String getTicket(final String realmName, final String userId, final String password);
}
