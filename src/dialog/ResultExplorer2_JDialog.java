
package dialog;

import COMPONENT_GRAPHER.graph;
import COMPONENT_GRAPHER.summary_statistics;
import COMPONENT_GRAPHER.triplets;
import config.Config;
import config.util;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;


public class ResultExplorer2_JDialog extends javax.swing.JDialog {
     
     static Config config=new Config();
     ArrayList<graph> networks=new ArrayList<graph>();
     summary_statistics data=null;
     

    /** Creates new form AboutJDialog */
    public ResultExplorer2_JDialog(java.awt.Frame parent, summary_statistics data, int mode, int[] nodeids) {
        super(parent, true);
        initComponents();
        this.data=data;
        
        this.setTitle("ComponentGrapher version "+config.get("version"));
        //this.jTextArea1.setText(PrintMemory());
         //ArrayList<String> stri=util.loadString("data"+File.separator+"about.html");
        this.setIconImage(Config.getImage());
      
        networks=data.getGraphs();
        
        //--Logic her
        switch (mode) {
            case 0: findEdges(nodeids); 
                    this.Search_jLabel.setText(" Search for edges");
            break;
            case 1: findTriplets(nodeids); 
            this.Search_jLabel.setText(" Search for triplets");
            break;
            case 2:  findTriangle(nodeids); 
            this.Search_jLabel.setText(" Search for triangles");
            break;  
        }
        
        
         //this.jEditorPane1.setText();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
        setVisible(true);
    }

    void findEdges(int[] nodeids) {
        int size=nodeids.length;
        String st="";
        switch(size) {
            case 0: 
                for (int gid=0; gid<=4; gid++) {           
                     graph g=networks.get(gid);
                    if (gid==0) {
                         st+="Network complete:\n";
                     } else {
                         st+="Network type "+gid+":\n";
                     }
                     for (int i=0;i<data.data.nodes.size();i++) {
                        for (int nid:g.get_neighbors(i)) {
                            st+="Edge "+nodeids[0]+" -- "+nid+" (exists)\n"; 
                        }
                    }
                }
                 this.Result_jTextPane.setText(st);   
                 break;
            case 1: for (int gid=0; gid<=4; gid++) {           
                     graph g=networks.get(gid);
                     if (gid==0) {
                         st+="Network complete:\n";
                     } else {
                         st+="Network type "+gid+":\n";
                     }
                     for (int nid:g.get_neighbors(nodeids[0])) {
                            st+="Edge "+nodeids[0]+" -- "+nid+" (exists)\n"; 
                        }
                       }
                      this.Result_jTextPane.setText(st);   
                      break;             
              case 2:   
                  System.out.println(nodeids[0]+" "+nodeids[1]);
                  for (int gid=0; gid<=4; gid++) {           
                     
                     graph g=networks.get(gid);
                     if (gid==0) {
                         st+="Network complete:\n";
                     } else {
                         st+="Network type "+gid+":\n";
                     }
                     if (gid!=2&&g.edge_exists(nodeids[0], nodeids[1])||g.edge_exists(nodeids[1], nodeids[0])) {
                        st+="Edge "+nodeids[0]+" -- "+nodeids[1]+"\t(exists)\n"; 
                    } else if (gid==2) {
                        if (g.edge_exists(nodeids[0], nodeids[1])) {
                            st+="Edge "+nodeids[0]+" -- "+nodeids[1]+"\t(exists)\n"; 
                        } else {
                            st+="Edge "+nodeids[0]+" -- "+nodeids[1]+"\t(not found)\n"; 
                        } 
                        if (g.edge_exists(nodeids[1], nodeids[0])) {
                             st+="Edge "+nodeids[1]+" -- "+nodeids[0]+"\t(exists)\n"; 
                        } else {
                            st+="Edge "+nodeids[1]+" -- "+nodeids[0]+"\t(not found)\n"; 
                        }
                    } else  {
                        st+="Edge "+nodeids[0]+" -- "+nodeids[1]+"\t(not found)\n"; 
                    }
                  }
                      this.Result_jTextPane.setText(st);   
                      break; 
                 default:
                  //--More than 2 nodes                    
                  ArrayList<Integer> kcomb=COMPONENT_GRAPHER.util.k2combination(nodeids);
                     for (int gid=0; gid<=4; gid++) {           
                         graph g=networks.get(gid);
                         if (gid==0) {
                             st+="Network complete:\n";
                         } else {
                             st+="Network type "+gid+":\n";
                         }
                         for (int l=0; l<kcomb.size();l+=2) {
                             int nodeids1=kcomb.get(l);
                             int nodeids2=kcomb.get(l+1);
                            // System.out.println(nodeids1+" "+nodeids2);
                           if (gid!=2&&g.edge_exists(nodeids1, nodeids2)||g.edge_exists(nodeids2, nodeids1)) {
                                    st+="Edge "+nodeids1+" -- "+nodeids2+"\t(exists)\n"; 
                            } else if (gid==2) {
                              if (g.edge_exists(nodeids1, nodeids2)) {
                                    st+="Edge "+nodeids1+" -- "+nodeids2+"\t(exists)\n"; 
                                } 
                                if (g.edge_exists(nodeids2, nodeids1)) {
                                    st+="Edge "+nodeids2+" -- "+nodeids1+"\t(exists)\n"; 
                                } 
                            } else  {
                                    st+="Edge "+nodeids1+" -- "+nodeids2+"\t(not found)\n"; 
                            }      
                    }
                  }  
                     this.Result_jTextPane.setText(st);   
                  break;
        }     
        
        
    }
    
