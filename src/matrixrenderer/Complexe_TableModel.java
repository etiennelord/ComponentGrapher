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


import COMPONENT_GRAPHER.connected_complexe;
import java.util.ArrayList;
import javax.swing.table.*;


public class Complexe_TableModel extends AbstractTableModel {
   public COMPONENT_GRAPHER.summary_statistics data=null;
   ArrayList<connected_complexe> CCS=null;
   int network_type=1;

//3a) le nombre n de complexes (CC dans graphes avec arètes de type I only, n étant fixé par l’utilisateur, mais par défaut on donne tous les complexes).
//Ces complexes devraient être présentés par taille décroissante (de celui qui a le plus de composantes, à celui qui a le moins de composantes), sous forme d’un tableau dans un fichier « complexes » :
//complexe1 (liste composantes + nombre de samples contenant le complexe + liste détaillées des samples concernés)   

//complexe 1 \t bras rouge, œil bleu, nez rose \t 2 \t mickey, minnie 
   
   String[] qualifier={"Complexes","Characters states","Number of nodes","Taxa"};

  
   public void setData(COMPONENT_GRAPHER.summary_statistics data) {
       this.data=data;
   }

   public void setNetworkType(int network_type) {
       this.network_type=network_type;
       if (data!=null) {
           CCS=data.getConnectedComplexes(network_type);
       }
   }
    
   
   @Override
    public int getRowCount() {
        //int number=data.size();        
        if (CCS==null) return 0;        
        return CCS.size();
    }

   @Override
    public int getColumnCount() {       
        return qualifier.length;
        //return data.ntax;
    }
    

   @Override
    public Object getValueAt(int row, int col) {
       if (CCS==null) {
           return 0;
       }
       connected_complexe CC=CCS.get(row);
       switch(col) {
           case 0: return CC.complexe_id;
           case 1: return CC.getCharStates();
           case 2: return CC.nodes.size(); 
           case 3: return CC.getTaxas();
       }
       return 0;
    }

    
    
   @Override
     public Class getColumnClass(int c) {
         //System.out.println("here "+c);
         switch(c) {
             case 0: return String.class;
             case 1: return String[].class;
             case 2: return Integer.class;
             default: return String.class;
         }
    }


    @Override
     public String getColumnName(int c) {
        return "  "+qualifier[c]; //--Space for better display
        
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         return false;
    }


}
