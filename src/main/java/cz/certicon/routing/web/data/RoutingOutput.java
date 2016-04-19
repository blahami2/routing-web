/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.data;

import cz.certicon.routing.model.entity.Coordinates;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class RoutingOutput {

    private final double time;
    private final double length;
    private final List<Coordinates> coordinates;

    private final long searchTime;
    private final long routeTime;

    public RoutingOutput( double time, double length, List<Coordinates> coordinates, long searchTime, long routeTime ) {
        this.time = time;
        this.length = length;
        this.coordinates = coordinates;
        this.searchTime = searchTime;
        this.routeTime = routeTime;
    }

    public double getTime() {
        return time;
    }

    public double getLength() {
        return length;
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public long getRouteTime() {
        return routeTime;
    }

}
