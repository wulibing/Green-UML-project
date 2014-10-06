/*
 * CCVisu is a tool for visual graph clustering
 * and general force-directed graph layout.
 * This file is part of CCVisu. 
 * 
 * Copyright (C) 2005-2007  Dirk Beyer
 * 
 * CCVisu is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * CCVisu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CCVisu; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Please find the GNU Lesser General Public License in file
 * license_lgpl.txt or http://www.gnu.org/licenses/lgpl.txt
 * 
 * Dirk Beyer    (firstname.lastname@sfu.ca)
 * Simon Fraser University (SFU), B.C., Canada
 */

package ccvisu;

import java.awt.Color;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

/*****************************************************************
 * Writer for layouts in VRML format.
 * @version  $Revision: 1.1 $; $Date: 2011/10/20 16:56:35 $
 * @author   Dirk Beyer
 *****************************************************************/
public class WriterDataGraphicsVRML extends WriterDataGraphics {

  private PrintWriter out;
  private float scalePos;

  /**
   * Constructor.
   * @param graph       Graph representation, contains the positions of the vertices.
   * @param out         Output stream writer.
   * @param minVert     Diameter of the smallest vertex.
   * @param fontSize    Font size of vertex annotations.
   * @param backColor   Background color.
   * @param blackCircle If true, draw black circle around each vertex.
   * @param showEdges   If true, draw the edges between the vertices (if possible).
   * @param scalePos    Scaling factor for the layout to adjust to drawing area.
   */
  public WriterDataGraphicsVRML(GraphData graph, 
                                PrintWriter out,
                                float minVert,
                                int fontSize,
                                Color backColor,
                                boolean blackCircle,
                                boolean showEdges,
                                boolean openURL,
                                float scalePos) {
	super(graph, minVert, fontSize, backColor, blackCircle, showEdges, openURL);
    this.out = out;
	this.scalePos = scalePos;
  }

