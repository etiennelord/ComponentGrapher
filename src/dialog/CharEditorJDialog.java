/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialog;

import COMPONENT_GRAPHER.datasets;
import static COMPONENT_GRAPHER.datasets.config;
import config.Config;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.RowFilter;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import matrixrenderer.CharStateRowFilter;
import matrixrenderer.ExcelResultAdapter;
import matrixrenderer.MatrixTableModel;
import matrixrenderer.StateCharTableModel;
import matrixrenderer.StateTableCellRenderer;
import matrixrenderer.VerticalTableHeaderCellRendered;

/**
 *
 * @author etienne
 */
public class CharEditorJDialog extends javax.swing.JDialog {

      datasets data=null;                             // The dataset
    Frame frame;         
    ExcelResultAdapter StateMatrixTable;
     TableRowSorter sorter;
    
    /**
     * Creates new form CharEditorJDialog
     */
    public CharEditorJDialog(java.awt.Frame parent, datasets data_) {
        super(parent, true);         
        data=data_;
        this.frame=parent;
        initComponents();
        StateCharTableModel tm = new StateCharTableModel();
        this.CharState_jTable.setModel(tm);
          sorter = new TableRowSorter<StateCharTableModel>(tm);
          CharState_jTable.setRowSorter(sorter);
        this.Copy_jButton.setFont(Config.getGlyphicon());
        this.Copy_jButton.setText("\uf0c5");
        this.setTitle("Edit character and state labels");
        StateMatrixTable = new ExcelResultAdapter(this.CharState_jTable);StateCharTableModel tm2=(StateCharTableModel)this.CharState_jTable.getModel();
            tm2.setData(data);       
            
            tm2.fireTableDataChanged();
            tm2.fireTableStructureChanged();
            //We need to update the model also
            StateTableCellRenderer staterendere=new StateTableCellRenderer();
            
            this.CharState_jTable.setDefaultRenderer(String.class, staterendere);
            
             this.CharState_jTable.setModel(tm);
                         TableCellRenderer headerRenderer = new VerticalTableHeaderCellRendered(true);  
                         Enumeration columns=this.CharState_jTable.getColumnModel().getColumns();
                         while (columns.hasMoreElements()) {            
                              ((TableColumn)columns.nextElement()).setHeaderRenderer(headerRenderer);
                         }
          this.CharState_jTable.getColumnModel().getColumn(0).setPreferredWidth(25);
          this.CharState_jTable.getColumnModel().getColumn(1).setPreferredWidth(25);                 
          this.CharState_jTable.getColumnModel().getColumn(2).setPreferredWidth(200);                 
          this.CharState_jTable.getColumnModel().getColumn(3).setPreferredWidth(200);   
          
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
        this.setVisible(true); 
    }
    

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        CharState_jTable = new javax.swing.JTable();
        Import_jButton = new javax.swing.JButton();
        Export_jButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        Copy_jButton = new javax.swing.JButton();
        Filter_ComboBox = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        CharState_jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(CharState_jTable);

        Import_jButton.setText("Import dictionnary");
        Import_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Import_jButtonActionPerformed(evt);
            }
        });

        Export_jButton.setText("Export dictionnary");
        Export_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Export_jButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Filter");

        Copy_jButton.setText("c");
        Copy_jButton.setToolTipText("Copy characters and states to clipboard");
        Copy_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Copy_jButtonActionPerformed(evt);
            }
        });

        Filter_ComboBox.setEditable(true);
        Filter_ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter_ComboBoxActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 153, 0));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Note: characters should be used in the order they are available, i.e. 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Import_jButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Export_jButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Filter_ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Copy_jButton))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Copy_jButton)
                    .addComponent(Filter_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Import_jButton)
                    .addComponent(Export_jButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Import_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Import_jButtonActionPerformed
            JFileChooser chooser = new JFileChooser(config.getExplorerPath());
            FileFilter filter_text = new FileNameExtensionFilter("Character state file (.txt)", "txt", "mat");                
            chooser.addChoosableFileFilter(filter_text);
            chooser.setFileFilter(filter_text);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
                chooser.setDialogTitle("Import character-states for this matrix");
                int returnVal = chooser.showOpenDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    data.load_charstate(chooser.getSelectedFile().getAbsolutePath());
                }
    }//GEN-LAST:event_Import_jButtonActionPerformed

    private void Copy_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Copy_jButtonActionPerformed
       StateMatrixTable.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CopyAll"));
    }//GEN-LAST:event_Copy_jButtonActionPerformed

    private void Filter_ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter_ComboBoxActionPerformed
      
                int mode=this.Filter_ComboBox.getSelectedIndex();
                String search_term=(String)this.Filter_ComboBox.getSelectedItem();
                //if (search_term.indexOf("[")>-1) search_term=search_term.substring(0,search_term.indexOf("["));
                search_term=search_term.trim();                
                RowFilter<StateCharTableModel, Object> rf = null;
                //If current expression doesn't parse, don't update.
                if (search_term.isEmpty()) {                    
                       sorter.setRowFilter(null);
                } else {                   
                try {
                      sorter.setRowFilter(new CharStateRowFilter(search_term));
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                  
                }
            
    }//GEN-LAST:event_Filter_ComboBoxActionPerformed

    private void Export_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Export_jButtonActionPerformed
           JFileChooser chooser = new JFileChooser(config.getExplorerPath());
           FileFilter filter_text = new FileNameExtensionFilter("Character state file (.txt)", "txt", "mat");                
            chooser.addChoosableFileFilter(filter_text);
            chooser.setFileFilter(filter_text);          
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
            chooser.setDialogTitle("Saving character-states");
                int returnVal = chooser.showOpenDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   data.export_charstate(chooser.getSelectedFile().getAbsolutePath());
                }
    }//GEN-LAST:event_Export_jButtonActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable CharState_jTable;
    private javax.swing.JButton Copy_jButton;
    private javax.swing.JButton Export_jButton;
    private javax.swing.JComboBox Filter_ComboBox;
    private javax.swing.JButton Import_jButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
