package Deltas;
import java.util.*;

/** 
 */
public class Simplex {
    private final ArrayList<Integer> simplex = new ArrayList<>();

    public static final String _version = "1";

    //Package private Innere Liste darf nicht geändert werden
    ArrayList<Integer> getInnerList() {
        return simplex;
    }

    /**Gibt den neu erstellten Simplex s(arg1,...,argN) zurück*/
    public static Simplex s(int... Args) {
        return new Simplex(Args);
    }

    //--------------------------------------------------------

    /**Baut einen Simplex mit dem Inhalt der Liste */
    public Simplex(List<Integer> foo) {
        for (int i: foo)
            if (!simplex.contains(i))
                simplex.add(i);
    }

    /**Baut einen Simplex mit dem Inhalt des übergebenen Arrays */
    public Simplex(int foo[]) {
        simplex.ensureCapacity(foo.length);
        for (int i:foo) 
            if (!simplex.contains(i))
                simplex.add(i);
    }

    /**Baut einen Simplex mit dem Inhalt des übergebenen Simplizes */
    public Simplex(Simplex foo) {
        this.simplex.addAll(foo.simplex);
    }
    
    /** Simplex s(1,2,...,type+1), type ist sog. Dimension des Simplizes */
    public Simplex(int type) {
        simplex.ensureCapacity(type+1);
        for (int i=1;i<=type+1;i++) simplex.add(i);
    }

    //--------------------------------------------------------

    // Privater konstruktor für folgende Funktion: subsequence
    private Simplex(Simplex simp, int withoutIndex) {
        this.simplex.addAll(simp.simplex);
        this.simplex.remove(withoutIndex);

    }

    /**Gibt einen neu Instanziierten Simplex zurück, wobei nur der Eintrag withoutIndex fehlt. */
    public Simplex subsequence(int withoutIndex) {
        return new Simplex(this, withoutIndex);
    }

    /**Gibt die Länge des Simplizes zurück */
    public int getLength() {
        return simplex.size();
    }

    /** Gibt den Typ des Simplizes zurück = Länge-1 */
    public int getType() {
        return simplex.size()-1;
    }

    /**Gibt den größten Eintrag zurück */
    public int maxVal() {
        int max = Integer.MIN_VALUE;
        for (Integer i: simplex) {
            if(i>max) max = i;
        }
        return max;
    }

    /**Gibt den kleinsten Eintrag zurück */
    public int minVal() {
        int min = Integer.MAX_VALUE;
        for (Integer i: simplex) {
            if(i<min) min = i;
        }
        return min;
    }

    /**
     * this const.
     * @return Gibt die Werte des Simplizes in ein Array und gibt dieses zurück. Das Array ist nicht mit dem Simplex
     * verknüpft und kann frei bearbeitet werden.
     */
    public int[] asArray() {
        int[] ret = new int[this.simplex.size()];

        int counter = 0;
        for (int i: this.simplex) {
            ret[counter] = i;
            counter += 1;
        }
        return ret;
    }

    /** True, falls der Simplex den Vertex beinhaltet*/
    public boolean containsVertex(Integer vertex) {
        return simplex.contains(vertex);
    }

    /** 
     * Verändert den Vertex of zu to, falls of im Simplex vorhanden ist. Package privat.
    */
    void changeVertex(int of, int to) {
        simplex.replaceAll( i -> {if (i.equals(of)) return to; else return i;});
        
        List<Integer> l = new ArrayList<>();
        l.addAll(this.simplex);
    }


    /**
     * Prüft ob this eine gerde Permutation von other ist.
     * @param other 
     * @return -1, falls keine gerade Permutation vorliegt, +1 falls eine gerade Perm. vorliegt.
     */
    int isEvenPermutationOf(Simplex other) {
        if (!this.equals(other))
            throw new IllegalArgumentException("Simplizes sind nicht gleich, Frage nach Permuation nicht auflösbar.");
        // Assume this equals other
        int ret = 1;
        int[] otCpy = other.asArray();

        for (int i=0;i<this.getLength();i++) {
            int currentVal = this.simplex.get(i);
            if (currentVal != otCpy[i]) {
                // Suche den aktuellen Wert in otCpy
                for (int j=i+1;j<otCpy.length;j++)
                    if (otCpy[j] == currentVal) {
                        // Swape i mit j in otCpy
                        int h=otCpy[i];
                        otCpy[i] = otCpy[j];
                        otCpy[j] = h;
                        break;
                    }

                ret = -ret ;
            }
        }
        return ret;
    }

    //--------------------------------------------------------

    // Für Prints
    @Override
    public String toString() {
        return simplex.toString();
    }




    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((simplex == null) ? 0 : hashCodeAsSet());
        return result;
    }

    private int hashCodeAsSet() {
        int result = 0;
        for (Integer f:simplex) result += f.hashCode();
        return result;
    }

    /**
     * 2 Simplices sind gleich, wenn sie Permutationen von einander sind.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Simplex other = (Simplex) obj;
        if (simplex == null) {
            if (other.simplex != null)
                return false;
        } else if (! (this.isPermutationOf(other))) // Prüfen auf PermutaionsGleichheit
            return false;
        return true;
    }


    /**
    * Prüft ob foo Permutation von this ist.
     * @param foo
    * @return true, falls foo Permutation von this, false sonst
    */
    public boolean isPermutationOf(Simplex foo) {
        if (foo.simplex.size() != this.simplex.size()) return false;

        for (Integer i: this.simplex) {
            if (! (foo.containsVertex(i)) ) return false;
        }
        
        return true;
    }


    public void increaseAllVertices(int value) {
        for (int i=0;i<simplex.size();i++) simplex.set(i, simplex.get(i)+value);
    }


}