  /*****************************************************************
   * Writes the layout in graphics format VRML.
   *****************************************************************/
  public void write() {
	int size = (int) (1000 * scalePos);

    // Header.
    out.print(
          "#VRML V2.0 utf8 " + endl
		+ "# " + endl 
	    + "# Generated by CCVisu, a tool for visual graph clustering " + endl 
	    + "#   and general force-directed graph layout. " + endl
	    + "#   " + DateFormat.getDateTimeInstance().format(new Date()) + " " + endl
	    + "  " + endl 
	    + " PROTO TRANSFORM_Sphere [ " + endl
	    + "    field SFVec3f translation 0 0 0 " + endl
	    + "    field SFVec3f scale 1 1 1 " + endl
	    + "    field SFColor diffuseColor 0.5 0.5 0.5 " + endl
	    + "    field SFString name \"\" " + endl
	    + "    field SFVec3f textTranslate 0 0 1 " + endl
	    + "    field SFFloat textSize " + fontSize + endl
	    + " ] { " + endl
	    + "    Transform { " + endl
	    + "        translation IS translation " + endl
	    + "        children [ " + endl
	    + "            Transform { " + endl
	    + "                scale IS scale " + endl
	    + "                children [ " + endl
	    + "                    DEF sensor0 TouchSensor {} " + endl
	    + "                    Shape { " + endl
	    + "                        geometry Sphere { radius 0.5 } " + endl
	    + "                        appearance Appearance { " + endl
	    + "                            material Material { " + endl
	    + "                                ambientIntensity 0.3 " + endl
	    + "                                diffuseColor IS diffuseColor " + endl
	    + "                            } " + endl
	    + "                        } " + endl
	    + "                    } " + endl
	    + "                ] " + endl
	    + "            } " + endl
	    + "            Billboard { " + endl
	    + "                axisOfRotation 0 0 0 " + endl
	    + "                children [ " + endl
	    + "                    Transform { " + endl
	    + "                        translation IS textTranslate " + endl
	    + "                        children [ " + endl
	    + "                            Shape { " + endl
        + "                                geometry DEF label0 Text { string [ \"\" ] fontStyle FontStyle { family \"SANS\" size IS textSize } }" + endl
        + "                                appearance Appearance { " + endl
        + "                                    material Material { " + endl
        + "                                        ambientIntensity 0.3 " + endl
        + "                                        diffuseColor 0 0 0 " + endl
        + "                                    } " + endl
        + "                                } " + endl
        + "                            } " + endl
	    + "                        ] " + endl
	    + "                    } " + endl
	    + "                ] " + endl
	    + "            } " + endl
	    + "            DEF script0 Script { " + endl
	    + "                eventIn SFBool isOver " + endl
	    + "                eventIn SFBool touchTime " + endl
	    + "                eventOut MFString trans " + endl
	    + "                field SFBool labelDisplayed FALSE " + endl
	    + "                field SFString label IS name " + endl
	    + "                url [ \"javascript: " + endl
	    + "                    function isOver(value) { " + endl
	    + "                        if (value) { " + endl
	    + "                            trans[0] = label; " + endl
	    + "                        } else { " + endl
	    + "                            if (!labelDisplayed) trans[0] = ''; " + endl
	    + "                        } " + endl
	    + "                    } " + endl
	    + "                    function touchTime() { " + endl
	    + "                        if (labelDisplayed) { " + endl
	    + "                            trans[0] = ''; " + endl
	    + "                            labelDisplayed = false; " + endl
	    + "                        } else { " + endl
	    + "                            trans[0] = label; " + endl
	    + "                            labelDisplayed = true; " + endl
    );
    
    if(openURL){
        out.println(
           "                            Browser.loadURL(new MFString(label), new MFString('target=DUMMY'));" 
           );
    }
    
    out.print(
	      "                        } " + endl
	    + "                    }\" " + endl
	    + "                ] " + endl
	    + "            } " + endl
	    + "        ] " + endl
	    + "    } " + endl
	    + "    ROUTE sensor0.isOver TO script0.isOver " + endl
	    + "    ROUTE sensor0.touchTime TO script0.touchTime " + endl
	    + "    ROUTE script0.trans TO label0.set_string " + endl 
	    + " } " + endl + endl
        
        + " PROTO TRANSFORM_Cylinder [ " + endl
        + "    field SFVec3f translation 0 0 0 " + endl
        + "    field SFRotation rotation 0 0 0 0" + endl
        + "    field SFFloat height 1 " + endl
        + "    field SFColor diffuseColor 0.5 0.5 0.5 " + endl
        + "    field SFString name \"\" " + endl
        + "    field SFVec3f textTranslate 0 0 1 " + endl
        + "    field SFFloat textSize " + fontSize + endl
        + " ] { " + endl
        + "     Transform { " + endl
        + "         translation IS translation " + endl
        + "         children [ " + endl
        + "         Transform { " + endl
        + "             rotation IS rotation " + endl
        + "             children [ " + endl
        + "                 DEF sensor0 TouchSensor {} " + endl
        + "                 Shape { " + endl
        + "                     geometry Cylinder { " + endl
        + "                         height IS height " + endl
        + "                         radius 0.5  " + endl
        + "                         top FALSE  " + endl
        + "                         side TRUE  " + endl
        + "                         bottom FALSE " + endl
        + "                     } " + endl
        + "                         appearance Appearance { " + endl
        + "                         material Material { " + endl
        + "                             ambientIntensity 0.3 " + endl
        + "                             diffuseColor IS diffuseColor " + endl
        + "                         } " + endl
        + "                     } " + endl
        + "                 } " + endl
        + "             ] "+ endl
        + "         } " + endl
        + "         Billboard { " + endl
        + "            axisOfRotation 0 0 0 " + endl
        + "            children [ " + endl
        + "                Transform { " + endl
        + "                    translation IS textTranslate " + endl
        + "                    children [ " + endl
        + "                        Shape { " + endl
        + "                            geometry DEF label0 Text { string [ \"\" ] fontStyle FontStyle { family \"SANS\" size IS textSize } }" + endl
        + "                            appearance Appearance { " + endl
        + "                                material Material { " + endl
        + "                                    ambientIntensity 0.3 " + endl
        + "                                    diffuseColor 0 0 0 " + endl
        + "                                } " + endl
        + "                            } " + endl
        + "                        } " + endl
        + "                    ] " + endl
        + "                } " + endl
        + "            ] " + endl
        + "         } " + endl
        + "         DEF script0 Script { " + endl
        + "             eventIn SFBool isOver " + endl
        + "             eventIn SFBool touchTime " + endl
        + "             eventOut MFString trans " + endl
        + "             field SFBool labelDisplayed FALSE " + endl
        + "             field SFString label IS name " + endl
        + "             url [ \"javascript: " + endl
        + "                 function isOver(value) { " + endl
        + "                     if (value) { " + endl
        + "                         trans[0] = label; " + endl
        + "                     } else { " + endl
        + "                         if (!labelDisplayed) trans[0] = ''; " + endl
        + "                     } " + endl
        + "                 } " + endl
        + "                 function touchTime() { " + endl
        + "                     if (labelDisplayed) { " + endl
        + "                         trans[0] = ''; " + endl
        + "                         labelDisplayed = false; " + endl
        + "                     } else { " + endl
        + "                         trans[0] = label; " + endl
        + "                         labelDisplayed = true; " + endl
        + "                     } " + endl
        + "                 }\" " + endl
        + "             ] " + endl
        + "          } " + endl
        + "       ] "+ endl
        + "    } " + endl
        + "    ROUTE sensor0.isOver TO script0.isOver " + endl
        + "    ROUTE sensor0.touchTime TO script0.touchTime " + endl
        + "    ROUTE script0.trans TO label0.set_string " + endl 
        + " } " + endl+ endl
        
        + "Background { skyColor .8 .8 .8 }" + endl
    );

    // Body.
    writeGraphicsLayout(size);
  }
  
