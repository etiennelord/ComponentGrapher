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
import javax.swing.table.*;


public class Summary_TableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.summary_statistics data;

   //--Column descriptions
   String[] qualifier_strings={
   "<html>&nbsp;&nbsp;<b>Nodeid</b></html>",
   "<html>&nbsp;&nbsp;<b>Name</b></html>",
   "<html>&nbsp;&nbsp;<b>Found in type 1 network</b></html>",
    "<html>&nbsp;&nbsp;<b>Found in type 2 network</b></html>",
    "<html>&nbsp;&nbsp;<b>Found in type 3 network</b></html>",
    "<html>&nbsp;&nbsp;<b>Found in complete network</b></html>",
    "<html>&nbsp;&nbsp;<b>Column</b></html>",
    "<html>&nbsp;&nbsp;<b>Encoded state</b></html>",
    "<html>&nbsp;&nbsp;<b>Character</b></html>",
    "<html>&nbsp;&nbsp;<b>Connected component type1</b></html>",    
    "<html>&nbsp;&nbsp;<b>Connected component complete</b></html>",    
    "<html>&nbsp;&nbsp;<b>Local articulation point type3</b></html>",
    "<html>&nbsp;&nbsp;<b>Global articulation point type3</b></html>",
    "<html>&nbsp;&nbsp;<b>Local articulation point complete</b></html>",
    "<html>&nbsp;&nbsp;<b>Global articulation point complete</b></html>",
    "<html>&nbsp;&nbsp;<b>Indegree type2</b></html>",
    "<html>&nbsp;&nbsp;<b>Normalized indegree type2</b></html>",
    "<html>&nbsp;&nbsp;<b>Betweenness type3</b></html>",
    "<html>&nbsp;&nbsp;<b>Closeness type3</b></html>",
    "<html>&nbsp;&nbsp;<b>Numbers of triplet type3 </b></html>",
    "<html>&nbsp;&nbsp;<b>Percent(%) triplet type3</b></html>",   
    "<html>&nbsp;&nbsp;<b>Convergence</b></html>",    
    "<html>&nbsp;&nbsp;<b>Number of taxa</b></html>",    
    "<html>&nbsp;&nbsp;<b>Taxa</b></html>",
     "<html>&nbsp;&nbsp;<b>Triplet type A</b></html>",
     "<html>&nbsp;&nbsp;<b>Triplet type B</b></html>",
     "<html>&nbsp;&nbsp;<b>Triplet type C</b></html>",
     "<html>&nbsp;&nbsp;<b>Triplet type D</b></html>"
   };
    

     @Override
    public Object getValueAt(int row, int col) {
        // We need to get to the displayed sequence     
        try {
           node n=data.data.nodes.get(row);
           switch (col) {
               case 0: return n.id;
               case 1: return " "+n.complete_name;
               case 2: return (n.stats.getBoolean("found_in_type_1")?"x":"-");
               case 3: return (n.stats.getBoolean("found_in_type_2")?"x":"-");
               case 4: return (n.stats.getBoolean("found_in_type_3")?"x":"-");
               case 5: return (n.stats.getBoolean("found_in_complete")?"x":"-");
               case 6: return n.stats.getInt("column");
               case 7: return n.stats.get("encoded_state");
               case 8: return n.stats.get("char_states");
               case 9: return (n.stats.get("CC_type1").equals("0")?"-":n.stats.getStringInt("CC_type1"));
               case 10: return (n.stats.get("CC_complete").equals("0")?"-":n.stats.getStringInt("CC_complete"));
               case 11: return (n.stats.getBoolean("local_ap_type3")?"x":"-");
               case 12: return (n.stats.getBoolean("global_ap_type3")?"x":"-");
               case 13: return (n.stats.getBoolean("local_ap_complete")?"x":"-");
               case 14: return (n.stats.getBoolean("global_ap_complete")?"x":"-");
               case 15: return n.stats.getInt("in_degree2");
               case 16: return n.stats.get("norm_indegree_type2");             
               case 17: return (n.stats.isSet("betweenness_type3")?n.stats.getFloat("betweenness_type3"):0.0f);
               case 18: return (n.stats.isSet("closeness_type3")?n.stats.getFloat("closeness_type3"):0.0f);
               case 19: return (n.stats.isSet("triplet_type3")?n.stats.getFloat("triplet_type3"):0.0f);
               case 20: return n.stats.getFloat("percent_triplet_type3");                        
               case 21: return (n.stats.isSet("convergence")?n.stats.getFloat("convergence"):0.0f);
               case 22: return (n.stats.isSet("taxa_count")?n.stats.getInt("taxa_count"):0);
               case 23: return n.stats.get("taxa");      
               case 24: return (n.stats.isSet("triplet_typeA")?n.stats.get("triplet_typeA"):"-");      
               case 25: return (n.stats.isSet("triplet_typeB")?n.stats.get("triplet_typeA"):"-");
               case 26: return (n.stats.isSet("triplet_typeC")?n.stats.get("triplet_typeA"):"-");    
               case 27: return (n.stats.isSet("triplet_typeD")?n.stats.get("triplet_typeA"):"-");          
           }
           
//return data.char_matrix[col][row];
       } catch(Exception e) {
           //e.printStackTrace();
           return 0;
       }
    return 0;
    }


   public void setData(COMPONENT_GRAPHER.summary_statistics data) {
       this.data=data;
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
        if (qualifier_strings!=null) return qualifier_strings.length;
        //return data.ntax;
        return 0;
    }
    

 
    
    
   @Override
     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return Integer.class;
             case 1: return String.class;
             case 2: return String.class;
             case 3: return String.class;
             case 4: return String.class; 
             case 5: return String.class; 
             case 6: return Integer.class;    
             case 7: return String.class;     
             case 8: return String.class;         
             case 9: return String.class;    
             case 10: return String.class;  
             case 15: return Integer.class;  
             case 17:return Float.class;
             case 18:return Float.class;   
             case 19:return Float.class;   
             case 20:return Float.class;   
             case 21:return Integer.class;   
             case 22:return Float.class;   
             default: return String.class;
         }
    }


    @Override
     public String getColumnName(int c) {        
        return qualifier_strings[c];
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         return false;
    }


}
