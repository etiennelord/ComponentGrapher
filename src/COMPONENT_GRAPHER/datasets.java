package COMPONENT_GRAPHER;
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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import umontreal.iro.lecuyer.rng.LFSR258;
import umontreal.iro.lecuyer.util.BitVector;
import config.Config;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;

/**
 * This is a new version 
 * The code to compute the graph is included in this class
 * The edge are now 2 array of integer : src_edge, dst_edge
 * @author Etienne Lord, Jananan Pathmanathan
 * @since October/November 2015, 2016
 */
public class datasets extends Observable implements Serializable {
    
    transient public static Config config=new Config();
    
    transient static LFSR258  rand=new LFSR258();  
    
   /////////////////////////////////////////////////////////////////////////////
   /// Colors of links (edges)
    
    // Type1 17,255,17GREEN
    // Type2 0,98,203 BLUE
    // Type3 255,42,80 RED
    
    
   /////////////////////////////////////////////////////////////////////////////
   /// OPTIONS 
   public double min_rand_index=0.0;
   public double min_taxa_percent=0;
   public double max_taxa_percent=1;
   public double min_taxa=1;
   public static int maxthreads=2;
   
   //--p-value min (see compute_nodes()
    public double p05=1.0;
    public double p01=1.0;
    public double p001=1.0;
    
   public int random=0; 
   public int replicate=10; //--Bootstrap replicate
   public boolean permutation=true;
   public boolean bootstrap=false;
   public boolean save_graphml=false;
   public boolean nooutput=true; //--Don't save intermediate
   public boolean remove_undefined_column=false;//f
   public boolean remove_multiple_column=false; //f
   public boolean bipartite=false;
   public boolean save_summary=false;   
   public int bipartite_type=0;
   public int bipartite_index=0;
   public int current_bipartite=0;
   public String user_state_string="";
   public HashMap<String,Integer> bipartite_node_id=new HashMap<String, Integer>();
   public String taxa="";
   
   public boolean analysed=false; //--We have analysed the dataset?
   public boolean node_computed=false; //--Set to false to recompute
   public boolean inverse_matrix_table=true; //--Tag to change the table interface
   public boolean display_char_numbering=false; //--Tag to display the char numbering
   
   
   public String commandline="";
   public int maxiter=1;
   transient util output_biparition_complete=new util();
   transient util output_biparition_1=new util();
   transient util output_biparition_2=new util();
   transient util output_biparition_3=new util();
   public StringBuffer  st_option=new StringBuffer(); //--Selected options
   public StringBuffer  st_results=new StringBuffer(); //--Selected options
   public boolean  nexus=false;

   /////////////////////////////////////////////////////////////////////////////
   /// VARIABLES - Datasets
   public ArrayList<String> charlabels=new ArrayList<String>();
   //public ArrayList<ArrayList<String>> statelabels=new ArrayList<ArrayList<String>>();
   public ArrayList<HashMap<String,String>> statelabels=new ArrayList<HashMap<String,String>>();
   public HashMap<String, Integer> index_label=new HashMap<String, Integer>();
   public ArrayList<String> label=new ArrayList<String>(); //--Taxa name
   public ArrayList<String> state=new ArrayList<String>(); //--line of char. for the taxa 
   public static Pattern isNumbers=Pattern.compile("^\\s{0,}([0-9]{1,})\\s{1,}([0-9]{1,})$");   
   
   
   public String title="";
   public String filename="";       //--Original matrix file 
   public String serial_file="";    //--file use for the serialization of dataset
   public String result_directory="";
   public String symbols="";       //SYMBOLS="012345";   
   public int ntax=0;
   public int nchar=0;	
   public int total_valid_column=0; //--Valid columns (characters) 
   public int max_char_state=1; //--Maximum char state found for a taxon e.g. {1,2,3} =3
   public String char_matrix[][]; //--This is the original data matrixd minus the {}
   public int mode=0;
    /////////////////////////////////////////////////////////////////////////////
   /// VARIABLES - Datasets
   //--This is for rapid access to node type
   public ArrayList<HashMap<Integer,Integer>> node_id_type=new ArrayList<HashMap<Integer,Integer>>();
   
   public ArrayList<Integer> undefined_column=new ArrayList<Integer>();
   public ArrayList<Integer> multiple_column=new ArrayList<Integer>();
   
   //public int char_state[]; //--char state for this partition
   /////////////////////////////////////////////////////////////////////////////
   /// STATES
   int total_state_id=0; 
   public float total_states=1;
   public boolean save_inter_result=true;
   transient PrintWriter pw_output;
   
   public ArrayList<state> states=new ArrayList<state>(); //--Multiple-states positions (i,j)and information in the matrix
   public ArrayList<String> state_strings=new ArrayList<String>(); //--StateString for this char matrix        
   public String current_state_matrix[][]; 
   public String current_state_variation;
    /////////////////////////////////////////////////////////////////////////////
    /// INFO FROM get_Info()
       public int info_total_undefined=0; // ? or -
       public int info_total_multiple=0; // {1,2}
       public int info_total_undefined_column=0;
       public int info_total_multiple_column=0; // {1,2}
       public int info_total_multistate=this.states.size();
       public int info_total_variation=(int)this.total_states;      
       public int info_Ntaxa=this.ntax;
       public int info_Nchar=this.nchar;
       public int info_total_valid_column=0;
       public int ìnfo_total_variation_tested=this.maxiter;
       public int info_total_possible_nodes=0; //--Used to estimate the number of replicates

    /////////////////////////////////////////////////////////////////////////////
   /// Constant
     public static String[] m_type={"","perfect concomittant","inclusion","overlap","disjoint","inclusion"};
   
   /////////////////////////////////////////////////////////////////////////////
   /// Results for this datasets
 
   public static ArrayList<ArrayList<Integer>>precomp_partitions[]; //Precalculated partition   
  
   public int current_total_edge=0; //--Current computed network total edges
   public int current_total_iter=0; //--Max iter, determined by total states and setted maxiter
   public int total_edges=0; //--Total possible edges base on number of nodes and maxiter
   public int[] src_edge;    //--Array of edges src
   public int[] dest_edge;   //-Array of edges dest
   public int[] type_edge;  //--Array of edges type (1,2,3)
   public int[] taxa_edge;  //--Array of number of taxa on the edges
   public int[] count_edge; // For multiple_edge, the stengh of the link , number of time we find the edge
   
   //--Special handle for type 4 since we don't want to mix them.
   public int[] type4_src_edge;
   public int[] type4_dest_edge;  
   public int type4_total_edge=0;
   
   public ArrayList<node> nodes=new ArrayList<node>(); //--The nodes of the networks
   public ArrayList<node> undefined_nodes=new ArrayList<node>(); //--nodes with ? caracter for a column to remove type 4 edges
   
   //--Statistics
   public ConcurrentHashMap<String,Integer> identification=new ConcurrentHashMap<String,Integer>();
   public ConcurrentHashMap<Integer,String> inv_identification=new ConcurrentHashMap <Integer,String>();
   
   public int total_type0=0;
   public int total_type1=0;
   public int total_type2=0;
   public int total_type3=0;
   public int total_type4=0;
   
  transient Callable callback=null;

/////////////////////////////////////////////////////////////////////////////
   /// FUNCTIONS    

   
   public datasets() {}
   
    /**
     * Copy constructor
     * Note: implemet better version using serialization
     * @param d the obkject that we need to clone
     */
    public datasets(datasets d) {
        super();
        //--Copy without extra allocation
        
        this.total_edges=d.total_edges; //--Important for the deep copy
         //--Allocate memory
       allocate_edges_memory();
       
       
        
        this.current_state_variation=d.current_state_variation;
        this.callback=d.callback;
        this.node_computed=d.node_computed;
        //--Configuration
        this.min_rand_index=d.min_rand_index; 
        this.min_taxa_percent=d.min_taxa_percent;
        this.max_taxa_percent=d.max_taxa_percent;
        this.min_taxa=d.min_taxa;
        this.random=d.random;        
        this.replicate=d.replicate;
        
        this.title=d.title;
       this.filename=d.filename;      
       this.serial_file=d.serial_file;
       this.result_directory=d.result_directory;
       this.ntax=d.ntax;
       this.nchar=d.nchar;
       this.total_valid_column=d.total_valid_column; 
       this.max_char_state=d.max_char_state;

       this.permutation=d.permutation;
       this.save_graphml=d.save_graphml;
       this.nooutput=d.nooutput;
       this.remove_undefined_column=d.remove_undefined_column;
       this.remove_multiple_column=d.remove_multiple_column;
       this.bipartite=d.bipartite;
       this.save_summary=d.save_summary;
       this.bipartite_type=d.bipartite_type;
       this.bipartite_index=d.bipartite_index;
       this.current_bipartite=d.current_bipartite;
       this.user_state_string=d.user_state_string;   
       this.taxa=d.taxa;
       this.inverse_matrix_table=d.inverse_matrix_table;
       this.display_char_numbering=d.display_char_numbering;
       this.commandline=d.commandline;
       this.maxiter=d.maxiter;
       this.st_option.append(d.st_option.toString());    
       this.st_results.append(d.st_results.toString());
       this.nexus=d.nexus;
       this.mode=d.mode;
     /////////////////////////////////////////////////////////////////////////////
       /// STATES
       this.total_state_id=d.total_state_id;
       this.total_states=d.total_states;
       this.save_inter_result=d.save_inter_result;
       this.analysed=d.analysed;
  
    /////////////////////////////////////////////////////////////////////////////
    /// INFO FROM get_Info()
       this.info_total_undefined=d.info_total_undefined; // ? or -
       this.info_total_multiple=d.info_total_multiple; // {1,2}
       this.info_total_undefined_column=d.info_total_undefined_column;
       this.info_total_multiple_column=d.info_total_multiple_column; // {1,2}
       this.info_total_multistate=d.info_total_multistate;
       this.info_total_variation=d.info_total_variation;
       this.info_Ntaxa=d.info_Ntaxa;
       this.info_Nchar=d.info_Nchar;
       this.info_total_valid_column=d.info_total_valid_column;
       this.ìnfo_total_variation_tested=d.info_total_variation;
       this.info_total_possible_nodes=d.info_total_possible_nodes;
        this.current_total_edge=d.current_total_edge;
       this.current_total_iter=d.current_total_iter; 

       this.total_type0=d.total_type0;
       this.total_type1=d.total_type1;
       this.total_type2=d.total_type2;
       this.total_type3=d.total_type3;
       this.total_type4=d.total_type4;
   
      

       for (String k:d.index_label.keySet()) {
           this.index_label.put(k, d.index_label.get(k));
       }
       this.node_id_type.clear();
        //System.out.println(d.node_id_type);
      try {
        for (int i=0; i<=4; i++) {
         HashMap<Integer,Integer> ni=d.node_id_type.get(i);
           HashMap<Integer,Integer> tmp=new HashMap<Integer,Integer>();
           for (int k:ni.keySet()) {
               tmp.put(k, ni.get(k));               
           }
           this.node_id_type.add(tmp);
       }
      } catch(Exception ea) {
        //--This might failed?
    }
       
       this.undefined_column.addAll(d.undefined_column);
       this.multiple_column.addAll(d.multiple_column);
       for (String s:d.bipartite_node_id.keySet()) {
           this.bipartite_node_id.put(s, d.bipartite_node_id.get(s));
       }
       
   
        //--Deep Copy with memory allocation
       Enumeration<String> kk=d.identification.keys();
       while(kk.hasMoreElements()) {
           String k=kk.nextElement();
           this.identification.put(k, d.identification.get(k));
       }
        Enumeration<Integer> kkk= d.inv_identification.keys();
       while(kkk.hasMoreElements()) {
           Integer k=kkk.nextElement();
           this.inv_identification.put(k, d.inv_identification.get(k));
       }
       for (state s:d.states) {
           this.states.add(new state(s));
       }       
       this.charlabels.addAll(d.charlabels);
       this.state.addAll(d.state);
       this.label.addAll(d.label);
       this.state_strings.addAll(d.state_strings);
       //--Deepcopy state label
       for (HashMap<String,String> s:d.statelabels) {
           HashMap<String,String> tmp=new HashMap<String,String>();
           for (String k:s.keySet()) {
           tmp.put(k, s.get(k));
           }           
           this.statelabels.add(tmp);
       }
       
       for (node n:d.nodes) {
           this.nodes.add(new node(n));
       }
       
      //--Critical values
         this.p05=0.05/(double)d.nodes.size(); //--critical values
         this.p01=0.01/(double)d.nodes.size();
         this.p001=0.001/(double)d.nodes.size();      
         
       for (node n:d.undefined_nodes) {
           this.undefined_nodes.add(new node(n));
       }
       
       this.current_state_matrix=new String[d.ntax][d.nchar]; 
       this.char_matrix=new String[d.ntax][d.nchar]; 
       
       for (int i=0; i<d.ntax;i++) 
           for (int j=0; j<d.nchar;j++) {
               this.current_state_matrix[i][j]=d.current_state_matrix[i][j];
               this.char_matrix[i][j]=d.char_matrix[i][j];
           }
       
        for (int i=0; i<d.type4_total_edge;i++) {
            this.type4_dest_edge[i]=d.type4_dest_edge[i];
            this.type4_src_edge[i]=d.type4_src_edge[i];            
        }
        
        for (int i=0; i<d.total_edges;i++) {               
           this.src_edge[i]=d.src_edge[i];    //--Array of edges src
           this.dest_edge[i]=d.dest_edge[i];   //-Array of edges dest
           this.type_edge[i]=d.type_edge[i];  //--Array of edges type (1,2,3)
           this.taxa_edge[i]=d.taxa_edge[i];
           this.count_edge[i]=d.count_edge[i];
        }
        
        
        
    }
   