  /*****************************************************************
   * Write graphics layout.
   * @param size  Size of output area (e.g., number of pixel).
   *****************************************************************/
  public void writeGraphicsLayout(int size) {
    float xPosMin = 1000000;
    float xPosMax = -1000000;
    float yPosMin = 1000000;
    float yPosMax = -1000000;
    float zPosMin = 1000000;
    float zPosMax = -1000000;
    for (int i = 0; i < graph.vertices.size(); ++i) {
      if ( graph.vertices.get(i).showVertex ) {
        xPosMin = Math.min(xPosMin, graph.pos[i][0]);
        xPosMax = Math.max(xPosMax, graph.pos[i][0]);
        yPosMin = Math.min(yPosMin, graph.pos[i][1]);
        yPosMax = Math.max(yPosMax, graph.pos[i][1]);
        zPosMin = Math.min(zPosMin, graph.pos[i][2]);
        zPosMax = Math.max(zPosMax, graph.pos[i][2]);
      } 
    }

    float layoutDist;
    layoutDist = Math.max(xPosMax - xPosMin,  yPosMax - yPosMin);
    layoutDist = Math.max(layoutDist,         zPosMax - zPosMin);
    float xOffset = - xPosMin + 0.05f * layoutDist;
    float yOffset = - yPosMin + 0.05f * layoutDist;
    float zOffset = - zPosMin + 0.05f * layoutDist;
    float scale = 0.9f * size / layoutDist;
    
    int x = (int)(((xPosMin+xPosMax)/2 + xOffset) *  scale);
    int y = (int)(((yPosMin+yPosMax)/2 + yOffset) *  -scale + size);
    int z = (int)((zPosMax +
            Math.max((xPosMax-xPosMin),(yPosMax-yPosMin)) * 1.3 
            + zOffset) * scale);
    
    out.print(
          "Viewpoint { " + endl
        + "    position  "+ x +" "+ y +" "+ z  + endl
        + "    orientation  0  0  1  0 " + endl
        + "    description \"origin\" " + endl
        + "} " + endl + endl
    );
    
    // Draw the edges.
    if(showEdges && !graph.edges.isEmpty()){
        int end = graph.edges.size();
        for (int i = 0; i < end; ++i) {
            GraphEdgeInt e = graph.edges.get(i);
            writeEdge( i,
                      (int)((graph.pos[e.x][0]+ xOffset) *  scale),
                      (int)((graph.pos[e.x][1]+ yOffset) * -scale + size),
                      (int)((graph.pos[e.x][2]+ zOffset) *  scale),
                      (int)((graph.pos[e.y][0]+ xOffset) *  scale),
                      (int)((graph.pos[e.y][1]+ yOffset) * -scale + size),
                      (int)((graph.pos[e.y][2]+ zOffset) *  scale));
        }
    }
        
    // Draw the vertices.
    for (int i = 0; i < graph.vertices.size(); ++i) {
      GraphVertex curVertex = graph.vertices.get(i);
      if (!curVertex.showName) {
        if (curVertex.showVertex) {
          int radius  = (int) Math.max(Math.pow(curVertex.degree, 0.5) * minVert, 
                                       minVert);
          int xPos = (int) ((graph.pos[i][0] + xOffset) *  scale);
          int yPos = (int) ((graph.pos[i][1] + yOffset) * -scale + size);
          int zPos = (int) ((graph.pos[i][2] + zOffset) *  scale);
          
          writeVertex(curVertex, xPos, yPos, zPos, radius);
        }
      }
    }

    // Draw the annotated vertices.
    for (int i = 0; i < graph.vertices.size(); ++i) {
      GraphVertex curVertex = graph.vertices.get(i);
      if (curVertex.showName) {
        if (curVertex.showVertex) {
          int dia  = (int) Math.max(Math.pow(curVertex.degree, 0.5) * minVert, 
                                    minVert);
          int xPos = (int) ((graph.pos[i][0] + xOffset) *  scale);
          int yPos = (int) ((graph.pos[i][1] + yOffset) * -scale + size);
          int zPos = (int) ((graph.pos[i][2] + zOffset) *  scale);

          writeVertex(curVertex, xPos, yPos, zPos, dia);
        }
      }
    }

  }

