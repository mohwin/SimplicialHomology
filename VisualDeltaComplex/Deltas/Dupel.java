package Deltas;



class Dupel {
    public int l;
    public int r;

    
    public Dupel(int l, int r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + l;
        result = prime * result + r;
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
        Dupel other = (Dupel) obj;
        if (l != other.l)
            return false;
        if (r != other.r)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[ " + l + ", " + r + " ]";
    }

    

}