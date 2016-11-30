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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@Import( { RouterConfiguration.class, MapConfiguration.class, RouterConfiguration.class } )
public class RoutingWebApplication {

    static Properties properties;

    public static void main( String[] args ) throws IOException {
        if(args.length == 0){
            throw new IllegalArgumentException( "Must provide web.properties" );
        }
        properties = new Properties(  );
        InputStream in = new FileInputStream( args[0] );
        properties.load( in );
        in.close();
        SpringApplication.run( RoutingWebApplication.class, args );
    }


    @Bean
    public RegionDAO getRegionDAO( @Autowired @Qualifier( "connectionProperties" ) Properties connectionProperties ) {
        return new SqliteRegionDAO( connectionProperties );
    }

    @Bean
    @Qualifier( "connectionProperties" )
    public Properties getConnectionProperties() {
        properties.put( "spatialite_path", properties.getProperty( "db.spatialite_path" ) );
        properties.put( "driver", properties.getProperty( "db.driver" ) );
        properties.put( "url", properties.getProperty( "db.url" ) );
        return properties;
    }
}
