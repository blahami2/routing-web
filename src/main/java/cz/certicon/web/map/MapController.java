package cz.certicon.web.map;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.web.common.model.Algorithms;
import cz.certicon.web.common.model.Region;
import cz.certicon.web.common.services.regions.RegionProvider;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static java.util.stream.Collectors.toList;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Controller
public class MapController {

    private final RegionProvider regionProvider;
    private final Properties properties;

    public MapController( @Autowired RegionProvider regionProvider, @Autowired @Qualifier( "connectionProperties" ) Properties properties ) {
        this.regionProvider = regionProvider;
        this.properties = properties;
    }

    @RequestMapping( "/map" )
    public ModelAndView mapView() {
        ModelAndView model = new ModelAndView( "mapview" );
        model.addObject( "url", properties.getProperty( "web.url" ) );
        model.addObject( "fromLon", "14.5131146" );
        model.addObject( "fromLat", "50.1032571" );
        model.addObject( "toLon", "14.3638555" );
        model.addObject( "toLat", "50.0526668" );
        model.addObject( "metricOptions", Metric.values() );
        model.addObject( "algorithmOptions", Algorithms.values() );
        model.addObject( "regionsId", "regionsId" );
        try {
            model.addObject( "regions", regionProvider.getRegions().stream().map( r -> new RegionSelect( r, true ) ).collect( toList() ) );
        } catch ( IOException e ) {
            // TODO log and handle
            e.printStackTrace();
        }
        return model;
    }


    @RequestMapping( value = "/map", params = { "latFrom", "lonFrom", "latTo", "lonTo", "metric", "algorithm", "regions" } )
    public ModelAndView mapView( @RequestParam( "latFrom" ) double latFrom,
                                 @RequestParam( "lonFrom" ) double lonFrom,
                                 @RequestParam( "latTo" ) double latTo,
                                 @RequestParam( "lonTo" ) double lonTo,
                                 @RequestParam( "metric" ) String metricStr,
                                 @RequestParam( "algorithm" ) String algorithmStr,
                                 @RequestParam( "regions" ) long regions ) {
        ModelAndView model = new ModelAndView( "mapview" );
        model.addObject( "url", properties.getProperty( "web.url" ) );
        model.addObject( "fromLon", lonFrom );
        model.addObject( "fromLat", latFrom );
        model.addObject( "toLon", lonTo );
        model.addObject( "toLat", latTo );
        model.addObject( "metricOptions", Metric.values() );
        model.addObject( "metricSelected", metricStr );
        model.addObject( "algorithmOptions", Algorithms.values() );
        model.addObject( "algorithmSelected", algorithmStr );
        model.addObject( "regionsId", "regionsId" );
        try {
            model.addObject( "regions", regionProvider.getRegions().stream().map( r -> new RegionSelect( r, ( r.getId() & regions ) > 0 ) ).collect( toList() ) );
        } catch ( IOException e ) {
            // TODO log and handle
            e.printStackTrace();
        }
        return model;
    }

    // backward compatibility
    @RequestMapping( value = "/map", params = { "latFrom", "lonFrom", "latTo", "lonTo", "priority", "algorithm" } )
    public ModelAndView mapView( @RequestParam( "latFrom" ) double latFrom,
                                 @RequestParam( "lonFrom" ) double lonFrom,
                                 @RequestParam( "latTo" ) double latTo,
                                 @RequestParam( "lonTo" ) double lonTo,
                                 @RequestParam( "priority" ) String metricStr,
                                 @RequestParam( "algorithm" ) String algorithmStr ) {
        ModelAndView model = new ModelAndView( "mapview" );
        model.addObject( "url", properties.getProperty( "web.url" ) );
        model.addObject( "fromLon", lonFrom );
        model.addObject( "fromLat", latFrom );
        model.addObject( "toLon", lonTo );
        model.addObject( "toLat", latTo );
        model.addObject( "metricOptions", Metric.values() );
        model.addObject( "metricSelected", metricStr );
        model.addObject( "algorithmOptions", Algorithms.values() );
        model.addObject( "algorithmSelected", algorithmStr );
        model.addObject( "regionsId", "regionsId" );
        try {
            model.addObject( "regions", regionProvider.getRegions().stream().map( r -> new RegionSelect( r, true ) ).collect( toList() ) );
        } catch ( IOException e ) {
            // TODO log and handle
            e.printStackTrace();
        }
        return model;
    }

    @Value
    private static class RegionSelect {

        Region region;
        boolean selected;

        public long getId() {
            return region.getId();
        }

        public long getCellId() {
            return region.getCellId();
        }

        public String getName() {
            return region.getName();
        }
    }
}
