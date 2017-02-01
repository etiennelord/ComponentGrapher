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
import matrix.DataMatrix;
import java.awt.Component;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import matrix.DataMatrix;

public class Summary_TableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.summary_statistics data;

   String[] qualifier={
   "<html>&nbsp;&nbsp;<b>Nodeid</b></html>",
   "<html>&nbsp;&nbsp;<b>Name</b></html>",
   "<html>&nbsp;&nbsp;<b>found_in_type_1</b></html>",
    "<html>&nbsp;&nbsp;<b>found_in_type_2</b></html>",
    "<html>&nbsp;&nbsp;<b>found_in_type_3</b></html>",
    "<html>&nbsp;&nbsp;<b>found_in_complete</b></html>",
    "<html>&nbsp;&nbsp;<b>column</b></html>",
    "<html>&nbsp;&nbsp;<b>encoded_state</b></html>",
    "<html>&nbsp;&nbsp;<b>char_states</b></html>",
    "<html>&nbsp;&nbsp;<b>CC_type1</b></html>",
    "<html>&nbsp;&nbsp;<b>CC_complete</b></html>",
    "<html>&nbsp;&nbsp;<b>local_ap_type3</b></html>",
    "<html>&nbsp;&nbsp;<b>global_ap_type3</b></html>",
    "<html>&nbsp;&nbsp;<b>local_ap_complete</b></html>",
    "<html>&nbsp;&nbsp;<b>global_ap_complete</b></html>",
    "<html>&nbsp;&nbsp;<b>in_degree_type2</b></html>",
    "<html>&nbsp;&nbsp;<b>norm_indegree_type2</b></html>",
    "<html>&nbsp;&nbsp;<b>betweenness_type3</b></html>",
    "<html>&nbsp;&nbsp;<b>closeness_type3</b></html>",
    "<html>&nbsp;&nbsp;<b>triplet_type3</b></html>",
    "<html>&nbsp;&nbsp;<b>per_triplet_type3</b></html>",
    "<html>&nbsp;&nbsp;<b>triplet_complete</b></html>",
    "<html>&nbsp;&nbsp;<b>per_triplet_complete</b></html>",
    "<html>&nbsp;&nbsp;<b>max_shortest_path_type3</b></html>",
    "<html>&nbsp;&nbsp;<b>max_shortest_path_complete</b></html>",
    "<html>&nbsp;&nbsp;<b>convergence</b></html>",
    "<html>&nbsp;&nbsp;<b>progressive_transition</b></html>",
    "<html>&nbsp;&nbsp;<b>progressive_transition_end_node</b></html>",
    //--Add degree
    
    
    //"<html>&nbsp;&nbsp;<b>contains</b></html>",
   // "<html>&nbsp;&nbsp;<b>percent_contained</b></html>",
    "<html>&nbsp;&nbsp;<b>Taxa</b></html>"
   };
    //pw.println("nodeid\tcontains_taxa\tfound_in_type_1\tfound_in_type_2\tfound_in_type_3\tfound_in_complete\tcolumn\tencoded_state\tchar_states\tCC_type1\tCC_complete\tlocal_ap_type3\tglobal_ap_type3\tlocal_ap_complete\tglobal_ap_complete\tin_degree_type2\tnorm_indegree_type2\tbetweenness_type3\tcloseness_type3\ttriplet_type3\tper_triplet_type3\ttriplet_complete\tper_triplet_complete\tmax_shortest_path_type3\tmax_shortest_path_complete\tconvergence\tprogressive_transition\tprogressive_transition_end_node\tcontains\tpercent_contained\tTaxa");

     @Override
    public Object getValueAt(int row, int col) {
        // We need to get to the displayed sequence     
        try {
           node n=data.data.nodes.get(row);
           switch (col) {
               case 0: return n.id;
               case 1: return " "+n.complete_name;
               case 2: return (n.stats.getBoolean("found_in_type_1")?"x":" ");
               case 3: return (n.stats.getBoolean("found_in_type_2")?"x":" ");
               case 4: return (n.stats.getBoolean("found_in_type_3")?"x":" ");
               case 5: return (n.stats.getBoolean("found_in_complete")?"x":" ");
               case 6: return n.stats.get("column");
               case 7: return n.stats.get("encoded_state");
               case 8: return n.stats.get("char_states");
               case 9: return n.stats.get("CC_type1");
               case 10: return n.stats.get("CC_complete");
               case 11: return (n.stats.getBoolean("local_ap_type3")?"x":" ");
               case 12: return (n.stats.getBoolean("global_ap_type3")?"x":" ");
               case 13: return (n.stats.getBoolean("local_ap_complete")?"x":" ");
               case 14: return (n.stats.getBoolean("global_ap_complete")?"x":" ");
               case 15: return n.stats.get("in_degree2");
               case 16: return n.stats.get("norm_indegree_type2");
              
               case 17: return (n.stats.isSet("betweenness_type3")?n.stats.getFloat("betweenness_type3"):0);
               case 18: return (n.stats.isSet("closeness_type3")?n.stats.getFloat("closeness_type3"):0);
               case 19: return (n.stats.isSet("triplet_type3")?n.stats.getFloat("triplet_type3"):0);
               case 20: return n.stats.get("percent_triplet_type3");
               case 21: return (n.stats.isSet("triplet_complete")?n.stats.getFloat("triplet_complete"):0);
               case 22: return n.stats.get("percent_triplet_complete");
               case 23: return n.stats.getInt("max_shortest_path_type3");
               case 24: return n.stats.getInt("max_shortest_path_complete");
               case 25: return (n.stats.isSet("convergence")?n.stats.getFloat("convergence"):0);
               case 26: return n.stats.get("progressive_transition");
               case 27: return n.stats.get("progressive_transition_end_node");    
               //case 28: return n.stats.get("contains");
              // case 29: return n.stats.get("percent_contained");
               case 28: return n.stats.get("taxa");
               
           }
           
//return data.char_matrix[col][row];
       } catch(Exception e) {
           //e.printStackTrace();
           return 0;
       }
    return 0;
    }

   
   public Summary_TableModel() {
     
   }
  
   
   
   public void setColumnName(String[] qualifier) {
       //this.qualifier=qualifier;
   }

   public void setData(COMPONENT_GRAPHER.summary_statistics data) {
       this.data=data;
   }

    public void addData(DataMatrix data) {
        //this.data.add(data);
   }
    

    
   @Override
    public int getRowCount() {              
       if (data!=null) {
        return data.total_node;        
       } else {
           return 0;
       }        
    }

   @Override
    public int getColumnCount() {       
        if (qualifier!=null) return qualifier.length;
        //return data.ntax;
        return 0;
    }
    

 
    
    
   @Override
     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return Integer.class;
             case 17:return Float.class;
             case 18:return Float.class;
             case 23: return Integer.class;
             case 24: return Integer.class;
             default: return String.class;
         }
    }


    @Override
     public String getColumnName(int c) {
        //return "  "+data.label.get(c); //--Space for better display
        return qualifier[c];
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         return false;
    }

        
    @Override
    public void setValueAt(Object value, int row, int col) {

        //data.char_matrix[col][row]=(String)value;
       
    }


}
