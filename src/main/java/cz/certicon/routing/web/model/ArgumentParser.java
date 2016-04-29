/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.web.model;

import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.utils.cor.ChainGroup;
import cz.certicon.routing.utils.cor.ChainLink;
import cz.certicon.routing.utils.cor.XorChainGroup;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ArgumentParser {

    private final ChainGroup<String> chain;

    public ArgumentParser() {
        chain = new XorChainGroup<>();
        chain.addChainLink( (ChainLink<String>) ( String keyval ) -> {
            KeyValuePair parsed = parseKeyValueString( keyval );
            if ( "properties".equals( parsed.getKey() ) ) {
                System.out.println( "properties path = '" + parsed.getValue() + "'" );
                Settings.PROPERTIES_PATH = parsed.getValue();
                return true;
            }
            return false;
        } );
    }

    public void parse( String... args ) {
        for ( String arg : args ) {
            chain.execute( arg );
        }
    }

    private static KeyValuePair parseKeyValueString( String keyValue ) {
        String[] split = keyValue.split( "=" );
        return new KeyValuePair( split[0], split[1] );
    }

    private static class KeyValuePair extends Pair<String, String> {

        public KeyValuePair( String a, String b ) {
            super( a, b );
        }

        public String getKey() {
            return a;
        }

        public String getValue() {
            return b;
        }

    }
}
