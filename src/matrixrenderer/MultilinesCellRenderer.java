package matrixrenderer;

import java.awt.Component;
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
public class MultilinesCellRenderer extends JList implements TableCellRenderer {

  public MultilinesCellRenderer() {
//    setLineWrap(true);
//    setWrapStyleWord(true);
//    setOpaque(true);
  }

  
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //make multi line where the cell value is String[]
        //System.out.println(value+" "+value.getClass());
       if (value instanceof String) {
           String[] str=((String)value).split(",");
           if (str.length>1) {
                setListData(str);
           }
           table.setRowHeight(row, str.length*16);
       }
        if (value instanceof String[]) {
            setListData((String[]) value);
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