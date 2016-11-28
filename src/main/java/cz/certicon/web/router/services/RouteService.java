package cz.certicon.web.router.services;

import cz.certicon.routing.algorithm.DijkstraAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.ZeroNode;
import cz.certicon.routing.algorithm.sara.query.mld.MLDFullMemoryRouteUnpacker;
import cz.certicon.routing.algorithm.sara.query.mld.MLDRecursiveRouteUnpacker;
import cz.certicon.routing.algorithm.sara.query.mld.MultilevelDijkstraAlgorithm;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.RouteData;
import cz.certicon.routing.model.RoutingPoint;
import cz.certicon.routing.model.graph.*;
import cz.certicon.routing.model.values.*;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import cz.certicon.web.common.model.Algorithms;
import cz.certicon.web.common.model.RoutingResult;
import java8.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Service
public class RouteService {

    private final GraphSupplyService graphSupplyService;
    private final PointSearchService pointSearchService;
    private final RouteDataService routeDataService;

    public RouteService( @Autowired GraphSupplyService graphSupplyService, @Autowired PointSearchService pointSearchService, @Autowired RouteDataService routeDataService ) {
        this.graphSupplyService = graphSupplyService;
        this.pointSearchService = pointSearchService;
        this.routeDataService = routeDataService;
    }

    public Optional<RoutingResult> route( Coordinate from, Coordinate to, Algorithms algorithm, Metric metric, Set<Long> turnOffCells ) throws IOException {
        TimeMeasurement time = new TimeMeasurement();
        time.setTimeUnits( TimeUnits.MILLISECONDS );
        SaraGraph graph = graphSupplyService.getGraph();
        time.start();
        RoutingPoint<SaraNode, SaraEdge> fromPoint = pointSearchService.search( graph, from, metric );
        RoutingPoint<SaraNode, SaraEdge> toPoint = pointSearchService.search( graph, to, metric );
        time.stop();
        Time pointSearchTime = time.getTime();
        Optional<Route<SaraNode, SaraEdge>> route = Optional.empty();
        time.start();
        if ( algorithm.equals( Algorithms.DIJKSTRA ) ) {
            DijkstraAlgorithm<SaraNode, SaraEdge> dijkstraAlgorithm = new DijkstraAlgorithm<>();
            route = dijkstraAlgorithm.route( metric, fromPoint.getEdge().get(), toPoint.getEdge().get()
                    , fromPoint.getDistanceToSource( metric ).orElse( Distance.newInstance( 0 ) ), fromPoint.getDistanceToTarget( metric ).orElse( Distance.newInstance( 0 ) )
                    , toPoint.getDistanceToSource( metric ).orElse( Distance.newInstance( 0 ) ), toPoint.getDistanceToTarget( metric ).orElse( Distance.newInstance( 0 ) ) );
        } else if ( algorithm.equals( Algorithms.SARA ) ) {
            if ( !turnOffCells.isEmpty() ) {
                Logger.getLogger( getClass().getName() ).log( Level.INFO, "Setting areas off: " + Arrays.toString( toArray( turnOffCells ) ) );
                graphSupplyService.getOverlayBuilder().turnTopCells( false, toArray( turnOffCells ) );
            }
            SaraNode saraSource = fromPoint.getNode().orElse( fromPoint.getEdge().get().getTarget() );
            SaraNode saraTarget = toPoint.getNode().orElse( toPoint.getEdge().get().getSource() );
            ZeroNode zeroSource = graphSupplyService.getOverlayBuilder().getZeroNode( saraSource );
            ZeroNode zeroTarget = graphSupplyService.getOverlayBuilder().getZeroNode( saraTarget );
            MLDFullMemoryRouteUnpacker unpacker = new MLDFullMemoryRouteUnpacker();
            MultilevelDijkstraAlgorithm mldAlg = new MultilevelDijkstraAlgorithm();
            route = mldAlg.route( graphSupplyService.getOverlayBuilder(), metric, zeroSource, zeroTarget, unpacker );
            if ( !turnOffCells.isEmpty() ) {
                Logger.getLogger( getClass().getName() ).log( Level.INFO, "Setting areas on: " + Arrays.toString( toArray( turnOffCells ) ) );
                graphSupplyService.getOverlayBuilder().turnTopCells( true, toArray( turnOffCells ) );
            }
        } else {
            throw new IllegalArgumentException( "Unknown algorithm: " + algorithm.name() );
        }
        time.stop();
        Time routeTime = time.getTime();
        if ( route.isPresent() ) {
            time.start();
            RouteData<SaraEdge> routeData = routeDataService.loadRouteData( route.get() );
            time.stop();
            Logger.getLogger( getClass().getName() ).log( Level.INFO, "Route found: " + routeData.getLength() + ", " + routeData.getTime() );
            Time pathLoadTime = time.getTime();
            // long length, long time, long executionTime, List<Coordinate> coords
            return Optional.of( new RoutingResult(
                    routeData.getLength().getValue( LengthUnits.METERS ),
                    routeData.getTime().getValue( TimeUnits.SECONDS ),
                    routeTime.getValue( TimeUnits.MILLISECONDS ),
                    routeData.getCoordinates( route.get() ) ) );
        } else {
            Logger.getLogger( getClass().getName() ).log( Level.WARNING, "Route not found" );
            return Optional.empty();
        }
    }

    private long[] toArray( Set<Long> set ) {
        long[] array = new long[set.size()];
        int counter = 0;
        for ( long num : set ) {
            array[counter++] = num;
        }
        return array;
    }
}
