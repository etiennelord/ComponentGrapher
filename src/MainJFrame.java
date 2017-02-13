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

import COMPONENT_GRAPHER.permutation_statistics;
import config.Config;
import config.util;
import dialog.AboutJDialog;
import dialog.CharEditorJDialog;
import dialog.ConfigureAnalysis_JDialog;
import dialog.ExportMatrixJDialog1;
import dialog.ExportNetworkJDialog;
import dialog.HelpJDialog;
import dialog.InformationJDialog;
import dialog.MatrixInfoJDialog;
import dialog.MatrixOptions;
import dialog.PolymorphicChar_EditorJDialog;
import dialog.RenameTaxaJDialog;
import dialog.TaxaEditorJDialog;

import matrixrenderer.VerticalTableHeaderCellRendered;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import matrixrenderer.ExcelAdapter;
import matrixrenderer.ExcelResultAdapter;
import matrixrenderer.MatrixTableCellRenderer;
import matrixrenderer.MatrixTableModel;
import matrixrenderer.MultilinesCellRenderer;
import matrixrenderer.PermutationStatistics_TableModel;
import matrixrenderer.Complexe_TableModel;
import matrixrenderer.RowNumberTable;
import matrixrenderer.SummaryTableCellRenderer;
import matrixrenderer.Summary_TableModel;
import matrixrenderer.PvalueCellRenderer;
import matrixrenderer.Nodes_TableModel;
import matrixrenderer.SummaryRowFilter;



public class MainJFrame extends javax.swing.JFrame implements Observer{

    public JFrame that;
    JButton MatrixJButton; //Where we display matrix info
    public Config config=new Config(); //--Configuration file
    public COMPONENT_GRAPHER.datasets data=new COMPONENT_GRAPHER.datasets(); //--The dataset
    public COMPONENT_GRAPHER.summary_statistics summary;        //--The main summary_statistics (current table)
    public COMPONENT_GRAPHER.permutation_statistics statistics; //--The main permutation_statistics
    
    RowNumberTable rowtable;                 //--For the row label
     ExcelResultAdapter SummaryMatrixTable;  //--For eawsy copying data from table (Cut-and-Paste)
     ExcelResultAdapter StatisticsTable;
     ExcelAdapter MainMatrixTable;     
     InformationJDialog loading_info;
     InformationJDialog saving_info;
     ExcelResultAdapter ComplexeTable;
     ExcelResultAdapter NodeTable;
     
      TableRowSorter summary_sorter;
     
    SwingWorker<Boolean, String> ComponetGrapher_SwingWorker;
    
    /**
     * Creates new form MainJFrame
     */
    public MainJFrame() {
        
        that=this;
        initComponents();
        init();
       
    }
   
