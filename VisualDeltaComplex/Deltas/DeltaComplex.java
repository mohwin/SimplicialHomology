package Deltas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.io.Serializable;


public class DeltaComplex<V extends Comparable<V>> implements Serializable {
    public static final String _version = "1";
    
    
    // Erster Index steht für den Simplex Typ, Zweiter für den Eig. Simplex
    private ArrayList<HashSet<VisualSimplex<V>>> Komplex = new ArrayList<>();

    int eqClassCounter = 1;
    private HashMap<VisualSimplex<V>, Integer> EquGetter = new HashMap<>();



    /**
     * 
     * @param foo eqClass wird vom Komplex geregelt
     * @return true, falls Simplex hinzugefügt wurde, false, falls er bereits enthalten war
     */
    private boolean addSimplex_s(VisualSimplex<V> foo) {
        // Sicherstellen, dass genug Kapazität vorliegt.
        Komplex.ensureCapacity(foo.getLength());
        for (int i = Komplex.size() - 1; i < foo.getDimension(); i++) {
            Komplex.add(new HashSet<VisualSimplex<V>>());
        }
        // Hinzufügen
        boolean added = Komplex.get(foo.getDimension()).add(new VisualSimplex<>(foo, eqClassCounter));
        if (added) { 
            EquGetter.put(foo,eqClassCounter);
            eqClassCounter++;
        }
        return added;
    }

    /**
     * 
     * @param foo eqClass wird vom Komplex geregelt. Ordnung wird später vom Komplex geregelt.
     */
    private void addSimplex_c(VisualSimplex<V> foo) {
        if (addSimplex_s(foo) == false)
            return;
        if (foo.getLength() == 1)
            return;
        for (int i = 0; i < foo.getLength(); i++) {
            VisualSimplex<V> sub = foo.subsequence(i,-1); //eqClass interesiert nicht,wird im nächsten Aufrufvon .._s geregelt.
            addSimplex_c(sub); // Rekursionstiefe ist maximal Länge des Simplizes foo beim Erstaufruf
        }
    }


    /**
     * 
     * @param ofType dimension der Simplizes
     * @return Set aller Simplizes des Typs ofType. Sets sind evtl. nicht gebunden an tatsächlichen interner Struktur.
     */
    public HashSet<VisualSimplex<V>> getSimplizes(int ofType) {
        if (ofType >= Komplex.size())
            return new HashSet<>();
        return Komplex.get(ofType);
    }


    /**
     * Gibt die Dimesion des Komplexes zurück. Die Dimension ist der Typ des
     * längsten Simplex im Komplex.
     * Gibt -1 zurück, falls der Komplex leer war.
     * 
     * @return int, Dimension des Komplexes.
     */
    public int dimension() {
        for (int i = 1; i <= Komplex.size(); i++) {
            if (!(Komplex.get(Komplex.size() - i).isEmpty()))
                return Komplex.size() - i;
        }
        return -1;
    }



    /**
     * Gibt zurück, wie viele verschiedene Äquivalenzklassen es vom Typ ofType im Komplex gibt.
     * z.B. Ist numberOfEqClasses(0) die Anzahl der verschiedenen Vertizes zum aktuellen Zeitpunkt, Verklebungen sind
     * dabei berücksichtigt.
     * @param ofType in 0,...,this.dimension()
     * @return die Anzahl der Äklassen. -1 falls Eingabe ausserhalb der Grenzen war.
     */
    private int numberOfEqClasses(int ofType) {
        if (this.dimension() > ofType || ofType < 0) return -1;
        return (int)this.Komplex.get(ofType).stream().map(ds->ds.eqClass).distinct().count();
    }

    private List<VisualSimplex<V>> uniqueCOl(int which) {
        ArrayList<VisualSimplex<V>> L = new ArrayList<>(getSimplizes(which).size());
        HashSet<Integer> alrToken = new HashSet<>();
        for (VisualSimplex<V> cc:getSimplizes(which)) {
            if (alrToken.contains((Integer) cc.eqClass ) ) continue;
            alrToken.add(cc.eqClass);
            L.add(cc);
        }
        return L;
    }



