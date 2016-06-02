/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model.beans;

import cz.certicon.routing.application.algorithm.Distance;
import cz.certicon.routing.application.algorithm.DistanceFactory;
import cz.certicon.routing.application.algorithm.RoutingAlgorithm;
import cz.certicon.routing.application.algorithm.algorithms.astar.StraightLineAStarRoutingAlgorithm;
import cz.certicon.routing.application.algorithm.algorithms.ch.OptimizedContractionHierarchiesRoutingAlgorithm;
import cz.certicon.routing.application.algorithm.algorithms.dijkstra.DijkstraRoutingAlgorithm;
import cz.certicon.routing.application.algorithm.data.number.LengthDistanceFactory;
import cz.certicon.routing.application.algorithm.data.number.TimeDistanceFactory;
import cz.certicon.routing.data.DistanceType;
import cz.certicon.routing.data.ch.ContractionHierarchiesDataRW;
import cz.certicon.routing.data.ch.sqlite.SqliteContractionHierarchiesDataRW;
import cz.certicon.routing.data.coordinates.CoordinateReader;
import cz.certicon.routing.data.coordinates.sqlite.SqliteCoordinateRW;
import cz.certicon.routing.data.graph.GraphReader;
import cz.certicon.routing.data.graph.sqlite.SqliteGraphRW;
import cz.certicon.routing.data.nodesearch.NodeSearcher;
import cz.certicon.routing.data.nodesearch.sqlite.SqliteNodeSearcher;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.model.basic.TimeUnits;
import cz.certicon.routing.model.basic.Trinity;
import cz.certicon.routing.model.entity.Coordinate;
import cz.certicon.routing.model.entity.Edge;
import cz.certicon.routing.model.entity.Graph;
import cz.certicon.routing.model.entity.GraphEntityFactory;
import cz.certicon.routing.model.entity.Node;
import cz.certicon.routing.model.entity.Shortcut;
import cz.certicon.routing.model.entity.neighbourlist.NeighborListGraphEntityFactory;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import cz.certicon.routing.web.model.AlgorithmType;
import cz.certicon.routing.web.model.Priority;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private CoordinateReader coordinateReader;
    private NodeSearcher nodeSearcher;
    private GraphEntityFactory graphEntityFactory;

    private final Map<Priority, Graph> graphs;
    private final Map<Pair<Priority, AlgorithmType>, RoutingAlgorithm> algorithms;
    private final Map<Priority, DistanceFactory> distanceFactories;

    @Autowired
    private PropertiesBean databasePropertiesBean;

    public GraphBean() {
        this.graphEntityFactory = new NeighborListGraphEntityFactory();
        this.graphs = new HashMap<>();
        this.algorithms = new HashMap<>();
        this.distanceFactories = new HashMap<>();
    }

    private DistanceFactory getDistanceFactory( Priority priority ) {
        DistanceFactory distanceFactory = distanceFactories.get( priority );
        if ( distanceFactory == null ) {
            switch ( priority ) {
                case LENGTH:
                    distanceFactory = new LengthDistanceFactory();
                    break;
                case TIME:
                    distanceFactory = new TimeDistanceFactory();
                    break;
                default:
                    throw new AssertionError( "Unknown priority: " + priority.name() );
            }
            distanceFactories.put( priority, distanceFactory );
        }
        return distanceFactory;
    }

    public Graph getGraph( Priority priority ) throws IOException {
        Graph graph = graphs.get( priority );
        if ( graph == null ) {
            System.out.println( "Reading..." );
            TimeMeasurement time = new TimeMeasurement();
            time.setTimeUnits( TimeUnits.MILLISECONDS );
            time.start();
            GraphReader graphReader = new SqliteGraphRW( databasePropertiesBean.getProperties() );
            graph = graphReader.read( new Pair<>( graphEntityFactory, getDistanceFactory( priority ) ) );
            graphReader.close();
            System.out.println( "Done reading in " + time.getTimeString() );
            graphs.put( priority, graph );
        }
        return graph;
    }

    public Map<Edge, List<Coordinate>> getCoordinates( Set<Edge> edges ) throws IOException {
        getCoordinateReader().open();
        Map<Edge, List<Coordinate>> edgeMap = getCoordinateReader().read( edges );
        getCoordinateReader().close();
        return edgeMap;
    }

    public RoutingAlgorithm getRoutingAlgorithm( Priority priority, AlgorithmType algorithm ) throws IOException {
        RoutingAlgorithm routingAlgorithm = algorithms.get( new Pair<>( priority, algorithm ) );
        if ( routingAlgorithm == null ) {
            Graph g = getGraph( priority );
            switch ( algorithm ) {
                case ASTAR:
                    routingAlgorithm = new StraightLineAStarRoutingAlgorithm( g, graphEntityFactory, getDistanceFactory( priority ) );
                    break;
                case DIJKSTRA:
                    routingAlgorithm = new DijkstraRoutingAlgorithm( g, graphEntityFactory, getDistanceFactory( priority ) );
                    break;
                case CONTRACTION_HIERARCHIES:
                    ContractionHierarchiesDataRW preprocessedDataAccess = new SqliteContractionHierarchiesDataRW( databasePropertiesBean.getProperties() );
                    DistanceType dt;
                    switch ( priority ) {
                        case LENGTH:
                            dt = DistanceType.LENGTH;
                            break;
                        case TIME:
                            dt = DistanceType.TIME;
                            break;
                        default:
                            throw new AssertionError( "Unknown priority: " + priority.name() );
                    }
                    Trinity<Map<Node.Id, Integer>, List<Shortcut>, DistanceType> preprocessedData = preprocessedDataAccess.read( new Trinity<>( g, graphEntityFactory, dt ) );
                    routingAlgorithm = new OptimizedContractionHierarchiesRoutingAlgorithm( g, graphEntityFactory, getDistanceFactory( priority ), preprocessedData.b, preprocessedData.a );
                    preprocessedDataAccess.close();
                    break;
                default:
                    throw new AssertionError( "Unknown algorithm: " + algorithm.name() );
            }
            algorithms.put( new Pair<>( priority, algorithm ), routingAlgorithm );
        }
        return routingAlgorithm;
    }

    public CoordinateReader getCoordinateReader() {
        if ( coordinateReader == null ) {
            coordinateReader = new SqliteCoordinateRW( databasePropertiesBean.getProperties() );
            //new XmlCoordinateReader( new FileSource( new File( Settings.COORDINATES_FILE_PATH ) ) );
            //new DatabaseCoordinatesRW( databasePropertiesBean.getConnectionProperties() );
        }
        return coordinateReader;
    }

    public Pair<Map<Node.Id, Distance>, Long> getClosestNodes( Coordinate coords, NodeSearcher.SearchFor searchFor, Priority priority ) throws IOException {
        if ( nodeSearcher == null ) {
            nodeSearcher = new SqliteNodeSearcher( databasePropertiesBean.getProperties() );
            //new GraphNodeSearcher( getGraph() );
        }
        return nodeSearcher.findClosestNodes( coords, getDistanceFactory( priority ), searchFor );
    }
}
