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
public class MatrixTableCellRenderer extends DefaultTableCellRenderer {

    public MatrixTableCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object o, boolean selected, boolean bln1, int row, int column) {           
        super.getTableCellRendererComponent(table, o, selected, bln1, row, column);
        
        try {
        String data=(String)o; 
        if (selected) {
          this.setBackground(Color.blue);
        } else
        if (data.length()>1) {
            this.setBackground(Color.yellow);
            
        } else if (data.contains("?")||data.contains("*")) {
            this.setBackground(Color.pink);
            
        } else {
            this.setBackground(Color.white);
        }
        } catch(Exception e) {
            return this;
        }
        
        
        
//        if (table.isRowSelected(row)) {
//                  setFont( getFont().deriveFont(Font.BOLD) );
//             } 
        return this;  //To change body of generated methods, choose Tools | Templates.
    }


 

}
