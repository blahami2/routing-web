/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.controller;

import cz.certicon.routing.application.algorithm.AlgorithmType;
import cz.certicon.routing.memsensitive.model.entity.DistanceType;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.web.data.Provider;
import cz.certicon.routing.web.model.beans.PropertiesBean;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
//@Grab("thymeleaf-spring4")
@Controller
//@RequestMapping("/")
public class MapController {

    @Autowired
    private PropertiesBean propertiesBean;

    private final Properties bundle;

    public MapController() {
        InputStream in = getClass().getClassLoader().getResourceAsStream( "MapBundle_en_EN.properties" );
        bundle = new Properties();
        try {
            bundle.load( in );
            in.close();
        } catch ( IOException ex ) {
        }
    }

    @RequestMapping( "/map" )
    public ModelAndView mapView() {
        ModelAndView modelAndView = new ModelAndView( "mapview" );
        modelAndView.addObject( "url", propertiesBean.getProperties().getProperty( "route_service_url" ) );
        modelAndView.addObject( "latFrom", "14.3296708" );
        modelAndView.addObject( "lonFrom", "50.0430858" );
        modelAndView.addObject( "latTo", "14.5171250" );
        modelAndView.addObject( "lonTo", "50.1190931" );
        modelAndView.addObject( "prioritySelected", DistanceType.LENGTH.name() );
        List<Pair<String, String>> priorities = new ArrayList<>();
        for ( DistanceType allowedMetric : Provider.getAllowedMetrics() ) {
            priorities.add( new Pair<>( allowedMetric.name().toLowerCase(), bundle.getProperty( allowedMetric.name() ) ) );
        }
        modelAndView.addObject( "priorityOptions", priorities );
        modelAndView.addObject( "algorithmSelected", AlgorithmType.DIJKSTRA.name() );
        List<Pair<String, String>> algorithms = new ArrayList<>();
        for ( AlgorithmType allowedAlgorithm : Provider.getAllowedAlgorithms() ) {
            algorithms.add( new Pair<>( allowedAlgorithm.name().toLowerCase(), bundle.getProperty( allowedAlgorithm.name() ) ) );
        }
        modelAndView.addObject( "algorithmOptions", algorithms );
        return modelAndView;
    }

    @RequestMapping( value = "/map", params = { "latFrom", "lonFrom", "latTo", "lonTo", "priority", "algorithm" } )
    public ModelAndView mapView( @RequestParam( value = "latFrom" ) double latFrom,
            @RequestParam( value = "lonFrom" ) double lonFrom,
            @RequestParam( value = "latTo" ) double latTo,
            @RequestParam( value = "lonTo" ) double lonTo,
            @RequestParam( value = "priority" ) String priorityString,
            @RequestParam( value = "algorithm" ) String algorithmString ) {
        ModelAndView modelAndView = new ModelAndView( "mapview" );
        modelAndView.addObject( "url", propertiesBean.getProperties().getProperty( "route_service_url" ) );
        modelAndView.addObject( "latFrom", latFrom );
        modelAndView.addObject( "lonFrom", lonFrom );
        modelAndView.addObject( "latTo", latTo );
        modelAndView.addObject( "lonTo", lonTo );
        modelAndView.addObject( "prioritySelected", priorityString );
        List<Pair<String, String>> priorities = new ArrayList<>();
        for ( DistanceType allowedMetric : Provider.getAllowedMetrics() ) {
            priorities.add( new Pair<>( allowedMetric.name().toLowerCase(), bundle.getProperty( allowedMetric.name() ) ) );
        }
        modelAndView.addObject( "priorityOptions", priorities );
        modelAndView.addObject( "algorithmSelected", algorithmString );
        List<Pair<String, String>> algorithms = new ArrayList<>();
        for ( AlgorithmType allowedAlgorithm : Provider.getAllowedAlgorithms() ) {
            algorithms.add( new Pair<>( allowedAlgorithm.name().toLowerCase(), bundle.getProperty( allowedAlgorithm.name() ) ) );
        }
        modelAndView.addObject( "algorithmOptions", algorithms );
        return modelAndView;
    }
}
