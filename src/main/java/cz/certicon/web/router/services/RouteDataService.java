package cz.certicon.web.router.services;

import cz.certicon.routing.data.RouteDataDAO;
import cz.certicon.routing.model.Route;
import cz.certicon.routing.model.RouteData;
import cz.certicon.routing.model.graph.Edge;
import cz.certicon.routing.model.graph.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Service
@Scope( "singleton" )
public class RouteDataService {

    private final RouteDataDAO routeDataDAO;

    public RouteDataService( @Autowired RouteDataDAO routeDataDAO ) {
        this.routeDataDAO = routeDataDAO;
    }

    synchronized public <N extends Node<N, E>, E extends Edge<N, E>> RouteData<E> loadRouteData( Route<N, E> route ) throws IOException {
        return routeDataDAO.loadRouteData( route );
    }
}
