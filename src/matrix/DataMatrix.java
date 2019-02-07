package matrix;
/*
 *  COMPOSITE-GRAPHER v1.0.11
 *  
 *  Copyright (C) 2015-2019  Etienne Lord
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

import config.util;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This is the Matrix structure
 * @author Etienne Lord
 */
@Deprecated
public class DataMatrix {
 
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES
    public ArrayList<String> charlabels=new ArrayList<String>();
   public ArrayList<ArrayList<String>> statelabels=new ArrayList<ArrayList<String>>();
   public HashMap<String, Integer> index_label=new HashMap<String, Integer>();
   public ArrayList<String> label=new ArrayList<String>(); //--Taxa name
   public ArrayList<String> state=new ArrayList<String>(); //--line of char. for the taxa when loading the matrix
   public static Pattern isNumbers=Pattern.compile("^\\s{0,}([0-9]{1,})\\s{1,}([0-9]{1,})$");   
  
   public String title="";
   public String filename="";   
   public int ntax=0;
   public int nchar=0;	
   public int max_char_state=1; //--Maximum char state found for a taxon e.g. {1,2,3} =3
   public String char_matrix[][]; //--This is the original data matrixd minus the {}
   public int mode=0;
   
   public boolean changed=false; //--Did we do some change to the matrix
   
    /////////////////////////////////////////////////////////////////////////////
   /// VARIABLES - Datasets
   //--This is for rapid access to node type
   public ArrayList<HashMap<Integer,Integer>> node_id_type=new ArrayList<HashMap<Integer,Integer>>();
   
   public ArrayList<Integer> undefined_column=new ArrayList<Integer>();
   public ArrayList<Integer> multiple_column=new ArrayList<Integer>();
   
   public int char_state[]; //--char state for this partition
   /////////////////////////////////////////////////////////////////////////////
   /// STATES
   int total_state_id=0; 
   float total_states=1;
   public boolean save_inter_result=true;
  
   
   ArrayList<State> states=new ArrayList<State>(); //--Multiple-states positions (i,j)and information in the matrix
   public static ArrayList<String> state_strings=new ArrayList<String>(); //--StateString for this char matrix        
   public String current_state_matrix[][]; 
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// FUNCTIONS
   
