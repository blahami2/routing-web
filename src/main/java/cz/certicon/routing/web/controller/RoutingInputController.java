/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.controller;

import cz.certicon.routing.GlobalOptions;
import cz.certicon.routing.application.algorithm.AlgorithmType;
import cz.certicon.routing.memsensitive.algorithm.Route;
import cz.certicon.routing.memsensitive.algorithm.RouteNotFoundException;
import cz.certicon.routing.memsensitive.data.nodesearch.EvaluableOnlyException;
import cz.certicon.routing.memsensitive.model.entity.DistanceType;
import cz.certicon.routing.memsensitive.model.entity.NodeSet;
import cz.certicon.routing.memsensitive.model.entity.Path;
import cz.certicon.routing.model.basic.LengthUnits;
import cz.certicon.routing.model.basic.Time;
import cz.certicon.routing.model.basic.TimeUnits;
import cz.certicon.routing.model.entity.Coordinate;
import cz.certicon.routing.utils.debug.Log;
import cz.certicon.routing.utils.measuring.StatsLogger;
import cz.certicon.routing.utils.measuring.TimeLogger;
import cz.certicon.routing.web.model.transport.RoutingOutput;
import cz.certicon.routing.web.model.beans.GraphBean;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private static final String FILENAME = "routing";

    @Autowired
    private GraphBean graphBean;

    @RequestMapping( "/route" )
    public RoutingOutput route( @RequestParam( value = "latFrom" ) double latFrom,
            @RequestParam( value = "lonFrom" ) double lonFrom,
            @RequestParam( value = "latTo" ) double latTo,
            @RequestParam( value = "lonTo" ) double lonTo,
            @RequestParam( value = "priority" ) String priorityString,
            @RequestParam( value = "algorithm" ) String algorithmString ) {
        GlobalOptions.MEASURE_STATS = true;
        GlobalOptions.MEASURE_TIME = true;

        try {
            System.out.println( "priority = " + priorityString );
            DistanceType distanceType;
            try {
                distanceType = DistanceType.valueOf( priorityString.toUpperCase() );
            } catch ( IllegalArgumentException ex ) {
                distanceType = DistanceType.TIME;
            }
            AlgorithmType algorithmType;
            try {
                algorithmType = AlgorithmType.valueOf( algorithmString );
            } catch ( IllegalArgumentException ex ) {
                algorithmType = AlgorithmType.CONTRACTION_HIERARCHIES;
            }
            Coordinate from = new Coordinate( latFrom, lonFrom );
            Coordinate to = new Coordinate( latTo, lonTo );

            DateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss dd.MM.yyyy " );
            Calendar cal = Calendar.getInstance();
            System.out.println( "===================================================" );
            System.out.println( dateFormat.format( cal.getTime() ) + " - new request" );
            Log.dln( FILENAME, "=== log ===" );
            Log.dln( FILENAME, dateFormat.format( cal.getTime() ) );
            Log.dln( FILENAME, "source: " + from );
            Log.dln( FILENAME, "target: " + to );
            Log.dln( FILENAME, "priority: " + distanceType.name().toLowerCase() );
            Log.dln( FILENAME, "algorithm: " + algorithmType.name().toLowerCase() );
            Path path = null;
            try {
                NodeSet nodeSet = graphBean.getClosestNodes( distanceType, from, to );
                Time nodeSearchTime = TimeLogger.getTimeMeasurement( TimeLogger.Event.NODE_SEARCHING ).getTime();
                Log.dln( FILENAME, "search: " + nodeSearchTime.toString( TimeUnits.MICROSECONDS ) );
                System.out.println( "Searching done in " + nodeSearchTime.toString( TimeUnits.MICROSECONDS ) + "! Routing..." );

                Route route;
                try {
                    route = graphBean.getRoute( distanceType, algorithmType,
                            nodeSet.getMap( graphBean.getGraph( distanceType ), NodeSet.NodeCategory.SOURCE ),
                            nodeSet.getMap( graphBean.getGraph( distanceType ), NodeSet.NodeCategory.TARGET ) );
                    Log.dln( FILENAME, "#nodes: " + StatsLogger.getValue( StatsLogger.Statistic.NODES_EXAMINED ) );
                    System.out.println( "Examined nodes: " + StatsLogger.getValue( StatsLogger.Statistic.NODES_EXAMINED ) );
                    Time routingTime = TimeLogger.getTimeMeasurement( TimeLogger.Event.ROUTING ).getTime();
                    Log.dln( FILENAME, "routing: " + routingTime.toString( TimeUnits.MICROSECONDS ) );
                    System.out.println( "Routing done in " + routingTime.toString( TimeUnits.MICROSECONDS ) + "! Printing result..." );
                    System.out.println( "Loading coordinates..." );
                    path = graphBean.getPath( distanceType, route, from, to );
                    Time pathTime = TimeLogger.getTimeMeasurement( TimeLogger.Event.PATH_LOADING ).getTime();
                    Log.dln( FILENAME, "coordinates: " + pathTime.toString( TimeUnits.MICROSECONDS ) );
                    System.out.println( "Loading coordinates done in " + pathTime.toString( TimeUnits.MICROSECONDS ) + "!" );
                } catch ( RouteNotFoundException ex ) {
                    System.out.println( "Path was not found." );
                    return null;
                }
            } catch ( EvaluableOnlyException ex ) {
                path = graphBean.getPath( distanceType, ex.getEdgeId(), from, to );
                Time pathTime = TimeLogger.getTimeMeasurement( TimeLogger.Event.PATH_LOADING ).getTime();
                Log.dln( FILENAME, "coordinates: " + pathTime.toString( TimeUnits.MICROSECONDS ) );
                System.out.println( "Loading coordinates done in " + pathTime.toString( TimeUnits.MICROSECONDS ) + "!" );
            }

            long routeTime = path.getTime().getTime( TimeUnits.SECONDS );
            System.out.println( "Path was found: length = " + path.getLength().toString( LengthUnits.METERS ) + ", time = " + String.format( "%02d:%02d:%02d", routeTime / 3600, ( routeTime % 3600 ) / 60, routeTime % 60 ) + "." );

            Log.dln( FILENAME, "path length: " + path.getLength().toString( LengthUnits.METERS ) );
            Log.dln( FILENAME, "path time: " + String.format( "%02d:%02d:%02d", routeTime / 3600, ( routeTime % 3600 ) / 60, routeTime % 60 ) );

            Time nodeSearchTime = TimeLogger.getTimeMeasurement( TimeLogger.Event.NODE_SEARCHING ).getTime();
            Time routingTime = TimeLogger.getTimeMeasurement( TimeLogger.Event.ROUTING ).getTime();
            Time pathTime = TimeLogger.getTimeMeasurement( TimeLogger.Event.PATH_LOADING ).getTime();
            int nodesExamined = StatsLogger.getValue( StatsLogger.Statistic.NODES_EXAMINED );
            RoutingOutput output = new RoutingOutput(
                    path.getTime().getTime( TimeUnits.SECONDS ),
                    path.getLength().getLength( LengthUnits.METERS ),
                    path.getCoordinates(),
                    nodeSearchTime.getTime( TimeUnits.MILLISECONDS ),
                    routingTime.getTime( TimeUnits.MILLISECONDS ),
                    pathTime.getTime( TimeUnits.MILLISECONDS ),
                    nodesExamined
            );
            return output;

        } catch ( IOException ex ) {
            Logger.getLogger( RoutingInputController.class.getName() ).log( Level.SEVERE, null, ex );
            return null;
        }

    }
}
