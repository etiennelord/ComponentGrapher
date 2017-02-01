package matrixrenderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 *Voir : http://stackoverflow.com/questions/9955595/how-to-display-multiple-lines-in-a-jtable-cell
 * @author Etienne Lord
 */
public class PvalueCellRenderer extends JLabel implements TableCellRenderer {

  public PvalueCellRenderer() {
//    setLineWrap(true);
//    setWrapStyleWord(true);
//    setOpaque(true);
  }

  
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //make multi line where the cell value is String[]
        //System.out.println(value+" "+value.getClass());
       if (value instanceof Float[]) {
           Float[] v=(Float[])value;
           this.setText(v[0]+" ***");
       }
      

        //cell backgroud color when selected
        if (isSelected) {
            setBackground(UIManager.getColor("Table.selectionBackground"));
        } else {
            setBackground(UIManager.getColor("Table.background"));
        }

        return this;
    }
}