    /***
     * Randomly permute the value in i and j
     * @param col i.e. character p.e. pied 
     */
    private void permute(int col) {
        int index_i=rand.nextInt(0,ntax-1);        
        int index_j=rand.nextInt(0,ntax-1);
        while (index_j==index_i) {
            index_j=rand.nextInt(0,ntax-1);
        }
       
        String o=current_state_matrix[index_i][col];
        current_state_matrix[index_i][col]=current_state_matrix[index_j][col];
        current_state_matrix[index_j][col]=o;        
    } 
    
    /**
     * We copy a random state from this column to a new position
     * @param col
     * @param index_i 
     */
    private void bootstrap(int col, int index_i) {        
        int index_j=rand.nextInt(0,ntax-1);
        current_state_matrix[index_i][col]=current_state_matrix[index_j][col];        
    } 
    
    /**
     * From the original data, do one matrix permutation
     */
    public void generate_permutation() {
    
       //Do the random permuation
        for (int j=0; j<nchar;j++) {
            for (int i=0; i<ntax-1;i++) permute(j);
         }        
    }
    
    
    public int getTotalCharState(int character, String state) {
        int total=0;
        for (int i=0; i<this.ntax;i++) {
            String st=this.char_matrix[i][character];
            if (st.contains(state)) total++;
        }        
        return total;
    }
    
     /**
     * From the original data, do one matrix permutation
     */
    public void generate_bootstrap() {
    
       //Do the random permuation
        for (int j=0; j<nchar;j++) {
            for (int i=0; i<ntax;i++) bootstrap(j, i);
         }        
    }
   
/////////////////////////////////////////////////////////////////////////////
   /// FUNCTIONS    
   
   

   public void setCallback(Callable callback_) {
       this.callback=callback_;
   }
   
    String extract_charlabels(String line) {
        //CHARLABELS
        //		 [1] 'GEN skull, telescoping, presence'
	String num="";
	String nlabel="";
	ArrayList<String> tmp=new ArrayList<String>();
	boolean in_num=false;
	boolean in_label=false;
	
	for (int i=0; i<line.length();i++) {
		if (line.charAt(i)=='[') {
		in_num=true;
		} else if (line.charAt(i)==']') {
		 in_num=false;
		 in_label=true;
		} else if (in_num) {
		   num+=line.charAt(i);
		} else if (in_label) {
			if (line.charAt(i)!='\'') nlabel+=line.charAt(i);
		}
	}	
	return (nlabel);
    }


    ArrayList<String> extract_matrix(String line) {

            String name="";
            String matrix="";
            ArrayList<String> tmp=new ArrayList<String>();

            boolean flag_in_name=false;
            // NULL CASE
            if (line.length()==0) {
                    tmp.add("");
                    tmp.add("");
                    return tmp;
            }
            // CASE WITHOUT '
            if (line.charAt(0)!='\'') {
                    flag_in_name=true;
                    for (int i=0; i<line.length();i++) {
                            if (line.charAt(i)==' '||line.charAt(i)=='\t') {
                                    flag_in_name=false;
                            } else if (flag_in_name) {
                                    name+=line.charAt(i);
                            } else if (!flag_in_name&&line.charAt(i)!=' '&&line.charAt(i)!='\t'&&line.charAt(i)!='\'') matrix+=line.charAt(i);		
                    }
            } else 
            // CASE WITH '
            if (line.charAt(0)=='\'')
                    for (int i=0; i<line.length();i++) {
                            if (line.charAt(i)=='\'') {
                                    flag_in_name=!flag_in_name;
                            } else if (flag_in_name) {
                                    name+=line.charAt(i);
                            } else if (!flag_in_name&&line.charAt(i)!=' '&&line.charAt(i)!='\t'&&line.charAt(i)!='\'') matrix+=line.charAt(i);		
                    }
            tmp.add(name);
            tmp.add(matrix);
            return tmp;		
    }
    
    public int intmaxrow() {
 	return (this.state.size());
 }
    
public String[][] charmatrix() {
  String  mat[][]=new String[intmaxrow()][intmaxcol()];
   
  for (int i=0; i<state.size();i++) {
	int l=0;
	boolean inside=false;
	String st="";
        
	for (int j=0;j<state.get(i).length();j++) {
		
		Character c=state.get(i).charAt(j);		
		if (inside&&(c=='}'||c==')')) {
			inside=false;
			if (st.length()>this.max_char_state) this.max_char_state=st.length();
                        mat[i][l++]=st;
			st="";
		} else 
		if (inside&&(c!=','&&c!=' ')) {
			st+=c;
		} else 
		if (c=='{'||c=='(') {
			inside=true;
			st="";
		} else
		if (!inside) {
			st+=c;
			mat[i][l++]=st;
                        st="";
		}
	}
    }
   
   return mat;
}
 
  
 public Integer get_ivalue(String s) {     
     s=s.substring(0, s.length()-1); 
     return Integer.valueOf(s.split("=")[1]);
 }
  
  public Integer get_ivalue(String s, String id) {     
      Pattern p=Pattern.compile(id, Pattern.CASE_INSENSITIVE);
      Matcher m=p.matcher(s);
      if (m.find()) {          
          return Integer.valueOf(m.group(1));
      }
     return 0;
 }
  
   public String get_svalue(String s, String id) {     
      Pattern p=Pattern.compile(id, Pattern.CASE_INSENSITIVE);
      Matcher m=p.matcher(s);
      if (m.find()) {          
          return m.group(1);
      }
     return "";
 }
  
  /**
   * Given a string, will split around space or ' ' delimiter
   * @param data
   * @return 
   */
  public static ArrayList<String> get_fields(String data) {
      //String str="1 PROSTOMIUM / Tentacles Flattened Acron Absent Distinct 'Fused, distinct' 'Fused, limited' 'Fused, frontal..', 2 PERISTOMIUM / Sipunculid Acron Absent Ring Two_rings Elongate Rings_and_collar Lips_only, 3 PROS._ANT. / Absent Present, 4 PROS._ANT_DIST. / Median Lateral Median_and_Lateral, 5 PALPS / 'Absent-Innervation only' Present, 6 PALP_TYPE / Grooved Ventral_Sens., 7 GROOVED_PALPS / Grooved_Prostomial Grooved_Peristomial, 8 Prostomial_G._Palps / G.Pro.Pair G.Pro.Mul. G.Pro.Cro., 9 Peristomial_Palps / G.Peri.Pai Peri.Pap. G.Peri.Mul., 10 V.SENS._PALPS / Pro.Ven. Pro.VenLat., 11 NUCHAL_ORGANS / Absent Pits_or_grooves post._proj. Caruncle, 12 LONG._BANDS / Absent Present, 13 SEGMENTATION / A. P., 14 1st_SEGMENT / Indistinct Similar Surround_head Fused_to_head DL_to_head Elongate Arthropod, 15 1st_APPENDAGES / Same_as_following Absent T._cirri_only Noto. Neuro. Frenulate Arthropod, 16 T.CIRRI / A. P., 17 PARAPODIA / Absent Rami_similar Neuropodia_larger Tori_present Noto._ridges Spiomorph Saccocirrid, 18 D.CIRRI / Absent Cirriform Elytrae Foliacious Limited Narrow_elongate, 19 V.CIRRI / Absent Present, 20 GILLS / Absent Parapodial Dorsal_simple Dorsal_flat 'Dorsal, ant. segments' Interramal Single, 21 LATERAL_O. / A. P., 22 D._CIRRUS_O. / A. P., 23 DORSAL_O. / A. P., 24 EPID._PAP. / A. P., 25 PYG._CIRRI / Absent PresentI PresentII, 26 D.LAT._FOLDS / A. P., 27 STOMODAEUM / Sipunculid Echiurid Arthropod D._muscularised No_organ Ax._Hyper Vent._organ Absent Vent._hyper Axial_Simp. Cossurid Psammodrilid Spintherid, 28 AXIAL_JAWS / Absent Lateral_Pair 'D-V pairs' Cross_or_Cir. Single_Tooth, 29 PROVENT. / A. P., 30 VENT._ORGAN / Eversible 'Non-eversible', 31 V.HYP. / Ridged Jaws, 32 V.HYP._JAWS / Ctenognaths Labidognaths Prionognaths, 33 GULAR_M. / A. P., 34 GUT / Sipunculid Echiurid Straight Lateral_folds Side_Branches Occluded, 35 NEPHRIDIA / Metanephridia Protonephridia, 36 CIL.PHAG. / A. P., 37 META.FUSION / None Mixonephridia Metanephromixia, 38 PROT.FUS. / None Protonephromixia, 39 REPRODUCTIVE_REGION / Not_segmental Along_body Ant.ex.post.gono Restricted Ant_sterile._post.gono Arthropod Clitellate Capitellid Histriobdellid Oweniid Myzostome Questid, 40 SPERM / A. P., 41 CIRCULATION / Absent_Lim. Closed 'Ostiate, Haemocoel', 42 HEART_BODY / A. P., 43 CHAETAE / A. P., 44 CHAETAL_COMP. / A. P., 45 CH._INVER. / A. P., 46 ACICULAE / Absent Present, 47 COMPOUND_Chaetae / Absent SIngle_ligament Double_ligament Fold, 48 COMPOUND_SHAPE / Tapers Falcate Dentate Hooked, 49 CAPILLARY / A. P., 50 SPINES_1_CHAET. / A. P., 51 SPINES / A. P., 52 HOODED_CHAETAE / A. P., 53 HOOKS / Absent Falcate Dentate, 54 UNCINI / A. P., 55 SILKY_CHAETAE / A. P., 56 Coelom / A. P., 57 Circum._Nerve_Ring / A. P., 58 Annelid_Cross / A. P., 59 Molluscan_Cross / A. P., 60 Trochophore / A. P., 61 Prototroch / A. P., 62 Metatroch / A. P., 63 Ciliated_Food_Groove / A. P., 64 Oral_Brush / A. P., 65 Akrotroch / A. P., 66 Meniscotroch / A. P., 67 Telotroch / A. P., 68 Neurotroch / A. P., 69 Protoneph / A. P., 70 Apical_Plate / A. P., 	71 Downstream / A. P.";
           
      ArrayList<String> tmp=new ArrayList<String>();
      String tmps="";
      data=data+" "; // Add space to process last groups
      boolean in_flag=false;
      for (int i=0; i<data.length();i++) {
          char c=data.charAt(i);
          if (c==' '&&!tmps.isEmpty()&&!in_flag) {
              tmps=tmps.trim(); 
             if (tmps.endsWith(",")) tmps=tmps.substring(0, tmps.length()-1);
             if (tmps.endsWith("/")&&tmp.size()>1) {
                 tmp.add(tmps.substring(0, tmps.length()-1));
                 tmp.add("/");
             } else {
                tmp.add(tmps);
             } 
             tmps="";
          } else if (c=='\'') {
            in_flag=!in_flag;  
          } else if (c==','&&!tmps.isEmpty()&&!in_flag) {
             tmps=tmps.trim(); 
             if (tmps.endsWith(",")) tmps=tmps.substring(0, tmps.length()-1);
             if (tmps.endsWith("/")&&tmp.size()>1) {
                 tmp.add(tmps.substring(0, tmps.length()-1));
                 tmp.add("/");
             } else {
                tmp.add(tmps);
             } 
             tmps="";
          }
          else {
              //if (c!=' '&&!in_flag) 
                  tmps+=c;
          }
      }
      for (int i=tmp.size()-1;i>-1;i--) {
          if (tmp.get(i).isEmpty()) tmp.remove(i);
      }
      return tmp;
  }
          
          
  /**
   * Helper function for tag CHARSTATELABELS
   * @param str 
   */
  public void process_char_state(String str) {
      
      if (str.endsWith(";")) str=str.substring(0, str.length()-1);
      int total=-1;
        // Spit string by pattern
        ArrayList<String> tmp=get_fields(str);   
        //for (String s:tmp) System.out.println(s);
        //System.exit(0);
//        for (String s:tmp) if (s.equals("/")) {
//            this.charlabels.add("");            
//            total++;
//        }
        HashMap<String,String> tmps=new HashMap<String,String>();
        // start at end
        int index=0;
        while (index<tmp.size()) {
            String id=tmp.get(index++);
            String labels=tmp.get(index);
            index+=2;
            this.charlabels.add(labels);
            //--find the state
            int pos=index;
            boolean new_state=false;
            ArrayList<String>states=new ArrayList<String>();
            while (pos<tmp.size()&&!new_state) {
                String stri=tmp.get(pos);
                states.add(stri);
                pos++;
                if (stri.equals("/")) {
                    new_state=true;                   
                    states.remove(states.size()-1); //--REmove last two label
                    states.remove(states.size()-1);
                     states.remove(states.size()-1);
                     pos--;
                }
               
            }            
            index+=states.size();
            //--Debug System.out.println(labels);
            for (int i=0; i<states.size();i++) {
               int idx=tmps.size();
               String cs=""+symbols.charAt(idx);                                            
                tmps.put(cs, states.get(i));
            }
            //--Debug System.out.println(states);
            this.statelabels.add(tmps);
            tmps=new HashMap<String,String>();
        }      
        
  }
     
