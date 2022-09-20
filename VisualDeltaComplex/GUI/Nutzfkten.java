package GUI;

import java.awt.*;

public class Nutzfkten {
    private Nutzfkten() {};


    public final static char DELTA  = 'Δ';
    public final static String SUBSCRIPT_NUMBERS = "₀₁₂₃₄₅₆₇₈₉";
    public final static String UPSCRIPT_NUMBERS  = "⁰¹²³⁴⁵⁶⁷⁸⁹";
    public final static String OPLUS = "Ꚛ";

   


    public static double distance(int x1, int x2, int y1, int y2) {
        double v1 = x1 - x2;
        double v2 = y1 - y2;
        return Math.sqrt(v1 * v1 + v2 * v2);
    }

    
    public static void drawSignedArrow(int x1,int x2,int y1,int y2, String signum, float thickness,Graphics g,int edgeoffset,
        Color arrowCol, Color signumCol) {

        int diffx = x2 -x1;
        int diffy = y2-y1;
       

        Graphics2D g2 = (Graphics2D) g;

        Stroke oldstroke = g2.getStroke();

        Color oldCol = g.getColor();

        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(arrowCol);
        g2.drawLine(x1, y1, x2, y2);

        g2.setStroke(oldstroke);

        double norm = Math.sqrt(diffx*diffx+diffy*diffy);
        if (norm < 2*(thickness + edgeoffset)) return; // Keine Spitze;

        double shiftNorm = Math.sqrt(2)*norm;

        int ox1 =(int)( (-(2*diffx-diffy)*(thickness+edgeoffset)) / shiftNorm );
        int oy1 =(int)( (-(diffx+2*diffy)*(thickness+edgeoffset)) / shiftNorm );
        int ox2 =(int)( (-(2*diffx+diffy)*(thickness+edgeoffset)) / shiftNorm );
        int oy2 =(int)( (-(2*diffy-diffx)*(thickness+edgeoffset)) / shiftNorm );


        int orgx = x1 +diffx/2;
        int orgy = y1+ diffy/2;

        int[] trX = {orgx,orgx+ox1,orgx+ox2};
        int[] trY = {orgy,orgy+oy1,orgy+oy2};
        // draw Pfeilspitze
        g.fillPolygon(trX, trY, 3);

        // draw signum
        g.setColor(signumCol);
        g.drawString(signum,orgx+(int)(1.4*ox1),orgy+(int)(1.4*oy1));
        
        g.setColor(oldCol);

    }

    public static void drawLine(int x1,int x2,int y1,int y2,float thickness,Graphics g, Color lineCol) {
      
        Graphics2D g2 = (Graphics2D) g;

        Stroke oldstroke = g2.getStroke();

        Color oldCol = g.getColor();

        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(lineCol);
        g2.drawLine(x1, y1, x2, y2);

        g2.setStroke(oldstroke);
        g.setColor(oldCol);
    }

}
