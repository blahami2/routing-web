/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.routing_web.model;

import cz.certicon.routing.model.entity.Coordinate;
import cz.certicon.routing.model.entity.Edge;
import cz.certicon.routing.model.entity.Node;
import cz.certicon.routing.model.entity.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class JsonPathParser {

    public List<Coordinate> toCoordinates( Path path ) {
        List<Coordinate> track = new ArrayList<>();
        Node source = path.getSourceNode();
        Node currentNode = source;
        System.out.println( "PATH = " + path );
        for ( Edge edge : path ) {
            System.out.println( "edge = " + edge );
            List<Coordinate> coordinates = edge.getCoordinates();
            if ( currentNode.equals( edge.getSourceNode() ) ) {
                for ( int i = 0; i < coordinates.size(); i++ ) {
                    Coordinate coord = coordinates.get( i );
                    track.add( coord );
                }
                currentNode = edge.getTargetNode();
            } else {
                for ( int i = coordinates.size() - 1; i >= 0; i-- ) {
                    Coordinate coord = coordinates.get( i );
                    track.add( coord );
                }
                currentNode = edge.getSourceNode();
            }
        }
        return track;
    }
}
