package cz.certicon.web.reporter.logic.logging;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.web.common.model.Algorithms;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class FileReportLogger extends AbstractReportLogger implements ReportLogger {
    private static int counter = 0;

    private final Logger logger = Logger.getLogger( FileReportLogger.class.getName() + counter++ );

    public FileReportLogger() {
        init( new File( "report.log" ) );
    }

    public FileReportLogger( Supplier<File> fileSupplier ) {
        init( fileSupplier.get() );
    }

    public FileReportLogger( Function<Coordinate, String> coordinateFormatter, Supplier<File> fileSupplier ) {
        super( coordinateFormatter );
        init( fileSupplier.get() );
    }

    private void init( File file ) {
        try {
            FileHandler fh = new FileHandler( file.getAbsolutePath() );
            fh.setFormatter( new SimpleFormatter() );
            logger.addHandler( fh );
        } catch ( IOException ex ) {
            Logger.getLogger( getClass().getName() ).log( Level.SEVERE, ex.getMessage() );
        }
    }

    @Override
    public void log( Coordinate from, Coordinate to, Algorithms algorithm, Metric metric, String message, String link ) throws IOException {
        logger.log( Level.INFO, "===================== log =====================\n" +
                LocalDateTime.now() + "\n" +
                "source: " + getCoordinateFormatter().apply( from ) + "\n" +
                "target: " + getCoordinateFormatter().apply( to ) + "\n" +
                "metric: " + metric.name().toLowerCase() + "\n" +
                "algorithm: " + algorithm.name().toLowerCase() + "\n" +
                "message: " + message + "\n" +
                "link: " + link );
    }
}
