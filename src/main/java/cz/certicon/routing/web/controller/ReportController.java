/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.controller;

import cz.certicon.routing.application.algorithm.AlgorithmType;
import cz.certicon.routing.memsensitive.model.entity.DistanceType;
import cz.certicon.routing.model.entity.Coordinate;
import cz.certicon.routing.web.data.logging.report.FakeReportLogger;
import cz.certicon.routing.web.data.logging.report.ReportLogger;
import cz.certicon.routing.web.data.logging.report.file.FileReportLogger;
import cz.certicon.routing.web.data.logging.report.googleapi.GoogleScriptApiReportLogger;
import cz.certicon.routing.web.model.beans.PropertiesBean;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PropertiesBean propertiesBean;

    private ReportLogger primaryReportLogger;
    private ReportLogger secondaryReportLogger;

    private void init() {
        this.secondaryReportLogger = new FileReportLogger();
        try {
            this.primaryReportLogger = new GoogleScriptApiReportLogger(
                    propertiesBean.getProperties().getProperty( "google_credentials" ),
                    propertiesBean.getProperties().getProperty( "script_id" ) );
        } catch ( IOException ex ) {
            this.primaryReportLogger = this.secondaryReportLogger;
            this.secondaryReportLogger = new FakeReportLogger();
            Logger.getLogger( ReportController.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    @RequestMapping( "/report" )
    public Result report( @RequestParam( value = "latFrom" ) double latFrom,
            @RequestParam( value = "lonFrom" ) double lonFrom,
            @RequestParam( value = "latTo" ) double latTo,
            @RequestParam( value = "lonTo" ) double lonTo,
            @RequestParam( value = "priority" ) String priority,
            @RequestParam( value = "algorithm" ) String algorithm,
            @RequestParam( value = "message" ) String message ) {

        if ( primaryReportLogger == null || secondaryReportLogger == null ) {
            init();
        }

//        Log.dln( FILENAME, "=== log ===" );
//        DateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss dd.MM.yyyy" );
//        Log.dln( FILENAME, dateFormat.format( Calendar.getInstance().getTime() ) );
        Coordinate from = new Coordinate( latFrom, lonFrom );
        Coordinate to = new Coordinate( latTo, lonTo );
        DistanceType priorityType;
        try {
            priorityType = DistanceType.valueOf( priority.toUpperCase() );
        } catch ( IllegalArgumentException ex ) {
            priorityType = DistanceType.LENGTH;
        }
        AlgorithmType algorithmType;
        try {
            algorithmType = AlgorithmType.valueOf( algorithm );
        } catch ( IllegalArgumentException ex ) {
            algorithmType = AlgorithmType.DIJKSTRA;
        }
        String decodedMessage;
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName( "JavaScript" );
        try {
            decodedMessage = (String) engine.eval( "decodeURIComponent('" + message + "')" );
        } catch ( ScriptException ex ) {
            decodedMessage = message;
        }

        String link = propertiesBean.getProperties().getProperty( "route_service_url" ) + "map?latFrom=" + latFrom + "&lonFrom=" + lonFrom + "&latTo=" + latTo + "&lonTo=" + lonTo + "&priority=" + priority + "&algorithm=" + algorithm + "&route=true";

        try {
            primaryReportLogger.log( from, to, algorithmType, priorityType, decodedMessage, link );
            secondaryReportLogger.log( from, to, algorithmType, priorityType, decodedMessage, link );
        } catch ( IOException ex ) {
            Logger.getLogger( ReportController.class.getName() ).log( Level.SEVERE, null, ex );
            return new Result( "FAIL" );
        }
        return new Result( "OK" );
    }

    public static class Result {

        private final String result;

        public Result( String result ) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }
    }

}
