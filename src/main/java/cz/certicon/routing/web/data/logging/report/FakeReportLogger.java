/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.data.logging.report;

import cz.certicon.routing.application.algorithm.AlgorithmType;
import cz.certicon.routing.memsensitive.model.entity.DistanceType;
import cz.certicon.routing.model.entity.Coordinate;
import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class FakeReportLogger implements ReportLogger {

    @Override
    public void log( Coordinate from, Coordinate to, AlgorithmType algorithm, DistanceType priority, String message, String link ) throws IOException {
        // do nothing
    }
    
}
