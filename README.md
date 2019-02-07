# ComponentGrapher

## About

This is the source code and some sample matrices for the ComponentGrapher software.

The ComponentGrapher software generates networks containing: identical, inclusion, overlap or disjoint links between character states.

## Usage

ComponentGrapher is a graphical (GUI) tool programmed in Java. 

1. First obtain a [binary release](#binary-releases) or [compile](#Compilation) your own version using Java and the Netbeans platform.
2. Extract the zipped archive into a new directory
3. Run the COMPONENT-GRAPHER software:

A) Start the software from the command-line with some minimum and maximum memory requirements.  

```
java -Xms=2g -Xmx=4g -jar COMPONENT-GRAPHER.jar sample/matrix.txt
```

B) To perform several permuation (higher than 1000) for the estimation of the _p_-value, it is recommended to run the analysis from the command-line. 

For example for the matrix panarthropod_wo_absent_trait.nex located in the _datasets_ directory  
With the **2000** permutations (-perm=2000)  and **100** concurrent threads (-threads=100)
```
java -Xms=2g -Xmx=6g -jar COMPONENT-GRAPHER.jar datasets/panarthropod_wo_absent_trait.nex -perm=2000 -threads=100
```
For the second example, we can specify a phylogenetic tree for the permutation.  
```
java -Xms=2g -Xmx=6g -jar COMPONENT-GRAPHER.jar datasets/rhinocerotid_original.nex -tree=datasets/rhinocerotid_tree.newick -threads=20
```

Full command-line options are*:   
* Please refers to the manual for more informations

| Options          | Usages                                                                 |
| ---------------- | ---------------------------------------------------------------------- |
|	-tree=file     | Specify the phylogenetic tree if the permutation mode is phylogenetic. |
|	-perm=100      | Specify the number permutation to performed.                           |
|   -permmode=0    | Specify the permutation mode.                                          |                  
|             0    | Equiprobable permutation (default)                                     |
|	          1    | Probabilistic permutation                                              |
|	          2    | Phylogeny (default if -tree option is used)                            |
|	          3    | Equiprobable- only permute undefined states                            |
|	          4    | Probabilistic- only permute undefined states                           |
|       -edges=0   | Specify edge's inference mode                                          |
|	          0    | Treat all edges (default)                                              |
|	          1    | Absolute Majority                                                      |
|	          2    | Majority Rule                                                          |
|	        >10    | Minimum percent to have edge                                           |
|	-threads=10    | Specify the number of concurrent threads.                              |
|	-output=dir    | Specify output directory.                                              |
|	-undefined     | Remove column containing undefined states (e.g. ?,-,\*)                |
|	-multiple      | Remove column containing polymorphic states (e.g. {1,2,3}).            |
|	-bipartite     | Output bipartite network files.                                        |
|	-graphml       | Output graphml network files (Gephi, Cytoscape compatibles).           |
|	-nodeid=file   | Provide a node identification file when importing the matrix.          |
|	-triplets      | Output triplets file (triplets.txt)                                    |
|	-filter        | Remove nodes matching this state (e.g. absent) from the network        | 

Generated results files in the ouput directory are:   

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
|	matrixfile_summary_statistics.tsv     | nodes summary statistics                               |
|	matrixfile_network_statistics.tsv     | network statistics and _p_-value.                      |
|	matrixfile_nodes_statistics.tsv       | nodes statistics and _p_-value.                        |

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

ComponentGrapher is compatible with Java SE8 (JDK 8) and later versions of Java. The latest
Java platform is available at [Oracle](https://www.java.com/fr/download/).

## Compilation

The source code is distributed as [Netbeans](https://netbeans.apache.org/download/index.html) project format. 

## Dependencies

ComponentGrapher depends on the following libraries:

##### [SSJ](https://github.com/umontreal-simul/ssj)  
The SSJ library is used for BitVector calculations and for computing random uniform distribution.  
The`ssj.jar` archive (version 3.2.0) is included in the COMPONENT-GRAPHER distribution and it must be in the CLASSPATH environment variable.

##### [Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/)
The Apache common mathematics library is used for distribution and p-value calculations.
The `commons-math3-3.6.1.jar` archive is included in the COMPONENT-GRAPHER distribution and it must be in the CLASSPATH environment variable.  

##### [Google gson](https://github.com/google/gson)
The Google gson library is used for serialization/deserialization of the datasets.  
The `gson-2.6.2.jar` archive is included in the COMPONENT-GRAPHER distribution and it must be in the CLASSPATH environment variable. 

##### [Forester](https://sites.google.com/site/cmzmasek/home/software/forester)
Forester is a collection of open source libraries for phylogenomics and evolutionary biology research created by Christian Zmasek.
The `forester.jar` archive is included in the COMPONENT-GRAPHER distribution and it must be in the CLASSPATH environment variable. 