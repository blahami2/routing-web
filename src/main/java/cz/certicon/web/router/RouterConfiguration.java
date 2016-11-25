package cz.certicon.web.router;

import cz.certicon.routing.algorithm.RoutingAlgorithm;
import cz.certicon.routing.algorithm.sara.preprocessing.PreprocessingInput;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayCreator;
import cz.certicon.routing.algorithm.sara.query.mld.MLDRecursiveRouteUnpacker;
import cz.certicon.routing.algorithm.sara.query.mld.RouteUnpacker;
import cz.certicon.routing.data.*;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.web.router.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
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
@Configuration
@ComponentScan( basePackageClasses = { RouterConfiguration.class, RouteService.class } )
public class RouterConfiguration {

    @Bean
    public GraphDAO getGraphDao( @Autowired @Qualifier( "connectionProperties" ) Properties connectionProperties ) {
        return new SqliteGraphDAO( connectionProperties );
    }

    @Bean
    public RouteDataDAO getRouteDataDao( @Autowired @Qualifier( "connectionProperties" ) Properties connectionProperties ) {
        return new SqliteRouteDAO( connectionProperties );
    }

    @Bean
    public PointSearcher getPointSearcher( @Autowired @Qualifier( "connectionProperties" ) Properties connectionProperties ) {
        return new SqlitePointSearcher( connectionProperties );
    }

    @Bean
    public GraphDataDao getGraphDataDao( @Autowired @Qualifier( "connectionProperties" ) Properties connectionProperties ) {
        return new SqliteGraphDataDAO( connectionProperties );
    }

}
