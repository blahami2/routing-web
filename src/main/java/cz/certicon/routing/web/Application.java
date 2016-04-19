/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web;

import cz.certicon.routing.web.model.Settings;
import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@ComponentScan
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure( SpringApplicationBuilder application ) {
        return application.sources( Application.class );
    }

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        String filePath = "C:\\Routing\\Data\\CZ";
        if ( args.length > 0 ) {
            filePath = args[0];
        }
        File file = new File( filePath );
        Settings.GRAPH_FILE_PATH = file.getAbsolutePath() + File.separator + file.getName() + ".graph.xml";
        Settings.COORDINATES_FILE_PATH = file.getAbsolutePath() + File.separator + file.getName() + ".coords.xml";
        SpringApplication.run( Application.class, args );
    }

}
