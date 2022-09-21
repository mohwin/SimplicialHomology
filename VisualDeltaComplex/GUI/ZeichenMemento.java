package GUI;

import java.io.Serializable;

import Deltas.DeltaComplex;

public class ZeichenMemento implements Serializable{
    public ZeichenMemento(DeltaComplex<VisualVertex> cmpl, int maxVertex,double scale) {
        this.cmpl = cmpl;
        this.maxVertex = maxVertex;
        this.scale = scale;
    }
    DeltaComplex<VisualVertex> cmpl;
    int maxVertex;
    double scale;

    

    
}