    /**
     * Erwartet vollständigen Komplex. Gibt die Anzahl der Zeilen der RandMatrix des
     * Typs wich aus, ohne die Randmatrix zu berechnen.
     * 
     * @param which in 0,...,dimension()
     * @return Anzahl der Zeilen der Randmatrix, -1 bei which> dimension
     */
    private  int nZOfBoundaryMap(int which) {
        if (which == 0) { // Ausgezeichnete NullAbb. folgener Form:
            return 1;
        } else if (which > dimension()) {
            System.out.println(
                    "Warning: Komplex's Dimension is to small to have reasonable boundary Map of Type: " + which);
            return -1;
        }

        return(int) getSimplizes(which-1).stream().mapToInt(ds -> ds.eqClass).distinct().count();
        
    }

    /**
     * Erwartet vollständigen Komplex. Gibt die Anzahl der Saplten der RandMatrix
     * des Typs wich aus, ohne die Randmatrix zu berechnen.
     * 
     * @param which in 0,...,dimension()
     * @return Anzahl der Spalten der Randmatrix, -1 bei which> dimension
     */
    private  int nSOfBoundaryMap(int which) {

        if (which == 0) { // Ausgezeichnete NullAbb. folgener Form:
            return numberOfEqClasses(0);
        } else if (which > dimension()) {
            System.out.println(
                    "Warning: Komplex's Dimension is to small to have reasonable boundary Map of Type: " + which);
            return -1;
        }

        return numberOfEqClasses(which);
    }




    //----------------------------------------------------------------


    private HashMap<V,SortedSet<V>> implicitRelations = new HashMap<>();
    private Comparator<V> currentOrdering = null;


    public Comparator<V> getCurrentOrdering() {
        return currentOrdering;
    }

    private void reorderEach() {
        Komplex.forEach((simples) -> simples.forEach(vs -> vs.changeComparator(currentOrdering)));
    }

    /**
     * 
     * @return alle Vertizes im aktuellen Komplex. Sortiert mit dem übergebenen Comparator
     */
    public SortedSet<V> getVertices(Comparator<V> c) {
        TreeSet<V> vertices = new TreeSet<>(c);
        if (Komplex.isEmpty()) return vertices;
        for (VisualSimplex<V> vs :Komplex.get(0)) {
            vertices.add(vs.getIfSingleton());
        }
        return vertices;
    }

    /*
     * Kopiert das Set mittels einer Shallow Copy. Erlaubt so das (shallow) Manipulieren an returnted set, ohne
     * das vorherige zu verändern.
     */
    private SortedSet<V> setCopyOf(SortedSet<V> set) {
        TreeSet<V> ret = new TreeSet<>();
        ret.addAll(set);
        return ret;
    }

    private TreeSet<V> glue_unsafe(Collection<V> s1, Collection<V> s2) {
        HashMap<V,SortedSet<V>> implRelClone = new HashMap<>();

        for (Map.Entry<V,SortedSet<V>> e : implicitRelations.entrySet()) 
            implRelClone.put(e.getKey(), setCopyOf(e.getValue()));

        // Ordnung implizieren
        SortedSet<V> lowerSet = new TreeSet<>();
        for (V v: s1) {
            SortedSet<V> lowerSetClone = new TreeSet<>();
            lowerSetClone.addAll(lowerSet);
            implRelClone.merge(v,lowerSetClone,(a,b) -> {
                TreeSet<V> n = new TreeSet<>();
                n.addAll(a); n.addAll(b);
                return n;
            });
            lowerSet.add(v);
        }
        lowerSet.clear();
        // Ordnungen von s2 implizieren
        for (V v: s2) { 
            SortedSet<V> lowerSetClone = new TreeSet<>();
            lowerSetClone.addAll(lowerSet);
            implRelClone.merge(v,lowerSetClone,(a,b) -> {
                TreeSet<V> n = new TreeSet<>();
                n.addAll(a); n.addAll(b);
                return n;
            });
            lowerSet.add(v);
        }
        lowerSet.clear();

        // Nun VertizeMenge Ordnen?
        Sortierer<V> sorter = new Sortierer<>(getVertices(null), implRelClone);
        Comparator<V> oldComp = currentOrdering; // Sicherstellen des vorherigen Comparators
        currentOrdering = sorter.getComparator();
        if (currentOrdering == null) { // Zyklus
            currentOrdering = oldComp; // Wiederherstellen des alten Komparators
            return sorter.getZyklus();
        }
        // aktualisieren der implRel
        this.implicitRelations = implRelClone;
        reorderEach();

        glueRec(new VisualSimplex<>(s1, currentOrdering), new VisualSimplex<>(s2, currentOrdering));

        return sorter.getZyklus();
    }

