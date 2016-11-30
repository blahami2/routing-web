package cz.certicon.web.reporter.logic.decoding;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Function;

/**
 * @author Michael Blaha {@literal <blahami2@gmail.com>}
 */
public class JavaScriptDecoder implements Function<String, String> {

    private final ScriptEngine engine;

    public JavaScriptDecoder() {
        ScriptEngineManager factory = new ScriptEngineManager();
        engine = factory.getEngineByName( "JavaScript" );
    }

    @Override
    public String apply( String message ) {
        try {
            return (String) engine.eval( "decodeURIComponent('" + message + "')" );
        } catch ( ScriptException ex ) {
            return message;
        }
    }
}
