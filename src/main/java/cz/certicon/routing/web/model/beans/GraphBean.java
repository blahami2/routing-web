/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model.beans;

import cz.certicon.routing.application.algorithm.AlgorithmType;
import cz.certicon.routing.memsensitive.algorithm.Route;
import cz.certicon.routing.memsensitive.algorithm.RouteBuilder;
import cz.certicon.routing.memsensitive.algorithm.RouteNotFoundException;
import cz.certicon.routing.memsensitive.algorithm.RoutingAlgorithm;
import cz.certicon.routing.memsensitive.algorithm.algorithms.AstarRoutingAlgorithm;
import cz.certicon.routing.memsensitive.algorithm.algorithms.ContractionHierarchiesRoutingAlgorithm;
import cz.certicon.routing.memsensitive.algorithm.algorithms.ContractionHierarchiesUbRoutingAlgorithm;
import cz.certicon.routing.memsensitive.algorithm.algorithms.DijkstraRoutingAlgorithm;
import cz.certicon.routing.memsensitive.algorithm.common.SimpleRouteBuilder;
import cz.certicon.routing.memsensitive.algorithm.preprocessing.ch.ContractionHierarchiesPreprocessor;
import cz.certicon.routing.memsensitive.algorithm.preprocessing.ch.calculators.SpatialHeuristicEdgeDifferenceCalculator;
import cz.certicon.routing.memsensitive.algorithm.preprocessing.ch.strategies.NeighboursOnlyRecalculationStrategy;
import cz.certicon.routing.memsensitive.data.ch.ContractionHierarchiesDataRW;
import cz.certicon.routing.memsensitive.data.ch.NotPreprocessedException;
import cz.certicon.routing.memsensitive.data.ch.sqlite.SqliteContractionHierarchiesRW;
import cz.certicon.routing.memsensitive.data.graph.GraphReader;
import cz.certicon.routing.memsensitive.data.graph.sqlite.SqliteGraphReader;
import cz.certicon.routing.memsensitive.data.nodesearch.EvaluableOnlyException;
import cz.certicon.routing.memsensitive.data.nodesearch.NodeSearcher;
import cz.certicon.routing.memsensitive.data.nodesearch.sqlite.SqliteNodeSearcher;
import cz.certicon.routing.memsensitive.data.path.PathReader;
import cz.certicon.routing.memsensitive.data.path.sqlite.SqlitePathReader;
import cz.certicon.routing.memsensitive.data.turntables.TurnTablesReader;
import cz.certicon.routing.memsensitive.data.turntables.sqlite.SqliteTurnTablesReader;
import cz.certicon.routing.memsensitive.model.entity.DistanceType;
import cz.certicon.routing.memsensitive.model.entity.Graph;
import cz.certicon.routing.memsensitive.model.entity.NodeSet;
import cz.certicon.routing.memsensitive.model.entity.Path;
import cz.certicon.routing.memsensitive.model.entity.PathBuilder;
import cz.certicon.routing.memsensitive.model.entity.ch.PreprocessedData;
import cz.certicon.routing.memsensitive.model.entity.ch.SimpleChDataFactory;
import cz.certicon.routing.memsensitive.model.entity.common.SimpleGraphBuilderFactory;
import cz.certicon.routing.memsensitive.model.entity.common.SimpleNodeSetBuilderFactory;
import cz.certicon.routing.memsensitive.model.entity.common.SimplePathBuilder;
import cz.certicon.routing.memsensitive.model.entity.common.SimpleTurnTablesBuilder;
import cz.certicon.routing.model.basic.TimeUnits;
import cz.certicon.routing.model.entity.Coordinate;
import cz.certicon.routing.model.entity.NodeSetBuilderFactory;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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

    public NodeSearcher nodeSearcher;
    public final RouteBuilder<Route, Graph> routeBuilder;
    public final PathBuilder<Path, Graph> pathBuilder;
    public PathReader<Graph> pathReader;
    public final Map<DistanceType, GraphData> graphDataMap;

    @Autowired
    private PropertiesBean databasePropertiesBean;

    public GraphBean() {
        this.routeBuilder = new SimpleRouteBuilder();
        this.pathBuilder = new SimplePathBuilder();
        this.graphDataMap = new HashMap<>();
    }

    public Graph getGraph( DistanceType distanceType ) throws IOException {
        return getGraphData( distanceType ).getGraph();
    }

    public RoutingAlgorithm getRoutingAlgorithm( DistanceType distanceType, AlgorithmType algorithm ) throws IOException {
        RoutingAlgorithm routingAlgorithm = getGraphData( distanceType ).getAlgorithm( algorithm );
        if ( routingAlgorithm == null ) {
            Graph g = getGraphData( distanceType ).getGraph();
            switch ( algorithm ) {
                case ASTAR:
                    routingAlgorithm = new AstarRoutingAlgorithm( g, distanceType );
                    break;
                case DIJKSTRA:
                    routingAlgorithm = new DijkstraRoutingAlgorithm( g );
                    break;
                case CONTRACTION_HIERARCHIES:
                    routingAlgorithm = new ContractionHierarchiesRoutingAlgorithm( g, getGraphData( distanceType ).getPreprocessedData() );
                    break;
                case CONTRACTION_HIERARCHIES_UB:
                    routingAlgorithm = new ContractionHierarchiesUbRoutingAlgorithm( g, getGraphData( distanceType ).getPreprocessedData() );
                    break;
                default:
                    throw new AssertionError( "Unknown algorithm: " + algorithm.name() );
            }
            getGraphData( distanceType ).putAlgorithm( algorithm, routingAlgorithm );
        }
        return routingAlgorithm;
    }

    public Route getRoute( DistanceType distanceType, AlgorithmType algorithm, Map<Integer, RoutingAlgorithm.NodeEntry> from, Map<Integer, RoutingAlgorithm.NodeEntry> to ) throws IOException, RouteNotFoundException {
        RoutingAlgorithm<Graph> alg = getRoutingAlgorithm( distanceType, algorithm );
        Route route = alg.route( routeBuilder, from, to );
        return route;
    }

    public Path getPath( DistanceType distanceType, Route route, Coordinate source, Coordinate target ) throws IOException {
        if ( pathReader == null ) {
            pathReader = new SqlitePathReader( databasePropertiesBean.getProperties() );
        }
        return pathReader.readPath( pathBuilder, getGraphData( distanceType ).getGraph(), route, source, target );
    }

    public Path getPath( DistanceType distanceType, long edgeId, Coordinate source, Coordinate target ) throws IOException {
        if ( pathReader == null ) {
            pathReader = new SqlitePathReader( databasePropertiesBean.getProperties() );
        }
        return pathReader.readPath( pathBuilder, getGraphData( distanceType ).getGraph(), edgeId, source, target );
    }

    public NodeSet getClosestNodes( DistanceType distanceType, Coordinate source, Coordinate target ) throws IOException, EvaluableOnlyException {
        if ( nodeSearcher == null ) {
            nodeSearcher = new SqliteNodeSearcher( databasePropertiesBean.getProperties() );
        }
        return nodeSearcher.findClosestNodes( getGraphData( distanceType ).nodeSetFactory, source, target );
    }

    public GraphData getGraphData( DistanceType distanceType ) throws IOException {
        GraphData gd = graphDataMap.get( distanceType );
        if ( gd == null ) {
            System.out.println( "Reading graph..." );
            TimeMeasurement time = new TimeMeasurement();
            time.setTimeUnits( TimeUnits.MILLISECONDS );
            time.start();
            GraphReader graphReader = new SqliteGraphReader( databasePropertiesBean.getProperties() );
            Graph graph = graphReader.readGraph( new SimpleGraphBuilderFactory( distanceType ), distanceType );
            System.out.println( "Done reading graph in " + time.getCurrentTimeString() );
            System.out.println( "Reading preprocessed data..." );
            time.start();
            NodeSetBuilderFactory<NodeSet<Graph>> nodeSetFactory = new SimpleNodeSetBuilderFactory( graph, distanceType );
            TurnTablesReader ttReader = new SqliteTurnTablesReader( databasePropertiesBean.getProperties() );
            graph.setTurnRestrictions( ttReader.read( graph, new SimpleTurnTablesBuilder() ).getTr() );
            ContractionHierarchiesDataRW chDataRw = new SqliteContractionHierarchiesRW( databasePropertiesBean.getProperties() );
            ContractionHierarchiesPreprocessor preprocessor = new ContractionHierarchiesPreprocessor();
            preprocessor.setEdgeDifferenceCalculator( new SpatialHeuristicEdgeDifferenceCalculator( graph.getNodeCount() ) );
            preprocessor.setNodeRecalculationStrategy( new NeighboursOnlyRecalculationStrategy() );
            PreprocessedData preprocessedData;
            preprocessedData = chDataRw.read( new SimpleChDataFactory( graph, distanceType ), graph, preprocessor );
            preprocessedData.setTurnRestrictions( ttReader.read( graph, new SimpleTurnTablesBuilder(), preprocessedData ).getTr() );
            System.out.println( "Done reading preprocessed data in " + time.getCurrentTimeString() );
            gd = new GraphData( graph, nodeSetFactory, preprocessedData );
            graphDataMap.put( distanceType, gd );
        }
        return gd;
    }

    public static class GraphData {

        private final Graph graph;
        private final NodeSetBuilderFactory<NodeSet<Graph>> nodeSetFactory;
        private final PreprocessedData preprocessedData;
        private final Map<AlgorithmType, RoutingAlgorithm<Graph>> algorithmMap;

        public GraphData( Graph graph, NodeSetBuilderFactory<NodeSet<Graph>> nodeSetFactory, PreprocessedData preprocessedData ) {
            this.graph = graph;
            this.nodeSetFactory = nodeSetFactory;
            this.preprocessedData = preprocessedData;
            this.algorithmMap = new HashMap<>();
        }

        public Graph getGraph() {
            return graph;
        }

        public NodeSetBuilderFactory<NodeSet<Graph>> getNodeSetFactory() {
            return nodeSetFactory;
        }

        public PreprocessedData getPreprocessedData() {
            return preprocessedData;
        }

        public RoutingAlgorithm<Graph> getAlgorithm( AlgorithmType algorithmType ) {
            return algorithmMap.get( algorithmType );
        }

        public void putAlgorithm( AlgorithmType algorithmType, RoutingAlgorithm<Graph> routingAlgorithm ) {
            algorithmMap.put( algorithmType, routingAlgorithm );
        }
    }
}
