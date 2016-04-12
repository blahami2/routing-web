/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model.entity;

import cz.certicon.routing.application.algorithm.DistanceFactory;
import cz.certicon.routing.application.algorithm.data.number.LengthDistanceFactory;
import cz.certicon.routing.model.entity.GraphEntityFactory;
import cz.certicon.routing.model.entity.neighbourlist.DirectedNeighborListGraphEntityFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Component
@Scope("singleton")
public class GraphFactoriesBean {

    private final GraphEntityFactory graphEntityFactory;
    private final DistanceFactory distanceFactory;

    public GraphFactoriesBean() {
        this.graphEntityFactory = new DirectedNeighborListGraphEntityFactory();
        this.distanceFactory = new LengthDistanceFactory();
    }

    public GraphEntityFactory getGraphEntityFactory() {
        return graphEntityFactory;
    }

    public DistanceFactory getDistanceFactory() {
        return distanceFactory;
    }
}
