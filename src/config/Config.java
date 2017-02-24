package config;

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

import java.awt.Font;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;



/**
 * Class Config : Class containing the main program configuration path, filename, icon, etc.
 *
 */
public class Config {

    public static properties properties;
    
    ////////////////////////////////////////////////////////////////////////////
    /// Global constant

    
    //-- Default log file (can be changed) January 2011
    //-- Required to used as Library sometimes...
    public static String log_file="compisitegrapher.log";
    public static boolean nolog=false; //--do not log to file
    
    
    //-- Status code of the various programs

    public static final int status_nothing=0;
    public static final int status_idle=1;
    public static final int status_changed=10;
    public static final int status_done=100;
    public static final int status_error=404; 
    public static final int status_running=500;

    public static String currentPath="";

    //////////////////////////////////////////////////////////////////////////
    /// Command-line Arguments
    ///
    /// Note: this can be used in commandLine mode to pass files as STDINPUT
    public static String[] cmdArgs={};

     ///////////////////////////////////////////////////////////////////////////
    //Clustering Option
    public static String[] ClusteringOption={"Display all"};
    ////////////////////////////////////////////////////////////////////////////
    /// Application icon
    
     static Image image=null;
    public static ImageIcon icon=null;
    public static ImageIcon loading_icon=null;
    public String defaultSaveFile="config.dat";
    public static PrintWriter log=null;
    public static  Font glyphicon = null;
  

    public Config()  {
          
        
       if (properties==null) {
            currentPath=new File("").getAbsolutePath();
            properties=new properties();
            if (!Load()) {
                 setDefaultProperties();
                Save();
            } 
            // -- Load the icons           
            if (log==null) {
                try {
                log=new PrintWriter(new FileWriter(new File(log_file),true));
                } catch(Exception e) {Config.log("Unable to open log file: "+log_file);return;}
            }
       //--Set compiler properties
            try {
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                set("CompilerFound", (compiler==null?false:true));
            } catch(Exception e) {
                Config.log("Unable to determine compiler: "+e.getMessage());
            }
       }
       //--Bet sure to create the results directory
       createDir(get("resultsDir"));
      
    }
 
    /**
     * Log to the log file compositegrapher.log
     * Note: not boolean beacause of compilation error (?)
     * @param s 
     */
    public static void log(String s) {
        if (nolog) {
            System.out.println(s);
            return; //--Nothing else to do
        }
        if (log==null) {
                try {
                log=new PrintWriter(new FileWriter(new File(log_file),true));
                log.flush();
                } catch(Exception e) {System.out.println("Unable to open log file: "+log_file);return ;}
            }        
        try {
            log.println(s);
          
            log.flush();            
        } catch(Exception e) {
            try {
            log=new PrintWriter(new FileWriter(new File(log_file),true));
          
            log.println(e);
            log.flush();
            } catch(Exception e2) {return ;}
        }        
    }

    public void loadIcon() {
        //-- Load Icon
        File application_icon_filename=new File(get("smallIconPath"));
        File loading_icon_file=new File(dataPath("loading.gif"));
                    try {
                    icon = new ImageIcon(application_icon_filename.toURI().toURL());                    
                    image = icon.getImage();
                    icon= new ImageIcon(loading_icon_file.toURI().toURL());                    
                    loading_icon=icon;
        //--Load Database Tree Explorer icon
             String[] iconName=loadImageslisting(iconsPath());
             for (String filename:iconName) {
                 if (filename.indexOf("icons")>-1) {
                     application_icon_filename=new File(iconsPath()+File.separator+filename);
                     ImageIcon tmpImage=new ImageIcon(application_icon_filename.toURI().toURL());
                     filename=filename.substring(filename.indexOf(".")+1,filename.indexOf("_"));
                     properties.put(filename, tmpImage);
                 }
             }
                        
        } catch(Exception e) {}
    } 
  
    public static Font getGlyphicon() {
         if (glyphicon==null) {
            try {                   
                    glyphicon = Font.createFont(Font.TRUETYPE_FONT, new File("data"+File.separator+"fontawesome-webfont.ttf"));                    
                    glyphicon = glyphicon.deriveFont(Font.PLAIN, 12f);                                       
         } catch(Exception e2) {
             Config.log("Unable to load glyphicon font.");
         }
       }
         return glyphicon;
    }
    