  /**
   * Writes a vertex in VRML format.
   * @param curVertex  The vertex object, to access vertex attributes.
   * @param xPos       x coordinate of the vertex.
   * @param yPos       y coordinate of the vertex.
   * @param zPos       z coordinate of the vertex.
   * @param radius     Radius of the vertex.
   */
  public void writeVertex(GraphVertex curVertex, 
						  int xPos, int yPos, int zPos, int radius) {
    String name = curVertex.name;
    //remove double quote
    if(name.startsWith("\"") && name.endsWith("\"")){
        name = name.substring(1,name.length()-1);
    }
    
	out.print(
           "TRANSFORM_Sphere { translation "
		 +    xPos + " "
		 +    yPos + " "
 		 +    zPos + " "
         + "scale " 
         +   2*radius + " "
         +   2*radius + " "
         +   2*radius + " " 
         + "diffuseColor "
         +   curVertex.color.getRed() + " "
         +   curVertex.color.getGreen() + " "
         +   curVertex.color.getBlue() + " "
         + "name "
         +   "\"" + name + "\" textTranslate "+ 2*radius + " "+ -fontSize/2 +" " + 2*radius + " } " + endl
	);
  }


  /**
   * Writes an edge.
   * @param index       index of the edge in graph.edges
   * @param xPos1       x coordinate of the first point.
   * @param yPos1       y coordinate of the first point.
   * @param zPos1       z coordinate of the first point.
   * @param xPos2       x coordinate of the second point.
   * @param yPos2       y coordinate of the second point.
   * @param zPos2       z coordinate of the second point.
   */
  public void writeEdge(int index, int xPos1, int yPos1, int zPos1,
                               int xPos2, int yPos2, int zPos2){
    
    String edgeName = graph.edges.get(index).relName;
    
    int dx = xPos2-xPos1;
    int dy = yPos2-yPos1;
    int dz = zPos2-zPos1;
    float dist = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
    // It does not make sense to draw an edge of length zero.
    if (dist == 0) {
    	return;
    }
    
    /*
    float rotx = dy; 
    float roty = -dx;
    float rotz = 0.0f;
     */
    float rotx = dz; 
    float roty = 0.0f;
    float rotz = -dx;
    
    float norm = (float)Math.sqrt(rotx*rotx + roty*roty + rotz*rotz);
    float rotw = (float)Math.asin(norm/dist);
    
    if(dy < 0){
        rotw = -rotw;
    }
    
    out.print(
          "TRANSFORM_Cylinder { translation "
        +    (xPos1+xPos2)/2 + " "
        +    (yPos1+yPos2)/2 + " "
        +    (zPos1+zPos2)/2 + " "
        + "rotation " 
        +   rotx + " "
        +   roty + " "
        +   rotz + " "
        +   rotw + " " 
        + "height "
        +   dist + " "
        + "diffuseColor 0 0 0 "
        + "name "
        + "\"" + edgeName + "\" " 
        + "textTranslate 0 0 "+ fontSize +" } " + endl
    );
  }
    
};
