package COMPONENT_GRAPHER;

import java.util.Locale;

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

/**
 * This will find biological intersection of character-staces 
 * -Note: new version with the change in node
 * IMPORTANT: This code use the Stochastic Simulation in Java (SSJ)
 *            from Pierre L'Ecuyer, in the Département d'Informatique 
 *            et de Recherche Opérationnelle (DIRO), 
 *            at the Université de Montréal.
 * https://github.com/umontreal-simul/ssj
 * @author Etienne Lord, Jananan Pathmanathan
 * @since October/November 2015
 */
public class main_oldmain {

        ///////////////////////////////////////////////////////////////////////
        /// VERSION
        //public static String version="1.0.9";
        //public static String authors="Etienne Lord, Jananan Pathmanathan, Vladimir Makarenkov,\nFrançois-Joseph Lapointe, Éric Bapteste";
        
    /**
     * This is the main_oldmain class which parse the different args parameters
 and applied the algorithms.
     * @param args the command line arguments
     */
    @Deprecated
    //--See main_oldmain
    
        public static void main(String[] args) {
         Locale.setDefault(new Locale("en", "US"));
        ///////////////////////////////////////////////////////////////////////
        /// FLAGS
        boolean analyse1=false;
        boolean save_graphml=false;   //--Save output as graphML
        boolean save_bipartite=false; //--Compute and save bipartite networks
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
        int perm_n=100;            //--Number of permutation for the p-value           
        String user_state_string="";//--passed user state string
        String node_information_filename="";
      
        //--Main dataset to compute 
      datasets d=new datasets();
        
      //--Test for arguments
      if (args.length==0||args[0].equalsIgnoreCase("-h")||args[0].equalsIgnoreCase("-help")) help();
      String filename=args[0];
      //--Display program info      
      d.commandline=util.string(args);
      //System.out.println("COMPONENT-GRAPHER v"+version+"\n"+authors);
        
        // Read command line option
        for (String st:args) {
            String s=st.toLowerCase();
            System.out.println(s);
            if (s.indexOf("-output=")>-1) filename=st.substring(8);         
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
          
            if (s.indexOf("-random=")>-1) random_n=Integer.valueOf(st.substring(8));
            if (s.indexOf("-variation=")>-1) user_state_string=st.substring(11);
            if (s.indexOf("-undefined")>-1) undefined=true;
            if (s.indexOf("-multiple")>-1) multiple=true;            
            if (s.indexOf("-show_matrix")>-1) show_matrix=true;            
            if (s.indexOf("-graphml")>-1) save_graphml=true;
            if (s.indexOf("-nodeid=")>-1) node_information_filename=s.substring(8);
            if (s.indexOf("-taxa=")>-1) d.taxa=s.substring(6);
            if (s.indexOf("-nooutput")>-1) nooutput=true;
            if (s.indexOf("-summary")>-1) save_summary=true; 
            //--Given a graph, analyse it...
            if (s.indexOf("-analyse")>-1) {
                analyse1=true;
                //--1. check if we refer to a graph or a series of graph
                //analyze_multiple an=new analyze_multiple();
                //an.process_path(s.substring(9));
                //--This only printout summary permutation_statistics for this graph.
                //d.analyse(s.substring(9),1);
                //System.exit(0);
            }
            if (s.indexOf("-bipartite")>-1) {
             bipartite=true;
            }
        }         
         
        
          boolean r=d.load_morphobank_nexus(args[0]);               
        
          if (!r||d.nchar==0||d.ntax==0) r=d.load_simple(filename);
         if (!r||d.nchar==0||d.ntax==0) {
             System.out.println("Unable to load : "+filename+". No file found?");
             System.exit(-1);
         }
      
         
        
        if (random_n>0&&maxiter==1) {             
             maxiter=random_n;
         }
         if (!user_state_string.isEmpty()) {
             maxiter=1;
             random_n=0;             
         }
         
         d.filename=filename;
         d.random=random_n;
         d.nooutput=nooutput;
         d.bipartite=bipartite;
         d.save_graphml=save_graphml;
         d.min_rand_index=minrand;         
         d.remove_undefined_column=undefined; 
         d.remove_multiple_column=multiple; 
         d.maxiter=maxiter;
         d.min_taxa=mintaxa;
         d.save_summary=save_summary;
         d.user_state_string=user_state_string;
         if (show_matrix) d.printCharMatrix();
         
         //--If there is an associated character file, use it.
         d.load_charstate(node_information_filename);
          
         
         
             permutation_statistics stat=new permutation_statistics(d);
             stat.generate_statistics_new();
         
      System.out.println("normal exit.");
      System.exit(0);
    }
    