    public static Image getImage() {
        if (image==null) {
            Config c=new Config();
            c.loadIcon();
        }
        return image;
    }
     /**
     * Load a properties file
     * Note: The Name of this properties will be set to the key("Name")
     *
     * @param filename
     * @return true if success
     */
    public boolean Load(String filename) {
    try {
        BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
        properties.load(br);
        br.close();
        //Config.log("Loading of config file "+filename+" successfull.");
    } catch(Exception e) {
        return false;}
    return true;
    }

    public boolean Load() {
        return Load(defaultSaveFile);
    }

    /**
     * Save a properties file
     * @param filename
     * @return true if success, false otherwise
     */
    public boolean Save(String filename) {
    try {
        properties.save(filename);
        } catch(Exception e) {return false;}
    return true;
    }

    public boolean Save() {
        return Save(defaultSaveFile);
    }

     /**
      * This set the default configuration properties
      */
     public void setDefaultProperties() {  
       
         //--Path
         set("Name","Configuration file (config.dat)");
         set("currentPath", currentPath);
         set("explorerPath", currentPath);
         //==
         //==Note:
         //==This databasePath represent the default project filename
         
         set("dataPath",currentPath+File.separator+"data");
         set("ExecutableDir",currentPath+File.separator+"Executable");
         set("tmpDir", currentPath+File.separator+"tmp");
         set("temporaryDir", currentPath+File.separator+"tmp");
         set("resultsDir", currentPath+File.separator+"results");
         set("testDir", currentPath+File.separator+"test");
         set("iconsPath",currentPath+File.separator+"data"+File.separator+"icons");
         set("projectsDir", currentPath+File.separator+"projects"); // Projects Directory
         
         //--Try to create the tempDir (temporary file directory) if doesn't exists
         createDir(get("tmpDir"));
         createDir(get("resultsDir"));
         //createDir(get("projectsDir"));
         
         createDir(get("temporaryDir"));

         //--Version and Other
         
         set("author","Etienne Lord, Jananan Pathmanathan, Vladimir Makarenkov, François-Joseph Lapointe, Éric Bapteste");
         set("version","1.0");
         set("applicationName","CompositeGrapher");                  
         set("imagePath",currentPath+File.separator+"data"+File.separator+"images");
         set("FirstTime",1);
         set("last_matrix","sample"+File.separator+"sample_4.txt");
         set("project_filename","experiments.txt");
      //--Programs          
          set("last_path", currentPath);
        //Databases used
        
     }

 

 ///////////////////////////////////////////////////////////////////////////////
 /// Defautl Getter / Setter

 public String dataPath() {
     return this.get("dataPath");
 }

  public String dataPath(String filename) {
     if (!util.DirExists("data")) {
         this.createDir("data");
         set("dataPath","data");         
     }
      return this.get("dataPath")+File.separator+filename;
 }
  
 public String propertiesPath() {
     return this.get("propertiesPath");
 }
 public String iconsPath() {
     return this.get("iconsPath");
 }
 public String imagePath() {
     return this.get("imagePath");
 }

 public String tmpDir() {
     return this.get("tmpDir");
 }

 public String resultsDir() {
     return this.get("resultsDir");
 }

  public String ExecutableDir() {
     String dir= this.get("ExecutableDir");
     return dir;
 }

 public String testDir(){
     return this.get("testDir");
 }

 public String projectsDir(){
     return this.get("projectsDir");
 }

public String databasePath() {
    return this.get("databasePath");
}
public String temporaryDir() {
     return this.get("temporaryDir");
 }
public void temporaryDir(String dir) {
    //--Delete old if exists
    if (util.FileExists(temporaryDir())) {
        util.deleteFile(temporaryDir());
    }
    String directory="tmp"+File.separator+dir;
    if (!util.FileExists(directory)) this.createDir(directory);
    set("temporaryDir",directory);
}


public String currentPath() {
    return this.get("currentPath");
}

public boolean isCompilerFound() {
    return this.getBoolean("CompilerFound");
}

  /**
   * Normal getter for config option
   * @param key
   * @return
   */
   public String get(Object key) {
        String value=(String) properties.get(key);
        return (value==null?"Key: "+key+" not found.":value);
      }

