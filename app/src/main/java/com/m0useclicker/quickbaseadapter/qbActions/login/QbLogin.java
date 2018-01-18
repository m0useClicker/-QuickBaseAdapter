package com.m0useclicker.quickbaseadapter.qbActions.login;

import android.util.Log;

import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.XmlObjectParser;
import com.m0useclicker.quickbaseadapter.qbActions.login.responses.AuthResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class QbLogin implements IAuthenticator {
    public static final String invalidTicket = "";

    @Override
    public String getTicket(String realmSubDomain, String userId, String password) {
        AuthResponse authResponse = getAuthResponse(realmSubDomain, userId, password);
        return authResponse.errcode == 0? authResponse.ticket: invalidTicket;
    }

    @Override
    public AuthResponse getAuthResponse(String realmName, String userId, String password) {
        if (isBlank(realmName)) {
            return AuthResponse.invalidAuthResponse();
        }
        if (isBlank(userId)) {
            return AuthResponse.invalidAuthResponse();
        }
        if (isBlank(password)) {
            return AuthResponse.invalidAuthResponse();
        }

        URL url = null;
        try {
            url = getAuthUrl(realmName, userId, password);
        } catch (MalformedURLException e) {
            Log.e(QbLogin.class.getName(), "Unable to build auth url", e);
            return AuthResponse.invalidAuthResponse();
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e(QbLogin.class.getName(), "Unable to open http connection", e);
            return AuthResponse.invalidAuthResponse();
        }
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            XmlNamespaceDictionary dictionary = new XmlNamespaceDictionary();
            dictionary.set("", "");
            XmlObjectParser xmlObjectParser = new XmlObjectParser(dictionary);

            AuthResponse response = xmlObjectParser.parseAndClose(in, Charset.defaultCharset(), AuthResponse.class);

            return response;
        } catch (IOException e) {
            Log.e(QbLogin.class.getName(), "Unexpected exception", e);
            return AuthResponse.invalidAuthResponse();
        } finally {
            urlConnection.disconnect();
        }
    }


    private static URL getAuthUrl(final String subDomain, final String userId, final String password) throws MalformedURLException {
        String authUrl = String.format("https://%1$s.quickbase.com/db/main?a=API_Authenticate&username=%2$s&password=%3$s", subDomain, userId, password);
        return new URL(authUrl);
    }
}
