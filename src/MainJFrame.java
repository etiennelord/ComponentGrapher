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
import COMPONENT_GRAPHER.util;
import config.Config;
import dialog.CharEditorJDialog;
import dialog.ConfigureAnalysis_JDialog;
import dialog.ExportMatrixJDialog1;
import dialog.ExportNetworkJDialog;
import dialog.MatrixInfoJDialog;
import dialog.MatrixOptions;
import dialog.PolymorphicChar_EditorJDialog;
import dialog.RenameTaxaJDialog;
import matrixrenderer.VerticalTableHeaderCellRendered;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import matrixrenderer.ExcelAdapter;
import matrixrenderer.ExcelResultAdapter;
import matrixrenderer.MatrixTableCellRenderer;
import matrixrenderer.MatrixTableModel;
import matrixrenderer.MultilinesCellRenderer;
import matrixrenderer.PermutationStatistics_TableModel;
import matrixrenderer.R1_TableModel;
import matrixrenderer.RowNumberTable;
import matrixrenderer.SummaryTableCellRenderer;
import matrixrenderer.Summary_TableModel;
import matrixrenderer.PvalueCellRenderer;
import matrixrenderer.R2_TableModel;
import visual.NetworkExplorer;




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
     NetworkExplorer VisualNetwork;
     
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
        
        ImageIcon main_icon=new ImageIcon("data\\app.png");         
        this.setIconImage(main_icon.getImage());
        jTabbedPane.setEnabledAt(1, true);
        jTabbedPane.setEnabledAt(2, false);
        jTabbedPane.setEnabledAt(3, false);
        jTabbedPane.setEnabledAt(4, false);
        jTabbedPane.setEnabledAt(5, false);
        jTabbedPane.setEnabledAt(6, false);
        jTabbedPane.setEnabledAt(7, false);
        jTabbedPane.setEnabledAt(8, false);
        
        
        jInfo.setFont(Config.glyphicon);
        jInfo.setText("\uf129");
        CopyAll2_jButton.setFont(Config.glyphicon);
        CopyAll2_jButton.setText("\uf0c5");
        CopyMainAll_jButton.setFont(Config.glyphicon);
        CopyMainAll_jButton.setText("\uf0c5");
        VisualNetwork=new NetworkExplorer();
        VisualNetwork.init();
        Network_jPanel.add(VisualNetwork);
        //TableColumn column = new TableColumn();
	//	column.setHeaderValue(" ");
      //this.Matrix_jTable.addColumn( column );
