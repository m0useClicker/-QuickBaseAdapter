package com.m0useclicker.quickbaseadapter.qbActions.login;

import android.util.Log;

import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.XmlObjectParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class QbLogin implements IAuthenticator {
    public static final String InvalidTicket = "";

    @Override
    public String getTicket(String realmSubDomain, String userId, String password) {
        if (isBlank(realmSubDomain)) {
            return InvalidTicket;
        }
        if (isBlank(userId)) {
            return InvalidTicket;
        }
        if (isBlank(password)) {
            return InvalidTicket;
        }

        URL url = null;
        try {
            url = getAuthUrl(realmSubDomain, userId, password);
        } catch (MalformedURLException e) {
            Log.e(QbLogin.class.getName(), "Unable to build auth url", e);
            return InvalidTicket;
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e(QbLogin.class.getName(), "Unable to open http connection", e);
            return InvalidTicket;
        }
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            XmlNamespaceDictionary dictionary = new XmlNamespaceDictionary();
            dictionary.set("", "");
            XmlObjectParser xmlObjectParser = new XmlObjectParser(dictionary);

            AuthResponse response = xmlObjectParser.parseAndClose(in, Charset.defaultCharset(), AuthResponse.class);

            return response.errcode == 0 ? response.ticket : InvalidTicket;
        } catch (IOException e) {
            Log.e(QbLogin.class.getName(), "Unexpected exception", e);
            return InvalidTicket;
        } finally {
            urlConnection.disconnect();
        }
    }

    private static URL getAuthUrl(final String subDomain, final String userId, final String password) throws MalformedURLException {
        String authUrl = String.format("https://%1$s.quickbase.com/db/main?a=API_Authenticate&username=%2$s&password=%3$s", subDomain, userId, password);
        return new URL(authUrl);
    }
}
