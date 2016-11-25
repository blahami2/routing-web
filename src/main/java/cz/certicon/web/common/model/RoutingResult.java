package cz.certicon.web.common.model;


import cz.certicon.routing.model.values.Coordinate;
import lombok.Value;

import java.util.List;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Value
public class RoutingResult {
    private long length;
    private long time;
    private long executionTime;
    private List<Coordinate> coords;

    public RoutingResult(long length, long time, long executionTime, List<Coordinate> coords) {
        this.length = length;
        this.time = time;
        this. executionTime = executionTime;
        this.coords = coords;
    }
}
