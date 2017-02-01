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
public class StateTableCellRenderer1 extends DefaultTableCellRenderer {

    public StateTableCellRenderer1() {
         setHorizontalAlignment(JLabel.CENTER);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object o, boolean selected, boolean hasFocus, int row, int column) {           
         
        super.getTableCellRendererComponent(table, o, selected, hasFocus, row, column);
     
        try {
                 this.setForeground(Color.black);
                if (column==0||column==1) {
                      this.setBackground(Color.white);
                } 
                else if  (selected){
                   this.setBackground(Color.blue);
                   this.setForeground(Color.white);
                } 
//                else if((Integer)table.getValueAt(row, 4)==0) {                    
//                     this.setBackground(Color.lightGray);
//                     this.setForeground(Color.black);
//                } 
                else if (!table.isCellEditable(row, column)) {
                      this.setBackground(Color.lightGray);
                }
                else {
                    this.setForeground(Color.black);
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
