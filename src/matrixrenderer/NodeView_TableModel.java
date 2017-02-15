package matrixrenderer;

/*
 *  COMPOSITE-GRAPHER v1.0
 *  
 *  Copyright (C) 2016-2017  Etienne Lord
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import COMPONENT_GRAPHER.node;
import COMPONENT_GRAPHER.permutation_statistics;
import COMPONENT_GRAPHER.summary_statistics;
import matrix.DataMatrix;
import java.awt.Component;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import matrix.DataMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Display statistics about a particular node
 * @author Etienne
 */
public class NodeView_TableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.summary_statistics data;
   int nodeid=0;
   String[] qualifier={"Node properties","Value"};
   public node current_node=null;
   ArrayList<String> prop=new ArrayList<String>();
   public NodeView_TableModel() {
     
   }

      
   public void setData(COMPONENT_GRAPHER.summary_statistics data, int nodeid) {
       this.data=data;    
       this.nodeid=nodeid;
        this.prop=new ArrayList<String>();
        this.current_node=null;
       try {
        this.current_node=data.data.nodes.get(nodeid);
       
        for (Object s:current_node.stats.keySet()) prop.add(s.toString());       
        Collections.sort(prop);
       } catch(Exception e) {
           
       }
   }
   

   @Override
    public int getRowCount() {       
        if (data==null) {
            return 0;
        } else {
            try {
                node n=data.data.nodes.get(nodeid);
                if (n!=null) {
                    return n.stats.size();
                }
            } catch(Exception e) {}            
        }
        return 0;
    }

   @Override
    public int getColumnCount() {       
        return qualifier.length;
        
    }
    



   @Override
    public Object getValueAt(int row, int col) {
        // We need to get to the displayed sequence
        if (data==null) return "";
        try {          
           switch(col) {
               case 0: return prop.get(row);
               case 1: return current_node.stats.get(prop.get(row));
           }                      
       } catch(Exception e) {                   
           return 0;
       }
        return "";
    }

    
    
   @Override
     public Class getColumnClass(int c) {
         switch(c) {
             //case 0: return Boolean.class;
             default: return String.class;
         }
    }


    @Override
     public String getColumnName(int c) {
        return qualifier[c];              
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         return false;
    }


}
