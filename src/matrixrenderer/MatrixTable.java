/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrixrenderer;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Etienne
 */
public class MatrixTable extends JTable {
    
    TableColumnModel columnModel = this.getColumnModel();

    @Override
    public void columnSelectionChanged(ListSelectionEvent lse) {
         
        super.columnSelectionChanged(lse); 
        System.out.println(lse);
        invalidate();
    }

}
