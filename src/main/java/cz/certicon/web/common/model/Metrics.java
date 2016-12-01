package cz.certicon.web.common.model;

import cz.certicon.routing.model.graph.Metric;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public enum Metrics {
    LENGTH, TIME;

    public Metric toMetric() {
        switch ( this ) {
            case LENGTH:
                return Metric.LENGTH;
            case TIME:
                return Metric.TIME;
            default:
                return Metric.LENGTH;
        }
    }
}
