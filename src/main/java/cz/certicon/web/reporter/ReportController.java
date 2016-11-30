package cz.certicon.web.reporter;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.web.common.model.Algorithms;
import cz.certicon.web.reporter.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@RestController
public class ReportController {

    private final ReportService reportService;
    private final Function<String, String> encodedMessageParser;

    public ReportController( @Autowired ReportService reportService, @Autowired Function<String, String> encodedMessageParser ) {
        this.reportService = reportService;
        this.encodedMessageParser = encodedMessageParser;
    }

    @RequestMapping( "/report" )
    public Result report( @RequestParam( "latFrom" ) double latFrom,
                          @RequestParam( "lonFrom" ) double lonFrom,
                          @RequestParam( "latTo" ) double latTo,
                          @RequestParam( "lonTo" ) double lonTo,
                          @RequestParam( "metric" ) String metricStr,
                          @RequestParam( "algorithm" ) String algorithmStr,
                          @RequestParam( "regions" ) long regions,
                          @RequestParam( "message" ) String message ) {
        Metric metric = Metric.valueOf( metricStr );
        Algorithms algorithms = Algorithms.valueOf( algorithmStr );
        Coordinate sourceCoordinate = new Coordinate( latFrom, lonFrom );
        Coordinate targetCoordinate = new Coordinate( latTo, lonTo );
        String decodedMessage = encodedMessageParser.apply( message );
        try {
            reportService.report( sourceCoordinate, targetCoordinate, algorithms, metric, regions, decodedMessage );
        } catch ( IOException ex ) {
            Logger.getLogger( ReportController.class.getName() ).log( Level.SEVERE, null, ex );
            return new Result( "FAIL" );
        }
        return new Result( "OK" );
    }

    public static class Result {

        private final String result;

        public Result( String result ) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }
    }
}
