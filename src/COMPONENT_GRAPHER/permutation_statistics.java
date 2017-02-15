/*
 *  COMPONENT-GRAPHER v1.0
 *  
 *  Copyright (C) 2015-2016  Etienne Lord
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

package COMPONENT_GRAPHER;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import config.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import umontreal.iro.lecuyer.charts.CustomHistogramDataset;
import umontreal.iro.lecuyer.rng.LFSR258;

/**
 * Implement the permutation or permutation permutation_statistics
 * @author Etienne Lord
 */
public class permutation_statistics implements Serializable {
    
    public static int TOTALBINS=10;
    public int total_permute=100; //--Permutation of matrix
    int seed=0;
    
    public datasets data;         //pointer to the original datasets  current 
    public datasets reference_data;   //the datasets  (reference)  
    
    String original_state_matrix[][]; //--Internal data
    public int replicate=100;
    public int current_replicate=1;
    LFSR258  rand=new LFSR258();            
    transient Callable callback=null;
    transient Config config=new Config();
    
    public summary_statistics reference=null; //--The reference 
    public ArrayList<summary_statistics> replicates=new ArrayList<summary_statistics>(); //--The replicates
    long starttime=0;
    long endtime=0;
    
    String[] pvalue_fields={
                    "total_CC_type1",
                    "total_edges_type3",
                    "total_ap_global_complete", 
                    "total_ap_local_complete",
                    "total_ap_local_type3",
                     "triplet_type3", 
                     "convergence",                   
      };
    
    
      String[] pvalue_fields_description={
              "Number of connected component (type 1 network)",
             "Number of edges (type 3 network)",
             "Number of global articulation points (complete network) ", 
             "Number of local articulation points (complete network)",
             "Number of global articulation points (type 3 network) ",
             "Number of Non-transitive triplet (type 3 network)", 
             "Ratio of cycle of length 4 (type 3 network)",           
          
      };
    
   //--Helper class 
   public class stats {
       public String title="";
       public String node_field="";
       public Double reference_value=0.0;       
       public DescriptiveStatistics stat=new DescriptiveStatistics();    
       public Double[] pvalue;  
       
       public stats() {}       
   }
   
    
//    /**
//     * Note: call after the current_state_matrix is generated in datasets.prepare_current_state_matrix
//     * @param data 
//     */
    public permutation_statistics(datasets data) {
        this.data=data;  //--Pointer to the data      
        this.reference_data=new datasets(data);
        //Clone orignial
        original_state_matrix=new String[data.ntax][data.nchar];
        for (int i=0;i<data.ntax;i++) {
            for (int j=0; j<data.nchar;j++) {
                original_state_matrix[i][j]=data.current_state_matrix[i][j];
            }
        }        
    }
   
    
    public void setCallback(Callable callback_) {
       this.callback=callback_;
    }
      
    public void saveReferenceStatistics() {
        String directory=reference_data.result_directory;
        String serial=directory+File.separator+"reference.json";
        util.CreateDir(directory);             
        // 1. Serialize
        System.out.println(serial);
        reference.serialize(serial);
        
        // 2. Export summary statistics ...
        
        // 3. Save the matrix 
        reference.data.save_CurrentPhylipMatrix(directory+File.separator+"reference.phy", true);
        // 4. Save the summary
        
    }
    
    public void saveReplicateStatistics(int replicate, summary_statistics su) {
         String directory=reference_data.result_directory;
         if (!util.DirExists(directory)) util.CreateDir(directory);       
         String serial=directory+File.separator+"randomization_"+replicate+".json";
         su.serialize(serial);
         reference.data.save_CurrentPhylipMatrix(directory+File.separator+"replicate_"+replicate+".phy", true);
    }
    
