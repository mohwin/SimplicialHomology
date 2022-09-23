package GUI;

import java.awt.Color;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import java.awt.*;
import javax.swing.*;

import Deltas.ZmodN;
import Deltas.Z;

public class Ausgabebereich extends JPanel {

    GUI_Main parent = null;

    // Für die Ausgabe der Homologiegruppen
    private DefaultListModel<String> innerList = new DefaultListModel<>();
    private JList<String> jinnerList = new JList<String>(innerList);
    private JScrollPane homScrollPane = new JScrollPane(jinnerList);

    private String[] koeffsStrings = null;
    //linke ctrl buttons
    JCheckBox toggleVertexEqBox = new JCheckBox("Vertex Äquivalenzklassen anzeigen");
    JComboBox<String> koeffChoosingBox = null;
    JButton homBerechnenButton = new JButton("Homologie berechnen");

    JButton showKomplexData = new JButton("Komplex Daten anzeigen");

    // zeichenbereich Marked Vertices Line
    JTextField currentMarkedField = new JTextField("Rechtsklick zum markieren");
    // zeichenbereich ctrl buttons (clr fkt zu menubar geschoben)
    JButton buildSimplexButton = new JButton("Simplex erzeugen");
    JButton removeVerticesButton = new JButton("Vertices löschen");
    JButton dynamicGlueButton = new JButton("Verkleben mit...");
    JButton clearQueuesButton = new JButton("Auswahl aufheben");
    JButton rückgButton = new JButton("Rückgängig");
    JButton vwrtsButton = new JButton("Vorwärts");

    void clearAusgBoard() {
        innerList.clear();
        repaint();
    }

    private void initLayout() {
        GridBagLayout ly = new GridBagLayout();
        setLayout(ly);
        GridBagConstraints c = new GridBagConstraints();
        
        c.weightx = 0.2;
        c.weighty = 0.2;

        // linke crtl Einheit
        // toggle
        c.gridy = 0; 
        ly.setConstraints(toggleVertexEqBox, c);
        this.add(toggleVertexEqBox);
        //koeff
        c.gridy = 1;
        ly.setConstraints(koeffChoosingBox, c);
        this.add(koeffChoosingBox);
        //komplexdata
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 2;
        ly.setConstraints(showKomplexData, c);
        this.add(showKomplexData);
        //homsBer
        c.gridy = 3;
        ly.setConstraints(homBerechnenButton, c);
        this.add(homBerechnenButton);

        //HomsAusg
        c.weightx = 1.0;
        c.weighty = 1.0;
        
        c.gridy = 0;
        c.gridx = 1;
        c.gridwidth = 6;
        c.gridheight = 4;
        ly.setConstraints(homScrollPane,c);
        this.add(homScrollPane);
        //ctrl rechts
        c.weightx = 0.2;
        c.weighty = 0.2;
        // SimplCHoos
        c.gridx = 7;
        c.gridwidth = 2;
        c.gridheight = 1;
        ly.setConstraints(currentMarkedField, c);
        this.add(currentMarkedField);
        //buildSimpl
        c.gridy = 1;
        c.gridwidth = 1;
        ly.setConstraints(buildSimplexButton, c);
        this.add(buildSimplexButton);
        // remove Simpl
        c.gridx = 8;
        ly.setConstraints(removeVerticesButton, c);
        this.add(removeVerticesButton);
        //dynamicGlue
        c.gridx = 7;
        c.gridy = 2;
        ly.setConstraints(dynamicGlueButton, c);
        this.add(dynamicGlueButton);
        // pop Queues
        c.gridx = 8;
        ly.setConstraints(clearQueuesButton, c);
        this.add(clearQueuesButton);
        // rück
        c.gridx = 7;
        c.gridy = 3;
        ly.setConstraints(rückgButton, c);
        this.add(rückgButton);
        // vrwrts
        c.gridx = 8;
        ly.setConstraints(vwrtsButton, c);
        this.add(vwrtsButton);
    }


