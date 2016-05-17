/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.data.basic.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GoogleServiceConnector {

    private static final int TIMEOUT = 380000;
    private static final String ACCESS_TYPE = "offline";

    private String applicationName = "RoutingClient";
    private final java.io.File dataStoreFile = new java.io.File( System.getProperty( "user.home" ), ".credentials/script-java-quickstart.json" );
    private FileDataStoreFactory dataStoreFactory;
    private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private HttpTransport httpTransport;

    private final List<String> SCOPES = Arrays.asList( "https://www.googleapis.com/auth/drive", "https://www.googleapis.com/auth/spreadsheets" );

    private final InputStream jsonCredentials;

    public GoogleServiceConnector( String applicationName, InputStream jsonCredentials ) throws IOException {
        this.applicationName = applicationName;
        this.jsonCredentials = jsonCredentials;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory( dataStoreFile );
        } catch ( GeneralSecurityException ex ) {
            throw new IOException( ex );
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize() throws IOException {
        return GoogleCredential.fromStream( jsonCredentials ).createScoped( SCOPES );
    }
}
