package GUI;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import Deltas.VisualSimplex;

class EqStringAssigner {
    

    private EqStringAssigner() {}



    public static Map<VisualSimplex<VisualVertex>,String> eqSolver(Map<VisualSimplex<VisualVertex>,Integer> nonTrivEqMap) {
        HashMap<VisualSimplex<VisualVertex>,String> ret = new HashMap<>();

        SortedSet<Integer> vertexEqClasses = new TreeSet<>(); // Sorted
        SortedSet<Integer> edgeEqClasses = new TreeSet<>();
        SortedSet<Integer> twoSimplexEqClasses = new TreeSet<>();
        
        for (Map.Entry<VisualSimplex<VisualVertex>,Integer> e : nonTrivEqMap.entrySet()) {
            if (e.getKey().getDimension() == 0)
                vertexEqClasses.add(e.getValue());
            else if (e.getKey().getDimension() == 1)
                edgeEqClasses.add(e.getValue());
            else if (e.getKey().getDimension() == 2)
                twoSimplexEqClasses.add(e.getValue());
        }

        Map<Integer, String> vertexAssn = new HashMap<>();
        Map<Integer, String> edgeAssn = new HashMap<>();
        Map<Integer, String> twoSimplexAssn = new HashMap<>();
        
        int counter = 0;
        for (int i:vertexEqClasses) {
            vertexAssn.put(i, nextVertexBez(counter)); counter++;
        }
        counter = 0;
        for (int i:edgeEqClasses) {
            edgeAssn.put(i, nextEdgeBez(counter));counter++;
        }
        counter = 0;
        for (int i:twoSimplexEqClasses) {
            twoSimplexAssn.put(i, nextTwoSimplexBez(counter));counter++;
        }

        
        for (VisualSimplex<VisualVertex> vs : nonTrivEqMap.keySet()) {
            if (vs.getLength() == 1) {
                ret.put(vs,vertexAssn.get(nonTrivEqMap.get(vs)));   
            }
            else if (vs.getLength() == 2){
                ret.put(vs,edgeAssn.get(nonTrivEqMap.get(vs))); 
            }
            else if (vs.getLength() == 3) {
                ret.put(vs,twoSimplexAssn.get(nonTrivEqMap.get(vs))); 
            }
        }

        return ret;
    }

    public static String numberToLowerCase(int n) {
        String number = Integer.toString(n);
        for (int i=0;i<=9;i++) {
            number = number.replaceAll(Integer.toString(i),Character.toString(Nutzfkten.SUBSCRIPT_NUMBERS.charAt(i)));
        }
        return number;
    }


    private static String nextVertexBez(int currNumber) {
        return "v" + numberToLowerCase(currNumber);
    }

    private static final String EDGE_ALPHABET = "abcdefghijklmnopqrstuwyz";

    private static String nextEdgeBez(int currNumber) {
        if (currNumber < EDGE_ALPHABET.length()) {
            return Character.toString(EDGE_ALPHABET.charAt(currNumber));
        }
        int  ncurr = currNumber - EDGE_ALPHABET.length(); // = 0,1,2,3 wachsend mit currNumber

        return "x" + numberToLowerCase(ncurr);
    }

    private static String nextTwoSimplexBez(int currNumber) {
        if (currNumber < EDGE_ALPHABET.length()) {
            return Character.toString(EDGE_ALPHABET.toUpperCase().charAt(currNumber));
        }
        int  ncurr = currNumber - EDGE_ALPHABET.length(); // = 0,1,2,3 wachsend mit currNumber

        return "X" + numberToLowerCase(ncurr);
    }


}
