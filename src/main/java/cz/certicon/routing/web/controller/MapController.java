/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.controller;

import cz.certicon.routing.web.model.beans.PropertiesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping( "/map" )
    public ModelAndView mapView() {
        ModelAndView modelAndView = new ModelAndView( "mapview" );
        modelAndView.addObject( "url", propertiesBean.getProperties().getProperty( "route_service_url" ) );
        System.out.println( modelAndView );
        return modelAndView;
    }
}
