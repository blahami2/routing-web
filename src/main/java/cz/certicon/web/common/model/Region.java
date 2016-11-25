package cz.certicon.web.common.model;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
@Value
@EqualsAndHashCode(exclude = {"name"})
public class Region {
    long id;
    long cellId;
    String name;

}
