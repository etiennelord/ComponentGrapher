# COMPOSITEGRAPHER

## About

This is the source code and some sample matrices for the COMPOSITEGRAPHER software.

The COMPOSITERAPHER software generates networks containing: perfect, inclusion, partial concomitant links between character-states.

## Usage

COMPOSITERAPHER is a command-line tool programmed in Java. 

1. First obtain a [binary release](#binary-releases) or [compile](#Compilation) your own version.
2. Typical command-line to analyse a morphological matrix are:

A) Create the networks and export as graphml files 

```
java -jar COMPOSITEGRAPHER.jar example/Sample.phy -graphml
```

B) Processing the Smith-Caron sample matrix and generate a summary of the results.

```
java -Xms=2g -Xmx=4g -jar COMPOSITEGRAPHER.jar example/Smith_Caron.nex -summary
```

### Binary releases

COMPOSITEGRAPHER is compatible with Java SE5 (JDK 5) and later versions of Java. The latest
Java platform is available at
[Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

## Compilation

The source code is distributed as Netbeans project format. 

## Dependencies

COMPOSITEGRAPHER depends on the following library:

##### [SSJ](https://github.com/umontreal-simul/ssj)  
The SSJ library is used for BitVector calculations and for computing random uniform distribution.  The
`ssj.jar` archive is included in the COMPOSITEGRAPHER distribution 
and it must be in the CLASSPATH environment variable.

