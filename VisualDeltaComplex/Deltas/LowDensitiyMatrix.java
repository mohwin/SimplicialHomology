package Deltas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


class  LowDensitiyMatrix <T extends RecordHIB<T>> {
    
    public Map<Dupel, T> matrix = new HashMap<>();
    private int nZ;
    private int nS;
    private final T nullobj;


    private Dupel p(int i,int j) {
        return new Dupel(i, j);
    }



    /**
     * Es muss nullobj null im Ring sein und T eine Record klasse
     * @param nZ
     * @param nS
     * @param nullobj
     */
    public LowDensitiyMatrix(int nZ, int nS, T nullobj) {
        this.nZ = nZ;
        this.nS = nS;
        this.nullobj = nullobj;
    }


    public LowDensitiyMatrix(LowDensitiyMatrix<T> toCpy) {
        this.nS = toCpy.nS;
        this.nZ = toCpy.nZ;
        this.matrix = new HashMap<>(toCpy.matrix);
        this.nullobj = toCpy.nullobj;
    }


    /**
     * Setzt den Eintrag i,j zu value. Kontrolliert nicht, ob Bounds verletzt wurden.
     * @param i Zeilenindex, von 0 bis einschl. nZ-1
     * @param j Spaltenindex, von 0 bis eischl. nS-1
     * @param value Wert der gesetzt wird
     */
    public void setEntry(int i, int j,T value) {
        if ( nullobj.equals(value)) matrix.remove(p(i,j));
        else matrix.put(p(i,j), value);
    }

     /**
     * Gibt den Eintrag an i,j zurück. Kontrolliert nicht, ob Bounds verletzt wurden.
     * @param i Zeilenindex, von 0 bis einschl. nZ-1
     * @param j Spaltenindex, von 0 bis eischl. nS-1
     * @return Wert an i,j
     */
    public T getEntry(int i,int j) {
        return matrix.getOrDefault(p(i,j), nullobj);
    }


    public T getEntry(Dupel d) {
        return matrix.getOrDefault(d, nullobj);
    }

 
    /**
     * Transponiert die momentane Matrix
     */
    public void transpose() {
        int h=nZ; nZ = nS; nS = h;
        Map<Dupel,T> nMap = new HashMap<>();
        Set<Dupel> keys = matrix.keySet();
        for (Dupel key: keys) { // Vertauschung der Indices
            nMap.put(p(key.r,key.l), matrix.get(key));
        }
        matrix = nMap;
        
    }


    /**
     * Gibt neu Instanziierte Liste der Zeile zurück. Die Länge der Liste beträgt nS
     * @param index Zeilenindex von 0,...,nZ-1
     * @return Neu Instanziierte Liste der Zeile.
     */
    public ArrayList<T> getRow(int index) {
        ArrayList<T> RowList = new ArrayList<>();
        RowList.ensureCapacity(nS);

        for (int i=0;i<nS;i++) {
            RowList.add(getEntry(index, i));
        }
        return RowList;
    }

    /**
     * Gibt neu Instanziierte Liste der Spalte zurück. Die Länge der Liste beträgt nZ
     * @param index Zeilenindex von 0,...,nS-1
     * @return Neu Instanziierte Liste der Spalte.
     */
    public ArrayList<T> getCol(int index) {
        ArrayList<T> ColList = new ArrayList<>();
        ColList.ensureCapacity(nZ);

        for (int i=0;i<nZ;i++) {
            ColList.add(getEntry(i, index));
        }
        return ColList;
    }
    
