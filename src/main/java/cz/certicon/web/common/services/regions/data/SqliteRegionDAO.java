package cz.certicon.web.common.services.regions.data;

import cz.certicon.routing.data.basic.database.SimpleDatabase;
import cz.certicon.web.common.model.Region;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class SqliteRegionDAO implements RegionDAO {

    private final SimpleDatabase database;

    public SqliteRegionDAO( Properties connectionProperties ) {
        this.database = SimpleDatabase.newSqliteDatabase( connectionProperties );
    }

    @Override
    public Region loadById( long id ) throws IOException {
        try {
            ResultSet rs = database.read( "SELECT * FROM regions WHERE id = " + id );
            if ( rs.next() ) {
                return new Region( rs.getLong( "id" ), rs.getLong( "cell_id" ), rs.getString( "name" ) );
            }
            throw new IOException( "Could not find region with id: " + id );
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public List<Region> loadAll() throws IOException {
        try {
            List<Region> regions = new ArrayList<>();
            ResultSet rs = database.read( "SELECT * FROM regions" );
            while ( rs.next() ) {
                regions.add( new Region( rs.getLong( "id" ), rs.getLong( "cell_id" ), rs.getString( "name" ) ) );
            }
            return regions;
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }
}
