package GUI;

import java.awt.*;

import javax.swing.*;

public class KomplexRepr extends JPanel {

    private GUI_Main parent = null;

    private DefaultListModel<String> innerList = new DefaultListModel<>();
    private JList<String> jinnerList = new JList<String>(innerList);
    private JScrollPane kompScrollPane = new JScrollPane(jinnerList);


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        jinnerList.setFont(parent.opt.komplexReprFont);
        
        
        innerList.clear();
        parent.deltaKomplex.stringListRepr()
            .forEach( str -> innerList.addElement(str));    
    }


    private void initLayout() {
        GridBagLayout ly = new GridBagLayout();
        this.setLayout(ly);
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0; c.weighty = 1.0;

        // kompScrollPane
        ly.setConstraints(kompScrollPane, c);
        this.add(kompScrollPane);
        
    }

    private void initOptions() {
        this.setFont(parent.opt.komplexReprFont);
        jinnerList.setBackground(parent.opt.backGroundColor);
        jinnerList.setForeground(parent.opt.komplexReprTextColor);
    }

    KomplexRepr(GUI_Main par) {
        this.parent = par;
        initOptions();
        initLayout();
    }
}
