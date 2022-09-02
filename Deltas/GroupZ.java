package Deltas;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Klasse zur Darstellung von Gruppen (über Z) nach Zerlegung in Elementarteilern.
 */
class GroupZ {
    private int freepart=0;
    // 
    private final ArrayList<Integer> elementalDivs = new ArrayList<>(); // Angabe der ElementarTeiler in einer Liste   

    public static GroupZ TrivialGroup = new GroupZ();

    GroupZ() {
        this.freepart = 0;
    }

    /**
     * Erstellt eine Gruppe in der Hauptform. Prüft nicht, ob die Elementarteiler (Divs) Teilbarkeitsbed. genügen.
     * Ändert evtl. Werte in Divs mit Mult. mit -1, und ignoriert Werte in Divs mit Betrag 1.
     * @param freepart die "Dimension" des freien Gruppenanteils
     * @param Divs die Elementarteiler
     */
    public GroupZ(int freepart, Collection<Integer> Divs) {
        this.freepart = freepart;
        this.addElementalDiv(Divs);
    }

    /**
     * Erstellt eine Gruppe in der Hauptform. Prüft nicht, ob die Elementarteiler (Divs) Teilbarkeitsbed. genügen.
     * Ändert evtl. Werte in Divs mit Mult. mit -1, und ignoriert Werte in Divs mit Betrag 1.
     * @param freepart die "Dimension" des freien Gruppenanteils
     * @param elementalDivs die Elementarteiler
     */
    public static GroupZ create(int freepart, int... elementalDivs) {
        ArrayList<Integer> L = new ArrayList<Integer>();
        for (int i:elementalDivs) L.add(i);
        return new GroupZ(freepart, L);
    }


    public GroupZ(int freepart) {
        this.freepart = freepart;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elementalDivs == null) ? 0 : elementalDivs.hashCode());
        result = prime * result + freepart;
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
        GroupZ other = (GroupZ) obj;
        if (elementalDivs == null) {
            if (other.elementalDivs != null)
                return false;
        } else if (! listeqPerm(elementalDivs, other.elementalDivs))
            return false;
        if (freepart != other.freepart)
            return false;
        return true;
    }
    

    private static boolean listeqPerm(ArrayList<Integer>l1, ArrayList<Integer> l2) {
        if (l1.size() != l2.size()) return false;
        for (int a:l1) if (!l2.contains(a)) return false;
        return true;
    }



    @Override
    public String toString() {
        String rückgabe="";

        if (freepart != 0 && !elementalDivs.isEmpty()) rückgabe += "Z^"+ freepart + " + ";
        else if (freepart != 0 && elementalDivs.isEmpty()) rückgabe += "Z^" + freepart;

        int counter = 0;
        for (Integer eldiv : elementalDivs) {
            counter++;
            if (counter == elementalDivs.size()) rückgabe += "Z/"+ eldiv; // letzter Eingabe Wert
            else rückgabe += "Z/"+ eldiv + " + ";
        }

        if (rückgabe.equals("")) return "{0}"; // Rückgabe Triviale Gruppe
        return rückgabe;
    }


    public int getFreepart() {
        return freepart;
    }


    public void setFreepart(int freepart) {
        this.freepart = freepart;
    }
    
    /**
     * Fügt einen ElementarTeiler hinzu. Fügt nichts hinzu, falls Eingabe -1 oder 1 ist
     * @param div
     */
    public void addElementalDiv(int div) {
        if (div == 1 || div == -1) return;

        int entry = div > 0 ? div : -div;
        elementalDivs.add(entry);
        
    }


    public void addElementalDiv(Collection<Integer> Liste) {
        for (Integer alpha:Liste) {
            if (alpha == 1 || alpha == -1) continue;
		    int entry = alpha > 0 ? alpha : -alpha;
            elementalDivs.add(entry);
	    }
    }

}
