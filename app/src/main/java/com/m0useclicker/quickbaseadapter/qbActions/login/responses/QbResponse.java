package com.m0useclicker.quickbaseadapter.qbActions.login.responses;

import com.google.api.client.repackaged.com.google.common.base.Objects;
import com.google.api.client.util.Key;

public class QbResponse {
    public static final long OkResponseCode = 0;
    public static final int StartingErrorCode = 10000;

    @Key
    public String errtext;
    @Key
    public String errdetail;
    @Key
    public Long errcode;
    @Key
    public String action;

    public QbResponse(){

    }

    public QbResponse(Long errorCode){
        this.errcode = errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QbResponse that = (QbResponse) o;
        return Objects.equal(errtext, that.errtext) &&
                Objects.equal(errdetail, that.errdetail) &&
                Objects.equal(errcode, that.errcode) &&
                Objects.equal(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(errtext, errdetail, errcode, action);
    }
}