  /**
   * Main function to load NEXUS files
   * @param filename
   * @return 
   */
  public boolean load_morphobank_nexus(String filename) {
         //--Note, we need to reset the variables here
         states.clear();
         charlabels.clear();
         label.clear();
         statelabels.clear();
         state_strings.clear();
         symbols="";
         
         
         nexus=true;         
         HashMap<String,String> tmp_state=new HashMap<String,String>();
         this.filename=filename;
         this.result_directory=Config.currentPath+File.separator+"results"+File.separator+Config.CleanFileName(filename);
         this.serial_file=result_directory+File.separator+"results.json";
         
	 // flags!
	 boolean flag_in_matrix=false;
	 boolean flag_in_CHARLABELS=false;
	 boolean flag_in_STATELABELS=false;
         boolean flag_in_CHARSTATELABELS=false;
         boolean next_state=true;
         String tmp_char_state="";        
        int count_section=0;
  	ArrayList<String> data=loadStrings(filename);
        if (data.isEmpty()) return false;
        for (int i=0; i<data.size();i++) {
                
                String line=data.get(i);   
                line=line.replaceAll("\t", "");                
                line=line.trim();
                //System.out.println(flag_in_matrix+" "+line);
                if (!line.isEmpty()) {
                    if ((line.indexOf("DIMENSIONS")>-1||line.indexOf("dimensions")>-1)&&line.indexOf("NTAX")>-1||line.indexOf("ntax")>-1) {
                        this.ntax=datasets.this.get_ivalue(line, "ntax=([0-9]*)");
                    }
                    if (line.indexOf("SYMBOLS")>-1||line.indexOf("symbols")>-1) {
                        this.symbols=get_svalue(line, "symbols.*=.*\"(.*)\"");
                        this.symbols=this.symbols.replaceAll(" ","");                        
                        
                    }
                    if (line.indexOf("BEGIN CHARACTERS;")>-1) count_section++;
                    if ((line.indexOf("DIMENSIONS")>-1||line.indexOf("dimensions")>-1)&&line.indexOf("NCHAR")>-1||line.indexOf("nchar")>-1) this.nchar=datasets.this.get_ivalue(line, "nchar=([0-9]*)");;    		  
                    if (flag_in_matrix&&line.indexOf(";")>-1) flag_in_matrix=false;	
                     if (flag_in_CHARSTATELABELS&&(line.charAt(0)==';'||line.charAt(line.length()-1)==';'||line.endsWith(";"))) {                         
                         flag_in_CHARSTATELABELS=false;                                            
                         process_char_state(tmp_char_state+line);                         
                     }	
                    if (flag_in_CHARLABELS&&(line.charAt(0)==';'||line.charAt(line.length()-1)==';')) flag_in_CHARLABELS=false;	
                    if (flag_in_STATELABELS&&(line.charAt(0)==';'||line.charAt(line.length()-1)==';')) {
                            if (tmp_state.size()>1) {                             
                                statelabels.add(tmp_state);                                                
                        }
                        flag_in_STATELABELS=false;         		 	                       
                    }
     		// test flag	
			if (line.indexOf("CHARSTATELABELS")>-1||line.indexOf("charstatelabels")>-1) {                             
                            flag_in_CHARSTATELABELS=true;			
			} else 
                        if (line.indexOf("CHARLABELS")>-1||line.indexOf("charlabels")>-1) { 
				flag_in_CHARLABELS=true;			
			} else 
			if (line.indexOf("STATELABELS")>-1||line.indexOf("statelabels")>-1) { 
				flag_in_STATELABELS=true;	
                               
			} else
                        if (line.indexOf("MATRIX")>-1) { 
                            //Note: Matrix must be in Uppercase
                            flag_in_matrix=true;			
			} else 
			if (flag_in_matrix) {
				 //Try to extract
				ArrayList<String> d=extract_matrix(line);
				// Test if we have the label
                                Integer index=index_label.get(d.get(0));
                                if (d.get(1)==null||d.get(1).isEmpty()) {
                                    // Add to last state
                                    index=state.size()-1;
                                    state.set(index,state.get(index)+d.get(0));
                                         
                                } else {
                                    //Integer index=index_label.get(d.get(0));
                                    if (index==null) {
                                        index=label.size();
                                        index_label.put(d.get(0),index);
                                        label.add(d.get(0));
                                        state.add("");
                                    }
                                    state.set(index,state.get(index)+d.get(1));	
                                }
                         } else 
			 if (flag_in_CHARSTATELABELS) {
                           tmp_char_state+=line;
                           if (line.endsWith(";")) {
                               flag_in_CHARSTATELABELS=false;
                               process_char_state(tmp_char_state);
                           }
                        } else 
			if (flag_in_CHARLABELS) {
				 charlabels.add(extract_charlabels(line).trim());
                                 if (line.indexOf('/')>0) {
                                     //--We might have state information embedde
                                     //TODO HERE
                                 }
			 }
			else 
			if (flag_in_STATELABELS) {	
                            
                              line=line.trim();        
                              line.replaceAll("'", "");
                                if (line.charAt(0)==','||line.charAt(0)==';'||line.endsWith(",")) {                                    
				   line=line.substring(0, line.length()-1);
                                   next_state=true;
                                   if (tmp_state.size()>0) {                                       
                                       if (line.length()>1) {
                                           int index=tmp_state.size();
                                            String c=""+symbols.charAt(index);
                                            tmp_state.put(c, line);
                                       }				 	
                                       statelabels.add(tmp_state);
				 	//new state
				 	tmp_state=new HashMap<String,String>();
                                   }
				 } else {
                                    //--New state
                                    if (next_state) {
                                        next_state=false;
                                        //--Line is state number here                                        
                                    } else {
                                        //We have a state
                                        int index=tmp_state.size();
                                        //--Symbol must be set
                                        if (symbols.length()==0) {
                                            //--try to uess it
                                        } 
                                        String c=""+symbols.charAt(index);
                                        tmp_state.put(c, line);
                                    }				 	                              
				 }
				
			 } //--End statelabels
                } //--End line empty
	} //--End data 
        if (count_section>1) {
            System.err.println("Warning, more than one matrix (with tag MATRIX) found in file: "+filename);
            return false;
        }
     this.char_matrix=charmatrix();
     this.build_charstate_label(); //--Ensure annotation
    //--debug for Nexus
     //--print_state_label();
    //--Create the different states
  try {
    if (this.nchar!=0) {
        create_states();
        state_strings.add(prepare_current_state_matrix(0, false));
    } 
      
  } catch(Exception e) {
    e.printStackTrace();
      System.out.println("Unable to create char. state matrix.->"+this.filename);
      for (String k:this.index_label.keySet()) {
          Integer v=this.index_label.get(k);
          System.out.println(this.label.get(v)+"|"+this.state.get(v));
      }
      //printCharMatrix();
      return false;
  }
  return true;
}
  
  public String getCharMatrixSymbols() {
      ArrayList<String> map=new ArrayList<String>();
      for (int i=0; i<this.ntax;i++) {
          for (int j=0;j<this.nchar;j++) {
              String st=this.char_matrix[i][j];
              for (char c:st.toCharArray()) {
                  if (c!=' '&&c!='?'&&c!='*'&&c!='-') {
                      if (!map.contains(""+c)) map.add(""+c);
                  }
              }
          }
      }
      Collections.sort(map);
      String stri="";
      for (String s:map) stri+=s;
      return stri;
  }
  
void print_state_label() {
    if (charlabels.size()>0)
    for (int i=0; i<this.nchar;i++) {
        System.out.println(this.charlabels.get(i));
        if (i<this.statelabels.size()) {
            HashMap<String,String> st=this.statelabels.get(i);
            if (st!=null) {
               for (String k:util.sort((Set<String>)st.keySet())) {
                   System.out.println("\t"+k+"\t"+st.get(k));
               } 
            }
        }
    }
}
  
