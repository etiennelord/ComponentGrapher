package COMPONENT_GRAPHER;

import java.util.ArrayList;
import java.util.HashMap;
import org.forester.evoinference.matrix.distance.BasicSymmetricalDistanceMatrix;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import umontreal.iro.lecuyer.rng.LFSR258;

/**
 * This class handle the permutation of the dataset
 * @author Etienne Lord
 * @since July 2017
 */
public class permute {
   
       static int I1=1234, I2=5678; //For unif_rand 
       transient static LFSR258  rand=new LFSR258();  
       datasets dat;
       
////////////////////////////////////////////////////////////////////////////////
///

public permute(datasets d) {
    this.dat=d;
}       

public permute() {
    
}       

       
/** 
 * From R base sunif.c
 * A version of Marsaglia-MultiCarry
 * @return 
 */
static double unif_rand()
{
    I1= 36969*(I1 & 0177777) + (I1>>16);
    I2= 18000*(I2 & 0177777) + (I2>>16);
    return ((I1 << 16)^(I2 & 0177777)) * 2.328306437080797e-10; /* in [0,1) */
}
    /***
     * Randomly permute the value in i and j
     * @param col i.e. character p.e. pied 
     */
    private void permute(int col) {
        int index_i=rand.nextInt(0,dat.ntax-1);        
        int index_j=rand.nextInt(0,dat.ntax-1);
        while (index_j==index_i) {
            index_j=rand.nextInt(0,dat.ntax-1);
        }
       
        String o=dat.current_state_matrix[index_i][col];
        dat.current_state_matrix[index_i][col]=dat.current_state_matrix[index_j][col];
        dat.current_state_matrix[index_j][col]=o;        
    } 
    
    /**
     * We copy a random state from this column to a new position
     * @param col
     * @param index_i 
     */
    private void bootstrap(int col, int index_i) {        
        int index_j=rand.nextInt(0,dat.ntax-1);
        dat.current_state_matrix[index_i][col]=dat.current_state_matrix[index_j][col];        
    } 
    
    /**
     * From the original data, do one matrix permutation
     */
    public void generate_permutation() {
    
       //Do the random permuation
        for (int j=0; j<dat.nchar;j++) {
            for (int i=0; i<dat.ntax-1;i++) permute(j);
         }        
    }
    
     /**
     * From the original data, do one matrix permutation
     */
    public void generate_probpermutation() {
    
       //Do the random permuation
        for (int j=0; j<dat.nchar;j++) {
            //--Compute for this column the % of each character
            int total=0;
            HashMap<String,Float> hc=new HashMap<String,Float>();
            for (int i=0; i<dat.ntax;i++) {
                String key=dat.current_state_matrix[i][j];
                Float c=hc.get(dat.current_state_matrix[i][j]);
                if (c==null) c=0.0f;
                c+=1;
                if (key!="?"&&key!="*"&&key!="-") {
                    hc.put(key, c);
                    total++;
                }
            }
            //--Create the prob. vector
            ArrayList<String> possible_char=new ArrayList<String>();
            for (String k:hc.keySet()) possible_char.add(k);
            Double[] prob=new Double[possible_char.size()];
            Double last=0.0;
            for (int i=0;i<possible_char.size();i++) {
                prob[i]=(hc.get(possible_char.get(i))/(1.0*total))+last;
                last=prob[i];
            }
            //-- Iterate through each tax
            for (int i=0; i<dat.ntax;i++) {
                double p=rand.nextDouble();
                for (int k=0; k<prob.length;k++) {
                    if (p<=prob[k]) {
                        dat.current_state_matrix[i][j]=possible_char.get(k);
                        break;
                    }
                }
            }
            //---End tax
         } //--End char       
    }
    
     /**
     * From the original data, do one matrix permutation
     */
    public void generate_phylopermutation() {
       if (dat.tree==null) {
           System.out.println("Error. No phylogeny found! Performint normal permutation.");
           generate_permutation();
           return;
       }
       //Do the random permuation
        for (int j=0; j<dat.nchar;j++) {
            //--1. Create the HashMap for this coloumn nata iwht the key (taxa) and value (char)
            HashMap<String,String> tax_char=new HashMap<String,String>();
            for (int i=0; i<dat.ntax;i++) {
                tax_char.put(dat.label.get(i), dat.current_state_matrix[i][j]);
            }
            //--2. Generate a permutation for this column 
            ArrayList<String> tax_place=phyloPermute(dat.tree,dat.tree_k);
            for (int i=0; i<dat.ntax;i++) {
                dat.current_state_matrix[i][j]=tax_char.get(tax_place.get(i)); //--GEt the rigth char
            }
         }        
    }
    
