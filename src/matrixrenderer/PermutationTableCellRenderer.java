/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrixrenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Etienne
 */
public class PermutationTableCellRenderer extends DefaultTableCellRenderer {

    public PermutationTableCellRenderer() {
         setHorizontalAlignment(JLabel.CENTER);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object o, boolean selected, boolean bln1, int row, int column) {           
         
        super.getTableCellRendererComponent(table, o, selected, bln1, row, column);
       if (column==1||column==28)  {
           this.setHorizontalAlignment(JLabel.LEFT);
       } else {
           this.setHorizontalAlignment(JLabel.CENTER);
       }
        try {
            
             String data=(String)o; 
                  if (selected) {
                    this.setBackground(Color.blue);
                  } else
                  if (data.equals("x")) {
                      this.setBackground(Color.green);

                  } else if (data.contains("?")||data.contains("*")||data.equals("NA")||data.equals(" ")||data.equals("")||data.equals("NaN")) {
                      this.setBackground(Color.lightGray);

                  } else {
                      this.setBackground(Color.white);
                  }

                  if (table.isRowSelected(row)) {
                            setFont( getFont().deriveFont(Font.BOLD) );
                       } 
        } catch(Exception e) {
            return this;
        }
//        String data=(String)o;
//        if (selected) {
//          this.setBackground(Color.blue);
//        } else
//        if (data.length()>1) {
//            this.setBackground(Color.yellow);
//            
//        } else if (data.contains("?")||data.contains("*")) {
//            this.setBackground(Color.pink);
//            
//        } else {
//            this.setBackground(Color.white);
//        }
        
//        if (table.isRowSelected(row)) {
//                  setFont( getFont().deriveFont(Font.BOLD) );
//             } 
        return this;  //To change body of generated methods, choose Tools | Templates.
    }


 

}