    private void initOptionsAndButtonDesign() {
        // Aufbauen der KoeffAuswahl
        this.setBackground(parent.opt.backGroundColor);
        koeffsStrings  = new String[10];
        koeffsStrings[0] = "Koeffizienten aus Z";
        for (int i=1;i< Math.min(koeffsStrings.length,PRIMENUMBERS.length);i++) 
            koeffsStrings[i] = "Koeffizienten aus Z/" + PRIMENUMBERS[(i-1)];
        //
        koeffChoosingBox = new JComboBox<>(koeffsStrings);

        currentMarkedField.setEditable(false);

        Font cf = parent.opt.ausgabeBereichButtonFont;
        jinnerList.setFont(parent.opt.ausgabeBereichFont);
        toggleVertexEqBox.setFont(cf);
        koeffChoosingBox.setFont(parent.opt.ausgabeBereichFont);
        homBerechnenButton.setFont(cf);
        rückgButton.setFont(cf);
        vwrtsButton.setFont(cf);
        showKomplexData.setFont(cf);
        currentMarkedField.setFont(parent.opt.komplexReprFont);
        buildSimplexButton.setFont(cf);
        removeVerticesButton.setFont(cf);
        dynamicGlueButton.setFont(cf);
        clearQueuesButton.setFont(cf);

        Color bg = parent.opt.backGroundColor;
        Color fg = parent.opt.ausgabeBereichTextColor;
        jinnerList          .setBackground(bg);
        toggleVertexEqBox   .setBackground(bg);
        koeffChoosingBox    .setBackground(bg);
        homBerechnenButton  .setBackground(bg);
        currentMarkedField  .setBackground(bg);
        buildSimplexButton  .setBackground(bg);
        removeVerticesButton.setBackground(bg);
        dynamicGlueButton   .setBackground(bg);
        clearQueuesButton   .setBackground(bg);
        rückgButton         .setBackground(bg);
        vwrtsButton         .setBackground(bg);
        showKomplexData     .setBackground(bg);
        //
        jinnerList          .setForeground(fg);
        toggleVertexEqBox   .setForeground(fg);
        koeffChoosingBox    .setForeground(fg);
        homBerechnenButton  .setForeground(fg);
        currentMarkedField  .setForeground(fg);
        buildSimplexButton  .setForeground(fg);
        removeVerticesButton.setForeground(fg);
        dynamicGlueButton   .setForeground(fg);
        clearQueuesButton   .setForeground(fg);
        rückgButton         .setForeground(fg);
        vwrtsButton         .setForeground(fg);
        showKomplexData     .setForeground(fg);

        
        homScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {  
                if (!scrollbar_brm.getValueIsAdjusting()) {
                    if (scrollbar_wasAtBottom)
                    scrollbar_brm.setValue(scrollbar_brm.getMaximum());
                 } else
                 scrollbar_wasAtBottom = ((scrollbar_brm.getValue() + scrollbar_brm.getExtent()) == scrollbar_brm.getMaximum());
          
            }
        });

    }

    // For Scroll Pane
    private BoundedRangeModel scrollbar_brm = homScrollPane.getVerticalScrollBar().getModel();
    private boolean scrollbar_wasAtBottom = true;


    private static int[] PRIMENUMBERS = {2,3,5,7,11,13,17,19,23,29,31};

    Ausgabebereich(GUI_Main par) {
        this.parent = par;

        initOptionsAndButtonDesign();

        initLayout();

        Zeichenbereich z = parent.zeichenbereich;
        // Add Listenener
        buildSimplexButton.addActionListener(z.getActionMap().get("buildSimplex"));
        removeVerticesButton.addActionListener(z.getActionMap().get("removeVertex"));
        dynamicGlueButton.addActionListener(z.getActionMap().get("pushStacksOrGlue"));
        clearQueuesButton.addActionListener(z.getActionMap().get("popAllQueues"));
        rückgButton.addActionListener(z.getActionMap().get("undoDeltaChanging"));
        vwrtsButton.addActionListener(z.getActionMap().get("redoDeltaChanging"));
        toggleVertexEqBox.setSelected(parent.opt.showVertexEqClass);
        toggleVertexEqBox.addActionListener(e -> {
            parent.opt.showVertexEqClass = !parent.opt.showVertexEqClass;
            z.repaint();
        });

        homBerechnenButton.addActionListener(e -> {
            int koeffidx = koeffChoosingBox.getSelectedIndex();
            if (koeffidx == 0) {
                for (int i=0;i<=parent.deltaKomplex.dimension();i++) {
                    String wh = "H" +i +"(X) = ";
                    innerList.addElement(wh+parent.deltaKomplex.HomologyGroup(i,new Z(0)).toString());
                }
            }
            else {
                for (int i=0;i<=parent.deltaKomplex.dimension();i++) {
                    String wh = "H" + i +"(X,Z/"+ PRIMENUMBERS[ (koeffidx-1) ]+") = ";
                    innerList.addElement(wh+parent.deltaKomplex.HomologyGroup(i,new ZmodN(0, PRIMENUMBERS[ (koeffidx-1) ])).toString());
                }
            }
            innerList.addElement("-------------------------------------");
            innerList.addElement("-------------------------------------");
            
            this.repaint();
            
        });

        showKomplexData.addActionListener(e -> {
            int koeffidx = koeffChoosingBox.getSelectedIndex();
            java.util.List<String> info = null;
            if (koeffidx == 0) {
                info = parent.deltaKomplex.getKomplexInfoString(new Z(0));
                for (String s: info) 
                    innerList.addElement(s);
                
            }
            else {
                info = parent.deltaKomplex.getKomplexInfoString(new ZmodN(0,PRIMENUMBERS[ (koeffidx-1) ]));
                for (String s: info) 
                    innerList.addElement(s);
                
            }
            innerList.addElement("-------------------------------------");
            innerList.addElement("-------------------------------------");
            
            this.repaint();
        });
    }

}
