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
import java.util.HashMap;
import java.util.Locale;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * New class for the summary_statistics 
 * This is the descriptive statistics for one dataset
 * @author Etienne Lord
 */
public class summary_statistics implements Serializable {
        
        public datasets data; //Dataset for this summary_statistics
    
        public  ArrayList<ArrayList<Integer>> CC;
        public  Integer[] CC_info_type1;
        public  Integer[] CC_info_type2;
        public  Integer[] CC_info_complete;
        public  Integer[] CC_info_type3;
        public  boolean[] local_articulation_point;
        public  boolean[] global_articulation_point;
        public  boolean[] local_articulation_point_complete;
        public  boolean[] global_articulation_point_complete;
          //ArrayList<Integer>[] global_articulation_point=new ArrayList[4];
        public  Float[][] Triplets;
        public  Float[] betweenness;
        public  Float[] closeness;
        public  Float[] in_degree2;
        public  Float[] out_degree2;
        public  Float[] in_degree2_norm;
        public  Float[] path_len4_type3;
        public  Float[] path_loop_len4_type3;
        public  int[][] degrees;
          
 
        public   ArrayList<String>[] Progressive_transition;
        public   Integer[] max_sp_type3;
        public   Integer[] max_sp_complete;
        public   float total_triplet_type3=0;
        public   float total_triplet_complete=0;
        public   float total_triplet_type2=0;
        public   int total_CC_type1=0;
        public   int total_CC_type2=0;
        public   int total_CC_type3=0;
        public   int total_CC_complete=0;
       
        public long total_time=0;
       
        //--Total for the different columns
      // System.out.println("Total\t"+total_taxa+"\t"+data.node_id_type.get(1).size()+"\t"+data.node_id_type.get(2).size()+"\t"+data.node_id_type.get(3).size()+"\t"+data.node_id_type.get(0).size()+"\tNA\tNA\tNA\t"+total_CC_type1+"\t"+total_CC_complete+"\t"+total_ap_local_type3+"\t"+total_ap_global_type3+"\t"+total_ap_local_complete+"\t"+total_ap_global_complete+"\tNA\tNA\tNA\tNA\tNA\tNA\tNA\tNA\tNA\t"+total_progressive+"\t\t"+total_taxa);
         public     int total_taxa=0;
         public     int total_node=0;         
         public int  total_node_type1=0;
         public int  total_node_type2=0;
         public int  total_node_type3=0;
         public int  total_node_type0=0;
         public int  total_edges_complete=0;
         public int  total_edges_type1=0;
         public int  total_edges_type2=0;
         public int  total_edges_type3=0;
         public int  total_edges_type4=0;
         public double total_ap_local_type3=0;
         public double total_ap_local_complete=0;
         public double total_ap_global_type3=0;
         public double total_ap_global_complete=0;
         public double total_progressive=0;
         public double total_convergence=0;
         public double triplet_type1=0;
         public double triplet_type2=0;
         public double triplet_type3=0;
         public double triplet_complete=0;
         public double per_triplet_type3=0;
         public double loop4_type3=0;
         public double len4_type3=0;
         public double per_len4_type3=0;
         public double per_loop4_type3=0;
         public double convergence=0;
         public double max_spc=0;         
         public double max_sp3=0;
                  
       /**
        * Constructor
        * @param dat the dataset to analyse
        */
        public summary_statistics(datasets dat) {
            super();
            this.data=dat;
            this.total_node=data.nodes.size();           
       }
        
         public summary_statistics() {
         }
        /**
         * Initialize the data structures and default values (0)
         * @param dat 
         */  
          public void init() {
              local_articulation_point=new boolean[data.nodes.size()];
              global_articulation_point=new boolean[data.nodes.size()];
              local_articulation_point_complete=new boolean[data.nodes.size()];
              global_articulation_point_complete=new boolean[data.nodes.size()];
            //ArrayList<Integer>[] global_articulation_point=new ArrayList[4];
              Triplets=new Float[7][data.nodes.size()]; // 0..3 (network complete,type 1, ...), 4: type 1 edge, 5, type 2 edge, 6: type 4 edge
              betweenness=new Float[data.nodes.size()];
              closeness=new Float[data.nodes.size()];
              in_degree2=new Float[data.nodes.size()];
              out_degree2=new Float[data.nodes.size()];
              in_degree2_norm=new Float[data.nodes.size()];
              path_len4_type3=new Float[data.nodes.size()];
              path_loop_len4_type3=new Float[data.nodes.size()];
              degrees=new int[data.nodes.size()][6];
              CC_info_type1=new Integer[data.nodes.size()];
              CC_info_type3=new Integer[data.nodes.size()];
              CC_info_complete=new Integer[data.nodes.size()];
              Progressive_transition=new  ArrayList[data.nodes.size()];
              max_sp_type3=new Integer[data.nodes.size()];
              max_sp_complete=new Integer[data.nodes.size()]; 
              total_triplet_type3=0;
              total_triplet_complete=0;
              total_triplet_type2=0;
              total_CC_type1=0;
              total_CC_complete=0;

            for (int i=0; i<data.nodes.size();i++) {
                for (int j=0; j<6;j++) degrees[i][j]=0;
                CC_info_type1[i]=0;
                CC_info_complete[i]=0;
                CC_info_type3[i]=0;
                max_sp_type3[i]=0;
                max_sp_complete[i]=0;
                in_degree2_norm[i]=0.0f;
                in_degree2[i]=0.0f;
                out_degree2[i]=0.0f;
                path_len4_type3[i]=0.0f;
                path_loop_len4_type3[i]=0.0f;
                Progressive_transition[i]=new ArrayList<String>(0);
            }
          }
     
