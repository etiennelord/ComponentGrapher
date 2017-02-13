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


import COMPONENT_GRAPHER.permutation_statistics.stats;
import javax.swing.table.*;
import java.util.ArrayList;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class PermutationStatistics_TableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.permutation_statistics data;
   ArrayList<stats> datas=new ArrayList<stats>();
   
   
   String[] qualifier={"Statistics","Reference","<html><i>p</i>-value</html>","Significance","Mean","STD","Min","Max","5%","95%"};
    //pw.println("nodeid\tcontains_taxa\tfound_in_type_1\tfound_in_type_2\tfound_in_type_3\tfound_in_complete\tcolumn\tencoded_state\tchar_states\tCC_type1\tCC_complete\tlocal_ap_type3\tglobal_ap_type3\tlocal_ap_complete\tglobal_ap_complete\tin_degree_type2\tnorm_indegree_type2\tbetweenness_type3\tcloseness_type3\ttriplet_type3\tper_triplet_type3\ttriplet_complete\tper_triplet_complete\tmax_shortest_path_type3\tmax_shortest_path_complete\tconvergence\tprogressive_transition\tprogressive_transition_end_node\tcontains\tpercent_contained\tTaxa");
   
   public void setData(COMPONENT_GRAPHER.permutation_statistics data) {
       this.data=data;
       this.datas=data.calculate_stat();
        //--Calculate the different statistics
   }
   
     @Override
    public Object getValueAt(int row, int col) {
        // We need to get to the displayed sequence     
        try {
           
           stats s=datas.get(row);
           double pvalue=Math.min(s.pvalue[0],s.pvalue[1]);
           switch (col) {
               case 0: return s.title;
               case 1: return s.reference_value;
               case 2: return (s.reference_value==0?"NA":pvalue); //P1
               case 3: return getSignificance(pvalue, s.reference_value);
               //case 3: return ; //P2
               case 4: return s.stat.getMean();
               case 5: return s.stat.getStandardDeviation();
               case 6: return s.stat.getMin();
               case 7: return s.stat.getMax();
               case 8: return s.stat.getPercentile(5);
               case 9: return s.stat.getPercentile(95);    
               default: return s.stat.getElement(col-10);
           }
           
//return data.char_matrix[col][row];
       } catch(Exception e) {
           e.printStackTrace();
           return 0;
       }         
    //return 0;
    }
    
    

      public String getSignificance(double value, double reference) {          
          if (data.replicate<100) return " ";
          if (reference==0) return " ";
          if (value<=0.0) return " ";
         if (value>0.05) return " ";
         if (value<data.reference_data.p001) return "***";
         if (value<data.reference_data.p01) return "**";
         if (value<data.reference_data.p05) return "*";
         return "";
     }
    
   @Override
    public int getRowCount() {              
       if (datas!=null) {
        return  datas.size();
       } else {
           return 0;
       }        
    }

   @Override
    public int getColumnCount() {       
        if (qualifier!=null&&data!=null) return qualifier.length+data.replicates.size();
        if (qualifier!=null) return qualifier.length;        
//return data.ntax;
        return 0;
    }
    

 
    
    
   @Override
     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return String.class;
             case 3: return String.class;
             case 2: return String.class;
             default: return Float.class;
         }
    }


    @Override
     public String getColumnName(int c) {
        //return "  "+data.label.get(c); //--Space for better display
        if (c<qualifier.length) return qualifier[c];
        return "Randomization "+(c-qualifier.length);
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
