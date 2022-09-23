package GUI;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import Deltas.DeltaComplex;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.SortedSet;
import java.util.stream.Collectors;


public class GUI_Main extends JFrame {
    
    Options opt = new Options();

    DeltaComplex<VisualVertex> deltaKomplex = new DeltaComplex<>();

    Zeichenbereich zeichenbereich = new Zeichenbereich(this);

    Ausgabebereich ausgabebereich = new Ausgabebereich(this);

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

        setTitle("Berechnung der Homologie von "+ Nutzfkten.DELTA + "-Komplexen (unf alpha Version)");
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



        JMenuItem cls = new JMenuItem("Neu");
        JMenuItem speichern = new JMenuItem("Speichern unter");
        JMenuItem laden = new JMenuItem("Öffnen");
        cls.addActionListener(zeichenbereich.getActionMap().get("clearBoard"));
        
        FileFilter filter = new FileFilter() {


            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                return f.toString().endsWith(".vdlts");
            }

            @Override
            public String getDescription() {
                return Nutzfkten.DELTA + "-Komplex Speicherdatei";
            }
            
        };
        speichern.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(filter);
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.exists()) 
                    try{
                        file.createNewFile();
                    }
                    catch (Exception err){
                        System.err.println("Ein fehler beim Neu Erstellend der Speicherdatei ist aufgetreten");
                        System.exit(-1);
                    }
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    zeichenbereich.saveCurrMemento(os);
                    os.close();
                }
                catch (Exception err) {
                    System.err.println(err.getMessage());
                    System.exit(-1);
                }
            }

        });
        laden.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream is = new ObjectInputStream(fis);
                    zeichenbereich.loadCompl(is);
                    is.close();
                }
                catch (Exception err) {
                    System.err.println(err.getMessage());
                    System.exit(-1);
                }
            }
        });

        Datei.add(cls);
        Datei.add(speichern);
        Datei.add(laden);

        menubar.add(Datei);



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