    /**
     * Initialize the interface
     */
    public void init() {        
        
        ImageIcon main_icon=new ImageIcon("data"+File.separator+"app.png");     
        loading_info=new InformationJDialog(this,"Loading analysis","Loading ");
        saving_info=new InformationJDialog(this,"Saving analysis","Saving ");
        this.setIconImage(main_icon.getImage());
        jTabbedPane.setEnabledAt(1, false);
        jTabbedPane.setEnabledAt(2, false);
        jTabbedPane.setEnabledAt(3, false);
        
        jInfo.setFont(Config.glyphicon);
        jInfo.setText("\uf129");
//        CopyAll2_jButton.setFont(Config.glyphicon);
//        CopyAll2_jButton.setText("\uf0c5");
        CopyMainAll_jButton.setFont(Config.glyphicon);
        CopyMainAll_jButton.setText("\uf0c5");
        CopySummary_jButton.setFont(Config.glyphicon);
        CopySummary_jButton.setText("\uf0c5");
//        VisualNetwork=new NetworkExplorer(); --to do
//        VisualNetwork.init();
    
        MatteBorder border = new MatteBorder(1, 1, 1, 1, Color.BLACK);
        
        this.Matrix_jTable.setModel(new MatrixTableModel());
        
        this.Matrix_jTable.setGridColor(Color.DARK_GRAY);
        this.Statistics_jTable.setModel(new PermutationStatistics_TableModel());
        this.Statistics_jTable.setGridColor(Color.DARK_GRAY);
        Summary_TableModel summaryModel=new Summary_TableModel();
        this.Summary_jTable.setModel(summaryModel);
        this.Summary_jTable.setGridColor(Color.DARK_GRAY);
        //this.Summary_jTable.setBorder(border);
        this.Complexe_jTable.setModel(new Complexe_TableModel());
        this.Complexe_jTable.setGridColor(Color.DARK_GRAY);
        this.Nodes_jTable.setModel(new Nodes_TableModel());
        this.Nodes_jTable.setGridColor(Color.DARK_GRAY);
        
        //--Allow copy-and-paste 
        MainMatrixTable = new ExcelAdapter(this.Matrix_jTable);
        SummaryMatrixTable = new ExcelResultAdapter(this.Summary_jTable);
        StatisticsTable = new ExcelResultAdapter(this.Statistics_jTable); 
        ComplexeTable = new ExcelResultAdapter(this.Complexe_jTable); 
        NodeTable=new ExcelResultAdapter(this.Nodes_jTable); 
        
        //--Sorter
        summary_sorter = new TableRowSorter<Summary_TableModel>(summaryModel);
          Summary_jTable.setRowSorter(summary_sorter);
        
        //this.Matrix_jTable.setDefaultRenderer(Object.class,new ColorField());
        MatrixTableCellRenderer cellMatrixRenderer = new MatrixTableCellRenderer();
        SummaryTableCellRenderer cellSummaryRenderer = new SummaryTableCellRenderer();
        MultilinesCellRenderer cellmultiRenderer = new MultilinesCellRenderer();
        PvalueCellRenderer cellpRenderer = new PvalueCellRenderer();
                
        this.Matrix_jTable.setDefaultRenderer(String.class, cellMatrixRenderer);
        this.Summary_jTable.setDefaultRenderer(String.class, cellSummaryRenderer);
        this.Complexe_jTable.setDefaultRenderer(String[].class, cellmultiRenderer);
        //this.R2_jTable.setDefaultRenderer(Float[].class, cellpRenderer);
        
        ImageIcon icon=new ImageIcon("data\\glyphicons-151-edit_17.png");                    
        //--Test load glyphicon
        
        rowtable=new RowNumberTable(this.Matrix_jTable);
        rowtable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row =  Matrix_jTable.rowAtPoint(e.getPoint());                   
                    //System.out.println("Row index selected " + row + " "+ tm.data.charlabels.get(row));
                    if (e.getClickCount() == 2) {
                       if (data.inverse_matrix_table) {
                          RenameTaxaJDialog d=new RenameTaxaJDialog(that,data,row);
                            d.display();
                           data.label.set(row, d.NewName);
                       } else {
                        CharEditorJDialog ch=new CharEditorJDialog(that, data);
                       }
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
//                        System.out.println("editing");
//                        System.out.println("Row index selected " + row + " "+ data.charlabels.get(row));
                    }
                }
        });
                
        this.Matrix_jTable.getTableHeader().setResizingAllowed(true);  
        this.Matrix_jTable.getTableHeader().addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            
            int col =  Matrix_jTable.columnAtPoint(e.getPoint());
            String name =  Matrix_jTable.getColumnName(col);
            //--debug System.out.println("Column index selected " + col + " " + name);
            if (e.getClickCount()==2||SwingUtilities.isRightMouseButton(e)) {
                 if (data.inverse_matrix_table) {
                     CharEditorJDialog ch=new CharEditorJDialog(that, data);
                } else {
                    RenameTaxaJDialog d=new RenameTaxaJDialog(that,data,col);
                     d.display();
                    data.label.set(col, d.NewName);
                }
               updateMatrixTableInfo();
            }
            
        }}             
       );
       
        //Matrix_jTable.getTableHeader().setDefaultRenderer(new VerticalTableHeaderCellRendered());
       // this.Matrix_jTable.setD
        TableCellRenderer headerRenderer = new VerticalTableHeaderCellRendered(true);
       
        //this.Matrix_jTable.setTableHeader(new MatrixTableHeader());
         Enumeration columns=this.Matrix_jTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {            
           TableColumn tmp=((TableColumn)columns.nextElement());
           tmp.setHeaderRenderer(headerRenderer);
           tmp.setCellRenderer(cellMatrixRenderer);
           
        }
        
        headerRenderer = new VerticalTableHeaderCellRendered(false);
        columns=this.Summary_jTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {            
           TableColumn tmp=((TableColumn)columns.nextElement());
           tmp.setHeaderRenderer(headerRenderer);
           tmp.setCellRenderer(cellSummaryRenderer);
           
        }
        columns=this.Statistics_jTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {            
           TableColumn tmp=((TableColumn)columns.nextElement());
           tmp.setHeaderRenderer(headerRenderer);
        }
        
          this.Summary_jTable.getTableHeader().setResizingAllowed(true);        
    //--Set min and max
          this.Summary_jTable.getColumnModel().getColumn(0).setPreferredWidth(27);
          this.Summary_jTable.getColumnModel().getColumn(1).setPreferredWidth(200);
          
       this.MatrixjScrollPane.setRowHeaderView(rowtable);
       MatrixJButton=new JButton("");
        MatrixJButton.setIconTextGap(20);
        MatrixJButton.setText("<html>Matrix<br>20x20</html>");
        MatrixJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
             MatrixOptionAction();
            }
        });
        MatrixJButton.setHorizontalAlignment(JLabel.CENTER);
        MatrixJButton.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        MatrixJButton.setIcon(icon);
       
       MatrixJButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
       this.MatrixjScrollPane.setCorner(MatrixjScrollPane.UPPER_LEFT_CORNER, MatrixJButton);     
      
       //--Test matrix
       //LoadTable("sample\\Smith_Caron_wo_absent_trait.nex");
       // LoadTable("sample\\matrice_ebapteste.nex");
       // LoadTable("sample\\Test_matrix-rhinos2.nex");       
      if  (config.isSet("last_analysis")&&util.FileExists(config.get("last_analysis"))) {
          LoadAnalysis(config.get("last_analysis"));
      } else if (config.isSet("last_matrix")) {
           if(!LoadTable(config.get("last_matrix"))) {
             config.remove("last_matrix");
             MessageError("Unable to load pri", null);
           }
       }         
    }
    
    private void MatrixOptionAction() {
           MatrixOptions m=new MatrixOptions(this,data);
           updateMatrixTableInfo();
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane = new javax.swing.JTabbedPane();
        Matrix_jPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jIcon = new javax.swing.JLabel();
        Run_Analysis_jButton = new javax.swing.JButton();
        Filename_jTextField = new javax.swing.JTextField();
        CopyMainAll_jButton = new javax.swing.JButton();
        jInfo = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        Polymorphic_jButton = new javax.swing.JButton();
        MatrixjScrollPane = new javax.swing.JScrollPane();
        Matrix_jTable = new javax.swing.JTable();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        Summary_jPanel = new javax.swing.JPanel();
        Summary_sum_jPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        CopySummary_jButton = new javax.swing.JButton();
        Filter_ComboBox = new javax.swing.JComboBox();
        Summary_jScrollPane = new javax.swing.JScrollPane();
        Summary_jTable = new javax.swing.JTable();
        Statistiques_jPanel = new javax.swing.JPanel();
        Statistics_jPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        Statistics_jScrollPane = new javax.swing.JScrollPane();
        Statistics_jTable = new javax.swing.JTable();
        Nodes_jScrollPane = new javax.swing.JScrollPane();
        Nodes_jTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        NodeStatistics_jComboBox = new javax.swing.JComboBox();
        ConnectedComponent_jPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Complexe_jTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        progress = new javax.swing.JProgressBar();
        percent_jLabel = new javax.swing.JLabel();
        Message_jLabel = new javax.swing.JLabel();
        Stop_jButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        ImportMatrix_jMenuItem = new javax.swing.JMenuItem();
        ExportMatrix_jMenuItem = new javax.swing.JMenuItem();
        ImportState_jMenuItem = new javax.swing.JMenuItem();
        SaveAnalysis_jMenuItem = new javax.swing.JMenuItem();
        LoadAnalysis_jMenuItem = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        PolymorphicChar_jMenu = new javax.swing.JMenu();
        EditCharacter_jMenuItem = new javax.swing.JMenuItem();
        EditTaxa_jMenuItem = new javax.swing.JMenuItem();
        Define_state_jMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        Help_Jitem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CompositeGrapher");
        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane.setBackground(new java.awt.Color(255, 255, 255));

        Matrix_jPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setMaximumSize(new java.awt.Dimension(800, 49));
        jPanel5.setPreferredSize(new java.awt.Dimension(800, 45));

        jIcon.setText("  ");

        Run_Analysis_jButton.setBackground(new java.awt.Color(255, 255, 255));
        Run_Analysis_jButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Run_Analysis_jButton.setText("Run analysis");
        Run_Analysis_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Run_Analysis_jButtonActionPerformed(evt);
            }
        });

        Filename_jTextField.setEditable(false);
        Filename_jTextField.setBackground(new java.awt.Color(255, 255, 255));

        CopyMainAll_jButton.setText("c");
        CopyMainAll_jButton.setToolTipText("Copy current matrix to Clipboard");
        CopyMainAll_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CopyMainAll_jButtonActionPerformed(evt);
            }
        });

        jInfo.setText("i");
        jInfo.setToolTipText("Display informations for this matrix");
        jInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jInfoActionPerformed(evt);
            }
        });

        jPanel4.setBackground(java.awt.Color.yellow);
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Matrix legend");

        jLabel4.setText("polymorphic character");
        jLabel4.setToolTipText("Notice: For character(s) with multiple possible states, one state will be chosen during simulation.");

        jPanel12.setBackground(java.awt.Color.pink);
        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel12.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );

        jLabel6.setText("undefined character or masked data");
        jLabel6.setToolTipText("Notice: the characters (?,-,*) are ignored during the analysis.");

        Polymorphic_jButton.setText("define states");
        Polymorphic_jButton.setToolTipText("If polymorphic characters are found in the matrix, you will need to define the characters used in the analysis.");
        Polymorphic_jButton.setEnabled(false);
        Polymorphic_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Polymorphic_jButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(Run_Analysis_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(Filename_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 817, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CopyMainAll_jButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jInfo))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Polymorphic_jButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jIcon)
                    .addComponent(Run_Analysis_jButton)
                    .addComponent(CopyMainAll_jButton)
                    .addComponent(jInfo)
                    .addComponent(Filename_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Polymorphic_jButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        MatrixjScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        MatrixjScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        MatrixjScrollPane.setToolTipText("");
        MatrixjScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        MatrixjScrollPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        MatrixjScrollPane.setMaximumSize(new java.awt.Dimension(500, 500));

        Matrix_jTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        Matrix_jTable.setCellSelectionEnabled(true);
        Matrix_jTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        Matrix_jTable.getTableHeader().setReorderingAllowed(false);
        MatrixjScrollPane.setViewportView(Matrix_jTable);

        javax.swing.GroupLayout Matrix_jPanelLayout = new javax.swing.GroupLayout(Matrix_jPanel);
        Matrix_jPanel.setLayout(Matrix_jPanelLayout);
        Matrix_jPanelLayout.setHorizontalGroup(
            Matrix_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Matrix_jPanelLayout.createSequentialGroup()
                .addGroup(Matrix_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MatrixjScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 1072, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Matrix_jPanelLayout.setVerticalGroup(
            Matrix_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Matrix_jPanelLayout.createSequentialGroup()
                .addGroup(Matrix_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Matrix_jPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MatrixjScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Data", Matrix_jPanel);

        Summary_jPanel.setEnabled(false);

        Summary_sum_jPanel.setBackground(new java.awt.Color(255, 255, 255));
        Summary_sum_jPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Summary_sum_jPanel.setPreferredSize(new java.awt.Dimension(1080, 45));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Filter results");

        CopySummary_jButton.setText("c");
        CopySummary_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CopySummary_jButtonActionPerformed(evt);
            }
        });

        Filter_ComboBox.setEditable(true);
        Filter_ComboBox.setToolTipText("Filter your results. Enter a search string");
        Filter_ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter_ComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Summary_sum_jPanelLayout = new javax.swing.GroupLayout(Summary_sum_jPanel);
        Summary_sum_jPanel.setLayout(Summary_sum_jPanelLayout);
        Summary_sum_jPanelLayout.setHorizontalGroup(
            Summary_sum_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Summary_sum_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(10, 10, 10)
                .addComponent(Filter_ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CopySummary_jButton)
                .addContainerGap())
        );
        Summary_sum_jPanelLayout.setVerticalGroup(
            Summary_sum_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Summary_sum_jPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Summary_sum_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(CopySummary_jButton)
                    .addComponent(Filter_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );

        Summary_jScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        Summary_jScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        Summary_jScrollPane.setDoubleBuffered(true);

        Summary_jTable.setAutoCreateRowSorter(true);
        Summary_jTable.setModel(new Summary_TableModel());
        Summary_jTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        Summary_jTable.setCellSelectionEnabled(true);
        Summary_jTable.setDoubleBuffered(true);
        Summary_jTable.setDragEnabled(true);
        Summary_jScrollPane.setViewportView(Summary_jTable);

        javax.swing.GroupLayout Summary_jPanelLayout = new javax.swing.GroupLayout(Summary_jPanel);
        Summary_jPanel.setLayout(Summary_jPanelLayout);
        Summary_jPanelLayout.setHorizontalGroup(
            Summary_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Summary_sum_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(Summary_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        Summary_jPanelLayout.setVerticalGroup(
            Summary_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Summary_jPanelLayout.createSequentialGroup()
                .addComponent(Summary_sum_jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Summary_jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 574, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane.addTab("Summary", Summary_jPanel);

        Statistiques_jPanel.setEnabled(false);

        Statistics_jPanel.setBackground(new java.awt.Color(255, 255, 255));
        Statistics_jPanel.setPreferredSize(new java.awt.Dimension(1080, 45));

        jLabel8.setBackground(new java.awt.Color(0, 102, 205));
        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText(" Network statistics");
        jLabel8.setOpaque(true);

        javax.swing.GroupLayout Statistics_jPanelLayout = new javax.swing.GroupLayout(Statistics_jPanel);
        Statistics_jPanel.setLayout(Statistics_jPanelLayout);
        Statistics_jPanelLayout.setHorizontalGroup(
            Statistics_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Statistics_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        Statistics_jPanelLayout.setVerticalGroup(
            Statistics_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Statistics_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        Statistics_jScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        Statistics_jScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        Statistics_jScrollPane.setDoubleBuffered(true);

        Statistics_jTable.setAutoCreateRowSorter(true);
        Statistics_jTable.setModel(new Summary_TableModel());
        Statistics_jTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        Statistics_jTable.setCellSelectionEnabled(true);
        Statistics_jTable.setDoubleBuffered(true);
        Statistics_jTable.setDragEnabled(true);
        Statistics_jScrollPane.setViewportView(Statistics_jTable);

        Nodes_jScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        Nodes_jScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        Nodes_jTable.setAutoCreateRowSorter(true);
        Nodes_jTable.setModel(new Nodes_TableModel());
        Nodes_jTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        Nodes_jScrollPane.setViewportView(Nodes_jTable);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(0, 102, 205));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText(" Node specific statistics");
        jLabel2.setOpaque(true);

        NodeStatistics_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Indegree                  (type 2 network)", "Outdegree               (type 2 network)", "Closeness                (type 3 network)", "Betweenness           (type 3 network)", "Percent (%) triplets (type 3 network)" }));
        NodeStatistics_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NodeStatistics_jComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NodeStatistics_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NodeStatistics_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout Statistiques_jPanelLayout = new javax.swing.GroupLayout(Statistiques_jPanel);
        Statistiques_jPanel.setLayout(Statistiques_jPanelLayout);
        Statistiques_jPanelLayout.setHorizontalGroup(
            Statistiques_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Statistics_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(Statistics_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Nodes_jScrollPane)
        );
        Statistiques_jPanelLayout.setVerticalGroup(
            Statistiques_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Statistiques_jPanelLayout.createSequentialGroup()
                .addComponent(Statistics_jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Statistics_jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Nodes_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Permutation statistics", Statistiques_jPanel);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Complete network", "Type 1 network", "Type 2 network", "Type 3 network" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(0, 102, 205));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("  Network complexes (connected components)");
        jLabel7.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Complexe_jTable.setModel(new matrixrenderer.Complexe_TableModel());
        Complexe_jTable.setRowHeight(52);
        jScrollPane2.setViewportView(Complexe_jTable);

        javax.swing.GroupLayout ConnectedComponent_jPanelLayout = new javax.swing.GroupLayout(ConnectedComponent_jPanel);
        ConnectedComponent_jPanel.setLayout(ConnectedComponent_jPanelLayout);
        ConnectedComponent_jPanelLayout.setHorizontalGroup(
            ConnectedComponent_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ConnectedComponent_jPanelLayout.setVerticalGroup(
            ConnectedComponent_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ConnectedComponent_jPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Complexes", ConnectedComponent_jPanel);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        percent_jLabel.setText("0% ");

        Message_jLabel.setText("CompositeGrapher v1.0");

        Stop_jButton.setBackground(new java.awt.Color(255, 0, 0));
        Stop_jButton.setText("Stop");
        Stop_jButton.setToolTipText("Stop the current analysis.");
        Stop_jButton.setEnabled(false);
        Stop_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Stop_jButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Message_jLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Stop_jButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(percent_jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(Message_jLabel)
                                    .addComponent(percent_jLabel)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Stop_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jMenu1.setText("File");

        ImportMatrix_jMenuItem.setText("Import matrix");
        ImportMatrix_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportMatrix_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(ImportMatrix_jMenuItem);

        ExportMatrix_jMenuItem.setText("Export matrix");
        ExportMatrix_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportMatrix_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(ExportMatrix_jMenuItem);

        ImportState_jMenuItem.setText("Import states");
        ImportState_jMenuItem.setToolTipText("Import character/state descriptions from file (sse the help section for details)");
        ImportState_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportState_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(ImportState_jMenuItem);

        SaveAnalysis_jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        SaveAnalysis_jMenuItem.setText("Save analysis");
        SaveAnalysis_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveAnalysis_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(SaveAnalysis_jMenuItem);

        LoadAnalysis_jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        LoadAnalysis_jMenuItem.setText("Load analysis");
        LoadAnalysis_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadAnalysis_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(LoadAnalysis_jMenuItem);

        jMenuItem6.setText("Export network");
        jMenuItem6.setToolTipText("Export generated networks");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem2.setText("Export results");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Quit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu4.setText("Analysis");

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Run analysis");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        jMenuBar1.add(jMenu4);

        PolymorphicChar_jMenu.setText("Edit");
        PolymorphicChar_jMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PolymorphicChar_jMenuActionPerformed(evt);
            }
        });

        EditCharacter_jMenuItem.setText("Edit characters");
        EditCharacter_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditCharacter_jMenuItemActionPerformed(evt);
            }
        });
        PolymorphicChar_jMenu.add(EditCharacter_jMenuItem);

        EditTaxa_jMenuItem.setText("Edit taxa");
        EditTaxa_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditTaxa_jMenuItemActionPerformed(evt);
            }
        });
        PolymorphicChar_jMenu.add(EditTaxa_jMenuItem);

        Define_state_jMenuItem.setText("Define character states");
        Define_state_jMenuItem.setToolTipText("");
        Define_state_jMenuItem.setEnabled(false);
        PolymorphicChar_jMenu.add(Define_state_jMenuItem);

        jMenuBar1.add(PolymorphicChar_jMenu);

        jMenu3.setText("Help");

        jMenuItem8.setText("About");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        Help_Jitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        Help_Jitem.setText("Help");
        Help_Jitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Help_JitemActionPerformed(evt);
            }
        });
        jMenu3.add(Help_Jitem);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ImportMatrix_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportMatrix_jMenuItemActionPerformed
            JFileChooser chooser = new JFileChooser(config.getExplorerPath());
             FileFilter filter_nex = new FileNameExtensionFilter("Nexus matrix file (.nex .nexus)", "nex", "nexus");
             FileFilter filter_phylip = new FileNameExtensionFilter("Phylip matrix file (.phy)", "phylip", "phy");
             FileFilter filter_text = new FileNameExtensionFilter("Simple matrix file (.txt .mat)", "txt", "mat");             
             chooser.addChoosableFileFilter(filter_nex);
             chooser.addChoosableFileFilter(filter_phylip);
             chooser.addChoosableFileFilter(filter_text);
             chooser.setFileFilter(filter_nex);
             //chooser.addChoosableFileFilter(filter_phylip);
             chooser.setDialogTitle("Import matrix file");
             chooser.setToolTipText("Import matrix in nexus format or in phylip format.");
             int returnVal = chooser.showOpenDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("Matrix filename: " +chooser.getSelectedFile().getAbsolutePath());
                    MatrixTableModel tm=(MatrixTableModel)this.Matrix_jTable.getModel();
              
               config.setExplorerPath(chooser.getSelectedFile().getPath());
               config.Save();
               
               this.LoadTable(chooser.getSelectedFile().getAbsolutePath());
               jTabbedPane.setSelectedIndex(0);
               jTabbedPane.setEnabledAt(1, true);
                jTabbedPane.setEnabledAt(2, false);
                jTabbedPane.setEnabledAt(3, false);
                this.progress.setValue(0);

          }
    }//GEN-LAST:event_ImportMatrix_jMenuItemActionPerformed

    private void ExportMatrix_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportMatrix_jMenuItemActionPerformed
       ExportMatrixJDialog1 savematrix=new ExportMatrixJDialog1(this, data);
       this.Filename_jTextField.setText(savematrix.filename);
       this.data.filename=savematrix.filename;
    }//GEN-LAST:event_ExportMatrix_jMenuItemActionPerformed

    private void Stop_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Stop_jButtonActionPerformed
        //--Top the curent RUN
        ComponetGrapher_SwingWorker.cancel(true);
    }//GEN-LAST:event_Stop_jButtonActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
      if (data.nchar==0) {
            JOptionPane.showMessageDialog(this, "Please load a matrix file before starting an analysis.","No matrix loaded", JOptionPane.WARNING_MESSAGE);
        } else {
            if (data.matrixNotDefined()) {
                JOptionPane.showMessageDialog(this, "The current matrix contains polymorphic characters.\nPlease, go into the 'Edit->Define character states'\n to select the states for this analysis.","Polymorphic characters detected", JOptionPane.WARNING_MESSAGE);
            } else {
                Run();
            }
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        AboutJDialog about=new AboutJDialog(this);
        
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void Run_Analysis_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Run_Analysis_jButtonActionPerformed
        if (data.nchar==0) {
            JOptionPane.showMessageDialog(this, "Please load a matrix file before starting an analysis.","No matrix loaded", JOptionPane.WARNING_MESSAGE);
        } else {
            if (data.matrixNotDefined()) {
                JOptionPane.showMessageDialog(this, "The current matrix contains polymorphic characters.\nPlease, go into the 'Edit->Define character states'\n to select the states for this analysis.","Polymorphic characters detected", JOptionPane.WARNING_MESSAGE);
            } else {
                Run();
            }
        }
    }//GEN-LAST:event_Run_Analysis_jButtonActionPerformed

    private void jInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jInfoActionPerformed
       MatrixInfoJDialog m=new MatrixInfoJDialog(this, data);
    }//GEN-LAST:event_jInfoActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        Complexe_TableModel r1=(Complexe_TableModel)Complexe_jTable.getModel();
        r1.setNetworkType(this.jComboBox1.getSelectedIndex());
        r1.fireTableDataChanged();
        r1.fireTableStructureChanged();
        Complexe_jTable.setModel(r1);
        TableCellRenderer headerRenderer = new VerticalTableHeaderCellRendered(false);   
        Enumeration columns=this.Complexe_jTable.getColumnModel().getColumns();          
          while (columns.hasMoreElements()) {                    
          TableColumn tmp=((TableColumn)columns.nextElement());
           tmp.setHeaderRenderer(headerRenderer);          
         }
         DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
          centerRenderer.setHorizontalAlignment(JLabel.CENTER);  
          this.Complexe_jTable.getColumnModel().getColumn(0).setPreferredWidth(50); //--Complexe
          this.Complexe_jTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); //--Complexe
          this.Complexe_jTable.getColumnModel().getColumn(1).setPreferredWidth(200); //--Char
          this.Complexe_jTable.getColumnModel().getColumn(2).setPreferredWidth(50); //--Total
          this.Complexe_jTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
          this.Complexe_jTable.getColumnModel().getColumn(3).setPreferredWidth(1000); //--Taxa
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
       if (statistics==null) {
            JOptionPane.showMessageDialog(this, "You need to analyse the matrix before saving the results.","No analysis performed", JOptionPane.WARNING_MESSAGE);
            return;
        }    
        ExportNetworkJDialog export=new ExportNetworkJDialog(this, data);       
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void Filter_ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter_ComboBoxActionPerformed
         int mode=this.Filter_ComboBox.getSelectedIndex();
                String search_term=(String)this.Filter_ComboBox.getSelectedItem();
                //if (search_term.indexOf("[")>-1) search_term=search_term.substring(0,search_term.indexOf("["));
                search_term=search_term.trim();                                
                //RowFilter<Summary_TableModel, Object> rf = null;
                //If current expression doesn't parse, don't update.
                if (search_term.isEmpty()) {                    
                       summary_sorter.setRowFilter(null);
                } else {                   
                try {
                      summary_sorter.setRowFilter(new SummaryRowFilter(search_term));
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                  
                }
    }//GEN-LAST:event_Filter_ComboBoxActionPerformed

    private void ImportState_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportState_jMenuItemActionPerformed
            JFileChooser chooser = new JFileChooser(config.getExplorerPath());
            chooser.setDialogTitle("Select character state file");
            FileFilter filter_text = new FileNameExtensionFilter("Character state file (.txt)", "txt", "mat");                
            chooser.addChoosableFileFilter(filter_text);
            chooser.setFileFilter(filter_text);
                int returnVal = chooser.showOpenDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("Import state filename: " +chooser.getSelectedFile().getAbsolutePath());
                    MatrixTableModel tm=(MatrixTableModel)this.Matrix_jTable.getModel();
                    tm.data.load_charstate(chooser.getSelectedFile().getAbsolutePath());
                    config.setExplorerPath(chooser.getSelectedFile().getPath());
                    config.Save();
                    tm.fireTableDataChanged();
                    tm.fireTableStructureChanged();
                    this.Matrix_jTable.setModel(tm);
                    updateMatrixTableInfo();
             }
    }//GEN-LAST:event_ImportState_jMenuItemActionPerformed

    private void CopyMainAll_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CopyMainAll_jButtonActionPerformed
       MainMatrixTable.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CopyAll"));
    }//GEN-LAST:event_CopyMainAll_jButtonActionPerformed

    private void EditCharacter_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditCharacter_jMenuItemActionPerformed
       CharEditorJDialog charj=new CharEditorJDialog(this, data);
       this.updateMatrixTableInfo();
    }//GEN-LAST:event_EditCharacter_jMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       //--Do clean up here...
        
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void Polymorphic_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Polymorphic_jButtonActionPerformed
        PolymorphicChar_EditorJDialog poly=new PolymorphicChar_EditorJDialog(this,data);
    }//GEN-LAST:event_Polymorphic_jButtonActionPerformed

    private void EditTaxa_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditTaxa_jMenuItemActionPerformed
      TaxaEditorJDialog tx=new TaxaEditorJDialog(this, data);
      updateMatrixTableInfo();
    }//GEN-LAST:event_EditTaxa_jMenuItemActionPerformed

    private void PolymorphicChar_jMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PolymorphicChar_jMenuActionPerformed
       PolymorphicChar_EditorJDialog poly=new PolymorphicChar_EditorJDialog(this,data);
    }//GEN-LAST:event_PolymorphicChar_jMenuActionPerformed

    private void SaveAnalysis_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveAnalysis_jMenuItemActionPerformed
         final JFileChooser chooser = new JFileChooser(config.getExplorerPath());
             chooser.setDialogTitle("Saving analysis");
            // FileFilter filter_csv = new FileNameExtensionFilter("CSV files (results only)", "csv");
             FileFilter filter_json = new FileNameExtensionFilter("JSON file (whole analysis)", "json");
             
             
             chooser.addChoosableFileFilter(filter_json);
             //chooser.addChoosableFileFilter(filter_csv);
             chooser.setFileFilter(filter_json);
                int returnVal = chooser.showOpenDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                            config.set("last_analysis", chooser.getSelectedFile().getAbsolutePath());
                            config.Save();
                             SwingWorker sw=new SwingWorker() {

                                @Override
                                protected Object doInBackground() {
                                  saving_info.setVisible(true);
                                  //--we need to take care of csv file herr 
                                  
                                  if (statistics==null) {
                                            statistics=new permutation_statistics(data);
                                       }
                                       statistics.saveAnalysis(chooser.getSelectedFile().getAbsolutePath());
                                
                                return true;
                                }

                                protected void done() {
                                    saving_info.setVisible(false);
                                    Message("Saved analysis", "");
                                }

                            }; 
                           sw.run();
                }
    }//GEN-LAST:event_SaveAnalysis_jMenuItemActionPerformed

    private void LoadAnalysis_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadAnalysis_jMenuItemActionPerformed
       
          final JFileChooser chooser = new JFileChooser(config.getExplorerPath());
             chooser.setDialogTitle("Loading analysis");
             FileFilter filter_json = new FileNameExtensionFilter("JSON file (whole analysis)", "json");
             chooser.addChoosableFileFilter(filter_json);
             chooser.setFileFilter(filter_json);
                int returnVal = chooser.showOpenDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                  LoadAnalysis(chooser.getSelectedFile().getAbsolutePath());               
               }
      
    }//GEN-LAST:event_LoadAnalysis_jMenuItemActionPerformed

    private void CopySummary_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CopySummary_jButtonActionPerformed
       SummaryMatrixTable.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CopyAll"));
    }//GEN-LAST:event_CopySummary_jButtonActionPerformed

    private void NodeStatistics_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NodeStatistics_jComboBoxActionPerformed
