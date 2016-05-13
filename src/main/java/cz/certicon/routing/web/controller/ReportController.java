/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.controller;

import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.utils.debug.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@RestController
public class ReportController {

    private static final String FILENAME = "report";

    @RequestMapping( "/report" )
    public String report( @RequestParam( value = "latFrom" ) double latFrom,
            @RequestParam( value = "lonFrom" ) double lonFrom,
            @RequestParam( value = "latTo" ) double latTo,
            @RequestParam( value = "lonTo" ) double lonTo,
            @RequestParam( value = "priority" ) String priorityString,
            @RequestParam( value = "algorithm" ) String algorithmString,
            @RequestParam( value = "message" ) String message ) {

        Log.dln( FILENAME, "=== log ===" );
        DateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss dd.MM.yyyy" );
        Log.dln( FILENAME, dateFormat.format( Calendar.getInstance().getTime() ) );
        Coordinates from = new Coordinates( latFrom, lonFrom );
        Coordinates to = new Coordinates( latTo, lonTo );
        Log.dln( FILENAME, "from: " + from );
        Log.dln( FILENAME, "to: " + to );
        Log.dln( FILENAME, latFrom + "," + lonFrom );
        Log.dln( FILENAME, latTo + "," + lonTo );
        Log.dln( FILENAME, "priority: " + priorityString );
        Log.dln( FILENAME, "algorithm: " + algorithmString );

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName( "JavaScript" );
        try {
            Log.dln( FILENAME, "message: " + engine.eval( "decodeURIComponent('" + message + "')" ) );
        } catch ( ScriptException ex ) {
            Log.dln( FILENAME, "message: " + message );
        }
        return "OK";
    }

}
