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


import COMPONENT_GRAPHER.datasets;
import COMPONENT_GRAPHER.util;
import javax.swing.table.*;
import java.util.HashMap;
import java.util.Set;

public class TaxaTableModel extends AbstractTableModel {
 
   datasets data =new datasets();
   String symbols="";
   boolean[] selected;
   
String[] qualifier={"Taxa"};

   public TaxaTableModel() {
    
   }

   public void setColumnName(String[] qualifier) {
       //this.qualifier=qualifier;
   }

   public void resetSelection() {
       for (int i=0; i<selected.length;i++) selected[i]=true;
   }
   
   public void setData(datasets _data) {
       this.data=_data;       
   }
    
    
   @Override
    public int getRowCount() {        
        int number=data.ntax;        
        return number;
    }

   @Override
    public int getColumnCount() {       
        //if (qualifier!=null) return qualifier.length;
        return qualifier.length;
    }
    

   @Override
    public Object getValueAt(int row, int col) {
      try {
          return data.label.get(row);
      }  catch (Exception e) {}
      
       return "";
    }

    
    
   @Override
     public Class getColumnClass(int c) {
         switch(c) {
             //case 0: return Boolean.class;
             default: return String.class;
         }
    }


    @Override
     public String getColumnName(int c) {
        return qualifier[c];        
     }

    @Override
    public boolean isCellEditable(int row, int col) {        
        return true;
    }

        
    @Override
    public void setValueAt(Object value, int row, int col) {
          data.label.set(row, (String)value);
    }


}
