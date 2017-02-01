package execution;

//////////////////////////////////////////////////////////////////////////////////////////////33
///
/// Create a Thread to run dnaml
///
/// Etienne Lord 2009


import config.Config;
import java.io.*;
import java.lang.Thread;
import java.math.BigInteger;
import java.util.Vector;
import matrix.DataMatrix;


public class componentGrapher extends DataMatrix implements Serializable {
   private String infile="";               //Unique infile : Must be deleted at the end
    private String outfile="";              //Unique outfile: Must be deleted at the end
    private String filename="";
    public String name="COMPONENT-GRAPHER.jar";
    public String Xmx="4g";
    private Vector<String> output=new Vector<String>();
    public int exitVal=0; //Program exit value
    private boolean done=false;
    private String pathDnaml="";
    private String pathToOutput="";
    //Outgroup sequence
    public static int outgroup=0;
    // Debug and programming variables
    public static boolean debug=false;
    // For the Thread version
    private int status=0;           //status code
    private long timerunning=0;     //time running
    Thread thread;                  //Thread
    Process p;                      //Process
    public int retry=0;             //Retry
    // status code
    public final static int status_done=100;
    public final static int status_running=200;
    public final static int status_running_query_done=201;
    public final static int status_error=999;
    

    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public componentGrapher(Config config, DataMatrix seq) {      
        if (debug) System.out.println(name);       
        runthread();
        while (!done){}
    }

     

     public void runthread() {

         thread=new Thread(){

            @Override
             public void run() {
                 try {
                      status=  status_running;
                       // RUN THE PROGRAM
                       Runtime r = Runtime.getRuntime();
                       String[] com=new String[11];
                       for (int i=0; i<com.length;i++) com[i]="";
                       com[0]="java";                      
                       com[1]="-Xmx"+Xmx;
                       com[2]="-jar";
                       com[3]=name;                       
                       p = r.exec(com);
                       InputStream stderr = p.getErrorStream();
                       InputStream stdoutput = p.getInputStream();
                       InputStreamReader isr = new InputStreamReader(stdoutput);
                       InputStreamReader isr2 = new InputStreamReader(stderr);
                       BufferedReader br = new BufferedReader(isr);
                       BufferedReader br2= new BufferedReader(isr2);
                       String line = null;
                       while ( (line = br.readLine()) != null) {
                           getOutput().add(line); //Add line to the output vector
                           if (debug) System.out.println(line);
                       }
                           exitVal = p.waitFor();
                        if (exitVal!=0) {
                            status=status_error;
                        } else {
                            status=status_done;//                          
                        }
                        if (debug) System.out.println("done.");
                } catch (Exception ex) {
                    status=status_error;
                    ex.printStackTrace();
                }
            done=true;
            }
            
        };
        timerunning=System.currentTimeMillis();
        thread.start();
  }

     /**
      * Le thread est fini?
      * @return done
      */
     public boolean isDone() {
         return done;
     }

    ////////////////////////////////////////////////////////////////////////////
   // HELPER FUNCTIONS

 
   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS

    /**
     * Create a Phylip configurationFile [param] (Override this method to change param file)
     * @param filename
     * @return true if success!
     */
    public boolean createConfigFile(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.println("0");
            //pw.println("I");
            if (outgroup>0) {
                //Sequence number valid?
                    pw.println("O");
                    pw.println(outgroup);
            }
            pw.println("Y");
            pw.println("R");
            pw.flush();
            pw.close();

        } catch (Exception e) {System.out.println("Error in creating ConfigFile "+filename);return false;}
        return true;
    }

    
    @Override
    public int hashCode() {
         long time=System.currentTimeMillis();
         return BigInteger.valueOf(time).intValue();        
     }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final componentGrapher other = (componentGrapher) obj;
        if ((this.filename == null) ? (other.filename != null) : !this.filename.equals(other.filename)) {
            return false;
        }
        return true;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

      /////////////////////////////////////////////////////////////////////////
    /// THREAD

     /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     *
     * @return the timerunning
     */
    public long getRunningTime() {
        return System.currentTimeMillis()-timerunning;
    }

    /**
     * Kill the current thread
     * @return True if Succes
     */
    public boolean KillThread() {
     try {
        if (timerunning!=0&&thread!=null) {
            p.destroy();
            if (thread.isAlive()) thread.interrupt();
            done=true;
            status=status_done;
        }
     } catch(Exception e) {return false;}
     return true;
    }

    public int getExitVal() {
        return this.exitVal;
    }

    /**
     * @return the output
     */
    public Vector<String> getOutput() {
        return output;
    }

    private String uniqueInfile(String filename) {
        if (infile.equals("")) this.infile=filename+this.hashCode();
        return infile;
    }

    public Object getItself() {
        return this;
    }

     public String getName() {
        return this.name;
    }

    public String getInformation() {
        //TO DO ADD MORE
        return " ";
    }

}
