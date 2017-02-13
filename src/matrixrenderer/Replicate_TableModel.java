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


import COMPONENT_GRAPHER.summary_statistics;
import matrix.DataMatrix;
import java.awt.Component;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import matrix.DataMatrix;

public class Replicate_TableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.permutation_statistics data;
   
//3d3)Dans un troisieme tableau « pivots-triplets» : on présente un nombre n de composantes, classées par ordre décroissant des valeurs de %central in triplets. (n fixé par l’utilisateur, par défaut on liste toutes les composantes)
// 
//Composante1 \t valeur de %central in triplets (dans graphe arète type 3) \t  nombre de samples contenant le component 1 \ t liste détaillées des samples concernés.
// 
//Par exemple :
//Œil violet \t 7% \t 3 \t angelina tartanpion hulk
   String[] qualifier={"Replicate"};
   

   public Replicate_TableModel() {
     
   }

   public void setColumnName(String[] qualifier) {
       //this.qualifier=qualifier;
   }

   public void setData(COMPONENT_GRAPHER.permutation_statistics data) {
       this.data=data;
   }

    

   @Override
    public int getRowCount() {
        int number=0;
        try {
           number=data.replicates.size();
        } catch(Exception e) {}
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
        // We need to get to the displayed sequence
       try {
           summary_statistics s=data.replicates.get(row);
           return row;
           
       } catch(Exception e) {
           e.printStackTrace();
           System.out.println(row+ " "+col+ " ");           
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
        return qualifier[c];
        
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         return false;
    }


}
