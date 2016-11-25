package cz.certicon.web.router.services;

import cz.certicon.routing.algorithm.DijkstraAlgorithm;
import cz.certicon.routing.algorithm.RoutingAlgorithm;
import cz.certicon.routing.algorithm.sara.query.mld.MultilevelDijkstraAlgorithm;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import cz.certicon.web.common.model.Algorithms;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Service
public class AlgorithmSupplyService {

    private final Map<Algorithms, RoutingAlgorithm> map;

    public AlgorithmSupplyService() {
        map = new EnumMap<>( Algorithms.class );
        map.put( Algorithms.DIJKSTRA, new DijkstraAlgorithm() );
        map.put( Algorithms.SARA, new MultilevelDijkstraAlgorithm() );
    }

    public <N extends Node<N, E>, E extends Edge<N, E>> RoutingAlgorithm findAlgorithm( Algorithms algorithm ) {
        return map.get( algorithm );
    }
}
