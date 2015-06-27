package grails.react

import asset.pipeline.grails.AssetResourceLocator
import grails.compiler.GrailsCompileStatic
import jdk.nashorn.api.scripting.NashornScriptEngine
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.springframework.core.io.Resource
import grails.converters.JSON

import javax.annotation.PostConstruct
import javax.script.Invocable
import javax.script.ScriptEngineManager

import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.ScriptObjectMirror


class ReactTagLib implements GrailsApplicationAware {
    static final namespace = "react"
    static final defaultEncodeAs = [taglib: "none"]

    AssetResourceLocator assetResourceLocator
    GrailsApplication grailsApplication
    NashornScriptEngine engine

    @PostConstruct
    void initialize() {
        try {
            engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn")
            engine.eval(loadScript("application.js"))
        }
        catch (ScriptException e) {
            throw new IllegalStateException("could not init nashorn", e);
        }
    }

    /**
     * Render react in server side
     *
     * @attr require REQUIRED javascript file
     * @attr function REQUIRED javascript function name
     * @attr model the model object
     */
    def render = {attrs ->
		if (!attrs.require) throwTagError("'require' attribute is required")
		if (!attrs.function) throwTagError("'function' attribute is required")
        try {
            engine.eval(loadScript(attrs.require + ".js"))
            Object html = engine.invokeFunction(attrs.function, attrs.model? attrs.model : null);
            out << String.valueOf(html)
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to render react component", e);
        }
    }
	
	/**
	 * Simple helper for rendering JSON model as string
	 *
	 * @attr model REQUIRED the model object
	 */
	def renderModelAsString = {attrs ->
		if (!attrs.model) throwTagError("'model' attribute is required")
		try {
			String toString =  new JSON(attrs.model).toString(true)
			out << toString
		} catch (Exception e) {
			throw new IllegalStateException("failed to render model", e);
		}
	}


    @GrailsCompileStatic
    private Reader loadScript(String uri) {
        Resource resource = assetResourceLocator.findResourceForURI(uri)
        return new InputStreamReader(resource.getInputStream());
    }

}