    /**
     *  Vertauscht die Zeilen i und j.
     * @param i Zeilenindex von 0,...,nZ-1
     * @param j Zeilenindex von 0,...,nZ-1
     */
    public void permutateRows(int i, int j) {
        List<T> Ri = getRow(i);
        List<T> Rj = getRow(j);

        matrix.keySet().removeIf(d->(d.l==i || d.l==j));

        for (int ii=0;ii<Ri.size();ii++) {
            if (! Ri.get(ii).equals(nullobj) ) matrix.put(p(j,ii), Ri.get(ii));
        }
        for (int ii=0;ii<Rj.size();ii++) {
            if (! Rj.get(ii).equals(nullobj)) matrix.put(p(i,ii), Rj.get(ii));
        }
    
    }

    /**
     *  Vertauscht die Spalten i und j.
     * @param i Spaltenindex von 0,...,nS-1
     * @param j Spaltenindex von 0,...,nS-1
     */
    public void permutateCols(int i, int j) {
        List<T> Ci = getCol(i);
        List<T> Cj = getCol(j);

        matrix.keySet().removeIf(d->(d.r==i || d.r==j));

        for (int ii=0;ii<Ci.size();ii++) {
            if (!Ci.get(ii).equals(nullobj)) matrix.put(p(ii,j), Ci.get(ii));
        }
        for (int ii=0;ii<Cj.size();ii++) {
            if (!Cj.get(ii).equals(nullobj)) matrix.put(p(ii,i), Cj.get(ii));
        }
    
    }

    /**
     * Multipliziert die Zeile index mit times
     * @param index ZeilenIndex von 0,...,nZ-1
     * @param times Wert mit dem die Zeile multipliziert wird
     */
    public void rowTimes(int index, T times) {
        Object[] L = matrix.keySet().stream().filter(d-> d.l==index).toArray();
     
        for (Object dd:L) {
            Dupel d = (Dupel) dd;
            setEntry(d.l, d.r, getEntry(d.l, d.r).times(times) );
        }
    }

    /**
     * Multipliziert die Spalte index mit times
     * @param index SpaltenIndex von 0,...,nS-1
     * @param times Wert mit dem die Spalte multipliziert wird
     */
    public void colTimes(int index, T times) {
        List<Dupel> L = matrix.keySet().stream().filter(d-> d.r==index).collect(Collectors.toList());
        for (Dupel d:L) {
            setEntry(d.l, d.r, getEntry(d.l, d.r).times( times ));
        }
    }

    /**
     * Aufaddiert die Zeile ofRow * times auf die Zeile ontoRow. Dabei bleibt die ofRow unberührt(insofern sie nicht die ontoRow ist)
     * @param ofRow ZeilenIndex von 0,...,nZ-1.
     * @param times Wert mit em die ofRow Werte multipliziert werden
     * @param ontoRow ZeilenIndex von 0,...,nZ-1
     */
    public void rowTimesOnto(int ofRow,T times,int ontoRow) {
        List<Dupel> L = matrix.keySet().stream().filter(d-> d.l==ofRow).collect(Collectors.toList());
        for (Dupel d:L) {
            setEntry(ontoRow, d.r, getEntry(ontoRow, d.r).plus( getEntry(ofRow, d.r).times( times )) );
        }
    }

    /**
     * Aufaddiert die Spalte ofRCol * times auf die Spalte ontoCol. Dabei bleibt die ofCol unberührt(insofern sie nicht die ontoCol ist)
     * @param ofRow SpaltenIndex von 0,...,nS-1.
     * @param times Wert mit em die ofRow Werte multipliziert werden
     * @param ontoRow SpaltenIndex von 0,...,nS-1
     */
    public void colTimesOnto(int ofCol,T times,int ontoCol) {
        List<Dupel> L = matrix.keySet().stream().filter(d-> d.r==ofCol).collect(Collectors.toList());
        for (Dupel d:L) {
            setEntry(d.l, ontoCol, getEntry(d.l, ontoCol).plus(getEntry(d.l, ofCol).times( times) ));
        }
    }

    //-----------------------------------------------------

