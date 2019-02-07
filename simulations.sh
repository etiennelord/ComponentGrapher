#Article simulation.
java -jar COMPONENT-GRAPHER.jar datasets/matrix_rhinos.nex -triplets -tree=datasets=sample/tree_rhinos.newick -seed=1000 -maxpool=8 -k=2 -output=phylo_rhino_k2 -perm=5000