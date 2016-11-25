package cz.certicon.web.map;

import cz.certicon.routing.model.graph.Metric;
import cz.certicon.web.common.model.Algorithms;
import cz.certicon.web.common.services.regions.RegionProvider;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Controller
public class MapController {

    private final RegionProvider regionProvider;

    public MapController( @Autowired RegionProvider regionProvider ) {
        this.regionProvider = regionProvider;
    }

    @RequestMapping( "/map" )
    public ModelAndView mapView()  {
        ModelAndView model = new ModelAndView( "mapview" );
        model.addObject( "url", "127.0.0.1" );
        model.addObject( "fromLon", "14.5131146" );
        model.addObject( "fromLat", "50.1032571" );
        model.addObject( "toLon", "14.3638555" );
        model.addObject( "toLat", "50.0526668" );
        model.addObject( "metricOptions", Metric.values() );
        model.addObject( "algorithmOptions", Algorithms.values() );
        model.addObject( "regionsId", "regionsId" );
        try {
            model.addObject( "regions", regionProvider.getRegions() );
        } catch ( IOException e ) {
            // TODO log and handle
            e.printStackTrace();
        }
        return model;
    }
}
