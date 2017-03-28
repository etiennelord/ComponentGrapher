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

public class StateCharTableModel extends AbstractTableModel {
 
   datasets data =new datasets();
   String symbols="";
   boolean[] selected;
   
String[] qualifier={"Column","Encoded state","Character","State","Total in matrix"};

   public StateCharTableModel() {
    
   }

   public void setColumnName(String[] qualifier) {
       //this.qualifier=qualifier;
   }

   public void resetSelection() {
       for (int i=0; i<selected.length;i++) selected[i]=true;
   }
   
   public void setData(datasets _data) {
       this.data=_data;       
       updateData();
   }
    
   public void updateData() {
       this.symbols=data.getCharMatrixSymbols();      
   }
  
    
   @Override
    public int getRowCount() {
        //int number=data.size();        
        int number=data.nchar*this.symbols.length();
        //if (number<0) number=0;
        return number;
    }

   @Override
    public int getColumnCount() {       
        //if (qualifier!=null) return qualifier.length;
        return qualifier.length;
    }
    

   @Override
    public Object getValueAt(int row, int col) {
        //--Get the true row
        //--Case col=0 : charstate
        //System.out.println(this.symbols.length());
        int index_char=row / this.symbols.length();
        int index_state= row % this.symbols.length();
        //System.out.println(row + " "+ col + " "+index_char+ " "+ index_state+" "+data.charlabels.get(index_char)+" "+data.statelabels.get(index_state));
       //System.out.println(index_char+" "+data.charlabels.get(index_char)); 
        //System.out.println(index_state+" "+data.statelabels.get(index_char));
        HashMap<String,String> st=null;
        try {
            st=data.statelabels.get(index_char);  
        } catch(Exception e) {
            //e.printStackTrace();
            //System.out.println("Not found");
            st=new HashMap<String,String>();
            for (char c:this.symbols.toCharArray()) {
                st.put(""+c,""+c);
            }
            if (index_char>=data.statelabels.size()) {
                for (int j=data.statelabels.size();j<=index_char;j++) data.statelabels.add(new HashMap<String,String>());
            }
            data.statelabels.set(index_char, st);
        }        
        String ch="";
        try {
        data.charlabels.get(index_char);
        } catch(Exception e2) {
           // e2.printStackTrace();
            for (int i=0; i<data.nchar;i++) {
                data.charlabels.add("Char. "+(i+1));
            }
        }
        ch=data.charlabels.get(index_char);
        //System.out.println(st);
       switch(col) {
           case 0: return (index_char+1);
           case 1: return symbols.charAt(index_state);
           case 2: if (index_state==0) {
                        return ch; 
                    } else {
                        return "";
                   }                   
           case 3: return st.get(""+symbols.charAt(index_state));
           case 4: return data.getTotalCharState(index_char,""+symbols.charAt(index_state));
       }                  
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
        if (col==0) return false;
        if (col==1) return false;
        int index_state= row % this.symbols.length();
        if (col==2&&index_state!=0) return false;
        if (col==4) return false;
        return true;
    }

        
    @Override
    public void setValueAt(Object value, int row, int col) {
           int index_char=row / this.symbols.length();
          int index_state= row % this.symbols.length();
          if (col==2) {
              data.charlabels.set(index_char,(String)value);              
          }
          if (col==3) {
              HashMap<String,String> st=data.statelabels.get(index_char);
              st.put(""+symbols.charAt(index_state), (String)value);
              data.statelabels.set(index_char,st);
          }
    }


}
