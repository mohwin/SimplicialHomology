package Test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Deltas.Sortierer;

class SortiererTest {

    
    static TreeSet<Integer> tof(int... Args) {
        TreeSet<Integer> ret = new TreeSet<>();
        for(int i: Args) ret.add(i);
        return ret;
    }

    @Test
    @DisplayName("Torus")
    void test_1() {
        Integer[] verts = {0,1,2,3};
        TreeSet<Integer> vertices = tof(0,1,2,3);
        HashMap<Integer,SortedSet<Integer>> rels = new HashMap<>();

        rels.put(1, tof(0,2));
        rels.put(2,tof(3));
        rels.put(0,tof(3));

        Sortierer<Integer> s = new Sortierer<>(vertices,rels);
        Comparator<Integer> c = s.getComparator();
        SortedSet<Integer> zyklus = s.getZyklus();

        Arrays.sort(verts, c);
        Integer[] correctOrder = {3,0,2,1};

        assertTrue(Arrays.toString(verts) + " == " + Arrays.toString(correctOrder),Arrays.equals(verts, correctOrder));
        assertTrue(zyklus.toString()+ " is Empty",zyklus.isEmpty());
    }

    @Test
    @DisplayName("Zyklus")
    void test_2() {
        TreeSet<Integer> vertices = tof(0,1,2,3);
        HashMap<Integer,SortedSet<Integer>> rels = new HashMap<>();

        rels.put(1, tof(3));
        rels.put(2,tof(1));
        rels.put(3,tof(2));

        Sortierer<Integer> s = new Sortierer<>(vertices,rels);
        Comparator<Integer> c = s.getComparator();
        SortedSet<Integer> zyklus = s.getZyklus();

        assertTrue(c == null);
        assertTrue(zyklus.equals(tof(1,2,3)));
    }

}
