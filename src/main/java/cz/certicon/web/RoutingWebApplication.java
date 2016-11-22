package cz.certicon.web;

import cz.certicon.web.map.MapConfiguration;
import cz.certicon.web.router.RouterConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import( { RouterConfiguration.class, MapConfiguration.class } )
public class RoutingWebApplication {

    public static void main( String[] args ) {
        SpringApplication.run( RoutingWebApplication.class, args );
    }
}