      /**
       * Return the graph from this summary (or datasets)
       * @return 
       */    
      public ArrayList<graph> getGraphs() {
          ArrayList<graph> tmp=new ArrayList<graph>();  
          for (int type=0; type<=4;type++) {
             graph g=new graph();
             g.name="network "+type;
              if (type==4) {
                   for (int i=0; i<data.type4_total_edge;i++) {
                      int src=g.addNode(data.type4_src_edge[i]);
                      int dest=g.addNode(data.type4_dest_edge[i]);
                      g.addEdge(src, dest);
                      g.addEdge(dest, src);                      
                      g.total_edges++;                           
                   }
                    g.directed=false;  
              }

              //--Add edge
              if (type!=4) 
                  for (int i=0; i<data.current_total_edge;i++) {
                  if (type==0) {
                      int src=g.addNode(data.src_edge[i]);
                      int dest=g.addNode(data.dest_edge[i]);
                      switch(data.type_edge[i]) {
                          case 1: degrees[data.src_edge[i]][0]++;
                                  degrees[data.dest_edge[i]][0]++;
                                  degrees[data.src_edge[i]][1]++;
                                  degrees[data.dest_edge[i]][1]++;                                  
                                  break;                                                        
                          case 2: degrees[data.src_edge[i]][3]++;                                                                    
                                  degrees[data.dest_edge[i]][2]++;                                  
                                  break;                                  
                          case 3: degrees[data.src_edge[i]][4]++;
                                  degrees[data.dest_edge[i]][4]++;
                                  degrees[data.src_edge[i]][5]++;
                                  degrees[data.dest_edge[i]][5]++;                                  
                                  break;                                 
                      }
                      g.addEdge(src, dest);
                      g.addEdge(dest, src);                      
                      g.total_edges++;
                      g.directed=false;                                            
                  }                 
                  if (type==1&&data.type_edge[i]==1) {
                      int src=g.addNode(data.src_edge[i]);
                      int dest=g.addNode(data.dest_edge[i]);
                      g.addEdge(src, dest);
                      g.addEdge(dest, src);                      
                      g.total_edges++;
                      g.directed=false;                                 
                  }
                  if (type==2&&data.type_edge[i]==2) {
                      int src=g.addNode(data.src_edge[i]);
                      int dest=g.addNode(data.dest_edge[i]);
                      g.addEdge(src, dest);
                      g.total_edges++;
                      g.directed=true;
                  } 
                  if (type==3&&data.type_edge[i]==3) {
                      int src=g.addNode(data.src_edge[i]);
                      int dest=g.addNode(data.dest_edge[i]);
                      g.addEdge(src, dest);
                      g.addEdge(dest, src);   
                      g.total_edges++;
                      g.directed=false;                        
                  } 
              } //End edges type 0...3
              g.total_nodes=g.id_to_old_id.size();
              tmp.add(g);
        }   
         return (tmp); 
      }
          
