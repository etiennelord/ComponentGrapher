
package visual;

import COMPONENT_GRAPHER.datasets;
import COMPONENT_GRAPHER.graph;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import processing.core.PApplet;
import processing.core.PFont;

/**
 *
 * @author Etienne
 */
public class NetworkExplorer extends PApplet implements ChangeListener, ActionListener {

    ///testChechik - Program to Test the chechik database with Processing
///            - Use the chechik.db created with loadChechik
///              Note: set the correct path in <dbPath>
///              Note: Utilise le JDBC sqlitejdbc-v054.jar disponible sur http://www.zentus.com/sqlitejdbc/
///
/// Etienne Lord 2009

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Import




//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Variables

    private  String dbpath ="";          // Complete pathway to the chechik.db
    private String selectConditionID="1";              // La conditionID :: par default toutes les conditions!!!
    private String selectProteinID="1";                // La proteinID par default :: En fait un gène
    public String selectProteinName="NTH2";
    public String selectProteinState="UP";
    PFont font;                                        // Objet Font
    PFont bold;
    boolean download=false;                            // On est en train de downloader :: Si oui, on devrait afficher un message et ne rien faire avec la database
    boolean network=false;                             //Do we have a network
    boolean saving=false;                              // On est en train de sauver
    public static boolean displayed=true;
    public int[] geneID={226,569,611,728,729,1010,2432,2448,2709,2822,3041,3439,4160,4754,4979,5717};
    //String[] genenames={"NTH2","GLK1","PGK1","GPM2","GPD1","TPI1","PFK1","GND2","SOL3","RHR2","TDH1","FBA1","FBP1","PBI2","ZWF1","ERR1"};
    HashMap genename=new HashMap();
    int colorUP=color(242,17,17);
    int colorDOWN=color(25,183,15);
    int colorStable=color(10,24,255);
    int colorProduct=color(255,154,3);
    int imageNumber=0;              //ImageNumber for saving;
    Graph graph_gene=new Graph();
    long mouse_clicked_timer; // Timer for mouse doubleclick
     String query="";         // SQL query

public void setup () {

    size(1045,580);                                    // Set the screen size
    font=loadFont(dataPath("Arial-BoldMT-10.vlw"));
    bold=loadFont(dataPath("Arial-BoldMT-12.vlw"));
    textFont(bold);
    frameRate(10);
    //
    // Create network :: First get genename
    //
   
   try {
    
        graph_gene.loaded=false;   
         
        
        
//         graph_gene.addEdge(geneI, geneV);
       
      graph_gene.loaded=true;
      if (graph_gene.nodeCount>0) network=true;
    
    } catch(Exception e) {System.out.println("Error in loading networking of gene network");}

        //graph_gene.loadNodePosition(); //Load node position from disk
    
}

/////////////////////////////////////////////////////////////////////////////////////////////////////3
///
/// Calcul du modèle Impulse (voir
/// An Impulse model for temporal response profiles of gene expression


public void loadNetwork(datasets g) {
    //--Add each edge
    graph_gene=new Graph();
    for (int i=0; i<g.current_total_edge;i++) {        
        String source= g.inv_identification.get(g.src_edge[i]);
        String dest=g.inv_identification.get(g.dest_edge[i]);
        //System.out.println(source+" "+dest);
        if (source!=null) graph_gene.addEdge(source, dest);
    }
    this.network=true;    
}

