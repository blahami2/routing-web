package cz.certicon.web.router;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import cz.certicon.web.common.model.Algorithms;
import cz.certicon.web.common.model.Region;
import cz.certicon.web.common.model.RoutingResult;
import cz.certicon.web.common.services.regions.RegionProvider;
import cz.certicon.web.router.services.RouteService;
import java8.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@RestController
public class RouteController {

    private final RouteService routeService;
    private final RegionProvider regionProvider;

    public RouteController( @Autowired RouteService routeService, @Autowired RegionProvider regionProvider ) {
        this.routeService = routeService;
        this.regionProvider = regionProvider;
    }

    @RequestMapping( "/route" )
    public Object route(
            @RequestParam( "latFrom" ) double latFrom,
            @RequestParam( "lonFrom" ) double lonFrom,
            @RequestParam( "latTo" ) double latTo,
            @RequestParam( "lonTo" ) double lonTo,
            @RequestParam( "metric" ) String metricStr,
            @RequestParam( "algorithm" ) String algorithmStr,
            @RequestParam( "regions" ) long regions
    ) {

        Metric metric = Metric.valueOf( metricStr );
        Algorithms algorithms = Algorithms.valueOf( algorithmStr );

        Coordinate sourceCoordinate = new Coordinate( latFrom, lonFrom );
        Coordinate targetCoordinate = new Coordinate( latTo, lonTo );
        try {
//            List<Coordinate> coords = new ArrayList<>();
//            PointSearcher.PointSearchResult sourcePoint = pointSearcher.findClosestPoint( sourceCoordinate, EnumSet.of( metric ) );
//            if ( sourcePoint.isCrossroad() ) {
//                coords.add( sourceCoordinate );
//            } else {
//                coords.addAll( graphDataDao.loadEdgeData( sourcePoint.getEdgeId() ).getCoordinates() );
//            }
//            PointSearcher.PointSearchResult targetPoint = pointSearcher.findClosestPoint( targetCoordinate, EnumSet.of( metric ) );
//            if ( targetPoint.isCrossroad() ) {
//                coords.add( targetCoordinate );
//            } else {
//                coords.addAll( graphDataDao.loadEdgeData( targetPoint.getEdgeId() ).getCoordinates() );
//            }
            List<Region> regionList = regionProvider.getRegions( regions );
            Set<Region> allRegions = new HashSet<>( regionProvider.getRegions() );
            allRegions.removeAll( regionList );
            Set<Long> regionsToIds = allRegions.stream().map( r -> Long.valueOf( r.getCellId() ) ).collect( Collectors.toSet() ); // TODO parse from map - region bit identifier to cell id
            // TODO regons positive or negative? Consider using positive and create complementary collection of negatives here
            Optional<RoutingResult> result = routeService.route( sourceCoordinate, targetCoordinate, algorithms, metric, regionsToIds );
            return result.orElse( new RoutingResult( 0, 0, 0, Collections.EMPTY_LIST ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
//         Node source = nodeSearcher.search( latFrom, lonFrom );
//         Node target = nodeSearcher.search( latTo, lonTo );
        TimeMeasurement timeMeasurement = new TimeMeasurement();
        timeMeasurement.start();
//         Optional<Route> route = algorithmMap.get( algorithmStr ).route( overlay, metric, source, target );
        timeMeasurement.stop();
//        if( route.isPresent() ){
//            try {
//                RouteData routeData = routeDataDao.loadRouteData( route.get() );
//                return routeData;
//            } catch ( IOException e ) {
//                // log
//                return "error-io_exception";
//            }
//        } else {
//            return "error-not_found";
//        }
        return new RoutingResult( 10, 20, 30, Arrays.asList(
                new Coordinate( latFrom, lonFrom ),
                new Coordinate( latTo, lonTo )
        ) );
    }

}
