# ComponentGrapher

## About

This is the source code and some sample matrices for the ComponentGrapher software.

The ComponentGrapher software generates networks containing: identical, inclusion, overlap or disjoint links between character states.

## Usage

ComponentGrapher is a graphical (GUI) tool programmed in Java. 

1. First obtain a [binary release](#binary-releases) or [compile](#Compilation) your own version using Java and the Netbeans platform.
2. Extract the zipped archive into a new directory
3. Run the COMPONENT-GRAPHER software:

A) Double-click on the COMPONENT-GRAPHER.jar from the main directory

B) Alternatively, you can start the software from the command-line with some minimum and maximum memory requirements.  

```
java -Xms=2g -Xmx=4g -jar COMPONENT-GRAPHER.jar
```

C) To perform several permuation (higher than 1000) for the estimation of the _p_-value, it is recommended to run the analysis from the command-line. 

For example for the matrix panarthropod_wo_absent_trait.nex located in the _datasets_ directory  
With the **2000** permutations (-perm=2000)  and **100** concurrent threads (-maxpool=100)
```
java -Xms=2g -Xmx=6g -jar COMPONENT-GRAPHER.jar datasets/panarthropod_wo_absent_trait.nex -perm=2000 -maxpool=100
```

Full command-line options are*:   
* Please refers to the manual for more informations

| Options          | Usages                                                                 |
| ---------------- | ---------------------------------------------------------------------- |
|	-perm=100      | Specify the number permutation to performed.                           |
|	-maxpool=10    | Specify the number of concurrent threads.                              |
|	-output=dir    | Specify output directory.                                              |
|	-undefined     | Remove column containing undefined states (e.g. ?,-,\*)                |
|	-multiple      | Remove column containing polymorphic states (e.g. {1,2,3}).            |
|	-bipartite     | Output bipartite network files.                                        |
|	-graphml       | Output graphml network files (Gephi, Cytoscape compatibles).           |
|	-nodeid=file   | Provide a node identification file when importing the matrix.          |
|	-variation=X   | Specify the variation string to use if polymorphic states are presents.|
|	-triplets      | Output triplets file (triplets.txt)                                    |

Generated results files are:   

| Files                                   | Informations                                           |
| --------------------------------------- | ------------------------------------------------------ |
|	log.txt                               | logfile.                                               |
|	matrixfile_complete.txt               | edge list of the complete network.                     |
|	matrixfile_1.txt                      | edge list of the type 1 connections.                   |
|	matrixfile_2.txt                      | edge list of the type 2 connections.                   |
|	matrixfile_3.txt                      | edge list of the type 3 connections.                   |
|	matrixfile_4.txt                      | edge list of the type 4 connections.                   |
|	matrixfile_id.txt                     | identification for each node.                          |
|	reference.json                        | serialized results for original dataset.               |
|	randomization_XX.json                 | serialized results for each permuation.                |
|	matrixfile_summary.txt                | statistics and parameters for this run.                |
|	matrixfile_summary_statistics         | nodes summary statistics                               |
|	matrixfile_network_statistics.csv     | network statistics and _p_-value.                      |
|	matrixfile_nodes_statistics.csv       | nodes statistics and _p_-value.                        |

If the [-bipartite] option is use, the following files will also be produced:  

| Files                            | Informations                                   |
| -------------------------------- | ---------------------------------------------- |
| matrixfile.bipartite_complete.txt| bipartite network of the complete network.     |
| matrixfile.bipartite_1.txt       | bipartite network of the type 1                |
| matrixfile.bipartite_2.txt       | bipartite network of the type 2                |
| matrixfile.bipartite_3.txt       | bipartite network of the type 3                |
| matrixfile.bipartite_XX_id.txt   | identification for each node                   |

If the [-graphml] option is used, the following files will also be produced:

| Files                            | Informations                                   |
| -------------------------------- | ---------------------------------------------- |
|	matrixfile_complete.graphml    | complete network (type 1,2, and 3 edges)       |
|	matrixfile_1.graphml           | type 1 network                                 |
|	matrixfile_2.graphml           | type 2 network                                 |
|	matrixfile_3.graphml           | type 3 network                                 |
|	matrixfile_4.graphml           | type 4 network                                 |

If the [-triplets] option is used, the following file will also be produced:
	
| Files                           | Informations                                    |
| ------------------------------- | ----------------------------------------------- |
| triplets.txt                    | triplets found in type 3 network                |

### Binary releases

ComponentGrapher is compatible with Java SE5 (JDK 5) and later versions of Java. The latest
Java platform is available at
[Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

## Compilation

The source code is distributed as  Netbeans project format. 

## Dependencies

ComponentGrapher depends on the following library:

##### [SSJ](https://github.com/umontreal-simul/ssj)  
The SSJ library is used for BitVector calculations and for computing random uniform distribution.  
The`ssj.jar` archive (version 3.2.0) is included in the COMPONENT-GRAPHER distribution and it must be in the CLASSPATH environment variable.

##### [Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/)
The Apache common mathematics library is used for distribution and p-value calculations.
The `commons-math3-3.6.1.jar` archive is included in the COMPONENT-GRAPHER distribution and it must be in the CLASSPATH environment variable.  

##### [Google gson](https://github.com/google/gson)
The Google gson library is used for serialization/deserialization of the datasets.  
The `gson-2.6.2.jar` archive is included in the COMPONENT-GRAPHER distribution and it must be in the CLASSPATH environment variable. 