   public boolean load_simple(String filename) {  
       states.clear();
         charlabels.clear();
         label.clear();
         statelabels.clear();
         state_strings.clear();
         symbols="";
       nexus=false;
       this.filename=filename;
       File f=new File(Config.currentPath);
       String firetory=f.getPath();
       this.result_directory=firetory+File.separator+"results"+File.separator+Config.CleanFileName(filename);
         this.serial_file=result_directory+File.separator+"results.json";
        ArrayList<String> data=loadStrings(filename);
        if (data.isEmpty()) return false;
        for (int i=0; i<data.size();i++) {
           String line=data.get(i).trim();
           //--Do we have a phylip file ? 
           
            Matcher m=isNumbers.matcher(line);
            if (m.find()&&i==0)  {
                //--Skip first line for phylip matrix
            } else {                
                  ArrayList<String> d=extract_matrix(line);            
                  label.add(d.get(0));	
                  state.add(d.get(1));	
             }   
        }
            
        ntax=label.size();
        nchar=intmaxcol();
        this.char_matrix=charmatrix();        
        this.build_charstate_label(); //--Ensure annotation
          try {
                if (this.nchar!=0) {
                    create_states();
                    state_strings.add(prepare_current_state_matrix(0, false));
                }                 
                
              } catch(Exception e) {
                  e.printStackTrace();
                  return false;
              }         
        return true;   	
     }
     
  
  public int intmaxcol() {   
    int c=0;
    for (int i=0; i<state.size();i++) {
            int l=0;
            boolean inside=false;
            String s=state.get(i);
            for (int j=0;j<s.length();j++) {
                    Character cs=s.charAt(j);
                    if (!inside) l++;
                    if (inside&&(cs=='}'||cs==')')) inside=false;
                    if (cs=='{'||cs=='(')  inside=true;                    
            }
            //--debug System.out.println(l+"|"+s);
            if (l>c) c=l; 
    }
    return (c);
    }

  
  public static ArrayList<String> loadStrings(String filename) {
		ArrayList<String> tmp=new ArrayList<String>();
		try {
			//Change to read UTF-8 here
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)),"ISO-8859-1"));
			while (br.ready()) {
                            tmp.add(br.readLine());
                        }
                        br.close();
		} catch(Exception e) {}
		return tmp;
	}  
  
        //--Here, pos is the char position
        ArrayList<String> extract_char(int pos) {
                ArrayList<String> tmp=new ArrayList<String>();
               int l=this.ntax;
               for (int i=0; i<l;i++) {		
                       tmp.add(current_state_matrix[i][pos]);
               }
               return (tmp);
       }
        
        //--Here, pos is the char position
        //--This return the loaded matrix with possible polymorphic char.
        ArrayList<String> extract_original_char(int pos) {
                ArrayList<String> tmp=new ArrayList<String>();
               int l=this.ntax;
               for (int i=0; i<l;i++) {		
                       tmp.add(this.char_matrix[i][pos]);
               }
               return (tmp);
       } 

          //Here pos is the tax position  
          ArrayList<String> extract_char_taxa(int pos) {
                ArrayList<String> tmp=new ArrayList<String>();
               int l=this.nchar;
               for (int i=0; i<l;i++) {		
                       tmp.add(current_state_matrix[pos][i]);
               }
               return (tmp);
       }
        
      public static void CleanMemory() {
         Runtime r = Runtime.getRuntime();
         r.gc();
    }
        
       String extract_tax(int tax_pos) {
               String tmp="";
               for (int i=0;i<intmaxcol();i++) {		
                       tmp+=char_matrix[tax_pos][i];
               }
               return (tmp);
       }
 
   public static String PrintMemory() {
        String stri="System allocated memory: "+Runtime.getRuntime().totalMemory()/(1024*1024)+" MB System free memory: "+Runtime.getRuntime().freeMemory()/(1024*1024)+" MB\n"+
                    "System total core: "+Runtime.getRuntime().availableProcessors()+"\n";
         
         return stri;
    }
  
   /**
    * This COMPUTE and return information about the current matrix
    * and PROGRAM parameters
    * @return 
    */
   public String get_info() {
       
       StringBuilder str=new StringBuilder();
       //--Number of column with undefined states 
        info_total_undefined=0; // ? or -
        info_total_multiple=0; // {1,2}
        info_total_undefined_column=0;
        info_total_multiple_column=0; // {1,2}
        info_total_multistate=this.states.size();
        info_total_variation=(int)this.total_states;
        info_Ntaxa=this.ntax;
        info_Nchar=this.nchar;
        ìnfo_total_variation_tested=this.maxiter;
       
       for (int i=0; i<this.nchar;i++) {
           boolean found_undefined=false; // ? or -           
           boolean found_multiple=false;  // {1,2}
           for (int j=0; j<this.ntax;j++) {		
                         String s=char_matrix[j][i];
                         if (s.equals("?")||s.equals("-")||s.equals("*")) {
                             found_undefined=true;
                             info_total_undefined++;
                         }
                         if (s.length()>1) {
                             found_multiple=true;
                             info_total_multiple++;
                         }
           }
           if (found_undefined) {
               info_total_undefined_column++;
               undefined_column.add(i);
           }
           if (found_multiple) {
               info_total_multiple_column++;
               multiple_column.add(i);
           }
       }
         if (this.maxiter>total_states&&total_states<1000) this.maxiter=(int)total_states;
      
         // Compute possible nodes
       this.info_total_valid_column=0;
       ConcurrentHashMap<String,Integer> dummy_identification=new ConcurrentHashMap<String,Integer>();
      for (int i=0; i<this.nchar;i++) {         
         if (!(remove_multiple_column&&multiple_column.contains(i))&&!(remove_undefined_column&&undefined_column.contains(i))) {
         info_total_valid_column++;
          ArrayList<String>d=get_states_column(i);          
          for (String s:d) {            
             //--Skip undefined state
              if (!s.equals("?")&&!s.equals("-")&&!s.equals("*")) {
                //--Give the node an arbitrary name
                  String node_name=""+(i+1)+"_"+s;               
                if (!dummy_identification.contains(node_name)) {                                 
                    dummy_identification.put(node_name,0);                    
                }                
             }            
            }  
         }
      }
       this.info_total_possible_nodes=dummy_identification.size();
     //--Output to screen some information about the char matrix              
       str.append("=============================== PARAMETERS ====================================\n");       
       str.append("Command line options                 : "+this.commandline+"\n");            
       str.append("Input                                : "+this.filename+"\n");   
       str.append("Output directory                     : "+this.result_directory+"\n");   
       str.append("N taxa                               : "+info_Ntaxa+" (rows)\n");
       str.append("N characters                         : "+info_Nchar+" (columns)\n");
//--Create the various state matrix         
       str.append("Total number of multistate characters: "+ info_total_multistate+"\n");
       str.append("Total number of possible variations* : "+info_total_variation+"\n");    
       str.append("Total variations tested              : "+ìnfo_total_variation_tested+"\n");
       str.append("Permutations statistics              : "+this.replicate+"\n");   
       str.append("Suggested permutations**             : "+(int)(info_total_possible_nodes/0.05)+"\n");  
       str.append("Remove multiple state columns        : "+this.remove_multiple_column+"\n");
        str.append("Remove undefined columns             : "+this.remove_undefined_column+"\n");        

       if (this.random>0||(((float)this.maxiter)<this.total_states&&this.maxiter>1)) {
           str.append("Iteration mode                       : random"+"\n");       
       } else {
            str.append("Iteration mode                       : ordered"+"\n");   
       }
       str.append("Total column                         : "+info_Nchar+"\n");
       str.append("Total undefined column(s)            : "+info_total_undefined_column+"\n");
       str.append("Total multiple column(s)             : "+info_total_multiple_column+"\n");
       str.append("Total treated column                 : "+info_total_valid_column+"\n");
       str.append("Total undefined char                 : "+info_total_undefined+"\n");
       str.append("Total multiple char                  : "+info_total_multiple+"\n");              
       str.append("Total possible nodes                 : "+info_total_possible_nodes+"\n");   
       
       str.append("Number of thread (maxpool)           : "+this.maxthreads+"\n");  
       str.append("===============================================================================\n");       
       str.append("*  Indicate the number of state variation for polymorphic characters.\n");       
       str.append("** Indicate the minimum number of permuations for significant p-values.\n");       
        if (this.min_taxa<1) {
          //System.out.print("Minimum common shared taxa           : "+(int)(this.min_taxa*100)+"% ");
          str.append("Minimum common shared taxa           : "+(int)(this.min_taxa*100)+"% ");
          this.min_taxa=Math.ceil(this.ntax*this.min_taxa);
          //System.out.println(" ("+(int)this.min_taxa+")");
          str.append(" ("+(int)this.min_taxa+")\n");
      } else if (this.min_taxa>1) {
          //System.out.println("Minimum common shared taxa           : "+(int)this.min_taxa);
          str.append("Minimum common shared taxa           : "+(int)this.min_taxa+"\n");
      } 
      
       
        ///////////////////////////////
        if (this.remove_multiple_column) this.maxiter=1;
      if (this.maxiter>1&&remove_multiple_column) {
          //System.out.println("Max. iteration (if multiple states): "+this.maxiter);
          MessageOption("Max. iteration (if multiple states): "+this.maxiter);
      }   
       return str.toString();
   }
   /**
    * This is the main computing routine 
    * 
    * @param mode (either 0 - taxa mode (default) or 1 (char mode)
    * @param state
    * @return 
    */
  public boolean compute() {       
      //System.out.println("compute.");            
        if (this.nchar==0||this.ntax==0) {
            System.out.println("no data loaded from "+this.filename+"?");
            return false;
        }
      compute_nodes();
      allocate_edges_memory();
      MessageOption(get_info());
      //System.out.println(this.st_option);
      boolean b=compute_partition();  
       //--Analyse graph
//          if (this.maxiter>1) {
//            MessageOption("======================= CREATING SUMMARY NETWORKS =============================");
//              analyze_multiple an=new analyze_multiple();
//              an.process_path(this.filename, this.maxiter);
//              MessageOption("===============================================================================");
//          }
     //if (b) display_result("");
     return b;           
  }
  
  public void display_result() {
    
     MessageResult("===============================================================================\n");
      MessageResult("Results:\n");
      MessageResult("===============================================================================\n");
      MessageResult("Edges (total)                     : "+total_type0+"\n");
      int unassigned_node=0;
      for (node n:nodes) if (!node_id_type.get(0).containsKey(n.id)) unassigned_node++;
      MessageResult("Edges type 1 (perfect)            : "+total_type1+"\n");
      MessageResult("Edges type 2 (inclusion)          : "+total_type2+"\n");
      MessageResult("Edges type 3 (overlap)            : "+total_type3+"\n");
      MessageResult("Edges type 4 (disjoint)           : "+total_type4+"\n");
      MessageResult("\n");
      MessageResult("Total nodes evaluated             : "+nodes.size()+"\n");      
      //MessageResult("Total nodes (final)               : "+node_id_type.get(0).size()+"\n");      
      //MessageResult("Node (unassigned)                 : "+unassigned_node+"\n");  
      MessageResult("Node type 1 (perfect)             : "+node_id_type.get(1).size()+"\n");
      MessageResult("Node type 2 (inclusion)           : "+node_id_type.get(2).size()+"\n");
      MessageResult("Node type 3 (overlap)             : "+node_id_type.get(3).size()+"\n");
      MessageResult("Node type 4 (disjoint)            : "+node_id_type.get(4).size()+"\n");
      MessageResult("===============================================================================\n");
      
      if (total_states>1) {
          String sti=state_strings.get(state_strings.size()-1);
          MessageResult("States variations : "+sti+"\n");
          MessageResult("Taxon->Character(column)|Value\n");
          MessageResult("--------------------------------------------------------------------------------\n");
          
          for (int i=0; i<this.states.size();i++) {
                 state s=states.get(i);
                 //--This might fail if there is no label
                 if (this.charlabels.size()>0) {
                    MessageResult(this.label.get(s.pos_i)+"->"+(s.pos_j+1)+" ("+this.charlabels.get(s.pos_j)+")|"+sti.charAt(i)+"\n");
                 } else {
                     MessageResult(this.label.get(s.pos_i)+"->"+(s.pos_j+1)+"|"+sti.charAt(i)+"\n");
                 }
          }
      MessageResult("===============================================================================\n");
   
      }
      
      //--Output result to file
     // System.out.println(st_results);     
      
//      try {
//          PrintWriter pw=new PrintWriter(new FileWriter(new File(filename+"_stat.txt")));          
//          pw.print(st_option);
//          pw.print(st_results);
//          pw.close();
//      } catch(Exception e) {}
      
      //if (save_summary) {
//          summary stats=new summary(this);             
//          stats.calculate();
//          stats.get_node_table();
          
      //}
          
  }
  
  
  /**
   * Create the name for the node.
   * Note, the assignation of Bitvector is not done here!
   */
   public void compute_nodes() {
      
       //System.out.println("compute_nodes.");
       if (node_computed) return; //--Do not recompute node 
      ///////////////////////////////
      // Clear node 

        for (int i=0; i<5;i++) {
           node_id_type.add(new HashMap<Integer,Integer>());
       }
      //state_strings.clear();
      nodes.clear();
      undefined_nodes.clear();
      identification.clear();
      inv_identification.clear();    
      ///////////////////////////////
      // Precompute nodes id  
       this.total_valid_column=0;
      if (this.nchar==0) return; //--No data?
       for (int i=0; i<this.nchar;i++) {
         
      if (!(remove_multiple_column&&multiple_column.contains(i))&&!(remove_undefined_column&&undefined_column.contains(i))) {
         total_valid_column++;
          ArrayList<String>d=get_states_column(i);
          //--debug System.out.println(d);
          HashMap<String,String> st=null;
          try {
            st=this.statelabels.get(i);
          } catch(Exception e) {}
          //-- Iterate over possible states s (see get_states_column)
          for (String s:d) {            
             //--Skip undefined state
              if (!s.equals("?")&&!s.equals("-")&&!s.equals("*")) {
                //--Give the node an arbitrary name
                  String node_name=""+(i+1)+"_"+s;
                  String state=s;
                   if (st!=null) {
                       state=""+st.get(state);
                   }

                if (!identification.contains(node_name)) {
                    node n=new node(node_name,nodes.size());
                    if (this.charlabels.size()>0) {                        
                        n.complete_name=charlabels.get(i)+"|"+state;
                        n.char_label=charlabels.get(i);                                                
                    } else {
                        n.complete_name="char. "+(i+1)+"|"+state;
                        n.char_label="char. "+(i+1);
                    }
                    n.state_label=state;                                           
                    n.state_matrix=s;     
                    n.column=(i+1);
                    n.multistate=get_node_multistate(n);                   
                    nodes.add(n);                    
                    identification.put(n.name,n.id);
                    inv_identification.put(n.id, n.name);
                }                
            } 
//              else {
//                  //--We found an invalid caracter ?,*,-
//                  if (s.equals("?")) {
//                       String node_name=""+(i+1)+"_"+s;
//                  String state=s;
//                   if (st!=null) {
//                       state=""+st.get(state);
//                   }
//                    node n=new node(node_name,undefined_nodes.size());
//                    if (this.charlabels.size()>0) {                        
//                        n.complete_name=charlabels.get(i)+"|"+state;
//                        n.char_label=charlabels.get(i);                                                
//                    } else {
//                        n.complete_name="char. "+(i+1)+"|"+state;
//                        n.char_label="char. "+(i+1);
//                    }
//                    n.state_label=state;                                           
//                    n.state_matrix=s;     
//                    n.column=(i+1);
//                    n.multistate=get_node_multistate(n);
//                    undefined_nodes.add(n);                    
//                    //identification.put(n.name,n.id);
//                    //inv_identification.put(n.id, n.name);                             
//                 }                  
//            }           
           }  
         }
      }
      this.current_total_iter=this.maxiter;
      if (this.total_states==1) this.current_total_iter=1;
      if (this.total_states<1000&&this.maxiter>this.total_states) this.current_total_iter=(int)this.total_states;
      this.total_edges=(((nodes.size()*(nodes.size()-1))/2)*this.current_total_iter);
      //--Set the statistic values
      this.p05=0.05/(float)nodes.size(); //--critical values
      this.p01=0.01/(float)nodes.size();
      this.p001=0.001/(float)nodes.size();          
  }
  
  private void allocate_edges_memory() {
        //--debug System.out.println("allocate_edges_memory.");    
        //System.out.println("Trying to allocate memory for "+total_edges+" possible edges.");
       MessageResult("Trying to allocate memory for "+total_edges+" possible edges.\n");
       node_id_type.clear();
       for (int i=0; i<5;i++) {
           node_id_type.add(new HashMap<Integer,Integer>());
       }
        src_edge=new int[total_edges];
        dest_edge=new int[total_edges];
        type_edge=new int[total_edges];
        taxa_edge=new int[total_edges];
        count_edge=new int[total_edges];
        //--
        type4_src_edge=new int[total_edges];
        type4_dest_edge=new int[total_edges];
        
        ///////////////////////////////////
        /// Allocate edge and set as unset
       int l2=nodes.size();    
              int p=0;
              for (int i=0; i<l2;i++) {
                  for (int j=0; j<l2;j++) {
                      if (i<j) {
                        src_edge[p]=-1;                             
                        dest_edge[p]=-1;
                        type4_src_edge[p]=-1;
                        type4_dest_edge[p]=-1;                        
                        type_edge[p]=-1;
                        taxa_edge[p]=-1;                        
                        p++;
                      }
                  }
              }
       
         this.current_total_edge=0; 
         this.type4_total_edge=0;
         this.total_type0=0;
         this.total_type1=0;
         this.total_type2=0;
         this.total_type3=0;
         this.total_type4=0;         
         this.node_id_type.get(0).clear();
         this.node_id_type.get(1).clear();
         this.node_id_type.get(2).clear();
         this.node_id_type.get(3).clear();
         this.node_id_type.get(4).clear();
  }
  
  /**
   * Compute the solution from the CURRENT_STATE_MARIX
   * and STORE in node_id_type
   */
  private void compute_network_solution() {
      //System.out.println("compute_network_solution");              
      if (nodes.size()==0||this.total_valid_column==0) {          
          System.out.println("Warning. No node in analyse. (valid column(s):"+this.total_valid_column+")");
      }
      for (int i=0; i<nodes.size();i++) {                       
              //--This is new, we do it for the node
              node n=nodes.get(i);                            
              ArrayList<String> stris=extract_char(n.column-1);  
              ArrayList<String> ori_stris=extract_original_char(n.column-1);
              n.identification.clear();
              n.partition=new BitVector(stris.size());
              n.partition_with_undefined=new BitVector(stris.size()); //--Include ? char
              n.partition_with_multistate=new BitVector(stris.size()); //--TO DO
              for (int j=0; j<stris.size();j++) {
                  if (stris.get(j).equals(n.state_matrix)) {
                      n.identification.add(j+1); //To have the taxa numbering starting at 1
                      n.partition.setBool(j, true);
                      n.partition_with_undefined.setBool(j, true); //--undefined
                  }
                  if (stris.get(j).equals("?")) {
                       n.partition_with_undefined.setBool(j, true); //--undefined (test of type 4)
                  }
                  if (ori_stris.get(j).contains(n.state_matrix)) {
                      n.partition_with_multistate.setBool(j, true); //--polymorphics
                  }
              }
              
              
              n.total_taxa=util.total_bitset(n.partition);
              nodes.set(i, n);
             }                  
            int l=nodes.size();    
            int total=l*(l-1)/2;
            int total_10p=total/10;
              int k=0;
              long timerunning=System.currentTimeMillis();              
              for (int i=0; i<l;i++) {
                  for (int j=0; j<l;j++) {
                      if (i<j&&nodes.get(i).column!=nodes.get(j).column) {                                                            
                                compute_persistance_and_bipartition(nodes.get(i), nodes.get(j));
                                k++;
                                
                                if (k%total_10p==0) {
                                    if (callback!=null) {
                                        try {
                                            callback.call();
                                        }catch(Exception e){}
                                    }
                                    long elapsed=System.currentTimeMillis()-timerunning;
                                    MessageResult(k+" / "+total+" ( "+util.msToString(elapsed)+")\n");                                    
                                    CleanMemory();
                                }
                      }
                  }
              }  //--End i
                long elapsed=System.currentTimeMillis()-timerunning;
               //System.out.println(total+" / "+total+" ( "+util.msToString(elapsed)+")");    
               MessageResult(total+" / "+total+" ( "+util.msToString(elapsed)+")\n");    
               //--Set the flag of analysed data
               this.analysed=true;
               
  }
  
  //Type is the new mode October 2015
    // This include:
    // 1. the BitVector
    // 2. Computation by nodes and not by the partition
   
  public boolean compute_partition() {
      //System.out.println("compute_partition.");     
      compute_nodes();
    
       ///////////////////////////////
      // Precompute partition      
      //System.out.println("Computing partition...");
    
      //precomp_partitions=new ArrayList[this.nchar];
     
      //--Main loop for each partition
      for (int state_id=0; state_id<this.current_total_iter;state_id++) {         
            ///////////////////////////////
           // NOTE: CLEAR TAXA HERE
          String solution="";
          if (state_strings.size()<=state_id) {              
              solution=prepare_current_state_matrix(state_id, false);
             state_strings.add(solution);
          } else {
              //--This is because we can now set a solution
                solution=state_strings.get(state_strings.size()-1); 
          }
          
            ///////////////////////////////
            // Bipartition
            String solution2=""+(state_id+1);
            if (solution.isEmpty()) solution2="";
            String f=result_directory+File.separator+util.getFilename(filename)+".bipartite";
            String f2=result_directory+File.separator+util.getFilename(filename)+"_"+solution2+"_complete.txt";
            String f_complete=f+"_"+solution2+"_complete.txt";
            String f_1= f+"_"+solution2+"_1.txt";
            String f_2= f+"_"+solution2+"_2.txt";
            String f_3= f+"_"+solution2+"_3.txt";
            String f_id= f+"_"+solution2+"_id.txt";          
            if (bipartite) {
                output_biparition_complete.open(f_complete);            
                output_biparition_1.open(f_1);
                output_biparition_2.open(f_2);
                output_biparition_3.open(f_3);            
                bipartite_index=nodes.size(); //starting bipartition id
            }
         this.st_results=new StringBuffer();
          MessageResult("===============================================================================\n");
          MessageResult("Current iteration : "+(state_id+1)+"/"+this.current_total_iter+ "\nStates variations : "+this.current_state_variation+"\n(saving to: "+f2+")\n");
          MessageResult("===============================================================================\n");         
        //--Precompute the partition
         //Reset OR not? TO DO. IF maxiter is not set, we should pile up results?
          
          //--Put this inside a new function
         compute_network_solution();
               
             if (this.bipartite) {
               output_biparition_complete.close();
                output_biparition_1.close();
                 output_biparition_2.close();
                output_biparition_3.close();     
             }
                if (!nooutput) export_edgelist(result_directory+File.separator+util.getFilename(filename)+"_"+solution2);   
                if (save_graphml) {           
                     export_graphml(result_directory+File.separator+util.getFilename(filename)+"_"+solution2+"_complete",0);
                     export_graphml(result_directory+File.separator+util.getFilename(filename)+"_"+solution2+"_1",1);
                     export_graphml(result_directory+File.separator+util.getFilename(filename)+"_"+solution2+"_2",2);
                     export_graphml(result_directory+File.separator+util.getFilename(filename)+"_"+solution2+"_3",3);
                     export_graphml(result_directory+File.separator+util.getFilename(filename)+"_"+solution2+"_4",4);
                }
                
                //--This save to file
                
               // display_result(filename+"_"+solution2);
                if (this.bipartite) { 
                   MessageResult("=============================== BIPARTITION ===================================");                
                    MessageResult("Saving bipartition files to : "+f_complete);
                   MessageResult("Saving bipartition node identification to : "+f_id);
                    output_biparition_complete.open(f_id);
                    for (String m:bipartite_node_id.keySet()) {
                        String taxa=get_taxa(m);
                        output_biparition_complete.println(bipartite_node_id.get(m)+"\t\""+taxa+"\"");
                    }
                    for (node n:nodes) {
                        output_biparition_complete.println(n.id+"\t\""+n.complete_name+"\"");
                    }
                    output_biparition_complete.close();
                   MessageResult("===============================================================================");                
                 
                } //End saving bipartite graph
                
            } //--End state
           
           return true;
    }    
  
  /**
   * Output to stdout the present char. matrix
   */  
  public void printCharMatrix() {
              for (int i=0;i<ntax;i++) {
                  System.out.print(label.get(i)+"\t");
                  for (int j=0; j<nchar;j++) {
                      System.out.print(char_matrix[i][j]+"\t"); 
                  }
                  System.out.println("");
         }
    }
  
  public void printCurrentCharMatrix() {
              for (int i=0;i<ntax;i++) {
                  System.out.print(label.get(i)+"\t");
                  for (int j=0; j<nchar;j++) {
                      System.out.print(current_state_matrix[i][j]+"\t"); 
                  }
                  System.out.println("");
         }
    }
  
   public String getCurrentCharMatrix() {
       StringBuffer st=new StringBuffer();       
       for (int i=0;i<ntax;i++) {
                  st.append(label.get(i)+"\t");
                  for (int j=0; j<nchar;j++) {
                      st.append((current_state_matrix[i][j]+"\t")); 
                  }
                  st.append("\n");
         }
       return st.toString();
    }
  
   /////////////////////////////////////////////////////////////////////////////
   /// SAVE FUNCTIONS
   
   
   public boolean save_PhylipMatrix(String filename, boolean phylip_compatible) {
       String char_state_filename=util.getFilename_wo_Ext(filename)+"_charstates.txt";
       
       try {
           PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
           if (phylip_compatible) {
               pw.println("   "+this.ntax+"   "+this.nchar);
           }
           int max_name=10;
           if (!phylip_compatible) {
              for (String s:this.label) {
               if (s.trim().length()>max_name) max_name=s.trim().length();
              }
                 max_name=max_name+5;                      
           }
           String fs="%-"+max_name+"s";           
           for (int i=0; i<this.ntax;i++) {
               pw.printf(fs, this.label.get(i));
               for (int j=0; j<this.nchar;j++) {
                   String ch=this.char_matrix[i][j];
                   if (ch.length()==1) {
                       pw.print(ch);
                   } else {
                       pw.print("{");
                       for (int k=0; k<ch.length();k++) {
                           pw.print(ch.charAt(k));
                           if (k<ch.length()-1) pw.print(",");
                       }
                       pw.print("}");
                   }                   
               }
               pw.println("");
           }                      
           pw.flush();
           pw.close();
           //--Write char.state if any
           pw=new PrintWriter(new FileWriter(new File(char_state_filename)));
            if (charlabels.size()==0) {
               for( int j=0; j<this.nchar;j++) charlabels.add("Char. "+(j+1)+"");
           }
           for (int j=0; j<this.nchar;j++) {              
               //--Character
               if (j<this.statelabels.size()) {
                    HashMap<String,String> st=this.statelabels.get(j);
                    if (st!=null) {
                        ArrayList<String> aa=util.sort((Set<String>)st.keySet());
                        for (int k=0; k<aa.size();k++) {                               
                            pw.println((j+1)+"\t"+aa.get(k)+"\t"+this.charlabels.get(j)+"\t"+st.get(aa.get(k)));                                                              
                       }                            
                    }
                }
           }                   
           pw.flush();
           pw.close();
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }
       return true;
   }
   
    public boolean save_CurrentPhylipMatrix(String filename, boolean phylip_compatible) {
       String char_state_filename=util.getFilename_wo_Ext(filename)+"_charstates.txt";
       
       try {
           PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
           if (phylip_compatible) {
               pw.println("   "+this.ntax+"   "+this.nchar);
           }
           int max_name=10;
           if (!phylip_compatible) {
              for (String s:this.label) {
               if (s.trim().length()>max_name) max_name=s.trim().length();
              }
                 max_name=max_name+5;                      
           }
           String fs="%-"+max_name+"s";           
           for (int i=0; i<this.ntax;i++) {
               pw.printf(fs, this.label.get(i));
               for (int j=0; j<this.nchar;j++) {
                   String ch=this.current_state_matrix[i][j];
                   if (ch.length()==1) {
                       pw.print(ch);
                   } else {
                       pw.print("{");
                       for (int k=0; k<ch.length();k++) {
                           pw.print(ch.charAt(k));
                           if (k<ch.length()-1) pw.print(",");
                       }
                       pw.print("}");
                   }                   
               }
               pw.println("");
           }                      
           pw.flush();
           pw.close();
           //--Write char.state if any
           pw=new PrintWriter(new FileWriter(new File(char_state_filename)));
            if (charlabels.size()==0) {
               for( int j=0; j<this.nchar;j++) charlabels.add("Char. "+(j+1)+"");
           }
           for (int j=0; j<this.nchar;j++) {              
               //--Character
               if (j<this.statelabels.size()) {
                    HashMap<String,String> st=this.statelabels.get(j);
                    if (st!=null) {
                        ArrayList<String> aa=util.sort((Set<String>)st.keySet());
                        for (int k=0; k<aa.size();k++) {                               
                            pw.println((j+1)+"\t"+aa.get(k)+"\t"+this.charlabels.get(j)+"\t"+st.get(aa.get(k)));                                                              
                       }                            
                    }
                }
           }                   
           pw.flush();
           pw.close();
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }
       return true;
   }
   
     public boolean save_NexusMatrix(String filename) {
       try {
           PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
           pw.println("#NEXUS"+"\n");
           pw.println("[ File saved by COMPONENT-GRAPHER version "+config.get("version")+","+util.returnCurrentDateAndTime()+"]\n");
           pw.println("BEGIN TAXA;");
           pw.println(" \t"+"DIMENSIONS NTAX="+this.ntax+";");
           pw.println(" \t"+"TAXLABELS");
           for (int i=0; i<this.ntax;i++) {               
               pw.println("\t\t'"+this.label.get(i)+"'");
           }
           pw.println("\t\t;");
           pw.println("ENDBLOCK;\n");
           pw.println("BEGIN CHARACTERS;");
           pw.println("\tDIMENSIONS NCHAR="+this.nchar+";");
           pw.println("\tFORMAT DATATYPE=STANDARD MISSING=? GAP=- SYMBOLS=\""+this.getCharMatrixSymbols()+"\";");
           if (charlabels.size()==0) {
               for( int j=0; j<this.nchar;j++) charlabels.add("Char. "+(j+1)+"");
           }
           pw.println("\tCHARLABELS");
           for (int j=0; j<this.nchar;j++) {
               pw.println("\t\t["+(j+1)+"] '"+this.charlabels.get(j)+"'");
           }
           pw.println("\t\t;");           
           if (statelabels.size()>0) {
            pw.println("\tSTATELABELS");
               for (int j=0; j<this.nchar;j++) {
                   pw.println("\t\t"+(j+1));
                   if (j<this.statelabels.size()) {
                        HashMap<String,String> st=this.statelabels.get(j);
                        if (st!=null) {
                            ArrayList<String> aa=util.sort((Set<String>)st.keySet());
                           for (int k=0; k<aa.size();k++) {
                               pw.print("\t\t\t"+st.get(aa.get(k)));
                               if (k!=aa.size()-1) {
                                   pw.print("\n");
                               } else {
                                   pw.print(",\n");
                               }
                               
                           }                            
                        }
                    }
               }       
            pw.println("\t;");
            }          
           pw.println("\tMATRIX");
           int max_name=10;
           for (String s:this.label) {
               if (s.trim().length()>max_name) max_name=s.trim().length();
           }
           max_name=max_name+5;           
           String fs="\t\t%-"+max_name+"s";
           for (int i=0; i<this.ntax;i++) {
               pw.printf(fs, this.label.get(i));
               for (int j=0; j<this.nchar;j++) {
                   String ch=this.char_matrix[i][j];
                   if (ch.length()==1) {
                       pw.print(ch);
                   } else {
                       pw.print("{");
                       for (int k=0; k<ch.length();k++) {
                           pw.print(ch.charAt(k));
                           if (k<ch.length()-1) pw.print(",");
                       }
                       pw.print("}");
                   }                   
               }
               pw.println("");
           }     
           pw.println("\t;");
           pw.println("ENDBLOCK;");
           pw.println("BEGIN ASSUMPTIONS;\n" +
                      "\tOPTIONS DEFTYPE = unord PolyTcount = MINSTEPS ; \n" +
                      "END;");
           pw.flush();
           pw.close();
           //--Write char.state if any
           
           
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }
       return true;
   }
     
     public boolean save_CurrentNexusMatrix(String filename) {
       try {
           PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
           pw.println("#NEXUS"+"\n");
           pw.println("[ File saved by COMPONENT-GRAPHER version "+config.get("version")+","+util.returnCurrentDateAndTime()+"]\n");           
           pw.println("BEGIN TAXA;");
           pw.println(" \t"+"DIMENSIONS NTAX="+this.ntax+";");
           pw.println(" \t"+"TAXLABELS");
           for (int i=0; i<this.ntax;i++) {               
               pw.println("\t\t'"+this.label.get(i)+"'");
           }
           pw.println("\t\t;");
           pw.println("ENDBLOCK;\n");
           pw.println("BEGIN CHARACTERS;");
           pw.println("\tDIMENSIONS NCHAR="+this.nchar+";");
           pw.println("\tFORMAT DATATYPE=STANDARD MISSING=? GAP=- SYMBOLS=\""+this.getCharMatrixSymbols()+"\";");
           if (charlabels.size()==0) {
               for( int j=0; j<this.nchar;j++) charlabels.add("Char. "+(j+1)+"");
           }
           pw.println("\tCHARLABELS");
           for (int j=0; j<this.nchar;j++) {
               pw.println("\t\t["+(j+1)+"] '"+this.charlabels.get(j)+"'");
           }
           pw.println("\t\t;");           
           if (statelabels.size()>0) {
            pw.println("\tSTATELABELS");
               for (int j=0; j<this.nchar;j++) {
                   pw.println("\t\t"+(j+1));
                   if (j<this.statelabels.size()) {
                        HashMap<String,String> st=this.statelabels.get(j);
                        if (st!=null) {
                            ArrayList<String> aa=util.sort((Set<String>)st.keySet());
                           for (int k=0; k<aa.size();k++) {
                               pw.print("\t\t\t"+st.get(aa.get(k)));
                               if (k!=aa.size()-1) {
                                   pw.print("\n");
                               } else {
                                   pw.print(",\n");
                               }
                               
                           }                            
                        }
                    }
               }       
            pw.println("\t;");
            }          
           pw.println("\tMATRIX");
           int max_name=10;
           for (String s:this.label) {
               if (s.trim().length()>max_name) max_name=s.trim().length();
           }
           max_name=max_name+5;           
           String fs="\t\t%-"+max_name+"s";
           for (int i=0; i<this.ntax;i++) {
               pw.printf(fs, this.label.get(i));
               for (int j=0; j<this.nchar;j++) {
                   String ch=this.current_state_matrix[i][j];
                   if (ch.length()==1) {
                       pw.print(ch);
                   } else {
                       pw.print("{");
                       for (int k=0; k<ch.length();k++) {
                           pw.print(ch.charAt(k));
                           if (k<ch.length()-1) pw.print(",");
                       }
                       pw.print("}");
                   }                   
               }
               pw.println("");
           }     
           pw.println("\t;");
           pw.println("ENDBLOCK;");
           pw.println("BEGIN ASSUMPTIONS;\n" +
                      "\tOPTIONS DEFTYPE = unord PolyTcount = MINSTEPS ; \n" +
                      "END;");
           pw.flush();
           pw.close();
           //--Write char.state if any
           
           
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }
       return true;
   }
   
 /**
  * Export edgelist files()
  * @param filename 
  */
   public void export_edgelist(String filename) {
       //  noeud1 separateur noeud2 separateur partition_de_taxa_commune separateur directed(or undirected) separateur type_d_arete(1,23)
       try {            
            //-Type 0
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename+"_complete.txt")));                                       
            pw.println("#src_id\tdest_id\tedge_type\tnumber_common_taxa\tpercent_common_taxa");
            for (int i=0; i<this.current_total_edge;i++) {               
                    if (type_edge[i]!=-1) pw.println(""+src_edge[i]+"\t"+dest_edge[i]+"\t"+type_edge[i]+"\t"+taxa_edge[i]+"\t"+(taxa_edge[i]/this.ntax));                     
                }    
            pw.close();   
            //-Type 1
            pw=new PrintWriter(new FileWriter(new File(filename+"_1.txt")));                       
                pw.println("#src_id\tdest_id\tedge_type\tnumber_common_taxa\tpercent_common_taxa"); 
                for (int i=0; i<current_total_edge;i++) {               
                   if (type_edge[i]==1) pw.println(""+src_edge[i]+"\t"+dest_edge[i]+"\t"+type_edge[i]+"\t"+taxa_edge[i]+"\t"+(taxa_edge[i]/this.ntax));                     
                }  
            pw.close();            
            //--Type 2
            pw=new PrintWriter(new FileWriter(new File(filename+"_2.txt")));                       
                pw.println("#src_id\tdest_id\tedge_type\tnumber_common_taxa\tpercent_common_taxa");
                for (int i=0; i<current_total_edge;i++) {               
                  if (type_edge[i]==2) pw.println(""+src_edge[i]+"\t"+dest_edge[i]+"\t"+type_edge[i]+"\t"+taxa_edge[i]+"\t"+(taxa_edge[i]/this.ntax));                                        
                }  
            pw.close();                
