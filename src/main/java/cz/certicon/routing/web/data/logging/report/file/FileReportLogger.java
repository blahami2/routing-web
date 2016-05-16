/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.data.logging.report.file;

import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.utils.debug.Log;
import cz.certicon.routing.web.data.logging.report.ReportLogger;
import cz.certicon.routing.web.model.AlgorithmType;
import cz.certicon.routing.web.model.Priority;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class FileReportLogger implements ReportLogger {

    private static final String FILENAME = "report";

    @Override
    public void log( Coordinates from, Coordinates to, AlgorithmType algorithm, Priority priority, String message ) throws IOException {
        Log.dln( FILENAME, "=== log ===" );
        DateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss dd.MM.yyyy" );
        Log.dln( FILENAME, dateFormat.format( Calendar.getInstance().getTime() ) );
        Log.dln( FILENAME, "source:" + Formatter.formatCoordinates( from ) );
        Log.dln( FILENAME, "target:" + Formatter.formatCoordinates( to ) );
        Log.dln( FILENAME, "priority: " + priority.name().toLowerCase() );
        Log.dln( FILENAME, "algorithm: " + algorithm.name().toLowerCase() );
        Log.dln( FILENAME, "message: " + message );
    }

}