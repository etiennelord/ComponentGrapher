package matrixrenderer;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;


public class RowNumberTable extends JTable
	implements ChangeListener,PropertyChangeListener, TableModelListener
{
	private JTable main;
        private MatrixTableModel tm;
        private static ImageIcon icon;
        
	public RowNumberTable(JTable table)
	{
		if (icon==null) {
                    icon=new ImageIcon("data\\glyphicons-151-edit_17.png");
                    
                }
                main = table;
		main.addPropertyChangeListener( this );
		main.getModel().addTableModelListener( this );
                
                tm=(MatrixTableModel)main.getModel();
		setFocusable( true );
		setAutoCreateColumnsFromModel( false );
		setSelectionModel( main.getSelectionModel() );

		TableColumn column = new TableColumn();
		column.setHeaderValue(" ");
		addColumn( column );
		column.setCellRenderer(new RowNumberRenderer());
                
		getColumnModel().getColumn(0).setPreferredWidth(150);
		setPreferredScrollableViewportSize(getPreferredSize());
//                this.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    int row =  main.rowAtPoint(e.getPoint());                   
//                    //System.out.println("Row index selected " + row + " "+ tm.data.charlabels.get(row));
//                    if (e.getClickCount() == 2) {
//                       if (!tm.data.inverse_matrix_table) {
//                           
//                       } else {
//                           System.out.println("Error here");
//                           //System.out.println("Row index selected " + row + " "+ tm.data.charlabels.get(row));
//                       }
//                    }
//                    if (SwingUtilities.isRightMouseButton(e)) {
//                        System.out.println("editing");
//                        System.out.println("Row index selected " + row + " "+ tm.data.charlabels.get(row));
//                    }
//                }
//
//                }       
                        
//               );
	}

          @Override
            protected JTableHeader createDefaultTableHeader() {
              
                JTableHeader header = new JTableHeader(getColumnModel()) {

                    @Override
                    public void columnSelectionChanged(ListSelectionEvent e) {
                        repaint();
                    }

                };
                return header;
            }

    
	@Override
	public void addNotify()
	{
		super.addNotify();

		Component c = getParent();

		//  Keep scrolling of the row table in sync with the main table.

		if (c instanceof JViewport)
		{
			JViewport viewport = (JViewport)c;
			viewport.addChangeListener( this );
		}
	}

	/*
	 *  Delegate method to main table
	 */
	@Override
	public int getRowCount()
	{
		return main.getRowCount();
	}

	@Override
	public int getRowHeight(int row)
	{
		int rowHeight = main.getRowHeight(row);

		if (rowHeight != super.getRowHeight(row))
		{
			super.setRowHeight(row, rowHeight);
		}

		return rowHeight;
	}

	/*
	 *  No model is being used for this table so just use the row number
	 *  as the value of the cell.
	 */
	@Override
	public Object getValueAt(int row, int column)
	{		
            String charlabel=" "+(row+1);
            
            try {
                
                if (tm.data.inverse_matrix_table) {
                    charlabel="  "+tm.data.label.get(row);
                } else {
                    if (tm.data.display_char_numbering) charlabel=" ("+(row+1)+") ";
                    charlabel+=" "+tm.data.charlabels.get(row);
                }
            } catch (Exception e) {
                                
            }
            return charlabel;
	}

	/*
	 *  Don't edit data in the main TableModel by mistake
	 */
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	/*
	 *  Do nothing since the table ignores the model
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {}
//
//  Implement the ChangeListener
//
	public void stateChanged(ChangeEvent e)
	{
		//  Keep the scrolling of the row table in sync with main table

		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane)viewport.getParent();
		scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	}
//
//  Implement the PropertyChangeListener
//
	public void propertyChange(PropertyChangeEvent e)
	{
		//  Keep the row table in sync with the main table

		if ("selectionModel".equals(e.getPropertyName()))
		{
			setSelectionModel( main.getSelectionModel() );
		}

		if ("rowHeight".equals(e.getPropertyName()))
		{
			repaint();
		}

		if ("model".equals(e.getPropertyName()))
		{
			main.getModel().addTableModelListener( this );
			tm=(MatrixTableModel)main.getModel();
                        revalidate();
		}
	}

//
//  Implement the TableModelListener
//
	@Override
	public void tableChanged(TableModelEvent e)
	{
		revalidate();
	}

	/*
	 *  Attempt to mimic the table header renderer
	 */
	private static class RowNumberRenderer extends DefaultTableCellRenderer
	{
		public RowNumberRenderer()
		{
			setHorizontalAlignment(JLabel.LEFT);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (table != null)
			{
				JTableHeader header = table.getTableHeader();

				if (header != null)
				{
					
                                        setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
                                        // this.setIcon(null);                                         
                                         setText((value == null) ? "" : value.toString());
				}
			}

			if (isSelected)
			{
				//setFont( getFont().deriveFont(Font.BOLD) ); 
                                //setIconTextGap(20);
                                setText((value == null) ? "" : value.toString() );
                                setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                                //setIcon(icon);
			} 
			
			return this;
		}
	}
        
       
}
