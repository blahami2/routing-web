/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.routing_web.data;

import cz.certicon.routing.model.entity.Coordinate;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class RoutingInput {
    private final Coordinate from;
    private final Coordinate to;

    public RoutingInput( Coordinate from, Coordinate to ) {
        this.from = from;
        this.to = to;
    }

    public Coordinate getFrom() {
        return from;
    }

    public Coordinate getTo() {
        return to;
    }
}
