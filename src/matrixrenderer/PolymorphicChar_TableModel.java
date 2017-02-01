package matrixrenderer;

/*
 *  COMPOSITE-GRAPHER v1.0
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


import COMPONENT_GRAPHER.state;
import javax.swing.table.*;
import matrix.State;

public class PolymorphicChar_TableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.datasets data=new COMPONENT_GRAPHER.datasets();
   
   String[] qualifier={"Taxa","Character","Possible state","State"};
   

   public PolymorphicChar_TableModel() {
     
   }

   public void setColumnName(String[] qualifier) {
       //this.qualifier=qualifier;
   }

   public void setData(COMPONENT_GRAPHER.datasets data) {
       this.data=data;
   }
 
     
   @Override
    public int getRowCount() {
       int number=data.states.size();
       
        return number;
    }

   @Override
    public int getColumnCount() {       
        if (qualifier!=null) return qualifier.length;
        return 0;
    }
    

   @Override
    public Object getValueAt(int row, int col) {
      state s=data.states.get(row);

//--Char state            
      switch(col) {
          case 0: return data.label.get(s.pos_i); //--Taxa
          case 1: return data.charlabels.get(s.pos_j); //--Character
          case 2: return s.state;
          case 3: return s;
          default: return "";    
      }      
    }

    
    
   @Override
     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return String.class;
             case 1: return String.class; 
             case 2: return String.class; 
             case 3: return state.class;
             default: return String.class;    
         }
    }


    @Override
     public String getColumnName(int c) {
        //if (data.inverse_matrix_table) return data.
         return qualifier[c]; //--Space for better display
        
     }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col==3) return true; 
        return false;
    }

        
    @Override
    public void setValueAt(Object value, int row, int col) {       
        if (data.inverse_matrix_table) {
            data.char_matrix[row][col]=(String)value;
        } else {
            data.char_matrix[col][row]=(String)value;
        }
       
    }


}
