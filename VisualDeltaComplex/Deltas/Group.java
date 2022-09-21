package Deltas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import GUI.Nutzfkten;

public class Group<T extends RecordHIB<T>> {

    int freepart;
    private final List<T> elDivs = new ArrayList<>();

    private final T nullobj;

    public Group(int freepart, T nullobj) {
        this.freepart = freepart;
        this.nullobj = nullobj;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        String upscrt = Nutzfkten.UPSCRIPT_NUMBERS;
        String freeString = Integer.toString(freepart);
        for (int i=0;i<=9;i++) {
            freeString = freeString.replace(Integer.toString(i).charAt(0),upscrt.charAt(i));
        }

        if (freepart > 1) {
            s.append(nullobj.GroupRepr()+ freeString);
            for (T ff : elDivs) {
                s.append(" + ");
                s.append(nullobj.GroupRepr());
                s.append("/" + ff);
            }
        }
        else if (freepart == 1) {
            s.append(nullobj.GroupRepr());
            for (T ff : elDivs) {
                s.append(" + ");
                s.append(nullobj.GroupRepr());
                s.append("/" + ff);
            }
        }
        else if (freepart == 0) {

            if (elDivs.isEmpty())
                return "0";

            int cc = 0;
            for (T ff : elDivs) {
                    s.append(nullobj.GroupRepr());
                    s.append("/" + ff);
                    if (cc < elDivs.size()-1)
                        s.append(" + ");
                    cc++;
            }
            
        
        }


        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elDivs == null) ? 0 : elDivs.hashCode());
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
        Group<?> other = (Group<?>) obj;
        if (elDivs == null) {
            if (other.elDivs != null)
                return false;
        } else if (!areEqAsSets(this.elDivs, other.elDivs))
            return false;
        if (freepart != other.freepart)
            return false;
        return true;
    }

    private static <F, V> boolean areEqAsSets(List<F> l1, List<V> l2) {
        Set<F> A = new HashSet<>(l1);
        Set<V> B = new HashSet<>(l2);
        return A.equals(B);
    }

    public void addElementalDiv(List<T> diagA) {
        for (T aa : diagA) {
            if (aa.isUnit())
                continue;
            this.elDivs.add(aa);
        }
    }

}
