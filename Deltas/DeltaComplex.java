package Deltas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;


public class DeltaComplex {
    public static final String _version = "1";
    
    
    // Erster Index steht für den Simplex Typ, Zweiter für den Eig. Simplex
    private ArrayList<HashSet<DeltaSimplex>> Komplex = new ArrayList<>();

    int eqClassCounter = 1;
    private HashMap<DeltaSimplex, Integer> EquGetter = new HashMap<>();



    boolean addSimplex_s(Simplex foo) {
        // Sicherstellen, dass genug Kapazität vorliegt.
        Komplex.ensureCapacity(foo.getType() + 1);
        for (int i = Komplex.size() - 1; i < foo.getType(); i++) {
            Komplex.add(new HashSet<DeltaSimplex>());
        }
        // Hinzufügen
        smithNormalFormCacheIsValid = false;
        DeltaSimplex nEl = DeltaSimplex.of(foo,eqClassCounter);
        boolean added = Komplex.get(foo.getType()).add(nEl);
        if (added) { 
            EquGetter.put(nEl,eqClassCounter);
            eqClassCounter++;
        }
        return added;
    }

    
    void addSimplex_c(Simplex foo) {
        smithNormalFormCacheIsValid = false;
        if (addSimplex_s(foo) == false)
            return;
        if (foo.getLength() == 1)
            return;
        for (int i = 0; i < foo.getLength(); i++) {
            Simplex sub = foo.subsequence(i);
            addSimplex_c(sub); // Rekursionstiefe ist maximal Länge des Simplizes foo beim Erstaufruf
        }
    }