    /**
     * Main function to call to generate statistics
     */
    public void generate_statistics() {
       try {
         String directory=reference_data.result_directory;
         if (!util.DirExists(directory)) util.CreateDir(directory);   
        //--Log to the current directory
           final util logfile=new util();
            String log_filename=directory+File.separator+"log.txt";
           logfile.open(log_filename);
           this.replicate=reference_data.replicate;
       // System.out.println(reference_data.result_directory);
       reference_data.compute();
       data.MessageOption(data.get_info());
       data.MessageOption("Saving to "+reference_data.result_directory);
       data.MessageOption("* This is set in the Run analysis menu.");
       logfile.println(data.get_info());
       starttime=System.currentTimeMillis();
       reference=new summary_statistics(reference_data);
        data.MessageResult("Calculating reference");
        data.MessageResult("Analyzing :"+reference.data.filename);        
        //System.out.println("Calculating reference\n");
        logfile.println("Analyzing :"+reference.data.filename);
        reference.calculate_network_statistics();
        //--Save the reference and statistics to results directory
        saveReferenceStatistics();
        data.MessageResult(reference.get_info().toString()); 
        logfile.println(reference.get_info().toString());
         try {
                        if (callback!=null) callback.call();
                         
                    } catch(Exception e) {
                        e.printStackTrace();
                   }
        //--Test if we are doing replicate here
                
        long estimate=System.currentTimeMillis()-starttime;
        System.out.println("Total time for reference :"+util.msToString(estimate));
        logfile.println("Total time for reference :"+util.msToString(estimate));
        data.MessageResult("Total time for reference :"+util.msToString(estimate)+"\n"); 
        if (data.replicate<=1 || !data.permutation) {
            //--No replicate
            logfile.close();
            return;
        }
        System.out.println("Estimatied time for all replicates ("+replicate+"):"+util.msToString((replicate*estimate/4)));      
        logfile.println("Estimatied time for all replicates ("+replicate+"):"+util.msToString((replicate*estimate/4)));
         //long timerunning=System.currentTimeMillis();  //--We will estimate 1%
         //int one_percent=(int)Math.ceil(replicate/100)+1; //Not used
         
         ExecutorService pool=Executors.newFixedThreadPool(datasets.maxthreads);
         final ArrayList<Callable<summary_statistics>>partitions=new ArrayList<Callable<summary_statistics>>();
        if (data.replicate<=1) return;
         for (int i=0; i<replicate;i++) {
             partitions.add(new Callable<summary_statistics>(){
             public summary_statistics call() {
                  final int this_replicate= current_replicate++;
                  final long this_start_time=System.currentTimeMillis();
                   System.out.println(this_replicate+" / "+replicate +" [started]");
                   data.MessageResult(this_replicate+" / "+replicate +" [started]\n");
                   logfile.println(this_replicate+" / "+replicate +" [started]");
//                     try {
//                        if (callback!=null) callback.call();
//                         
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                   }
                   datasets t=new datasets(data);
                   if (data.permutation) {
                       t.generate_permutation();
                   } else if (data.bootstrap) {
                        t.generate_bootstrap();
                   }
                   t.compute();
                   summary_statistics s=new summary_statistics(t);            
                    s.calculate_network_statistics();           
                    saveReplicateStatistics(this_replicate, s);
                    String dt=this_replicate+" / "+replicate +" [done in "+util.msToString(System.currentTimeMillis()-this_start_time)+"]";
                    System.out.println(dt);
                    logfile.println(dt);
                    data.MessageResult(dt+"\n");
                    try {
                        if (callback!=null) callback.call();
                         
                    } catch(Exception e) {
                        e.printStackTrace();
                   }
                    return s;
             }             
            });
         }
         try {
            final List<Future<summary_statistics>> results_partition=pool.invokeAll(partitions);
            pool.shutdown();
            for (final Future<summary_statistics> r:results_partition) {
                replicates.add(r.get());
            }
            
         } catch(Exception e){
             logfile.println("Job was stopped. "+e.getMessage());
            // e.printStackTrace();
         }
         endtime=System.currentTimeMillis();
         System.out.println("Total time: "+util.msToString(endtime-starttime));
         logfile.println("Total time: "+util.msToString(endtime-starttime));
        //--done in the mainframe now...data=reference_data;
       logfile.close();
       } catch(Exception pp) {
           
       }
       
}
    
    public Double[] getPvalue(String node_field, ArrayList<summary_statistics> replicates, summary_statistics reference) {
        StringBuffer st=new StringBuffer();
        double[] values=new double[replicates.size()];
        double refvalue=0.0f;
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i=0; i<replicates.size();i++) {
            
            
            switch (node_field) {
                case "total_CC_type1": values[i]=replicates.get(i).total_CC_type1; refvalue=reference.total_CC_type1; break;
               case "total_CC_type3": values[i]=replicates.get(i).total_CC_type3; refvalue=reference.total_CC_type3; break;
                case "total_CC_complete": values[i]=replicates.get(i).total_CC_complete; refvalue=reference.total_CC_complete;break;
                case "total_ap_global_complete": values[i]=replicates.get(i).total_ap_global_complete; refvalue=reference.total_ap_global_complete; break;
                case "total_edges_type1": values[i]=replicates.get(i).total_edges_type1; refvalue=reference.total_edges_type1;break;
                case "total_edges_type2": values[i]=replicates.get(i).total_edges_type2; refvalue=reference.total_edges_type2;break;
                case "total_edges_type3": values[i]=replicates.get(i).total_edges_type3; refvalue=reference.total_edges_type3;break;
                case "total_edges_complete": values[i]=replicates.get(i).total_edges_complete; refvalue=reference.total_edges_complete;break;
                case "total_ap_global_type3": values[i]=replicates.get(i).total_ap_global_type3; refvalue=reference.total_ap_global_type3;break;
                case "total_ap_local_type3": values[i]=replicates.get(i).total_ap_local_type3; refvalue=reference.total_ap_local_type3;break;
                case "total_ap_local_complete": values[i]=replicates.get(i).total_ap_local_complete; refvalue=reference.total_ap_local_complete;break;
                case "triplet_type3": values[i]=replicates.get(i).triplet_type3; refvalue=reference.triplet_type3;break;
                case "convergence": values[i]=replicates.get(i).convergence; refvalue=reference.convergence;break;
                case "per_loop4_type3": values[i]=replicates.get(i).per_loop4_type3; refvalue=reference.per_loop4_type3;break;
                case "per_len4_type3": values[i]=replicates.get(i).per_len4_type3; refvalue=reference.per_len4_type3;break;                        
            }            
           //System.out.println(node_field+" "+i+" :"+values[i]);
            stats.addValue(values[i]);
        }
        stats.addValue(refvalue);        
        
        st.append(node_field+":\n");
        st.append("Min     : "+stats.getMin()+"\n");
        st.append("Max     : "+stats.getMax()+"\n");
        st.append("Mean    : "+stats.getMean()+"\n");
        st.append("5%      : "+stats.getPercentile(5)+"\n");
        st.append("95%     : "+stats.getPercentile(95)+"\n");
        st.append("Ref     : "+refvalue+"\n");
        st.append("P-value : "+getPvalue1(values,refvalue)+"\n");
        st.append("Histogram:\n");
        st.append(hist(values, TOTALBINS)+"\n");
        st.append("Values:\n");
        for (int i=0; i<values.length;i++) {
            st.append(values[i]+"\t");
        }
        st.append("\n\n");
        
        return getPvalue1(values,refvalue);
    }
    
    public String hist( double[] values, int num_bins) {
        String hh="";
        double[] histogram = new double[num_bins];
        double[] histogram_min = new double[num_bins];
        double[] histogram_max = new double[num_bins];
        org.apache.commons.math3.random.EmpiricalDistribution distribution = new org.apache.commons.math3.random.EmpiricalDistribution(num_bins);
        distribution.load(values);
        
        
        int k = 0;
        for(org.apache.commons.math3.stat.descriptive.SummaryStatistics stats: distribution.getBinStats())
        {                        
            histogram_min[k] = stats.getMin();
            histogram_max[k] = stats.getMax();
            histogram[k++] = stats.getN();            
        }
        //--find number of NaN to ajust the bin size up to nums_bins
        int count=0;
        for (int i=0; i<num_bins;i++) {
            if (Double.isNaN(histogram_min[i])) count++;
        }
        if (count>0) {
            return hist(values,num_bins-count);
        }
        
        hh+="     [";
        for (int i=0;i<num_bins;i++) {
            hh+=histogram[i]+",";        
        }
        hh+="]\n[";
         for (int i=0;i<num_bins;i++) {
            hh+=util.deux_decimal(histogram_min[i])+"-"+util.deux_decimal(histogram_max[i])+",";        
        }
         hh+="]\n";
        return hh;
    }
    
    /**
     * Return the double-sided p-value associated with the ref data
     * @param values
     * @param ref
     * @return 
     */
    public Double[] getPvalue1(double[] values, double ref) {
              double ceg=0.0;
              double csup=0.0;
              double cinf=0.0;
              double replicate=values.length;
              for (int j=0;j<replicate;j++) {
                   if (values[j]==ref) ceg+=1.0;
                  if (values[j]<ref)  cinf+=1.0;
                   if (values[j]>ref)  csup+=1.0;
              }
              double P1=((csup+ceg)+1)/((1.0*replicate)+1.0);
              double P2=((cinf+ceg)+1)/((1.0*replicate)+1.0);
              Double[] d=new Double[5];
              d[0]=P1;
              d[1]=P2;
              d[2]=ceg;    
              d[3]=cinf;
              d[4]=csup;
              return d;
                      //"P1: "+P1+" P2:"+P2+" (creg:"+ceg+" csup:"+csup+" cinf:"+cinf+")";
    }
    
     public static Double[] getPvalue_unilateral(double[] values, double ref, double total_nodes) {              
              double csup_eg=0.0;              
              double replicate=values.length;
              for (int j=0;j<replicate;j++) {                   
                   if (values[j]>=ref&&values[j]!=ref)  csup_eg+=1.0;
              }
              double P1=((csup_eg+1.0)/((1.0*replicate)+1.0));
              Double[] d=new Double[5];
              d[0]=P1;
              d[1]=0.05/total_nodes;              
              d[2]=csup_eg; 
              d[3]=replicate;
              d[4]=0.0;
              //System.out.println("Uni"+Arrays.toString(d));
              return d;                   
    }
    
      public static Double[] getPvalue_bilateral(double[] values, double ref, double total_nodes) {              
              double csup_eg=0.0;    
              double cinf_eg=0.0;    
              double replicate=values.length;
              for (int j=0;j<replicate;j++) {                   
                   if (values[j]>=ref&&values[j]!=ref)  csup_eg+=1.0;
                   if (values[j]<=ref&&values[j]!=ref)  csup_eg+=1.0;
              }
              double P1=((csup_eg+1.0)/((1.0*replicate)+1.0));
              double P2=((cinf_eg+1.0)/((1.0*replicate)+1.0));
              
              Double[] d=new Double[7];
              d[0]=Math.min(P1, P2);
              d[1]=P1;
              d[2]=P2;
              d[3]=0.05/total_nodes;              
              d[4]=csup_eg;
              d[5]=cinf_eg;
              d[6]=replicate;
              //System.out.println("bi"+Arrays.toString(d));
              return d;                   
    }
     
    public double getPvalue2(ArrayList<Float[]> values, Float[] ref) {
        //--Create the double version
        double[] dref=new double[ref.length];
        for (int i=0; i<ref.length;i++) dref[i]=ref[i].doubleValue();
        //--get the min and max of the whole distribution
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (Float[] value:values) 
            for (double v:value) stats.addValue(v);        
        for (double v:ref) stats.addValue(v);            
        double min=stats.getMin();
        double max=stats.getMax();        
        //--histref by TOTALBINS classes
        ArrayList<ArrayList<Float>> histref=calcBins(ref, TOTALBINS, min,max);
        //--Calculate the reference frequency
        double[] freq_ref=new double[TOTALBINS];
        for (int i=0; i<TOTALBINS;i++) {
            freq_ref[i]=histref.get(i).size(); //--Number and not relative
        }
        //--Calculate by frequence in each bins for the replicate
        for (int i=0; i<TOTALBINS;i++) {
            
            for (int j=0; j<values.size();j++) {
                ArrayList<ArrayList<Float>> histrand=calcBins(values.get(j), TOTALBINS, min,max);
            }
        }
        
        
        return 0;
    }
    
     public static double quartile2(double[] values, double lowerPercent) {      
        double[] v = new double[values.length];
        System.arraycopy(values, 0, v, 0, values.length);
        Arrays.sort(v);
        int n = (int) Math.round(v.length * lowerPercent / 100);
        return v[n];
    }
     
     //TestUtils.tTest(sample1, sample2);
     public double quantile(double[] values, double lowerPercent) {
         DescriptiveStatistics stats = new DescriptiveStatistics();
         for (double v:values) stats.addValue(v);         
         return stats.getPercentile(lowerPercent);
     }
     /**
      * Return the frequenfy for each bins of value
      * @param values
      * @param num_bins
      * @return 
      */
     public double[] histogram(double[] values, int num_bins) {      
        double[] histogram = new double[num_bins];
        org.apache.commons.math3.random.EmpiricalDistribution distribution = new org.apache.commons.math3.random.EmpiricalDistribution(num_bins);
        distribution.load(values);
        int k = 0;
        for(org.apache.commons.math3.stat.descriptive.SummaryStatistics stats: distribution.getBinStats())
        {            
            histogram[k++] = (stats.getN()/(1.0f*values.length));
        }
        
        return histogram;
     }
     
     /**
      * This classify into bins the values
      * @param values
      * @param numBins
      * @return 
      */
     public static ArrayList<ArrayList<Float>> calcBins(Float[] values, int numBins, double min, double max) {
       ArrayList<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();       
        DescriptiveStatistics stats = new DescriptiveStatistics();
        if (max==0) {
            for (double v:values) stats.addValue(v);         
            min=stats.getMin();
            max=stats.getMax();
        }
        final double binSize = (max - min)/numBins;
        for (Float d : values) {
          int bin = (int) ((d - min) / binSize); 
          if (bin < 0) { 
              bin=0;
          } else if (bin >= numBins) { 
              bin=numBins-1;      
          } else {
              ArrayList<Float> r=result.get(bin);
              if (r==null) r=new ArrayList<Float>();
                r.add(d);
                result.set(bin, r);
            }
          }        
        return result;
        }
     
     
     
     
      /**
      * This classify into bins the values
      * @param values
      * @param numBins
      * @param min
      * @param max
      * @return 
      */
     public static ArrayList<ArrayList<Float>> calcBins(ArrayList<Float[]> values, int numBins, double min, double max) {
       ArrayList<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();       
        if (max==0) {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            for (Float[] value:values) 
            for (double v:value) stats.addValue(v);         
            min=stats.getMin();
            max=stats.getMax();
        }       
        final double binSize = (max - min)/numBins;
         for (Float[] value:values) 
            for (Float d : value) {
                int bin = (int) ((d - min) / binSize); 
                if (bin < 0) { 
                    bin=0;
                } else if (bin >= numBins) { 
                    bin=numBins-1;      
                } else {
                    ArrayList<Float> r=result.get(bin);
                    if (r==null) r=new ArrayList<Float>();
                      r.add(d);
                      result.set(bin, r);
                  }
                }        
        return result;
        }

    @Override
    public String toString() {
        StringBuffer st=new StringBuffer();
//      pvalue_fieldsString[] pv={"total_CC_type1","total_CC_type3",
//        "total_CC_complete",
//        "total_ap_global_complete",
//        "total_edges_type1",
//        "total_edges_type2",
//        "total_edges_type3",
//        "total_edges_complete",
//        "total_ap_global_type3",
//        "total_ap_local_type3",
//        "total_ap_local_complete",
//        "triplet_type3",
//        "convergence",
////"per_loop4_type3",
////"per_len4_type3"
//        };
//pvalue_fields      for (String p:pv) {
//            st.append(getPvalue(p, replicates, reference));
//        }
//        
        return st.toString(); 
    }
     
    public Object getRefNodeStatistics(int row, int col) {
     try {
           node n=reference.data.nodes.get(row);
           switch (col) {
               case 0: return n.id;
               case 1: return " "+n.complete_name;
               case 2: return (n.stats.getBoolean("found_in_type_1")?"x":"-");
               case 3: return (n.stats.getBoolean("found_in_type_2")?"x":"-");
               case 4: return (n.stats.getBoolean("found_in_type_3")?"x":"-");
               case 5: return (n.stats.getBoolean("found_in_complete")?"x":"-");
               case 6: return n.stats.get("column");
               case 7: return n.stats.get("encoded_state");
               case 8: return n.stats.get("char_states");
               case 9: return (n.stats.get("CC_type1").equals("0")?"-":n.stats.get("CC_type1"));
               case 10: return (n.stats.get("CC_complete").equals("0")?"-":n.stats.get("CC_complete"));
               case 11: return (n.stats.getBoolean("local_ap_type3")?"x":"-");
               case 12: return (n.stats.getBoolean("global_ap_type3")?"x":"-");
               case 13: return (n.stats.getBoolean("local_ap_complete")?"x":"-");
               case 14: return (n.stats.getBoolean("global_ap_complete")?"x":"-");
               case 15: return n.stats.get("in_degree2");
               case 16: return n.stats.get("norm_indegree_type2");             
               case 17: return (n.stats.isSet("betweenness_type3")?n.stats.getFloat("betweenness_type3"):0.0f);
               case 18: return (n.stats.isSet("closeness_type3")?n.stats.getFloat("closeness_type3"):0.0f);
               case 19: return (n.stats.isSet("triplet_type3")?n.stats.getFloat("triplet_type3"):0.0f);
               case 20: return n.stats.getFloat("percent_triplet_type3");                        
               case 21: return (n.stats.isSet("convergence")?n.stats.getFloat("convergence"):0.0f);
               case 22: return n.stats.get("taxa");                              
           }
     } catch(Exception e) {
         
     }   
     return "";
}
    
     public Object getNetworkValue(ArrayList<stats> datas, int row, int col) {
        // We need to get to the displayed sequence     
        try {
           
           stats s=datas.get(row);
           double pvalue=Math.min(s.pvalue[0],s.pvalue[1]);    
           switch (col) {
               case 0: return s.title;
               case 1: return s.reference_value;
               case 2: return pvalue; //P1
               case 3: return getSignificance(pvalue,s.reference_value);                
               case 4: return s.stat.getN()-1; //--Remove ref
               case 5: return s.stat.getMean();
               case 6: return s.stat.getStandardDeviation();
               case 7: return s.stat.getMin();
               case 8: return s.stat.getMax();
               case 9: return s.stat.getPercentile(5);
               case 10: return s.stat.getPercentile(95);
               default: return s.stat.getElement(col-11);
           }
           
//return data.char_matrix[col][row];
       } catch(Exception e) {
           e.printStackTrace();
           return 0;
       }         
    //return 0;
    }
    
