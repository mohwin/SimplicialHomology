package Deltas;


public class ZmodN implements RecordHIB<ZmodN> {

    
    public final int value;
    public final int module;

    public static final String _version = "1";
    
    public ZmodN(int value,int module) {
        this.value = value%module;
        this.module = module;
    }


    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + module;
        result = prime * result + value;
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
        ZmodN other = (ZmodN) obj;
        if (module != other.module)
            return false;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(value) + " mod("+ Integer.toString(module)+")";
    }



    @Override
    public ZmodN Nullwert() {
        return new ZmodN(0, module);
    }



    @Override
    public ZmodN times(ZmodN factor) {

        if (this.module != factor.module)
            throw new ArithmeticException("Falsche Moduln");
        
        return new ZmodN((this.value*factor.value) % module, module);
        
    }



    @Override
    public ZmodN plus(ZmodN summand) {
        if (this.module != summand.module)
            throw new ArithmeticException("Falsche Moduln");
        
        return new ZmodN((this.value+summand.value) % module, module);
    }


    private ZmodN fof(int val) {
        return new ZmodN(val, module);
    }


    public static ZmodN cpy(ZmodN of) {
        return new ZmodN(of.value, of.module); 
    }

    public ZmodN cpy() {
        return new ZmodN(this.value, this.module);
    }

    @Override
    public ZmodN[] bezout(ZmodN bb) {
        ZmodN b = fof(bb.value);
        ZmodN a = fof(this.value);
        
        ZmodN[] ret = new ZmodN[3];

        ret[1] = fof(1); // Initialisierung der Zeiger
        ret[2] =  fof(0);
        ZmodN u = fof(0); // Deklaration der lokalen Variablen
        ZmodN v = fof(1);
        while (b.equals(this.Nullwert()))
        {
            ZmodN q = a.divide(b);
            ZmodN b1 = b; // Variable zum Zwischenspeichern
            b = a.plus( q.times(b).addiditiveInvers());
            a = b1;
            ZmodN u1 = u; // Variable zum Zwischenspeichern
            u = ret[1].plus( q.times(u).addiditiveInvers());
            ret[1] = u1;
            ZmodN v1 = v; // Variable zum Zwischenspeichern
            v = ret[2].plus( q.times(v).addiditiveInvers());
            ret[2] = v1;
        }  
        ret[0] = a;
        return ret;
    }



    @Override
    public boolean divides(ZmodN other) {
        if (this.module != other.module)
            throw new ArithmeticException("Falsche Moduln");
    
        for (int i=0;i<module;i++)
            if( ((this.value*i) % module) == other.value) return true;

        return false;
    }



    @Override
    public ZmodN addiditiveInvers() {
        return new ZmodN((-this.value) % module, module);
    }



    @Override
    public ZmodN divide(ZmodN divisor) {

        if (this.module != divisor.module)
            throw new ArithmeticException("Falsche Moduln");
    
        for (int i=0;i<module;i++)
            if( ((divisor.value*i) % module) == this.value) return new ZmodN(i, module);


        throw new ArithmeticException("divisor teilte diesen Wert nicht.");
    }



    @Override
    public boolean isUnit() {
        int ggT = bezout(this.value, this.module, new Dupel(0, 0));
        return (Math.abs(ggT)==1);
    }

    @Override
    public ZmodN EinsWert() {
        return new ZmodN(1, module);
    }


    /**
     * Berechnet mittels erw. Euklidischen Algos die Bezout Koeff x,y und den ggT von a und b. Die Koeff. 
     * werden in den Container Dupel Koeff gespeichert, dabei ist Koeff.l = x, Koeff.r = y. 
     * Es gilt ggT = ax+by
     * @param a Erstes ggT Argument
     * @param b Zweites ggT Argument
     * @param Koeffs Container Dupel für die Koeffizienten. wird geändert, nicht neu Instanziiert
     * @return den ggT von a und b, bis auf Assoz. mit einer Einheit
     */
    private static int bezout(int a, int b, Dupel Koeffs) {
        Koeffs.l =  1; // Initialisierung der Zeiger
        Koeffs.r = 0;
        int u = 0; // Deklaration der lokalen Variablen
        int v = 1;
        while (b != 0)
        {
            int q = a / b;
            int b1 = b; // Variable zum Zwischenspeichern
            b = a - q * b;
            a = b1;
            int u1 = u; // Variable zum Zwischenspeichern
            u = Koeffs.l - q * u;
            Koeffs.l = u1;
            int v1 = v; // Variable zum Zwischenspeichern
            v = Koeffs.r - q * v;
            Koeffs.r = v1;
        }  
        return a;
    }



    @Override
    public String GroupRepr() {
        return "(Z/" +module +")";
    }


    public static ZmodN NullModulo(int module) {
        return new ZmodN(0, module);
    }


}
