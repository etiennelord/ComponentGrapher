package COMPONENT_GRAPHER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *  COMPONENT-GRAPHER v1.0.11
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

/**
 *Helper class to regroup node of CC
 * @author Etienne Lord
 */
public class connected_complexe implements Serializable, Comparable {
    public int complexe_id=0;
    public int network_type=0; //--network type
    public ArrayList<node> nodes=new ArrayList<node>();
    
    @Override
    public int compareTo(Object t) {
        connected_complexe tt=(connected_complexe)t;
        return  (this.nodes.size()>tt.nodes.size()?1:(this.nodes.size()==tt.nodes.size())?0:-1);
    }
  
    public String getCharStates() {
        String st="";
        for (node n:nodes) {
            st+=n.complete_name+",";
        }
        st=st.substring(0, st.length()-1);
        return st;
    }
    
    public String getTaxas() {
          String st="";
          HashMap<String,Integer>map=new HashMap<String,Integer>(); 
          //--Unique
          for (node n:nodes) {
              String[] str=n.stats.get("taxa").split(",");
              for (String s:str) map.put(s, 1);
          }
          for (String k:map.keySet()) {
              st+=k+",";
          }
          st=st.substring(0, st.length()-1);
           return st;
    }
    
}
