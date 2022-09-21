package Deltas;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VisualSimplex<V extends Comparable<V>> implements Iterable<V>, Serializable {
    

    private SortedSet<V> simplex = new TreeSet<>();
    int eqClass; // Wird vom Toplevel gehandelt


    private VisualSimplex() {}



    public VisualSimplex(Collection<V> simpl) {
        simplex.addAll(simpl);
    }

    public VisualSimplex(Collection<V> simpl, Comparator<V> c) {
        simplex = new TreeSet<>(c);
        simplex.addAll(simpl);
    }


    public VisualSimplex(VisualSimplex<V> simpl, int neqClass) {
        this.simplex = new TreeSet<>(simpl.simplex.comparator());
        simplex.addAll(simpl.simplex);
        eqClass = neqClass;
    }

    public VisualSimplex(VisualSimplex<V> simpl) {
        this.simplex = new TreeSet<>(simpl.simplex.comparator());
        simplex.addAll(simpl.simplex);
    }


    /**
     * 
     * @return shallow Copy des aktuellen Simplex
     */
    public VisualSimplex<V> shallowCopy() {
        return new VisualSimplex<>(this,this.eqClass);
    }

    /**
     * Falls this ein Singleton ist (nur aus einem Element besteht), so wird dieses zurückgegeben. sonst wird null zurückgegeben
     * @return
     */
    public V getIfSingleton() {
        if (this.getLength() == 1)
            return simplex.first();
        return null;
    }

    public boolean contains(V vertex) {
        return this.simplex.contains(vertex);
    }

    public VisualSimplex<V> subsequence(int withoutIndex,int subsEqClass) {
        VisualSimplex<V> ret = new VisualSimplex<>();
        ret.eqClass = subsEqClass;
        int idx = -1;
        for (V v: this.simplex) {
            idx++;
            if (withoutIndex == idx) continue;
            else ret.simplex.add(v);
        }
        return ret;
    }

    public int getDimension() {
        return this.simplex.size() - 1;
    }

    public int getLength() {
        return this.simplex.size();
    }

    public boolean isEmpty() {
        return this.simplex.isEmpty();
    }

    /**
     * @param other (const)
     * @return true, falls this alle Vertices aus other beinhaltet. (Ungeachtet der verwendeten Comparatoren)
     */
    public boolean isSuperSimplexOf(VisualSimplex<V> other) {
        return this.simplex.containsAll(other.simplex);
    }

    /**
     * 
     * @param other (const)
     * @return true, falls this vollständig in other liegt. (Ungeachtet der verwendeten Comparatoren)
     */
    public boolean isSubsequenceOf(VisualSimplex<V> other) {
        return other.simplex.containsAll(this.simplex);
    }

    /**
     * Wechselt den zugrunde liegenden Comparator auf den übergebenen
     * @param c Comparator. bei c == null wird der natürliche Comparator verwendet.
     */
    public void changeComparator(Comparator<V> c) {
        TreeSet<V> nsimp = new TreeSet<>(c);
        nsimp.addAll(this.simplex);
        this.simplex.clear();
        this.simplex = nsimp;
    }

    /**
     * @return Gibt das erste (kleinste) Element des Simplex zurück (gemäß den aktuellen Comparator)
     */
    public V first() {
        return this.simplex.first();
    }

    /**
     * @return Gibt das letzte (größte) Element des Simplex zurück (gemäß den aktuellen Comparator)
     */
    public V last() {
        return this.simplex.last();
    }


    /**
     * Gibt einen Stream über die Elemente des Simplex zurück.
     * WICHTIG: alle Streamoperationen sollten den Simplex unberührt lassen; 
     * Änderungen an (für Comparator) relevanten Daten in V führen zu undefiniertem Verhalten.
     */
    public Stream<V> elementStream() {
        return simplex.stream();
    }

    /**
     * Gibt ein SortedSet über die Elemente des Simplex zurück.
     * WICHTIG: Das Set sollte unberührt bleiben! 
     * Änderungen am Set führen zu undefiniertem Verhalten.
     */
    public SortedSet<V> elementSet() {
        return simplex;
    }

    //--------------------------------------------

    private int pseudoHasher(Collection<V> col) {
        return (int)col.stream().collect(Collectors.summarizingInt(v -> v.hashCode())).getSum();
    }

    /**
     * Ignoriert eqClass Parameter. Beachetet NICHT den momentanten Comparator
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((simplex == null) ? 0 : pseudoHasher(simplex));
        return result;
    }

    /**
     * Prüft audf Gleichheit der zugrunde liegenden SortedSets(Und damit auch auf Comparatoren Gleichheit).
     * Ignoriert eqClass Parameter.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VisualSimplex<?> other = (VisualSimplex<?>) obj;
        if (simplex == null) {
            if (other.simplex != null)
                return false;
        } else if (!simplex.equals(other.simplex))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return simplex.toString();
    }

    @Override
    public Iterator<V> iterator() {
        return this.simplex.iterator();
    }

}
