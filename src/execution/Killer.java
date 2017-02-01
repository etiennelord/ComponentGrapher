package execution;

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

import config.Config;
import config.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import config.properties;

/**
 * This is a task to kill a Windows program
 * @author Etienne Lord
 */
public class Killer  {

    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    String executable=""; // Executable to kill
    Vector<String>output=new Vector<String>();
    protected Thread thread;                  //Thread
    protected Process p;                      //Process
    properties properties;
    public static Config config=new Config();
    

    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR

//    public Killer(workflow_properties properties) {        
//        this.properties=properties;
//        this.executable=properties.getExecutable();
//        properties.removeStatus();
//        properties.setStatus(RunProgram.status_error, "Cancelled...");
//        if (properties.isSet("Executable")) {
//            Config.log("Cancelling "+executable);
//            kill();
//        }
//    }

    
     /**
       * This is the Main procedure responsable for:
       * 1. Separate the commandline
       * 2. Replace some parameters
       * @return
       */
      public String[] init_createCommandLine_winXPpro() {
          // Initialize the String[]
          String[] com=new String[30];
          for (int i=0; i<com.length;i++) com[i]="";
          File f=new File(executable);
          com[0]="taskkill.exe";
          com[1]="/IM";
          com[2]=f.getName();
          com[3]="/F";
          com[4]="/T";
          return com;
      }

      public String[] init_createCommandLine_Linux() {
          // Initialize the String[]
          String[] com=new String[30];
          for (int i=0; i<com.length;i++) com[i]="";
          File f=new File(executable);
          com[0]="taskkill.exe";              
          com[1]="/IM";
          com[2]=f.getName();
          com[3]="/F";
          com[4]="/T";          
          return com;
      }

      public String[] init_createCommandLine_winXPHome() {
          // Initialize the String[]

          String[] com=new String[30];
          for (int i=0; i<com.length;i++) com[i]="";
          File f=new File(executable);          
          String filename=f.getName().toLowerCase();
          if (filename.endsWith(".exe")||filename.endsWith(".com")) filename=filename.substring(0, filename.length()-4);
          com[0]="tskill.exe";
          com[1]=filename;
          return com;
      }


      public boolean kill()  {
           //--Run the thread and catch stdout and stderr
           try {
           
           //--Actual stop
           p = RunProgram.getRuntime().exec(this.init_createCommandLine_winXPHome());
           InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
           InputStreamThread  stdout = new InputStreamThread(p.getInputStream());
           //InputStreamThread  stdout2 = new InputStreamThread(p.getOutputStream());
           //--Wait for the exitValue
           int exitvalue=p.waitFor();
           Config.log("KillTask "+executable);
           Config.log("Commandline: "+util.toString(this.init_createCommandLine_winXPHome()));
           Config.log("Exit Value: "+exitvalue);
           if (exitvalue!=0) {
               Config.log("Unable to kill with normal command, trying Taskkill.exe");
               Config.log("Commandline: "+util.toString(this.init_createCommandLine_winXPpro()));
               p = RunProgram.getRuntime().exec(this.init_createCommandLine_winXPpro());
               stderr = new InputStreamThread(p.getErrorStream());
               stdout = new InputStreamThread(p.getInputStream());
               exitvalue=p.waitFor();
               Config.log("Exit Value: "+exitvalue);
           }
           } catch(Exception e) {Config.log("Error in KillTask "+e.getMessage());}
         
           return true;
     }

  public class InputStreamThread {

         InputStream is;
         boolean debug=false;
         private Vector<String> output=new Vector<String>();

         public InputStreamThread(InputStream is) {
             this.is=is;
             runthread();
         }

         public void runthread() {
             Thread thread=new Thread(){

            @Override
             public void run() {
                try {
                BufferedReader br=new BufferedReader(new InputStreamReader(is));
                String line=null;
                while ((line=br.readLine())!=null) {
                          //if (!line.endsWith("\n")) line+="\n";
                    Config.log(line);
                    //getOutput().add(line);
                }
                } catch(Exception e){e.printStackTrace();}
             }
             };
             thread.start();
         }
    }
}
