/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public enum Priority {
    LENGTH, TIME;

    public static Priority valueOfCaseInsensitive( String priority ) {
        for ( Priority value : values() ) {
            if ( value.name().equalsIgnoreCase( priority ) ) {
                return value;
            }
        }
        return valueOf( priority );
    }
}