//       final DefaultCategoryDataset dataset = new DefaultCategoryDataset( ); 
//       dataset.addValue(1.0,"Between" ,"Type1");
//       dataset.addValue(2.0,"Between" ,"Type2");
//       dataset.addValue(3.0,"Between" ,"Type3");
//dataset.addValue( 1.0 , fiat , speed ); dataset.addValue( 3.0 , fiat , userrating ); dataset.addValue( 5.0 , fiat , millage ); dataset.addValue( 5.0 , fiat , safety );
// dataset.setValue("IPhone 5s", new Double( 20 ) );
// dataset.setValue("SamSung Grand", new Double( 20 ) );
// dataset.setValue("MotoG", new Double( 40 ) );
// dataset.setValue("Nokia Lumia", new Double( 10 ) );
//DefaultPieDataset dataset = new DefaultPieDataset( );
// dataset.setValue("IPhone 5s", new Double( 20 ) );
// dataset.setValue("SamSung Grand", new Double( 20 ) );
// dataset.setValue("MotoG", new Double( 40 ) );
// dataset.setValue("Nokia Lumia", new Double( 10 ) );
//
// JFreeChart chart = ChartFactory.createPieChart(
// "Mobile Sales", // chart title
// dataset, // data
// true, // include legend
// true,
// false
// );
//		//JFreeChart chart = ChartFactory.createBarChart( "Title", "Category", "Score", dataset, PlotOrientation.VERTICAL, true, true, false );
//                ChartPanel CP = new ChartPanel(chart);
//                this.Summary_sum_jPanel.setLayout(new java.awt.BorderLayout());
//                this.Summary_sum_jPanel.add(CP,BorderLayout.CENTER);
//                this.Summary_sum_jPanel.validate();
//                this.Summary_sum_jPanel.repaint();
        this.Matrix_jTable.setModel(new MatrixTableModel());
        this.Statistics_jTable.setModel(new PermutationStatistics_TableModel());
        this.Summary_jTable.setModel(new Summary_TableModel());
        this.R1_jTable.setModel(new R1_TableModel());
        this.R2_jTable.setModel(new R2_TableModel());
        //--Allow copy-and-paste 
        MainMatrixTable = new ExcelAdapter(this.Matrix_jTable);
        SummaryMatrixTable = new ExcelResultAdapter(this.Summary_jTable);
        StatisticsTable = new ExcelResultAdapter(this.Statistics_jTable); 
        ExcelResultAdapter R1Table = new ExcelResultAdapter(this.R1_jTable); 
        
        //this.Matrix_jTable.setDefaultRenderer(Object.class,new ColorField());
        MatrixTableCellRenderer cellMatrixRenderer = new MatrixTableCellRenderer();
        SummaryTableCellRenderer cellSummaryRenderer = new SummaryTableCellRenderer();
        MultilinesCellRenderer cellmultiRenderer = new MultilinesCellRenderer();
        PvalueCellRenderer cellpRenderer = new PvalueCellRenderer();
        
        this.Matrix_jTable.setDefaultRenderer(String.class, cellMatrixRenderer);
        this.Summary_jTable.setDefaultRenderer(String.class, cellSummaryRenderer);
        this.R1_jTable.setDefaultRenderer(String[].class, cellmultiRenderer);
        this.R2_jTable.setDefaultRenderer(Float[].class, cellpRenderer);
        
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
                        System.out.println("editing");
                        System.out.println("Row index selected " + row + " "+ data.charlabels.get(row));
                    }
                }
        });
                
        this.Matrix_jTable.getTableHeader().setResizingAllowed(true);  
        this.Matrix_jTable.getTableHeader().addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            
            int col =  Matrix_jTable.columnAtPoint(e.getPoint());
            String name =  Matrix_jTable.getColumnName(col);
            System.out.println("Column index selected " + col + " " + name);
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
      
       //LoadTable("sample\\Smith_Caron_wo_absent_trait.nex");
       if (config.isSet("last_matrix")) {
           if(!LoadTable(config.get("last_matrix"))) {
               
           }
       }
      // LoadTable("sample\\matrice_ebapteste.nex");
       // LoadTable("sample\\Test_matrix-rhinos2.nex");
       //this.jScrollPane1.updateUI();
         
    }
    
    private void MatrixOptionAction() {
           MatrixOptions m=new MatrixOptions(this,data);
           updateMatrixTableInfo();
    }
    
    
    /**
     * The different experiments 
     * Put in config
     */
    public void load_projects() {
        
    }
    
     /**
     * The different experiments 
     */
    public void save_projects() {
        
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
        Log_jPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        LongLog_jTextArea = new javax.swing.JTextArea();
        Summary_jPanel = new javax.swing.JPanel();
        Summary_sum_jPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        Filter_ComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        Summary_jScrollPane = new javax.swing.JScrollPane();
        Summary_jTable = new javax.swing.JTable();
        Statistiques_jPanel = new javax.swing.JPanel();
        Statistics_jPanel = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        CopyAll2_jButton = new javax.swing.JButton();
        Statistics_jScrollPane = new javax.swing.JScrollPane();
        Statistics_jTable = new javax.swing.JTable();
        R1_jPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        R1_jTable = new javax.swing.JTable();
        R2_jPanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        R2_jTable = new javax.swing.JTable();
        R3_jPanel = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        R3_jTable = new javax.swing.JTable();
        R4_jPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        R4_jTable = new javax.swing.JTable();
        R5_jPanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        R5_jTable = new javax.swing.JTable();
        R6_jPanel = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        R6_jTable = new javax.swing.JTable();
        Network_Jpanel = new javax.swing.JPanel();
        Summary_sum_jPanel1 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        Filter_ComboBox1 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        Network_jPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        progress = new javax.swing.JProgressBar();
        percent_jLabel = new javax.swing.JLabel();
        Message_jLabel = new javax.swing.JLabel();
        Stop_jButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        ExportMatrix_jMenuItem = new javax.swing.JMenuItem();
        ImportMatrix_jMenuItem = new javax.swing.JMenuItem();
        ImportState_jMenuItem = new javax.swing.JMenuItem();
        SaveAnalysis_jMenuItem = new javax.swing.JMenuItem();
        LoadAnalysis_jMenuItem = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();

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
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MatrixjScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Data", Matrix_jPanel);

        Log_jPanel.setEnabled(false);

        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        LongLog_jTextArea.setColumns(20);
        LongLog_jTextArea.setRows(5);
        jScrollPane3.setViewportView(LongLog_jTextArea);

        javax.swing.GroupLayout Log_jPanelLayout = new javax.swing.GroupLayout(Log_jPanel);
        Log_jPanel.setLayout(Log_jPanelLayout);
        Log_jPanelLayout.setHorizontalGroup(
            Log_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        Log_jPanelLayout.setVerticalGroup(
            Log_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
        );

        jTabbedPane.addTab("Log", Log_jPanel);

        Summary_jPanel.setEnabled(false);

        Summary_sum_jPanel.setBackground(javax.swing.UIManager.getDefaults().getColor("FormattedTextField.selectionForeground"));
        Summary_sum_jPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Summary_sum_jPanel.setPreferredSize(new java.awt.Dimension(1080, 45));

        jButton1.setText("Export as csv");

        Filter_ComboBox.setEditable(true);
        Filter_ComboBox.setModel(new javax.swing.DefaultComboBoxModel(Config.ClusteringOption));
        Filter_ComboBox.setToolTipText("Filter your results. Enter a search string");
        Filter_ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter_ComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Filter results");

        javax.swing.GroupLayout Summary_sum_jPanelLayout = new javax.swing.GroupLayout(Summary_sum_jPanel);
        Summary_sum_jPanel.setLayout(Summary_sum_jPanelLayout);
        Summary_sum_jPanelLayout.setHorizontalGroup(
            Summary_sum_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Summary_sum_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Filter_ComboBox, 0, 710, Short.MAX_VALUE)
                .addGap(166, 166, 166)
                .addComponent(jButton1)
                .addContainerGap())
        );
        Summary_sum_jPanelLayout.setVerticalGroup(
            Summary_sum_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Summary_sum_jPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Summary_sum_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1)
                    .addComponent(Filter_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
                .addComponent(Summary_sum_jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Summary_jScrollPane))
        );

        jTabbedPane.addTab("Summary", Summary_jPanel);

        Statistiques_jPanel.setEnabled(false);

        Statistics_jPanel.setBackground(javax.swing.UIManager.getDefaults().getColor("FormattedTextField.selectionForeground"));
        Statistics_jPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Statistics_jPanel.setPreferredSize(new java.awt.Dimension(1080, 45));

        jButton3.setText("Export csv");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        CopyAll2_jButton.setText("c");
        CopyAll2_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CopyAll2_jButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Statistics_jPanelLayout = new javax.swing.GroupLayout(Statistics_jPanel);
        Statistics_jPanel.setLayout(Statistics_jPanelLayout);
        Statistics_jPanelLayout.setHorizontalGroup(
            Statistics_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Statistics_jPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(CopyAll2_jButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap())
        );
        Statistics_jPanelLayout.setVerticalGroup(
            Statistics_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Statistics_jPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Statistics_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(CopyAll2_jButton))
                .addContainerGap())
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

        javax.swing.GroupLayout Statistiques_jPanelLayout = new javax.swing.GroupLayout(Statistiques_jPanel);
        Statistiques_jPanel.setLayout(Statistiques_jPanelLayout);
        Statistiques_jPanelLayout.setHorizontalGroup(
            Statistiques_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Statistics_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(Statistics_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        Statistiques_jPanelLayout.setVerticalGroup(
            Statistiques_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Statistiques_jPanelLayout.createSequentialGroup()
                .addComponent(Statistics_jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Statistics_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Permutation statistics", Statistiques_jPanel);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Complete network\t", "Type 1 network", "Type 2 network", "Type 3 network" }));
        jComboBox1.setSelectedIndex(1);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton2.setText("Export csv");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        R1_jTable.setModel(new R1_TableModel());
        R1_jTable.setRowHeight(52);
        jScrollPane2.setViewportView(R1_jTable);

        javax.swing.GroupLayout R1_jPanelLayout = new javax.swing.GroupLayout(R1_jPanel);
        R1_jPanel.setLayout(R1_jPanelLayout);
        R1_jPanelLayout.setHorizontalGroup(
            R1_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        R1_jPanelLayout.setVerticalGroup(
            R1_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(R1_jPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Complexes", R1_jPanel);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(1080, 45));

        jButton4.setText("Export report");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addContainerGap())
        );

        R2_jTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(R2_jTable);

        javax.swing.GroupLayout R2_jPanelLayout = new javax.swing.GroupLayout(R2_jPanel);
        R2_jPanel.setLayout(R2_jPanelLayout);
        R2_jPanelLayout.setHorizontalGroup(
            R2_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        R2_jPanelLayout.setVerticalGroup(
            R2_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(R2_jPanelLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Most stable components", R2_jPanel);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setPreferredSize(new java.awt.Dimension(1080, 45));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );

        R3_jTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(R3_jTable);

        javax.swing.GroupLayout R3_jPanelLayout = new javax.swing.GroupLayout(R3_jPanel);
        R3_jPanel.setLayout(R3_jPanelLayout);
        R3_jPanelLayout.setHorizontalGroup(
            R3_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        R3_jPanelLayout.setVerticalGroup(
            R3_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(R3_jPanelLayout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Least stable components", R3_jPanel);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(1080, 45));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );

        R4_jTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(R4_jTable);

        javax.swing.GroupLayout R4_jPanelLayout = new javax.swing.GroupLayout(R4_jPanel);
        R4_jPanel.setLayout(R4_jPanelLayout);
        R4_jPanelLayout.setHorizontalGroup(
            R4_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        R4_jPanelLayout.setVerticalGroup(
            R4_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(R4_jPanelLayout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Pivots-points", R4_jPanel);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setPreferredSize(new java.awt.Dimension(1080, 45));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );

        R5_jTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane5.setViewportView(R5_jTable);

        javax.swing.GroupLayout R5_jPanelLayout = new javax.swing.GroupLayout(R5_jPanel);
        R5_jPanel.setLayout(R5_jPanelLayout);
        R5_jPanelLayout.setHorizontalGroup(
            R5_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        R5_jPanelLayout.setVerticalGroup(
            R5_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(R5_jPanelLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Pivots-betweenness", R5_jPanel);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setPreferredSize(new java.awt.Dimension(1080, 45));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );

        R6_jTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane6.setViewportView(R6_jTable);

        javax.swing.GroupLayout R6_jPanelLayout = new javax.swing.GroupLayout(R6_jPanel);
        R6_jPanel.setLayout(R6_jPanelLayout);
        R6_jPanelLayout.setHorizontalGroup(
            R6_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        R6_jPanelLayout.setVerticalGroup(
            R6_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(R6_jPanelLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Pivots-triplets", R6_jPanel);

        Summary_sum_jPanel1.setBackground(javax.swing.UIManager.getDefaults().getColor("FormattedTextField.selectionForeground"));
        Summary_sum_jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Summary_sum_jPanel1.setPreferredSize(new java.awt.Dimension(1080, 45));

        jButton5.setText("Export as csv");

        Filter_ComboBox1.setEditable(true);
        Filter_ComboBox1.setModel(new javax.swing.DefaultComboBoxModel(Config.ClusteringOption));
        Filter_ComboBox1.setToolTipText("Filter your results. Enter a search string");
        Filter_ComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter_ComboBox1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Filter results");

        javax.swing.GroupLayout Summary_sum_jPanel1Layout = new javax.swing.GroupLayout(Summary_sum_jPanel1);
        Summary_sum_jPanel1.setLayout(Summary_sum_jPanel1Layout);
        Summary_sum_jPanel1Layout.setHorizontalGroup(
            Summary_sum_jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Summary_sum_jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Filter_ComboBox1, 0, 710, Short.MAX_VALUE)
                .addGap(166, 166, 166)
                .addComponent(jButton5)
                .addContainerGap())
        );
        Summary_sum_jPanel1Layout.setVerticalGroup(
            Summary_sum_jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Summary_sum_jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Summary_sum_jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jLabel2)
                    .addComponent(Filter_ComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Network_jPanel.setPreferredSize(new java.awt.Dimension(1050, 585));

        javax.swing.GroupLayout Network_jPanelLayout = new javax.swing.GroupLayout(Network_jPanel);
        Network_jPanel.setLayout(Network_jPanelLayout);
        Network_jPanelLayout.setHorizontalGroup(
            Network_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        Network_jPanelLayout.setVerticalGroup(
            Network_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 585, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Network_JpanelLayout = new javax.swing.GroupLayout(Network_Jpanel);
        Network_Jpanel.setLayout(Network_JpanelLayout);
        Network_JpanelLayout.setHorizontalGroup(
            Network_JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Summary_sum_jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(Network_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        Network_JpanelLayout.setVerticalGroup(
            Network_JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Network_JpanelLayout.createSequentialGroup()
                .addComponent(Summary_sum_jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Network_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Network", Network_Jpanel);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        percent_jLabel.setText("0% ");

        Message_jLabel.setText("CompositeGrapher v1.0");

        Stop_jButton.setBackground(new java.awt.Color(255, 0, 0));
        Stop_jButton.setText("Stop");
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

        jMenuItem2.setText("New matrix");
        jMenu1.add(jMenuItem2);

        ExportMatrix_jMenuItem.setText("Export matrix");
        ExportMatrix_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportMatrix_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(ExportMatrix_jMenuItem);

        ImportMatrix_jMenuItem.setText("Import matrix");
        ImportMatrix_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportMatrix_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(ImportMatrix_jMenuItem);

        ImportState_jMenuItem.setText("Import states");
        ImportState_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportState_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(ImportState_jMenuItem);

        SaveAnalysis_jMenuItem.setText("Save analysis");
        SaveAnalysis_jMenuItem.setEnabled(false);
        jMenu1.add(SaveAnalysis_jMenuItem);

        LoadAnalysis_jMenuItem.setText("Load analysis");
        LoadAnalysis_jMenuItem.setEnabled(false);
        jMenu1.add(LoadAnalysis_jMenuItem);

        jMenuItem6.setText("Export network");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem1.setText("Quit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu4.setText("Analysis");

        jMenuItem10.setText("Matrix informations");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem10);

        jMenuItem5.setText("Run analysis");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        jMenuBar1.add(jMenu4);

        jMenu2.setText("Edit");

        jMenuItem3.setText("Insert columns");
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Insert rows");
        jMenu2.add(jMenuItem4);

        jMenuItem7.setText("Edit characters");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");

        jMenuItem8.setText("About");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        jMenuItem9.setText("Help");
        jMenu3.add(jMenuItem9);

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
                jTabbedPane.setEnabledAt(4, false);
                jTabbedPane.setEnabledAt(5, false);
                jTabbedPane.setEnabledAt(6, false);
                jTabbedPane.setEnabledAt(7, false);
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
            Run();
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void Run_Analysis_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Run_Analysis_jButtonActionPerformed
        if (data.nchar==0) {
            JOptionPane.showMessageDialog(this, "Please load a matrix file before starting an analysis.","No matrix loaded", JOptionPane.WARNING_MESSAGE);
        } else {
            Run();
        }
    }//GEN-LAST:event_Run_Analysis_jButtonActionPerformed

    private void jInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jInfoActionPerformed
       MatrixInfoJDialog m=new MatrixInfoJDialog(this, data);
    }//GEN-LAST:event_jInfoActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        R1_TableModel r1=(R1_TableModel)R1_jTable.getModel();
        r1.setNetworkType(this.jComboBox1.getSelectedIndex());
        r1.fireTableDataChanged();
        r1.fireTableStructureChanged();
        R1_jTable.setModel(r1);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
       ExportNetworkJDialog export=new ExportNetworkJDialog(this, data);       
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void Filter_ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter_ComboBoxActionPerformed
//        this.RemoveFromGroupSequence_jButton.setEnabled(false);
//        //Update the displayed data in the table
//        InfoSequenceTableModel tm=(InfoSequenceTableModel)this.jTable1.getModel();
//        //Remove previous selection
//        for (InfoSequence s:tm.data) s.setSelected(false);
//        //
//        int mode=this.Filter_ComboBox.getSelectedIndex();
//        int count=0; //--ResultCound
//        String searchString=(String)this.Filter_ComboBox.getSelectedItem();
//        if (mode==0) {
//            //--Reset the table
//            tm.data.clear();
//            tm.setData(MultipleInfoSequence);
//            Message("Found "+MultipleInfoSequence.size()+" sequence(s)","", this.jStatusMessageSequence);
//        } else {
//            if (searchString.startsWith(">")) {
//                String s=searchString.substring(1);
//                Vector<Integer> resultIndex=search(s, LoadSequenceFrame.MODE_LENMORE);
//                count=resultIndex.size();
//                tm.data.clear();
//                for (Integer index:resultIndex) {
//                    tm.addData(MultipleInfoSequence.get(index));
//                }
//                Message("Found "+resultIndex.size()+" sequence(s)","",this.jStatusMessageSequence);
//
//            } else
//            if (searchString.startsWith("<")) {
//                String s=searchString.substring(1);
//                Vector<Integer> resultIndex=search(s, LoadSequenceFrame.MODE_LENLESS);
//                count=resultIndex.size();
//                tm.data.clear();
//                for (Integer index:resultIndex) {
//                    tm.addData(MultipleInfoSequence.get(index));
//                }
//                Message("Found "+resultIndex.size()+" sequence(s)","",this.jStatusMessageSequence);
//
//            } else
//            if (searchString.startsWith("MultipleSequences:")) {
//                this.RemoveFromGroupSequence_jButton.setEnabled(true);
//                //-- We select only the sequence of a particular group...
//                //--Get the ID
//                String IDS=searchString.substring(searchString.indexOf(" ")+1, searchString.indexOf("\t"));
//                int ID=0;
//                try {
//                    ID=Integer.valueOf(IDS);
//                } catch(Exception e) {ID=0;}
//                tm.data.clear();
//                Vector<Integer> resultIndex=df.getSequenceIDinMultipleSequence(ID);
//                count=resultIndex.size();
//                for (InfoSequence S:MultipleInfoSequence) {
//                    if (resultIndex.contains(S.getId())) {
//                        tm.addData(S);
//                    }
//                }
//                Message("Found "+df.getSequenceIDinMultipleSequence(ID).size()+" sequence(s)","",this.jStatusMessageSequence);
//            }else
//            if (searchString.startsWith("Alignment:")) {
//                this.RemoveFromGroupSequence_jButton.setEnabled(true);
//                //-- We select only the sequence of a particular group...
//                //--Get the ID
//                String IDS=searchString.substring(searchString.indexOf(" ")+1, searchString.indexOf("\t"));
//                int ID=0;
//                try {
//                    ID=Integer.valueOf(IDS);
//                } catch(Exception e) {ID=0;}
//                tm.data.clear();
//                Vector<Integer> resultIndex=df.getSequenceIDinAlignment(ID);
//                count=resultIndex.size();
//                for (InfoSequence S:MultipleInfoSequence) {
//                    if (resultIndex.contains(S.getId())) {
//                        tm.addData(S);
//                    }
//                }
//                Message("Found "+df.getSequenceIDinAlignment(ID).size()+" sequence(s)","",this.jStatusMessageSequence);
//            }
//            else {
//                //--Normal search
//                Vector<Integer> resultIndex=search(searchString, LoadSequenceFrame.MODE_ALL);
//                count=resultIndex.size();
//                tm.data.clear();
//                for (Integer index:resultIndex) {
//                    tm.addData(MultipleInfoSequence.get(index));
//                }
//                Message("Found "+resultIndex.size()+" sequence(s)","",this.jStatusMessageSequence);
//            }
//        }
//
//        tm.fireTableDataChanged();
//        this.jTable1.setModel(tm);
    }//GEN-LAST:event_Filter_ComboBoxActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void CopyAll2_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CopyAll2_jButtonActionPerformed
       StatisticsTable.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CopyAll"));
    }//GEN-LAST:event_CopyAll2_jButtonActionPerformed

    private void ImportState_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportState_jMenuItemActionPerformed
     JFileChooser chooser = new JFileChooser(config.getExplorerPath());
    
        chooser.setName("Select character state file");
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

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
      R2_TableModel r2=(R2_TableModel) R2_jTable.getModel();
      r2.ExportResult();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void CopyMainAll_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CopyMainAll_jButtonActionPerformed
       MainMatrixTable.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CopyAll"));
    }//GEN-LAST:event_CopyMainAll_jButtonActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
       CharEditorJDialog charj=new CharEditorJDialog(this, data);
       this.updateMatrixTableInfo();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void Filter_ComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter_ComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Filter_ComboBox1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       //--Do clean up here...
        
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void Polymorphic_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Polymorphic_jButtonActionPerformed
        PolymorphicChar_EditorJDialog poly=new PolymorphicChar_EditorJDialog(this,data);
    }//GEN-LAST:event_Polymorphic_jButtonActionPerformed

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
         summary=statistics.reference;
        //--PermutationStatistics
          PermutationStatistics_TableModel sm2=(PermutationStatistics_TableModel)this.Statistics_jTable.getModel();
          sm2.setData(statistics);
          sm2.fireTableDataChanged();
          sm2.fireTableStructureChanged();
          this.Statistics_jTable.setModel(sm2);
          
          //R1- Complexe_jTable
          
          R1_TableModel r1=(R1_TableModel)R1_jTable.getModel();
          r1.setData(summary);
          r1.setNetworkType(1);
          r1.fireTableDataChanged();
          r1.fireTableStructureChanged();
          this.R1_jTable.setModel(r1);
          
           //R1- Complexe_jTable
          
          R2_TableModel r2=(R2_TableModel)R2_jTable.getModel();
          r2.setData(statistics);          
          r2.fireTableDataChanged();
          r2.fireTableStructureChanged();
          this.R2_jTable.setModel(r2);
          
         //--Summary Table
        Summary_TableModel sm=(Summary_TableModel)this.Summary_jTable.getModel();
         sm.setData(summary);
        sm.fireTableDataChanged();
        sm.fireTableStructureChanged();
        this.Summary_jTable.setModel(sm);
         jTabbedPane.setEnabledAt(2, true);
         jTabbedPane.setEnabledAt(3, true);
         jTabbedPane.setEnabledAt(4, true);
         jTabbedPane.setEnabledAt(5, true);
         jTabbedPane.setEnabledAt(6, true);
         jTabbedPane.setEnabledAt(7, true);
         jTabbedPane.setEnabledAt(8, true);
         //jTabbedPane.setSelectedIndex(2);
         TableCellRenderer headerRenderer = new VerticalTableHeaderCellRendered(false);  
          Enumeration columns=this.Summary_jTable.getColumnModel().getColumns();
          while (columns.hasMoreElements()) {            
           //TableColumn tmp=((TableColumn)columns.nextElement());
          ((TableColumn)columns.nextElement()).setHeaderRenderer(headerRenderer);
           //tmp.setCellRenderer(cellRenderer);
           
        }          
          for (int i=0; i<=20; i++) {
              this.Summary_jTable.getColumnModel().getColumn(1).setMinWidth(50);
          }
          this.Summary_jTable.getColumnModel().getColumn(1).setPreferredWidth(200); //--Node name          
          
          this.Summary_jTable.getColumnModel().getColumn(28).setMinWidth(1000); //--Taxa         
          this.Statistics_jTable.getColumnModel().getColumn(0).setPreferredWidth(200); //--Node name          
         //this.Summary_jScrollPane.updateUI();
          this.VisualNetwork.loadNetwork(data);
    }
    
     private void Run() {
        
         ConfigureAnalysis_JDialog configure=new ConfigureAnalysis_JDialog(this,data);
        this.data=configure.getData(); //--Update data         
        if (configure.status_run) {
            Message("Analyzing "+data.filename+"","");
            Stop_jButton.setEnabled(true);
            ComponetGrapher_SwingWorker=getNewRunWorker(configure.no_log);
            ComponetGrapher_SwingWorker.execute(); 
        }
       
      }
    
     
             
             
         SwingWorker<Boolean, String>  getNewRunWorker(final boolean no_log) {
              progress.setValue(0);
             SwingWorker<Boolean, String> nt=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {
                 boolean status=false;
                 //--Keep traeck of time running
                 
                 
                //--TO DO... Divide the report generator in different part...
                 //UpdateMessage_SwingWorker.execute();
                  LongLog_jTextArea.setText("");
                 LongLog_jTextArea.append(data.get_info());
                 setProgress(1);                 
                 //jTabbedPane.setSelectedIndex(1);
                 
                 data.setCallback(new Callable() {
                                  
                     @Override
                     public Object call() {
                       if (!no_log) {
                         try {
                         String st=data.st_results.toString();
                         util.log("log.txt", st);
                         boolean update_caretpos=(LongLog_jTextArea.getCaretPosition()==LongLog_jTextArea.getText().length());                         
                         if (LongLog_jTextArea.getLineCount()>1000) {                             
                             int end=LongLog_jTextArea.getLineEndOffset(100); //--Remove the first 100 lines..
                             LongLog_jTextArea.setText(LongLog_jTextArea.getText().substring(end));
                         }
                         data.st_results=new StringBuffer();
                          LongLog_jTextArea.append(st);                          
                          if (update_caretpos) LongLog_jTextArea.setCaretPosition(LongLog_jTextArea.getText().length());                                                   
                        } catch(Exception e) {
                            e.printStackTrace();
                        }                        
                      }
                      return true;
                     }
                    });
                    //--Initialize
                    setProgress(5); 
                     data.compute();                    
                     data.display_result();                     
                     //--Computed the network
                    setProgress(10); 
                    
                    //--
                    long timerunnig=System.currentTimeMillis(); 
                    
                    
                    //--Permutation statistic here
                    statistics=new permutation_statistics(data);                    
//                     if (current_replicate==one_percent) {
//                        long timerunning_one=System.currentTimeMillis()-timerunning;
//                        timerunning_one*=100.0;                
//                        data.MessageResult("Estimate for 100%: "+util.msToString(timerunning_one));
//                    }
                 //--If bootstrap, do it here                    
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
                
                 //--push update to results stables
              
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
//                             String st=data.st_results.toString();
//                                data.st_results=new StringBuffer();
//                                 LongLog_jTextArea.setText(LongLog_jTextArea.getText()+st);
                            if (!o.isDone()) {
                                int prog=(Integer)evt.getNewValue();                                
                                progress.setValue(prog);
                                percent_jLabel.setText(prog+" %");
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                                progress.setValue(100);   
                                percent_jLabel.setText("100 %");
                                //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
              return nt;
       }
     
     ///////////////////////////////////////////////////////////////////////////
    /// MESSAGE FONCTION

    /**
     * Affiche un message dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void Message(String text, String tooltip) {
        this.Message_jLabel.setEnabled(true);
        this.Message_jLabel.setForeground(new java.awt.Color(0, 51, 153));
        this.Message_jLabel.setBackground(Color.WHITE);
        this.Message_jLabel.setToolTipText(tooltip);
        this.Message_jLabel.setText(text+"\n");
    }

      void MessageLog(String text) {        
        this.LongLog_jTextArea.append(text+"\n");
    }
    
    /**
     * Affiche un message d'erreur en rouge dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void MessageError(String text, String tooltip) {
        this.Message_jLabel.setEnabled(true);
        this.Message_jLabel.setForeground(Color.RED);
        this.Message_jLabel.setBackground(Color.WHITE);
        this.Message_jLabel.setToolTipText(tooltip);
        this.Message_jLabel.setText(text);
    }

   
   




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CopyAll2_jButton;
    private javax.swing.JButton CopyMainAll_jButton;
    private javax.swing.JMenuItem ExportMatrix_jMenuItem;
    private javax.swing.JTextField Filename_jTextField;
    private javax.swing.JComboBox Filter_ComboBox;
    private javax.swing.JComboBox Filter_ComboBox1;
    private javax.swing.JMenuItem ImportMatrix_jMenuItem;
    private javax.swing.JMenuItem ImportState_jMenuItem;
    private javax.swing.JMenuItem LoadAnalysis_jMenuItem;
    private javax.swing.JPanel Log_jPanel;
    private javax.swing.JTextArea LongLog_jTextArea;
    private javax.swing.JPanel Matrix_jPanel;
    private javax.swing.JTable Matrix_jTable;
    private javax.swing.JScrollPane MatrixjScrollPane;
    private javax.swing.JLabel Message_jLabel;
    private javax.swing.JPanel Network_Jpanel;
    private javax.swing.JPanel Network_jPanel;
    private javax.swing.JButton Polymorphic_jButton;
    private javax.swing.JPanel R1_jPanel;
    private javax.swing.JTable R1_jTable;
    private javax.swing.JPanel R2_jPanel;
    private javax.swing.JTable R2_jTable;
    private javax.swing.JPanel R3_jPanel;
    private javax.swing.JTable R3_jTable;
    private javax.swing.JPanel R4_jPanel;
    private javax.swing.JTable R4_jTable;
    private javax.swing.JPanel R5_jPanel;
    private javax.swing.JTable R5_jTable;
    private javax.swing.JPanel R6_jPanel;
    private javax.swing.JTable R6_jTable;
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
    private javax.swing.JPanel Summary_sum_jPanel1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jIcon;
    private javax.swing.JButton jInfo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel percent_jLabel;
    private javax.swing.JProgressBar progress;
    // End of variables declaration//GEN-END:variables

    @Override
    public void update(Observable o, Object o1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
