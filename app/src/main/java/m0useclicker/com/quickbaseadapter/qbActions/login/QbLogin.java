package m0useclicker.com.quickbaseadapter.qbActions.login;

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
    public String getTicket(String realmSubDomain, String userName, String password) {
        if (isBlank(realmSubDomain)) {
            return InvalidTicket;
        }
        if (isBlank(userName)) {
            return InvalidTicket;
        }
        if (isBlank(password)) {
            return InvalidTicket;
        }

        String authUrl = String.format("https://%1$s.quickbase.com/db/main?a=API_Authenticate&username=%2$s&password=%3$s", realmSubDomain, userName, password);
        URL url = null;
        try {
            url = new URL(authUrl);
        } catch (MalformedURLException e) {
            return InvalidTicket;
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
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
            return InvalidTicket;
        } finally {
            urlConnection.disconnect();
        }
    }
}