    /**
     * Reordnet den Komplex. Ändert nicht die Ordnung, falls die momentane implizierten Relationen
     * einen Zyklus induzieren.
     * @return Zyklus Instanz des Sortierers
     */
    private SortedSet<V> reorderComplex() {
        Sortierer<V> sorter = new Sortierer<>(getVertices(null), implicitRelations);
        Comparator<V> oldComp = currentOrdering; // Sicherstellen des vorherigen Comparators
        currentOrdering = sorter.getComparator();
        if (currentOrdering == null) { // Zyklus
            currentOrdering = oldComp; // Wiederherstellen des alten Komparators
            return sorter.getZyklus();
        }
        reorderEach();
        return sorter.getZyklus();
    }


    private void glueRec(VisualSimplex<V> s1, VisualSimplex<V> s2) {
         
        //Rek Abbruch
        if (s1.isEmpty()) return;

        VisualSimplex<V> ss1 = new VisualSimplex<>(s1);
        VisualSimplex<V> ss2 = new VisualSimplex<>(s2);

        ss1.eqClass =  EquGetter.get(ss1);
        ss2.eqClass =  EquGetter.get(ss2);
        
        int max = Integer.max(ss1.eqClass,ss2.eqClass);

        getSimplizes(ss1.getDimension()).forEach(ds -> {
            if (ds.eqClass == ss1.eqClass || ds.eqClass == ss2.eqClass) {
                ds.eqClass = max;
                EquGetter.put(ds, max);
            }
        });

        for (int i=0;i<s1.getLength();i++) {
            glueRec(s1.subsequence(i, 0),s2.subsequence(i, 0)); // impl. eq Klassen sind wieder egal
        }
    }



    /*
     * Berechnet die  Randmatrix über den freien Moduln RecordHIB F. Diese Werte werden nicht gecacht!
     */
    public <F extends RecordHIB<F> >LowDensitiyMatrix<F> boundaryMap(int which,F nullobj) {

        if (which < 0) return null;

        if (which == 0) { // Ausgezeichnete NullAbb. folgener Form:
            return new LowDensitiyMatrix<F>(1, numberOfEqClasses(0), nullobj);
        } else if (which > dimension()) {
            System.out.println(
                    "Warning: Komplex's Dimension is to small to have reasonable boundary Map of Type: " + which);
            return null;
        }


        // Die Zeilen sind die Äklassen
        int[] rowIndiziierung = getSimplizes(which-1).stream().mapToInt(ds -> ds.eqClass).distinct().toArray();

        // Die Spalten sind Vertreter aus Äklassen
        List<VisualSimplex<V>> colIndiziierung = uniqueCOl(which);
        

        int nZ = rowIndiziierung.length;
        int nS = colIndiziierung.size();

        LowDensitiyMatrix<F> M = new LowDensitiyMatrix<F>(nZ, nS,nullobj);

        for (int k = 0; k < nS; k++) {
            VisualSimplex<V> a = colIndiziierung.get(k); // k-te SpaltenIndiziierung

            for (int i = 0; i < which + 1; i++) { // which+1 ist die Länge eines Spalten-Index Simplex.
                VisualSimplex<V> b = a.subsequence(i,-1); // Nun eine ZeilenIndizzierung
                b.eqClass = EquGetter.get(b);

                F matrixEintrag = (i % 2 == 0) ? nullobj.EinsWert() : nullobj.EinsWert().addiditiveInvers();

                for (int j = 0; j < nZ; j++) { // Durch die Zeilen-Ind. Iterieren
                    if (b.eqClass == rowIndiziierung[j]) { // Falls b mit Zeileindex übereinstimmt
                        F oldval = M.getEntry(j,k);
                        M.setEntry(j, k, oldval.plus(matrixEintrag));
                        break;
                    }

                }

            }
        }

        return M;
    }


