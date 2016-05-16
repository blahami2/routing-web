/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.data.basic.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.util.Map;
import com.google.api.services.script.model.*;
import com.google.api.services.script.Script;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GoogleScriptConnector {

    private static final int TIMEOUT = 380000;
    private static final String ACCESS_TYPE = "offline";

    private String applicationName = "RoutingClient";
    private final java.io.File dataStoreFile = new java.io.File( System.getProperty( "user.home" ), ".credentials/script-java-quickstart.json" );
    private FileDataStoreFactory dataStoreFactory;
    private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private HttpTransport httpTransport;

    /**
     * Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/script-java-quickstart.json
     */
    private final List<String> SCOPES = Arrays.asList( "https://www.googleapis.com/auth/drive", "https://www.googleapis.com/auth/spreadsheets" );

    private final InputStream jsonCredentials;

    public GoogleScriptConnector( String applicationName, InputStream jsonCredentials ) throws IOException {
        this.applicationName = applicationName;
        this.jsonCredentials = jsonCredentials;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory( dataStoreFile );
        } catch ( GeneralSecurityException ex ) {
            throw new IOException( ex );
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = GoogleScriptConnector.class.getResourceAsStream( "/client_secret.json" );
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load( jsonFactory, new InputStreamReader( jsonCredentials ) );

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder( httpTransport, jsonFactory, clientSecrets, SCOPES )
                .setDataStoreFactory( dataStoreFactory )
                .setAccessType( ACCESS_TYPE )
                .build();
        AppIdentityCredential credential = new AppIdentityCredential(SCOPES );
        
        //        Credential credential = new AuthorizationCodeInstalledApp( flow, new LocalServerReceiver() ).authorize( "user" );
                System.out.println( "Credentials saved to " + dataStoreFile.getAbsolutePath() );
        return credential;
    }

    /**
     * Create a HttpRequestInitializer from the given one, except set the HTTP
     * read timeout to be longer than the default (to allow called scripts time
     * to execute).
     *
     * @param {HttpRequestInitializer} requestInitializer the initializer to
     * copy and adjust; typically a Credential object.
     * @return an initializer with an extended read timeout.
     */
    private static HttpRequestInitializer setHttpTimeout( final HttpRequestInitializer requestInitializer ) {
        return ( HttpRequest httpRequest ) -> {
            requestInitializer.initialize( httpRequest );
            // This allows the API to call (and avoid timing out on)
            // functions that take up to 6 minutes to complete (the maximum
            // allowed script run time), plus a little overhead.
            httpRequest.setReadTimeout( TIMEOUT );
        };
    }

    /**
     * Build and return an authorized Script client service.
     *
     * @return an authorized Script client service
     * @throws java.io.IOException
     */
    public Script getScriptService() throws IOException {
        Credential credential = authorize();
        return new Script.Builder( httpTransport, jsonFactory, setHttpTimeout( credential ) )
                .setApplicationName( applicationName )
                .build();
    }

    /**
     * Interpret an error response returned by the API and return a String
     * summary.
     *
     * @param operation the {@link Operation} returning an error response
     * @return summary of error response, or null if Operation returned no error
     */
    public String getScriptError( Operation operation ) {
        if ( operation.getError() == null ) {
            return null;
        }

        // Extract the first (and only) set of error details and cast as a Map.
        // The values of this map are the script's 'errorMessage' and
        // 'errorType', and an array of stack trace elements (which also need to
        // be cast as Maps).
        Map<String, Object> detail = operation.getError().getDetails().get( 0 );
        List<Map<String, Object>> stacktrace = (List<Map<String, Object>>) detail.get( "scriptStackTraceElements" );

        java.lang.StringBuilder sb = new StringBuilder( "\nScript error message: " );
        sb.append( detail.get( "errorMessage" ) );
        sb.append( "\nScript error type: " );
        sb.append( detail.get( "errorType" ) );

        if ( stacktrace != null ) {
            // There may not be a stacktrace if the script didn't start
            // executing.
            sb.append( "\nScript error stacktrace:" );
            for ( Map<String, Object> elem : stacktrace ) {
                sb.append( "\n  " );
                sb.append( elem.get( "function" ) );
                sb.append( ":" );
                sb.append( elem.get( "lineNumber" ) );
            }
        }
        sb.append( "\n" );
        return sb.toString();
    }

//    public void main( String[] args ) throws IOException {
//        // ID of the script to call. Acquire this from the Apps Script editor,
//        // under Publish > Deploy as API executable.
//        String scriptId = "ENTER_YOUR_SCRIPT_ID_HERE";
//        Script service = getScriptService();
//
//        // Create an execution request object.
//        ExecutionRequest request = new ExecutionRequest().setFunction( "getFoldersUnderRoot" );
//
//        try {
//            // Make the API request.
//            Operation op = service.scripts().run( scriptId, request ).execute();
//
//            // Print results of request.
//            if ( op.getError() != null ) {
//                // The API executed, but the script returned an error.
//                System.out.println( getScriptError( op ) );
//            } else {
//                // The result provided by the API needs to be cast into
//                // the correct type, based upon what types the Apps
//                // Script function returns. Here, the function returns
//                // an Apps Script Object with String keys and values,
//                // so must be cast into a Java Map (folderSet).
//                Map<String, String> folderSet = (Map<String, String>) ( op.getResponse().get( "result" ) );
//                if ( folderSet.size() == 0 ) {
//                    System.out.println( "No folders returned!" );
//                } else {
//                    System.out.println( "Folders under your root folder:" );
//                    for ( String id : folderSet.keySet() ) {
//                        System.out.printf( "\t%s (%s)\n", folderSet.get( id ), id );
//                    }
//                }
//            }
//        } catch ( GoogleJsonResponseException e ) {
//            // The API encountered a problem before the script was called.
//            e.printStackTrace( System.out );
//        }
//    }
    /**
     *
     * @param scriptId ID of the script to call. Acquire this from the Apps
     * Script editor, under Publish > Deploy as API executable.
     * @param request execution request object.
     * @return The result provided by the API needs to be cast into the correct
     * type, based upon what types the Apps Script function returns.
     * @throws java.io.IOException
     */
    public Operation runScript( String scriptId, ExecutionRequest request ) throws IOException {
        Script service = getScriptService();
        try {
            // Make the API request.
            Operation op = service.scripts().run( scriptId, request ).execute();

            // Print results of request.
            if ( op.getError() != null ) {
                // The API executed, but the script returned an error.
                throw new IOException( getScriptError( op ) );
            } else {
                return op;
            }
        } catch ( GoogleJsonResponseException ex ) {
            // The API encountered a problem before the script was called.
            throw new IOException( ex );
        }
    }
}