   public ImageIcon getIcon(Object key) {
       return properties.getImageIcon(key);
   }

   public Boolean getBoolean(Object key) {
       return properties.getBoolean(key);
   }

   public Integer getInt(Object key) {
       return properties.getInt(key);
   }

   /**
    * Normal setter for config option
    * @param key
    * @param value
    */
   public void set(Object key, Object value) {
       try {
        properties.put(key, value);
       } catch(Exception e) {}
   }

   /**
    * Normal remove
    * @param key
    *
    */
   public void remove(Object key) {
       try {
        properties.remove(key);
       } catch(Exception e){}
   }

   public void createDir(String path) {
       File dir=new File(path);
       //1.5 created if
       try {
        if (!dir.exists()||!dir.isDirectory()) {
           if (!dir.mkdirs()) {
               Config.log("Unable to create "+path);
            }
        }
       } catch(Exception e) {Config.log("Unable to create "+path+ " directory");}
   }

//   public static boolean changeDir(String path) {
//       try {
//           String[] command={"cmd.exe","/C","CD","\""+path+"\""};
//           for(String s:command) Config.log(s+"\t");
//           Runtime r = Runtime.getRuntime();
//           Process p = r.exec(command);
//           int result=p.waitFor();
//           File f=new File("");
//           Config.log(path+" : "+f.getAbsolutePath());
//           return result==0;
//       } catch(Exception e) {e.printStackTrace();return false;}
//   }

      /**
 * Return a string array of the PNG in the specify directory or null if not found
 */
    public String[] loadImageslisting (String path) {
      FilenameFilter filter=new FilenameFilter() {
      public boolean accept(File dir, String name) {
      if (name.charAt(0) == '.') return false;
      if (name.toLowerCase().endsWith(".png")) return true;
      return false;
      }
      };
      File dataFolder = new File(path);
      String[] names = dataFolder.list(filter);
      return names;
    }

    /**
     * List the files in directory
     * @return
     */
    public static Vector<String> listDir(String dir) {
        Vector<String> tmp=new Vector<String>();
        try {
            File f=new File(dir);
            if (!f.isDirectory()) {
                dir+=File.separator;
                f=new File(dir);
            }
            for (String filename:f.list()) tmp.add(filename);
        } catch(Exception e) {}
        return tmp;
    }

    /**
     * List the files in directory and return filename with the full path
     * @return
     */
    public static Vector<String> listDirWithFullPath(String dir) {
        Vector<String> tmp=new Vector<String>();
        try {
            File f=new File(dir);
            if (!f.isDirectory()) {
                dir+=File.separator;
                f=new File(dir);
            }
            for (String filename:f.list()) tmp.add(f.getAbsolutePath()+File.separator+filename);
        } catch(Exception e) {}
        return tmp;
    }
    
    /**
     * List the files in the current directory
     * @return
     */
    public static Vector<String> listCurrentDir() {
        String filename=(new File("").getAbsolutePath())+File.separator;
        return listDir(filename);
    }

     /**
     * @return the explorerPath
     */
    public String getExplorerPath() {
        return (properties.isSet("explorerPath")?properties.get("explorerPath"):"");
    }
   
      /**
     * @return the explorerPath
     */
    public void setExplorerPath(String path) {
        properties.put("explorerPath", path);
    }
   
    
    /**
     * This get the last Workflow Database loaded...
     * @return the explorerPath
     */
    public String getLastProject() {
        return (properties.isSet("lastWorkflow")?properties.get("lastWorkflow"):"");
    }

    /**
     * This get the last Workflow Database loaded...
     * @param aExplorerPath the explorerPath to set
     */
    public void setLastWorkflow(String aLastWorkflow) {
        properties.put("lastWorkflow",aLastWorkflow);
        this.Save();
    }

    public boolean isSet(Object key) {
        return properties.isSet(key);
    }
    
    
    public static String CleanFileName(String filename)
    {   
        File f=new File(filename);        
        String s= f.getName().replaceAll("[^a-zA-Z0-9-_]", "_");        
        return s;
    }
    
    // public long getRunningTime() {
   //     return System.currentTimeMillis()-timerunning;
   // }
    
}
