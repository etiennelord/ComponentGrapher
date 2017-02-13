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


import javax.swing.table.*;
//import matrix.DataMatrix;

public class MatrixTableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.datasets data=new COMPONENT_GRAPHER.datasets();
   
   
   

   public MatrixTableModel() {
     
   }

   public void setColumnName(String[] qualifier) {
       //this.qualifier=qualifier;
   }

   public void setData(COMPONENT_GRAPHER.datasets data) {
       this.data=data;
   }
  
   @Override
    public int getRowCount() {
        //int number=data.size();        
        Integer number=data.nchar;
        if (data.inverse_matrix_table) number=data.ntax;
        if (number==null||number<0) number=0;
        return number;
    }

   @Override
    public int getColumnCount() {       
        //if (qualifier!=null) return qualifier.length;
         Integer number=data.ntax;
         if (data.inverse_matrix_table) number=data.nchar;
         if (number==null||number<0) number=0;
        return number;
    }
    

   @Override
    public Object getValueAt(int row, int col) {
        // We need to get to the displayed sequence
       try {
            if (data.inverse_matrix_table) return data.char_matrix[row][col].toUpperCase();
           return data.char_matrix[col][row].toUpperCase();
        
       } catch(Exception e) {
           e.printStackTrace();
           System.out.println(row+ " "+col+ " "+data.ntax+" "+data.nchar);
           System.out.println(data.intmaxrow()+" "+data.intmaxcol());
           return 0;
       }

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
         String colname="  ";         
         if (data.inverse_matrix_table) {             
             if (data.display_char_numbering) colname+="("+(c+1)+") ";                    
            try {
                colname+=""+data.charlabels.get(c);
            } catch (Exception e) {
                colname+="Char. "+(c+1);    
            }            
           } else {
             colname=data.label.get(c);
         }
        
         return colname;
                
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         return true;
    }

        
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (data.inverse_matrix_table) {
             data.char_matrix[row][col]=((String)value).toUpperCase();
        } else {
            data.char_matrix[col][row]=((String)value).toUpperCase();
        }
       
    }


}
