package cz.certicon.web.router;

import cz.certicon.routing.algorithm.RoutingAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.PreprocessingInput;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayCreator;
import cz.certicon.routing.algorithm.sara.query.mld.MLDRecursiveRouteUnpacker;
import cz.certicon.routing.algorithm.sara.query.mld.RouteUnpacker;
import cz.certicon.routing.data.GraphDAO;
import cz.certicon.routing.data.RouteDataDAO;
import cz.certicon.routing.data.SqliteGraphDAO;
import cz.certicon.routing.data.SqliteRouteDAO;
import cz.certicon.routing.model.basic.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
//@PropertySource( "connection.properties" )
@Configuration
@ComponentScan
public class RouterConfiguration {

    @Bean
    public RoutingAlgorithm multileveldijkstra() {
        return null;
    }

    @Bean
    public RoutingAlgorithm dijkstra() {
        return null;
    }

    @Bean
    public GraphDAO getGraphDao() {
        return new SqliteGraphDAO( getConnectionProperties() );
    }

    @Bean
    public Properties getConnectionProperties(
//            @Value( "${db.spatialite_path}" ) String spatialitePath
    ) {
        Properties properties = new Properties(); // load propeties
        return properties;
    }

    @Bean
    public PreprocessingInput getPreprocessingInput() {
        return null;
    }

    @Bean
    public OverlayCreator getOverlayCreator() {
        OverlayCreator creator = new OverlayCreator();
        OverlayCreator.SaraSetup setup = creator.getSetup();
        // setup.setShit(...)
        return creator;
    }

    @Bean
    public RouteUnpacker getRouteUnpacker() {
        return new MLDRecursiveRouteUnpacker();
    }

    @Bean
    public RouteDataDAO getRouteDataDao() {
        return new SqliteRouteDAO( getConnectionProperties() );
    }

}
