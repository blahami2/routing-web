/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.data;

import cz.certicon.routing.application.algorithm.AlgorithmType;
import cz.certicon.routing.memsensitive.model.entity.DistanceType;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class Provider {

    public static AlgorithmType[] getAllowedAlgorithms() {
        return new AlgorithmType[]{ AlgorithmType.DIJKSTRA, AlgorithmType.ASTAR, AlgorithmType.CONTRACTION_HIERARCHIES, AlgorithmType.CONTRACTION_HIERARCHIES_UB };
    }

    public static DistanceType[] getAllowedMetrics() {
        return new DistanceType[]{ DistanceType.LENGTH, DistanceType.TIME };
    }
}
