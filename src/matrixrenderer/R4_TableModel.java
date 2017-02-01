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


import matrix.DataMatrix;
import java.awt.Component;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import matrix.DataMatrix;

public class R4_TableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.datasets data=new COMPONENT_GRAPHER.datasets();
   
//d) Le nombre n des composantes qui sont des pivots (n fixé par l’utilisateur, par défaut on liste toutes les composantes pivots). Si les caractères ne sont pas pivots on ne les indique pas.
//Ici il y a une astuce car il faut distinguer plusieurs types de pivots dans le fichier de sortie, donc faire plusieurs fichiers de sortie.
//
//3d1)Dans un premier tableau « pivots-points d’articulation » :
//Composante1 \t pivot global (articulation point dans graphe arète type 1,2,3) \t pivot global chevauchant (articulation point dans graphe arète type 3) \t pivot local chevauchant (articulation point qui deconnecte ces voisins directs dans graphe arète type 3 SI ON faisait déjà ca, sinon tant pis) \ t + nombre de samples contenant le component 1 + liste détaillées des samples concernés)
// 
//Par exemple :
//Œil violet \t YES\t YES\t YES\t3\t angelina tartanpion hulk
//   
   

   public R4_TableModel() {
     
   }

   public void setColumnName(String[] qualifier) {
       //this.qualifier=qualifier;
   }

   public void setData(COMPONENT_GRAPHER.datasets data) {
       this.data=data;
   }

    public void addData(DataMatrix data) {
        //this.data.add(data);
   }
    
    public void test() {
        data.nchar=10;
        data.ntax=20;
    }
    
   @Override
    public int getRowCount() {
        //int number=data.size();        
        int number=data.nchar;
        //if (number<0) number=0;
        return number;
    }

   @Override
    public int getColumnCount() {       
        //if (qualifier!=null) return qualifier.length;
        return data.ntax;
    }
    

   @Override
    public Object getValueAt(int row, int col) {
        // We need to get to the displayed sequence
       try {
        return data.char_matrix[col][row];
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
        return "  "+data.label.get(c); //--Space for better display
        
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         return true;
    }

        
    @Override
    public void setValueAt(Object value, int row, int col) {

        data.char_matrix[col][row]=(String)value;
       
    }


}
