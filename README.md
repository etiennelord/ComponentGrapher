# COMPOSITEGRAPHER

## About

This is the source code and some sample matrices for the COMPOSITEGRAPHER software.

The COMPOSITERAPHER software generates networks containing: identical, inclusion, overlap and disjoint links between character-states.

## Usage

COMPOSITERAPHER is a graphical tool (GUI) programmed in Java. 

1. First obtain a [binary release](#binary-releases) or [compile](#Compilation) your own version.
2. Extract the zipped archive into a new directory
3. Run the COMPOSITEGRAPHER software:

A) Double-click on the COMPOSITEGRAPHER.jar from the main directory

B) Alternatively, you can start the software from the command-line with some minimum and maximum memory requirements.  

```
java -Xms=2g -Xmx=4g -jar COMPOSITEGRAPHER.jar
```
C) To perform several permuation (higher than 1000) for the estimation of the _p_-value, it is recommended to run the analysis from the command-line. 

For example for the matrix sample_5.txt located in the _sample_ directory  
With the **2000** permutations (-perm=1000)  and **100** concurrent threads (-maxpool=100)
```
java -jar COMPOSITEGRAPHER.jar sample/sample_5.txt -perm=2000 -maxpool=100
```
Full command-line options are:   



|             Options   | Usages                                         |
| -------------------- | ---------------------------------------------- |
|	-perm=100      | Specify the number permutation to performed.  |
|	-maxpool=10    | Specify the number of concurrent threads.|
|	-undefined     | Remove column containing undefined states (e.g. ?,-,\*)|
|	-multiple      | Remove column containing polymorphic states (e.g. {1,2,3}).|
|	-bipartite     | Output bipartite network files.|
|	-graphml       | Output graphml network files (Gephi, Cytoscape compatibles).|
|	-nodeid=file   | Provide a node identification file when importing the matrix.|
|	-output=file   | Specify the output filename.|
|	-variation=X   | Specify the variation string to use if polymorphic states are presents.|
|	-summary       | Output summary statistic such as degrees, betweenness.|


### Requirements

COMPOSITEGRAPHER is compatible with Java SE5 (JDK 5) and later versions of Java. The latest
Java platform is available at
[Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

## Compilation

The source code is distributed as Netbeans project format. 

## Dependencies

COMPOSITEGRAPHER depends on the following library:

##### [SSJ](https://github.com/umontreal-simul/ssj)  
The SSJ library is used for BitVector calculations and for computing random uniform distribution.  
The`ssj.jar` archive (version 3.2.0) is included in the COMPOSITEGRAPHER distribution and it must be in the CLASSPATH environment variable.

##### [Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/)
The Apache common mathematics library is used for distribution and p-value calculations.
The `commons-math3-3.6.1.jar` archive is included in the COMPOSITEGRAPHER distribution and it must be in the CLASSPATH environment variable.  

##### [Google gson](https://github.com/google/gson)
The Google gson library is used for serialization/deserialization of the datasets.  
The `gson-2.6.2.jar` archive is included in the COMPOSITEGRAPHER distribution and it must be in the CLASSPATH environment variable. 


