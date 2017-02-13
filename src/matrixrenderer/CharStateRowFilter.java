package matrixrenderer;

import java.util.HashMap;
import javax.swing.RowFilter;

/**
 *
 * @author Etienne Lord
 */
public class CharStateRowFilter extends RowFilter {

    String filter="";
    
    
    public CharStateRowFilter(String filter) {
        this.filter=filter.toLowerCase();
    }
            
    @Override
    public boolean include(Entry entry) {
        StateCharTableModel tm=(StateCharTableModel)entry.getModel();
        Integer row=(Integer)entry.getIdentifier();
        int index_char=row / tm.symbols.length();        
        //--Scan the associated char state with filter
        if (tm.data.charlabels.get(index_char).toLowerCase().contains(filter)) return true;
        //--Scan the associated state
        HashMap<String,String> st=tm.data.statelabels.get(index_char);
        for (String s:st.values()) {
            if (s.toLowerCase().contains(filter)) return true;
        }
        
        
        return false;
    }
    
}
