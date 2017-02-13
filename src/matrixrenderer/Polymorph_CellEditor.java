package matrixrenderer;

import COMPONENT_GRAPHER.datasets;
import COMPONENT_GRAPHER.state;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import matrix.State;

/**
 *Voir : http://stackoverflow.com/questions/9955595/how-to-display-multiple-lines-in-a-jtable-cell
 * @author Etienne Lord
 */
public class Polymorph_CellEditor extends AbstractCellEditor implements TableCellEditor,ActionListener {

    datasets data;    
    protected static final String EDIT = "edit";
    int selectedindex=0;
    state this_state=null;


    public Polymorph_CellEditor(datasets _data) {
        this.data=_data;    
        
  }
  
    @Override
    public Object getCellEditorValue() {
       // System.out.println("getCellEditorValue"+this_state);
        //if (this_state==null) return 0;
//        System.out.println(this_state);
//        System.out.println(selectedindex);
        return "";
    }
    

    @Override
    public Component getTableCellEditorComponent(JTable table, Object o, boolean isSelected, int row, int column) {               
        
        JComboBox<String> combo=new JComboBox<String>();        
        
        if (o instanceof state) {
            this_state=(state)o;          
            //System.out.println("getting state...");
            //--Get info from dataset
            HashMap<String,String> st=data.statelabels.get(this_state.pos_j);
            for (int i=0; i<this_state.state.length();i++) {
                String k=""+this_state.state.charAt(i);
                combo.addItem(k+"|"+st.get(k));
            }
        } 
         try {
            combo.setSelectedIndex(0);
         } catch(Exception e) {
             
         }
         combo.addActionListener(this);

        return combo;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            JComboBox<String> combo = (JComboBox<String>) e.getSource();
            this.selectedindex = combo.getSelectedIndex();    
            //System.out.println( this.selectedindex);
            this_state.selected=selectedindex;
            this_state.state_label=(String)combo.getSelectedItem();            
            //System.out.println( combo.getSelectedItem());
    }
}