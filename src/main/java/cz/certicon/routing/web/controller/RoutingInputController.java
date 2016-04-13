/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.controller;

import cz.certicon.routing.application.algorithm.Distance;
import cz.certicon.routing.application.algorithm.DistanceFactory;
import cz.certicon.routing.application.algorithm.RoutingAlgorithm;
import cz.certicon.routing.application.algorithm.algorithms.astar.StraightLineAStarRoutingAlgorithm;
import cz.certicon.routing.application.algorithm.data.number.LengthDistanceFactory;
import cz.certicon.routing.data.graph.database.DatabaseGraphRW;
import cz.certicon.routing.data.nodesearch.NodeSearcher;
import cz.certicon.routing.data.nodesearch.database.DatabaseNodeSearcher;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.model.entity.Graph;
import cz.certicon.routing.model.entity.GraphEntityFactory;
import cz.certicon.routing.model.entity.Node;
import cz.certicon.routing.model.entity.Path;
import cz.certicon.routing.model.entity.neighbourlist.DirectedNeighborListGraphEntityFactory;
import cz.certicon.routing.web.model.entity.DatabasePropertiesBean;
import cz.certicon.routing.web.model.entity.GraphBean;
import cz.certicon.routing.web.model.entity.GraphFactoriesBean;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
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
    public List<Coordinates> route( @RequestParam( value = "latFrom" ) double latFrom,
            @RequestParam( value = "lonFrom" ) double lonFrom,
            @RequestParam( value = "latTo" ) double latTo,
            @RequestParam( value = "lonTo" ) double lonTo ) {

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
        RoutingAlgorithm routingAlgorithm = new StraightLineAStarRoutingAlgorithm(
                graphBean.getGraph(),
                graphFactoriesBean.getGraphEntityFactory(),
                graphFactoriesBean.getDistanceFactory() );
        Coordinates from = new Coordinates( latFrom, lonFrom );
        Coordinates to = new Coordinates( latTo, lonTo );

        NodeSearcher ns = new DatabaseNodeSearcher( databasePropertiesBean.getConnectionProperties() );
        try {
            System.out.println( "from: " + from );
            System.out.println( "to: " + to );
            Map<Coordinates, Distance> fromMap = ns.findClosestNodes( from, graphFactoriesBean.getDistanceFactory() );
            Map<Coordinates, Distance> toMap = ns.findClosestNodes( to, graphFactoriesBean.getDistanceFactory() );
            System.out.println( "from map = " + fromMap );
            System.out.println( "to map = " + toMap );
            Path route = routingAlgorithm.route( fromMap, toMap );
            if ( route == null ) {
                System.out.println( "path not found" );
            } else {
                System.out.println( "path found" );
                return route.getCoordinates();
            }
        } catch ( IOException ex ) {
            Logger.getLogger( RoutingInputController.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return null;
    }
}