   /**
      * This must be called after the preparation of the char matrix
      * This prepate the multiples-states matrix
      */
     public void create_states() {
         ArrayList<String> st=new ArrayList<String>();        
         this.current_state_matrix=new String[this.ntax][this.nchar];        
         for (int i=0; i<this.ntax;i++) {
             for (int j=0; j<this.nchar;j++) {
                 this.current_state_matrix[i][j]=char_matrix[i][j];
                 if (char_matrix[i][j].length()>1) {
                     State s=new State();
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
    
     /**
      * Return the number of taxa
      * @return 
      */
    public int intmaxrow() {
 	return (this.state.size());
 }
    
    /**
     * Return the number of char
     * @return 
     */
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
         System.out.println("*"+c);
    return (c);
    }
    
String[][] charmatrix() {
  String  mat[][]=new String[intmaxrow()][intmaxcol()];
    //System.out.println(intmaxrow()+" "+intmaxcol()); 
  
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
 
  
 public Integer get_value(String s) {     
     s=s.substring(0, s.length()-1); 
     return Integer.valueOf(s.split("=")[1]);
 }
  
  public Integer get_value(String s, String id) {     
      Pattern p=Pattern.compile(id, Pattern.CASE_INSENSITIVE);
      Matcher m=p.matcher(s);
      if (m.find()) {          
          return Integer.valueOf(m.group(1));
      }
     return 0;
 }
  
  /**
   * Given a string, will split around space or ' ' delimiter
   * @param data
   * @return 
   */
  ArrayList<String> get_fields(String data) {
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
  
   String extract_charlabels(String line) {
        //CHARLABELS
        //		 [1] 'GEN skull, telescoping, presence'
	String num="";
	String label="";
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
			if (line.charAt(i)!='\'') label+=line.charAt(i);
		}
	}	
	return (label);
    }

          
  /**
   * Helper function for tag CHARSTATELABELS
   * @param str 
   */
  public void process_char_state(String str) {
        int total=-1;
        // Spit string by pattern
        ArrayList<String> tmp=get_fields(str);
        for (String s:tmp) if (s.equals("/")) {
            this.charlabels.add("");
            this.statelabels.add(new ArrayList<String>());
            total++;
        }
        ArrayList<String> tmps=new ArrayList<String>();
        // start at end
        int i=tmp.size()-1;
        while (i>-1) {
            String c=tmp.get(i);
            if (c.equals("/")) {
              this.charlabels.set(total,tmp.get(i-1));
              this.statelabels.set(total,tmps);
              total--;
              tmps=new ArrayList<String>();
              i-=3;
            } else {
                tmps.add(c);
                i--;
            }
        }
  }
     
  /**
   * Main function to load NEXUS files
   * @param filename
   * @return 
   */
  public boolean load_morphobank_nexus(String filename) {
        ArrayList<String> tmp_state=new ArrayList<String>();
         this.filename=filename;
	 // flags!
	 boolean flag_in_matrix=false;
	 boolean flag_in_CHARLABELS=false;
	 boolean flag_in_STATELABELS=false;
         boolean flag_in_CHARSTATELABELS=false;
         String tmp_char_state="";        
        int count_section=0;
  	ArrayList<String> data=util.loadStrings(filename);
        if (data.isEmpty()) return false;
        for (int i=0; i<data.size();i++) {
                
                String line=data.get(i);   
                line=line.replaceAll("\t", "");                
                line=line.trim();
                //System.out.println(flag_in_matrix+" "+line);
                if (!line.isEmpty()) {
                    if ((line.indexOf("DIMENSIONS")>-1||line.indexOf("dimensions")>-1)&&line.indexOf("NTAX")>-1||line.indexOf("ntax")>-1) {
                        this.ntax=get_value(line, "ntax=([0-9]*)");
                    }
                    if (line.indexOf("BEGIN CHARACTERS;")>-1) count_section++;
                    if ((line.indexOf("DIMENSIONS")>-1||line.indexOf("dimensions")>-1)&&line.indexOf("NCHAR")>-1||line.indexOf("nchar")>-1) this.nchar=get_value(line, "nchar=([0-9]*)");;    		  
                    if (flag_in_matrix&&line.indexOf(";")>-1) flag_in_matrix=false;	
                     if (flag_in_CHARSTATELABELS&&(line.charAt(0)==';'||line.charAt(line.length()-1)==';')) {
                         flag_in_CHARSTATELABELS=false;
                         process_char_state(tmp_char_state);
                     }	
                    if (flag_in_CHARLABELS&&(line.charAt(0)==';'||line.charAt(line.length()-1)==';')) flag_in_CHARLABELS=false;	
                    if (flag_in_STATELABELS&&(line.charAt(0)==';'||line.charAt(line.length()-1)==';')) {
                            if (tmp_state.size()>1) {
                                tmp_state.remove(0);
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
				 charlabels.add(extract_charlabels(line));
                                 if (line.indexOf('/')>0) {
                                     //--We might have state information embedded
                                 }
			 }
			else 
			if (flag_in_STATELABELS) {			
                                if (line.charAt(0)==','||line.charAt(0)==';'||line.endsWith(",")) {
				   //System.out.println(line);
                                   if (tmp_state.size()>1) {
                                       if (line.endsWith(",")) tmp_state.add(line.replaceAll("'", "").replaceAll(",",""));
                                       tmp_state.remove(0);                                   
				 	statelabels.add(tmp_state);
				 	//new state
				 	tmp_state=new ArrayList<String>();
                                   }
				 } else {
				 	tmp_state.add(line.replaceAll("'", ""));                                        
				 }
				
			 } //--End statelabels
                } //--End line empty
	} //--End data 
        if (count_section>1) {
            System.err.println("Warning, more than one matrix (with tag MATRIX) found in file: "+filename);
            return false;
        }
     this.char_matrix=charmatrix();
    //--Create the different states
  try {
    if (this.nchar!=0) create_states(); 
  } catch(Exception e) {
    //e.printStackTrace();
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
   
   public boolean load_simple(String filename) {  
	this.filename=filename;
       
        ArrayList<String> data=util.loadStrings(filename);
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
        
          try {
                if (this.nchar!=0) create_states(); 
              } catch(Exception e) {
                  return false;
              }
          
          System.out.println("Input                                : "+filename);       
        return true;   	
     }
   
   //--Create a random data matrix
   public boolean random() {
            Random r = new Random();
            this.nchar=r.nextInt(5)+2;
            this.ntax=r.nextInt(5)+2;
            this.char_matrix=new String[this.ntax][this.nchar];
            //Create label
            label.clear();
            String LETTERS="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String[] STATES={"0","1","2","3","?","12","01"};
            String[] SPECIES={"taurus","sapiens","musculus","coli","scrofa","lateus","zicoccus","pirococcus","norvegius","canadensis","rattus","hilarious"};
            for (int i=0; i<this.ntax;i++) {
                int l_index=r.nextInt(LETTERS.length());
                int l_species=r.nextInt(SPECIES.length);
                label.add(LETTERS.charAt(l_index)+". "+SPECIES[l_species]);
            }
           
            //Create states
            state.clear();
            for (int i=0; i<this.nchar;i++) {
                String tmp="";                
                for (int j=0; j<this.ntax;j++) {
                    char_matrix[j][i]=STATES[r.nextInt(STATES.length)];
                }
                state.add(tmp);
            }            
            
            printCharMatrix(char_matrix);
            System.out.println("");
            //printCharMatrix(charmatrix());
        
          try {
                if (this.nchar!=0) create_states(); 
              } catch(Exception e) {
                  return false;
              }
    return true;      
   }
   
   public void printCharMatrix(String[][] data) {
              for (int i=0;i<data.length;i++) {
                  System.out.print(label.get(i)+"\t");
                  for (int j=0; j<data[i].length;j++) {
                      System.out.print(data[i][j]+"\t"); 
                  }
                  System.out.println("");
         }
    }
}
