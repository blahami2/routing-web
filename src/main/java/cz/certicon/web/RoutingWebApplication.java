package cz.certicon.web;

import cz.certicon.web.common.services.regions.data.RegionDAO;
import cz.certicon.web.common.services.regions.data.SqliteRegionDAO;
import cz.certicon.web.map.MapConfiguration;
import cz.certicon.web.router.RouterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

@PropertySource( "connection.properties" )
@SpringBootApplication
@Import( { RouterConfiguration.class, MapConfiguration.class } )
public class RoutingWebApplication {

    public static void main( String[] args ) {
        SpringApplication.run( RoutingWebApplication.class, args );
    }


    @Bean
    public RegionDAO getRegionDAO( @Autowired @Qualifier( "connectionProperties" ) Properties connectionProperties ) {
        return new SqliteRegionDAO( connectionProperties );
    }

    @Bean
    @Qualifier( "connectionProperties" )
    public Properties getConnectionProperties( @Value( "${db.spatialite_path}" ) String spatialitePath, @Value( "${db.url}" ) String url, @Value( "${db.driver}" ) String driver ) {
        Properties properties = new Properties();
        properties.put( "spatialite_path", spatialitePath );
        properties.put( "url", url );
        properties.put( "driver", driver );
        return properties;
    }
}