     void findTriangle(int[] nodeids) {
         graph g3=networks.get(3);
         int size=nodeids.length;
        String st="";   
          st+="#node1\tcentral_node\tnode3\ttype\n"; 
        if (nodeids.length==3) {
            if (g3.triangle_exists(nodeids[0],nodeids[1],nodeids[2])) {
                st+=nodeids[0]+"\t"+nodeids[1]+"\t"+nodeids[2]+"\t(exists)"+"\n";
            } else {
                st+=nodeids[0]+"\t"+nodeids[1]+"\t"+nodeids[2]+"\t(not found)"+"\n";
            }
        } 
      
         
         for (int n:nodeids) {             
             st+="Triangles for "+n+"\n";
             g3.export_triangle(n,"tmp.txt", networks, "\t");
             st+=util.loadString("tmp.txt");
             
         }
        this.Result_jTextPane.setText(st);  
     }
    
    void findTriplets(int[] nodeids) {
        int size=nodeids.length;
        String st="";
        //--No node, all triplets
        if (size==0) {
          graph g3=networks.get(3);
          g3.export_triplet("tmp.txt", networks, "\t");
          st=util.loadString("tmp.txt");
        }
         //--No node, all triplets
        if (size==1) {
             graph g3=networks.get(3);
             g3.export_triplet(nodeids[0], "tmp.txt", networks, "\t");             
             st=util.loadString("tmp.txt");
        }
        if (size==2) {
            graph g3=networks.get(3);
            g3.export_triplet(nodeids[0],nodeids[1], "tmp.txt", networks, "\t");             
            st+=util.loadString("tmp.txt");
            
        }
        if (size==3) {
            graph g3=networks.get(3);           
            g3.export_triplet(nodeids[0],nodeids[1],nodeids[2], "tmp.txt", networks, "\t");             
            st+=util.loadString("tmp.txt");
        }
        if (size>3) {
              ArrayList<Integer> kcomb=COMPONENT_GRAPHER.util.k3combination(nodeids);
               st+="#node1\tcentral_node\tnode3\ttype\n"; 
              for (int l=0; l<kcomb.size();l+=3) {
                    int nodeids1=kcomb.get(l);
                    int nodeids2=kcomb.get(l+1);
                    int nodeids3=kcomb.get(l+2);
                    graph g3=networks.get(3);
                     g3.export_triplet(nodeids1,nodeids2,nodeids3,"tmp.txt", networks, "\t");
                    
                     st+=util.loadString("tmp.txt");
                             
              }
        }
         this.Result_jTextPane.setText(st);  
    }
    
    
//    public static String PrintMemory() {
//        String stri="Armadillo version "+config.get("version")+"\n "+"\n"+
//                    "System allocated memory: "+Runtime.getRuntime().totalMemory()/Mb+" MB\nSystem free memory: "+Runtime.getRuntime().freeMemory()/Mb+" MB\n"+
//                    "System total core: "+Runtime.getRuntime().availableProcessors()+"\n";
//         //Config.log(stri);
//         return stri;
//    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        Result_jTextPane = new javax.swing.JTextPane();
        Search_jLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(Result_jTextPane);

