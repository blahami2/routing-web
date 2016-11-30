package cz.certicon.web.router.services;

import cz.certicon.routing.data.PointSearcher;
import cz.certicon.routing.model.RoutingPoint;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.routing.model.values.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Service
@Scope( "singleton" )
public class PointSearchService {

    private final PointSearcher pointSearcher;

    public PointSearchService( @Autowired PointSearcher pointSearcher ) {
        this.pointSearcher = pointSearcher;
    }

    synchronized public <N extends Node<N, E>, E extends Edge<N, E>> RoutingPoint<N, E> search( Graph<N, E> graph, Coordinate coordinate, Metric metric ) throws IOException {
        PointSearcher.PointSearchResult closestPoint = pointSearcher.findClosestPoint( coordinate, EnumSet.of( metric ) );
        return RoutingPoint.of( closestPoint, EnumSet.of( metric ), graph );
    }
}
