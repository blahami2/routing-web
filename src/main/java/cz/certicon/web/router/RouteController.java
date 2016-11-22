package cz.certicon.web.router;

import cz.certicon.routing.algorithm.RoutingAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.PreprocessingInput;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayBuilder;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayCreator;
import cz.certicon.routing.algorithm.sara.query.mld.RouteUnpacker;
import cz.certicon.routing.data.GraphDAO;
import cz.certicon.routing.data.RouteDataDAO;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.RouteData;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.Metric;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.values.Coordinate;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import cz.certicon.web.model.RoutingResult;
import java8.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@RestController
public class RouteController {

    private Map<String, RoutingAlgorithm> algorithmMap;
    private SaraGraph graph;
    private OverlayBuilder overlay;
    private RouteUnpacker routeUnpacker;
    private RouteDataDAO routeDataDao;

    @Autowired
    public RouteController( Map<String, RoutingAlgorithm> algorithmMap,
                            GraphDAO graphDAO,
                            OverlayCreator overlayCreator,
                            RouteUnpacker routeUnpacker,
                            RouteDataDAO routeDataDAO
    ) {
        this.algorithmMap = algorithmMap;
        overlay = overlayCreator.createBuilder();
        //overlay.buildOverlays();
        // graph = overlay.getGraph();
        this.routeDataDao = routeDataDAO;
        this.routeUnpacker = routeUnpacker;
        // nodeSearcher
    }

    @RequestMapping( "/route" )
    public Object route(
            @RequestParam( "latFrom" ) double latFrom,
            @RequestParam( "lonFrom" ) double lonFrom,
            @RequestParam( "latTo" ) double latTo,
            @RequestParam( "lonTo" ) double lonTo,
            @RequestParam( "metric" ) String metricStr,
            @RequestParam( "algorithm" ) String algorithmStr
    ) {
        Logger.getLogger( RouteController.class.getName() ).info( algorithmMap.toString() );

        Metric metric = Metric.valueOf( metricStr );
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
