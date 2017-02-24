/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialog;

import COMPONENT_GRAPHER.datasets;
import static COMPONENT_GRAPHER.datasets.config;
import COMPONENT_GRAPHER.node;
import COMPONENT_GRAPHER.summary_statistics;
import config.Config;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import matrixrenderer.CharStateRowFilter;
import matrixrenderer.ExcelResultAdapter;
import matrixrenderer.MatrixTableModel;
import matrixrenderer.ResultExplorer_TableModel;
import matrixrenderer.StateCharTableModel;
import matrixrenderer.StateTableCellRenderer;
import matrixrenderer.VerticalTableHeaderCellRendered;

/**
 *
 * @author etienne
 */
public class ResultExplorer_JDialog extends javax.swing.JDialog {

      summary_statistics data=null;                             // The dataset
    Frame frame;         
    ExcelResultAdapter StateMatrixTable;
     TableRowSorter sorter;
     DefaultListModel pcm=new DefaultListModel(); //--Possible
     DefaultListModel cm=new DefaultListModel(); //--Constrant
     
    /**
     * Creates new form CharEditorJDialog
     */
    public ResultExplorer_JDialog(java.awt.Frame parent, summary_statistics data_) {
        super(parent, true);         
        data=data_;
        this.frame=parent;
        initComponents();
        //--Populate the list of properties
        node n=data.data.nodes.get(0);
        ArrayList<String>pcmi=new ArrayList<String>();
        for (Object s:n.stats.keySet()) {
            pcmi.add(s.toString());
        }
        Collections.sort(pcmi);
        for (String s:pcmi) pcm.addElement(s);
        this.PossibleConstrant_jList.setModel(pcm);
        ResultExplorer_TableModel tm = new ResultExplorer_TableModel();
        this.Result_jTable.setModel(tm);
         sorter = new TableRowSorter<ResultExplorer_TableModel>(tm);
          Result_jTable.setRowSorter(sorter);
        this.Copy_jButton.setFont(Config.getGlyphicon());
        this.Copy_jButton.setText("\uf0c5");
        this.setTitle("Edit character and state labels");
        //StateMatrixTable = new ExcelResultAdapter(this.Result_jTable);StateCharTableModel tm2=(StateCharTableModel)this.Result_jTable.getModel();
            //tm2.setData(data);       
            
//            tm2.fireTableDataChanged();
//            tm2.fireTableStructureChanged();
//            //We need to update the model also
            StateTableCellRenderer staterendere=new StateTableCellRenderer();
            
            this.Result_jTable.setDefaultRenderer(String.class, staterendere);
            
             this.Result_jTable.setModel(tm);
                         TableCellRenderer headerRenderer = new VerticalTableHeaderCellRendered(true);  
                         Enumeration columns=this.Result_jTable.getColumnModel().getColumns();
                         while (columns.hasMoreElements()) {            
                              ((TableColumn)columns.nextElement()).setHeaderRenderer(headerRenderer);
                         }
//          this.Result_jTable.getColumnModel().getColumn(0).setPreferredWidth(25);
//          this.Result_jTable.getColumnModel().getColumn(1).setPreferredWidth(25);                 
//          this.Result_jTable.getColumnModel().getColumn(2).setPreferredWidth(200);                 
//          this.Result_jTable.getColumnModel().getColumn(3).setPreferredWidth(200);   
          
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
        Result_jTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        Copy_jButton = new javax.swing.JButton();
        Filter_ComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        Type_jComboBox = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        Constraint_jList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        Max_jButton = new javax.swing.JButton();
        Remove_jButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        PossibleConstrant_jList = new javax.swing.JList();
        Min_jButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        data_jTextPane = new javax.swing.JTextPane();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Result_jTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(Result_jTable);

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

        jLabel3.setText("Find");

        Type_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Node", "Type 1 edge (identical)", "Type 2 edge (inclusion)", "Type 3 edge (overlap)", "Type 4 edge (disjoint)", "Triplet A  ( i -3- n -3- j )", "Triplet B  ( i -3- n -3- j ,i-1-j )", "Triplet C ( i -3- n -3- j ,i-2-j )", "Triplet D (i -3- n -3- j ,i-4-j )" }));
        Type_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Type_jComboBoxActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(Constraint_jList);

        jLabel2.setBackground(new java.awt.Color(0, 204, 0));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Selected constrant");
        jLabel2.setOpaque(true);

        Max_jButton.setText("<- Max");
        Max_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Max_jButtonActionPerformed(evt);
            }
        });

        Remove_jButton.setText("Remove ->");

        jScrollPane3.setViewportView(PossibleConstrant_jList);

        Min_jButton.setText("<- Min");

        jLabel4.setBackground(new java.awt.Color(102, 102, 102));
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Possible constrant");
        jLabel4.setOpaque(true);

        data_jTextPane.setEditable(false);
        data_jTextPane.setBorder(null);
        jScrollPane4.setViewportView(data_jTextPane);

        jLabel5.setBackground(new java.awt.Color(0, 102, 204));
        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText(" General data");
        jLabel5.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Type_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(530, 530, 530))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Filter_ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Copy_jButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Remove_jButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Max_jButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Min_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(Type_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(9, 9, 9)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(Max_jButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Min_jButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Remove_jButton))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Copy_jButton)
                    .addComponent(Filter_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(70, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void Max_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Max_jButtonActionPerformed
        
    }//GEN-LAST:event_Max_jButtonActionPerformed

    private void Type_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Type_jComboBoxActionPerformed
        //--Update the table with the different statistiques
        int mode=this.Type_jComboBox.getSelectedIndex();
        switch(mode) {
            case 0: update_node(); break;
            case 1: update_type1(); break;
                
        }
        
    }//GEN-LAST:event_Type_jComboBoxActionPerformed

    
    public void update_node(){
        //--Total nodes, etcs 
        String txt="";
        datasets dat=data.data;
        int unassigned_node=0;
        for (node n:dat.nodes) if (!dat.node_id_type.get(0).containsKey(n.id)) unassigned_node++;
        txt+=("Total nodes evaluated             : "+dat.nodes.size()+"\n");      
        txt+=("Total nodes                       : "+dat.node_id_type.get(0).size()+"\n");              
        txt+=("Node unassigned                   : "+unassigned_node+"\n");
        txt+=("Node type 1 (perfect)             : "+dat.node_id_type.get(1).size()+"\n");
        txt+=("Node type 2 (inclusion)           : "+dat.node_id_type.get(2).size()+"\n");
        txt+=("Node type 3 (overlap)             : "+dat.node_id_type.get(3).size()+"\n");
        txt+=("Node type 4 (disjoint)            : "+dat.node_id_type.get(4).size()+"\n");        
        data_jTextPane.setText(txt);
    }
    
     public void update_type1(){
        //--Total nodes, etcs 
        String txt="";
        datasets dat=data.data;
        txt+=("Edges type 1 (identical)            : "+dat.total_type1+"\n");  
        txt+=("Complexe type 1                     : "+data.total_CC_type1+"\n");  
        
        data_jTextPane.setText(txt);
    }
     
      public void update_type2(){
        //--Total nodes, etcs 
        String txt="";
        datasets dat=data.data;
        txt+=("Edges type 2 (inclusion)            : "+dat.total_type2+"\n");    
        data_jTextPane.setText(txt);
    }
      
       public void update_type3(){
        //--Total nodes, etcs 
        String txt="";
        datasets dat=data.data;
        txt+=("Edges type 3 (overlap)            : "+dat.total_type3+"\n");    
        data_jTextPane.setText(txt);
    }
   
     public void update_type4(){
        //--Total nodes, etcs 
        String txt="";
        datasets dat=data.data;
        txt+=("Edges type 4 (disjoint)            : "+dat.total_type4+"\n");    
        data_jTextPane.setText(txt);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList Constraint_jList;
    private javax.swing.JButton Copy_jButton;
    private javax.swing.JComboBox Filter_ComboBox;
    private javax.swing.JButton Max_jButton;
    private javax.swing.JButton Min_jButton;
    private javax.swing.JList PossibleConstrant_jList;
    private javax.swing.JButton Remove_jButton;
    private javax.swing.JTable Result_jTable;
    private javax.swing.JComboBox Type_jComboBox;
    private javax.swing.JTextPane data_jTextPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    // End of variables declaration//GEN-END:variables
}
