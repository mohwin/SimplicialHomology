package GUI;

import javax.swing.*;

import Deltas.DeltaComplex;

import java.awt.*;
import java.util.Collection;
import java.util.SortedSet;
import java.util.stream.Collectors;

public class GUI_Main extends JFrame {
    
    Options opt = new Options();

    DeltaComplex<VisualVertex> deltaKomplex = new DeltaComplex<>();

    Ausgabebereich ausgabebereich = new Ausgabebereich(this);

    Zeichenbereich zeichenbereich = new Zeichenbereich(this);

    KomplexRepr komplexRepr = new KomplexRepr(this);

    /**
     * Erwartet, dass arg vollst. in VisualVertexMenge liegt.
     * @param arg
     * @return Eine Liste von VisualVertices, (die per Referenz im Komplex sind), in der Ordnung der Kollektion
     * 
     */
    java.util.List<VisualVertex> sof(Collection<Integer> arg) {
        
        SortedSet<VisualVertex> d =  deltaKomplex.getVertices(null);

        return d.stream()
            .filter(vv -> arg.contains(vv.vertexID))
            .sorted((x,y) -> {
                if (x.vertexID == y.vertexID) return 0;
                for (int i: arg)
                    if (i == x.vertexID) return -1;
                    else if (i == y.posy) return 1;
                throw new IllegalStateException("This cant be ever thrown");
            })
            .collect(Collectors.toList());
            

    }

    private void initWindow(){
        //TODO load Options ... 

        setTitle("Berechnung der Homologie von "+ Nutzfkten.DELTA + "-Komplexen");
        setSize(opt.mainWindowSize.x,opt.mainWindowSize.y);
        
        getContentPane().setBackground(opt.backGroundColor);
        

        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    


    private void initLayout() {
        Container pane = this.getContentPane();
        GridBagLayout ly = new GridBagLayout();
        pane.setLayout(ly);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        

        // KomplexRepr
        c.gridwidth = 3;
        c.gridheight = 7;
        c.weightx = .3;
        ly.setConstraints(komplexRepr, c);
        pane.add(komplexRepr);

        // Zeichenbereich
        c.gridx = 3;
        c.gridwidth = 7;
        c.weightx = 1.0;
        ly.setConstraints(zeichenbereich, c);
        pane.add(zeichenbereich);

        // Ausgabe
        c.gridx = 0;
        c.gridy = 7;
        c.weighty = .3;
        c.gridheight = 3;
        c.gridwidth = 10;
        ly.setConstraints(ausgabebereich, c);
        pane.add(ausgabebereich);


    }


    private void initMenubar() {
        // Menu Bar
        JMenuBar menubar = new JMenuBar();
        JMenu Datei = new JMenu("Datei");
        JMenu Berechnen = new JMenu("Berechnen");
       
        menubar.add(Datei);
        menubar.add(Berechnen);

        setJMenuBar(menubar);
        
    }

    public GUI_Main() {
        
        initWindow();

        initLayout();

        initMenubar();


        pack();
        this.setSize(1200,800);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                GUI_Main prog = new GUI_Main();
                prog.setVisible(true);
            } 
        });
    }
}
