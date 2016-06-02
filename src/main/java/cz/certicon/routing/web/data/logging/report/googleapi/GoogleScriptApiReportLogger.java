/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.data.logging.report.googleapi;

import com.google.api.services.script.model.ExecutionRequest;
import com.google.api.services.script.model.Operation;
import cz.certicon.routing.data.basic.FileSource;
import cz.certicon.routing.model.entity.Coordinate;
import cz.certicon.routing.web.data.basic.google.GoogleScriptConnector;
import cz.certicon.routing.web.data.logging.report.ReportLogger;
import cz.certicon.routing.web.model.AlgorithmType;
import cz.certicon.routing.web.model.Priority;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GoogleScriptApiReportLogger implements ReportLogger {

    private static final String APPLICATION_NAME = "RoutingClient";

    private final GoogleScriptConnector googleScriptConnector;
    private final String scriptId;

    public GoogleScriptApiReportLogger( String jsonCredentialsAbsolutePath, String scriptId ) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        Scanner sc = new Scanner( new File( jsonCredentialsAbsolutePath ) );
//        while ( sc.hasNextLine() ) {
//            sb.append( sc.nextLine() );
//        }
        this.googleScriptConnector = new GoogleScriptConnector( APPLICATION_NAME, new FileSource( new File( jsonCredentialsAbsolutePath ) ) );
        this.scriptId = scriptId;
    }

    @Override
    public void log( Coordinate from, Coordinate to, AlgorithmType algorithm, Priority priority, String message ) throws IOException {
        ExecutionRequest request = new ExecutionRequest()
                .setFunction( "report" )
                .setParameters( Arrays.asList( Formatter.formatCoordinates( from ), Formatter.formatCoordinates( to ),
                        algorithm.name().toLowerCase(), priority.name().toLowerCase(), message ) )
                .setDevMode( true );
        Operation op = googleScriptConnector.runScript( scriptId, request );
        op.getResponse().get( "result" );
    }

}