     /**
      * Main function to calculate_network_statistics 
      * This calculate the network statistics
      */     
     public StringBuilder calculate_network_statistics_old() {
      StringBuilder st=new StringBuilder();
      init();
      data.MessageResult("===============================================================================\n");
      data.MessageResult("Computing statistics:\n");
      data.MessageResult("===============================================================================\n");
      long timerunning=System.currentTimeMillis();                
      for (int type=0; type<4;type++) {
            
             graph g=new graph();
             g.name="network "+type;
              //--Add edge
              for (int i=0; i<data.current_total_edge;i++) {
                  if (type==0) {
                      int src=g.addNode(data.src_edge[i]);
                      int dest=g.addNode(data.dest_edge[i]);
                      switch(data.type_edge[i]) {
                          case 1: degrees[data.src_edge[i]][0]++;
                                  degrees[data.dest_edge[i]][0]++;
                                  degrees[data.src_edge[i]][1]++;
                                  degrees[data.dest_edge[i]][1]++;                                  
                                  break;                                                        
                          case 2: degrees[data.src_edge[i]][3]++;                                                                    
                                  degrees[data.dest_edge[i]][2]++;                                  
                                  break;                                  
                          case 3: degrees[data.src_edge[i]][4]++;
                                  degrees[data.dest_edge[i]][4]++;
                                  degrees[data.src_edge[i]][5]++;
                                  degrees[data.dest_edge[i]][5]++;                                  
                                  break;                                 
                      }
                      g.addEdge(src, dest);
                      g.addEdge(dest, src);                      
                      g.total_edges++;
                      g.directed=false;                      
                      
                  }
                  if (type==1&&data.type_edge[i]==1) {
                      int src=g.addNode(data.src_edge[i]);
                      int dest=g.addNode(data.dest_edge[i]);
                      g.addEdge(src, dest);
                      g.addEdge(dest, src);                      
                      g.total_edges++;
                      g.directed=false;            
                     
                  }
                  if (type==2&&data.type_edge[i]==2) {
                      int src=g.addNode(data.src_edge[i]);
                      int dest=g.addNode(data.dest_edge[i]);
                      g.addEdge(src, dest);
                      g.total_edges++;
                      g.directed=true;
                  } 
                  if (type==3&&data.type_edge[i]==3) {
                      int src=g.addNode(data.src_edge[i]);
                      int dest=g.addNode(data.dest_edge[i]);
                      g.addEdge(src, dest);
                      g.addEdge(dest, src);   
                      g.total_edges++;
                      g.directed=false;                        
                  } 
              }
              g.total_nodes=g.id_to_old_id.size();
                            
              //System.out.println(""+(type==0?"complete    \t":type+"           \t")+g.total_nodes+"\t"+g.total_edges+" \t"+g.density());
              //--calculate_network_statistics some statistic on each graph                           
              if (type==0) {
                   for (int i=0; i<g.total_nodes;i++) {                          
                     Triplets[0][g.id_to_old_id.get(i)]=g.find_triplet(i, false);                     
                    total_triplet_complete+= Triplets[0][g.id_to_old_id.get(i)];
                    if (Triplets[0][g.id_to_old_id.get(i)]==0)Triplets[0][g.id_to_old_id.get(i)]=null; //--for display purpose
                     local_articulation_point_complete[g.id_to_old_id.get(i)]=g.is_local_articulation_point(i);
                     global_articulation_point_complete[g.id_to_old_id.get(i)]=g.is_global_articulation_point(i);
                   }
                   ArrayList<ArrayList<Integer>>tmp=g.getCC();
                    total_CC_complete=tmp.size();
                    for (int i=0; i<tmp.size();i++) {
                      ArrayList<Integer>cc=tmp.get(i);
                      for (int w:cc) CC_info_complete[g.id_to_old_id.get(w)]=i+1;
                  }
                   
              }
              
              if (type==1) {
                  CC=g.getCC();
                  total_CC_type1=CC.size();
                  //--REname each node in cc
                  for (int i=0; i<CC.size();i++) {
                      ArrayList<Integer>cc=CC.get(i);
                      for (int w:cc) CC_info_type1[g.id_to_old_id.get(w)]=i+1;
                  }
              }           
              
              if (type==2) {
//                   CC=g.getCC();
//                  total_CC_type2=CC.size();
                 for (int i=0; i<g.total_nodes;i++) {                                             
                   in_degree2[g.id_to_old_id.get(i)]=g.in_degree(i);
                   out_degree2[g.id_to_old_id.get(i)]=g.out_degree(i);
                   in_degree2_norm[g.id_to_old_id.get(i)]=  in_degree2[g.id_to_old_id.get(i)]/(float)(g.total_nodes-1);
                }   
//                  for (int i=0; i<CC.size();i++) {
//                      ArrayList<Integer>cc=CC.get(i);
//                      for (int w:cc) CC_info_type2[g.id_to_old_id.get(w)]=i+1;
//                  }
              }
            
              if (type==3) {
                  float[] tmp=g.Betweenness();
                  float[] tmp_c=g.Closeness();
                  CC=g.getCC();
                  total_CC_type3=CC.size();
                  //--REname each node in cc
                  for (int i=0; i<CC.size();i++) {
                      ArrayList<Integer>cc=CC.get(i);
                      for (int w:cc) CC_info_type3[g.id_to_old_id.get(w)]=i+1;
                  }
                  for (int i=0; i<g.total_nodes;i++) {
                      betweenness[g.id_to_old_id.get(i)]=tmp[i];
                      closeness[g.id_to_old_id.get(i)]=tmp_c[i];
                      local_articulation_point[g.id_to_old_id.get(i)]=g.is_local_articulation_point(i);
                      global_articulation_point[g.id_to_old_id.get(i)]=g.is_global_articulation_point(i);
                      graph.results r=g.findLoops(i,false);
                      path_len4_type3[g.id_to_old_id.get(i)]=r.total_len4;
                      path_loop_len4_type3[g.id_to_old_id.get(i)]=r.total_loop3+r.total_loop4;                                    
                      Triplets[3][g.id_to_old_id.get(i)]=g.find_triplet(i, false); //--Normal Triplet
                      total_triplet_type3+= Triplets[3][g.id_to_old_id.get(i)];
                }
              }
               
              data.MessageResult("("+(type+1)*10+"%) Phase ["+(type+1)+"/4] ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
          } // End for type
          
         data.MessageResult("(50%) Creating and analyzing intermediate networks ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
          //--Test for transitive progress (Shortpath>2 in type 3 but never smaller in complete
          //--Note: the graph is mixed
          graph g3=new graph();
          graph gc=new graph();
          for (int i=0; i<data.current_total_edge;i++) {
                      int type=data.type_edge[i];
                      int src=gc.addNode(data.src_edge[i]);
                      int dest=gc.addNode(data.dest_edge[i]);                      
                      gc.addEdge(src, dest);
                      if (type!=2)gc.addEdge(dest, src);                                              
                      src=g3.addNode(data.src_edge[i]);
                      dest=g3.addNode(data.dest_edge[i]); 
                      if (type==3) {
                          g3.addEdge(dest, src);
                          g3.addEdge(src, dest);
                      }
                      
                     
         }
          g3.directed=false; 
          gc.directed=true;
          g3.total_nodes=g3.id_to_old_id.size();
          gc.total_nodes=gc.id_to_old_id.size();
          //--Now execute Floyd
          data.MessageResult("(60%) Shortest paths complete network ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
          int[][] gcf= gc.Floyd();
          data.MessageResult("(80%) Shortest paths type 3 network ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
          int[][] g3f=g3.Floyd();
         
          //--Search for progressive_tr
          for (int i=0; i<g3.total_nodes;i++) {
              int original_id=g3.id_to_old_id.get(i);
              max_sp_type3[original_id]=-1; 
              max_sp_complete[original_id]=-1;
              int original_id_gc=gc.old_id_to_id.get(original_id);
              for (int j=0; j<g3.total_nodes;j++) {
              if (i!=j) {
                int original_id_j=g3.id_to_old_id.get(j);
                //int nodeid_g3=i;
                int nodeid_gc=gc.old_id_to_id.get(original_id_j);
                Integer len_g3=g3f[i][j];
                Integer len_gc=gcf[original_id_gc][nodeid_gc];
                    if (len_g3<graph.infinity) {
                        if (len_g3>2&&len_gc>=len_g3) {
                            Progressive_transition[original_id].add(data.nodes.get(original_id_j).complete_name);
                        }
                    }
                   if (len_g3>max_sp_type3[original_id]&&len_g3<graph.infinity) max_sp_type3[original_id]=len_g3;
                   if (len_gc>max_sp_complete[original_id]&&len_gc<graph.infinity) max_sp_complete[original_id]=len_gc;
              }
            }
         }
       this.total_time=System.currentTimeMillis()-timerunning;
       st=calculate_node_statistics();
       data.MessageResult("(100%) Done statistics ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
        data.MessageResult("===============================================================================\n");
        
        return st;
     }     
       
     /**
      * Main function to calculate_network_statistics 
      * This calculate the network statistics
      */     
     public StringBuilder calculate_network_statistics() {
      StringBuilder st=new StringBuilder();
      init();
      data.MessageResult("===============================================================================\n");
      data.MessageResult("Computing statistics:\n");
      data.MessageResult("===============================================================================\n");
      long timerunning=System.currentTimeMillis();                
      ArrayList<graph> networks=getGraphs();
     //    System.out.println(networks);
         

      //--Calculate stats for different types of network
      //--Complete (type 0)          
                  graph g=networks.get(0);
                   for (int i=0; i<g.total_nodes;i++) {                          
                     Triplets[0][g.id_to_old_id.get(i)]=g.find_triplet(i, false);                     
                    total_triplet_complete+= Triplets[0][g.id_to_old_id.get(i)];
                    if (Triplets[0][g.id_to_old_id.get(i)]==0)Triplets[0][g.id_to_old_id.get(i)]=null; //--for display purpose
                     local_articulation_point_complete[g.id_to_old_id.get(i)]=g.is_local_articulation_point(i);
                     global_articulation_point_complete[g.id_to_old_id.get(i)]=g.is_global_articulation_point(i);
                   }
                   ArrayList<ArrayList<Integer>>tmp=g.getCC();
                    total_CC_complete=tmp.size();
                    for (int i=0; i<tmp.size();i++) {
                      ArrayList<Integer>cc=tmp.get(i);
                      for (int w:cc) CC_info_complete[g.id_to_old_id.get(w)]=i+1;
                  }
                   
         //--Complete (type 1)        
              
              g=networks.get(1);
                  CC=g.getCC();
                  total_CC_type1=CC.size();
                  //--REname each node in cc
                  for (int i=0; i<CC.size();i++) {
                      ArrayList<Integer>cc=CC.get(i);
                      for (int w:cc) CC_info_type1[g.id_to_old_id.get(w)]=i+1;
                  }
                         
            //--Complete (type 2)     
                g=networks.get(2);
                 for (int i=0; i<g.total_nodes;i++) {                                             
                   in_degree2[g.id_to_old_id.get(i)]=g.in_degree(i);
                   out_degree2[g.id_to_old_id.get(i)]=g.out_degree(i);
                   in_degree2_norm[g.id_to_old_id.get(i)]=  in_degree2[g.id_to_old_id.get(i)]/(float)(g.total_nodes-1);
                }   

              
              //--Complete (type 3) 
                  g=networks.get(3);
                  
                  float[] tmp3=g.Betweenness();
                  float[] tmp_c3=g.Closeness();
                  CC=g.getCC();
                  total_CC_type3=CC.size();
                  //--REname each node in cc
                  for (int i=0; i<CC.size();i++) {
                      ArrayList<Integer>cc=CC.get(i);
                      for (int w:cc) CC_info_type3[g.id_to_old_id.get(w)]=i+1;
                  }
                  for (int i=0; i<g.total_nodes;i++) {
                      betweenness[g.id_to_old_id.get(i)]=tmp3[i];
                      closeness[g.id_to_old_id.get(i)]=tmp_c3[i];
                      local_articulation_point[g.id_to_old_id.get(i)]=g.is_local_articulation_point(i);
                      global_articulation_point[g.id_to_old_id.get(i)]=g.is_global_articulation_point(i);
                      graph.results r=g.findLoops(i,false);
                      path_len4_type3[g.id_to_old_id.get(i)]=r.total_len4;
                      path_loop_len4_type3[g.id_to_old_id.get(i)]=r.total_loop3+r.total_loop4;                                    
                      //Triplets[3][g.id_to_old_id.get(i)]=g.find_triplet(i, false); //--Normal Triplet
                      Float[] tri=g.find_triplet(i,false,networks);
                      Triplets[3][g.id_to_old_id.get(i)]=tri[0]; //--Type A
                      Triplets[4][g.id_to_old_id.get(i)]=tri[1]; //--Type B
                      Triplets[5][g.id_to_old_id.get(i)]=tri[2]; //--Type C
                      Triplets[6][g.id_to_old_id.get(i)]=tri[3]; //--Type D
                      
                      total_triplet_type3+= Triplets[3][g.id_to_old_id.get(i)];
                 }
              
                //--End  
               
             // data.MessageResult("("+(type+1)*10+"%) Phase ["+(type+1)+"/4] ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
         
          
         data.MessageResult("(50%) Creating and analyzing intermediate networks ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
          //--Test for transitive progress (Shortpath>2 in type 3 but never smaller in complete
          //--Note: the graph is mixed
          graph g3=new graph();
          graph gc=new graph();
          for (int i=0; i<data.current_total_edge;i++) {
                      int type=data.type_edge[i];
                      int src=gc.addNode(data.src_edge[i]);
                      int dest=gc.addNode(data.dest_edge[i]);                      
                      gc.addEdge(src, dest);
                      if (type!=2)gc.addEdge(dest, src);                                              
                      src=g3.addNode(data.src_edge[i]);
                      dest=g3.addNode(data.dest_edge[i]); 
                      if (type==3) {
                          g3.addEdge(dest, src);
                          g3.addEdge(src, dest);
                      }
                      
                     
         }
          g3.directed=false; 
          gc.directed=true;
          g3.total_nodes=g3.id_to_old_id.size();
          gc.total_nodes=gc.id_to_old_id.size();
          //--Now execute Floyd
          data.MessageResult("(60%) Shortest paths complete network ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
          int[][] gcf= gc.Floyd();
          data.MessageResult("(80%) Shortest paths type 3 network ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
          int[][] g3f=g3.Floyd();
         
          //--Search for progressive_tr
          for (int i=0; i<g3.total_nodes;i++) {
              int original_id=g3.id_to_old_id.get(i);
              max_sp_type3[original_id]=-1; 
              max_sp_complete[original_id]=-1;
              int original_id_gc=gc.old_id_to_id.get(original_id);
              for (int j=0; j<g3.total_nodes;j++) {
              if (i!=j) {
                int original_id_j=g3.id_to_old_id.get(j);
                //int nodeid_g3=i;
                int nodeid_gc=gc.old_id_to_id.get(original_id_j);
                Integer len_g3=g3f[i][j];
                Integer len_gc=gcf[original_id_gc][nodeid_gc];
                    if (len_g3<graph.infinity) {
                        if (len_g3>2&&len_gc>=len_g3) {
                            Progressive_transition[original_id].add(data.nodes.get(original_id_j).complete_name);
                        }
                    }
                   if (len_g3>max_sp_type3[original_id]&&len_g3<graph.infinity) max_sp_type3[original_id]=len_g3;
                   if (len_gc>max_sp_complete[original_id]&&len_gc<graph.infinity) max_sp_complete[original_id]=len_gc;
              }
            }
         }
       this.total_time=System.currentTimeMillis()-timerunning;
       st=calculate_node_statistics();
       data.MessageResult("(100%) Done statistics ( "+util.msToString(System.currentTimeMillis()-timerunning)+").\n");
        data.MessageResult("===============================================================================\n");
        
        return st;
     }     
     
     /**
      * This calculate individual node statistics 
      * Note: this need to be called after calculate_network_statistics()
      * @return a string of the results
      */
     private StringBuilder calculate_node_statistics() {
           
          ArrayList<Integer> taxa_pos=new ArrayList<Integer>();
          boolean number=false;          
          if (data.taxa.length()>0) {              
               String[] t=data.taxa.split(",");
               try {
                   for (String s:t) {   
                      Integer i=Integer.valueOf(s);
                      taxa_pos.add(i); //--Add position starting at 1
                   }
                   number=true;
                 } catch(Exception e){}
              if (!number) {
                  //--Try to match taxa position
                  for (String s:t) {
                      for (int i=0; i<data.label.size();i++) {
                          if (data.label.get(i).toLowerCase().indexOf(s)>-1) taxa_pos.add(i+1);
                      }
                  }                  
              }
          } //--End if        
              total_taxa=0;
              total_ap_local_type3=0;
              total_ap_local_complete=0;
              total_ap_global_type3=0;
              total_ap_global_complete=0;
              total_progressive=0;
              triplet_type3=0;              
          
          try {         
              for (int i=0; i<data.nodes.size();i++) {
              node n=data.nodes.get(i);
              n.stats.put("in_degree1", degrees[n.id][0]);
              n.stats.put("out_degree1", degrees[n.id][1]);
              n.stats.put("in_degree2", degrees[n.id][2]);
              n.stats.put("out_degree2", degrees[n.id][3]);
              n.stats.put("in_degree3", degrees[n.id][4]);
              n.stats.put("out_degree3", degrees[n.id][5]);   
              
                            //--Get the CC for this node
                  String taxas=data.get_taxa(n.identification).toLowerCase();                                    
                  boolean contain_taxa=false;
                 ArrayList<Integer>  contains_total=new ArrayList<Integer>();
                  if (taxa_pos.size()>0) {
                    contains_total=(ArrayList<Integer>)util.intersection(n.identification,taxa_pos);                  
                    contain_taxa=!contains_total.isEmpty();
                  }
                 
                  float ft=n.identification.size();
                  if (ft==0) ft=1;
                  float percent_contains=contains_total.size()*100/ft;
                  
                  //--Some counter
                  if (contain_taxa)total_taxa++;
                 if (local_articulation_point[n.id]) total_ap_local_type3++;
                 if (global_articulation_point[n.id]) total_ap_global_type3++;
                 if (local_articulation_point_complete[n.id]) total_ap_local_complete++;
                 if (global_articulation_point_complete[n.id]) total_ap_global_complete++;                 
                 String max_sp3="Inf.";
                 if (max_sp_type3[n.id]!=null&&max_sp_type3[n.id]<graph.infinity) max_sp3=""+max_sp_type3[n.id];
                 if (max_sp_type3[n.id]==null||max_sp_type3[n.id]<1) max_sp3="";
                 String max_spc="Inf.";
                 if (max_sp_complete[n.id]!=null&&max_sp_complete[n.id]<graph.infinity) max_spc=""+max_sp_complete[n.id];
                 if (max_sp_complete[n.id]==null||max_sp_complete[n.id]<1) max_spc="";
                  total_progressive+=Progressive_transition[n.id].size();
                  n.stats.put("nodeid", n.id);
                  n.stats.put("complete_name", n.complete_name);
                  n.stats.put("found_in_type_1", data.node_id_type.get(1).containsKey(n.id));
                  n.stats.put("found_in_type_2", data.node_id_type.get(2).containsKey(n.id));
                  n.stats.put("found_in_type_3", data.node_id_type.get(3).containsKey(n.id));
                  n.stats.put("found_in_complete", data.node_id_type.get(0).containsKey(n.id));
                  n.stats.put("column", n.column);   
                  n.stats.put("encoded_state",n.state_matrix);
                  n.stats.put("char_states",n.char_label);
                    n.stats.put("CC_type1",CC_info_type1[n.id]);
                   // n.stats.put("CC_type2",CC_info_type2[n.id]);
                    n.stats.put("CC_type3",CC_info_type3[n.id]);
                    n.stats.put("CC_complete",CC_info_complete[n.id]);
                    n.stats.put("local_ap_type3",local_articulation_point[n.id]);
                    n.stats.put("global_ap_type3",global_articulation_point[n.id]);
                    n.stats.put("local_ap_complete",local_articulation_point_complete[n.id]);
                    n.stats.put("global_ap_complete",global_articulation_point_complete[n.id]);
                    n.stats.put("norm_indegree_type2",util.trois_decimal(in_degree2_norm[n.id]));
                    n.stats.put("betweenness_type3",betweenness[n.id]);
                    n.stats.put("closeness_type3",closeness[n.id]);
                    n.stats.put("triplet_type1",Triplets[1][n.id]);
                    triplet_type1+= n.stats.getFloat("triplet_type1");
                    n.stats.put("triplet_type2",Triplets[2][n.id]);                    
                    triplet_type2+= n.stats.getFloat("triplet_type2");
                    n.stats.put("triplet_type3",Triplets[3][n.id]);
                    
                     n.stats.put("triplet_typeA",Triplets[3][n.id]);
                     n.stats.put("triplet_typeB",Triplets[4][n.id]);
                     n.stats.put("triplet_typeC",Triplets[5][n.id]);
                     n.stats.put("triplet_typeD",Triplets[6][n.id]);
                    
                    triplet_type3+= n.stats.getFloat("triplet_type3");                    
                    n.stats.put("triplet_complete",Triplets[0][n.id]);                                        
                    triplet_complete+= n.stats.getFloat("triplet_complete");
                    n.stats.put("percent_triplet_type3",(Triplets[3][n.id]==null?"0.0":util.deux_decimal(Triplets[3][n.id]*100.0/total_triplet_type3)));
                    n.stats.put("percent_triplet_complete",(Triplets[0][n.id]==null?"0.0":util.deux_decimal(Triplets[0][n.id]*100.0/total_triplet_complete)));
                    n.stats.put("max_shortest_path_type3",max_sp3);
                    n.stats.put("max_shortest_path_complete",max_spc);
                    n.stats.put("convergence",(path_loop_len4_type3[n.id]==null||path_len4_type3[n.id]==null||path_len4_type3[n.id]==0?" ":util.trois_decimal(path_loop_len4_type3[n.id]/path_len4_type3[n.id])));
                    convergence+=(path_loop_len4_type3[n.id]==null||path_len4_type3[n.id]==null||path_len4_type3[n.id]==Float.POSITIVE_INFINITY||path_loop_len4_type3[n.id]==0?0.0f:path_loop_len4_type3[n.id]/path_len4_type3[n.id]);
                    //System.out.println(path_loop_len4_type3[n.id]+" "+path_len4_type3[n.id]);
                    loop4_type3+=(path_loop_len4_type3[n.id]==null?0.0:path_loop_len4_type3[n.id]);
                    len4_type3+=(path_loop_len4_type3[n.id]==null?0.0:path_loop_len4_type3[n.id]);
                    //total_convergence+= n.stats.getDouble("progressive_transition");
                    n.stats.put("progressive_transition",Progressive_transition[n.id].size());
                    total_progressive+= n.stats.getFloat("progressive_transition");
                    n.stats.put("progressive_transition_end_node",Progressive_transition[n.id]);
                    n.stats.put("contains",contain_taxa);
                    n.stats.put("percent_contained",percent_contains);
                    n.stats.put("taxa",data.get_taxa(n.identification));
                    n.stats.put("taxa_count",data.get_taxa_count(n.identification));
                    
                  
             //--Update node info
              data.nodes.set(i, n);
            }
         per_loop4_type3=loop4_type3/(1.0*data.nodes.size());
         per_len4_type3=len4_type3/(1.0*data.nodes.size());
         convergence=convergence/(1.0*data.nodes.size());
              //--Summary
          this.total_edges_type1=+data.total_type1;
          this.total_edges_type2=+data.total_type2;
          this.total_edges_type3=+data.total_type3;
          this.total_edges_complete=+data.total_type0;
              //System.out.println("Total\t"+total_taxa+"\t"+data.node_id_type.get(1).size()+"\t"+data.node_id_type.get(2).size()+"\t"+data.node_id_type.get(3).size()+"\t"+data.node_id_type.get(0).size()+"\tNA\tNA\tNA\t"+total_CC_type1+"\t"+total_CC_complete+"\t"+total_ap_local_type3+"\t"+total_ap_global_type3+"\t"+total_ap_local_complete+"\t"+total_ap_global_complete+"\tNA\tNA\tNA\tNA\tNA\tNA\tNA\tNA\tNA\t"+total_progressive+"\t\t"+total_taxa);
              //System.out.println("Note: 'x' indicates presence, CC stands for Connected Components, local_ap stands for Local Articulation points, global_ap stands for Global Articulation points, Triplets stands for linear series of 3 nodes where the terminal nodes are not connected, Convergence stands for the ratio of loops found in paths of length 3 or 4 from the starting nodes, Progressive convergence stands for short paths of length>2 in type 3 network that are not smaller in the complete network.");
              if (!data.taxa.isEmpty()) System.out.println("(Searched taxa: "+data.taxa+")");
            //  System.out.println("===============================================================================");
          } catch(Exception e) {e.printStackTrace();}
          return new StringBuilder();
     }
     
     /**
      * This return information about the solution for this CURRENT_CHAR_MATRIX and DATASETS
      * @return 
      */
     public StringBuilder get_info() {
        
         StringBuilder st=new StringBuilder();
         try {
         st.append("===============================================================================\n");
        st.append("Results:\n");
        st.append("===============================================================================\n");
        st.append("Edges (total)                     : "+data.total_type0+"\n");
        int unassigned_node=0;
        for (node n:data.nodes) if (!data.node_id_type.get(0).containsKey(n.id)) unassigned_node++;
        st.append("Edges type 1 (perfect)            : "+data.total_type1+"\n");
        st.append("Edges type 2 (inclusion)          : "+data.total_type2+"\n");
        st.append("Edges type 3 (overlap)            : "+data.total_type3+"\n");
        st.append("Edges type 4 (disjoint)           : "+data.total_type4+"\n");
        st.append("\n");
        st.append("Total nodes evaluated             : "+data.nodes.size()+"\n");      
        st.append("Total nodes                       : "+data.node_id_type.get(0).size()+"\n");      
        st.append("Node (unassigned)                 : "+unassigned_node+"\n");  
        st.append("Node type 1 (perfect)             : "+data.node_id_type.get(1).size()+"\n");
        st.append("Node type 2 (inclusion)           : "+data.node_id_type.get(2).size()+"\n");
        st.append("Node type 3 (overlap)             : "+data.node_id_type.get(3).size()+"\n");
        st.append("Node type 4 (disjoint)            : "+data.node_id_type.get(4).size()+"\n");
        st.append("===============================================================================\n");
           
        if (data.total_states>1&&data.state_strings.size()!=0) {
          String sti=data.state_strings.get(data.state_strings.size()-1);
          st.append("States variations : "+sti+"\n");
          st.append("Taxon->Character(column)|Value\n");
          st.append("--------------------------------------------------------------------------------\n");
          
          for (int i=0; i<data.states.size();i++) {
                 state s=data.states.get(i);
                 //--This might fail if there is no label
                 if (data.charlabels.size()>0) {
                    st.append(data.label.get(s.pos_i)+"->"+(s.pos_j+1)+" ("+data.charlabels.get(s.pos_j)+")|"+sti.charAt(i)+"\n");
                 } else {
                     st.append(data.label.get(s.pos_i)+"->"+(s.pos_j+1)+"|"+sti.charAt(i)+"\n");
                 }
          }
        st.append("===============================================================================\n");
         }
      } catch(Exception e) {
             e.printStackTrace();
         }
        return st;        
     }
     
     /**
      * Get the list of complexe associated with this network type
      * @param network_type
      * @return 
      */
     public ArrayList<connected_complexe> getConnectedComplexes(int network_type) {
         ArrayList<connected_complexe> CCS=new ArrayList<connected_complexe>();         
         HashMap<Integer,connected_complexe> map=new    HashMap<Integer,connected_complexe>();                                 
         boolean found =false; //--Flag of rid 0 
        //System.out.println(Arrays.toString(this.CC_info_complete));
         //System.out.println(Arrays.toString(this.CC_info_type1));
         int total_CC=0;
         switch(network_type) {
             case 0: total_CC=this.total_CC_complete; break;
             case 1: total_CC=this.total_CC_type1; break;
             case 2: total_CC=this.total_CC_type2; break;
             case 3: total_CC=this.total_CC_type3; break;                 
         }
         if (total_CC>0) 
            for (int i=0;i<this.total_node;i++) {
                 Integer index=0; 
                 switch(network_type) {
                     case 0: index=this.CC_info_complete[i]; break;
                     case 1: index=this.CC_info_type1[i]; break;
                     case 2: index=this.CC_info_type2[i]; break;
                     case 3: index=this.CC_info_type3[i]; break;                 
                 }  
                connected_complexe tmp=map.get(index);
                if (tmp==null) {
                    tmp=new connected_complexe();
                    tmp.network_type=network_type;
                    tmp.complexe_id=index; 
                    if (index==0) found=true;
                }
                tmp.nodes.add(data.nodes.get(i));
                map.put(index, tmp);
            }
         for (int index:map.keySet()) {             
             CCS.add(map.get(index));             
         }
         //--be sure no CCS have id 0         
         if (found) {
             for (int i=0; i<CCS.size();i++) {
                connected_complexe c=CCS.get(i);
                c.complexe_id++;
                CCS.set(i, c);
            }
         }
        
         
         
         return CCS;
     }
     

    @Override
    public String toString() {
        return this.get_info().toString();
    }
    
    public void copy(summary_statistics su) {
        this.init();
        this.data=su.data;
         for (ArrayList<Integer>cc : su.CC) {
             ArrayList<Integer> tmp=new ArrayList<Integer>();
             for (Integer i:cc) tmp.add(i);
             this.CC.add(tmp);
         }  
         for (int i=0; i<su.CC_info_complete.length;i++) this.CC_info_complete[i]=su.CC_info_complete[i];
         for (int i=0; i<su.CC_info_type1.length;i++) this.CC_info_type1[i]=su.CC_info_type1[i];
         for (int i=0; i<su.CC_info_type2.length;i++) this.CC_info_type2[i]=su.CC_info_type2[i];
         for (int i=0; i<su.CC_info_type3.length;i++) this.CC_info_type3[i]=su.CC_info_type3[i];
         for (int i=0; i<su.local_articulation_point.length;i++) this.local_articulation_point[i]=su.local_articulation_point[i];
         for (int i=0; i<su.global_articulation_point.length;i++) this.global_articulation_point[i]=su.global_articulation_point[i];
         for (int i=0; i<su.local_articulation_point_complete.length;i++) this.local_articulation_point_complete[i]=su.local_articulation_point_complete[i];
         for (int i=0; i<su.global_articulation_point_complete.length;i++) this.global_articulation_point_complete[i]=su.global_articulation_point_complete[i];
         for (int i=0; i<su.global_articulation_point.length;i++) this.global_articulation_point[i]=su.global_articulation_point[i];
         
          for (int i=0; i<su.betweenness.length;i++) this.betweenness[i]=su.betweenness[i];
          for (int i=0; i<su.closeness.length;i++) this.closeness[i]=su.closeness[i];
          for (int i=0; i<su.in_degree2.length;i++) this.in_degree2[i]=su.in_degree2[i];
          for (int i=0; i<su.out_degree2.length;i++) this.out_degree2[i]=su.out_degree2[i];
          for (int i=0; i<su.closeness.length;i++) this.closeness[i]=su.closeness[i];
          
          for (int i=0; i<su.path_len4_type3.length;i++) this.path_len4_type3[i]=su.path_len4_type3[i];
          for (int i=0; i<su.path_loop_len4_type3.length;i++) this.path_loop_len4_type3[i]=su.path_loop_len4_type3[i];
          for (int i=0; i<su.closeness.length;i++) this.closeness[i]=su.closeness[i];
          
          for (int i=0; i<su.max_sp_type3.length;i++) this.max_sp_type3[i]=su.max_sp_type3[i];
          for (int i=0; i<su.max_sp_complete.length;i++) this.max_sp_complete[i]=su.max_sp_complete[i];
          
          for (int i=0; i<Triplets.length;i++) {
              for (int j=0;j<Triplets[i].length;j++) Triplets[i][j]=su.Triplets[i][j];
          }
         
          for (int i=0; i<degrees.length;i++) {
              for (int j=0;j<degrees[i].length;j++) degrees[i][j]=su.degrees[i][j];
          }

        this.total_triplet_type3=su.total_triplet_type3;
        this.total_triplet_complete=su.total_triplet_complete;
        this.total_triplet_type2=su.total_triplet_type2;
        this.total_CC_type1=su.total_CC_type1;
        this.total_CC_type2=su.total_CC_type2;
        this.total_CC_type3=su.total_CC_type3;
        this.total_CC_complete=su.total_CC_complete;
       
        this.total_time=su.total_time;
       
        //--Total for the different columns
      // System.out.println("Total\t"+total_taxa+"\t"+data.node_id_type.get(1).size()+"\t"+data.node_id_type.get(2).size()+"\t"+data.node_id_type.get(3).size()+"\t"+data.node_id_type.get(0).size()+"\tNA\tNA\tNA\t"+total_CC_type1+"\t"+total_CC_complete+"\t"+total_ap_local_type3+"\t"+total_ap_global_type3+"\t"+total_ap_local_complete+"\t"+total_ap_global_complete+"\tNA\tNA\tNA\tNA\tNA\tNA\tNA\tNA\tNA\t"+total_progressive+"\t\t"+total_taxa);
         this.total_taxa=su.total_taxa;
         this.total_node=su.total_node;         
         this.total_node_type1=su.total_node_type1;
         this.total_node_type2=su.total_CC_type2;
         this.total_node_type3=su.total_node_type3;
         this.total_node_type0=su.total_node_type0;
         this.total_edges_complete=su.total_edges_complete;
         this.total_edges_type1=su.total_edges_type1;
         this.total_edges_type2=su.total_edges_type2;
         this.total_edges_type3=su.total_edges_type3;
         this.total_edges_type4=su.total_edges_type4;
         this.total_ap_local_type3=su.total_ap_local_type3;
         this.total_ap_local_complete=su.total_ap_local_complete;
         this.total_ap_global_type3=su.total_ap_global_type3;
         this.total_ap_global_complete=su.total_ap_global_complete;
         this.total_progressive=su.total_progressive;
         this.total_convergence=su.total_convergence;
         this.triplet_type1=su.triplet_type1;
         this.triplet_type2=su.triplet_type2;
         this.triplet_type3=su.triplet_type3;
         this.triplet_complete=su.triplet_complete;
         this.per_triplet_type3=su.per_triplet_type3;
         this.loop4_type3=su.loop4_type3;
         this.len4_type3=su.len4_type3;
         this.per_len4_type3=su.per_len4_type3;
         this.per_loop4_type3=su.per_loop4_type3;
         this.convergence=su.convergence;
         this.max_spc=su.max_spc;         
         this.max_sp3=su.max_sp3;
    }
    
    public boolean serialize(String filename) {
         
        try {           
            Gson gson = new Gson();
            JsonWriter js = new JsonWriter(new FileWriter(new File(filename)));
            gson.toJson(this,summary_statistics.class,js);
            js.flush();
            js.close();
         } catch(Exception e) {
             e.printStackTrace();
             Config.log("Error in serialize "+filename+":" + e.getMessage());
             return false;
         }
        return true;
    } 
    
    public boolean deserialize(String filename) {
         try {   
            Gson gson = new Gson();
            summary_statistics su=gson.fromJson(gson.newJsonReader(new FileReader(new File(filename))),summary_statistics.class);
            copy(su);
           
        } catch(Exception e) {
             Config.log("Error in deserialize "+filename+":" + e.getMessage());
             
         }
        return true;
    }
    
      /**
     * Test serialization
     * @param args 
     */
     public static void main(String[] args) {
           Locale.setDefault(new Locale("en", "US"));
         datasets data=new datasets();
         data.replicate=5;
         data.load_simple("sample\\sample_4.txt");
         datasets data2=new datasets(data);
         data.compute();
         data2.compute();
         System.out.println(data);
         System.out.println(data2);
         
        // for (int i=0; i<10;i++) {
            summary_statistics su=new summary_statistics(data);
            summary_statistics su2=new summary_statistics(new datasets());
            su.calculate_network_statistics();
            su.calculate_node_statistics();
            //su2.calculate_network_statistics();
         //}
//            System.out.println(su);
//            su.serialize("test.ser");
//            su2.deserialize("test.ser");
//            System.out.println(su2);
     }
     
}