//    public void output_html(String filename) {
//        
//          String[] pv2={
//                    "total_CC_type1",
//                    "total_edges_type3",
//                    "total_ap_global_complete", 
//                    "total_ap_local_complete",
//                    "total_ap_local_type3",
//                     "triplet_type3", 
//                     "convergence",                   
//      };
//      String[] decription2={
//             "nb de complexes (CC type 1)",
//                    "nb d’aretes (type 3)",
//                    "nb de points d'articulations globaux", 
//                    "nb de points d’articulations locaux",
//                    " nb de points d’articulations locaux (type 3)",
//                     "nb de triplets non transitifs (type 3)", 
//                     "proportion de cycles de taille 4 (type3)",        
//          
//      };
//      
//       String[] qualifier2={"Statistics","Reference","<i>p</i>-value","N","Mean","STD","Min","Max"};
//      
//        util u=new util();
//        u.open(filename);
//        u.println("<html><head></head><body>");
//         u.println("<h2>General info</h2><br>");        
//         u.println("<table border=1>");
//         u.println("<tr><td>");
//         u.println(reference_data.st_option.toString().replaceAll("\n", "<br>"));
//         u.println("Total permutations (N replicate):"+this.replicate+"<br>");
//         u.println("Total time "+util.msToString(endtime-starttime)+"<br>");
//         u.println("</td></tr></table>");          
//        String[] sta={"in_degree2","out_degree2", "closeness_type3","betweenness_type3","percent_triplet_type3"};
//        //--Summary statistic per node
//        u.println("<h2>General node statistics</h2><br>");        
//        u.println("<table border=1>");
//        u.println("<thead>");
//        for (String s:qualifier) u.println("<th>"+s+"</th>");        
//        u.println("</thead><tbody>");
//        for (int r=0; r<reference.data.nodes.size();r++) {
//            u.println("<tr>");
//            for (int c=0; c<qualifier.length;c++) {
//                u.println("<td>"+getRefNodeStatistics(r, c)+"</td>");
//            }
//            u.println("</tr>");
//        }        
//        u.println("</tbody></table>");
//        //--Permutation statistics global network
//        ArrayList<stats> datas=this.calculate_stat();
//        
//        u.println("<h2>General permutation statistics</h2><br>"); 
//        u.println("<table border=1>");
//          u.println("<thead>");
//         for (String s:qualifier2) u.println("<th>"+s+"</th>");   
//           u.println("</thead><tbody>");   
//           u.println("<tr>");
//          for (int r=0; r<pv2.length;r++) {
//             for (int c=0;c<qualifier2.length;c++) {
//                 u.println("<td>"+getNetworkValue(datas,r, c)+"</td>");
//             }
//              u.println("</tr>");
//         }                      
//         u.println("</tbody></table>");
//        //--Permutation statistics per node
//        for (String identifier:sta) {
//            u.println("<h2>"+identifier+"</h2><br>");
//            u.println("<table border=1>");
//            u.println("<thead><th>NodeID</th><th>Name</th><th>Reference</th>");
//            if (identifier.equals("closeness_type3")) {
//                u.println("<th>P1</th><th>P2</th><th>Others values</th></thead><tbody>");
//            } else {
//                u.println("<th>p-value</th><th>Others values</th></thead><tbody>");
//            }
//            for (int nodeid=0; nodeid<reference_data.nodes.size();nodeid++) {
//             node n=reference.data.nodes.get(nodeid);
//             u.println("<tr>");
//             u.println("<td>"+n.id+"</td><td>"+n.complete_name+"</td>");
//             double ref=0;
//             if (identifier.equals("in_degree2")||identifier.equals("out_degree2")) {
//                ref=this.reference.data.nodes.get(nodeid).stats.getInt(identifier);
//            } else {             
//               ref=this.reference.data.nodes.get(nodeid).stats.getFloat(identifier);
//             }
//              double[] values=new double[this.replicates.size()];
//            for (int i=0; i<this.replicates.size();i++) {
//                node nr=this.replicates.get(i).data.nodes.get(nodeid);
//                //System.out.println(nr.stats);
//                values[i]=nr.stats.getFloat(identifier);
//                if (identifier.equals("in_degree2")||identifier.equals("out_degree2")) {
//                values[i]=nr.stats.getInt(identifier);
//                }         
//                //System.out.println(this.data.replicates.get(i).data.nodes.get(nodeid));
//            }            
//            Double[] st;
//             if (identifier.equals("closeness_type3")) {
//                 st=permutation_statistics.getPvalue_bilateral(values, ref,this.reference.data.nodes.size());
//             } else {
//                 st=permutation_statistics.getPvalue_unilateral(values, ref,this.reference.data.nodes.size());
//             }
//             u.println("<td>"+ref+"</td>"); //--Reference value
//           
//             if (!identifier.equals("closeness_type3")) {
//                 u.println("<td>"+st[0]+"</td>"); //P-value
//             } else {
//                   u.println("<td>"+st[1]+"</td>"); //P-value
//                   u.println("<td>"+st[2]+"</td>"); //P-value
//             }
//             for (int j=0; j<values.length;j++) u.println("<td>"+values[j]+"</td>");             
//             u.println("</tr>");
//            }            
//            u.println("</tbody></table>");
//        }
//        u.println("</body></html>");
//        u.close();
//        
//    }
//    
    /**
     * Output a series of CSV file to the ouput directory
     * @param directory 
     */
     public void output_csv(String filename) {
        
         String[] qualifier={
  "Nodeid",
  "Name",
  "Found in type 1 network",
   "Found in type 2 network",
   "Found in type 3 network",
   "Found in complete network",
   "Column",
   "Encoded state",
   "Character",
   "Connected component type 1 network",    
   "Connected component complete network",    
   "Local articulation point type 3 network",
   "Global articulation point type 3 network",
   "Local articulation point complete network",
   "Global articulation point complete network",
   "Indegree type 2 network",
   "Normalized indegree type 2 network",
   "Betweenness type 3 network",
   "Closeness type 3 network",
   "Numbers of triplet type 3 network",
   "Percent(%) triplet type 3 network",   
   "Convergence",    
   "Taxa"
   };
    
    String[] complete_pv={
                    "total_CC_type1",
                    "total_CC_type3",
                    "total_CC_complete",
                    "total_ap_global_complete",
                    "total_edges_type1",
                    "total_edges_type2",
                    "total_edges_type3",
                    "total_edges_complete",
                    "total_ap_global_type3",
                    "total_ap_local_type3",
                    "total_ap_local_complete",
                    "triplet_type3",
                    "convergence",
                    "per_loop4_type3",
                    "per_len4_type3"
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
          String[] pv2={
                    "total_CC_type1",
                    "total_edges_type3",
                    "total_ap_global_complete", 
                    "total_ap_local_complete",
                    "total_ap_local_type3",
                     "triplet_type3", 
                     "convergence",                   
      };
      String[] decription2={
             "Number of connected component        (type 1 network)",
             "Number of edges                      (type 3 network)",
             "Number of global articulation points (complete network) ", 
             "Number of local articulation points  (complete network)",
             "Number of global articulation points (type 3 network)",
             "Number of non-transitive triplets    (type 3 network)", 
             " Ratio of length 4 cycles            (type 3 network)",                  
      };
      
       String[] qualifier2={"Statistics","Reference","p-value","Significance","N","Mean","STD","Min","Max","5%","95%"};
      String[] sta={"in_degree2","out_degree2", "closeness_type3","betweenness_type3","percent_triplet_type3"};
      
        util u=new util();
        //--Summary.txt
         u.open(filename+"_summary.txt");
         u.print(reference_data.st_option.toString());        
         u.println("Total permutations (N replicate)     : "+this.replicate);
         u.println("Total time                           : "+util.msToString(endtime-starttime));
         u.println("===============================================================================");
         u.println(reference_data.st_results.toString());
         u.close();
         
        
          u.open(filename+"_summary_statistics.csv");        
        //--Summary statistic per node        
        for (String s:qualifier) u.print(s+",");
        u.println("");
        //--Data
        for (int r=0; r<reference.data.nodes.size();r++) {            
            for (int c=0; c<qualifier.length;c++) {
                u.print(util.encapsulate(""+getRefNodeStatistics(r, c))+",");
            }
            u.println("");
        }        
        u.close();
                
        ArrayList<stats> datas=this.calculate_stat();
        
        if (datas.size()>0) {
            //--Permutation statistics global network
            u.open(filename+"_permutation_statistics.csv");        
            for (String s:qualifier2) u.print(s+",");     
//             for (int rep=0;rep<data.replicate;rep++) {
//                       u.print("randomization "+(rep+1)+",");
//                   }
            u.println();
            for (int r=0; r<pv2.length;r++) {
                 for (int c=0;c<qualifier2.length;c++) {
                     u.print(getNetworkValue(datas,r, c)+",");
                 }
                 
                 u.println();
            }    
            u.close();
            
            //--Permutation statistics per node
            for (String identifier:sta) {
                //--identifier is filename for now
                    u.open(filename+"_"+identifier+".csv");
                    u.print("NodeID,Name,Reference,p-value,significance level,N,Min,Max,Mean,STD,5%,95%,");
                    //        st.append("Min     : "+stats.getMin()+"\n");
//        st.append("Max     : "+stats.getMax()+"\n");
//        st.append("Mean    : "+stats.getMean()+"\n");
//        st.append("5%      : "+stats.getPercentile(5)+"\n");
//        st.append("95%     : "+stats.getPercentile(95)+"\n");
//        st.append("Ref     : "+refvalue+"\n");
//        st.append("P-value : "+getPvalue1(values,refvalue)+"\n");
                    
                   for (int rep=0;rep<data.replicate;rep++) {
                       u.print("randomization "+(rep+1)+",");
                   }
                   u.println();
                   System.out.println("nodes size:"+reference_data.nodes.size());
                    for (int nodeid=0; nodeid<reference_data.nodes.size();nodeid++) {
                     node n=reference.data.nodes.get(nodeid);
                   
                     u.print(n.id+","+n.complete_name+",");
                     double ref=0;
                     if (identifier.equals("in_degree2")||identifier.equals("out_degree2")) {
                        ref=this.reference.data.nodes.get(nodeid).stats.getInt(identifier);
                    } else {             
                       ref=this.reference.data.nodes.get(nodeid).stats.getFloat(identifier);
                     }
                     //--This is the array of values for this node
                     double[] values=new double[this.replicates.size()];
                    for (int i=0; i<this.replicates.size();i++) {
                        node nr=this.replicates.get(i).data.nodes.get(nodeid);
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
                         st=permutation_statistics.getPvalue_bilateral(values, ref,this.reference.data.nodes.size());
                     } else {
                         st=permutation_statistics.getPvalue_unilateral(values, ref,this.reference.data.nodes.size());
                     }
                     u.print(ref+","); //--Reference value
                     double pvalue=0.0;
                     if (!identifier.equals("closeness_type3")) {
                        pvalue=st[0];
                     } else {
                        pvalue=st[1]; //P-value (P1)                          
                     }
                     //--Add information for 
                    if (ref==0) {
                       u.print("NA,");
                       u.print(node_stat.getN()+","); //N
                       u.print(node_stat.getMin()+","); //Min
                       u.print(node_stat.getMax()+","); //Max
                       u.print(node_stat.getMean()+","); //Mean
                       u.print(node_stat.getStandardDeviation()+","); //STD
                       u.print(node_stat.getPercentile(5)+","); //5%
                       u.print(node_stat.getPercentile(95)+","); //95%        
                    } else {
                     u.print(pvalue+","); //P-value
                      u.print(getSignificance(pvalue,ref)+","); //P-value
                     u.print(node_stat.getN()+","); //N
                     u.print(node_stat.getMin()+","); //Min
                     u.print(node_stat.getMax()+","); //Max
                     u.print(node_stat.getMean()+","); //Mean
                     u.print(node_stat.getStandardDeviation()+","); //STD
                     u.print(node_stat.getPercentile(5)+","); //5%
                     u.print(node_stat.getPercentile(95)+","); //95%           
                    } 
                      for (int j=0; j<values.length;j++) u.print(values[j]+",");             
                     u.println();
                     
                    }
                    u.println("Significance levels: * p<0.05; ** p<0.01; *** p<0.001. Critical p-value levels ("+data.p05+";"+data.p01+";"+data.p001+")");
                    u.close();
                } //--End identifier
        }
      
    }
     
     public String getSignificance(double value, double reference) {
         if (data.replicate<100) return " ";
         if (reference==0) return " "; 
         if (value<=0.0) return " ";
         if (value>0.05) return " ";
         if (value<data.p001) return "***";
         if (value<data.p01) return "**";
         if (value<data.p05) return "*";
         return " ";
     }
     
     /**
      * This calculate global statistic (general)
      * @return 
      */
     public ArrayList<stats> calculate_stat() {
       ArrayList<stats> datas=new ArrayList<stats>();
       if (!data.permutation) return datas; //--No pvalue_fieldsplicate
       for (int p=0; p<pvalue_fields.length;p++) {
           String node_field=pvalue_fields[p];           
            stats s=new stats();
             s.node_field=pvalue_fields[p];
             s.title=pvalue_fields_description[p];
           
            double[] values=new double[replicates.size()];
            double refvalue=0.0f;            
         
            for (int i=0; i<replicates.size();i++) {
            
            
            switch (s.node_field) {
                case "total_CC_type1": values[i]=replicates.get(i).total_CC_type1; refvalue=reference.total_CC_type1; break;
               case "total_CC_type3": values[i]=replicates.get(i).total_CC_type3; refvalue=reference.total_CC_type3; break;
                case "total_CC_complete": values[i]=replicates.get(i).total_CC_complete; refvalue=reference.total_CC_complete;break;
                case "total_ap_global_complete": values[i]=replicates.get(i).total_ap_global_complete; refvalue=reference.total_ap_global_complete; break;
                case "total_edges_type1": values[i]=replicates.get(i).total_edges_type1; refvalue=reference.total_edges_type1;break;
                case "total_edges_type2": values[i]=replicates.get(i).total_edges_type2; refvalue=reference.total_edges_type2;break;
                case "total_edges_type3": values[i]=replicates.get(i).total_edges_type3; refvalue=reference.total_edges_type3;break;
                case "total_edges_complete": values[i]=replicates.get(i).total_edges_complete; refvalue=reference.total_edges_complete;break;
                case "total_ap_global_type3": values[i]=replicates.get(i).total_ap_global_type3; refvalue=reference.total_ap_global_type3;break;
                case "total_ap_local_type3": values[i]=replicates.get(i).total_ap_local_type3; refvalue=reference.total_ap_local_type3;break;
                case "total_ap_local_complete": values[i]=replicates.get(i).total_ap_local_complete; refvalue=reference.total_ap_local_complete;break;
                case "triplet_type3": values[i]=replicates.get(i).triplet_type3; refvalue=reference.triplet_type3;break;
                case "convergence": values[i]=replicates.get(i).convergence; refvalue=reference.convergence;break;
                case "per_loop4_type3": values[i]=replicates.get(i).per_loop4_type3; refvalue=reference.per_loop4_type3;break;
                case "per_len4_type3": values[i]=replicates.get(i).per_len4_type3; refvalue=reference.per_len4_type3;break;                        
            }            
           //System.out.println(node_field+" "+i+" :"+values[i]);
            s.stat.addValue(values[i]);
        }            
         s.stat.addValue(refvalue);        
        s.reference_value=refvalue;
        s.pvalue=getPvalue1(values, refvalue);
        datas.add(s);
//        st.append(node_field+":\n");
//        st.append("Min     : "+stats.getMin()+"\n");
//        st.append("Max     : "+stats.getMax()+"\n");
//        st.append("Mean    : "+stats.getMean()+"\n");
//        st.append("5%      : "+stats.getPercentile(5)+"\n");
//        st.append("95%     : "+stats.getPercentile(95)+"\n");
//        st.append("Ref     : "+refvalue+"\n");
//        st.append("P-value : "+getPvalue1(values,refvalue)+"\n");
//        st.append("Histogram:\n");
//        st.append(hist(values, TOTALBINS)+"\n");
//        st.append("Values:\n");       
       }
       return datas;
   }
    
    /**
     * Save analysis the current analysis to a file (json)
     * @param filename
     * @return 
     */        
     public boolean saveAnalysis(String filename) {
         try {
//            FileOutputStream fo = new FileOutputStream(filename);
//            ObjectOutputStream oos = new ObjectOutputStream(fo);
//            oos.writeObject(this);
//            oos.flush();   
//            oos.close();
//            Gson gson = new Gson();
//            util u=new util();
//            System.out.println("ser:"+data.serial_file);
//            //gson.newJsonWriter(new FileWriter(new File("data.json")));
//            u.open(data.serial_file);
//            u.println(gson.toJson(this));
//            u.close();
               Gson gson = new Gson();
             JsonWriter js = new JsonWriter(new FileWriter(new File(filename)));
            gson.toJson(this,permutation_statistics.class,js);
            js.flush();
            js.close();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }         
         return true;
     }
    
     public boolean loadAnalysis(String datafile) {
         if (util.DirExists(datafile)) {
             
         } else {
             
             try {
//             FileInputStream fi = new FileInputStream(datafile);
//            ObjectInputStream ois = new ObjectInputStream(fi);
//           permutation_statistics su=(permutation_statistics) ois.readObject();
              Gson gson = new Gson();
                 permutation_statistics su=gson.fromJson(gson.newJsonReader(new FileReader(new File(datafile))),permutation_statistics.class);

                 
            this.total_permute=su.total_permute; //--Permutation of matrix             
            this.original_state_matrix=su.original_state_matrix;
            this.data=su.data;
            this.reference_data=su.reference_data;
           this.original_state_matrix=su.original_state_matrix;
           this.reference=su.reference;
           this.replicates=su.replicates;
           this.total_permute=su.total_permute;
           this.current_replicate=su.current_replicate;           
           this.starttime=su.starttime;
           this.endtime=su.endtime;
                 
             } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
         }
         return true;
     }
     
    
    public void serialize() {
        try {
             Gson gson = new Gson();
            util u=new util();
            //System.out.println("ser:"+data.serial_file);
            JsonWriter js = new JsonWriter(new FileWriter(new File(data.serial_file+".json")));
            gson.toJson(this,permutation_statistics.class,js);
            js.flush();
            js.close();
            //u.close();
//             String assert1=gson.toJson(this);
//            FileOutputStream fo = new FileOutputStream(data.serial_file);
//            ObjectOutputStream oos = new ObjectOutputStream(fo);
//            oos.writeObject(this);
//            oos.flush();   
//            oos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deserialize() {
           try {
                Gson gson = new Gson();
                 permutation_statistics su=gson.fromJson(gson.newJsonReader(new FileReader(new File(data.serial_file+".json"))),permutation_statistics.class);
            this.total_permute=su.total_permute; //--Permutation of matrix             
            this.original_state_matrix=su.original_state_matrix;
            this.data=su.data;
           this.original_state_matrix=su.original_state_matrix;
           this.reference=su.reference;
           this.replicates=su.replicates;
           this.total_permute=su.total_permute;
           this.current_replicate=su.current_replicate;           
           this.starttime=su.starttime;
           this.endtime=su.endtime;       
           } catch(Exception e) {
            e.printStackTrace();
        }
    }
     
      /**
     * Test serialization
     * @param args 
     */
     public static void main(String[] args) {
       Locale.setDefault(new Locale("en", "US"));    
         datasets dummy=new datasets();        
         //dummy.load_morphobank_nexus("sample\\Smith_Caron_wo_absent_trait.nex");         
         dummy.load_simple("sample\\sample_5.txt");         
         //dummy.compute();
         
         //dummy.display_result();
         //System.out.println(dummy.st_option.toString());
         //System.out.println(dummy.st_results.toString());
         dummy.replicate=5;
         
         permutation_statistics su=new permutation_statistics(dummy);
          permutation_statistics su2=new permutation_statistics(dummy);
         su.generate_statistics();
//         Gson gson = new Gson();
//         String assert1=gson.toJson(su);         
//         String assert2=gson.toJson(gson.fromJson(assert1, permutation_statistics.class));
//         System.out.println(assert1.length());
//         System.out.println(assert2.length());
//         util u=new util();
//         u.open("data.txt");
//         u.println(assert1);
//         u.println(assert2);
//         u.close();
         su.serialize();
         
//         System.out.println(su.replicates.size());
         su2.deserialize();
         System.out.println(su.calculate_stat());
         System.out.println(su2.calculate_stat());
//         String assert2=gson.toJson(su);
//         System.out.println(assert1.equals(assert2));
//         util u=new util();
//         u.open("data.txt");
//         u.println(assert1);
//         u.println("*****");
//         u.println(assert2);
//         u.close();
//         System.out.println(su.replicates.size());         
          //permutation_statistics su=new permutation_statistics(dummy);
           //  su.generate_statistics();
           //   System.out.println(su);
//         for (int i=0; i<5; i++) {
//             System.out.println(dummy.get_info());
////             summary_statistics s=new summary_statistics(dummy);
////             s.calculate_network_statistics();
//             //System.out.println(s);
//             permutation_statistics su=new permutation_statistics(dummy);
//             su.generate_stati();
//             //System.out.println(su);
//         }
//         for (summary_statistics ss:su.replicates) {
//             System.out.println(ss.data.getCurrentCharMatrix());
//         }
////          su.serialize();
////          su.deserialize();
////          System.out.println(su);
//         System.out.println(su.reference);
         
     }
     
}
