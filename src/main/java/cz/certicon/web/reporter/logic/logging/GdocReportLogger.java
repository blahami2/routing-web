package cz.certicon.web.reporter.logic.logging;

import com.google.api.services.script.model.ExecutionRequest;
import com.google.api.services.script.model.Operation;
import cz.certicon.routing.data.basic.FileDataSource;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.web.common.model.Algorithms;
import cz.certicon.web.reporter.logic.logging.googleapi.GoogleScriptConnector;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class GdocReportLogger extends AbstractReportLogger implements ReportLogger {

    private static final String APPLICATION_NAME = "RoutingClient";

    private final GoogleScriptConnector googleScriptConnector;
    private final String scriptId;

    public GdocReportLogger( String jsonCredentialsAbsolutePath, String scriptId ) throws IOException {
        this.googleScriptConnector = new GoogleScriptConnector( APPLICATION_NAME, new FileDataSource( new File( jsonCredentialsAbsolutePath ) ) );
        this.scriptId = scriptId;
    }

    @Override
    public void log( Coordinate from, Coordinate to, Algorithms algorithm, Metric priority, String message, String link ) throws IOException {
        ExecutionRequest request = new ExecutionRequest()
                .setFunction( "report" )
                .setParameters( Arrays.asList( getCoordinateFormatter().apply( from ), getCoordinateFormatter().apply( to ),
                        algorithm.name().toLowerCase(), priority.name().toLowerCase(), message, "=HYPERLINK(\"" + link + "\";\"display route on map\")" ) )
                .setDevMode( true );
        Operation op = googleScriptConnector.runScript( scriptId, request );
        op.getResponse().get( "result" );
    }
}
