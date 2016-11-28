package cz.certicon.web.router.services;

import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayBuilder;
import cz.certicon.routing.algorithm.sara.preprocessing.overlay.OverlayCreator;
import cz.certicon.routing.model.graph.SaraGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Service
public class GraphSupplyService {

    private final OverlayBuilder overlayBuilder;
    private final SaraGraph graph;

    public GraphSupplyService( @Autowired  @Qualifier( "connectionProperties" ) Properties connectionProperties) {
        OverlayBuilder.keepShortcuts = true;
        OverlayCreator creator = new OverlayCreator();
        OverlayCreator.SaraSetup setup = creator.getSetup();

        setup.setSpatialModulePath( connectionProperties.getProperty( "spatialite_path" ) );
        String dbUrl = connectionProperties.getProperty( "url" ).substring( "jdbc:sqlite:".length() );
        dbUrl = dbUrl.substring( 0, dbUrl.length() - ".sqlite".length() );
        int lastSlashIdx = dbUrl.lastIndexOf( "/" );
        String dbFolder = dbUrl.substring( 0, lastSlashIdx + 1 );
        String dbName = dbUrl.substring( lastSlashIdx + 1 );
        setup.setDbFolder( dbFolder );
//        setup.setRandomSeed( 123 );
//        setup.setLayerCount( 5 );
//        setup.setMaxCellSize( 20 );
//        setup.setNumberOfAssemblyRuns( 1 );

        // D://prog-20-5.sqlite
        setup.setDbName( dbName );

        // punch and save
        //setup.runPunch = true;
        //no punch, load only
        setup.setRunPunch( false );

        overlayBuilder = creator.createBuilder();
        overlayBuilder.buildOverlays();

        graph = overlayBuilder.getSaraGraph();
    }

    public OverlayBuilder getOverlayBuilder(){
        return overlayBuilder;
    }

    public SaraGraph getGraph(){
        return graph;
    }
}
