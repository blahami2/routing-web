/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping( "/map" )
    public ModelAndView mapView() {
        ModelAndView modelAndView = new ModelAndView( "mapview.jsp" );
        modelAndView.getModelMap().addAttribute( "name", "YOoooo" );
        System.out.println( "returning mapview" );
        return modelAndView;
    }
}
