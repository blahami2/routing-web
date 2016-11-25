package cz.certicon.web.common.services.regions.data;

import cz.certicon.web.common.model.Region;

import java.io.IOException;
import java.util.List;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public interface RegionDAO {
    Region loadById( long id ) throws IOException;

    List<Region> loadAll() throws IOException;
}
