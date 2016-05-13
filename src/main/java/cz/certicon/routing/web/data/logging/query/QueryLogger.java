/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.data.logging.query;

import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.web.model.AlgorithmType;
import cz.certicon.routing.web.model.Priority;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface QueryLogger {

    public void log( Coordinates from, Coordinates to, AlgorithmType algorithm, Priority priority, long searchTime, long routingTime, long pathBuildTime, long coordinatesLoadTime, double routeLength, double routeTime );
}
