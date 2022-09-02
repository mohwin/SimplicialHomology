import Deltas.*;
import static Deltas.Simplex.s;


public class Main {


    /*
     * Dies kann in der Konsole ausgeführt werden
     * Konsole in <Vorlage> Ordner öffnen.
     * Programm kompilieren: Eingeben von <javac -classpath "SimplicialHomology.jar" Main.java>
     * 
     * Programm ausführen (Windows): Eingeben von <java -classpath "SimplicialHomology.jar;." Main>
     * Wichtig: Beim  Ausführen auf "SimplicialHomology.jar;." achten! Bei Linux-Systemen verwendet man anstatt
     * des Semicolons den Doppelpunkt, also:
     * Programm ausführen (Linux): Eingeben von <java -classpath "SimplicialHomology.jar:." Main>
     */

    public static void main(String[] args) {
        /*
         * Hier kann man einen DeltaComplex erstellen und die Homologie berechnen.
         * Wichtig ist die Ausgabe mit System.out.println(...) !
         */

        DeltaComplex D = new DeltaComplex();

        D.addSimplex(s(1,2,3));

        // Ausgabe von H_1
        System.out.println(D.AllHomologies());
    }
}