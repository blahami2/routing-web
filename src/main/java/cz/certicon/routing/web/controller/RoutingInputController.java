/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.controller;

import cz.certicon.routing.application.algorithm.Distance;
import cz.certicon.routing.application.algorithm.RoutingAlgorithm;
import cz.certicon.routing.application.algorithm.algorithms.astar.StraightLineAStarRoutingAlgorithm;
import cz.certicon.routing.data.nodesearch.NodeSearcher;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.model.entity.Edge;
import cz.certicon.routing.model.entity.Node;
import cz.certicon.routing.model.entity.Path;
import cz.certicon.routing.utils.CoordinateUtils;
import cz.certicon.routing.utils.GeometryUtils;
import cz.certicon.routing.utils.GraphUtils;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import cz.certicon.routing.utils.measuring.TimeUnits;
import cz.certicon.routing.web.data.RoutingOutput;
import cz.certicon.routing.web.model.Priority;
import cz.certicon.routing.web.model.beans.PropertiesBean;
import cz.certicon.routing.web.model.beans.GraphBean;
import cz.certicon.routing.web.model.beans.GraphFactoriesBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@RestController
@RequestMapping( "/rest" )
public class RoutingInputController {

    @Autowired
    private GraphBean graphBean;
    @Autowired
    private GraphFactoriesBean graphFactoriesBean;
    @Autowired
    private PropertiesBean databasePropertiesBean;

