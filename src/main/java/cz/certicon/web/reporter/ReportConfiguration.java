package cz.certicon.web.reporter;

import cz.certicon.web.reporter.logic.logging.FakeReportLogger;
import cz.certicon.web.reporter.logic.logging.FileReportLogger;
import cz.certicon.web.reporter.logic.logging.GdocReportLogger;
import cz.certicon.web.reporter.logic.logging.ReportLogger;
import cz.certicon.web.reporter.services.ReportService;
import cz.certicon.web.reporter.logic.decoding.JavaScriptDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Configuration
@ComponentScan( basePackageClasses = { ReportConfiguration.class, ReportService.class } )
public class ReportConfiguration {

    @Bean
    public ReportLogger getGdocLogger( @Autowired @Qualifier( "connectionProperties" ) Properties properties ) {
        try {
            return new GdocReportLogger( properties.getProperty( "web.google.credentials" ), properties.getProperty( "web.google.script.id" ) );
        } catch ( IOException e ) {
            Logger.getLogger( getClass().getName() ).log( Level.SEVERE, "Could not find gdoc properties", e );
            return new FakeReportLogger();
        }
    }

    @Bean
    public ReportLogger getFileLogger() {
        return new FileReportLogger( () -> new File( "report" ) );
    }

    @Bean
    public Function<String,String> getMessageDecoder(){
        return new JavaScriptDecoder();
    }
}
