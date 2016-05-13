/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model.transport;

import cz.certicon.routing.model.entity.Coordinates;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class RoutingInput {
    private final Coordinates from;
    private final Coordinates to;

    public RoutingInput( Coordinates from, Coordinates to ) {
        this.from = from;
        this.to = to;
    }

    public Coordinates getFrom() {
        return from;
    }

    public Coordinates getTo() {
        return to;
    }
}
