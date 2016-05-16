/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.data.logging.report;

import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.web.model.AlgorithmType;
import cz.certicon.routing.web.model.Priority;
import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface ReportLogger {

    public void log( Coordinates from, Coordinates to, AlgorithmType algorithm, Priority priority, String message ) throws IOException;

    public static class Formatter {

        public static String formatCoordinates( Coordinates coords ) {
            return coords.getLatitude() + "," + coords.getLongitude();
        }
    }
}