    /**
     * From the original data, do one matrix permutation
     */
    public void generate_bootstrap() {
    
       //Do the random permuation
        for (int j=0; j<dat.nchar;j++) {
            for (int i=0; i<dat.ntax;i++) bootstrap(j, i);
         }        
    }
    
    double getMax(BasicSymmetricalDistanceMatrix matrix) {
        double max=-1;
        for (int i=0;i<matrix.getSize();i++) {
            for (int j=0;j<matrix.getSize();j++) {
                if (matrix.getValue(i, j)>max) max=matrix.getValue(i, j);
            }
        }        
        return max;
    }
   
   static double getMax(double[][] matrix) {
        double max=-1;
        for (int i=0;i<matrix.length;i++) {
            for (int j=0;j<matrix.length;j++) {
                if (matrix[i][j]>max) max=matrix[i][j];
            }
        }        
        return max;
    }
   
   double[] getRowsums(BasicSymmetricalDistanceMatrix matrix) {
        double[] sum=new double[matrix.getSize()];
        
        for (int row=0;row<matrix.getSize();row++) {
            sum[row]=0;
            for (int col=0;col<matrix.getSize();col++) {
                sum[row]+=matrix.getValue(col, row);
            }
        }        
        return sum;
    }
   
   static double[] getRowsums(double[][] matrix) {
        double[] sum=new double[matrix.length];
        
        for (int row=0;row<matrix.length;row++) {
            sum[row]=0;
            for (int col=0;col<matrix.length;col++) {
                sum[row]+=matrix[col][row];
            }
        }        
        return sum;
    }
   
   double[] getCummulsums(BasicSymmetricalDistanceMatrix matrix, int row) {
        double[] sum=new double[matrix.getSize()];
        double prev=0;
        for (int col=0;col<matrix.getSize();col++) {
                sum[col]=prev+matrix.getValue(col, row);
                prev=sum[col];
        }        
        return sum;
    }
   
   static double[] getCummulsums(double[][] matrix, int row) {
        double[] sum=new double[matrix.length];
        double prev=0;
        for (int col=0;col<matrix.length;col++) {
                sum[col]=prev+matrix[col][row];
                prev=sum[col];
        }        
        return sum;
    }
  
  
      double[][] phyloProb(Phylogeny p, double k) {
            int nl=p.getNumberOfExternalNodes();
            
            //if distance to parent is not set --Note: possible race condition
            try {
            for (int i=0; i<nl;i++) {                
                if (p.getNode(i).getDistanceToParent()<0) {
                    p.getNode(i).setDistanceToParent(1.0);
                }                 
             }   
            } catch(Exception e){}
            
             ArrayList<PhylogenyNode> nodes=(ArrayList<PhylogenyNode>) p.getExternalNodes();
             double d[][]=new double[nl][nl];
            
             for (int i=0; i<nodes.size();i++) {                 
                 //d.setIdentifier(i, nodes.get(i).getName());
                 for (int j=0; j<nodes.size();j++) {
                        d[i][j]=PhylogenyMethods.calculateDistance(nodes.get(i),nodes.get(j));
                 }
             }             
             return phyloProb(d, k);
        }
      
      static double[][] phyloProb(double[][] d, double k) {
            int nl=d.length;
             double scaled_d[][]=new double[nl][nl];
             
             double maxd=getMax(d);
             //Scale the matrix
             
             for (int i=0; i<nl;i++) {                 
                 //d.setIdentifier(i, nodes.get(i).getName());
                 for (int j=0; j<nl;j++) {                     
                         scaled_d[i][j]=k-(d[i][j]/maxd);                     
                 }
             }
             //Normalize d_scaled by rowSums
             double[] rowsum=getRowsums(scaled_d);
             for (int row=0; row<rowsum.length;row++) {
                 for (int col=0; col<rowsum.length;col++) {
                     scaled_d[col][row]=scaled_d[col][row]/rowsum[row];                     
                 }
             }           
             return scaled_d;
        }
      

            /* generate sample */
      
      /* A version of Marsaglia-MultiCarry */


//void set_seed(unsigned int i1, unsigned int i2)
//{
//    I1 = i1; I2 = i2;
//}
//
//void get_seed(unsigned int *i1, unsigned int *i2)
//{
//    *i1 = I1; *i2 = I2;
//}