//            //-Type 3
            pw=new PrintWriter(new FileWriter(new File(filename+"_3.txt")));                       
                pw.println("#src_id\tdest_id\tedge_type\tnumber_common_taxa\tpercent_common_taxa");
                for (int i=0; i<current_total_edge;i++) {               
                   if (type_edge[i]==3) pw.println(""+src_edge[i]+"\t"+dest_edge[i]+"\t"+type_edge[i]+"\t"+taxa_edge[i]+"\t"+(taxa_edge[i]/this.ntax));                                 
                }  
            pw.close();
            //-Type 4
            pw=new PrintWriter(new FileWriter(new File(filename+"_4.txt")));                       
                pw.println("#src_id\tdest_id\tedge_type");
                for (int i=0; i<type4_total_edge;i++) {               
                   pw.println(""+type4_src_edge[i]+"\t"+type4_dest_edge[i]+"\t4");                                 
                }  
            pw.close();
            //--Dict
            Collections.sort(nodes);
             pw=new PrintWriter(new FileWriter(new File(filename+"_id.txt")));      
                pw.println("#node_id\tcomplete_name\tchar_label\tstate_label\tn.state_in_matrix\tedgecount\tin_edgecount\tout_edgecount\tnumber_of_taxa\ttaxa");
                for (node n:nodes) pw.println(n.id+"\t"+n.complete_name+"\t"+n.char_label+"\t"+n.state_label+"\t"+n.state_matrix+"\t"+n.edgecount+"\t"+n.in_edgecount+"\t"+n.out_edgecount+"\t"+n.identification.size()+"\t"+get_taxa(n.identification));
            pw.close();

        } catch(Exception ex) {ex.printStackTrace();}
        
    }
   
   public boolean export_cytoscapejs(String filename, int type) {
         try {      
             // for (String s:this.charlabels) System.out.println(s);
             PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));     
               pw.println("<html>");
               pw.println("<head>");
               pw.println("<style>");
               pw.println("#cy {\n" +
                          "  width: 1200px;\n" +
                          "  height: 1200px;\n" +
                          "  display: block;\n" +
                          "}");
               pw.println("</style>");
               pw.println("<script src='cola.v3.min.js'></script>");
               pw.println("<script src='cytoscape.min.js'></script>");      
             pw.println("<script src='cytoscape-cola.js'></script>");
               pw.println("</head>");
               pw.println("<body>");
               pw.println("<div id='cy'></div>");               
               
               //--Include the graph here
               pw.println("<script>var cy = cytoscape({\n" +
                            "\n" +
                            "  container: document.getElementById('cy'), // container to render in\n" +
                            "\n" +                           
                            "  elements: [ // list of graph elements to start with\n");                                           
               HashMap<Integer,Integer> nodes_id=node_id_type.get(type);
               for (node n:nodes) {                   
                   if (nodes_id.containsKey(n.id)) {
                       pw.println("{data: {id: '"+n.id+"', vname:\""+n.complete_name+"\" } },");
                   }
               }
               for (int i=0; i<current_total_edge;i++) {                  
                   if (type==type_edge[i]||type==0) {
                    switch(type_edge[i]) {
                        case 1: pw.println("{  data: { id: 'e"+i+"', source: '"+src_edge[i]+"', target: '"+dest_edge[i]+"',type: '"+type_edge[i]+"' },classes: 'type1' },"); break;
                        case 2: pw.println("{  data: { id: 'e"+i+"', source: '"+src_edge[i]+"', target: '"+dest_edge[i]+"',type: '"+type_edge[i]+"' },classes: 'type2' },"); break;
                        case 3: pw.println("{  data: { id: 'e"+i+"', source: '"+src_edge[i]+"', target: '"+dest_edge[i]+"',type: '"+type_edge[i]+"' },classes: 'type3' },"); break;
                            
                    }                      
                   }
               }
                pw.println(" ],");
                    pw.println(
                            " style: [ // the stylesheet for the graph\n" +
                            "    {\n" +
                            "      selector: 'node',\n" +
                            "      style: {\n" +                              
                           "'background-color': '#999',\n" +
                    "        'color': '#000',\n" +
                    "        'label': 'data(vname)',		\n" +
                    "        'text-valign': 'center',\n" +
                    "        'text-outline-width': 0,"+
                            "      }\n" +
                            "    },\n" +
                            "\n" +
                            "    {\n" +
                            "      selector: 'edge',\n" +
                            "      style: {\n" +
                            "        'width': 3,\n" +
                            "        'line-color': '#ccc',\n" +
                            "        'target-arrow-color': '#ccc',\n" +
                            "        'target-arrow-shape': 'triangle'\n" +
                            "      }\n" +
                            "    }\n" +
                            "  ],\n" +
                            "\n" +
                            "  layout: {\n" +
                            "    name: 'grid',\n" +
                            "    rows: 1\n" +
                            "  }\n" +
                            "\n" +
                            "});</script>");
               pw.println("<script> var params = {\n" +
                            "    name: 'cola',\n" +
                            "    nodeSpacing: 5,\n" +
                            "    edgeLengthVal: 5,\n" +
                            "    animate: true,\n" +
                            "    maxSimulationTime: 4000,\n"+
                            "    randomize: true,\n" +
                            "    maxSimulationTime: 1500\n" +
                            "  };\n" +
                            "  var layout = makeLayout();\n" +
                            "  var running = false;\n" +
                            "\n" +
//                            "  cy.on('layoutstart', function(){\n" +
//                            "    running = true;\n" +
//                            "  }).on('layoutstop', function(){\n" +
//                            "    running = false;\n" +
//                            "  });\n" +
                            "   function makeLayout( opts ){ "+
                            "    params.randomize = true;\n"+
                            //"    params.edgeLength = function(e){ return params.edgeLengthVal / e.data('weight'); };"+
                            "    for( var i in opts ){"+
                            "      params[i] = opts[i];"+
                            "    }"+
                            "    return cy.makeLayout( params );"+
                            "  }"+
                            "  layout.run();</script>");     
               pw.println("</body>");
               pw.println("</html>");
               pw.flush();
               pw.close();
         } catch(Exception e) {
             return false;
        }
         return true;
   }
   
   /**
    * Export graphml
    * @param filename
    * @param type 
    */
   public void export_graphml(String filename, int type) {
       //System.out.println("**"+this.charlabels.size());   
      
       try {      
             // for (String s:this.charlabels) System.out.println(s);
             PrintWriter pw=new PrintWriter(new FileWriter(new File(filename+".graphml")));     
               pw.println("<?xml version='1.0' encoding='UTF-8' standalone='no'?>");
               pw.println("<graphml>");
               //--Attributes
               //pw.println("<key id='k1' for='edge' attr.name='weight' attr.type='double'/>");
               pw.println("<key id='k2' for='edge' attr.name='type' attr.type='double'/>");
               //pw.println("<key id='r1' for='edge' attr.name='randindex' attr.type='double'/>");
               pw.println("<key id='k1' for='edge' attr.name='total_shared_taxa' attr.type='double'/>");
               
               //pw.println("<key id='k0' for='node' attr.name='nodeid' attr.type='string'/>");             
               pw.println("<key id='k3' for='node' attr.name='fullname' attr.type='string'/>");
               pw.println("<key id='k4' for='node' attr.name='number_of_taxa' attr.type='double'/>");               
               pw.println("<key id='k5' for='node' attr.name='partition' attr.type='string'/>");
               pw.println("<key id='k6' for='node' attr.name='total_edges' attr.type='double'/>");
               pw.println("<key id='k61' for='node' attr.name='in_edges' attr.type='double'/>");
               pw.println("<key id='k62' for='node' attr.name='out_edges' attr.type='double'/>");
               pw.println("<key id='k7' for='node' attr.name='associated_character_column' attr.type='double'/>");
               pw.println("<key id='k8' for='node' attr.name='charlabel' attr.type='string'/>");
               pw.println("<key id='k9' for='node' attr.name='statelabel' attr.type='string'/>");
               pw.println("<key id='k10' for='node' attr.name='statematrix' attr.type='string'/>");
               
               pw.println("<key id='k11' for='node' attr.name='total_taxa' attr.type='double'/>");
               pw.println("<key id='k12' for='node' attr.name='taxa_id' attr.type='string'/>");
               
               //if (type==1) {
                pw.println("<graph edgedefault='undirected' id='"+this.title+"'>"); 
               //} else {
               //    pw.println("<graph edgedefault='directed' id='"+this.title+"'>"); 
               //}
               HashMap<Integer,Integer> nodes_id=node_id_type.get(type);
               for (node n:nodes) {                   
                   if (nodes_id.containsKey(n.id)) {
                    pw.println("<node id='"+n.name+"'>");
                    pw.println("<data key='k0'>"+n.id+"</data>");
                    pw.println("<data key='k3'>"+util.removeBadGraphmlChar(n.complete_name)+"</data>");
                     
                    //pw.println("<data key='k4'>"+n.count+"</data>");                    
                   if (type!=4) {
                    pw.println("<data key='k6'>"+n.edgecount+"</data>");
                    pw.println("<data key='k7'>"+n.column+"</data>");
                    pw.println("<data key='k8'>"+util.removeBadGraphmlChar(n.char_label)+"</data>");
                    pw.println("<data key='k9'>"+util.removeBadGraphmlChar(n.state_label)+"</data>");
                    pw.println("<data key='k10'>"+n.state_matrix+"</data>");
                    pw.println("<data key='k61'>"+n.in_edgecount+"</data>");
                    pw.println("<data key='k62'>"+n.out_edgecount+"</data>");
                    pw.println("<data key='k11'>"+n.total_taxa+"</data>");
                   } else {
                     pw.println("<data key='k8'>"+util.removeBadGraphmlChar(n.char_label)+"</data>");
                     pw.println("<data key='k9'>"+util.removeBadGraphmlChar(n.state_label)+"</data>");
                     pw.println("<data key='k10'>"+n.state_matrix+"</data>");
                   }
                    pw.println("</node>");
                   }
               }      
               if (type!=4) {
                for (int i=0; i<current_total_edge;i++) {                  
                   if (type==type_edge[i]||type==0) {
                    if (type==1||type==3) {
                        pw.println("<edge directed='false' source='"+inv_identification.get(src_edge[i])+"' target='"+inv_identification.get(dest_edge[i])+"'>");
                    } else {
                        pw.println("<edge directed='true' source='"+inv_identification.get(src_edge[i])+"' target='"+inv_identification.get(dest_edge[i])+"'>");
                    }
                     pw.println("<data key='k1'>"+taxa_edge[i]+"</data>");                    
                     pw.println("<data key='k2'>"+type_edge[i]+"</data>");
                     //--TO DO
                     //pw.println("<data key='r1'>"+e.randindex+"</data>");
                     pw.println("<data key='k13'>"+m_type[type_edge[i]]+"</data>");
                    pw.println("</edge>");
                   } 
                   // if (e.type!=1) pw.println("<edge directed='true' source='"+e.source_str+"' target='"+e.dest_str+"'/>");
                    //if (e.type==1) pw.println("<edge  directed='false' source='"+e.source_str+"' target='"+e.dest_str+"'/>");
                }
               } else {
                    for (int i=0; i<type4_total_edge;i++) {
                        pw.println("<edge directed='false' source='"+inv_identification.get(type4_src_edge[i])+"' target='"+inv_identification.get(type4_dest_edge[i])+"'>");
                          pw.println("<data key='k13'>4</data>");
                           pw.println("</edge>");
                    }
               }
               pw.println("</graph>");
               pw.println("</graphml>");
               pw.close();
           } catch(Exception e) {e.printStackTrace();}
   }
   
    @Override
    public String toString() {
        
        //return "<html>Taxa (rows): <b>"+this.ntax+"</b> Characters (columns): <b>"+this.nchar+"</b> Treated columns: <b>"+this.total_valid_column+ "</b> Multistate characters: "+this.info_total_multiple+"</html>";
        return "Taxa (rows): "+this.ntax+" Characters (columns): "+this.nchar+" Treated columns: "+this.total_valid_column+ " Multistate characters: "+this.info_total_multiple;
        
    }
   
     /**
      * This must be called after the preparation of the char matrix
      * This prepate the multiples-states matrix
      */
     public void create_states() {
         this.states.clear();
         ArrayList<String> st=new ArrayList<String>();        
         this.current_state_matrix=new String[this.ntax][this.nchar];        
         for (int i=0; i<this.ntax;i++) {
             for (int j=0; j<this.nchar;j++) {
                 this.current_state_matrix[i][j]=char_matrix[i][j];
                 if (char_matrix[i][j].length()>1) {
                     state s=new state();
                     s.pos_i=i;
                     s.pos_j=j;
                     s.state_id=this.total_state_id++;
                     s.state=char_matrix[i][j];                     
                     this.total_states*=s.state.length();
                     states.add(s);                    
                     st.add(s.state);
                 }
             }
         }         
     }
     
     /**
      * This initialize the current state matrix 
      * @param state_id
      * @param rand
      * @return 
      */
     public String prepare_current_state_matrix(int state_id, boolean rand) {
          current_state_variation=""; 
          if (random>0&&random>total_states) {
              random=0;              
          } 
         
         boolean ok=false;      
         
        if (!user_state_string.isEmpty()&&user_state_string.length()>=states.size()) {
                 current_state_variation=user_state_string;                       
             try {
              for(int i=0; i<states.size();i++) {
                  state s=states.get(i);
                  if (!s.state.contains(""+current_state_variation.charAt(i))) {
                      System.out.println("Illegal user supplied variation string : "+current_state_variation); 
                      System.out.println("Available states are:");
                      this.print_state_label();
                  } else {
                    this.current_state_matrix[s.pos_i][s.pos_j]=""+current_state_variation.charAt(i);
                  }
              }
             } catch (Exception e) {
                 System.out.println("Illegal user supplied variation string : "+current_state_variation); 
                 this.print_state_label();
                 System.out.println("Available states are:");                   
             }           
        } else {
           //--random
            while (!ok) {
                current_state_variation="";         
                for (int i=0; i<states.size();i++) {
                      state s=states.get(i);
                      //--Randomly pick a state
                      LFSR258  r=new  LFSR258();                      
                      int pos=r.nextInt(0,s.state.length()-1);
                      current_state_variation+=s.state.charAt(pos);
                      this.current_state_matrix[s.pos_i][s.pos_j]=""+s.state.charAt(pos);
                      s.selected=pos;
                      HashMap<String,String> st=statelabels.get(s.pos_j);                      
                      String k=""+s.state.charAt(pos);
                      if (!rand) s.selected=-1;
                      s.state_label=k+"|"+st.get(k);
                      states.set(i, s);
                      
                      
                  }
                if (!state_strings.contains(current_state_variation)) {                   
                    ok=true;
                }
            } 
         }       
         return current_state_variation;
     }
     
    
     /**
      * Test if all the caracters are defined (no polymorphic)
      * @return 
      */
     public boolean matrixNotDefined() {
         for (state s:states) {
             if (s.selected<0) return true;
         }         
         return false;
     }
    
          /**
      * This return the possible state found in a column
      * @param j
      * @return 
      */
     public ArrayList<String> get_column(int j) {
         ArrayList<String> temp=new ArrayList<String>();
         for (int i=0; i<this.ntax;i++) {
             String d=this.char_matrix[i][j];
             temp.add(""+d);            
         }
         return temp;
     }
     
     /**
      * This return the possible state found in a column
      * @param j
      * @return 
      */
     public ArrayList<String> get_states_column(int j) {
         ArrayList<String> temp=new ArrayList<String>();
         for (int i=0; i<this.ntax;i++) {
             String d=this.char_matrix[i][j];
             for (int k=0; k<d.length();k++) {
                 char e=d.charAt(k);
                 if (!temp.contains(""+e)) temp.add(""+e);
             }
         }
         return temp;
     }
     
     /**
      * This is the main function to compute the type of link between 2 nodes
      * It updates the dataset array, and create the bipartition FILES on the fly
      * @param node1
      * @param node2 
      */
     public void compute_persistance_and_bipartition(node node1, node node2) {
                        BitVector ids=util.intersection_Bit(node1, node2);
                        int bipartite_id=bipartite_index;
                        String id=ids.toString();
                        if (!ids.equals(new BitVector(ntax))) {
                            if (bipartite_node_id.containsKey(id)) {
                                  bipartite_id=bipartite_node_id.get(id);
                            } else {
                                 bipartite_node_id.put(id,bipartite_index);
                                 bipartite_index++;
                            }
                        } 
                        
                        ArrayList<Integer> tmp=util.intersectBitResult(node1.partition,node2.partition);
                        ArrayList<Integer> tmp_undefined=util.intersectBitResult(node1.partition_with_undefined,node2.partition_with_undefined);
                        ArrayList<Integer> tmp_multistate=util.intersectBitResult(node1.partition_with_multistate,node2.partition_with_multistate);
                        int total=tmp.size();
                        int total_undefined=tmp_undefined.size();
                        int total_multistate=tmp_multistate.size();
                        
                        //--Get the type here
                        int type=4; //--default
                        if (total==0) {
                            type=4;
                        } else if (total==node1.total_taxa&&total==node2.total_taxa) {
                            type=1;                            
                        } else 
                        if (total<node2.total_taxa&&total==node1.total_taxa) {
                            type=2;
                        } else 
                        if (total<node1.total_taxa&&total==node2.total_taxa) {
                            type=5;
                        } else 
                        if (total>0) {
                            type=3;
                        }
                       
                        if (type!=4&&total>=this.min_taxa) {
                            //--Type 1,2,3
                            int source_index=node1.id;
                            int dest_index=node2.id;
                            if (type==5) {
                                type=2;
                                int tt=source_index;
                                source_index=dest_index;
                                dest_index=tt;                                
                            } 
                           if (bipartite) {
                            // Output bipartition
                                    output_biparition_complete.println(bipartite_id+"\t"+source_index+"\t"+type);
                                    output_biparition_complete.println(bipartite_id+"\t"+dest_index+"\t"+type);
                                    
                                    if (type==1) {
                                          output_biparition_1.println(bipartite_id+"\t"+source_index);
                                          output_biparition_1.println(bipartite_id+"\t"+dest_index);
                                    }
                                    if (type==2) {
                                          output_biparition_2.println(bipartite_id+"\t"+source_index);
                                          output_biparition_2.println(bipartite_id+"\t"+dest_index);
                                    }
                                    if (type==3) {
                                          output_biparition_3.println(bipartite_id+"\t"+source_index);
                                          output_biparition_3.println(bipartite_id+"\t"+dest_index);
                                    }
                           }
                            // Output persistance
                             int current_edge=current_total_edge;                             
                             current_total_edge++;
                             node1.edgecount++;
                             node2.edgecount++;
                             if (node1.id==source_index) node1.out_edgecount++;
                             if (node2.id==source_index) node2.out_edgecount++;
                             if (node1.id==dest_index) node1.in_edgecount++;
                             if (node2.id==dest_index) node2.in_edgecount++;
                            src_edge[current_edge]=source_index;
                            dest_edge[current_edge]=dest_index;
                            type_edge[current_edge]=type;
                            taxa_edge[current_edge]=total;
                           //count_edge[current_edge]=estimate_link_likelyhood(node1,node2);
                            nodes.set(node1.id, node1);
                            nodes.set(node2.id, node2);
                            
                                //--Statistics
                                total_type0++;
                                switch( type_edge[current_edge]) {
                                    case 1: total_type1++;break;
                                    case 2: total_type2++;break;
                                    case 3: total_type3++;break;
                                }
                                 node_id_type.get(type_edge[current_edge]).put(source_index, type);
                                 node_id_type.get(type_edge[current_edge]).put(dest_index, type);
                                 node_id_type.get(0).put(source_index, type);
                                 node_id_type.get(0).put(dest_index, type);                                 
                    } else if (type==4) {
                       //--Type 4  --we need to ensure that this is not the same characters (column)
                        // We furter ensure that this is not because of undefined char.
                        //--Aded February 2017 - total_undefined=0 to account for ? char.
                       if (node1.column!=node2.column&&total_undefined==0) {                         
                           type4_src_edge[type4_total_edge]=node1.id;
                           type4_dest_edge[type4_total_edge]=node2.id;
                            node_id_type.get(4).put(node1.id, 4);
                            node_id_type.get(4).put(node2.id, 4);
                           type4_total_edge++;
                           total_type4++;
                       }     
                    }
                        
    }   
     
    
    /**
     * Return the taxa_id corresponding with the List
     * but with id starting at 1
     * @param ids_one
     * @return 
     */    
    String get_taxa(ArrayList<Integer> ids_one) {
        String str="";
        for (Integer id:ids_one) {
            str+=this.label.get(id-1)+",";
        }
        if (str.length()>2) str=str.substring(0,str.length()-1);
        return str;
    }
    
    /**
     * Return the number fo taxa
     * but with id starting at 1
     * @param ids_one
     * @return 
     */    
    int get_taxa_count(ArrayList<Integer> ids_one) {
        int count=0;
        for (Integer id:ids_one) {
           count++;
        }        
        return count;
    }
    
    public String get_taxa(BitVector s) {
        String str="";
        for (int i=0; i<ntax;i++) {
            if (s.getBool(i)) str+=this.label.get(i)+",";
        }
        if (str.length()>1) str=str.substring(0,str.length()-1);
        return str;
    }
    
     /**
     * Return the number fo taxa
     * but with id starting at 1
     * @param ids_one
     * @return 
     */    
    int get_taxa_count(BitVector s) {
        int count=0;
         for (int i=0; i<ntax;i++) {
            if (s.getBool(i)) count++;
        }        
        return count;
    }
    
     public String get_taxa(String s) {
        String str="";
        int index=0;
        for (int i=s.length()-1; i>-1;i--) {
            char c=s.charAt(i);
            if (c=='0') {
              index++;  
            } else if (c=='1'){
                 str+=this.label.get(index)+",";
                index++;
            } else {
                //Nothing
            }           
        }
        if (str.length()>1) str=str.substring(0,str.length()-1);
        return str;
    }
    
    BitVector get_taxa_bit(ArrayList<Integer> ids_one) {
        BitVector b=new BitVector(this.ntax);
        for (int i:ids_one) b.setBool(i-1, true);
        return b;
    }
    
    String get_taxa_id(ArrayList<Integer> ids_one) {
        String str="";
        for (Integer id:ids_one) {
            str+=id+",";
        }
        if (str.length()>1) str=str.substring(0,str.length()-1);
        return str;
    }
    
    //-- TO DO: only generate the node once
    //--WARNING: in development, NOT functionnal!
