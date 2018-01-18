package com.m0useclicker.quickbaseadapter.qbActions.login.responses;

import com.google.api.client.repackaged.com.google.common.base.Objects;
import com.google.api.client.util.Key;

public class AuthResponse extends QbResponse {
    @Key
    public String ticket;

    public AuthResponse() {
    }

    public AuthResponse(Long errorCode) {
        super(errorCode);
    }

    @Override
    public boolean equals(Object o) {
        AuthResponse that = (AuthResponse) o;
        return super.equals(o) &&
                Objects.equal(ticket, that.ticket);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(errtext, errdetail, errcode, action, ticket);
    }

    public static AuthResponse invalidAuthResponse(){
        return new AuthResponse(StartingErrorCode + 1L);
    }
}
