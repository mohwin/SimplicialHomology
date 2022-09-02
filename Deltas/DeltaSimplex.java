package Deltas;

import java.util.Arrays;



class DeltaSimplex {

    // Stets in natürlicher Ordnung
    int simplex[] = null;

    // Eq Klasse, TopLevel muss kümmern Unit. ist der Wert 0
    int eqClass = 0;




    public DeltaSimplex(int[] simplex, int eqClass) {
        this.simplex = simplex;
        this.eqClass = eqClass;
    }


    /**
     * 1 falls arr even Perm der nat.Order ist -1 sortiert arr.
     * @return
     */
    static int isEvenPermutation(int[] arr) {
        int[] cpy = arr.clone();
        Arrays.sort(arr);

        int ret = 1;

        for (int i=0;i<arr.length;i++) {
            if (arr[i] != cpy[i]) { // Suche Mismatch in cpy
                for (int j=i+1;j<arr.length;j++) {
                    if (arr[i] == cpy[j]) {
                        cpy[j] = cpy[i];
                        break;
                    }
                }
                ret *= -1;
            }
        }

        return ret;
    }


    private DeltaSimplex() {}



    /**
     * Factory Function. Gibt den DeltaSimplex (startValue+0, startValue+1,...,startValue+dimension) zurück.
     * @param dimension
     * @param startValue
     * @param eqClass
     * @return
     */
    static DeltaSimplex simp(int dimension, int startValue,int eqClass) {
        DeltaSimplex ret = new DeltaSimplex();
        ret.simplex = new int[dimension+1];
        ret.eqClass = eqClass;
        for (int i=0;i<ret.simplex.length;i++)
            ret.simplex[i] = startValue+i;
        return ret;
    }


    static DeltaSimplex of(Simplex si,int eqClass) {
        DeltaSimplex ret = new DeltaSimplex();

        ret.eqClass = eqClass;
        int[] x = si.asArray();
        Arrays.sort(x);

        ret.simplex = x;

        return ret;
    }

    /**
     * 
     * @param si const
     * @param eqClass
     * @return
     */
    static DeltaSimplex of(int[] si,int eqClass) {
        DeltaSimplex ret = new DeltaSimplex();
        
        ret.eqClass = eqClass;
        int[] x = si.clone();
        Arrays.sort(x);

        ret.simplex = x;

        return ret;
    }



    /**
     * Gibt den SubSimplex zurück.
     * @param withoutIndex
     * @param eqClass
     * @param weight
     * @return
     */
    DeltaSimplex subsequence(int withoutIndex,int eqClass) {
        DeltaSimplex ret = new DeltaSimplex();
        ret.simplex = new int[this.simplex.length-1];
        ret.eqClass = eqClass;
        int idx = 0;
        for (int i=0;i<this.simplex.length;i++) {
            if (i == withoutIndex) continue;
            else ret.simplex[idx] = this.simplex[i]; 

            idx++;
        }

        return ret;
    }

    //---------------------------------


    
    @Override
    public int hashCode() {
        return Arrays.hashCode(simplex);
    }
    // Consider two DSSimps as Equal iff the simplex fields are equal
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeltaSimplex other = (DeltaSimplex) obj;
        if (!Arrays.equals(simplex, other.simplex))
            return false;
        return true;
    }



    @Override
    public String toString() {
        return Arrays.toString(simplex)+"("+ eqClass +")" ;
    }



    //------------------------------------------------------

    int getDimension() {
        return this.simplex.length - 1;
    }

    int getLength() {
        return this.simplex.length;
    }


    



}
