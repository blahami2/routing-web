package cz.certicon.web.reporter.logic.logging;

import cz.certicon.routing.model.values.Coordinate;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public abstract class AbstractReportLogger implements ReportLogger {
    Function<Coordinate, String> coordinateFormatter;

    public AbstractReportLogger() {
        this.coordinateFormatter = c -> c.getLatitude() + "," + c.getLongitude();
    }

    public AbstractReportLogger( Function<Coordinate, String> coordinateFormatter ) {
        this.coordinateFormatter = coordinateFormatter;
    }

    public void setCoordinateFormatter( Function<Coordinate, String> coordinateFormatter ) {
        this.coordinateFormatter = coordinateFormatter;
    }

    protected Function<Coordinate, String> getCoordinateFormatter() {
        return coordinateFormatter;
    }
}
