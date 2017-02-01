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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
 * Implement the bootstrap or permutation permutation_statistics
 * @author Etienne Lord
 */
public class permutation_statistics implements Serializable {
    
    
   public static  String[] qualifier={
   "Nodeid",
   "Name",
   "found_in_type_1",
    "found_in_type_2",
    "found_in_type_3",
    "found_in_complete",
    "column",
    "encoded_state",
    "char_states",
    "CC_type1",
    "CC_complete",
    "local_ap_type3",
    "global_ap_type3",
    "local_ap_complete",
    "global_ap_complete",
    "in_degree_type1",
    "in_degree_type2",
    "norm_indegree_type2",
    "in_degree_type3",    
    "out_degree_type1",
    "out_degree_type2",
    "out_degree_type3",    
    "betweenness_type3",
    "closeness_type3",
    "triplet_type3",
    "per_triplet_type3",
    "triplet_complete",
    "per_triplet_complete",
    "max_shortest_path_type3",
    "max_shortest_path_complete",
    "convergence",
    "progressive_transition",
    "progressive_transition_end_node",   
    "Taxa"
   };
    
     public static  String[] complete_pv={
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
     public static  String[] pv={
                    "total_CC_type1",
                    "total_edges_type3",
                    "total_ap_global_complete", 
                    "total_ap_local_complete",
                    "total_ap_local_type3",
                     "triplet_type3", 
                     "convergence",                   
      };
      public static String[] decription={
             "nb de complexes (CC type 1)",
                    "nb d’aretes (type 3)",
                    "nb de points d'articulations globaux", 
                    "nb de points d’articulations locaux",
                    " nb de points d’articulations locaux (type 3)",
                     "nb de triplets non transitifs (type 3)", 
                     "proportion de cycles de taille 4 (type3)",        
          
      };
    
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
    
    public summary_statistics reference=null; //--The reference 
    public ArrayList<summary_statistics> replicates=new ArrayList<summary_statistics>(); //--The replicates
    long starttime=0;
    long endtime=0;
    
   //--Helper class 
   public class stats {
       public String title="";
       public String node_field="";
       public Double reference_value=0.0;       
       public DescriptiveStatistics stat=new DescriptiveStatistics();    
       public Double[] pvalue;        
   }
   
    
    /**
     * Note: call after the current_state_matrix is generated in datasets.prepare_current_state_matrix
     * @param data 
     */
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
    /***
     * Randomly permute the value in i and j
     * @param col i.e. character p.e. pied 
     */
    public void permute(int col) {
        int index_i=rand.nextInt(0,data.ntax-1);        
        int index_j=rand.nextInt(0,data.ntax-1);
        while (index_j==index_i) {
            index_j=rand.nextInt(0,data.ntax-1);
        }
       
        String o=data.current_state_matrix[index_i][col];
        data.current_state_matrix[index_i][col]=data.current_state_matrix[index_j][col];
        data.current_state_matrix[index_j][col]=o;        
    }
    
    /**
     * From the original data, do one matrix permutation
     */
    public void generate_permutation() {
        
        //Restore original matrix
       for (int i=0;i<data.ntax;i++) {
            for (int j=0; j<data.nchar;j++) {
                data.current_state_matrix[i][j]=original_state_matrix[i][j];
            }
        }        
       
       //Do the random permuation
        for (int j=0; j<data.nchar;j++) {
            for (int i=0; i<data.ntax-1;i++) permute(j);
         }        
    }
    
    public void saveReferenceStatistics() {
        String directory=reference_data.result_directory;
        String serial=directory+File.separator+"reference.ser";
        util.CreateDir(directory);             
        // 1. Serialize
        System.out.println(serial);
        reference.serialize(serial);
        
        // 2. Export summary statistics ...
        
    }
    
    public void saveReplicateStatistics(int replicate, summary_statistics su) {
         String directory=reference_data.result_directory;
         util.CreateDir(directory);         
         String serial=directory+File.separator+"randomization_"+replicate+".ser";
         su.serialize(serial);
    }
    
    /**
     * Main function to call to generate statistics
     */
    public void generate_statistics() {
       //--Get the original permutation_statistics
       //data.bipartite=false;
       //--First save information into the experiments.txt
        
        
        //--Second, test if 
        
        this.replicate=reference_data.replicate;
        System.out.println(reference_data.result_directory);
       reference_data.compute();
       data.MessageOption(data.get_info());
       //reference_data.compute_nodes();
       //reference_data.compute_network_solution();
       
        //data.printCurrentCharMatrix();
       starttime=System.currentTimeMillis();
       reference=new summary_statistics(reference_data);
        data.MessageResult("Calculating reference\n");
        System.out.println("\nAnalyzing :"+reference.data.filename);        
        System.out.println("Calculating reference\n");
        reference.calculate_network_statistics();
        //--Save the reference and statistics to results directory
        saveReferenceStatistics();
        
        long estimate=System.currentTimeMillis()-starttime;
        System.out.println("Total time for one replicate:"+util.msToString(estimate));
        System.out.println("Estimatied time for all replicates ("+replicate+"):"+util.msToString((replicate*estimate/4)));      
            
        data.MessageResult(reference.get_info().toString());        
        
         long timerunning=System.currentTimeMillis();  //--We will estimate 1%
         int one_percent=(int)Math.ceil(replicate/100)+1;
         
         ExecutorService pool=Executors.newFixedThreadPool(datasets.maxthreads);
         final ArrayList<Callable<summary_statistics>>partitions=new ArrayList<Callable<summary_statistics>>();
        if (data.replicate<=1) return;
         for (int i=0; i<replicate;i++) {
             partitions.add(new Callable<summary_statistics>(){
             public summary_statistics call() {
                  final int this_replicate= current_replicate++;
                  final long this_start_time=System.currentTimeMillis();
                   System.out.println(this_replicate+" / "+replicate +" [started]");
                    data.MessageResult(this_replicate+" / "+replicate +" [started]");
                   datasets t=new datasets(data);
                   t.generate_permutation();
                   t.compute();
                   summary_statistics s=new summary_statistics(t);            
                    s.calculate_network_statistics();           
                    
                    //data.MessageResult(s.get_info().toString());           
                     saveReplicateStatistics(this_replicate, s);
                    try {
                        if (callback!=null) callback.call();
                         
                    } catch(Exception e) {
                        e.printStackTrace();
                   }
                  
                    String dt=this_replicate+" / "+replicate +" [done in "+util.msToString(System.currentTimeMillis()-this_start_time)+"]";
                    System.out.println(dt);
                    data.MessageResult(dt);
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
             e.printStackTrace();
         }
         endtime=System.currentTimeMillis();
         System.out.println("Total time: "+util.msToString(endtime-starttime));
//        for (int i=1;i<=replicate;i++) {
//            this.current_replicate=i;
//             
//            //this.generate_permutation();
//            datasets t=new datasets(data);
//            t.generate_permutation();
//            
//            data.MessageResult("Replicate matrix ["+i+"/"+replicate+"]\n");
//            //data.MessageResult(data.getCurrentCharMatrix());
//            data.MessageResult("Calculating replicate: "+i+"/"+replicate+"( "+util.msToString(System.currentTimeMillis()-timerunning)+")\n");
//            //data.printCurrentCharMatrix();
//            //data.compute_network_solution();            
//            t.compute();
//            summary_statistics s=new summary_statistics(t);            
//            s.calculate_network_statistics();
//           
//            data.MessageResult(s.get_info().toString());
//           
//            try {
//                if (callback!=null) callback.call();
//            } catch(Exception e) {}
////            sdata[i][0]=s.total_CC_complete;
////            sdata[i][1]=s.total_CC_type1;
////            sdata[i][2]=s.total_CC_type2;
////            sdata[i][3]=s.total_CC_type3;
//            
//           //--TO DO, don't keep the whole permutation_statistics here...
//            replicates.add(s);
//            //System.out.println(i+"\t"+s.total_CC_complete+"\t"+s.total_CC_type1+"\t"+s.total_CC_type2+"\t"+s.total_CC_type3);
//            
//            if (i%1==0) {
//                datasets.CleanMemory();
//                //--Save backup
//                //serialize();
//            }
//        }
        // Calculate the p-value
//        for (int i=0; i<4;i++) {
//            float creg=0;
//            float csup=0;
//            float cinf=0;
//            for (int j=1;j<=100;j++) {
//                if (sdata[j][i]==sdata[0][i]) creg+=1;
//                if (sdata[j][i]<sdata[0][i])  cinf+=1;
//                if (sdata[j][i]>sdata[0][i])  csup+=1;
//            } 
//            System.out.println("p-value "+i+" P1:"+((csup+creg)/100.0)+"  P2:"+((cinf+creg)/100.0));
//        }
        
        //--Pour graphCustomHistogramDataset hist=new CustomHistogramDataset();``
        
        data.MessageResult("Output p-value:\n");
        String[] pv={"total_CC_type1","total_CC_type3",
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
                        "per_len4_type3"};

        
        for (String id:pv) {
           // data.MessageResult(getPvalue(id, replicates, reference));
        }
        
//         i)Le nb de complexes (CC de type 1)
//ii)Le nb d’aretes de type 3
//iii)Le nb de points d articulations globaux
//iv)Le nb de points d’articulations locaux (dans graphe total)
//v)Le nb de points d’articulations locaux (dans graphe  de type 3)
//vi)Le nb de triplets non transitifs  dans graphe  de type 3.
//vii)La proportion de cycles de taille 4 dans notre graphe de type 3
    
    
        
        //--Done, restore data
        data=reference_data;
        //data=new datasets(reference_data);
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
//        String[] pv={"total_CC_type1","total_CC_type3",
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
//        for (String p:pv) {
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
               case 15: return n.stats.get("in_degree1");
               case 16: return n.stats.get("in_degree2");
               case 17: return n.stats.get("norm_indegree_type2");
               case 18: return n.stats.get("in_degree3");
               case 19: return n.stats.get("out_degree1");
               case 20: return n.stats.get("out_degree2");
               case 21: return n.stats.get("out_degree3");    
               case 22: return (n.stats.isSet("betweenness_type3")?n.stats.getFloat("betweenness_type3"):0);
               case 23: return (n.stats.isSet("closeness_type3")?n.stats.getFloat("closeness_type3"):0);
               case 24: return (n.stats.isSet("triplet_type3")?n.stats.getFloat("triplet_type3"):0);
               case 25: return n.stats.get("percent_triplet_type3");
               case 26: return (n.stats.isSet("triplet_complete")?n.stats.getFloat("triplet_complete"):0);
               case 27: return n.stats.get("percent_triplet_complete");
               case 28: return n.stats.getInt("max_shortest_path_type3");
               case 29: return n.stats.getInt("max_shortest_path_complete");
               case 30: return (n.stats.isSet("convergence")?n.stats.getFloat("convergence"):0);
               case 31: return n.stats.get("progressive_transition");
               case 32: return n.stats.get("progressive_transition_end_node");    
               //case 28: return n.stats.get("contains");
              // case 29: return n.stats.get("percent_contained");
               case 33: return n.stats.get("taxa");
               
           }
     } catch(Exception e) {
         
     }   
     return "";
}
    
     public Object getNetworkValue(ArrayList<stats> datas, int row, int col) {
        // We need to get to the displayed sequence     
        try {
           
           stats s=datas.get(row);
                      
           switch (col) {
               case 0: return s.title;
               case 1: return s.reference_value;
               case 2: return Math.min(s.pvalue[0],s.pvalue[1]); //P1
               //case 3: return ; //P2
               case 3: return s.stat.getN()-1; //--Remove ref
               case 4: return s.stat.getMean();
               case 5: return s.stat.getStandardDeviation();
               case 6: return s.stat.getMin();
               case 7: return s.stat.getMax();
               default: return s.stat.getElement(col-8);
           }
           
//return data.char_matrix[col][row];
       } catch(Exception e) {
           e.printStackTrace();
           return 0;
       }         
    //return 0;
    }
    
    public void output_html(String filename) {
        
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
             "nb de complexes (CC type 1)",
                    "nb d’aretes (type 3)",
                    "nb de points d'articulations globaux", 
                    "nb de points d’articulations locaux",
                    " nb de points d’articulations locaux (type 3)",
                     "nb de triplets non transitifs (type 3)", 
                     "proportion de cycles de taille 4 (type3)",        
          
      };
      
       String[] qualifier2={"Statistics","Reference","<i>p</i>-value","N","Mean","STD","Min","Max"};
      
        util u=new util();
        u.open(filename);
        u.println("<html><head></head><body>");
         u.println("<h2>General info</h2><br>");        
         u.println("<table border=1>");
         u.println("<tr><td>");
         u.println(reference_data.st_option.toString().replaceAll("\n", "<br>"));
         u.println("Total permutations (N replicate):"+this.replicate+"<br>");
         u.println("Total time "+util.msToString(endtime-starttime)+"<br>");
         u.println("</td></tr></table>");          
        String[] sta={"in_degree2","out_degree2", "closeness_type3","betweenness_type3","percent_triplet_type3"};
        //--Summary statistic per node
        u.println("<h2>General node statistics</h2><br>");        
        u.println("<table border=1>");
        u.println("<thead>");
        for (String s:qualifier) u.println("<th>"+s+"</th>");        
        u.println("</thead><tbody>");
        for (int r=0; r<reference.data.nodes.size();r++) {
            u.println("<tr>");
            for (int c=0; c<qualifier.length;c++) {
                u.println("<td>"+getRefNodeStatistics(r, c)+"</td>");
            }
            u.println("</tr>");
        }        
        u.println("</tbody></table>");
        //--Permutation statistics global network
        ArrayList<stats> datas=this.calculate_stat();
        
        u.println("<h2>General permutation statistics</h2><br>"); 
        u.println("<table border=1>");
          u.println("<thead>");
         for (String s:qualifier2) u.println("<th>"+s+"</th>");   
           u.println("</thead><tbody>");   
           u.println("<tr>");
          for (int r=0; r<pv2.length;r++) {
             for (int c=0;c<qualifier2.length;c++) {
                 u.println("<td>"+getNetworkValue(datas,r, c)+"</td>");
             }
              u.println("</tr>");
         }                      
         u.println("</tbody></table>");
        //--Permutation statistics per node
        for (String identifier:sta) {
            u.println("<h2>"+identifier+"</h2><br>");
            u.println("<table border=1>");
            u.println("<thead><th>NodeID</th><th>Name</th><th>Reference</th>");
            if (identifier.equals("closeness_type3")) {
                u.println("<th>P1</th><th>P2</th><th>Others values</th></thead><tbody>");
            } else {
                u.println("<th>p-value</th><th>Others values</th></thead><tbody>");
            }
            for (int nodeid=0; nodeid<reference_data.nodes.size();nodeid++) {
             node n=reference.data.nodes.get(nodeid);
             u.println("<tr>");
             u.println("<td>"+n.id+"</td><td>"+n.complete_name+"</td>");
             double ref=0;
             if (identifier.equals("in_degree2")||identifier.equals("out_degree2")) {
                ref=this.reference.data.nodes.get(nodeid).stats.getInt(identifier);
            } else {             
               ref=this.reference.data.nodes.get(nodeid).stats.getFloat(identifier);
             }
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
            Double[] st;
             if (identifier.equals("closeness_type3")) {
                 st=permutation_statistics.getPvalue_bilateral(values, ref,this.reference.data.nodes.size());
             } else {
                 st=permutation_statistics.getPvalue_unilateral(values, ref,this.reference.data.nodes.size());
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
     
     public ArrayList<stats> calculate_stat() {
       ArrayList<stats> datas=new ArrayList<stats>();
       
       for (int p=0; p<pv.length;p++) {
       
           String node_field=pv[p];
           
           stats s=new stats();
           s.node_field=pv[p];
           s.title=decription[p];
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
    
    
    
    public void serialize() {
        try {
            FileOutputStream fo = new FileOutputStream(data.serial_file);
            ObjectOutputStream oos = new ObjectOutputStream(fo);
            oos.writeObject(this);
            oos.flush();   
            oos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deserialize() {
           try {
             FileInputStream fi = new FileInputStream(data.serial_file);
            ObjectInputStream ois = new ObjectInputStream(fi);
           permutation_statistics dd=(permutation_statistics) ois.readObject();
           this.data=dd.data;
           this.original_state_matrix=dd.original_state_matrix;
           this.reference=dd.reference;
           this.total_permute=dd.total_permute;
           this.current_replicate=dd.current_replicate;           
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
         su.generate_statistics();
         System.out.println(su);
         su.output_html("test.html");
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
