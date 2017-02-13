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
import COMPONENT_GRAPHER.util;
import javax.swing.table.*;
import java.util.ArrayList;

public class R2_TableModel extends AbstractTableModel {
   
   public COMPONENT_GRAPHER.permutation_statistics data;
   ArrayList<permutation_statistics.stats> datas=new ArrayList<permutation_statistics.stats>();
    //--Most stable
//Component1 + valeur de in degree de type 2+ nombre de samples contenant le component 1 + liste détaillées des samples concernés)
//Par exemple :
//Bras rouge \t 147 \t 2\t mickey minnie
   
   String[] pv={
                    "total_CC_type1",
                    "total_edges_type3",
                    "total_ap_global_complete", 
                    "total_ap_local_complete",
                    "total_ap_local_type3",
                     "triplet_type3", 
                     "convergence",                   
      };
      String[] decription={
             "nb de complexes (CC type 1)",
                    "nb d’aretes (type 3)",
                    "nb de points d'articulations globaux", 
                    "nb de points d’articulations locaux",
                    " nb de points d’articulations locaux (type 3)",
                     "nb de triplets non transitifs (type 3)", 
                     "proportion de cycles de taille 4 (type3)",        
          
      };
   
   String[] qualifier={"Nodeid","Name","in-degree (type 2)","Out-degree (type 2)","Closeness","Betweenness","Centrality (% type 3)"};

   public R2_TableModel() {
     
   }
   
   public void setData(COMPONENT_GRAPHER.permutation_statistics data) {
       this.data=data;
        //calculate_stat();
        //--Calculate the different statistics
   }
   
   private void calculate_stat() {
//       datas.clear();
//       for (int p=0; p<pv.length;p++) {
//       
//           String node_field=pvalue_fields[p];
//           
//           s.node_field=pvalue_fields[p];
//           s.title=pvalue_fields_description[p];
//            double[] values=new double[data.replicates.size()];
//            double refvalue=0.0f;            
//         
//            for (int i=0; i<data.replicates.size();i++) {
//            
//            
//            switch (s.node_field) {
//                case "total_CC_type1": values[i]=data.replicates.get(i).total_CC_type1; refvalue=data.reference.total_CC_type1; break;
//               case "total_CC_type3": values[i]=data.replicates.get(i).total_CC_type3; refvalue=data.reference.total_CC_type3; break;
//                case "total_CC_complete": values[i]=data.replicates.get(i).total_CC_complete; refvalue=data.reference.total_CC_complete;break;
//                case "total_ap_global_complete": values[i]=data.replicates.get(i).total_ap_global_complete; refvalue=data.reference.total_ap_global_complete; break;
//                case "total_edges_type1": values[i]=data.replicates.get(i).total_edges_type1; refvalue=data.reference.total_edges_type1;break;
//                case "total_edges_type2": values[i]=data.replicates.get(i).total_edges_type2; refvalue=data.reference.total_edges_type2;break;
//                case "total_edges_type3": values[i]=data.replicates.get(i).total_edges_type3; refvalue=data.reference.total_edges_type3;break;
//                case "total_edges_complete": values[i]=data.replicates.get(i).total_edges_complete; refvalue=data.reference.total_edges_complete;break;
//                case "total_ap_global_type3": values[i]=data.replicates.get(i).total_ap_global_type3; refvalue=data.reference.total_ap_global_type3;break;
//                case "total_ap_local_type3": values[i]=data.replicates.get(i).total_ap_local_type3; refvalue=data.reference.total_ap_local_type3;break;
//                case "total_ap_local_complete": values[i]=data.replicates.get(i).total_ap_local_complete; refvalue=data.reference.total_ap_local_complete;break;
//                case "triplet_type3": values[i]=data.replicates.get(i).triplet_type3; refvalue=data.reference.triplet_type3;break;
//                case "convergence": values[i]=data.replicates.get(i).convergence; refvalue=data.reference.convergence;break;
//                case "per_loop4_type3": values[i]=data.replicates.get(i).per_loop4_type3; refvalue=data.reference.per_loop4_type3;break;
//                case "per_len4_type3": values[i]=data.replicates.get(i).per_len4_type3; refvalue=data.reference.per_len4_type3;break;                        
//            }            
//           //System.out.println(node_field+" "+i+" :"+values[i]);
//            s.stat.addValue(values[i]);
//        }            
//         s.stat.addValue(refvalue);        
//        s.reference_value=refvalue;
//        s.pvalue=data.getPvalue1(values, refvalue);
//        datas.add(s);
////        st.append(node_field+":\n");
////        st.append("Min     : "+stats.getMin()+"\n");
////        st.append("Max     : "+stats.getMax()+"\n");
////        st.append("Mean    : "+stats.getMean()+"\n");
////        st.append("5%      : "+stats.getPercentile(5)+"\n");
////        st.append("95%     : "+stats.getPercentile(95)+"\n");
////        st.append("Ref     : "+refvalue+"\n");
////        st.append("P-value : "+getPvalue1(values,refvalue)+"\n");
////        st.append("Histogram:\n");
////        st.append(hist(values, TOTALBINS)+"\n");
////        st.append("Values:\n");       
//       }
       
   }
  