        Search_jLabel.setBackground(new java.awt.Color(51, 102, 255));
        Search_jLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Search_jLabel.setForeground(new java.awt.Color(255, 255, 255));
        Search_jLabel.setText(" Search for ");
        Search_jLabel.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 764, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane2)
                    .addComponent(Search_jLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Search_jLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
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

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_jButton1ActionPerformed

    
//    void getType1() {
//        String st="";
//        try {
//            Integer nodeid1=-1;
//            Integer nodeid2=-1;
//            Integer nodeid3=-1;
//            if (!node1.getText().trim().isEmpty()) nodeid1=Integer.valueOf(node1.getText().trim());
//            if (!node2.getText().trim().isEmpty()) nodeid2=Integer.valueOf(node1.getText().trim());
//            //--First edge with both node
//            graph g1=networks.get(1);
//            if (nodeid1!=-1&&nodeid2!=-1) {
//                if (g1.edge_exists(nodeid1, nodeid2)||g1.edge_exists(nodeid2, nodeid1)) {
//                    st+="Edge "+nodeid1+" -- "+nodeid2+" (exists)\n"; 
//                } else {
//                    st+="Edge "+nodeid1+" -- "+nodeid2+" (not found)\n"; 
//                }
//            } else if (nodeid1!=-1) {
//                //--Find other edges
//                for (int nid:g1.get_neighbors(nodeid1)) {
//                    st+="Edge "+nodeid1+" -- "+nid+" (exists)\n"; 
//                }
//                
//            }
//            
//            
//        } catch(Exception e) {e.printStackTrace();}
//        this.Result_jTextPane.setText(st);
//    }
// 
//     void getType2() {
//        String st="";
//        try {
//            Integer nodeid1=-1;
//            Integer nodeid2=-1;
//            Integer nodeid3=-1;
//            if (!node1.getText().trim().isEmpty()) nodeid1=Integer.valueOf(node1.getText().trim());
//            if (!node2.getText().trim().isEmpty()) nodeid2=Integer.valueOf(node1.getText().trim());
//            //--First edge with both node
//            graph g2=networks.get(1);
//            if (nodeid1!=-1&&nodeid2!=-1) {
//                if (g2.edge_exists(nodeid1, nodeid2)) {
//                    st+="Edge "+nodeid1+" -- "+nodeid2+" (exists)\n"; 
//                } 
//                if (g2.edge_exists(nodeid2, nodeid1)) {
//                     st+="Edge "+nodeid2+" -- "+nodeid1+" (exists)\n"; 
//                } 
////                else {
////                    st+="Edge "+nodeid1+" -- "+nodeid2+" (not found)\n"; 
////                }
//            } else if (nodeid1!=-1) {
//                //--Find other edges
//                for (int nid:g2.get_neighbors(nodeid1)) {
//                    st+="Edge "+nodeid1+" -- "+nid+" (exists)\n"; 
//                }
//                
//            }
//            
//            
//        } catch(Exception e) {e.printStackTrace();}
//        this.Result_jTextPane.setText(st);
//    }
//    
//     void getType3() {
//        String st="";
//        try {
//            Integer nodeid1=-1;
//            Integer nodeid2=-1;
//            Integer nodeid3=-1;
//            if (!node1.getText().trim().isEmpty()) nodeid1=Integer.valueOf(node1.getText().trim());
//            if (!node2.getText().trim().isEmpty()) nodeid2=Integer.valueOf(node1.getText().trim());
//            //--First edge with both node
//            graph g1=networks.get(1);
//            if (nodeid1!=-1&&nodeid2!=-1) {
//                if (g1.edge_exists(nodeid1, nodeid2)||g1.edge_exists(nodeid2, nodeid1)) {
//                    st+="Edge "+nodeid1+" -- "+nodeid2+" (exists)\n"; 
//                } else {
//                    st+="Edge "+nodeid1+" -- "+nodeid2+" (not found)\n"; 
//                }
//            } else if (nodeid1!=-1) {
//                //--Find other edges
//                for (int nid:g1.get_neighbors(nodeid1)) {
//                    st+="Edge "+nodeid1+" -- "+nid+" (exists)\n"; 
//                }
//                
//            }
//            
//            
//        } catch(Exception e) {e.printStackTrace();}
//        this.Result_jTextPane.setText(st);
//    }
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane Result_jTextPane;
    private javax.swing.JLabel Search_jLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
