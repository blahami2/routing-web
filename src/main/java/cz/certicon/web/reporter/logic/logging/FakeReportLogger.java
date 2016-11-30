package cz.certicon.web.reporter.logic.logging;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.web.common.model.Algorithms;

import java.io.IOException;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class FakeReportLogger implements ReportLogger {
    @Override
    public void log( Coordinate from, Coordinate to, Algorithms algorithm, Metric priority, String message, String link ) throws IOException {
        // do nothing
    }
}
