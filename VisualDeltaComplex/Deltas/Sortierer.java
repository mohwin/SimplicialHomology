package Deltas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Die Aufgabe dieser Klasse ist es, auf einer Vertex-Menge mit einer natürlichen Ordnung sowie implizierten Relationen
 * (die der Ordnung widersprechen können) eine neue Ordnung zu bauen, welche ALLE implizierten Relationen erfüllt und die 
 * natürliche Ordnung dabei so wenig wie möglich verletzt.
 */
public class Sortierer<T extends Comparable<T>> implements Serializable{
    
    
    private LinkedList<T> vertices;
    private HashMap<T,SortedSet<T>> implicitRelations;
    private List<T> verticesInNewOrder;
    private boolean used = false; // Sagt aus, ob die Instanz bereits abgearbeitet wurde.

    private TreeSet<T> zyklus = new TreeSet<T>();
    
    /**
     * Gibt einen Zyklus zurück, falls gefunden. Gibt null zurück, falls der Sortierer noch unverbraucht ist.
     * @return
     */
    public TreeSet<T> getZyklus() {
        if (!used) return null;
        return zyklus;
    }




    /**
     * Erstellt eine Sortierer-Instanz. 
     * @param vertices (const) die Vertex Menge in der natürlichen Ordnung.
     * @param implicitRelations (const) eine Map der implizierten Relationen. Dabei zeigt ein Element a auf eine in der nat. Ordnung
     * geordnete Menge von Werten (Dies dient nur der Performance; eine Kollektion ist sinngemäß auch korrekt),
     * die alle kleiner sind als a. Bsp: Wolle man a<b, a<c, b<c implizieren, so ist die Map gegeben durch:
     * b -> (a), c -> (a,b).
     */
    public Sortierer(SortedSet<T> vertices, HashMap<T,SortedSet<T>> implicitRelation) {
        this.vertices = new LinkedList<>();
        this.implicitRelations = new HashMap<>();
        this.verticesInNewOrder = new ArrayList<>(vertices.size());

        this.vertices.addAll(vertices);
        for (Map.Entry<T,SortedSet<T>> e : implicitRelation.entrySet()) 
            this.implicitRelations.put(e.getKey(), setCopyOf(e.getValue()));
        
    }

    /*
     * Kopiert das Set mittels einer Shallow Copy. Erlaubt so das (shallow) Manipulieren an returnted set, ohne
     * das vorherige zu verändern.
     */
    private SortedSet<T> setCopyOf(SortedSet<T> set) {
        TreeSet<T> ret = new TreeSet<>();
        ret.addAll(set);
        return ret;
    }

    /**
     * Call with empty alreadyVisitedSet only.
     * @param beginValue Anfangswert, von dem man ausgeht, er sei der kleinste
     * @param alreadyVisited leeres Set
     * @return kleinster Vertex der alle Anforderungen erfüllt. 
     * null, falls ein Zyklus gefunden wurde. Dann ist im alreadyVisited dieser Zyklus drin.
     */
    private T getSmallestRecursiv(T beginValue,SortedSet<T> alreadyVisited) {
        SortedSet<T> implicitSmallerVertices = implicitRelations.get(beginValue);

        if (implicitSmallerVertices == null || implicitSmallerVertices.isEmpty()) {
            // Es gibt keinen implizit kleineren Vertex als beginValue
            return beginValue;
        }

        // Sonst prüfe den ersten kleineren Wert. Wurde dieser schonmal besucht, so ist man im Kreis gelaufen.
        T smallerVert = implicitSmallerVertices.first();
        if (alreadyVisited.contains(smallerVert)) {
            alreadyVisited.add(beginValue);
            return null;
        }
        // Sonst füge den beginValue hinzu und nutze die Rekursion.
        alreadyVisited.add(beginValue);
        return getSmallestRecursiv(smallerVert, alreadyVisited);
    }