    HashSet<DeltaSimplex> getSimplizes(int ofType) {
        if (ofType >= Komplex.size())
            return null;
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
    int numberOfEqClasses(int ofType) {
        if (this.dimension() > ofType || ofType < 0) return -1;
        return (int)this.Komplex.get(ofType).stream().map(ds->ds.eqClass).distinct().count();
    }

    List<DeltaSimplex> uniqueCOl(int which) {
        ArrayList<DeltaSimplex> L = new ArrayList<>(getSimplizes(which).size());
        HashSet<Integer> alrToken = new HashSet<>();
        for (DeltaSimplex cc:getSimplizes(which)) {
            if (alrToken.contains((Integer) cc.eqClass ) ) continue;
            alrToken.add(cc.eqClass);
            L.add(cc);
        }
        return L;
    }


    @Override
    public String toString() {
        return Komplex.toString();
    }

    /**
     * Berechnet die Boundary Map des Typs which. Gibt null zurück, falls die Dimension des
     * Komplexes nicht ausreicht.
     * 
     * @param which in 0,...,dimension()
     * @return Die Boundary Map des Typs which, falls which klein genug, null falls
     *         which > dimension oder falls which < 0
     */
    public BoundaryMatrix boundaryMap(int which) {

        if (which < 0) return null;

        if (which == 0) { // Ausgezeichnete NullAbb. folgener Form:
            return new BoundaryMatrix(1, numberOfEqClasses(0));
        } else if (which > dimension()) {
            System.out.println(
                    "Warning: Komplex's Dimension is to small to have reasonable boundary Map of Type: " + which);
            return null;
        }


        // Die Zeilen sind die Äklassen
        int[] rowIndiziierung = getSimplizes(which-1).stream().mapToInt(ds -> ds.eqClass).distinct().toArray();

        // Die Spalten sind Vertreter aus Äklassen
        List<DeltaSimplex> colIndiziierung = uniqueCOl(which);
        

        int nZ = rowIndiziierung.length;
        int nS = colIndiziierung.size();

        BoundaryMatrix M = new BoundaryMatrix(nZ, nS);

        for (int k = 0; k < nS; k++) {
            DeltaSimplex a = colIndiziierung.get(k); // k-te SpaltenIndiziierung

            for (int i = 0; i < which + 1; i++) { // which+1 ist die Länge eines Spalten-Index Simplex.
                DeltaSimplex b = a.subsequence(i,-1); // Nun eine ZeilenIndizzierung
                b.eqClass = EquGetter.get(b);

                int matrixEintrag = (i % 2 == 0) ? 1 : -1;

                for (int j = 0; j < nZ; j++) { // Durch die Zeilen-Ind. Iterieren
                    if (b.eqClass == rowIndiziierung[j]) { // Falls b mit Zeileindex übereinstimmt
                        int oldval = M.getEntry(j,k);
                        M.setEntry(j, k, oldval +  matrixEintrag);
                        break;
                    }

                }

            }
        }

        return M;
    }

    /**
     * Erwartet vollständigen Komplex. Gibt die Anzahl der Zeilen der RandMatrix des
     * Typs wich aus, ohne die Randmatrix zu berechnen.
     * 
     * @param which in 0,...,dimension()
     * @return Anzahl der Zeilen der Randmatrix, -1 bei which> dimension
     */
    protected int nZOfBoundaryMap(int which) {
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
    protected int nSOfBoundaryMap(int which) {

        if (which == 0) { // Ausgezeichnete NullAbb. folgener Form:
            return numberOfEqClasses(0);
        } else if (which > dimension()) {
            System.out.println(
                    "Warning: Komplex's Dimension is to small to have reasonable boundary Map of Type: " + which);
            return -1;
        }

        return numberOfEqClasses(which);
    }

    // bool Ausdruck, der aussagt, ob der momentane Cache valide ist.
    private boolean smithNormalFormCacheIsValid = false;

    // Cache zur Speicherung von SmithNormalFormen der RandMatrizen des Typs which.
    // Speicherformat: smithNormalFormCache[i] = boundaryMap(i).smithDiagonal()
    private final HashMap<Integer, List<Integer>> smithNormalFormCache = new HashMap<>();

    // Gibt eine die SmithDiagonale der Rand Matrix which zurück. Lädt vom Cache,
    // falls dort vorhanden, und der Cache Valide ist.
    private List<Integer> smithDiagonalOfBoundaryMap(int which) {
        // Falls der Cache nicht valide ist, alle CacheEinträge entfernen.
        if (!smithNormalFormCacheIsValid) {
            smithNormalFormCache.clear();
            smithNormalFormCacheIsValid = true;
        }

        // Falls der Schlüssel vorhanden ist
        if (smithNormalFormCache.containsKey(which)) {
            return smithNormalFormCache.get(which);
        }
        // Sonst wird neu berechnet.
        else {
            BoundaryMatrix B;
            B = this.boundaryMap(which);
            if (B == null) {
                smithNormalFormCache.put(which, new ArrayList<>()); // Leere Liste falls keine Randmatrix existiert
            } else {
                smithNormalFormCache.put(which, B.smithDiagonal()); // Speicherung der SmithDiagonalen
            }
            return smithNormalFormCache.get(which);
        }

    }

    /**
     * Erwartet vollständigen Komplex. Berechnet die which-te Homologie Gruppe des
     * Komplexes.
     * 
     * @param which in N_0
     * @return GroupZ, der die Homologie Gruppe beschreibt. Triviale Gruppe falls which < 0.
     */
    public GroupZ HomologyGroup(int which) {
        int d = this.dimension();
        GroupZ HomGroup = new GroupZ(0);

        if (which < 0)
            return HomGroup;
        else if (which > d)
            return HomGroup;

        if (which == d) { // --> im(C_which+1) = trivial, es verbleibt h_which = ker(c_which)
            List<Integer> L = smithDiagonalOfBoundaryMap(d);
            int freepart = nSOfBoundaryMap(d) - L.size();
            HomGroup.setFreepart(freepart);
            return HomGroup;
        }
        List<Integer> DiagA = smithDiagonalOfBoundaryMap(which + 1);
        List<Integer> DiagB = smithDiagonalOfBoundaryMap(which);

        int freepart = nZOfBoundaryMap(which + 1) - DiagA.size() - DiagB.size();

        HomGroup.setFreepart(freepart);
        HomGroup.addElementalDiv(DiagA);
        return HomGroup;
    }



    /**
     * Verwirft den Cache zur Speicherung von Smith-Diagonalen. Dies geschieht bei der Nutzung aller öffentlichen
     * Schnittstellen automatisch, falls man den Komplex verändert.
     */
    public void invalidateSmithNormalFormCache() {
        smithNormalFormCacheIsValid = false;
    }

    //----------------------------------------------------------------




    void glueRec(int[] s1,int[] s2) {
        
        //Rek Abbruch
        if (s1 == null) return;

        DeltaSimplex ss1 = DeltaSimplex.of(s1, 0);
        DeltaSimplex ss2 = DeltaSimplex.of(s2, 0);

        ss1.eqClass =  EquGetter.get(ss1);
        ss2.eqClass =  EquGetter.get(ss2);
        
        int max = Integer.max(ss1.eqClass,ss2.eqClass);

        getSimplizes(ss1.getDimension()).forEach(ds -> {
            if (ds.eqClass == ss1.eqClass || ds.eqClass == ss2.eqClass) {
                ds.eqClass = max;
                EquGetter.put(ds, max);
            }
        });

        for (int i=0;i<s1.length;i++) {
            glueRec(arrDiv(i,s1),arrDiv(i, s2));
        }
        
        
    }


    private static int[] arrDiv(int withoutIndex, int[] arr) {
        if (arr.length <= 1) return null;

        int[] ret = new int[arr.length-1];
        
        int idx = 0;
        for (int i=0;i<arr.length;i++) {
            if (i == withoutIndex) continue;
            else ret[idx] = arr[i]; 

            idx++;
        }
        return ret;
    }


    // arr const
    private static  boolean isStrictMonoton(int[] arr) {
        for (int i=0;i<arr.length-1;i++)
            if (arr[i+1] <= arr[i]) return false;
        return true;
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
        List<DeltaSimplex> colIndiziierung = uniqueCOl(which);
        

        int nZ = rowIndiziierung.length;
        int nS = colIndiziierung.size();

        LowDensitiyMatrix<F> M = new LowDensitiyMatrix<F>(nZ, nS,nullobj);

        for (int k = 0; k < nS; k++) {
            DeltaSimplex a = colIndiziierung.get(k); // k-te SpaltenIndiziierung

            for (int i = 0; i < which + 1; i++) { // which+1 ist die Länge eines Spalten-Index Simplex.
                DeltaSimplex b = a.subsequence(i,-1); // Nun eine ZeilenIndizzierung
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



    //---------------------------------------- Erw. Öffentliche Schnittstelle--------------------


    /**
     * Prüft, ob der Komplex diesen oSimplex beinhaltet.
     * @param Args oSimplex
     * @return true, falls der oSimplex enthalten war, false sonst.
     */
    public boolean contains(int... Args) {
        DeltaSimplex el = DeltaSimplex.of(Args, 0);
        return Komplex.get(el.getDimension()).contains(el);
    }
    
    /**
     * Verklebt die geord. Simplizes [Args[0] ,....,Args[Args.length/2-1]] und [Args[Args.length/2] ,....,Args[Args.length-1]].
     * Die Simplizes müssen im Komplex vorhanden sein.
     * @param Args die beiden oSimplizes die verklebt werden sollen, nacheinander eingegeben.
     */
    public void glue(int... Args) {

        if (Args.length %2 != 0)
            throw new IllegalArgumentException("Argumentanzahl war nicht gerade");
        

        
        
        int alen = Args.length/2;
        int[] s1 = new int[alen];
        int[] s2 = new int[alen];

        for (int i=0;i<alen;i++) {
            s1[i] = Args[i];
            s2[i] = Args[i+alen];
        }


        if (!isStrictMonoton(s1) || !isStrictMonoton(s2))
            throw new IllegalArgumentException("Übergebene Simplizes entsprechen nicht der nat. Ordnung");

        if (!this.contains(s1) || !this.contains(s2))
            throw new NoSuchElementException("Ein übergebener Simplex ist nicht in diesen Komplex vorhanden");
        

        
        invalidateSmithNormalFormCache();

        glueRec(s1,s2);
    }


    /**
     * Verklebt die oSimplizes s1 und s2. Die Simplizes müssen im Komplex vorhanden und natürlich gleich lang sein.
     * @param s1 oSimplex
     * @param s2 oSimplex
     */
    public void glue(int[] s1, int[] s2) {

        if (s1 == null || s2 == null)
            throw new NullPointerException("'null' ist kein gültiger oSimplex");

        if (s1.length != s2.length)
            throw new IllegalArgumentException("Übergebene Simplizes waren nicht gleich lang.");

        if (!isStrictMonoton(s1) || !isStrictMonoton(s2))
            throw new IllegalArgumentException("Übergebene Simplizes entsprechen nicht der nat. Ordnung");

        if (!this.contains(s1) || !this.contains(s2))
            throw new NoSuchElementException("Ein übergebener Simplex ist nicht in diesen Komplex vorhanden");
        

        
        invalidateSmithNormalFormCache();

        glueRec(s1,s2);
    }


    /**
     * Fügt den übergebenden oSimplex vollständig in den Komplex ein, d.h. Fügt man zb (1,2,3) ein wird auch automatisch
     * (1,2),(1,3),(2,3),(1),(2),(3) eingefügt, falls diese nicht bereits vorhanden waren.
     * @param Args oSimplex
     */
    public void addSimplex(int... Args) {

        if (!isStrictMonoton(Args))
            throw new IllegalArgumentException("Übergebener Simplize entspricht nicht der nat. Ordnung");

        invalidateSmithNormalFormCache();
        this.addSimplex_c(Simplex.s(Args));
    }

    /**
     * Fügt die übergebenden oSimplizes vollständig in den Komplex ein, d.h. Fügt man zb (1,2,3) ein wird auch automatisch
     * (1,2),(1,3),(2,3),(1),(2),(3) eingefügt, falls diese nicht bereits vorhanden waren.
     * @param simplices oSimplex Array, die alle eingefügt werden
     */
    public void addSimplex(Simplex... simplices) {
        
        for (Simplex a:simplices) 
            this.addSimplex(a.asArray());
        

    }


    /**
     * Verklebt alle übergebenen oSimplizes in simplices. Tut nichts, falls nur ein Simplex übergeben wurde.
     * @param simplices die oSimplices die verklebt werden sollen. Sie müssen alle gleich Lang und im Komplex vorhanden sein.
     */
    public void glue(Simplex... simplices) {
        if (simplices.length < 2) return;

        for (int i=0;i<simplices.length-1;i++) {
            glue(simplices[i].asArray(), simplices[i+1].asArray());
        }


    }




    // Spezielle Funktionen für J8 Version--------------------------------


    public static DeltaComplex create() {
        return new DeltaComplex();
    }

    public void printAllHomologies() {
        for (int i=0;i<=this.dimension();i++) {
            System.out.println(i +"-te Homologie = " + this.HomologyGroup(i));
        }
    }
    
    public String AllHomologies() {
        String ret = "";
        for (int i=0;i<=this.dimension();i++) {
            ret += (i +"-te Homologie = " + this.HomologyGroup(i) + "\n");
        }
        return ret;
    }



}
