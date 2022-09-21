package GUI;


import java.awt.*;


public class Options {
    
    Point mainWindowSize = new Point(1400,900);

    Color backGroundColor = Color.black;

    // Für den Komplex Repr. Bereich-------------------------------------------------------------
    Font komplexReprFont = new Font("Arial",Font.BOLD,24);
    Color komplexReprTextColor = Color.WHITE;

    // Für den ZeichenBereich--------------------------------------------------------------------
    Font zeichenbereichFont = new Font("Comic Sans MS", Font.BOLD, 24);
    // For eqClass Bez (Edge twoSimplex)
    Color zeichenbereichTextColor = Color.WHITE;


    int vertexRadius = 10; 


    boolean showVertexEqClass = true;
    Color vertexColorNormal = Color.gray;
    Color vertexBezColor = Color.WHITE;
    Color edgeColorNormal = Color.gray;
    Color twoSimplexColorNormal = Color.darkGray;

    Color vertexColorMarked = Color.green;
    Color edgeColorMarked = Color.green;
    Color twoSimplexColorMarked = Color.green.darker();

    Color vertexColorWarning = Color.red;
    Color edgeColorWarning = Color.red;

    Color vertexColorGlueStacked = Color.YELLOW;
    Color edgeColorGlueStacked = Color.YELLOW;
    Color twoSimplexColorGlueStacked = Color.yellow.darker();


    // Für den Ausgabebereich----------------------------------------------------------------
    Font ausgabeBereichFont = new Font("Arial",Font.BOLD,24);
    Font ausgabeBereichButtonFont = new Font("Arial",Font.BOLD,12);
    Color ausgabeBereichTextColor = Color.WHITE;



}
