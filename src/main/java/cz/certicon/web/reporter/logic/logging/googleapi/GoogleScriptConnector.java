package cz.certicon.web.reporter.logic.logging.googleapi;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.script.Script;
import com.google.api.services.script.model.*;
import cz.certicon.routing.data.basic.DataSource;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
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
     * <p>
     * If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/script-java-quickstart.json
     */
    private final List<String> SCOPES = Arrays.asList( "https://www.googleapis.com/auth/drive", "https://www.googleapis.com/auth/spreadsheets" );

    private final DataSource jsonCredentials;

    public GoogleScriptConnector( String applicationName, DataSource jsonCredentials ) throws IOException {
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
//        InputStream in = GoogleScriptConnector.class.getResourceAsStream( "/client_secret.json" );

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load( jsonFactory, new InputStreamReader( jsonCredentials.getInputStream() ) );

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder( httpTransport, jsonFactory, clientSecrets, SCOPES )
                .setDataStoreFactory( dataStoreFactory )
                .setAccessType( ACCESS_TYPE )
                .build();

        Credential credential = new AuthorizationCodeInstalledApp( flow, new LocalCallbackServer() ).authorize( "user" );
//        System.out.println( "Credentials saved to " + dataStoreFile.getAbsolutePath() );
        return credential;
//        throw new UnsupportedOperationException( "Not implemented yet - library missing" );
    }

    /**
     * Create a HttpRequestInitializer from the given one, except set the HTTP
     * read timeout to be longer than the default (to allow called scripts time
     * to execute).
     *
     * @param {HttpRequestInitializer} requestInitializer the initializer to
     *                                 copy and adjust; typically a Credential object.
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
     * @param scriptId ID of the script to call. Acquire this from the Apps
     *                 Script editor, under Publish > Deploy as API executable.
     * @param request  execution request object.
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

    public static class LocalCallbackServer implements VerificationCodeReceiver {

        volatile String code;
        private final int LOCAL_SERVER_PORT = 10006;

        @Override
        public synchronized String waitForCode() {

            try {
                this.wait();
            } catch ( Exception ex ) {
            }
            System.out.println( "returning code is -> " + code );
            return code;

        }

        @Override
        public String getRedirectUri() {

            new Thread( new MyThread() ).start();
            return "http://localhost:" + LOCAL_SERVER_PORT;
        }

        @Override
        public void stop() {
        }

        class MyThread implements Runnable {

            @Override
            public void run() {
                try {
                    //    return GoogleOAuthConstants.OOB_REDIRECT_URI;
                    ServerSocket ss = new ServerSocket( LOCAL_SERVER_PORT );
                    System.out.println( "server is ready..." );
                    Socket socket = ss.accept();
                    System.out.println( "new request...." );
                    InputStream is = socket.getInputStream();
                    StringWriter writer = new StringWriter();
                    String firstLine = null;

                    InputStreamReader isr = new InputStreamReader( is );
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader( isr );
                    String read = br.readLine();
                    firstLine = read;
                    OutputStream os = socket.getOutputStream();
                    PrintWriter out = new PrintWriter( os, true );

                    StringTokenizer st = new StringTokenizer( firstLine, " " );
                    st.nextToken();
                    String codeLine = st.nextToken();
                    st = new StringTokenizer( codeLine, "=" );
                    st.nextToken();
                    code = st.nextToken();

                    out.write( "RETURNED CODE IS " + code + "" );
                    out.flush();
//                is.close();

                    socket.close();

                    System.out.println( "Extracted coded is " + code );

                    synchronized ( LocalCallbackServer.this ) {
                        LocalCallbackServer.this.notify();
                    }
                    System.out.println( "return is " + sb.toString() );

                } catch ( IOException ex ) {
                    Logger.getLogger( LocalCallbackServer.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
        }
    }
}
