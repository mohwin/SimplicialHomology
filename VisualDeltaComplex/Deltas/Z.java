package Deltas;

public class Z implements RecordHIB<Z>{

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        Z other = (Z) obj;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    int value;
    
    public Z() {
        value = 0;
    }

    public Z(int va) {
        value = va;
    }

    @Override
    public Z Nullwert() {
        return new Z(0);
    }

    @Override
    public Z times(Z factor) {
        return new Z(factor.value*this.value);
    }

    @Override
    public Z plus(Z summand) {
        return new Z(this.value+summand.value);
    }

    @Override
    public Z[] bezout(Z bb) {
        Z[] ret = new Z[3];

        int a = this.value;
        int b = bb.value;

        int l =  1; // Initialisierung der Zeiger
        int r = 0;
        int u = 0; // Deklaration der lokalen Variablen
        int v = 1;
        while (b != 0)
        {
            int q = a / b;
            int b1 = b; // Variable zum Zwischenspeichern
            b = a - q * b;
            a = b1;
            int u1 = u; // Variable zum Zwischenspeichern
            u = l - q * u;
            l = u1;
            int v1 = v; // Variable zum Zwischenspeichern
            v = r - q * v;
            r = v1;
        }  
        ret[0] = new Z(a);
        ret[1] = new Z(l);
        ret[2] = new Z(r);
        return ret;
    }

    @Override
    public boolean divides(Z other) {
        if (this.value == 0) return false;
        return ((other.value % this.value) == 0);
    }

    @Override
    public Z addiditiveInvers() {
        return new Z(-this.value);
    }


    

    @Override
    public Z divide(Z divisor) {
        return new Z(this.value / divisor.value);
    }

    @Override
    public boolean isUnit() {
        return ((value == 1) ||(value == -1));
    }

    @Override
    public Z EinsWert() {
        return new Z(1);
    }

    @Override
    public String GroupRepr() {
        return "Z";
    }

    
}
