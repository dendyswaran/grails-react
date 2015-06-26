/*
 * Copyright Balsamiq Studios, Inc.  All rights reserved.  http://www.balsamiq.com
 *
 */

package asset.pipeline.jsx

import asset.pipeline.AbstractAssetFile

class JsxAssetFile extends AbstractAssetFile {
  static final contentType = ['application/javascript','application/x-javascript','text/javascript']
  static extensions = ['jsx', 'js.jsx']
  static compiledExtension = 'js'
  static processors = [JsxProcessor]

  @Override
  String directiveForLine(String line) {
    line.find(/\/\/=(.*)/) { fullMatch, directive -> return directive }
  }
}
