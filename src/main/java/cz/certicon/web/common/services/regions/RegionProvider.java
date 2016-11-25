package cz.certicon.web.common.services.regions;

import cz.certicon.web.common.model.Region;
import cz.certicon.web.common.services.regions.data.RegionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Service
public class RegionProvider {

    private final RegionDAO regionDAO;

    public RegionProvider( @Autowired RegionDAO regionDAO ) {
        this.regionDAO = regionDAO;
    }

    public List<Region> getRegions() throws IOException {
        return regionDAO.loadAll();
    }

    public List<Region> getRegions( long id ) throws IOException {
        List<Region> regions = regionDAO.loadAll();
        return regions.stream().filter( r -> ( r.getId() & id ) > 0 ).collect( Collectors.toList() );
    }

    public Region getRegion( long id ) throws IOException {
        return regionDAO.loadById( id );
    }
}
