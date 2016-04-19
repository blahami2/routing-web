/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model.beans;

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
public class DatabasePropertiesBean {

    private Properties connectionProperties;

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

}
