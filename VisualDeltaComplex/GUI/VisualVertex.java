package GUI;

import java.io.Serializable;

public class VisualVertex implements Comparable<VisualVertex>, Serializable {

    int vertexID;

    int posx;
    int posy;


    public VisualVertex(int id, int x, int y) {
        this.vertexID = id;
        this.posx = x;
        this.posy = y;
    }


    @Override
    public String toString() {
        return Integer.toString(vertexID);
    }


    @Override
    public int compareTo(VisualVertex o) {
        return Integer.compare(this.vertexID, o.vertexID);
    }
    
}
