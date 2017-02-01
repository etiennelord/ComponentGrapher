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
import config.properties;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RunProgram  {

   
	
    ////////////////////////////////////////////////////////////////////////////
    /// Local variables
    protected int id=0;    
    protected String note = "";
    protected String programTimeStart = "";
    protected String programTimeEnd = "";
    protected String runProgramOutput="";

    public static Config config=new Config();

     

    ////////////////////////////////////////////////////////////////////////////
    /// Running program
    
    //--Input / Output
    private final ArrayList<String> outputText=new ArrayList<String>();


    ////////////////////////////////////////////////////////////////////////////
    ///

    protected HashMap<String,String> inputFilenames=new HashMap<String,String>();
    protected properties properties=new properties();
    protected long timerunning=0;     //time running
    protected Thread thread;                  //Thread
    protected Process p;                      //Process 
    protected static Runtime r;               //Runtime object
    protected String[] commandline={};

     //-- Status code (This is the most important table )
    public static final int status_nothing=0;
    public static final int status_idle=1;
    public static final int status_changed=10;
    public static final int status_done=100;
    public static final int status_error=404; //:)
    public static final int status_warning=900; 
    public static final int status_running=500;
    public static final int status_BadRequirements=450;
    public static final int status_runningclassnotfound=408;
    public static final int status_programnotfound=410;    

    ////////////////////////////////////////////////////////////////////////////
    /// Kill Thread Java
    protected boolean cancel=false; //obsolete
  

         
    public RunProgram(String command) {
        properties.put("CommandLine", command);
        if (r==null) r=Runtime.getRuntime();
    }
  
   
   
     ///////////////////////////////////////////////////////////////////////////
     /// Execution

     /**
      * Main function to call
      */
     public void execute() {
         runthread();
         //--Note: removed since its handle in the programs class 
         //if (properties.getBoolean("NoThread")) while(!isDone()){}
     }

     /**
      * Main function to call if we want to run something... 
      * Without waiting for it to terminate
      * Example: External editor
      */
     public void executeWithoutWait() {
          runthread_withoutWait();
          //--Note: removed since its handle in the programs class
          //if (properties.getBoolean("NoThread")) while(!isDone()){}
     }
   
//    /**
//     * @return the EndRegex
//     */
//    public Pattern getErrorEndRegex() {
//        return ErrorEndRegex;
//    }
//
//    /**
//     * @param EndRegex the EndRegex to set
//     */
//    public void setErrorEndRegex(Pattern EndRegex) {
//        this.ErrorEndRegex = EndRegex;
//    }
//
//    /**
//     * @return the NormalEndRegex
//     */
//    public Pattern getNormalEndRegex() {
//        return NormalEndRegex;
//    }
//
//    /**
//     * @param NormalEndRegex the NormalEndRegex to set
//     */
//    public void setNormalEndRegex(Pattern NormalEndRegex) {
//        this.NormalEndRegex = NormalEndRegex;
//    }

   

     ///////////////////////////////////////////////////////////////////////////
     ///

     public class InputStreamThread {
         //--TO DO: create a file handler here...
         
         
         InputStream is;
         boolean debug=false;
        // private Vector<String> output=new Vector<String>();

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
                    //--Hack for

                    msg(line);        
                    
                }
                } catch(Exception e){e.printStackTrace();}
             }
             };
             thread.start();
         }
     }



     /**
      * Standard thread definition with various level of overriding
      */
      protected void runthread() {
        
       thread=new Thread(){

            @Override
             public void run() {
                //--Cascade run...
                try {
                    //--Clean memory  
                    util.CleanMemory();  
                    //--pre run initialization
                        setStatus(status_running,"Initialization...");
                        if (init_run()&&!isInterrupted()) {                           
                            //--actual run
                              setStatus(status_running,"<-Program Output->"); 
                              if (do_run()&&!isInterrupted()) {
                                    //--Post run                                                                   
                                    setStatus(status_running,"<-End Program Output ->");
                                    post_run(); 
                              }                            
                       }
                       //--Note: work Even if not set because We return 0...
                        //setStatus(getStatus(), "Total running time: "+Util.msToString(getRunningTime()));
                        if (properties.getBoolean("VerifyExitValue")&&getExitVal()!=properties.getInt("NormalExitValue")) {
                                setStatus(status_error,"***Error with at "+getRunningTime()+" ms ExitValue: "+properties.get("ExitValue")+"\n");
                        }  else {
                                if (getStatus()!=status_error&&getStatus()!=status_BadRequirements&&getStatus()!=status_runningclassnotfound&&getStatus()!=status_programnotfound) setStatus(status_done,"");
                        }

                } catch (Exception ex) {
                    if (properties.getBoolean("debug")) ex.printStackTrace();                    
                    if (!cancel) {
                        setStatus(status_error,"Error in running... \n"+ex.getMessage());
                    }
                }                
                programTimeEnd=util.returnCurrentDateAndTime();               
                         
            }

            @Override
            public void destroy() {                                
                 if (p!=null) p.destroy();
                 this.interrupt();                                  
                 thread.currentThread().interrupt();
            }



        };


        timerunning=System.currentTimeMillis();
        properties.put("TimeRunning", timerunning);
        programTimeStart=util.returnCurrentDateAndTime();
        
        thread.start();
       
  }

    /**
      * Standard thread definition with various level of overriding
      */
      protected void runthread_withoutWait() {

       thread=new Thread(){

            @Override
             public void run() {
                //--Cascade run...                
                try {
                       //--pre run initialization
                     setStatus(status_running,"Initialization...");
                    if (init_run()&&!isInterrupted()) {
                            //--actual run
                             //--actual run
                              setStatus(status_running,"<-Program Output->");                           
                              if (do_run_withoutWait()&&!isInterrupted()) {
                                       //--Post run
                                    setStatus(status_running,"<-End Program Output ->");
                                    post_run();
                               }
                       }
                       //--Note: work Even if not set because We return 0...
                       //setStatus(getStatus(), "Total running time: "+Util.msToString(getRunningTime()));
                       if (properties.getBoolean("VerifyExitValue")&&getExitVal()!=properties.getInt("NormalExitValue")) {
                                setStatus(status_error,"***Error with at "+getRunningTime()+" ms ExitValue: "+properties.get("ExitValue")+"\n");
                        }  else {
                                if (getStatus()!=status_error&&getStatus()!=status_BadRequirements&&getStatus()!=status_runningclassnotfound&&getStatus()!=status_programnotfound) setStatus(status_done,"");
                        }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    setStatus(status_error,"Error in running... \n"+ex.getMessage());
                   //if (properties.getBoolean("debug"))                  
                }             
                programTimeEnd=util.returnCurrentDateAndTime();
                }             
        };
        timerunning=System.currentTimeMillis();
        properties.put("TimeRunning", timerunning);
        programTimeStart=util.returnCurrentDateAndTime(); 
        thread.start();

  }

    
    public String[] updateCommandLine(String[] cli) {
        //--Note: 50 is arbitrary number large enough...
        String[] command= new String[50];
        //--Initialization
        for (int i=0; i<50;i++) {
            if (i<cli.length) {
                command[i]=cli[i];
            } else command[i]="";
        }
       
        //--CASE 1. MacOSX (Warning, 1st because MAC_OSX is Linux (lol)   
          if (config.getBoolean("MacOSX")) {
               if (command[0].equalsIgnoreCase("cmd.exe")) 
                   for (int i=0; i<command.length-2;i++) command[i]=command[i+2];
               
               //--Hack
               if (command[0].equalsIgnoreCase("java")) {
                   //--Any extra command?
                   //--We locate the good spot
                   if (command[2].startsWith("-Xmx")||command[2].startsWith("-classpath")) {
                       command[3]=properties.getExecutableMacOSX();
                   } else {
                       command[2]=properties.getExecutableMacOSX();
                   }                                      
               } else {
                   command[0]=properties.getExecutableMacOSX();
               }    
              
              // return command;
           } else             
           //--or CASE 2.Linux?
           if (config.getBoolean("Linux")) {
               //--Test if we included the cmd.exe /C
               if (command[0].equalsIgnoreCase("cmd.exe")) 
                   for (int i=0; i<command.length-2;i++) command[i]=command[i+2];
               //--Hack
                //--Hack
               if (command[0].equalsIgnoreCase("java")) {
                   command[2]=properties.getExecutableLinux();
               } else {
                    command[0]=properties.getExecutableLinux();
               }               
           } 
          
          //--Change the - to -- if found...
//               for (int i=0; i<command.length;i++) {
//                   if (command[i].startsWith("-")) command[i]="-"+command[i];
//               }
          
           //--CASE 3. Use Alternative^
        if (properties.getBoolean("UseAlternative")) {
             command[0]=properties.getAlternative();
           } 
           
        // Finally return the command   
        return command;
    }
            
  /**
   * This is the initialization step of this particular program:
   * Note: Normally, we shouldn't overide directly this method
   *
   * Overidable:
   *    init_checkRequirements
   *    init_checkCreateInput
   *    init_createCommandline
   *
   * 1-Verification of the specific requierement
   * 2-Verification of the input
   * 3-Creation of the Running commandline
   * @throws Exception
   */
     public boolean init_run() throws Exception {        
         setStatus(status_idle,"");
         if (properties.isSet("Name")) {
             setStatus(status_running,"Running ["+properties.getName()+"]");
          } else {
             setStatus(status_running,"Running ["+properties.get("Executable")+"]");
          }
          //--CASE 1. Check if program is found on this system         
          if (true) {
                  //--ok... Everything is in order...
              } else {
              //--CASE 2. Ok, executable not found, maybe we need to update properties with new executable 
              //--Update (?)
                  String filename=properties.get("filename");
                  //--Take only the filename
                  File f=new File(filename);
                  filename=f.getName();
                  //--New - November 2011 -- Scan for filename
                  try {
                  Pattern p=Pattern.compile("properties.((.*).properties)");
                  
                  Matcher m=p.matcher(filename);
                  
                      if (m.find()) {
                      //System.out.println("found");
                      filename=m.group(1);
                  }
                  } catch(Exception e) {}
                  properties newprop=new properties();
                  newprop.load(filename, config.propertiesPath()); 
                  //System.out.println(newprop.filename+" "+filename+" "+config.propertiesPath());
                  if (true) {
                    setStatus(status_running,"Updating executable for "+properties.getName()+" from "+properties.getExecutable()+" to "+newprop.getExecutable());    
                    properties.setExecutable(newprop.getExecutable());
                    properties.setExecutableMacOSX(newprop.getExecutableMacOSX());
                    properties.setExecutableLinux(newprop.getExecutableLinux());
                    if (newprop.isSet("RunningDirectory")) properties.put("RunningDirectory", newprop.get("RunningDirectory"));                    
                    if (newprop.isSet("RuntimeMacOSX")) properties.put("RuntimeMacOSX", newprop.get("RuntimeMacOSX")) ;       
                  } else {
                        //--CASE 3. Ok, nothing found... report...
                      setStatus(status_programnotfound,"Executable not found: "+properties.getExecutable());
                      return false;
                  }             
            }
          //--Check the program requirement
           setStatus(status_running,"\tChecking program requirements...");
           if (!init_checkRequirements()) {
               //--
               setStatus(status_BadRequirements,"Some requirements not found.");
               return false;
           }

           //--Create the input
           setStatus(status_running,"\tCreating inputs...");
           init_createInput();

           //--Create the commandline and save to properties and Commandline_Running
           setStatus(status_running,"\tCreating commandline...");
           commandline = init_createCommandLine();
           //--Update command line with the good executable
           commandline = updateCommandLine(commandline);
           properties.put("Commandline_Running", util.toString(commandline));
           //--Output commmand line
           if (util.toString(commandline).indexOf("Not Set")==-1) {
               setStatus(status_running,properties.get("Commandline_Running"));
           } else if (!properties.getBoolean("InternalArmadilloApplication")&&!properties.getBoolean("WebServices")) {
               Config.log("Warning: Not Set in commandline:"+util.toString(commandline));
               setStatus(status_warning,"Error: Not Set in commandline:"+properties.get("Commandline_Running"));
           }          
           return true;
      }

     /**
      * This is the the actual run of the program
      * 1-Execute in the thread the commandline
      * 2-Catch both stderr and stdout
      * 3-Put the program ExitValue in properties->ExitValue
      * 4-Put  both stderr and stdout in the output vector and in the "output"properties
      * @throws Exception
      */
     public boolean do_run() throws Exception {
           //--Run the thread and catch stdout and stderr
           setStatus(status_running, "\tRunning program...");          
           System.out.println(util.toString(commandline));
           ProcessBuilder pb=new ProcessBuilder(commandline);
           if (properties.isSet("RunningDirectory")) {
               pb.directory(new File(properties.get("RunningDirectory")));
           }
           
           r = Runtime.getRuntime();
           //--Test August 2011 - For Mac OS X
           if (config.getBoolean("MacOSX")) {
               //System.out.println("MacOSX"); 
             if (properties.isSet("RuntimeMacOSX")) {
             
                 String execution_type=properties.get("RuntimeMacOSX");
                 
                 //--Default
                 if (execution_type.startsWith("default")) {
                     //? Not suppose to exists...
                     p = pb.start();
                 }
                 
                 //--Runtime (r.exec)
                 if (execution_type.startsWith("runtime")) {
                     System.out.println("Running by runtime..."); 
                     //--IF MAC_OSX, group option if UseRuntimeMacOSX
                     String cmdm="";
                      for (int i=0; i<commandline.length;i++) {
                          cmdm+=commandline[i]+" ";
                      }
                          commandline=new String[1];
                          commandline[0]=cmdm;                                       
                     p = r.exec(util.toString(commandline));
                 }
                 
                 //--Bash...
                 if (execution_type.startsWith("bash (.sh)")) {
                     //System.out.println("Running from bash...");
                     //--Create a new bash file
                     util u = new util("RunProgram.sh");
                     u.println("#!/bin/sh");
                     u.println("echo \"Executing by bash command: "+properties.getName()+"\"");
                     u.println(util.toString(commandline));
                     //--Return the application error code
                     u.println("exit $?");
                     u.close();
                     p=r.exec("sh RunProgram.sh");
                  }
                
             
                 
               } //--End RuntimeMacOSX
           } else {
               p = pb.start();
           }
           
           //pb.redirectErrorStream(true)
           InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
           InputStreamThread  stdout = new InputStreamThread(p.getInputStream());
           int exitvalue=p.waitFor();    
           
           properties.put("ExitValue", exitvalue);
           msg("\tProgram Exit Value: "+getExitVal());
           
           return true;
     }

     /**
      * This is the the actual run of the program
      * 1-Execute in the thread the commandline
      * 2-Catch both stderr and stdout
      * 3-Put the program exitValue in properties->exitValue
      * 4-Put  both stderr and stdout in the output vector and in the "output"properties
      * @throws Exception
      */
     public boolean do_run_withoutWait() throws Exception {
           //--Run the thread and catch stdout and stderr
           setStatus(status_running, "\tRunning program...");
           //--Use alternative?
           
           r = Runtime.getRuntime();
           if (config.getBoolean("MacOSX")) {
             //--Test August 2011 - For Mac OS X        
               System.out.println("MacOSX"); 
             if (properties.isSet("RuntimeMacOSX")) {
             
                 String execution_type=properties.get("RuntimeMacOSX");
                 
                 //--Runtime (r.exec)
                 if (execution_type.startsWith("runtime")) {
                     System.out.println("Running by runtime..."); 
                     //--IF MAC_OSX, group option if UseRuntimeMacOSX
                     String cmdm="";
                      for (int i=0; i<commandline.length;i++) {
                          cmdm+=commandline[i]+" ";
                      }
                          commandline=new String[1];
                          commandline[0]=cmdm;                                       
                     p = r.exec(util.toString(commandline));
                 }
                 
                 //--Bash...
                 if (execution_type.startsWith("bash (.sh)")) {
                     System.out.println("Running from bash...");
                     //--Create a new bash file
                     util u = new util("RunProgram.sh");
                     u.println("#!/bin/sh");
                     u.println("echo \"Executing by bash command: "+properties.getName()+"\"");
                     u.println(util.toString(commandline));
                      //--Return the application error code
                     u.println("exit $?");
                     u.close();
                     p=r.exec("sh RunProgram.sh");
                  }
                
             
                 
               } //--End RuntimeMacOSX
           } else {
             p = r.exec(commandline);
           }
           InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
           InputStreamThread  stdout = new InputStreamThread(p.getInputStream());
           //--Wait for the exitValue

           properties.put("ExitValue", 0);
           msg("\tProgram Exit Value: "+getExitVal());
           return true;
     }


     /**
      * This is the post run part of the program 
      * 1-Clean the inputFilename
      * 2-Set some status (by default: success required an exitValue of 0)
      * 3-Parset the output
      * @throws Exception
      */

      public void post_run() throws Exception {
              setStatus(status_running,"\tParsing outputs... ");
              
              if (util.FileExists("RunProgram.sh")) {
                  setStatus(status_running,"\tDeleting RunProgram.sh... ");
                  util.deleteFile("RunProgram.sh");
              }
              post_parseOutput();
              //setStatus(status_idle,"");
      }


      /**
       * This is the Main procedure responsable for:
       * 1. Separate the commandline
       * 2. Replace some parameters
       * @return
       */
      public String[] init_createCommandLine() {

          // Initialize the String[]
        
          String[] com=new String[30];
          for (int i=0; i<com.length;i++) com[i]="";

          String Executable=properties.get("Executable");
          String cmdline=properties.get("CommandLine");


          if (Executable.endsWith(".jar")) {
              com[0]="java.exe";
              com[1]="-jar";
              com[2]=Executable;
          } else {
              //CASE 1. Simple command line, we need to parse it...
              int index=0;
              com[index++]="cmd.exe";
              com[index++]="/C";
              com[index++]=Executable;
              Vector<String>commands=getCommands(cmdline);
                for (String command:commands) {
                    com[index++]=command;
                }
          }
          
          return com;
      }


      //////////////////////////////////////////////////////////////////////////
      /// Experimental command building
      public Vector<String> getCommands(String commandline) {
          Vector<String>commands=new Vector<String>();
          String current="";
          boolean compositeString=false;
          //--Add a space at the end for safety
          commandline+=' ';
          for (Character c:commandline.toCharArray()) {
              //CASE 1. Composite String flag
              if (c=='"') {
                  if (compositeString) {
                      //-*-match and replace here
                      current=match(current);
                      commands.add(current);
                      current="";
                  } else compositeString=true;
              } else 
              //CASE 2. Space ... the normal spliter    
              if (c==' ') {
                  if (compositeString) {
                      current+=c;
                  } else {
                      //--match and replace here
                      current=match(current);
                      commands.add(current);
                      current="";
                  }
              } else 
              //CASE 3. Normal character, we add...
              {
                 current+=c;
              }    
          }
          return commands;
      }

      //////////////////////////////////////////////////////////////////////////
      /// Part of experimental command building
      public String match(String current) {
          //CASE 1. Match filename
          for (String key:this.inputFilenames.keySet()) {
              int index=current.indexOf(key);
              if (index>-1) {
                  //Config.log("MATCH: "+(String)key+" "+current);
                  current=current.substring(0,index);
                  String inputfilename=inputFilenames.get(key);
                  if (inputfilename.indexOf(" ")>-1) inputfilename="\""+inputfilename+"\"";
                  current+=inputfilename;
              }
          }
         
          //CASE 3. Match some keyword

          //Temporary directory
              int index=current.indexOf("temporary");
              if (index>-1) {
                  String tmp=current.substring(index+9,current.length());
                  String temporary=config.temporaryDir()+tmp;
                  if (temporary.indexOf(" ")>-1) temporary="\""+temporary+"\"";
                   current=current.substring(0, index)+temporary;
                  //current=current.replaceAll("temporary", config.temporaryDir());
              }
          return current;
      }

      /**
       * This is the function called before we start the program
       */
      public void init_createInput() {
          
       // 1. Create the list of current file --Clean path
        config.temporaryDir(""+util.returnCount());
     

        // 2. Create the output file
        
      }

      /**
       * this is the function called after we run the program
       */
      public void post_parseOutput() {
   
        }
      
      public boolean init_checkRequirements() {
          //Get require keywords;
          //workflow_properties_dictionnary dict =new  workflow_properties_dictionnary();
          if (properties.isSet("RequiredParameter")) {
              String[] params=properties.get("RequiredParameter").split(",");
              for (String s:params) {
                  if (!properties.isSet(s)) return false;
              }
          }
          return true;
      }

     ///////////////////////////////////////////////////////////////////////////
     /// Getter / Setter


    /**
     * @return the runProgram_id
     */
    public int getId() {
        return id;
    }

    /**
     * @param runProgram_id the runProgram_id to set
     */
    public void setId(int runProgram_id) {
        this.id = runProgram_id;
    }

       /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return the programTimeStart
     */
    public String getProgramTimeStart() {
        return programTimeStart;
    }

    /**
     * @param programTimeStart the programTimeStart to set
     */
    public void setProgramTimeStart(String programTimeStart) {
        this.programTimeStart = programTimeStart;
    }

    /**
     * @return the programTimeEnd
     */
    public String getProgramTimeEnd() {
        return programTimeEnd;
    }

    /**
     * @param programTimeEnd the programTimeEnd to set
     */
    public void setProgramTimeEnd(String programTimeEnd) {
        this.programTimeEnd = programTimeEnd;
    }

    /**
     * @return the runProgramOutput
     */
    public String getRunProgramOutput() {
        return runProgramOutput;
    }

    /**
     * @param runProgramOutput the runProgramOutput to set
     */
    public void setRunProgramOutput(String runProgramOutput) {                
        this.runProgramOutput = runProgramOutput;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Thread



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
         //--Set flag to tell the thread that we cancel
         cancel=true;
         //--Try to kill this program if it is still running...
         //--Note: duplicate from what we found in programs class
         if (properties.getStatus()==status_running) {
             //Killer k=new Killer(properties);
         }
         //--Lower this thread properties... So that that the system become more responsible
         thread.setPriority(Thread.MIN_PRIORITY);                                          
         //--Destroy any remaining process
            if (p!=null) p.destroy();
            if (thread!=null) thread.interrupt();            
     } catch(Exception e) {e.printStackTrace();}
     return true;
    }


    public int getExitVal() {
        return properties.getInt("ExitValue");
    }
  
    public Object getItself() {
        return this;
    }

     public String getName() {
        return properties.getName();
    }

    public String getInformation() {
        return properties.getDescription();
    }


     public void msg(String msg) {
         //System.out.println(msg);
         //TO DO: save that to a file...
         //--Good for texst version...
         outputText.add(msg+"\n");
         
         
         //--        
         Config.log(msg);
         //--Kill Switch
//         Matcher m_error_end=ErrorEndRegex.matcher(msg); 
//         Matcher m_normal_end=NormalEndRegex.matcher(msg);
//         if (m_error_end.find()) {
//             setStatus(status_error,"Warning. Kill switch found.\nProgram will be automatically terminated.\n");
//         }
//          if (m_normal_end.find()) {
//             setStatus(status_done,"Done.\nProgram will be automatically terminated.\n");
//         }
    }

    public properties getProperties() {
        return this.properties;
    }

    /**
     * Set the status of the RunProgram
     * Note: we catch Exception since might block will the thread is ended...
     * @param statusCode (see list on top)
     * @param msg
     */
     public void setStatus(int statusCode, String msg) {        
         synchronized(this) {                     
            try {             
                 if (!msg.isEmpty()) msg(msg);
                //--Save output

                if (statusCode==status_done||statusCode==status_error||statusCode==status_BadRequirements||statusCode==status_programnotfound||statusCode==status_runningclassnotfound) {
//                      OutputText out=new OutputText();
//                      out.setText(outputText); //--Note, this also set a note
//                      out.setName(properties.getName()+" -software output ("+Util.returnCurrentDateAndTime()+")");
//                      out.saveToDatabase();
//                      if (out.getId()==0) Config.log("Unable to save software ouput with program status "+statusCode); 
//                      properties.put("output_outputtext_id",out.getId());
                }
                properties.setStatus(statusCode, msg);
            } catch(Exception e) {
                Config.log(e.getMessage());
            }
        }
    }

    /**
     * @return the status
     */
    public int getStatus() {
        synchronized(this) {
            return properties.getStatus();
        }
    }
    
    

    /**
     * Return the state of the current RunProgram
     * @return True if done OR status is Error
     */
    public boolean isDone() {      
        synchronized(this) {
            int status=getStatus();
            return (status==status_error||status==status_done||status==status_BadRequirements||status==status_programnotfound||status==status_runningclassnotfound);
        }
    }

    /**
     * Return the logged program output
     * @return
     */
    public ArrayList<String> getOutputTXT() {
        //return new ArrayList<String>();
        return this.outputText;
    }

    public String getOutputText() {
        String s="";
        synchronized(outputText) {
            for (String si:outputText) s+=(si);
        }
        return s;
    }


   ////////////////////////////////////////////////////////////////////////////
   // HELPER FUNCTIONS

   /**
    * Delete a file
    * @param filename (to delete)
    * @return true if success
    */
    public boolean deleteFile(String filename) {
        try {
            File file=new File(filename);
            if (file.exists()) file.delete();
        } catch(Exception e) {Config.log("Unable to delete file "+filename);return false;}
        return true;
    }

    /**
     * Output to stdout the current program output associated with this program
     */
    public void PrintOutput() {
         //for (String stri:this.outputText) Config.log(stri);
    }

	/**
	 * This delete the file created by Phylip
	 */
	public boolean sureDelete(String path) {
        try {
            String[] files_to_delete={"outtree","outfile","infile","intree"};
			if (!path.isEmpty()&&!path.endsWith("\\")) path+="\\"; //Add in windows the dir separator
			
			for (String filename:files_to_delete) {
				deleteFile(path+filename);
			}
        } catch(Exception e) {Config.log("Problem in suredelete()");return false;}
        return true;
    }

    public boolean rename(String filename, String new_filename) {        
            if (!util.FileExists(new_filename)) {
                try {
                    util.copy(filename, new_filename);
                    util.deleteFile(filename);
                } catch (Exception ex) {return false;}
            } else return false;
        return true;
        }

    public void setExecutable(String Executable) {
        properties.setExecutable(Executable);
    }

    public String getExecutable() {
        return properties.getExecutable();
    }

     /**
     * @return the commandline
     */
    public String[] getCommandline() {
        return commandline;
    }

    /**
     * @param commandline the commandline to set
     */
    public void setCommandline(String[] commandline) {
        this.commandline = commandline;
    }

 /**
     * @return the r
     */
    public static Runtime getRuntime() {
        return r;
    }

    /**
     * Note: this method should be override in all subsequent child
     * @return 
     */
    public boolean test() {
        return false;
    };
}
