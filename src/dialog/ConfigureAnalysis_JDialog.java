package dialog;

import COMPONENT_GRAPHER.datasets;
import config.Config;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.JFileChooser;


/*
 *  COMPOSITE-GRAPHER v1.0
 *  
 *  Copyright (C) 2016-2017  Etienne Lord
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class ConfigureAnalysis_JDialog extends javax.swing.JDialog {

    datasets data=null;                             // The dataset
    Frame frame;                                     //parent frame
    public boolean status_run=false;
     public boolean no_log=true;
     Config config =new Config();
    
    public ConfigureAnalysis_JDialog(java.awt.Frame parent, datasets data_) {
        super(parent, true);
         
        data=data_;
        this.frame=parent;
        setTitle("Configure analysis");
       
       
        initComponents();
        
        //--Init
        this.Name_jTextField.setText(data.filename);
        this.Directory_jTextField.setText(data.result_directory);
        this.Bootstrap_jTextField.setText(""+data.replicate);
        this.Bootstrap_jCheckBox.setSelected(data.bootstrap);
        this.remove_multiple_column_jCheckBox.setSelected(data.remove_multiple_column);
        this.remove_undefined_column_jCheckBox.setSelected(data.remove_undefined_column);
        this.Directory_jTextField.setText(data.result_directory);
//        this.Solution_jButton.setFont(Config.glyphicon);
//        this.Solution_jTextField.setEditable(false);
        
//        this.Solution_jButton.setText("\uf01e");
       
      data.get_info();
      this.Recommended_jLabel.setText("<html>(A minimum of <b>"+(int)(data.info_total_possible_nodes/0.05)+"</b> is recommended)</html>");
         //return "<html>Taxa (rows): <b>"+this.ntax+"</b> Characters (columns): <b>"+this.nchar+"</b> Treated columns: <b>"+this.total_valid_column+ "</b> Multistate characters: "+this.info_total_multiple+"</html>";
        this.InfojLabel.setText("<html>Total nodes: <b>"+data.info_total_possible_nodes+" </b>Taxa (rows): <b>"+data.info_Ntaxa+"</b> Characters (columns): <b>"+data.info_Nchar+"</b> Treated columns: <b>"+data.info_total_valid_column+ "</b> Multistate characters: "+data.info_total_multiple+"</html>");
       
//          if (data.state_strings.size()!=0&&data.info_total_multiple>0) {
//            String sti=state_strings.get(state_strings.size()-1);              
//            this.Solution_jTextField.setText(sti);
//            this.Solution_jTextField.setEnabled(false);
//            this.Solution_jTextField.setEditable(false);
//              this.Solution_jButton.setEnabled(false);
//          } else {
//              this.Solution_jTextField.setText("");
//              this.Solution_jTextField.setEnabled(false);
//              this.Solution_jTextField.setEditable(false);
//              this.Solution_jButton.setEnabled(false);
//          }
//        
        MessageText("Configure the analysis.","");
         // Set position
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
        this.setVisible(true); 
    }
          
    
    public datasets getData() {
        return this.data;
    }

    
     ///////////////////////////////////////////////////////////////////////////
    /// MESSAGE FONCTION

    /**
     * Affiche un message dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void MessageText(String text, String tooltip) {
        this.jStatusMessage.setEnabled(true);
        this.jStatusMessage.setForeground(new java.awt.Color(0, 51, 153));
        this.jStatusMessage.setBackground(Color.WHITE);
        this.jStatusMessage.setToolTipText(tooltip);
        this.jStatusMessage.setText(text);
    }

    /**
     * Affiche un message d'erreur en rouge dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void MessageErreur(String text, String tooltip) {
        this.jStatusMessage.setEnabled(true);
        this.jStatusMessage.setForeground(Color.RED);
        this.jStatusMessage.setBackground(Color.WHITE);
        this.jStatusMessage.setToolTipText(tooltip);
        this.jStatusMessage.setText(text);
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
        jPanel2 = new javax.swing.JPanel();
        Bootstrap_jTextField = new javax.swing.JTextField();
        Bootstrap_jCheckBox = new javax.swing.JCheckBox();
        Recommended_jLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        remove_multiple_column_jCheckBox = new javax.swing.JCheckBox();
        remove_undefined_column_jCheckBox = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        Directory_jTextField = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        graphml_jCheckBox = new javax.swing.JCheckBox();
        bipartite_jCheckBox = new javax.swing.JCheckBox();
        NoLog_jCheckBox = new javax.swing.JCheckBox();
        jStatusMessage = new javax.swing.JLabel();
        Name_jTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        InfojLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Run new analysis"));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        Bootstrap_jTextField.setText("100");
        Bootstrap_jTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Bootstrap_jTextFieldActionPerformed(evt);
            }
        });
        Bootstrap_jTextField.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                Bootstrap_jTextFieldInputMethodTextChanged(evt);
            }
        });

        Bootstrap_jCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        Bootstrap_jCheckBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Bootstrap_jCheckBox.setSelected(true);
        Bootstrap_jCheckBox.setText("Permutation test replicates");
        Bootstrap_jCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Bootstrap_jCheckBoxStateChanged(evt);
            }
        });

        Recommended_jLabel.setText("recommended");
        Recommended_jLabel.setToolTipText("This is calculated as the total possible network nodes divided by 0.05");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Bootstrap_jCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Bootstrap_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Recommended_jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Bootstrap_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Bootstrap_jCheckBox)
                    .addComponent(Recommended_jLabel))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jButton1.setText("Run");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        remove_multiple_column_jCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        remove_multiple_column_jCheckBox.setText("Remove from analysis the column(s) containing multiple states (e.g. {1,2,3})");
        remove_multiple_column_jCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remove_multiple_column_jCheckBoxActionPerformed(evt);
            }
        });

        remove_undefined_column_jCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        remove_undefined_column_jCheckBox.setText("Remove from analysis the column(s) containing undefined states (e.g. ?,-)");
        remove_undefined_column_jCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remove_undefined_column_jCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remove_multiple_column_jCheckBox)
                    .addComponent(remove_undefined_column_jCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(remove_multiple_column_jCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(remove_undefined_column_jCheckBox))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton3.setText("...");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Output directory");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Directory_jTextField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Directory_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Save intermediate networks"));

        graphml_jCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        graphml_jCheckBox.setText("Save graphml networks");

        bipartite_jCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        bipartite_jCheckBox.setText("Save bipartite networks");

        NoLog_jCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        NoLog_jCheckBox.setSelected(true);
        NoLog_jCheckBox.setText("No log (log.txt)");
        NoLog_jCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoLog_jCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(graphml_jCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bipartite_jCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(NoLog_jCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(graphml_jCheckBox)
                    .addComponent(bipartite_jCheckBox)
                    .addComponent(NoLog_jCheckBox)))
        );

        jStatusMessage.setForeground(new java.awt.Color(51, 51, 255));
        jStatusMessage.setText("Info");

        Name_jTextField.setText("jTextField2");
        Name_jTextField.setEnabled(false);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Matrix filename");

        InfojLabel.setText("Matrix description and info");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(InfojLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Name_jTextField))
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jStatusMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Name_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InfojLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2))
                    .addComponent(jStatusMessage, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
          try {
        data.replicate=Integer.valueOf(this.Bootstrap_jTextField.getText());        
        data.remove_multiple_column=this.remove_multiple_column_jCheckBox.isSelected();
        data.remove_undefined_column=this.remove_undefined_column_jCheckBox.isSelected();
        data.bipartite=this.bipartite_jCheckBox.isSelected();
          }catch(Exception e){
          e.printStackTrace();
          }
        this.status_run=true;
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
         try {
        data.replicate=Integer.valueOf(this.Bootstrap_jTextField.getText());
          }catch(Exception e){} 
        this.status_run=false;
        this.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void Bootstrap_jTextFieldInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_Bootstrap_jTextFieldInputMethodTextChanged
        try {
        this.data.replicate=Integer.valueOf(this.Bootstrap_jTextField.getText());
        } catch(Exception e){}
        
    }//GEN-LAST:event_Bootstrap_jTextFieldInputMethodTextChanged

    private void Bootstrap_jCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Bootstrap_jCheckBoxStateChanged
        data.bootstrap=this.Bootstrap_jCheckBox.isSelected();
    }//GEN-LAST:event_Bootstrap_jCheckBoxStateChanged

    private void remove_multiple_column_jCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remove_multiple_column_jCheckBoxActionPerformed
        data.remove_multiple_column=this.remove_multiple_column_jCheckBox.isSelected();       
        //data.compute_nodes();
        data.get_info();        
        this.Recommended_jLabel.setText("<html>(A minimum of <b>"+(int)(data.info_total_possible_nodes/0.05)+"</b> is recommended)</html>");
        this.InfojLabel.setText("<html>Total nodes: <b>"+data.info_total_possible_nodes+" </b>Taxa (rows): <b>"+data.info_Ntaxa+"</b> Characters (columns): <b>"+data.info_Nchar+"</b> Treated columns: <b>"+data.info_total_valid_column+ "</b> Multistate characters: "+data.info_total_multiple+"</html>");
    }//GEN-LAST:event_remove_multiple_column_jCheckBoxActionPerformed

    private void remove_undefined_column_jCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remove_undefined_column_jCheckBoxActionPerformed
        data.remove_undefined_column=this.remove_undefined_column_jCheckBox.isSelected();
       
        data.get_info();
        this.Recommended_jLabel.setText("<html>(A minimum of <b>"+(int)(data.info_total_possible_nodes/0.05)+"</b> is recommended)</html>");
        this.InfojLabel.setText("<html>Total nodes: <b>"+data.info_total_possible_nodes+" </b>Taxa (rows): <b>"+data.info_Ntaxa+"</b> Characters (columns): <b>"+data.info_Nchar+"</b> Treated columns: <b>"+data.info_total_valid_column+ "</b> Multistate characters: "+data.info_total_multiple+"</html>");

    }//GEN-LAST:event_remove_undefined_column_jCheckBoxActionPerformed

    private void NoLog_jCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoLog_jCheckBoxActionPerformed
        this.no_log=NoLog_jCheckBox.isSelected();
    }//GEN-LAST:event_NoLog_jCheckBoxActionPerformed

    private void Bootstrap_jTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Bootstrap_jTextFieldActionPerformed
       
        
    }//GEN-LAST:event_Bootstrap_jTextFieldActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       JFileChooser chooser = new JFileChooser(config.getExplorerPath());
         chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           data.result_directory=chooser.getSelectedFile().getPath();
           this.Directory_jTextField.setText(data.result_directory);
       }
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox Bootstrap_jCheckBox;
    private javax.swing.JTextField Bootstrap_jTextField;
    private javax.swing.JTextField Directory_jTextField;
    private javax.swing.JLabel InfojLabel;
    private javax.swing.JTextField Name_jTextField;
    private javax.swing.JCheckBox NoLog_jCheckBox;
    private javax.swing.JLabel Recommended_jLabel;
    private javax.swing.JCheckBox bipartite_jCheckBox;
    private javax.swing.JCheckBox graphml_jCheckBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel jStatusMessage;
    private javax.swing.JCheckBox remove_multiple_column_jCheckBox;
    private javax.swing.JCheckBox remove_undefined_column_jCheckBox;
    // End of variables declaration//GEN-END:variables
}