    /**
     * Berechnet die which-te Homologie Gruppe des Komplexes über den freien F- Moduln.
     * 
     * @param which in N_0
     * @return GroupZ, der die Homologie Gruppe beschreibt. Triviale Gruppe falls which < 0.
     */
    public <F extends RecordHIB<F> > Group<F> HomologyGroup(int which, F nullobj) {
        int d = this.dimension();
        Group<F> HomGroup = new Group<F>(0,nullobj);

        if (which < 0)
            return HomGroup;
        else if (which > d)
            return HomGroup;

        if (which == d) { // --> im(C_which+1) = trivial, es verbleibt h_which = ker(c_which)
            List<F> L = boundaryMap(d, nullobj).smithDiagonal();
            int freepart = nSOfBoundaryMap(d) - L.size();
            HomGroup.freepart = freepart;
            return HomGroup;
        }
        List<F> DiagA = boundaryMap(which+1, nullobj).smithDiagonal();
        List<F> DiagB = boundaryMap(which, nullobj).smithDiagonal();

        int freepart = nZOfBoundaryMap(which + 1) - DiagA.size() - DiagB.size();

        HomGroup.freepart = freepart;
        HomGroup.addElementalDiv(DiagA);
        return HomGroup;
    }

    /**
     * 
     * @param vs oSimplex, ignoriert eqClass
     * @return true, iff vs ein RootSimplex ist.
     */
    public boolean isRoot(VisualSimplex<V> simpl) {
        return !isSubsequenceOfASuperSimplex(simpl);
    }

    private boolean isSubsequenceOfASuperSimplex(VisualSimplex<V> simpl) {
        HashSet<VisualSimplex<V>> hvs = this.getSimplizes(simpl.getDimension()+1); // potenzielle Supersimplizes
        if (hvs == null || hvs.isEmpty())  // definitiv kein superSimplex vorhanden
            return false;

        return hvs.stream().anyMatch(vs -> vs.isSuperSimplexOf(simpl) && !simpl.equals(vs));   
    }

    //---------------------------------------- Erw. Öffentliche Schnittstelle--------------------

    private String ListToString(List<VisualSimplex<V>> li) {
        StringBuilder ret = new StringBuilder();
        for (VisualSimplex<V> vs:li) {
            ret.append(vs.toString());
        }
        return ret.toString();
    }


    /**
     * @return Gibt alle 1-Simplizes zurück, dessen Ordnungen durch Verklebungsanweisungen impliziert worden sind.
     * leere Liste, falls keine solchen vorhanden sind.
     */
    public List<VisualSimplex<V>> getImplicitDirectedEdges() {
        
        Map<Integer,List<VisualSimplex<V>>> groupedByeqClass = getSimplizes(1).stream()
            .collect(Collectors.groupingBy(vs -> vs.eqClass));

        groupedByeqClass.values().removeIf(vl -> vl.size()<= 1);

        return groupedByeqClass.values().stream()
            .flatMap(svs -> svs.stream())
            .collect(Collectors.toList());
    }

    // strictly use natural Comparator
    private SortedSet<V> shallowCopy(SortedSet<V> of) {
        TreeSet<V> ret = new TreeSet<>();
        ret.addAll(of);
        return ret;
    }

