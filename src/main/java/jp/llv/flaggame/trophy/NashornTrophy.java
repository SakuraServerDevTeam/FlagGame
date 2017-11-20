/*
 * Copyright (C) 2017 toyblocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.llv.flaggame.trophy;

import java.util.function.Consumer;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jp.llv.flaggame.api.FlagGameAPI;

/**
 *
 * @author toyblocks
 */
public abstract class NashornTrophy extends BaseTrophy {

    private static final String[] SCRIPT_OPTIONS = {
        "--no-java",
        "--no-syntax-extensions"
    };
    private static final String[] SCRIPT_FUNCTIONS_FOR_REMOVAL = {
        "print",
        "load",
        "loadWithNewGlobal",
        "quit", "exit"
    };

    private String script = "false";
    private transient CompiledScript compiledScript;

    public NashornTrophy(String name) {
        super(name);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
        this.compiledScript = null;
    }

    private void compileScript() throws ScriptException {
        if (compiledScript != null) {
            return;
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        NashornScriptEngineFactory factory = manager.getEngineFactories().stream()
                .filter(NashornScriptEngineFactory.class::isInstance)
                .map(NashornScriptEngineFactory.class::cast)
                .findFirst()
                .orElseThrow(() -> new RuntimeException());
        NashornScriptEngine nashorn = (NashornScriptEngine) factory.getScriptEngine(SCRIPT_OPTIONS);
        compiledScript = nashorn.compile(script);
    }

    private Bindings createBindings(FlagGameAPI api) throws ScriptException {
        Bindings bindings = compiledScript.getEngine().createBindings();
        for (String scriptFunctionForRemoval : SCRIPT_FUNCTIONS_FOR_REMOVAL) {
            bindings.remove(scriptFunctionForRemoval);
        }
        bindings.put("debug", (Consumer<String>) msg -> api.getLogger().debug(msg));
        return bindings;
    }

    protected boolean test(FlagGameAPI api, Consumer<Bindings> binder) throws ScriptException {
        compileScript();
        Bindings bindings = createBindings(api);
        binder.accept(bindings);
        Object result = compiledScript.eval(bindings);
        return result instanceof Boolean ? (boolean) result : false;
    }

}
