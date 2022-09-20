package GUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.*;
import Deltas.DeltaComplex;
import Deltas.VisualSimplex;

import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class Zeichenbereich extends JPanel {

    private GUI_Main parent = null;

    private int maxVertex = 0;

    private Deque<VisualVertex> vertexQueue = new LinkedList<>();

    private Deque<VisualVertex> glueIntermediateQueue = new LinkedList<>();

    private SortedSet<VisualVertex> vertexZyklus = new TreeSet<>();

    private int vertexRadius;

    private Map<VisualSimplex<VisualVertex>,String> eqBezMap = new HashMap<>();


    /**
     * Teure Funktion die die Stringbezeichner aktualisiert.
     */
    private void updateEqBezMap() {
        this.eqBezMap = EqStringAssigner.eqSolver(parent.deltaKomplex.getNonTrivialMappingToEqClass());
    }

    /**
     * Vereifachte Fkt zum Zugriff auf alle Vertizes.
     * 
     * @return
     */
    private SortedSet<VisualVertex> getVertices() {
        return parent.deltaKomplex.getVertices(null);
    }

    /**
     * @param x
     * @param y
     * @return den Vertex auf der geklickten Position. null, falls pos. nicht
     *         zugeordnnet werden kann.
     */
    private VisualVertex getVertexOnPos(int x, int y) {
        for (VisualVertex v : getVertices()) {
            if (Nutzfkten.distance(v.posx, x, v.posy, y) < vertexRadius) {
                return v;
            }
        }
        return null;
    }

    // mit akteller Graphic Setzung, nutzt für Str opt
    private  void drawVertexCircle(Graphics g, VisualVertex v) {
        g.fillOval(v.posx - vertexRadius, v.posy - vertexRadius,
        vertexRadius * 2 - 1, vertexRadius * 2 - 1);

        String veq = this.eqBezMap.get(parent.deltaKomplex.transformSingIntoVisualSimpl(v));
        if (veq == null) veq = Integer.toString(v.vertexID);
        else veq = veq + "("+ Integer.toString(v.vertexID)+")";
        //TODO Add toggle Option
        //TODO But not here, show verticeodering
        Color oldCol = g.getColor();
        g.setColor(parent.opt.vertexBezColor);
        g.drawString(veq,
            v.posx + (int) (1.2 * vertexRadius), v.posy + (int) (1.2 * vertexRadius));
        g.setColor(oldCol);    
    }


    /**
     * Malt bunte gerichtete Kanten, Vertices entsprechend des Iterators der Kollektion geordnet.
     * @param g
     * @param of kollektion von Vertices, !!! höchstens 3 Stück !!!
     * @param vertexCol
     * @param edgeCol
     * @param ifSimplCol Farbe des gemalten 2-Simplizes, falls of einen solchen bildet
     */
    private void drawEdgesOf(Graphics g,Collection<VisualVertex> of,Color vertexCol,Color edgeCol,Color ifSimplCol) {
        // Jeden Vertex malen
        g.setColor(vertexCol);
        for (VisualVertex v : of) {
            drawVertexCircle(g, v);
        }

        if (of.size() == 1) return;
        else if (of.size() == 2) {
            if (parent.deltaKomplex.contains(of)) { // malen der Kante
                int[] xs  = new int[2];
                int[] ys  = new int[2];
                int c = 0;
                for (VisualVertex v : of) {
                    xs[c] = v.posx;
                    ys[c] = v.posy;
                    c++;
                }
                Nutzfkten.drawSignedArrow(xs[0], xs[1], ys[0], ys[1],
                 "",  vertexRadius / 2, g, (int) (vertexRadius * 2),
                 edgeCol, edgeCol);

            }
        }
        else if (of.size() == 3) { // Für 2-Simplizes nutzt man ifSimplCol
            
            if (parent.deltaKomplex.contains(of)) {
                g.setColor(ifSimplCol);
                int[] xs = of.stream().mapToInt(vv -> vv.posx).toArray();
                int[] ys = of.stream().mapToInt(vv -> vv.posy).toArray();
                // Dreieck malen
                g.fillPolygon(xs, ys, 3);
                VisualSimplex<VisualVertex> ofAsSimpl = parent.deltaKomplex.transformColIntoVisualSimpl(of);
                String bez = this.eqBezMap.get(ofAsSimpl);
                if (bez == null) bez = "";
                g.drawString(bez, (xs[0] + xs[1] + xs[2]) / 3, (ys[0] + ys[1] + ys[2]) / 3);
            }

            // Runterbrechen aller 1-Simplizes
            ArrayList<VisualVertex> l1 = new ArrayList<>();
            ArrayList<VisualVertex> l2 = new ArrayList<>();
            ArrayList<VisualVertex> l3 = new ArrayList<>();

            int c = 0;
            for (VisualVertex v:of) {
                if (c == 0) {
                    l2.add(v);
                    l3.add(v);
                }
                else if (c == 1) {
                    l1.add(v);
                    l3.add(v);
                }
                else if (c == 2) {
                    l1.add(v);
                    l2.add(v);
                }
                c++;
            }

            drawEdgesOf(g, l1, vertexCol, edgeCol, ifSimplCol);
            drawEdgesOf(g, l2, vertexCol, edgeCol,ifSimplCol);
            drawEdgesOf(g, l3, vertexCol, edgeCol,ifSimplCol);
            
        }



    }




    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(parent.opt.zeichenbereichFont);

        DeltaComplex<VisualVertex> dc = parent.deltaKomplex;

        Graphics2D g2 = (Graphics2D) g;
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);

        g2.setTransform(at);

        //------------- Malen----------------------------------------------------------------------------
        // Malen der 2 Simplizes
        for (VisualSimplex<VisualVertex> vs : dc.getSimplizes(2)) {
            g.setColor(parent.opt.twoSimplexColorNormal);

            
            int[] xs = vs.elementStream().mapToInt(vv -> vv.posx).toArray();
            int[] ys = vs.elementStream().mapToInt(vv -> vv.posy).toArray();
            // Dreieck malen
            g.fillPolygon(xs, ys, 3);
            String bez = this.eqBezMap.get(vs);
            if (bez == null) bez = "";
            g.drawString(bez, (xs[0] + xs[1] + xs[2]) / 3, (ys[0] + ys[1] + ys[2]) / 3);
        }

        // Malen der (ungerichteten) 1 Simplizes
        for (VisualSimplex<VisualVertex> vs : dc.getSimplizes(1)) {

            int[] xs = vs.elementStream().mapToInt(vv -> vv.posx).toArray();
            int[] ys = vs.elementStream().mapToInt(vv -> vv.posy).toArray();
            Nutzfkten.drawLine(xs[0], xs[1], ys[0], ys[1], vertexRadius / 2, g, parent.opt.edgeColorNormal);
        }

        // Malen der gerichteten 1- Simplizes
        for (VisualSimplex<VisualVertex> vs : dc.getImplicitDirectedEdges()) {
            VisualVertex from = vs.first();
            VisualVertex to = vs.last();
            String signum = this.eqBezMap.get(vs);
            if (signum == null) signum = "";
            Nutzfkten.drawSignedArrow(from.posx, to.posx, from.posy, to.posy,
             signum, vertexRadius / 2, g, (int) (vertexRadius * 2),
             parent.opt.edgeColorNormal, parent.opt.zeichenbereichTextColor);
        }

        // Malen der Vertices ( mit standard Farbe)
        g.setColor(parent.opt.vertexColorNormal);
        for (VisualVertex v : getVertices()) {
            drawVertexCircle(g, v);
        }

         // Malen des GlueStacks
        drawEdgesOf(g, glueIntermediateQueue, parent.opt.vertexColorGlueStacked,
            parent.opt.edgeColorGlueStacked, parent.opt.twoSimplexColorGlueStacked);


        //TODO  Malen der Zyklus Kanten

       
        // Malen der Vertex-Queue Kanten
        drawEdgesOf(g, vertexQueue, parent.opt.vertexColorMarked, parent.opt.edgeColorMarked,parent.opt.twoSimplexColorMarked);


        // -------MalenENDE---------------------------------------------------------------*/
        parent.komplexRepr.repaint();
    }

    // --------------------------------------------------------------------------------------------

    private int mouseClickTracker_x;
    private int mouseClickTracker_y;
    private VisualVertex draggedVertex;

    private double scale = 1.0;
    final double ZOOM_FACTOR = 1.2;

    private void correctMouseZoom(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        double scaleInv = 1 / scale;
        e.translatePoint((int) (-x + scaleInv * x), (int) (-y + scaleInv * y));
    }

    private void EVENT_addVertexToQueue(MouseEvent e) {
        VisualVertex v = getVertexOnPos(e.getX(), e.getY());
        if (v != null) {
            if (vertexQueue.contains(v)) { // In dem Fall entmarkieren
                vertexQueue.remove(v);
                repaint();
            } else { // markieren
                vertexQueue.add(v);
                if (vertexQueue.size() > 3)
                    vertexQueue.poll();
                repaint();
            }
        }
    }

    private void EVENT_addVertex(MouseEvent e) {
        VisualVertex v = getVertexOnPos(e.getX(), e.getY());
        if (v == null) {
            v = new VisualVertex(maxVertex, e.getX(), e.getY());
            maxVertex += 1;
            parent.deltaKomplex.addVertex(v);
            parent.komplexRepr.repaint();
            repaint();
        }
    }

    /**
     * Action die das bauen eines Simplex aus gewählten Vertices übernimmt.
     */
    final class BuildSimplexAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (vertexQueue.size() > 1) {
                parent.deltaKomplex.addSimplex(vertexQueue);
                // Alle bis auf den Letzten Vertice entmarkieren
                while (vertexQueue.size() > 1) vertexQueue.poll();
                repaint();
            }

        }

    }

    /**
    * Im Gegensatz zur 1. ten Version wird nur die Verklebung 2er Simplizes unterstützt.
    * (Mehrfaches verkleben dieser ist aber nach wie vor möglich) 
    */
    final class AddQueueToGlueStackAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!vertexQueue.isEmpty() && parent.deltaKomplex.contains(vertexQueue)) {
                glueIntermediateQueue.clear();
                glueIntermediateQueue.addAll(vertexQueue);
                vertexQueue.clear();

                repaint();
            }
            
        }

    }


    /**
     * Verklebt den aktuellen gluestack mit der vertexQueue
     */
    final class PushGlueStacksAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (vertexQueue.isEmpty()) return;
            if (vertexQueue.size() != glueIntermediateQueue.size()) return;
            if (vertexQueue.containsAll(glueIntermediateQueue)) return;

            vertexZyklus = parent.deltaKomplex.glue(vertexQueue, glueIntermediateQueue);
            
            vertexQueue.clear();
            glueIntermediateQueue.clear();
            // Bezeichner aktualisieren
            updateEqBezMap();
            parent.komplexRepr.repaint();
            repaint();
        }

    }

    private void init_keyBindings() {
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "buildSimplex");
        getActionMap().put("buildSimplex", new BuildSimplexAction());

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0), "addQueueToGlueStack");
        getActionMap().put("addQueueToGlueStack", new AddQueueToGlueStackAction());
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pushGlueStacks");
        getActionMap().put("pushGlueStacks", new PushGlueStacksAction());
    }
    // --------------------------------------------------------------------------------------------

    private void initOptions() {
        this.setBackground(parent.opt.backGroundColor);
        this.setFont(parent.opt.zeichenbereichFont);
        this.vertexRadius = parent.opt.vertexRadius;
    }

    Zeichenbereich(GUI_Main par) {
        this.parent = par;

        initOptions();

        init_keyBindings();

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                correctMouseZoom(e);
                int diffx = e.getX() - mouseClickTracker_x;
                int diffy = e.getY() - mouseClickTracker_y;
                if (draggedVertex == null) {
                    // Correct all Vertice Positions
                    for (VisualVertex v : getVertices()) {
                        v.posx += diffx;
                        v.posy += diffy;
                    }
                } else {
                    // Correct only draggedVertex pos
                    draggedVertex.posx += diffx;
                    draggedVertex.posy += diffy;
                }
                mouseClickTracker_x = e.getX();
                mouseClickTracker_y = e.getY();
                repaint();
            }

        });

        addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                double factor = Math.pow(ZOOM_FACTOR, e.getWheelRotation());
                double factorInv = Math.pow(ZOOM_FACTOR, -e.getWheelRotation());

                double nscale = scale * factor;
                if (nscale > 0.1 && nscale < 10.0) {
                    scale = nscale;
                    for (VisualVertex v : getVertices()) {
                        v.posx *= factorInv;
                        v.posy *= factorInv;
                    }

                }

                repaint();
            }

        });

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                requestFocusInWindow();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearFocusOwner();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                correctMouseZoom(e);
                // EVENT add Vertex to Queue
                if (e.getButton() == MouseEvent.BUTTON3) {
                    EVENT_addVertexToQueue(e);
                }
                // EVENT add Vertex
                if (e.getButton() == MouseEvent.BUTTON1) {
                    EVENT_addVertex(e);
                }
                // EVENT addVertex And Mark it
                if (e.getButton() == MouseEvent.BUTTON2) {
                    EVENT_addVertex(e);
                    EVENT_addVertexToQueue(e);
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
                correctMouseZoom(e);
                mouseClickTracker_x = e.getX();
                mouseClickTracker_y = e.getY();

                draggedVertex = getVertexOnPos(e.getX(), e.getY());
            }

        });

    }

}