      static BasicSymmetricalDistanceMatrix removeRowCol(BasicSymmetricalDistanceMatrix p,int x) {
          if (p.getSize()==1) return p;
          if (x>=p.getSize()) return p;
          BasicSymmetricalDistanceMatrix pp=new BasicSymmetricalDistanceMatrix(p.getSize()-1);
          
          int pp_i=0;
          int pp_j=0;
          for (int i=0; i<p.getSize();i++) {
            if (i!=x) {  
            for (int j=0; j<p.getSize();j++) {
                  if (j!=x) {
                      pp.setValue(pp_i, pp_j, p.getValue(i,j));
                      pp_j++;
                  }
               }     
               pp_j=0;
               pp.setIdentifier(pp_i, p.getIdentifier(i));
               pp_i++;
            }
             
          }        
                  
          return pp;
      }

      static BasicSymmetricalDistanceMatrix removeRowCol(BasicSymmetricalDistanceMatrix p,int row, int col) {
          if (p.getSize()==1) return p;
          if (row>=p.getSize()) return p;
          if (col>=p.getSize()) return p;
          BasicSymmetricalDistanceMatrix pp=new BasicSymmetricalDistanceMatrix(p.getSize()-1);
          
          int pp_i=0;
          int pp_j=0;
          for (int i=0; i<p.getSize();i++) {
            if (i!=row) {  
            for (int j=0; j<p.getSize();j++) {
                  if (j!=col) {
                      pp.setValue(pp_i, pp_j, p.getValue(i,j));
                      pp_j++;
                  }
               }     
               pp_j=0;
               pp.setIdentifier(pp_i, p.getIdentifier(i));
               pp_i++;
            }             
          }        
                  
          return pp;
      }
      
      static double[][] removeRowCol(double[][] p,int row, int col) {
          int n=p.length;
          if (n==1) return p;
          if (row>=n) return p;
          if (col>=n) return p;
          double[][] pp=new double[n-1][n-1];
          //BasicSymmetricalDistanceMatrix pp=new BasicSymmetricalDistanceMatrix(p.getSize()-1);
          
          int pp_i=0;
          int pp_j=0;
          for (int i=0; i<n;i++) {
            if (i!=row) {  
            for (int j=0; j<n;j++) {
                  if (j!=col) {
                      pp[pp_j][pp_i]=p[j][i];
                      pp_j++;
                  }
               }     
               pp_j=0;              
               pp_i++;
            }             
          }                          
          return pp;
      }
      
  
       //TO DO
      static int getRowPos(double[][] p, int row) {
         double[] prob=getCummulsums(p,row);
         double rand=unif_rand();
         for (int i=0; i<prob.length;i++) {
             if (rand<=prob[i]) return i;
         }
          return prob.length-1; //last index
      }
      
      static ArrayList<String> permute(ArrayList<String> data,String name1, String name2) {          
          int pos1=data.indexOf(name1);
          int pos2=data.indexOf(name2);
          ArrayList<String> tmp=new ArrayList<String>();
          tmp.addAll(data);
          tmp.set(pos1, name2);
          tmp.set(pos2, name1);          
          return tmp;
      }
      //
      /**
       * This return a permutation order of the label based on the phylogeny 
       * @param pp phyloTree
       * @param k 
       */
       ArrayList<String> phyloPermute(Phylogeny pp,double k) {
         //BasicSymmetricalDistanceMatrix p=phyloProb(pp,k);
          LFSR258  r=new  LFSR258();   
         ArrayList<PhylogenyNode> nodes=(ArrayList<PhylogenyNode>) pp.getExternalNodes(); 
         ArrayList<String> names=new ArrayList<String>();                 
         ArrayList<String> output=new ArrayList<String>();
         int n=nodes.size();
         
         boolean[] done=new boolean[n];
         String[] st=new String[n]; 
         
         for (int i=0; i<n;i++) {
             done[i]=false;
             st[i]="";
             names.add(nodes.get(i).getName());             
         } 
         output.addAll(names);
        // Setp 0; get Initial distance
         double[][] prob=phyloProb(pp, k);         
        // Step 1. iterate         
          while (prob.length>1) {              
              int first=r.nextInt(0, prob.length-1);              
              int second=getRowPos(prob, first);              
              output=permute(output, names.get(first),names.get(second));              
              names.remove(first);
              prob=removeRowCol(prob, first, second);                                      
          }
          
          return output;
        }
         
         static void printm(double[][] p) {
             for (int i=0; i<p.length;i++) {
                 for (int j=0; j<p[i].length;j++) {
                     System.out.print(p[i][j]+" ");
                 }
                 System.out.println("");
             }
         }
}
