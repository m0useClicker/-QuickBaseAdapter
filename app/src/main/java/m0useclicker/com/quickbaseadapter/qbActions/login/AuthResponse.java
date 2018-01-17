package m0useclicker.com.quickbaseadapter.qbActions.login;

import com.google.api.client.util.Key;

public class AuthResponse {
    @Key
    String errtext;
    @Key
    String errdetail;
    @Key
    Long errcode;
    @Key
    String action;
    @Key
    String ticket;
}