//          Nodes_TableModel n1=(Nodes_TableModel)Nodes_jTable.getModel();
//          n1.setDisplayedStatistic(this.NodeStatistics_jComboBox.getSelectedIndex());
//          n1.fireTableDataChanged();
//          n1.fireTableStructureChanged();
//          this.Nodes_jTable.setModel(n1);
          updateResultsTable();
    }//GEN-LAST:event_NodeStatistics_jComboBoxActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        if (statistics==null) {
            JOptionPane.showMessageDialog(this, "You need to analyse the matrix before saving the results.","No analysis performed", JOptionPane.WARNING_MESSAGE);
            return;
        }    
        final JFileChooser chooser = new JFileChooser(config.getExplorerPath());
             chooser.setDialogTitle("Saving results");
             FileFilter filter_csv = new FileNameExtensionFilter("CSV files (results only)", "csv");
            // FileFilter filter_json = new FileNameExtensionFilter("JSON file (whole analysis)", "json");
              chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
             
            // chooser.addChoosableFileFilter(filter_json);
             //chooser.addChoosableFileFilter(filter_csv);
             chooser.setFileFilter(filter_csv);
                int returnVal = chooser.showOpenDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                         
                             SwingWorker sw=new SwingWorker() {

                                @Override
                                protected Object doInBackground() {
                                  saving_info.setVisible(true);
                                  //--we need to take care of csv file herr 
                                  
                                  if (statistics==null) {
                                            statistics=new permutation_statistics(data);
                                       }
                                       //statistics.saveAnalysis(chooser.getSelectedFile().getAbsolutePath());
                                       statistics.output_csv(chooser.getSelectedFile().getAbsolutePath());
                                return true;
                                }

                                protected void done() {
                                    saving_info.setVisible(false);
                                    Message("Saved analysis", "");
                                }

                            }; 
                           sw.run();
                }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void Help_JitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Help_JitemActionPerformed
       HelpJDialog help=new HelpJDialog(that);
    }//GEN-LAST:event_Help_JitemActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });
    }

    boolean LoadAnalysis(final String filename) {
           SwingWorker sw=new SwingWorker() {

                        @Override
                        protected Object doInBackground() {
                          loading_info.setVisible(true);
                           
                            statistics=new permutation_statistics(data);                            
                            if (statistics.loadAnalysis(filename)) {
                                  config.set("last_analysis", filename);
                                  config.Save(); 
                                   Message("Loaded analysis "+filename,""); 
                                   data=statistics.reference_data;
                                    SaveAnalysis_jMenuItem.setEnabled(true);
                                    updateMatrixTableInfo();
                                    updateResultsTable();
                            } else {                                
                                 MessageError("Failled to load analysis "+filename,"");
                            }                       
                        return true;
                        }


                        protected void done() {
                            loading_info.setVisible(false);                          
                        }

                    }; 
                   sw.run();
            return true;       
    }
    
    boolean LoadTable(String filename) {
        data=new COMPONENT_GRAPHER.datasets();
        summary=null;
        statistics=null;
        boolean r=data.load_morphobank_nexus(filename);               
         if (!r||data.nchar==0||data.ntax==0) r=data.load_simple(filename);
         if (!r||data.nchar==0||data.ntax==0) {                     
             MessageError("Unable to load "+filename,"");
             return false;
         } else {                    
            config.set("last_matrix",filename);
            config.Save();
            updateMatrixTableInfo();
            return true;
         }       
    }
    
    void updateMatrixTableInfo() {
        //--Interface
         jTabbedPane.setEnabledAt(1, false);
         jTabbedPane.setEnabledAt(2, false);
         jTabbedPane.setEnabledAt(3, false);
        
        if (data.states.size()>0) {
              Polymorphic_jButton.setEnabled(true);
          } else {
              Polymorphic_jButton.setEnabled(false);
          }
          rowtable.repaint();        
          
          MatrixJButton.setText("<html>Matrix<br>"+data.intmaxcol()+"x"+data.intmaxrow()+"</html>");
                  Filename_jTextField.setText(data.filename);
                  MatrixTableModel tm=(MatrixTableModel)this.Matrix_jTable.getModel();
                        tm.setData(data);        
                        tm.fireTableDataChanged();
                        tm.fireTableStructureChanged();
                        //We need to update the model also
                         this.Matrix_jTable.setModel(tm);
                          TableCellRenderer headerRenderer = new VerticalTableHeaderCellRendered(true);  
                         Enumeration columns=this.Matrix_jTable.getColumnModel().getColumns();
                            while (columns.hasMoreElements()) {            
                               ((TableColumn)columns.nextElement()).setHeaderRenderer(headerRenderer);
                            }
        
    }
    
    /**
     * This is called after a Run()
     */
    public void updateResultsTable() {
        //System.out.println(statistics); 
        //System.out.println(statistics.reference); 
        this.progress.setValue(100);
        this.percent_jLabel.setText("100%");
        summary=statistics.reference;
         data=statistics.reference_data;
         
         this.SaveAnalysis_jMenuItem.setEnabled(true);
         jTabbedPane.setEnabledAt(1, true);
         jTabbedPane.setEnabledAt(2, true);
         jTabbedPane.setEnabledAt(3, true);
        
         //--It is possible that it was not previously analysed
         if (summary==null) return;
        
         //--PermutationStatistics
         PermutationStatistics_TableModel sm2=(PermutationStatistics_TableModel)this.Statistics_jTable.getModel();
          sm2.setData(statistics);
          sm2.fireTableDataChanged();
          sm2.fireTableStructureChanged();
          this.Statistics_jTable.setModel(sm2);
          
          //--Node
          Nodes_TableModel n1=(Nodes_TableModel)Nodes_jTable.getModel();
          n1.setData(statistics);
          n1.setDisplayedStatistic((int)this.NodeStatistics_jComboBox.getSelectedIndex());
          n1.fireTableDataChanged();
          n1.fireTableStructureChanged();
          this.Nodes_jTable.setModel(n1);
          
          //R1- Complexe_jTable          
          Complexe_TableModel r1=(Complexe_TableModel)Complexe_jTable.getModel();
          r1.setData(summary);
          r1.setNetworkType(1);
          r1.fireTableDataChanged();
          r1.fireTableStructureChanged();
          this.Complexe_jTable.setModel(r1);

         //--Summary Table
        Summary_TableModel sm=(Summary_TableModel)this.Summary_jTable.getModel();
         sm.setData(summary);
        sm.fireTableDataChanged();
        
        sm.fireTableStructureChanged();
         TableCellRenderer headerRenderer = new VerticalTableHeaderCellRendered(false);  
         this.Summary_jTable.setModel(sm);
         
         //--Styling
         Enumeration columns=this.Summary_jTable.getColumnModel().getColumns();          
          while (columns.hasMoreElements()) {                    
          TableColumn tmp=((TableColumn)columns.nextElement());
           tmp.setHeaderRenderer(headerRenderer);          
        }
                  
          for (int i=0; i<=21; i++) {
              this.Summary_jTable.getColumnModel().getColumn(i).setMinWidth(50);              
          }
          this.Summary_jTable.getColumnModel().getColumn(1).setPreferredWidth(200); //--Node name          
          this.Summary_jTable.getColumnModel().getColumn(22).setMinWidth(50); //--Taxa_count             
          this.Summary_jTable.getColumnModel().getColumn(23).setMinWidth(1000); //--Taxa           
          
          DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
          leftRenderer.setHorizontalAlignment(JLabel.LEFT);  
          DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
          centerRenderer.setHorizontalAlignment(JLabel.CENTER);  
          this.Summary_jTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
          
          this.Summary_jTable.getColumnModel().getColumn(1).setPreferredWidth(200); //--Node id     
          this.Summary_jTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);          
          this.Summary_jTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
          this.Summary_jTable.getColumnModel().getColumn(8).setPreferredWidth(200); //--
          this.Summary_jTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
          
          this.Summary_jTable.getColumnModel().getColumn(10).setCellRenderer(centerRenderer);
          this.Summary_jTable.getColumnModel().getColumn(22).setCellRenderer(centerRenderer);
          this.Summary_jTable.getColumnModel().getColumn(23).setCellRenderer(leftRenderer);
          
          this.Statistics_jTable.getColumnModel().getColumn(0).setPreferredWidth(300); //Statistiscs   
          this.Statistics_jTable.getColumnModel().getColumn(1).setPreferredWidth(50); //--Node id     
          this.Statistics_jTable.getColumnModel().getColumn(2).setPreferredWidth(70); //--Reference
