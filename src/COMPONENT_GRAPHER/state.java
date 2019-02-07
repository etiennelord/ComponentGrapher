package COMPONENT_GRAPHER;
/*
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

import java.io.Serializable;
import java.util.ArrayList;

/**
 *.Class representing a character-state from the input matrix
 * @author Etienne Lord, Jananan Pathmanathan
 * @since October/November 2015
 */
public class state implements Serializable {
    
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES
    public int state_id=0;  //--ID for this state
    public int pos_i=0;     //--Position in matrix
    public int pos_j=0;     //--Position in matrix
    public ArrayList<String> states=new ArrayList<String>(); //--State as en ArrayList
    public  String state=""; //--State at this position (e.g. ABC) 
    public int selected=-1;
    public boolean undefined=false; //--State is undefined
    public String state_label="";
    
    public state() {}
    public state( state s) {
        this.state_id=s.state_id;
        this.pos_i=s.pos_i;
        this.pos_j=s.pos_j;
        this.state=s.state;
        this.states.addAll(s.states);
    }
    

    @Override
    public String toString() {
       if (selected==-1) {
           return "Choose a state.";
       }        
        return state_label;        
    }

}
