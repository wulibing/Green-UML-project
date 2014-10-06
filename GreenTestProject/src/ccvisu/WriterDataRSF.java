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

import java.io.PrintWriter;

/*****************************************************************
 * Writer for co-change graphs in RSF format.
 * @version  $Revision: 1.1 $; $Date: 2011/10/20 16:56:35 $
 * @author   Dirk Beyer
 *****************************************************************/
public class WriterDataRSF extends WriterData {

  private PrintWriter out;

  public WriterDataRSF(GraphData graph,
                       PrintWriter out) {
    super(graph);
    this.out = out;
  }

  /*****************************************************************
   * Writes the graph data in RSF (relational standard format).
   *****************************************************************/
  public void write() {
    for (int i = 0; i < graph.edges.size(); ++i) {
      GraphEdgeInt e = graph.edges.get(i);
      out.print( e.relName + "\t" );
      out.print( graph.vertices.get(e.x).name + "\t" );
      out.print( graph.vertices.get(e.y).name );
      out.println();
    }
  }

};