//          for (int i=10; i<statistics.replicates.size()+10;i++) {              
//              this.Statistics_jTable.getColumnModel().getColumn(i).setPreferredWidth(100);
//              this.Statistics_jTable.getColumnModel().getColumn(i).setMinWidth(100);
//          } //--Randomization
          columns=this.Statistics_jTable.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {            
               TableColumn tmp=((TableColumn)columns.nextElement());
               tmp.setHeaderRenderer(headerRenderer);
            }
            columns=this.Nodes_jTable.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {            
               TableColumn tmp=((TableColumn)columns.nextElement());
               tmp.setHeaderRenderer(headerRenderer);
            }
          
//          for (int i=5; i<statistics.replicates.size()+10;i++) {              
//              this.Nodes_jTable.getColumnModel().getColumn(i).setPreferredWidth(100);
//              this.Nodes_jTable.getColumnModel().getColumn(i).setMinWidth(100);
//          } //--Randomization
          
          this.Complexe_jTable.getColumnModel().getColumn(0).setPreferredWidth(50); //--Complexe
          this.Complexe_jTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); //--Complexe
          this.Complexe_jTable.getColumnModel().getColumn(1).setPreferredWidth(200); //--Char
          this.Complexe_jTable.getColumnModel().getColumn(2).setPreferredWidth(50); //--Total
          this.Complexe_jTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
          this.Complexe_jTable.getColumnModel().getColumn(3).setPreferredWidth(1000); //--Taxa
          
          columns=this.Complexe_jTable.getColumnModel().getColumns();          
          while (columns.hasMoreElements()) {                    
          TableColumn tmp=((TableColumn)columns.nextElement());
           tmp.setHeaderRenderer(headerRenderer);          
         }
          
          this.Nodes_jTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
          this.Nodes_jTable.getColumnModel().getColumn(0).setPreferredWidth(50);
          this.Nodes_jTable.getColumnModel().getColumn(1).setPreferredWidth(250);
          this.Nodes_jTable.getColumnModel().getColumn(2).setPreferredWidth(50);

    }
    
 
    
     private void Run() {
        
         ConfigureAnalysis_JDialog configure=new ConfigureAnalysis_JDialog(this,data);
        this.data=configure.getData(); //--Update data         
        if (configure.status_run) {
            Message("Analyzing "+data.filename+"","");
            Message("Log is found in: "+data.result_directory,"Analyzing "+data.filename);
            Stop_jButton.setEnabled(true);
             progress.setValue(0);
             percent_jLabel.setText("0 %");
            ComponetGrapher_SwingWorker=getNewRunWorker(configure.no_log);
            ComponetGrapher_SwingWorker.execute(); 
            //--See below for code
            
        }
       
      }
    
     ///////////////////////////////////////////////////////////////////////////
     //--Main computation loop       
             
    SwingWorker<Boolean, String>  getNewRunWorker(final boolean no_log) {
              progress.setValue(0);
        SwingWorker<Boolean, String> nt=new SwingWorker<Boolean, String>()  {

        @Override
        protected Boolean doInBackground() throws Exception {
                 
                 setProgress(1);                 
                 data.setCallback(new Callable() {
                     @Override
                     public Object call() {
                      // if (!no_log) {
                         try {
                         String st=data.st_results.toString();
                         //util.log("log.txt", st);                                               
                        } catch(Exception e) {
                            e.printStackTrace();
                        }                        
                     // }
                      return true;
                     }
                    });
                    //--Initialize
                    setProgress(5); 
                    long timerunnig=System.currentTimeMillis(); 
                                        
                    //--Permutation statistic here
                    statistics=new permutation_statistics(data);                    
                 //--If permutation, do it here                    
                    statistics.setCallback(new Callable() {                     
                       @Override
                     public Object call() {
                        try {
                        final int b=statistics.current_replicate;
                        int pro=10+(b*90/(statistics.replicate));
                        if (pro>100) pro=100;
                        if (pro<0) pro=0;
                         setProgress(pro);
                         return true;
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                     }
                    });
                statistics.generate_statistics();                
                setProgress(100);
                return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        // TO DO
                    } else {
                        // TO DO
                    }
                }
            }


           @Override
           protected void done(){
                        
               if (this.isCancelled()) {
                   setProgress(0);     
                   MessageError("Cancelled analyzing "+data.filename+"","");
                    updateResultsTable();
               }  else {
                     setProgress(100);     
                    Message("Done analyzing "+data.filename+"","");
                    updateResultsTable();               
               }
               Stop_jButton.setEnabled(false);            
           }
        }; //End SwingWorker declaration
             
              nt.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int prog=(Integer)evt.getNewValue();                                
                                progress.setValue(prog);
                                percent_jLabel.setText(prog+" %");
                                progress.setToolTipText("Currently launched tasks ["+statistics.current_replicate+" / "+statistics.replicate+"]");
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                                progress.setValue(100);   
                                percent_jLabel.setText("100 %");
                                progress.setToolTipText("Analysis of "+data.filename+" is done.");
                                //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
              return nt;
       }
     
     ///////////////////////////////////////////////////////////////////////////
    /// MESSAGE FONCTION

    void Message(String text, String tooltip) {
        this.Message_jLabel.setEnabled(true);
        this.Message_jLabel.setForeground(new java.awt.Color(0, 51, 153));
        this.Message_jLabel.setBackground(Color.WHITE);
        this.Message_jLabel.setToolTipText(tooltip);
        this.Message_jLabel.setText(text+"\n");
    }

    void MessageError(String text, String tooltip) {
        this.Message_jLabel.setEnabled(true);
        this.Message_jLabel.setForeground(Color.RED);
        this.Message_jLabel.setBackground(Color.WHITE);
        this.Message_jLabel.setToolTipText(tooltip);
        this.Message_jLabel.setText(text);
    }

   
   




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Complexe_jTable;
    private javax.swing.JPanel ConnectedComponent_jPanel;
    private javax.swing.JButton CopyMainAll_jButton;
    private javax.swing.JButton CopySummary_jButton;
    private javax.swing.JMenuItem Define_state_jMenuItem;
    private javax.swing.JMenuItem EditCharacter_jMenuItem;
    private javax.swing.JMenuItem EditTaxa_jMenuItem;
    private javax.swing.JMenuItem ExportMatrix_jMenuItem;
    private javax.swing.JTextField Filename_jTextField;
    private javax.swing.JComboBox Filter_ComboBox;
    private javax.swing.JMenuItem Help_Jitem;
    private javax.swing.JMenuItem ImportMatrix_jMenuItem;
    private javax.swing.JMenuItem ImportState_jMenuItem;
    private javax.swing.JMenuItem LoadAnalysis_jMenuItem;
    private javax.swing.JPanel Matrix_jPanel;
    private javax.swing.JTable Matrix_jTable;
    private javax.swing.JScrollPane MatrixjScrollPane;
    private javax.swing.JLabel Message_jLabel;
    private javax.swing.JComboBox NodeStatistics_jComboBox;
    private javax.swing.JScrollPane Nodes_jScrollPane;
    private javax.swing.JTable Nodes_jTable;
    private javax.swing.JMenu PolymorphicChar_jMenu;
    private javax.swing.JButton Polymorphic_jButton;
    private javax.swing.JButton Run_Analysis_jButton;
    private javax.swing.JMenuItem SaveAnalysis_jMenuItem;
    private javax.swing.JPanel Statistics_jPanel;
    private javax.swing.JScrollPane Statistics_jScrollPane;
    private javax.swing.JTable Statistics_jTable;
    private javax.swing.JPanel Statistiques_jPanel;
    private javax.swing.JButton Stop_jButton;
    private javax.swing.JPanel Summary_jPanel;
    private javax.swing.JScrollPane Summary_jScrollPane;
    private javax.swing.JTable Summary_jTable;
    private javax.swing.JPanel Summary_sum_jPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jIcon;
    private javax.swing.JButton jInfo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel percent_jLabel;
    private javax.swing.JProgressBar progress;
    // End of variables declaration//GEN-END:variables

    @Override
    public void update(Observable o, Object o1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
