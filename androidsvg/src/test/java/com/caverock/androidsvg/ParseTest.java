/*
   Copyright 2017 Paul LeBeau, Cave Rock Software Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.caverock.androidsvg;

import android.graphics.Path;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Config(manifest=Config.NONE, sdk = Build.VERSION_CODES.JELLY_BEAN, shadows={MockPath.class})
@RunWith(RobolectricTestRunner.class)
public class ParseTest
{
   @Test
   public void emptySVG() throws SVGParseException
   {
      // XmlPullParser
      String  test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                     "</svg>";
      SVG  svg = SVG.getFromString(test);
      assertNotNull(svg.getRootElement());
   }

   @Test
   public void emptySVGEntitiesEnabled() throws SVGParseException
   {
      // NOTE: Is *really* slow when running under JUnit (15-20secs).
      // However, the speed seems to be okay under normal usage (a real app).
      String test = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\" [" +
             "  <!ENTITY hello \"Hello World!\">" +
             "]>" +
             "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
             "</svg>";
      SVG svg = SVG.getFromString(test);
      assertNotNull(svg.getRootElement());
   }

   @Test
   public void emptySVGEntitiesDisabled() throws SVGParseException
   {
      String test = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\" [" +
             "  <!ENTITY hello \"Hello World!\">" +
             "]>" +
             "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
             "</svg>";
      SVG.setInternalEntitiesEnabled(false);
      SVG svg = SVG.getFromString(test);
      assertNotNull(svg.getRootElement());
   }

   @Test (expected = SVGParseException.class)
   public void unbalancedClose() throws SVGParseException
   {
      String  test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                     "</svg>" +
                     "</svg>";
      SVG  svg = SVG.getFromString(test);
   }


   @Test
   public void parsePath() throws SVGParseException
   {
      String  test = "M100,200 C100,100 250,100 250,200 S400,300 400,200";
      Path  path = SVG.parsePath(test);
      assertEquals("M 100 200 C 100 100 250 100 250 200 C 250 300 400 300 400 200", ((MockPath) Shadow.extract(path)).getPathDescription());

      // The arcs in a path get converted to cubic beziers
      test = "M-100 0 A 100 100 0 0 0 0,100";
      path = SVG.parsePath(test);
      assertEquals("M -100 0 C -100 55.22848 -55.22848 100 0 100", ((MockPath) Shadow.extract(path)).getPathDescription());

      // Path with errors
      test = "M 0 0 L 100 100 C 200 200 Z";
      path = SVG.parsePath(test);
      assertEquals("M 0 0 L 100 100", ((MockPath) Shadow.extract(path)).getPathDescription());
   }


/*
   @Test
   public void issue177() throws SVGParseException
   {
      String  test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                     "  <defs></defs>" +
                     "  <g></g>" +
                     "  <a></a>" +
                     "  <use></use>" +
                     "  <image></image>" +
                     "  <text>" +
                     "    <tspan></tspan>" +
                     "    <textPath></textPath>" +
                     "  </text>" +
                     "  <switch></switch>" +
                     "  <symbol></symbol>" +
                     "  <marker></marker>" +
                     "  <linearGradient>" +
                     "    <stop></stop>" +
                     "  </linearGradient>" +
                     "  <radialGradient></radialGradient>" +
                     "  <clipPath></clipPath>" +
                     "  <pattern></pattern>" +
                     "  <view></view>" +
                     "  <mask></mask>" +
                     "  <solidColor></solidColor>" +
                     "  <g>" +
                     "    <path>" +
                     "      <style media=\"print\">" +
                     "      </style>" +
                     "    </path>" +
                     "  </g>" +
                     "</svg>";

      try {
         SVG  svg = SVG.getFromString(test);
         fail("Should have thrown ParseException");
      } catch (SVGParseException e) {
         // passed!
      }
   }
*/

}