    /**
     * This print to stdout some help and exit.
     */
    public static void help() {
        // System.out.println("COMPONENT-GRAPHER v"+version+"\n"+authors); 
         System.out.println("========================== COMMAND-LINE OPTIONS ===============================");
         System.out.println("Usage:\n");
          
          System.out.println("java -Xms2g -Xmx8g COMPONENT-GRAPHER.jar matrixfile \n");
          
          System.out.println("Arguments:");
          System.out.println("\tmatrixfile : a nexus or phylip matrix to analyse.");      
          
          System.out.println("Options :");
          System.out.println("\t-tree=file   : Specify phylogenetic tree in newick format.");
          System.out.println("\t-taxa=list   : Specify some taxas tagged in the summary file\n\t\t\t(list separated by comma e.g. A,B,C).");
          System.out.println("\t-mintaxa=9[%]: The minimum number of taxa (or percent) to include one edge.");
          System.out.println("\t-maxiter=9   : Maximum number of variations to search in case of \n\t\t\tundefined states in the input matrix (e.g. {1,2,3})\n\t\t\t*Note: the first 1000 iterations are ordered and not \n\t\t\trandom. (default=1). ");
          System.out.println("\t-random=9    : Force a number of random variations.");
          System.out.println("\t-undefined   : Remove column containing undefined states (e.g. ?,-)");
          System.out.println("\t-multiple    : Remove column containing multiple states (e.g. {1,2,3}).");          
          System.out.println("\t-bipartite   : Output bipartite file.");
          System.out.println("\t-graphml     : Output graphml file (Gephi compatible).");
          System.out.println("\t-nodeid=file : Provide a node identification file.");
          System.out.println("\t-output=file : Specify output filename.");
          System.out.println("\t-variation=X : Specify the variation string to use.");
          System.out.println("\t-summary     : Compute summary statistic such as degrees, betweenness.");
         
          System.out.println("================================= OUTPUTS =====================================");
          System.out.println("\nFor each iteration (see maxiter parameter) :\n");
          System.out.println("\tmatrixfile_XXX_complete.txt: edge list of the complete network.");
          System.out.println("\tmatrixfile_XXX_1.txt       : edge list of the type 1 connections.");
          System.out.println("\tmatrixfile_XXX_2.txt       : edge list of the type 2 connections.");
          System.out.println("\tmatrixfile_XXX_3.txt       : edge list of the type 3 connections.");
          System.out.println("\tmatrixfile_XXX_4.txt       : edge list of the type 4 connections.");
          System.out.println("\tmatrixfile_XXX_id.txt      : identification for each node");
          System.out.println("\tmatrixfile_XXX_stat.txt    : statistics and parameters for this run.");
          System.out.println("\t*XXX will be replace by the iteration number if there is multiple states. ");
          System.out.println("\nIf the [-bipartite] option is use, the following files will also be produced:");
          System.out.println("\tmatrixfile.bipartite_XXX_complete.txt: bipartite network of the\n\t\t\t\t\t\tcomplete network.");
          System.out.println("\tmatrixfile.bipartite_XXX_1.txt       : bipartite network of the type 1.");
          System.out.println("\tmatrixfile.bipartite_XXX_2.txt       : bipartite network of the type 2.");
          System.out.println("\tmatrixfile.bipartite_XXX_3.txt       : bipartite network of the type 3.");          
          System.out.println("\tmatrixfile.bipartite_XXX_id.txt      : identification for each node");
          System.out.println("\nIf the [-summary] option is use, the following file will also be produced:");
          System.out.println("\tmatrixfile_XXX_summary.txt           : summary statistics for this run.");
          System.out.println("\nIf the [-graphml] option is used, the following file will also be produced:");
          System.out.println("\tmatrixfile_XXX_complete.graphml");
          System.out.println("\tmatrixfile_XXX_1.graphml");
          System.out.println("\tmatrixfile_XXX_2.graphml");
          System.out.println("\tmatrixfile_XXX_3.graphml");
          System.out.println("\tmatrixfile_XXX_4.graphml");
          System.out.println("===============================================================================");
          
          System.exit(0);
    }
}
