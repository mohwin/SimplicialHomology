package GUI;

import java.awt.Color;
import java.awt.*;
import javax.swing.*;

import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import Deltas.Z;
import Deltas.ZmodN;

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

    // zeichenbereich Marked Vertices Line
    JTextField currentMarkedField = new JTextField("Rechtsklick zum markieren");
    // zeichenbereich ctrl buttons (clr fkt zu menubar geschoben)
    JButton buildSimplexButton = new JButton("Simplex erzeugen");
    JButton removeVerticesButton = new JButton("Vertices löschen");
    JButton dynamicGlueButton = new JButton("Verkleben mit...");
    JButton clearQueuesButton = new JButton("Auswahl aufheben");



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
        //homsBer
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 2;
        ly.setConstraints(homBerechnenButton, c);
        this.add(homBerechnenButton);
        //HomsAusg
        c.weightx = 1.0;
        c.weighty = 1.0;
        
        c.gridy = 0;
        c.gridx = 1;
        c.gridwidth = 6;
        c.gridheight = 3;
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
        
    }


    private void initOptionsAndButtonDesign() {
        // Aufbauen der KoeffAuswahl
        this.setBackground(parent.opt.backGroundColor);
        koeffsStrings  = new String[10];
        koeffsStrings[0] = "Koeffizienten aus Z";
        for (int i=1;i<koeffsStrings.length;i++) 
            koeffsStrings[i] = "Koeffizienten aus Z/" + (i+1);
        //
        koeffChoosingBox = new JComboBox<>(koeffsStrings);

        currentMarkedField.setEditable(false);

        Font cf = parent.opt.ausgabeBereichButtonFont;
        jinnerList.setFont(parent.opt.ausgabeBereichFont);
        toggleVertexEqBox.setFont(cf);
        koeffChoosingBox.setFont(parent.opt.ausgabeBereichFont);
        homBerechnenButton.setFont(cf);
        currentMarkedField.setFont(parent.opt.komplexReprFont);
        buildSimplexButton.setFont(cf);
        removeVerticesButton.setFont(cf);
        dynamicGlueButton.setFont(cf);
        clearQueuesButton.setFont(cf);

        Color bg = parent.opt.backGroundColor;
        Color fg = parent.opt.ausgabeBereichTextColor;
        jinnerList.setBackground(bg);
        toggleVertexEqBox   .setBackground(bg);
        koeffChoosingBox    .setBackground(bg);
        homBerechnenButton  .setBackground(bg);
        currentMarkedField  .setBackground(bg);
        buildSimplexButton  .setBackground(bg);
        removeVerticesButton.setBackground(bg);
        dynamicGlueButton   .setBackground(bg);
        clearQueuesButton   .setBackground(bg);
        //
        jinnerList.setForeground(fg);
        toggleVertexEqBox   .setForeground(fg);
        koeffChoosingBox    .setForeground(fg);
        homBerechnenButton  .setForeground(fg);
        currentMarkedField  .setForeground(fg);
        buildSimplexButton  .setForeground(fg);
        removeVerticesButton.setForeground(fg);
        dynamicGlueButton   .setForeground(fg);
        clearQueuesButton   .setForeground(fg);

        homScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {  
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
            }
        });

    }

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

        toggleVertexEqBox.setSelected(parent.opt.showVertexEqClass);
        toggleVertexEqBox.addActionListener(e -> {
            parent.opt.showVertexEqClass = !parent.opt.showVertexEqClass;
            z.repaint();
        });

        homBerechnenButton.addActionListener(e -> {
            int koeffidx = koeffChoosingBox.getSelectedIndex();
            innerList.addElement("------------------------------");
            if (koeffidx == 0) {
                for (int i=0;i<=parent.deltaKomplex.dimension();i++) {
                    String wh = "H" +i +"(X) = ";
                    innerList.addElement(wh+parent.deltaKomplex.HomologyGroup(i,new Z(0)).toString());
                }
            }
            else {
                for (int i=0;i<=parent.deltaKomplex.dimension();i++) {
                    String wh = "H" + i +"(X,Z/"+ (koeffidx+1) +") = ";
                    innerList.addElement(wh+parent.deltaKomplex.HomologyGroup(i,new ZmodN(0, koeffidx+1)).toString());
                }
            }

        });
    }

}
