/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.controller;

import cz.certicon.routing.application.algorithm.Distance;
import cz.certicon.routing.application.algorithm.RoutingAlgorithm;
import cz.certicon.routing.application.algorithm.algorithms.astar.StraightLineAStarRoutingAlgorithm;
import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.model.entity.Path;
import cz.certicon.routing.utils.GraphUtils;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import cz.certicon.routing.utils.measuring.TimeUnits;
import cz.certicon.routing.web.data.RoutingOutput;
import cz.certicon.routing.web.model.Priority;
import cz.certicon.routing.web.model.beans.DatabasePropertiesBean;
import cz.certicon.routing.web.model.beans.GraphBean;
import cz.certicon.routing.web.model.beans.GraphFactoriesBean;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
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
    private DatabasePropertiesBean databasePropertiesBean;

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
            System.out.println( "from: " + from );
            System.out.println( "to: " + to );
            time.start();
            Map<Coordinates, Distance> fromMap = graphBean.getClosestNodes( from );
            Map<Coordinates, Distance> toMap = graphBean.getClosestNodes( to );
            long searchTime = time.stop();
            System.out.println( "from map = " + fromMap );
            System.out.println( "to map = " + toMap );

            time.start();
            Path route = routingAlgorithm.route( fromMap, toMap );
            long routeTime = time.stop();
            System.out.println( "time = " + time.getTimeElapsed() );
            if ( route == null ) {
                System.out.println( "path not found" );
            } else {
                System.out.println( "path found: length = " + route.getLength() + " km, time = " + ( route.getTime() / 3600 ) + " h" );
                GraphUtils.fillWithCoordinates( route.getEdges(), graphBean.getCoordinates( new HashSet<>( route.getEdges() ) ) );
                RoutingOutput output = new RoutingOutput( route.getTime(), route.getLength() * 1000, route.getCoordinates(), searchTime, routeTime );
                return output;
            }
        } catch ( IOException ex ) {
            Logger.getLogger( RoutingInputController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return null;
    }
}
