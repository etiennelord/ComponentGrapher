package matrixrenderer;

import COMPONENT_GRAPHER.node;
import java.util.HashMap;
import javax.swing.RowFilter;

/**
 *
 * @author Etienne Lord
 */
public class SummaryRowFilter extends RowFilter {

    String filter="";
    
    
    public SummaryRowFilter(String filter) {
        this.filter=filter.toLowerCase();
    }
            
    @Override
    public boolean include(Entry entry) {
        Summary_TableModel tm=(Summary_TableModel)entry.getModel();
        Integer row=(Integer)entry.getIdentifier();
        node n=tm.data.data.nodes.get(row);        
        //--Scan the associated char state with filter
        if (n.complete_name.toLowerCase().contains(filter)) return true;
        if (n.stats.get("taxa").toLowerCase().contains(filter)) return true;        
        return false;
    }
    
}
