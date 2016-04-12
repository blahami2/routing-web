/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model.entity;

import cz.certicon.routing.data.graph.database.DatabaseGraphRW;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.entity.Graph;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Component
@Scope( "singleton" )
public class GraphBean implements Serializable {

    private Graph graph;

    @Autowired
    private GraphFactoriesBean graphFactoriesBean;

    public GraphBean() {
    }

    public Graph getGraph() {
        if ( graph == null ) {
            InputStream in = getClass().getClassLoader().getResourceAsStream( "database_connection.properties" );
            Properties connectionProperties = new Properties();
            try {
                connectionProperties.load( in );
                in.close();
                DatabaseGraphRW db = new DatabaseGraphRW( connectionProperties );
                graph = db.read( new Pair<>( graphFactoriesBean.getGraphEntityFactory(), graphFactoriesBean.getDistanceFactory() ) );
                db.close();
            } catch ( IOException ex ) {
                graph = null;
                Logger.getLogger( GraphBean.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }
        return graph;
    }

    public GraphFactoriesBean getGraphFactoriesBean() {
        return graphFactoriesBean;
    }
}