//    public int estimate_link_likelyhood(node node1, node node2) {
//        int total=0;
//        int len=node1.partition.size();
//        //1. Take the possible sate
//        if (node1.multistate==1&&node2.multistate==1) return 0;
//        // 2. compute each bitvector to estimate the number of time a link can be done.
//        // ... we don't care now what kind of link...
//        ArrayList<BitVector>node1_bit= new  ArrayList<BitVector>();
//        ArrayList<BitVector>node2_bit= new  ArrayList<BitVector>();
//        if (node1.multistate==1) {
//            node1_bit.add(node1.partition);
//        } else {             
//            
//              ArrayList<String> s1=util.combinations(get_column(node1.column-1));
//              for (String p:s1) {
//                BitVector tmp= new BitVector(len);
//                 for (int j=0; j<len;j++) {
//                   String p2=""+p.charAt(j);
//                   if (p2.equals(node1.state_matrix)) {                      
//                      tmp.setBool(j, true);
//                  }
//                }
//                node1_bit.add(tmp);
//              }
//        }
//        
//         if (node2.multistate==1) {
//        node2_bit.add(node2.partition);
//        } else {
//            ArrayList<String> s2=util.combinations(get_column(node2.column-1));
//              for (String p:s2) {
//                BitVector tmp= new BitVector(len);
//                 for (int j=0; j<len;j++) {
//                   String p2=""+p.charAt(j);
//                   if (p2.equals(node2.state_matrix)) {                      
//                      tmp.setBool(j, true);
//                  }
//                }
//                node2_bit.add(tmp);
//              }              
//        }
//       ///--Now, calculate the correspondance
//       int total_possible=(node1_bit.size()*node2_bit.size());
////         for (BitVector b1:node1_bit) 
////           for (BitVector b2:node2_bit) 
////            if (util.intersectBitResult(b1,b2).size()>0) total++;
////            total=(100*total)/total_possible; //--compute the score
//        return total;
//    }
    
    /**
     * Given a node with a state s, evaluate the number of bitvector state
     * @param node1
     * @return 
     */
    public int get_node_multistate(node node1) {         
         int total=1;
          for (int i=0; i<this.ntax;i++) {
             String d=this.char_matrix[i][node1.column-1];
               if (d.length()>1) { 
                   if (d.indexOf(node1.state_matrix)>-1) {
                     total*=2; //--because its a binary state (either we have, or not)
                 }                    
               }            
         }         
         return total;
    }

    public boolean load_charstate(String filename) {
        if (filename.isEmpty()) return false;
        System.out.println("Loading external char.-states        : "+filename);
        ArrayList<String> tmp=util.loadStrings(filename);
        if (tmp.size()==0) {
            System.out.println("...failed to load :" +filename);
            return false;
        }
       
        this.statelabels.clear();
        this.charlabels.clear();
        // For now, we expect the state labels in orer
        int last_ch=-1;
        String current_state="";
        HashMap<String,String> tmp_ch=new HashMap<String,String>();        
        for (String stri:tmp) {
            if (!stri.startsWith("#")) {
                String[] s=stri.split("\t");
                if (s.length>=4) {
                    int ch=Integer.valueOf(s[0]); //--Character [1...n]
                    String states1=s[1]; //e.g. 0, 1, A, B...
                    String states2=s[2].trim(); //--char string, eg. legs, wings...
                    String chs=s[3].trim(); //--State string e.g. present, absent

                    if (ch!=last_ch) {
                        if (last_ch!=-1) {
                            //System.out.println(last_ch+" "+current_state+tmp_ch);
                            this.charlabels.add(current_state);
                            this.statelabels.add(tmp_ch);
                        }
                        tmp_ch=new HashMap<String,String>();
                        last_ch=ch;
                        current_state=states2;
                    }
                    tmp_ch.put(states1,chs);
                }  else {
                    System.out.println("Error (not enough tabulation): "+stri);
                    return false;
                }  
            }
        }
        this.charlabels.add(current_state);
        this.statelabels.add(tmp_ch);       
        return true;
    }
    
    public boolean export_charstate(String filename) {
    try {
        PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));        
        pw.println("#Character-States for "+this.filename+" saved on "+util.returnCurrentDateAndTime());
        pw.println("#Column\tEncoded state\tCharacter\tState");
        for (int index_char=0; index_char<this.nchar;index_char++) {
        this.symbols=getCharMatrixSymbols();
        HashMap<String,String> st=null;
        
            try {
                st=this.statelabels.get(index_char);  
            } catch(Exception e) {
                //e.printStackTrace();
               HashMap<String,String> stmp=new HashMap<String,String>();
                for (char c:this.symbols.toCharArray()) {
                    stmp.put(""+c,""+c);
                }
                if (index_char>=statelabels.size()) {
                    for (int j=statelabels.size();j<=index_char;j++) this.statelabels.add(new HashMap<String,String>());
                }                         
                this.statelabels.set(index_char, stmp);
            }               
            String ch="";
            try {
           this.charlabels.get(index_char);
            } catch(Exception e2) {
                for (int i=0; i<nchar;i++) {
                    charlabels.add("Char. "+(i+1));
                }
            }
            ch=charlabels.get(index_char);
                st=this.statelabels.get(index_char);                        
                if (st!=null) {
                   for (String k:util.sort((Set<String>)st.keySet())) {                      
                       pw.println((index_char+1)+"\t"+k+"\t"+ch+"\t"+st.get(k));
                   } 
                } else {
                    
                }            
        }
        pw.flush();
        pw.close();        
    } catch(Exception e) {
        e.printStackTrace();
        return false;
    }
    return true;
}
    
    public void build_charstate_label() {
         for (int index_char=0; index_char<this.nchar;index_char++) {
        this.symbols=getCharMatrixSymbols();
        HashMap<String,String> st=null;        
            try {
                st=this.statelabels.get(index_char);  
            } catch(Exception e) {
                //e.printStackTrace();
               HashMap<String,String> stmp=new HashMap<String,String>();
                for (char c:this.symbols.toCharArray()) {
                    stmp.put(""+c,""+c);
                }
                if (index_char>=statelabels.size()) {
                    for (int j=statelabels.size();j<=index_char;j++) this.statelabels.add(new HashMap<String,String>());
                }                         
                this.statelabels.set(index_char, stmp);
            }               
            String ch="";
            try {
           this.charlabels.get(index_char);
            } catch(Exception e2) {
                for (int i=0; i<nchar;i++) {
                    charlabels.add("Char. "+(i+1));
                }
            }                
        }
    }
    
    public void MessageOption(String txt) {
         st_option.append(txt);
          setChanged();
         notifyObservers();        
    }
    
    public void MessageResult(String txt) {
          st_results.append(txt);
         setChanged();
         try {
             if (callback!=null) callback.call();         
         } catch(Exception e) {}       
    }
 
         /**
      * Compare one dataset to an other
      * @param t
      * @return 
      */
    public boolean compare(Object t) {
        datasets tt=(datasets)t;
        //-- Are the ntax and nchar equals
        if (this.ntax!=tt.ntax) return false;
        
        if (this.nchar!=tt.nchar) return false;
        //if (!this.symbols.equals(tt.symbols)) return false;        
        //1. Are the char. matrix equals?
        for (int i=0; i<this.ntax;i++) {
            for (int j=0; j<this.nchar;j++) {
                if (!char_matrix[i][j].equals(tt.char_matrix[i][j])) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    
    /**
     * Test serialization
     * @param args 
     */
     public static void main(String[] args) {
           Locale.setDefault(new Locale("en", "US"));
           
         datasets d1=new datasets();
         d1.printCharMatrix();
         d1.compute();
      
         d1.export_cytoscapejs("data\\test.html", 1);

    }

    

   
    
} //End dataset
