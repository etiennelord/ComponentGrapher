package COMPONENT_GRAPHER;

/**
 * Helper for triplets
 * @author Etienne Lord
 */
public class triplets {
    
    public int n1=0;
    public int n2=0; //central
    public int n3=0;    
    
    @Override
    public boolean equals(Object o) {
        triplets t=(triplets)o;
        if (n1!=t.n1&&n1!=t.n2&&n1!=t.n3) return false; //--n1 != a node in t
        if (n2!=t.n1&&n2!=t.n2&&n2!=t.n3) return false; //--n2 != a node in t
        if (n3!=t.n1&&n3!=t.n2&&n3!=t.n3) return false; //--n3 != a node in t
       //--we matched each node
        return true;
    }
    public triplets() {}

    public triplets(int i, int j, int k) {
        n1=i;
        n2=j;
        n3=k;
    }
    
}
