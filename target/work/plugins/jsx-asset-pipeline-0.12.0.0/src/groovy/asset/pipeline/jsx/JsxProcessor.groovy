/*
 * Copyright Balsamiq Studios, Inc.  All rights reserved.  http://www.balsamiq.com
 *
 */

package asset.pipeline.jsx

import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.NativeObject
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.commonjs.module.Require
import org.mozilla.javascript.commonjs.module.RequireBuilder
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider
import org.springframework.core.io.ClassPathResource

// Uses Mozilla Rhino to compile the JSX template
// using existing javascript in-browser compiler
// Code here adapted from https://gist.github.com/mingfang/3784a0a6e58c24dda687
// and https://github.com/bertramdev/coffee-grails-asset-pipeline
class JsxProcessor {
  Scriptable globalScope
  ClassLoader classLoader
  Scriptable exports
  Function transform

  JsxProcessor(precompiler=false){
    try {
      classLoader = getClass().getClassLoader()

      def jsxTransformerResource = new ClassPathResource('asset/pipeline/jsx/JSXTransformer.js', classLoader)
      assert jsxTransformerResource.exists() : "JSXTransformer resource not found"

      Context cx = Context.enter()
      cx.setOptimizationLevel(-1)
      globalScope = cx.initStandardObjects()
      RequireBuilder builder = new RequireBuilder()
      builder.moduleScriptProvider = new SoftCachingModuleScriptProvider(new UrlModuleSourceProvider(
          [jsxTransformerResource.file.parentFile.toURI()],
          null
      ))
      Require require = builder.createRequire(cx, globalScope)
      exports = require.requireMain(cx, "JSXTransformer.js")
      transform = exports.get("transform", globalScope)
    } catch (Exception e) {
      throw new Exception("JXTransformer initialization failed.", e)
    } finally {
      try {
        Context.exit()
      } catch (IllegalStateException e) {}
    }
  }

  def process(input, assetFile) {
    try {
      def cx = Context.enter()
      NativeObject result = transform.call(cx, globalScope, exports, [input].toArray())
      return result.get("code").toString()
    } catch (Exception e) {
      throw new Exception("""
        JSXTransformer compilation of jsx to javascript failed.
        $e
        """)
    } finally {
      Context.exit()
    }
  }
}