    @Override
public void draw () {
  background(255);
  noStroke();
  if (network) {
  // SET CONDITION
  //graph_gene.resetState();
   graph_gene.draw();
  //graph_gene.setState(selectProteinName, selectProteinState);
//  try {
//    query=String.format("SELECT GenV, ProbUP, ProbDOWN FROM Activity WHERE GenI='%s' AND ActRepGenI='%s';",selectProteinName, selectProteinState);
//    ResultSet rs = db.executeQuery(query);
//     while (rs.next()) {
//       String geneV=rs.getString(1);
//       float ProbUP=rs.getFloat(2);
//       float ProbDOWN=rs.getFloat(3);
//       String state="UP";
//       float prob=ProbUP;
//       int c=color(map(ProbUP,0,10,100,255),0,0);
//       if (ProbUP<ProbDOWN) {
//             state="DOWN";
//             c=color(0,map(ProbDOWN,0,10,100,255),0);
//             prob=ProbDOWN;
//       }
//       if (ProbUP==ProbDOWN) {
//         state="STABLE";
//         c=graph_gene.colorSTABLE;
//       }
//       graph_gene.setState(geneV, prob,c);
//       }
//
//    } catch(Exception e) {}
 
 

  //
    // Draw heat map
    //
//    for (int i=-10;i<11; i++) {
//       if (i>0) {
//          fill(color(map(i,0,10,100,255),0,0));
//        } else {
//          fill(color(0,map(i,0,-10,100,255),0));
//        }
//        rect(207+i*10,290, 10,10);
//    }
//    fill(color(10,24,255));
//    rect(450,290, 20, 10);//Stable
//    //fill(#9CAAC6);
//    //rect(550,430, 20, 10);//Current
//    fill(0);
//   
//    text("Down regulation", 107, 305);
//    text("Up regulation", 307, 305);
//    text("Stable expression", 460, 305);
//    textAlign(LEFT);
//    text("Probability", 30, 298);
//    //text("Current selection", 560, 450);

  if (saving) {
      save(dataPath("graph"+imageNumber+".png"));
      imageNumber++;
      saving=false;
    }
  } else if (!network) {
      background(255);
      fill(0);
      textAlign(CENTER);
      textFont(bold);
      text("Warning. No network build! First use the \"Build Network \"button... Then restart this application.  ", width/2, height/2);
  }
}

/**
 * Save gene relative position in the view
 */
public void saveNovePosition() {
    graph_gene.saveNodePosition();
}
/**
 * Set the Node String
 * @param gene
 */
public void setGene(String gene) {
    String[] s=split(gene," ");
     selectProteinName=s[0];
     selectProteinState=s[1];
}

/**
 * call when you need to reload the graph 
 */
public void rebuildNetwork() {
//    displayed=false;
//    genename.clear();
//    graph_gene=new Graph();
//    //
//    // Create network :: First get genename
//    //
//    try {
//    for (int g:geneID) {
//        query=String.format("SELECT ID, Name FROM Prot_Names WHERE ID='%s';",g);
//        ResultSet rs = db.executeQuery(query);
//         while (rs.next()) {
//              genename.put(rs.getInt(1), rs.getString(2));
//           }
//    }
//    } catch(Exception e) {System.out.println("Error in creating gene network");}
//   try {
//    query=String.format("SELECT NoExp, NoGenI, NoGenV, ActRepGenI, InflGenV FROM ActRep;");
//    ResultSet rs = db.executeQuery(query);
//     while (rs.next()) {
//         String geneI=(String)genename.get(rs.getInt(2));
//         String geneV=(String)genename.get(rs.getInt(3));
//         graph_gene.addEdge(geneI, geneV);
//       }
//      graph_gene.loaded=true;
//    } catch(Exception e) {System.out.println("Error in loading networking of gene network");}
//    displayed=true;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Event
void handleEvent(String[] parameters) {
  int result=0; //number of results
  int len_search=0; // To limit unnecessary search if not enough character entered
  String eventDesc = "type of object: "+parameters[0]+"; object name: "+parameters[1]+"; event: "+parameters[2]; // create a description of the event
  for (int i=3; i<parameters.length; i++) {
    eventDesc = eventDesc + "; additional info: " + parameters[i];
  }
  //println(eventDesc);   //For debug

   if (parameters[1].equals("myCondition") &&parameters[2].equals("selected")) {
     String[] s=split(parameters[3]," ");
     selectProteinName=s[0];
     selectProteinState=s[1];
   }

     if (parameters[1].equals("mySave") &&parameters[2].equals("mouseClicked")) {
       graph_gene.saveNodePosition();
   }

    if (parameters[1].equals("mySaving") &&parameters[2].equals("mouseClicked")) {
       saving=true;
   }


}

public void mousePressed() {
  // Simpleclick
    graph_gene.mouseSelect();


  // Doubleclick
  if ((System.currentTimeMillis()-mouse_clicked_timer)<500L) {
      // Display info of selected node
  }

  //Set new doubleclick delay
  mouse_clicked_timer=System.currentTimeMillis();
  }

  public void mouseDragged() {
    graph_gene.moveSelection();
  }

  public void mouseReleased() {
    graph_gene.mouseSelect();
  }

    public void stateChanged(ChangeEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



  class Graph {

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Variables
  boolean loaded=false;    //finished loading and ready to draw?
  int nodeCount=0;
  Node[] nodes = new Node[10];
  Node infoselection;
  HashMap nodeTable = new HashMap();
  int edgeCount=0;
  Edge[] edges = new Edge[10];
  final static int MAX_nbiteration=25;
  int nbiteration = MAX_nbiteration;               //Nombre d'iteration a faire dans la position
  String pathwayName="chechik";                    //Current pathwayName
  ArrayList pathway_displayed = new ArrayList();   //Pour mettre les pathways à l'écran
  int colorUP=color(242,17,17);
  int colorDOWN=color(25,183,15);
  int colorSTABLE=color(10,24,255);
  int colorProduct=color(255,154,3);
  public int colorNode=color(156,170,198);


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Constructeur

//  public Graph() {
//
//  }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Fonctions mouse
///
  void mouseSelect() {
    float closest=50;
    for (int i = 0; i < nodeCount; i++) {
        Node n = nodes[i];
        float d = dist(mouseX, mouseY, n.x, n.y);
        if (d < closest) {
        infoselection = n;
        closest = d;
    }
  }
  if (infoselection != null) {

    if (mouseButton == LEFT) {
      infoselection.fixed = true;
      } else if (mouseButton == RIGHT) {
      infoselection.fixed = false;
    }
  }
 }
 void moveSelection() {
 if (infoselection != null) {
    nbiteration=0;
    infoselection.x = mouseX;
    infoselection.y = mouseY;
    }
 }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Fonctions draw

  public void draw() {

   
     //Clear screen?
      fill(0);
     //textFont(titlefont);
     textAlign(RIGHT);
     //text(pathwayName,width-10, height-10);
     //textFont(calibrifont);
     fill(0);
     //if (nbiteration>0) {
     //relax node and edge and draw them
     for (int i =0; i < edgeCount; i++) {
       edges[i].relax();
      }
    for (int i =0; i < nodeCount; i++) {
      nodes[i].relax();
    }
    for (int i =0; i < nodeCount; i++) {
      nodes[i].update();
    }    
    for (int i =0; i < edgeCount; i++) {
      edges[i].draw();
    }
    for (int i =0; i < nodeCount; i++) {
      nodes[i].draw();
     }
     //nbiteration--;
      //end loaded?
 }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Fonctions loader and save/load node position


  private boolean displayed(String pathway) {
   if (pathway_displayed.size()==0) return false;
   for (int i=0; i<pathway_displayed.size(); i++) {
      if (pathway.equals((String)pathway_displayed.get(i))) return true;
   }
   return false;
  }

  //
  // Really, really simple parser for Node position
  // Warning, no error correction if data set change!
  //
  void loadNodePosition() {
      String filename=dataPath(pathwayName+".pos");
      File f = new File(filename);
      if (f.exists()) {
      String[] pos=loadStrings(filename);
      nbiteration=0;
      if (pos.length>2) {
       //Skip description
         for (int i=1; i<pos.length;i++) {
              String[] data=split(pos[i], TAB);
              nodes[i-1].x=Float.valueOf(data[1]);
              nodes[i-1].y=Float.valueOf(data[2]);
         } //for
       } //if pos
       loaded=true;
       } //End fileexists
  }

 void saveNodePosition() {
   if (!pathwayName.equals("")&&nodeCount>0) {
      String filename=dataPath(pathwayName+".pos");
      try {
        PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
        pw.println("# Node position for Saccharomyces cerevisiae - "+pathwayName);
      for (int i=0; i<nodeCount;i++) pw.println(nodes[i].label+TAB+nodes[i].x+TAB+nodes[i].y);
        pw.flush();
        pw.close();
      } catch(IOException e) {System.out.println("Unable to save node position for "+pathwayName+" in "+filename);}
    }
  }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Fonctions graphiques


  void addEdge(String fromLabel, String toLabel) {
    Node from = findNode(fromLabel);
    Node to = findNode(toLabel);
    Edge e = new Edge(from, to);
    if (edgeCount == edges.length) {
      edges = (Edge[]) expand(edges);
      }
      edges[edgeCount++] = e;
      }

  void addEdgeColor(String fromLabel, String toLabel, int c) {
    Node from = findNode(fromLabel);
    Node to = findNode(toLabel);
    to.nodeColor=c;
    Edge e = new Edge(from, to);
    if (edgeCount == edges.length) {
      edges = (Edge[]) expand(edges);
      }
      edges[edgeCount++] = e;
   }

    Node findNode(String s) {
    Node n = (Node) nodeTable.get(s);
    if (n==null) {
      return addNode(s);
      }
      return n;
    }

    public void resetState() {
       for (int i=0; i<nodeCount;i++) {
           nodes[i].prob=0;
           if (nodes[i].nodeColor!=colorProduct) nodes[i].nodeColor=colorNode;
       }
    }


    public void setState(String node, int state_color) {
      Node n=findNode(node);
      if (n!=null) {
           n.nodeColor=state_color;
      }
    }

    public void setState(String node, float prob, int state_color) {
      Node n=findNode(node);
      if (n!=null) {
           n.nodeColor=state_color;
           n.prob=prob;
      }
    }

    public void setState(String node, String state) {
      Node n=findNode(node);
      if (n!=null) {
          n.prob=100;
          if (state.startsWith("U")) n.nodeColor=colorUP;
          if (state.startsWith("D")) n.nodeColor=colorDOWN;
          if (state.startsWith("S")) n.nodeColor=colorSTABLE;
      }
    }


    Node addNode(String s) {
      Node n = new Node(s);
      n.x=width/2+random(200);
      n.y=height/2+random(200);
      if (s.startsWith("root")) {
        n.x = width/2;
        n.y= height/2;
        n.fixed=true;
      }
      if (nodeCount == nodes.length) {
      nodes = (Node[]) expand(nodes);
      }
      nodes[nodeCount++] = n;
      nodeTable.put(s, n);
      return n;
    }

class Node {
  float x, y;
  float dx, dy;
  float w, h;
  boolean fixed=false;
  String label;
  float prob=(float) 0.0; //probability
  public int nodeColor = colorNode;
  public int selectColor = 0xFF3030;
  public int fixedColor = 0xFF8080;


Node (String label) {
  this.label=label;
  x = random(width-10);
  y = random (height-10);
  //textFont(calibrifont);
  w = textWidth(label) + 10;
  h = textAscent() + textDescent() + 4;
  }

  void relax() {
    float ddx =0;
    float ddy =0;

    for (int j = 0; j < nodeCount; j++) {
     Node n = nodes[j];
     if (n!= this) {
      float vx = x - n.x;
      float vy = y - n.y;
      float lensq = sqrt(vx * vx + vy * vy);

      if (lensq == 0) {
        ddx += random(100);
        ddy += random(100);
        } else if (lensq < 100 * 100) {
          ddx += vx / lensq;
          ddy += vy / lensq;
        }
        }
      float dlen = mag(ddx, ddy) / 2;
      if (dlen>0) {
        dx += ddx / dlen;
        dy += ddy / dlen;

        }
      }
      }

    void update() {
      if (!fixed) {
        x += constrain(dx, -5, 5);
        y += constrain(dy, -5, 5);
        x = constrain(x, 0, width);
         y = constrain(y, 0, height);
//        if (x<0) x=0;
//        if (x>width) x=width;
//        if (y<this.y+50) y=this.y+50;
//        if (y>height) y=height-50;
      }
      dx /= 2;
      dy /= 2;
    }

  void draw() {
    if (!label.startsWith("root")) {fill (nodeColor);
      noStroke();
      rectMode(CORNER);
      if (nodeColor==colorNode||prob==100.0) {
        textFont(bold);
        rect (x-(w+10)/2, y - (h+6)/2, w+10, h+6);
      } else rect (x-w/2, y - h/2, w, h);
      fill(255);
      textAlign(CENTER, CENTER);

      text(label, x, y);
      textFont(font);
      fill(0);
      if (prob!=0.0) text(str(prob)+"%",x,y+15);
    }
  }

  }



  class Edge{
  Node from;
  Node to;
  float len;
  final int edgeColor = color(121, 125, 126);



  Edge(Node from, Node to) {
  this.from = from;
  this.to = to;
  this.len = 50;
  }

  Edge() {}

  void relax() {
    float vx = to.x - from.x;
    float vy = to.y - from.y;
    float d = mag(vx, vy);
    if (d > 0 ) {
      float f = (len - d) / (d * 3);
      float dx = f * vx;
      float dy = f * vy;
      to.dx += dx;
      to.dy += dy;
      from.dx -= dx;
      from.dy -= dy;
      }
    }

  void draw() {
    stroke(edgeColor);
    strokeWeight(0.35f);
    line(from.x, from.y, to.x, to.y);
    }

  }



}
}
