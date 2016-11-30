package cz.certicon.web.reporter.services;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.web.common.model.Algorithms;
import cz.certicon.web.reporter.logic.logging.ReportLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Service
@Scope( "singleton" )
public class ReportService {

    private final List<ReportLogger> loggers;
    private final Properties properties;

    public ReportService( @Autowired List<ReportLogger> loggers, @Autowired @Qualifier( "connectionProperties" ) Properties properties ) {
        this.loggers = loggers;
        this.properties = properties;
    }

    synchronized public void report( Coordinate from, Coordinate to, Algorithms algorithm, Metric metric, long regions, String message ) throws IOException {
        String link = properties.getProperty( "web.url" ) + "map?latFrom=" + from.getLatitude() + "&lonFrom=" + from.getLongitude() +
                "&latTo=" + to.getLatitude() + "&lonTo=" + to.getLongitude() +
                "&metric=" + metric.name() + "&algorithm=" + algorithm.name() + "&regions=" + regions + "&route=true";
        for ( ReportLogger logger : loggers ) {
            logger.log( from, to, algorithm, metric, message, link );
        }
    }
}
