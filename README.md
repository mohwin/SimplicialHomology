# SimplicialHomology

In diesem Projekt befindet sich ein Programm zur Berechnung der ganzzahligen simplizialen Homologie von $\Delta$-Komplexen. Der Algorithmus ist von https://eric-bunch.github.io/blog/calculating_homology_of_simplicial_complex entnommen. Die JARs wurden in Java 8 erzeugt.

Das Projekt befindet sich noch im Aufbau und ist unvollständig.

Das GUI ist noch nicht fertig, aber  man kann es zum Teil bereits nutzen.

Der Zeichenbereich ist das Rechteck in der oberen rechten Ecke. Die Eingabe wird im Bereich Links daneben nochmal angezeigt. Darunter sind Buttons für die Steuerung und das Ausgabefeld der Homologiegruppen.

Mit der Maus kann man im **Zeichenbereich**:
1) Linksklick   ->  Setzt einen Vertex an der geklickten Stelle, falls an dieser Stelle nicht bereits ein Vertex war.
2) Rechtsklick  ->  Markiert oder entmarkiert den Vertex an der geklickten Stelle. Dieser wird dann grün angezeigt. Es können maximal 3 Vertizes gleichzeitig markiert     sein. Wird diese Grenze überschritten, wird der zuerst markierte Vertex entmarkiert.
3) Mausradklick ->  Setzt und markiert einen Vertex an der geklickten Stelle.
4) "Ziehen"     -> Indem man die LinksklickTaste der Maus gedrückt hält, kann man den Komplex im Zeichenbereich bewegen. Falls ein Vertex auf der Position war, an der man begonnen hat die Maustaste zu halten, so wird lediglich der Vertex bewegt.
5) Mausradrollen -> Zoomen.

Die **Buttons** unten haben folgende Effekte:
1) Simplex erzeugen ->  Fasst die markierten Vertices zu einem Simplex zusammen. 1-Simplizes werden als Kanten, 2-Simplizes als Flächen angezeigt. Entmarkiert alle Vertices, bis auf den der zuletzt gesetzt wurde (dieser ist dann immer noch grün).
2) Vertices löschen ->  Noch nicht implementiert. :(
3) Verkleben mit... ->  Die markierten Vertices, die zusammen einen 0- 1- oder 2-Simplex bilden, werden als Verklebeargument "gemerkt", dabei ändert sich die Farbe des Simplex. Danach kann man einen anderen Simplex der gleichen Dimension durch seine Vertexmenge markieren. Wird dann wieder der Verkleben mit... Button gedrückt, so wird auch dieser als Verklebeargument gemerkt. Auf diese Weise kann man mehrere Simplizes merken, praktisch sind dann die folgenden 2 Buttons:
4) Verklebungen bestätigen  ->  Verklebt alle durch den Verkleben mit.. Button gemerkten Simplizes miteinandner. Verklebte Kanten sind mit geichen Kleinbuchstaben, Verklebte Flächen durch gleiche Großbuchstaben gekennzeichnet, Verklebte Vertices werden nicht besonders gekennzeichnet, Verklebungen kann man der Liste Links entnehmen.
5) Verklebung abbrechen ->  Bricht die Auswahl durch den Button Verkleben mit... ab.
6) Zurücksetzen -> Löscht alle eingegebenen Simplizes und Vertices.

Ist die Maus im Zeichenbereich, so kann man mit Shortcuts auf diese Funktionen zugreifen:
1) Simplex erzeugen -> Leertaste
2) Verkleben mit... -> Taste v
3) Verklebungen bestätigen  -> Enter
4) Verklebung abbrechen -> Taste a
5) Zurücksetzen -> SHIFT + Taste c (NICHT Ctrl + c, wie kopieren)

Wichtig ist, dass das Programm zu Zeit die Verklebungen mit a,b,c...,z bzw. mit A,B,C...,Z darstellen kann. Werden mehr Buchstaben gebraucht so produziert das Programm eine fehlerbehaftete bildliche Repräsentation, es sollte links auf die Eingabeliste geachtet werden.

Die **Eingabeliste** links vom Zeichenbereich gibt die Eingaben an, die für einen Deltas.DeltaKomplex (aus dem SimplicialHomology JAR) gemacht werden. Man kann sie wie folgt übersetzen:
+(a,b)      entspricht addSimplex(s(a,b))
=(a,b)(c,d) entspricht glue(s(a,b),s(c,d))
wie bei der Methode addSimplex(), werden alle UnterSeiten automatisch miteingefügt.

In dem kleinen Textfeld unterhalb dieser Liste kann man auch selbst Eingaben machen, um so auch höher dimensionalere Simplizes eingeben zu können. Diese Eingabe ist noch in Arbeit, aber funktionstüchtig. Gibt man Beispielsweise "+ (1,2,3,4)" ein, so wird dieser Simplex korrekt hinzugefügt. Man kann auch Einträge entfernen, um zum Beispiel die Einträge "+ (1,2,3)" und "=(3,4)(5,6)" zu entfernen nimmt kman diese und ersetz das "+" oder "=" mit einem "-": "-(1,2,3)" und "-(3,4)(5,6)".
Wichtig zu beachten ist sind folgende Aspekte:
1) Die Eingaben auf dieser Weise beeinflussen das Zeichenfeld nicht. Es wird davon abgeraten die Graphische als auch die Textmethode **gleichzeitig** zu verwenden.
2) Die Texteingabe ist noch unvollständig. Wird ein Komplex eingegebn, dessen Vertizes nicht natürlich geordnet sind, oder der aus anderen Gründen nicht gebildet werden kann, so gibt das Programm nur eine kurze, nichts sagende Fehlermeldung aus.

Um die Homologiegruppen berechnen zu lassen hat man obigen Links die Menü-Leiste mit nur einem Eintrag. darin ist ein Panel "Homologie berechnen". Es werden alle 
nichttrivialen **ganzzahligen** Homologiegruppen der Eingabe berechnet. Die Homologiegruppen werden links neben den Buttons ausgegeben. Die Zahlen oberhalb und unterhalb der Gruppen spiegeln einfach nur wieder, wie oft das Panel "Homologie berechnen" verwendet wurde.