    /**
     * 
     * @return deep-Copy (bis auf V) des aktuellen Complex. Zukünftige Änderungen werden nicht im 
     * memeto gespeichert.
     */
    public DeltaComplex<V> getMemento() {
        DeltaComplex<V> memento = new DeltaComplex<>();
        
        memento.currentOrdering = this.currentOrdering;
        memento.eqClassCounter = this.eqClassCounter;
        
        for (Map.Entry<VisualSimplex<V>,Integer> e: this.EquGetter.entrySet()) {
            memento.EquGetter.put(e.getKey().shallowCopy(), e.getValue()); // Later mybe deepCopy of Key? 
        }

        for (Map.Entry<V,SortedSet<V>> e: this.implicitRelations.entrySet()) {
            memento.implicitRelations.put(e.getKey(), shallowCopy(e.getValue())); // Later mybe deepCopy of Key?
        }

        for (HashSet<VisualSimplex<V>> sofvs : this.Komplex) {
            HashSet<VisualSimplex<V>> nsvs = new HashSet<>();
            nsvs.addAll(sofvs.stream()
                .map(vs -> vs.shallowCopy() ) // Later mybe deepCopy of Key?
                .collect(Collectors.toList()) );

            memento.Komplex.add(nsvs);
        }

        return memento;
    }

    public List<String> stringListRepr() {
        List<String> ret = new ArrayList<>();
        // Ordnung hinzufügen:
        StringBuilder ordnung = new StringBuilder();
        for (V v :getVertices(getCurrentOrdering())) {
            ordnung.append(v.toString()).append(" < ");
        }
        int end = ordnung.length() - 3;
        if (end > 0)
            ret.add(ordnung.substring(0, ordnung.length() - 3));
        // Sonst gab es keine Vertices

        for (int i = this.dimension();i>=0;i--) {
            ret.addAll(getSimplizes(i).stream()
                .filter(vs -> isRoot(vs))
                .map(vs -> vs.toString())
                .collect(Collectors.toList()));
            
        }

        //Verklebungen hinzunehmen
        for(int i=this.dimension();i>=0;i--) {
            Map<Integer,List<VisualSimplex<V>>> groupedByeqClass = getSimplizes(i).stream()
                .collect(Collectors.groupingBy(vs -> vs.eqClass));
            
            groupedByeqClass.values().removeIf(vl -> vl.size()<= 1);
            SortedSet<Integer> keysSorted = new TreeSet<>();
            keysSorted.addAll(groupedByeqClass.keySet());

            for (Integer k: keysSorted) {
                ret.add("="+ListToString(groupedByeqClass.get(k)));
            }
        }

        return ret;
    }

    /**
     * Nimmt an, das eqClass in hs vertreten ist.
     * @return true, falls eqClass mehr als einmal vorkommt.
     */
    private boolean containsEqClassMoreThanOnce(int eqClass,HashSet<VisualSimplex<V>> hs) {
        return (1 < hs.stream().filter(vs -> vs.eqClass == eqClass).count());
    }

    /**
     * @return Map, dessen Schlüssel alle in nichttrivialen ÄKlassen des Komplex liegen, die Werte sind
     * die entsprechenden Äklassen.
     */
    public Map<VisualSimplex<V>,Integer> getNonTrivialMappingToEqClass() {

        HashMap<VisualSimplex<V>,Integer> ret = new HashMap<>();

        for (HashSet<VisualSimplex<V>> hs: Komplex) {
            hs.stream()
                .filter(vs -> containsEqClassMoreThanOnce(vs.eqClass, hs))
                .forEach(vs -> ret.put(vs,vs.eqClass));
        }

        return ret;
    }


    public void clear() {
        this.EquGetter.clear();
        this.Komplex.clear();
        this.currentOrdering = null;
        this.eqClassCounter = 0;
        this.implicitRelations.clear();
        
    }