    @RequestMapping( "/route" )
    public RoutingOutput route( @RequestParam( value = "latFrom" ) double latFrom,
            @RequestParam( value = "lonFrom" ) double lonFrom,
            @RequestParam( value = "latTo" ) double latTo,
            @RequestParam( value = "lonTo" ) double lonTo,
            @RequestParam( value = "priority" ) String priority ) {

//        GraphEntityFactory graphEntityFactory = new DirectedNeighborListGraphEntityFactory();
//        DistanceFactory distanceFactory = new LengthDistanceFactory();
//        InputStream in = getClass().getClassLoader().getResourceAsStream( "database_connection.properties" );
//        Properties connectionProperties = new Properties();
//        try {
//            connectionProperties.load( in );
//            in.close();
//            DatabaseGraphRW db = new DatabaseGraphRW( connectionProperties );
//            Graph graph = db.read( new Pair<>( graphEntityFactory, distanceFactory ) );
//            db.close();
//            RoutingAlgorithm routingAlgorithm = new StraightLineAStarRoutingAlgorithm( graph, graphEntityFactory, distanceFactory );
//            Node from = graphEntityFactory.createNode( Node.Id.generateId(), latFrom, lonFrom );
//            Node to = graphEntityFactory.createNode( Node.Id.generateId(), latTo, lonTo );
////            for ( Node node : graph.getNodes() ) {
////                if ( node.getCoordinates().equals( from.getCoordinates() ) ) {
////                    System.out.println( "found from: " + node );
////                }
////                if ( node.getCoordinates().equals( to.getCoordinates() ) ) {
////                    System.out.println( "found to: " + node );
////                }
////            }
////            System.out.println( "from = " + from );
////            System.out.println( "to = " + to );
//            Path route = routingAlgorithm.route( from, to );
//            if ( route == null ) {
//                System.out.println( "No path has been found." );
//                return null;
//            }
//            return route.getCoordinates();
////            System.out.println( "route = " + route );
////            System.out.println( "nodes = " + route.getNodes() );
////            System.out.println( "edges = " + route.getEdges() );
////            System.out.println( "coordinates = " + route.getCoordinates() );
////            StringDestination dest = new StringDestination();
////            PathExporter pathExporter = new SeznamApiPathExporter();
////            pathExporter.exportPath( dest, route );
////            return dest.getResult();
//        } catch ( IOException ex ) {
//            Logger.getLogger( RoutingInputController.class.getName() ).log( Level.SEVERE, null, ex );
//        }
        try {
            System.out.println( "priority = " + priority );
            try {
                graphBean.setPriority( Priority.valueOfCaseInsensitive( priority ) );
            } catch ( IllegalArgumentException ex ) {
                // use default
            }
            RoutingAlgorithm routingAlgorithm = new StraightLineAStarRoutingAlgorithm(
                    graphBean.getGraph(),
                    graphBean.getGraphEntityFactory(),
                    graphBean.getDistanceFactory() );
            Coordinates from = new Coordinates( latFrom, lonFrom );
            Coordinates to = new Coordinates( latTo, lonTo );

            TimeMeasurement time = new TimeMeasurement();
            time.setTimeUnits( TimeUnits.MILLISECONDS );
            System.out.println( "Searching for nodes..." );
            time.start();
            Pair<Map<Node.Id, Distance>, Long> sourceClosest = graphBean.getClosestNodes( from, NodeSearcher.SearchFor.SOURCE );
            Map<Node.Id, Distance> fromMap = sourceClosest.a;
            Pair<Map<Node.Id, Distance>, Long> targetClosest = graphBean.getClosestNodes( to, NodeSearcher.SearchFor.TARGET );
            Map<Node.Id, Distance> toMap = targetClosest.a;
            long searchTime = time.stop();
            System.out.println( "Searching done in " + searchTime + " ms! Routing..." );

            time.start();
            Path route = routingAlgorithm.route( fromMap, toMap );
            long routingTime = time.stop();
            System.out.println( "time = " + time.getTimeElapsed() );
            if ( route == null ) {
                System.out.println( "path not found" );
            } else {
                double routeTime = route.getTime();
                double routeLength = route.getLength();

                Node firstNode = route.getSourceNode();
                Set<Edge> edgesOf = graphBean.getGraph().getEdgesOf( firstNode );
                Edge sourceEdge = null;
                for ( Edge edge : edgesOf ) {
                    if ( edge.getDataId() == sourceClosest.b ) {
                        sourceEdge = edge;
                        break;
                    }
                }
                Edge targetEdge = null;
                edgesOf = graphBean.getGraph().getEdgesOf( route.getTargetNode() );
                for ( Edge edge : edgesOf ) {
                    if ( edge.getDataId() == targetClosest.b ) {
                        targetEdge = edge;
                        break;
                    }
                }

                Set<Edge> edgeSet = new HashSet<>( route.getEdges() );
                edgeSet.add( sourceEdge );
                edgeSet.add( targetEdge );
                Map<Edge, List<Coordinates>> coordinateMap = graphBean.getCoordinates( edgeSet );
                GraphUtils.fillWithCoordinates( route.getEdges(), coordinateMap );
                List<Coordinates> coordinates = new ArrayList<>();

                sourceEdge.setCoordinates( coordinateMap.get( sourceEdge ) );
                double min = Double.MAX_VALUE;
                int minIndex = 0;
                List<Coordinates> sourceEdgeCoordinates = sourceEdge.getCoordinates();
                for ( int i = 0; i < sourceEdgeCoordinates.size(); i++ ) {
                    Coordinates coordinate = sourceEdgeCoordinates.get( i );
                    double dist = CoordinateUtils.calculateDistance( coordinate, from );
                    if ( dist < min ) {
                        min = dist;
                        minIndex = i;
                    }
                }
                double sourceLength = 0;
                for ( int i = minIndex; i < sourceEdgeCoordinates.size(); i++ ) {
                    Coordinates coordinate = sourceEdgeCoordinates.get( i );
                    coordinates.add( coordinate );
                    if ( i > minIndex ) {
                        sourceLength += CoordinateUtils.calculateDistance( sourceEdgeCoordinates.get( i - 1 ), coordinate ) / 1000;
                    }
                }

                coordinates.addAll( route.getCoordinates() );

                min = Double.MAX_VALUE;
                List<Coordinates> targetEdgeCoordinates = targetEdge.getCoordinates();
                for ( int i = 0; i < targetEdgeCoordinates.size(); i++ ) {
                    Coordinates coordinate = targetEdgeCoordinates.get( i );
                    double dist = CoordinateUtils.calculateDistance( coordinate, to );
                    if ( dist < min ) {
                        min = dist;
                        minIndex = i;
                    }
                }
                double targetLength = 0;
                for ( int i = 0; i <= minIndex; i++ ) {
                    Coordinates coordinate = targetEdgeCoordinates.get( i );
                    coordinates.add( coordinate );
                    if ( i > 0 ) {
                        targetLength += CoordinateUtils.calculateDistance( targetEdgeCoordinates.get( i - 1 ), coordinate ) / 1000;
                    }
                }
//                System.out.println( "route size: " + route.getEdges().size() );
//                System.out.println( "route: " + route.getLength() + ", " + route.getTime() );
                routeLength += sourceLength + targetLength;
                routeTime += 3600 * (sourceLength / sourceEdge.getSpeed() + targetLength / targetEdge.getSpeed());
                System.out.println( "path found: length = " + routeLength + " km, time = " + ( routeTime / 3600 ) + " h" );

                RoutingOutput output = new RoutingOutput( routeTime, routeLength * 1000, coordinates, searchTime, routingTime );
                route.getEdges().stream().forEach( ( edge ) -> {
                    edge.setCoordinates( null );
                } );
                return output;
            }
        } catch ( IOException ex ) {
            Logger.getLogger( RoutingInputController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return null;
    }
}
