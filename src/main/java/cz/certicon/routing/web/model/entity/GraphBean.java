/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model.entity;

import cz.certicon.routing.application.algorithm.Distance;
import cz.certicon.routing.data.basic.FileSource;
import cz.certicon.routing.data.coordinates.CoordinateReader;
import cz.certicon.routing.data.coordinates.database.DatabaseCoordinatesRW;
import cz.certicon.routing.data.coordinates.xml.XmlCoordinateReader;
import cz.certicon.routing.data.graph.GraphReader;
import cz.certicon.routing.data.graph.database.DatabaseGraphRW;
import cz.certicon.routing.data.graph.xml.XmlGraphReader;
import cz.certicon.routing.data.nodesearch.NodeSearcher;
import cz.certicon.routing.data.nodesearch.xml.GraphNodeSearcher;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.model.entity.Edge;
import cz.certicon.routing.model.entity.Graph;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.FileDataSource;
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
    private CoordinateReader coordinateReader;
    private NodeSearcher nodeSearcher;

    @Autowired
    private GraphFactoriesBean graphFactoriesBean;

    @Autowired
    private DatabasePropertiesBean databasePropertiesBean;

    public GraphBean() {
    }

    public Graph getGraph() throws IOException {
        if ( graph == null ) {
            GraphReader graphReader = new XmlGraphReader( new FileSource( new File( "C:\\Routing\\Data\\CZ\\CZ.graph.xml" ) ) );//= new DatabaseGraphRW( databasePropertiesBean.getConnectionProperties() );
            graph = graphReader.read( new Pair<>( graphFactoriesBean.getGraphEntityFactory(), graphFactoriesBean.getDistanceFactory() ) );
            graphReader.close();
        }
        return graph;
    }

    public Map<Edge, List<Coordinates>> getCoordinates( Set<Edge> edges ) throws IOException {
        if ( coordinateReader == null ) {
            coordinateReader = new XmlCoordinateReader( new FileSource( new File( "C:\\Routing\\Data\\CZ\\CZ.coords.xml" ) ) );//= new DatabaseCoordinatesRW( databasePropertiesBean.getConnectionProperties() );
        }
        coordinateReader.open();
        Map<Edge, List<Coordinates>> edgeMap = coordinateReader.read( edges );
        coordinateReader.close();
        return edgeMap;
    }

    public Map<Coordinates, Distance> getClosestNodes( Coordinates coords ) throws IOException {
        if ( nodeSearcher == null ) {
            nodeSearcher = new GraphNodeSearcher( getGraph() );
        }
        return nodeSearcher.findClosestNodes( coords, graphFactoriesBean.getDistanceFactory() );
    }
}