    /**
     * Sucht ein Element in der Spalte colIndex, welches nicht 0 ist.
     * @param colIndex Spalte in der gesucht werden soll in 0,...,nZ-1
     * @return Zeilenindex, falls ein Element ungelich 0 gefunden wurde, -1 sonst.
     */
    public int colIsZero(int colIndex) {
        List<Dupel> L = matrix.keySet().stream().filter(d->d.r == colIndex).collect(Collectors.toList());
        if (L.isEmpty()) return -1;
        else return L.get(0).l;
    }


    @Override
    public String toString() {
        String repr = "";
        for (int i=0;i<nZ;i++) {
            repr += "[ ";
            for (int j=0;j<nS;j++) {
                repr += getEntry(i, j).toString() + " ";
            }
            repr += "]\n";
        }
        return repr;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((matrix == null) ? 0 : matrix.hashCode());
        result = prime * result + nS;
        result = prime * result + nZ;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LowDensitiyMatrix<?> other = (LowDensitiyMatrix<?>) obj;
        
        if (nS != other.nS)
            return false;
        if (nZ != other.nZ)
            return false;

        if (matrix == null) {
            if (other.matrix != null)
                return false;
        } else if (!matrix.equals(other.matrix))
            return false;
        return true;
    }
    
    

    //------------------------------

    /** Elimiert Einträge unterhalb/ oberhalb von entry
     * 
     * @param A Matrix die geändert wird
     * @param entry der Eintrag um den es geht
     * @return true, falls etwas geändert wurde, false sonst(bei false ist alles =0 außer entry)
     */
    private static <F extends RecordHIB<F>> boolean adjust_and_eliminate_ROW(LowDensitiyMatrix<F> A,Dupel entry) {
        // Zunächst ZeilenAnpassungen, also Elemente in der gleichen Spalte sind zu betrachten, die nicht entry sind
        List<Dupel> SpaltenElemente = A.matrix.keySet().stream().filter(d -> ((d.r == entry.r) && (d.l != entry.l)))
        .collect(Collectors.toList());
        F entryValue = A.getEntry(entry);

        if (SpaltenElemente.isEmpty()) return false;

        
        for (Dupel d : SpaltenElemente) {
            F checkValue = A.getEntry(d); // Eintrag der Spalte der geprüft werden muss
            if (!entryValue.divides(checkValue)) { // entry teilt das Element nicht
                F[] erg = checkValue.bezout(entryValue);
                A.rowTimes(entry.l, erg[2]); // Zeile vom Eintrag mal Bez.Coeff
                A.rowTimesOnto(d.l, erg[1], entry.l); // Zeile mit zu el. Element auf entry Zeile
                entryValue = erg[0]; // Entry Value ist jetzt der ggT, (ändern das hier nur lokal)
            }

            // Eliminierung des Eintrags d
            A.rowTimesOnto(entry.l, checkValue.divide(entryValue).addiditiveInvers(), d.l); 

        }

        return true;
    }

    /** Elimiert Einträge links/rechts von entry
     * 
     * @param A Matrix die geändert wird
     * @param entry der Eintrag um den es geht
     * @return true, falls etwas geändert wurde, false sonst(bei false ist alles =0 außer entry)
     */
    private static <F extends RecordHIB<F>> boolean adjust_and_eliminate_COL(LowDensitiyMatrix<F> A,Dupel entry) {
        // Zunächst SpaltenAnpassunge, also Elemente in der gleichen Zeile sind zu betrachten, die nicht entry sind
        List<Dupel> ZeilenElemente = A.matrix.keySet().stream().filter(d -> ((d.l == entry.l) && (d.r != entry.r)))
        .collect(Collectors.toList());
        F entryValue = A.getEntry(entry);

        if (ZeilenElemente.isEmpty()) return false;


        
        for (Dupel d : ZeilenElemente) {
            F checkValue = A.getEntry(d); // Eintrag der Zeile der geprüft werden muss
            if (!entryValue.divides(checkValue)) { // entry teilt das Element nicht
                F[] erg = checkValue.bezout(entryValue);
                A.colTimes(entry.r, erg[2]); // Spalte vom Eintrag mal Bez.Coeff
                A.colTimesOnto(d.r, erg[1], entry.r); // Spalte mit zu el. Element auf entry Zeile
                entryValue = erg[0]; // Entry Value ist jetzt der ggT, (ändern das hier nur lokal)
            }

            // Eliminierung des Eintrags d
            A.colTimesOnto(entry.r, checkValue.divide(entryValue).addiditiveInvers(), d.r); 

        }

        return true;
    }


