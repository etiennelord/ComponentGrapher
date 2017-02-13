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
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import matrix.DataMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Nodes_TableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.permutation_statistics data;
   ArrayList<DescriptiveStatistics> stats=new ArrayList<DescriptiveStatistics>();
   ArrayList<Double> pvalues=new ArrayList<Double>();
   ArrayList<Double> references=new ArrayList<Double>();
           
//3d3)Dans un troisieme tableau « pivots-triplets» : on présente un nombre n de composantes, classées par ordre décroissant des valeurs de %central in triplets. (n fixé par l’utilisateur, par défaut on liste toutes les composantes)
// 
//Composante1 \t valeur de %central in triplets (dans graphe arète type 3) \t  nombre de samples contenant le component 1 \ t liste détaillées des samples concernés.
// 
//Par exemple :
//Œil violet \t 7% \t 3 \t angelina tartanpion hulk
   int selected_index=0;
   
   String[] qualifier={"Nodeid","Name","Reference","<html><i>p</i>-value</html>","Significance","N","Min","Max","Mean","STD","5%","95%"};

    String[] pv2={
                    "in_degree2","out_degree2", "closeness_type3","betweenness_type3","percent_triplet_type3"              
      };
      String[] decription2={
             "Indegree             (type 2 network)",
             "Outdegree            (type 2 network)",
             "Closeness            (type 3 network) ", 
             "Betweenness          (type 3 network)",
             "percent (%) triplets (type 3 network)",             
      };

   public Nodes_TableModel() {
     
   }

   /**
    * This will select the displayed node statistics
    * @param number 
    */
   public void setDisplayedStatistic(int number) {
       selected_index=number;
       calculate_stats();
   }
   
   public void setData(COMPONENT_GRAPHER.permutation_statistics data) {
       this.data=data;
       calculate_stats();
   }
   
   void calculate_stats() {
       stats.clear();
       pvalues.clear();
       references.clear();
       String identifier=pv2[selected_index];
         for (int nodeid=0; nodeid<data.reference_data.nodes.size();nodeid++) {
              node n=data.reference.data.nodes.get(nodeid);
              double ref=0;
              if (identifier.equals("in_degree2")||identifier.equals("out_degree2")) {
                        ref=data.reference.data.nodes.get(nodeid).stats.getInt(identifier);
                    } else {             
                       ref=data.reference.data.nodes.get(nodeid).stats.getFloat(identifier);
                     }
                     references.add(ref);
                     //--This is the array of values for this node
                     double[] values=new double[data.replicates.size()];
                    for (int i=0; i<data.replicates.size();i++) {
                        node nr=data.replicates.get(i).data.nodes.get(nodeid);
                        //System.out.println(nr.stats);
                        values[i]=nr.stats.getFloat(identifier);
                        if (identifier.equals("in_degree2")||identifier.equals("out_degree2")) {
                        values[i]=nr.stats.getInt(identifier);
                        }         
                        //System.out.println(this.data.replicates.get(i).data.nodes.get(nodeid));
                    }            
                        DescriptiveStatistics node_stat=new DescriptiveStatistics(values);                        
                    Double[] st;
                     if (identifier.equals("closeness_type3")) {
                         st=permutation_statistics.getPvalue_bilateral(values, ref,data.reference.data.nodes.size());
                     } else {
                         st=permutation_statistics.getPvalue_unilateral(values, ref,data.reference.data.nodes.size());
                     }                    
                     double pvalue=0.0;
                     if (!identifier.equals("closeness_type3")) {
                        pvalue=st[0];
                     } else {
                        pvalue=st[1]; //P-value (P1)                          
                     }
                     pvalues.add(pvalue);
                     stats.add(node_stat);
                     //--Add information for                    
                } //--End nodes
   }

   @Override
    public int getRowCount() {
        int number=0;
        try {
           number=data.reference_data.nodes.size();
        } catch(Exception e) {}
        //if (number<0) number=0;
        return number;
    }

   @Override
    public int getColumnCount() {       
        //if (qualifier!=null) return qualifier.length;
        int rcount=0;
        try {
           rcount=data.replicates.size();
        } catch(Exception e) {
        //e.printStackTrace();
        }
        //System.out.println(rcount);
        return qualifier.length+rcount;
    }
    

 public String getSignificance(double value, double reference) {
         if (data.replicate<100) return " ";         
         if (value<=0.0||reference<=0.0) return " ";
         if (value>0.05) return " ";
         if (value<data.reference_data.p001) return "***";
         if (value<data.reference_data.p01) return "**";
         if (value<data.reference_data.p05) return "*";
         return " ";
     } 

   @Override
    public Object getValueAt(int row, int col) {
        // We need to get to the displayed sequence
        if (data==null) return "";
        try {
           node n=data.reference_data.nodes.get(row);
           double pvalue=pvalues.get(row);
           double reference=references.get(row);
           DescriptiveStatistics stat=stats.get(row);
           switch(col) {
               case 0: return n.id;
               case 1: return n.complete_name;
               case 2: return reference;
               case 3: return (reference<=0?"NA":pvalue);
               case 4: return getSignificance(pvalue, reference); //--significance
               case 5: return stat.getN();
               case 6: return stat.getMin();
               case 7: return stat.getMax();
               case 8: return stat.getMean();
               case 9: return stat.getStandardDeviation();
               case 10: return stat.getPercentile(5);
               case 11: return stat.getPercentile(95);                                      
               default: return stat.getElement(col-12);    
           }
           
       } catch(Exception e) {
           //e.printStackTrace();
           //System.out.println(row+ " "+col+ " ");           
           return 0;
       }

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
        if (c<qualifier.length) return qualifier[c];
         return "Randomization "+(c-11);        
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         return false;
    }


}