    /**
     * Schließt durchs abarbeiten der Instanzvariablen auf das kleinste Element der neuen Ordnung.
     * Dabei werden die Instanzvariablen geändert. Sei Bspweise a das kleinste Element der neuen Ordnung.
     * Dann wird a zurückgegeben, a aus dem vertice-Set entfernt, Maps wie a -> iwas entfernt und alle a's aus der "rechten
     * Seite" der Map entfernt (iwas -> (a,b) wird zu iwas -> (b))
     * @return null, falls Zyklus gefunden wurde. Dieser liegt dann in der Variable "zyklus".
     */
    private T getSmallestElement() {
        //T naturalSmallest = vertices.poll(); // Was This the Bug reason? 
        T naturalSmallest = vertices.peekFirst();

        if (naturalSmallest == null) {
            // Set war leer
            throw new IllegalStateException("Kann kein kleinstes Element aus leerer Liste ziehen.");
        }
        // Prüfe ob es nach Implikation einen kleineren Wert gibt:
        T smallestInNewOrder = getSmallestRecursiv(naturalSmallest, zyklus);

        if (smallestInNewOrder == null) {
            // Zyklus gefunden.
            return null;
        }

        // Sonst alles ok. Passe nun die Parameter/Instanzvar. an.
        zyklus.clear();
        vertices.remove(smallestInNewOrder); // smallestInNewOrder kann nicht key in implicitRelations sein!
        implicitRelations.values().forEach(ss -> ss.remove(smallestInNewOrder));
        

        return smallestInNewOrder;
    }


    /**
     * Formatiert die Instanzvariable verticesInNewOrder, sodass der Iterator der Liste verticesInNewOrder
     * die Vertices in der neuen Ordnung aufsteigend zurückgibt.
     * @return true, falls alles erfolgreich. false, falls ein Zyklus entdeckt wurde.
     */
    private boolean buildVerticesInNewOrderList() {
        used = true;
        while(!vertices.isEmpty()) {
            T smallest = getSmallestElement();
            if (smallest == null) {
                //Zyklus
                verticesInNewOrder.clear();
                return false;
            }
            verticesInNewOrder.add(smallest);
        }

        return true;
    }


    /**
     * Gibt den Komparator der neuen Ordnung zurück. Falls null zurückgegeben wurde, so liegt ein Zyklus vor;
     * mit der Methode getZyklus() kann dieser eingesehen werden.
     * @return Komparator der neuen Ordnung. null, falls keine Ordung möglich war. 
     */
    public Comparator<T> getComparator() {
        if (used) {
            throw new IllegalStateException("Sortierer-Instance was already used.");
        }
        boolean cyclefree = buildVerticesInNewOrderList();
       

        this.implicitRelations.clear();
        this.vertices.clear();
        
        if (cyclefree) {
            // Rückgabe eines Komparators basierend auf der sortiertet Liste
            /*return (a,b) -> {
                if (a.equals(b)) return 0;
                if (!verticesInNewOrder.contains(a) || !verticesInNewOrder.contains(b)) {
                    return a.compareTo(b); //  Nutzt nat. Ordnung bei unknown Vertices
                }
                for (T v:verticesInNewOrder) {
                    if (v.equals(a)) return -1; // a ist kleiner als b
                    else if (v.equals(b)) return 1;
                }
                throw new IllegalStateException("this should never be thrown, something went very very wrong.");
            };*/ // Dies war vor der Serialize- Geschichte

            return new CopS() {

                @Override
                public int compare(T a, T b) {
                    if (a.equals(b)) return 0;
                if (!verticesInNewOrder.contains(a) || !verticesInNewOrder.contains(b)) {
                    return a.compareTo(b); //  Nutzt nat. Ordnung bei unknown Vertices
                }
                for (T v:verticesInNewOrder) {
                    if (v.equals(a)) return -1; // a ist kleiner als b
                    else if (v.equals(b)) return 1;
                }
                throw new IllegalStateException("this should never be thrown, something went very very wrong.");
                }

            };

        }


        return null;
    }

    
    private abstract class CopS implements Comparator<T>,Serializable {

    }


}