    /**
     * Normalisiert die Liste, die aus dem Smith Algo entstanden ist gemäß Schritt 4.
     * @param toNormalize zu Normalisierende Liste
     */
    private static <F extends RecordHIB<F>>void normalize(List<F> toNormalize) {
        List<F> W = new ArrayList<>(toNormalize.stream().filter(el->!el.isUnit()).collect(Collectors.toList()));
        
        if (W.isEmpty()) return;

        boolean changed = false;
        F v0 = null;
        F v1 = W.get(0).addiditiveInvers();
        do {
            changed = false;
            for (int i=0;i<W.size()-1;i++) {
                v0 = v1;
                v1 = W.get(i+1);
                if (!v0.divides(v1)) {
                    changed = true;
                    F[] erg = v1.bezout(v0);
                    W.set(i,erg[0]);
                    W.set(i+1,v1.times(v0.divide(erg[0])));
                    v1 =  v1.times(v0.divide(erg[0]));
                }
            }
        } while (changed);

        // Liste wieder zsmfügen
        // Alle Einheiten BEHALTEN
        toNormalize.removeIf(el->!el.isUnit());
        
        //zsmfügen
        toNormalize.addAll(W);

    }

    /**
     * Führt den Smith Algo zu Bestimmung der Elemetarteilern aus. Kopiert A.
     * @param A
     * @return Liste der Elementarteiler, Teilbarkeitsbedingung sollte gelten.
     */
    public static <F extends RecordHIB<F>> List<F> smithDiagonal(LowDensitiyMatrix<F> A) {
        LowDensitiyMatrix<F> M = new LowDensitiyMatrix<F>(A);

        Dupel entry = new Dupel(0,0);

        int j_t = -1; int oldj_t=-1;
        for (int t=0;t<M.nZ;t++) {

            // Spalte ungleich Null finden
            oldj_t = j_t; // alter Spaltenindex setzten
            int zidx_SplteN0 = -1;
            for (int j=j_t+1;j<M.nS;j++) {
                zidx_SplteN0 = M.colIsZero(j);
                if (zidx_SplteN0 !=-1) { // Spalte nicht Null gefunden
                    j_t = j;
                    break;
                }
            }

            if (oldj_t == j_t) { // Falls keine neue Spalte gefunden wurde, dann ist der Rest der Matrix =0
                break;
            }

            // OPT Zeilentausch weglassen

            // Setzten des Entrys 
            entry.l = zidx_SplteN0;
            entry.r = j_t;

            // Eliminieren der anderen Einträge
            boolean changed= true;
            do {
                changed = adjust_and_eliminate_ROW(M, entry);
                changed = adjust_and_eliminate_COL(M, entry);
            } while(changed);



        }

        // Alle Werte extrahieren
        ArrayList<F> Alphas = new ArrayList<>();
        Alphas.addAll(M.matrix.values());

        // Normalisieren
        normalize(Alphas);
        return Alphas;
    }


    /**
     * Führt den Smith Algo zu Bestimmung der Elemetarteilern aus. this bleibt unberührt.
     * @return Liste der Elementarteiler, Teilbarkeitsbedingung sollte gelten.
     */
    public List<T> smithDiagonal() {
        return smithDiagonal(this);
    }

    public int getnZ() {
        return nZ;
    }

    public int getnS() {
        return nS;
    }




}
