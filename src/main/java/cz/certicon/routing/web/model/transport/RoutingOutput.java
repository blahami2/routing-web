/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model.transport;

import cz.certicon.routing.model.entity.Coordinate;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class RoutingOutput {

    private final double time;
    private final double length;
    private final List<Coordinate> coordinates;

    private final long searchTime;
    private final long routeTime;
    private final long coordinatesTime;

    public RoutingOutput( double time, double length, List<Coordinate> coordinates, long searchTime, long routeTime, long coordinatesTime ) {
        this.time = time;
        this.length = length;
        this.coordinates = coordinates;
        this.searchTime = searchTime;
        this.routeTime = routeTime;
        this.coordinatesTime = coordinatesTime;
    }

    public double getTime() {
        return time;
    }

    public double getLength() {
        return length;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public long getRouteTime() {
        return routeTime;
    }

    public long getCoordinatesTime() {
        return coordinatesTime;
    }

}
