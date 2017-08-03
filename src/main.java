/*
 *  COMPOSITE-GRAPHER v1.0.7
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

import COMPONENT_GRAPHER.datasets;
import static COMPONENT_GRAPHER.main.authors;
import static COMPONENT_GRAPHER.main.help;
import static COMPONENT_GRAPHER.main.version;
import COMPONENT_GRAPHER.permutation_statistics;
import COMPONENT_GRAPHER.permutation_statistics_undefined;
import COMPONENT_GRAPHER.util;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US"));     
        
                
        
      //--Test for arguments
      if (args.length==1&&(args[0].equalsIgnoreCase("-h")||args[0].equalsIgnoreCase("-help"))) help();
      if (args.length>0) {
         //--Stand alone version   
         ///////////////////////////////////////////////////////////////////////
            /// FLAGS
            boolean analyse1=true;        //--Permutation statistic
            boolean analyse2=false;       //--Permutation statistic
            boolean save_graphml=false;   //--Save output as graphML
            boolean save_bipartite=false; //--Compute and save bipartite networks
            boolean save_triplets=false; //--Compute and save triplets information
            boolean save_summary=false; //--Compute and save summary permutation_statistics
            boolean bipartite=false;    //--Compute and save bipartite graph
            int bipartite_type=0;      //--Generate bipartite networks of type X where 0 indicate the complete networks
            boolean undefined=false;   //--Treat column containing undefined characters
            boolean multiple=false;    //--Treat column containing multiple characters
            boolean show_matrix=false; //--Display to stdout the input matrix
            boolean nooutput=false;    //--Silence output
            double minrand=0.0;        //--*Unused for now, the minimum randIndex to consider
            float mintaxa=1;         //-- the minimum number of taxa to consider
            int maxiter=1;          //--default, one iteration, get the first state
            int random_n=0;          //--Specify that we want random partition
            int perm_n=0;            //--Number of permutation for the p-value   
            int edges_selection_mode=0;
            int perm_mode=0;         //--Permutation mode
            String user_state_string="";//--passed user state string
            String node_information_filename="";
            String output_directory="";
            String input_directory="";
            
            //--Main dataset to compute 
          datasets d=new datasets();

      String filename=args[0];
      //--Display program info      
      d.commandline=util.string(args);
      System.out.println("COMPONENT-GRAPHER v"+version+"\n"+authors);
        
        // Read command line option
        for (String st:args) {
            String s=st.toLowerCase();    
            if (s.indexOf("-und")>-1) {
                analyse1=false;
                analyse2=true;
                perm_mode=4;
            }
            if (s.indexOf("-edges=")>-1) {
                edges_selection_mode=Integer.valueOf(st.substring(7));
                if (edges_selection_mode!=0) {
                    analyse1=false;
                    analyse2=true;
                }                 
            }
             if (s.indexOf("-permmode=")>-1) {
                perm_mode=Integer.valueOf(st.substring(10));
                if (perm_mode==4) {
                    analyse1=false;
                    analyse2=true;
                }                 
            }
            
              if (s.indexOf("-perm=")>-1) perm_n=Integer.valueOf(st.substring(6));
            
            if (s.indexOf("-tree=")>-1) {                
                d.tree_filename=st.substring(6);
            }
            if (s.indexOf("-k=")>-1) {              
                d.tree_k=Double.valueOf(st.substring(3));
            }
            if (s.indexOf("-seed=")>-1) {
                try {
                    d.seed=Long.valueOf(st.substring(6));                   
                    d.setSeed(d.seed);
                } catch(Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to read seed: "+st);
                    d.seed=1000;
                }
            }
            if (s.indexOf("-minrand=")>-1) minrand=Double.valueOf(st.substring(9));
            if (s.indexOf("-maxiter=")>-1) maxiter=Integer.valueOf(st.substring(9));
            if (s.indexOf("-mintaxa=")>-1) {
                if (s.indexOf("%")>-1) {
                    st=st.substring(0, st.length()-1);
                     mintaxa=Float.valueOf(st.substring(9));
                     mintaxa/=100;
                } else {
                    mintaxa=Float.valueOf(st.substring(9));
                }
            }
            if (s.indexOf("-maxpool=")>-1) datasets.maxthreads=Integer.valueOf(st.substring(9));
            if (s.indexOf("-threads=")>-1) datasets.maxthreads=Integer.valueOf(st.substring(9));
            if (s.indexOf("-perm=")>-1) { 
                perm_n=Integer.valueOf(st.substring(6));
            } 
            if (s.indexOf("-random=")>-1) random_n=Integer.valueOf(st.substring(8));
            if (s.indexOf("-variation=")>-1) user_state_string=st.substring(11);
            if (s.indexOf("-output=")>-1) output_directory=st.substring(8);
            if (s.indexOf("-undefined")>-1) undefined=true;
            if (s.indexOf("-multiple")>-1) multiple=true;            
            if (s.indexOf("-show_matrix")>-1) show_matrix=true;            
            if (s.indexOf("-graphml")>-1) save_graphml=true;
            if (s.indexOf("-nodeid=")>-1) node_information_filename=s.substring(8);
            if (s.indexOf("-taxa=")>-1) d.taxa=s.substring(6);
            if (s.indexOf("-nooutput")>-1) nooutput=true;
            if (s.indexOf("-summary")>-1) save_summary=true; 
            if (s.indexOf("-triplets")>-1) save_triplets=true; 
            if (s.indexOf("-input=")>-1) input_directory=st.substring(7);
            //--Given a graph, analyse it...
//            if (s.indexOf("-analyse")>-1) {
//                analyse1=true;
//                //--1. check if we refer to a graph or a series of graph
//                //analyze_multiple an=new analyze_multiple();
//                //an.process_path(s.substring(9));
//                //--This only printout summary permutation_statistics for this graph.
//                //d.analyse(s.substring(9),1);
//                //System.exit(0);
//            }
            if (s.indexOf("-bipartite")>-1) {
             bipartite=true;
            }
        }         
               
         if (!input_directory.isEmpty()) {
             permutation_statistics stat=new permutation_statistics();
             stat.calculate_from_directory_new(input_directory);
             
             stat.output_csv(input_directory+File.separator+util.getFilename(stat.data.filename));
             System.exit(0);
         }
        
         if (perm_mode==3) {
             System.out.println("Permutation mode 3 is not yet available.");
             System.exit(-1);
         }
          boolean r=d.load_morphobank_nexus(args[0]);               
        
          if (!r||d.nchar==0||d.ntax==0) r=d.load_simple(filename);
         if (!r||d.nchar==0||d.ntax==0) {
             System.out.println("Unable to load : "+filename+". No file found?");
             System.exit(-1);
         }
           //--Load phylogeny
         if (perm_mode==2&&d.tree_filename.isEmpty()) {
             System.out.println("Error. No phylogenetic tree pass to the -tree= option with phylogenetic permutations.");
             System.exit(-1);
         }
         if (!d.tree_filename.isEmpty()) {             
             if (!d.load_phylogeny()) {
                  System.exit(-1);
             }
             perm_mode=2; //phylogeny
         }
         
         
//        if (random_n>0&&maxiter==1) {             
//             maxiter=random_n;
//         }
         if (!user_state_string.isEmpty()) {
             maxiter=1;             
             d.random=0;
             d.user_state_string=user_state_string;
             d.prepare_current_state_matrix(d.random, false);
         }
         if (!output_directory.isEmpty()) {
             d.result_directory=output_directory;
             if (!util.DirExists(output_directory)) {
                 if (!util.CreateDir(output_directory)) {
                     System.out.println("Error. Unable to create the output directory:`"+output_directory);
                     System.exit(-1);
                 }
             }
         }   
             
         d.filename=filename;   
         d.get_info();
         //--Set default replicates for 0.05
         if (perm_n==0) {
             perm_n=(int)(d.info_total_possible_nodes/0.05);
         }
         d.perm_mode=perm_mode;
         d.nooutput=nooutput;
         d.edges_selection_mode=edges_selection_mode;
         d.bipartite=bipartite;
         d.save_graphml=save_graphml;
         d.min_rand_index=minrand;         
         d.remove_undefined_column=undefined; 
         d.remove_multiple_column=multiple; 
         d.replicate=perm_n;
         d.maxiter=maxiter;
         d.min_taxa=mintaxa;
         d.save_summary=save_summary;                
//         if ((d.perm_mode==4||d.perm_mode==5)&&d.edges_selection_mode==0) {
//             System.out.println("Warning. Permitted edges selection mode (-edges) for undefined permutation is :1,2 or 3.");
//             d.edges_selection_mode=1;
//         }
         
         
         if (show_matrix) d.printCharMatrix();
         
         //--If there is an associated character file, use it.
         d.load_charstate(node_information_filename);
         
        
         //--We analyse a dataset in the command-line
         if (analyse1) {             
                          
             System.out.println(d.get_info());                          
             System.out.println("Creating logfile(log.txt) in:\n"+d.result_directory);
             System.out.println("===============================================================================");
             permutation_statistics stat=new permutation_statistics(d);             
             stat.generate_statistics_new();                                   
             stat.output_csv(d.result_directory+File.separator+util.getFilename(d.filename));
             if (save_triplets) stat.reference.export_triplets(d.result_directory+File.separator+"triplets.txt","\t");
             System.out.println("===============================================================================================");              
             //stat.output_stats(stat.calculate_stat());                      
//             System.out.println("==============================================================================="); 
//             System.out.println("Nodes statistics");
//             System.out.println("==============================================================================="); 
//             for (int i=0; i<stat.reference_data.nodes.size();i++) {
//                 System.out.println(i+"\t"+stat.reference_data.nodes.get(i)+"\t");
//                 stat.output_stats(stat.calculate_stats_for_node(i));
//             }
//             
//              System.out.println("===============================================================================");
             System.out.println("done.");
             System.exit(0);
         }
         if (analyse2) {             
             System.out.println(d.get_info());                          
             System.out.println("Creating logfile(log.txt) in:\n"+d.result_directory);
             System.out.println("===============================================================================");
             permutation_statistics_undefined stat=new permutation_statistics_undefined(d);             
             stat.generate_statistics();                                   
             
             stat.output_csv(d.result_directory+File.separator+util.getFilename(d.filename));
             if (save_triplets) stat.reference.export_triplets(d.result_directory+File.separator+"triplets.txt","\t");
             System.out.println("===============================================================================================");              
             
             System.out.println("done.");
             System.exit(0);
         }
      System.out.println("normal exit.");
      
      } else {
          //--Gui version - No Argv -
          try {
          EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {                      
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");           
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {        }
                    MainJFrame frame = new MainJFrame();
                    Locale.setDefault(new Locale("en", "US")); 
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);              
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
             });
          } catch(Exception e) {
              help();
          }
      }
    }
    //http://docs.oracle.com/javafx/2/charts/bar-chart.htm
      
     public static void help() {
         System.out.println("COMPONENT-GRAPHER v"+version+"\n"+authors); 
         System.out.println("========================== COMMAND-LINE OPTIONS ===============================");
         System.out.println("Usage:\n");
          
          System.out.println("java -Xms2g -Xmx8g COMPONENT-GRAPHER.jar matrixfile \n");
          
          System.out.println("Arguments:");
          System.out.println("\tmatrixfile : a nexus or phylip matrix to analyse.");      
          
          System.out.println("Options :");
          //System.out.println("\t-taxa=list   : Specify some taxas tagged in the summary file\n\t\t\t(list separated by comma e.g. A,B,C).");
          System.out.println("\t-perm=100    : Specify the number permutation to performed.");  
          System.out.println("\t-permmode=0  : Specify the permutation mode");
           System.out.println("\t           0: Equiprobable permutation (default)");
           System.out.println("\t           1: Probabilistic permutation");
           System.out.println("\t           2: Phylogeny (default if -tree option is used)");
           //System.out.println("\t           3: Bootstrap");                 
           System.out.println("\t           4: Equiprobable- only permute undefined states"); 
           System.out.println("\t           5: Probabilistic- only permute undefined states"); 
           
          System.out.println("\t-threads=10  : Specify the number of concurrent threads.");          
          //System.out.println("\t-und         : Perform randomization on undefined states (?,*).");          
          System.out.println("\t-edges=0     : Specify edge's inference mode ");
          System.out.println("\t            0: Treat all edges (default)");
          System.out.println("\t            1: Absolute Majority");                 
          System.out.println("\t            2: Majority Rule");          
          System.out.println("\t          >10 : Minimum percent to have edge");                    
          //System.out.println("\t-mintaxa=9[%]: The minimum number of taxa (or percent) to include one edge.");          
          //System.out.println("\t-maxiter=9   : Maximum number of variations to search in case of \n\t\t\tundefined states in the input matrix (e.g. {1,2,3})\n\t\t\t*Note: the first 1000 iterations are ordered and not \n\t\t\trandom. (default=1). ");
          //System.out.println("\t-random=9    : Force a number of random variations.");
          System.out.println("\t-undefined   : Remove column containing undefined states (e.g. ?,*)");
          System.out.println("\t-multiple    : Remove column containing multiple states (e.g. {1,2,3}).");          
          System.out.println("\t-bipartite   : Output bipartite files.");
          System.out.println("\t-graphml     : Output graphml files (Gephi,Cytoscape compatibles).");
          System.out.println("\t-nodeid=file : Provide a node identification file.");
          System.out.println("\t-output=dir  : Specify output directory.");
          System.out.println("\t-variation=X : Specify the variation string to use.");
          System.out.println("\t-triplets    : Output triplets file (triplets.txt).");
          //System.out.println("\t-summary     : Compute summary statistic such as degrees, betweenness.");
         
          System.out.println("================================= OUTPUTS =====================================");          
          System.out.println("matrixfile_complete.txt               : edge list of the complete network.");
          System.out.println("matrixfile_1.txt                      : edge list of the type 1 connections.");
          System.out.println("matrixfile_2.txt                      : edge list of the type 2 connections.");
          System.out.println("matrixfile_3.txt                      : edge list of the type 3 connections.");
          System.out.println("matrixfile_4.txt                      : edge list of the type 4 connections.");
          System.out.println("matrixfile_id.txt                     : identification for each node.");
          System.out.println("matrixfile_summary.txt                : statistics and parameters for this run.");
          System.out.println("matrixfile_summary_statistics.csv     : nodes informations.");
          System.out.println("matrixfile_network_statistics.csv     : network statistics and p-value.");
          System.out.println("matrixfile_nodes_statistics.csv       : nodes statistics and p-value.");
                   
          System.out.println("log.txt                               : logfile");
          System.out.println("reference.json                        : serialized results for original dataset.");
          System.out.println("randomization_XX.json                 : serialized results for each permuation.");
          System.out.println("\nIf the [-bipartite] option is use, the following files will also be produced:");
          System.out.println("\tmatrixfile.bipartite_complete.txt: bipartite network of the\n\t\t\t\t\t\tcomplete network.");
          System.out.println("\tmatrixfile.bipartite_1.txt       : bipartite network of the type 1.");
          System.out.println("\tmatrixfile.bipartite_2.txt       : bipartite network of the type 2.");
          System.out.println("\tmatrixfile.bipartite_3.txt       : bipartite network of the type 3.");          
          System.out.println("\tmatrixfile.bipartite_id.txt      : identification for each node");
          System.out.println("\nIf the [-graphml] option is used, the following files will also be produced:");
          System.out.println("\tmatrixfile_complete.graphml");
          System.out.println("\tmatrixfile_1.graphml");
          System.out.println("\tmatrixfile_2.graphml");
          System.out.println("\tmatrixfile_3.graphml");
          System.out.println("\tmatrixfile_4.graphml");
          System.out.println("\nIf the [-triplets] option is used, the following file will also be produced:");
          System.out.println("\ttriplets.txt                     : triplets found in type 3 network");
          System.out.println("===============================================================================");
          
          System.exit(0);
    }
    
}
