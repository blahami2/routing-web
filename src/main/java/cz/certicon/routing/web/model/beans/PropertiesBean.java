/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model.beans;

import cz.certicon.routing.web.model.Global;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Component
@Scope( "singleton" )
public class PropertiesBean {

    private Properties connectionProperties;
    private Properties spatialiteProperties;

    public Properties getConnectionProperties() {
        if ( connectionProperties == null ) {
            InputStream in = getClass().getClassLoader().getResourceAsStream( "database_connection.properties" );
            connectionProperties = new Properties();
            try {
                connectionProperties.load( in );
                in.close();
            } catch ( IOException ex ) {
                connectionProperties = null;
                Logger.getLogger( GraphBean.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }
        return connectionProperties;
    }

    public Properties getProperties() {
        if ( spatialiteProperties == null ) {
            try {
                InputStream in = null;
                try {
                    if ( Global.PROPERTIES_PATH != null ) {
                        in = new FileInputStream( Global.PROPERTIES_PATH );
                        System.out.println( "properties loaded properly" );
                    }
                } catch ( FileNotFoundException ex ) {
                    System.out.println( "File not found: " + ex.getMessage() );
                    // handled below
                }
                if ( in == null ) {
                    in = getClass().getClassLoader().getResourceAsStream( "spatialite.properties" );
                    System.out.println( "properties overwritten" );
                }
                spatialiteProperties = new Properties();
                spatialiteProperties.load( in );
                in.close();
            } catch ( IOException ex ) {
                spatialiteProperties = null;
                Logger.getLogger( GraphBean.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }
        return spatialiteProperties;

    }

}
