package COMPONENT_GRAPHER;
/**
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


import java.io.Serializable;
import java.util.ArrayList;
import umontreal.iro.lecuyer.util.BitVector;

/**
 * Simple node in the graph
 * @author Etienne Lord, Jananan Pathmanathan
 * @since October/November 2015
 */
public class node implements Comparable,Serializable{
    
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES
    public String name="";    
    public String complete_name="";
    public String char_label="";  
    public String state_label="";
    public String state_matrix=""; //State in the matrix
    
    public int column=0; //--column (index starting at 1)
    public int id=0; //id in the vertex system
    public ArrayList<Integer>identification=new ArrayList<Integer>(); //taxa or node position    
    public ArrayList<Integer> source_ids=new ArrayList<Integer>(); //source taxa and count
    
    //public int count=0; //--Number of associated characted
    public int edgecount=0; //--Number of edges
    public int in_edgecount=0; //--Number of edges
    public int out_edgecount=0; //--Number of edges
    
    public BitVector partition; //--This will be used in the processing of the partition
    public int total_taxa=0;    //--idem
    ////////////////////////////////////////////////////////////////////////////
    /// Internal data
    public int multistate=1; //--Total number of stte for this node

    ////////////////////////////////////////////////////////////////////////////
    /// Properties
    public properties stats=new properties();
    
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTANTS
    public static int next_letter=0;
    
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR
    public node() {}
    
    public node(node n) {        
        this.name=n.name;
        this.id=n.id;
        this.complete_name=n.complete_name;
        this.char_label=n.char_label;
        this.state_label=n.state_label;
        this.state_matrix=n.state_matrix;
        this.column=n.column;
        //this.count=n.count;
        this.edgecount=n.edgecount;
        this.in_edgecount=n.in_edgecount;
        this.out_edgecount=n.out_edgecount;
        this.partition=n.partition;
        identification.addAll(n.identification);
        source_ids.addAll(n.source_ids);
        this.total_taxa=n.total_taxa;
        this.multistate=n.multistate; //--Total number of stte for this node
        //--Copy stats
        for (String k:n.stats.stringPropertyNames()) {
            this.stats.put(k, n.stats.get(k));
        }        
    }
    
    public node(String name, int id) {
        this.name=name; 
        this.id=id;         
    }
        
    @Override
    public boolean equals(Object obj) {       
        return this.id==(((node)obj).id);        
    }

     public String getName() {
       if (!this.name.isEmpty()) return this.name;
         if (this.identification.size()>4) {            
            this.name=get_next_letters();
            
        } else {
             String s="";
                for (int i:this.identification) {
                  s+=i+",";   
                }        
                s=s.substring(0,s.length()-1);
                this.name=s;
        }      
       return(this.name);
    }
   
     public String get_next_letters() {         
        String str="ID"+next_letter;       
        next_letter++;
        return str;
    }

    @Override
    public int compareTo(Object o) {
        return (this.id-((node)o).id);
    }
    
    @Override
    public String toString() {
        return this.complete_name+" (degree: "+this.edgecount+")";
    }

     
}