    /**
     * Prüft, ob der Komplex diesen oSimplex beinhaltet. (Ordnung wird hier beachtet)
     * @param simpl oSimplex (eqClass wird ignoriert)
     * @return true, falls der oSimplex enthalten war, false sonst.
     */
    public boolean contains(VisualSimplex<V> simpl) {
        if (Komplex.size() <simpl.getLength()) return false;
        return Komplex.get(simpl.getDimension()).contains(simpl);
    }
   
    /**
     * Prüft, ob der Komplex einen Simplex mit exakt den Werten aus der Kollektion beinhaltet.(Ordnung wird nicht beachtet)
     * @param simpl Simplex 
     * @return true, falls der Simplex enthalten war, false sonst.
     */
    public boolean contains(Collection<V> simpl) {
        if (simpl.isEmpty()) return false;
        if (Komplex.size() <simpl.size()) return false;
        return Komplex.get(simpl.size()-1).contains(new VisualSimplex<>(simpl, currentOrdering));
    }

    public VisualSimplex<V> transformColIntoVisualSimpl(Collection<V> simpl) {
        return new VisualSimplex<>(simpl, currentOrdering);
    }

    public VisualSimplex<V> transformSingIntoVisualSimpl(V vertex) {
        ArrayList<V> l = new ArrayList<>(1);
        l.add(vertex);
        return new VisualSimplex<>(l, currentOrdering);
    }
    
   

    /**
     * Verklebt die oSimplizes s1 und s2. Die Simplizes müssen im Komplex vorhanden und natürlich gleich lang sein.
     * Die Ordnung ist dabei durch den Iterator der Kollektion gegeben.
     * @param s1 oSimplex
     * @param s2 oSimplex
     * @return Zyklus, falls einer vorhanden.
     */
    public TreeSet<V> glue(Collection<V> s1, Collection<V> s2) {

        if (s1 == null || s2 == null)
            throw new NullPointerException("'null' ist kein gültiger oSimplex");

        if (s1.size() != s2.size())
            throw new IllegalArgumentException("Übergebene Simplizes waren nicht gleich lang.");

        if (!this.contains(s1) || !this.contains(s2))
            throw new NoSuchElementException("Ein übergebener Simplex ist nicht in diesen Komplex vorhanden");
        

        TreeSet<V> zyklus_unsorted = glue_unsafe(s1, s2);
        TreeSet<V> zyklus = new TreeSet<>(this.getCurrentOrdering());
        zyklus.addAll(zyklus_unsorted);

        return zyklus;
    }


    /**
     * Fügt den übergebenden oSimplex vollständig in den Komplex ein, d.h. Fügt man zb (1,2,3) ein wird auch automatisch
     * (1,2),(1,3),(2,3),(1),(2),(3) eingefügt, falls diese nicht bereits vorhanden waren.
     * @param Args oSimplex
     */
    public void addSimplex(Collection<V> simpl) {
        this.addSimplex_c(new VisualSimplex<>(simpl, currentOrdering));
    }

    /**
     * Fügt den übergebenden Vertex als 0-Simplex vollständig in den Komplex ein.
     * @param vertex Vertex
     */
    public void addVertex(V vertex) {
        List<V> simpl = new ArrayList<>(1);
        simpl.add(vertex);
        this.addSimplex(simpl);
    }

    
  
    /**
     * Entfernt den Vertex der durch den Singelton beschrieben ist und effektiv alles wo dieser Vertex vorkommt.
     * Rekursive Verklebungen der anderen Elemente bleiben erhalten.
     * @param singleton
     */
    public void removeVertex(V singleton) {
        
        Komplex.forEach(lvs -> 
            lvs.removeIf(vs ->vs.contains(singleton)) );

        this.EquGetter.keySet().removeIf(vs -> vs.contains(singleton));

        this.implicitRelations.remove(singleton);
        this.implicitRelations.values().forEach(setOfvs -> setOfvs.remove(singleton));

        this.reorderComplex();
    }






    //------------------------------------------------------------------------------------


    

}