   public Double getPvalue(String identifier, int nodeid, boolean bilateral) {
       
       double ref=this.data.reference.data.nodes.get(nodeid).stats.getFloat(identifier);
       double[] values=new double[this.data.replicates.size()];
       for (int i=0; i<this.data.replicates.size();i++) {
           values[i]=this.data.replicates.get(i).data.nodes.get(nodeid).stats.getFloat(identifier);
       }       
       if (!bilateral) {
           return permutation_statistics.getPvalue_unilateral(values, ref,this.data.reference.data.nodes.size())[0];
       }   else {
           return permutation_statistics.getPvalue_bilateral(values, ref,this.data.reference.data.nodes.size())[0];
       }  
   }
    
 
   @Override
    public int getRowCount() {
        //int number=data.size();        
        if (data==null) return 0;
        int number=data.reference_data.nodes.size();
        //if (number<0) number=0;
        return number;
    }

   @Override
    public int getColumnCount() {       
        //if (qualifier!=null) return qualifier.length;
        return qualifier.length;
    }
    

   @Override
    public Object getValueAt(int row, int col) {
       node n=data.reference_data.nodes.get(row);       
       switch (col) {
           case 0: return " "+n.id; 
           case 1: return " "+n.complete_name;
           case 2: return this.getPvalue("in_degree2", row, false);
           case 3: return this.getPvalue("out_degree2", row, false);
           case 4: return this.getPvalue("closeness_type3",row,true);
           case 5: return this.getPvalue("betweenness_type3",row,false);
           case 6: return this.getPvalue("percent_triplet_type3",row,false);        
               
       }
        
       return 0;
    }

    /**
     * 
     */
    public void ExportResult() {
        util u=new util();
        u.open("output.html");
        u.println("<html><head></head><body>");
        String[] sta={"in_degree2","out_degree2", "closeness_type3","betweenness_type3","percent_triplet_type3"};
        for (String identifier:sta) {
            u.println("<h2>"+identifier+"</h2><br>");
            u.println("<table border=1>");
            u.println("<thead><th>NodeID</th><th>Name</th><th>Reference</th>");
            if (identifier.equals("closeness_type3")) {
                u.println("<th>P1</th><th>P2</th><th>Others values</th></thead><tbody>");
            } else {
                u.println("<th>p-value</th><th>Others values</th></thead><tbody>");
            }
            for (int nodeid=0; nodeid<data.reference_data.nodes.size();nodeid++) {
             node n=data.reference.data.nodes.get(nodeid);
             u.println("<tr>");
             u.println("<td>"+n.id+"</td><td>"+n.complete_name+"</td>");
             double ref=0;
             if (identifier.equals("in_degree2")||identifier.equals("out_degree2")) {
                ref=this.data.reference.data.nodes.get(nodeid).stats.getInt(identifier);
            } else {             
               ref=this.data.reference.data.nodes.get(nodeid).stats.getFloat(identifier);
             }
              double[] values=new double[this.data.replicates.size()];
            for (int i=0; i<this.data.replicates.size();i++) {
                node nr=this.data.replicates.get(i).data.nodes.get(nodeid);
                //System.out.println(nr.stats);
                values[i]=nr.stats.getFloat(identifier);
                if (identifier.equals("in_degree2")||identifier.equals("out_degree2")) {
                values[i]=nr.stats.getInt(identifier);
                }         
                //System.out.println(this.data.replicates.get(i).data.nodes.get(nodeid));
            }            
            Double[] st;
             if (identifier.equals("closeness_type3")) {
                 st=permutation_statistics.getPvalue_bilateral(values, ref,this.data.reference.data.nodes.size());
             } else {
                 st=permutation_statistics.getPvalue_unilateral(values, ref,this.data.reference.data.nodes.size());
             }
             u.println("<td>"+ref+"</td>"); //--Reference value
           
             if (!identifier.equals("closeness_type3")) {
                 u.println("<td>"+st[0]+"</td>"); //P-value
             } else {
                   u.println("<td>"+st[1]+"</td>"); //P-value
                   u.println("<td>"+st[2]+"</td>"); //P-value
             }
             for (int j=0; j<values.length;j++) u.println("<td>"+values[j]+"</td>");             
             u.println("</tr>");
            }            
            u.println("</tbody></table>");
        }
        u.println("</body></html>");
        u.close();
    }
    
    
   @Override
     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return String.class;
             case 1: return String.class;
             case 2: return Float.class;       
             case 3: return Double.class;  
                 case 4: return Double.class;  
                     case 5: return Double.class;  
                         case 6: return Double.class;  
                 
//            case 4: return Float.class;  
//            case 5: return Float.class;  
//            case 6: return Float.class;  
             default: return String.class;
         }
    }


    @Override
     public String getColumnName(int c) {
        return "  "+qualifier[c]; //--Space for better display
        
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         return false;
    }


}
