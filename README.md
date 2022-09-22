# SimplicialHomology

In diesem Projekt befindet sich ein Programm zur Berechnung der ganzzahligen simplizialen Homologie von $\Delta$-Komplexen. Der Algorithmus ist von https://eric-bunch.github.io/blog/calculating_homology_of_simplicial_complex entnommen. Die JARs wurden in Java 8 erzeugt.

Das Jar File SimplicialHomology.jar ist nur noch aus Dokumentationzwecken hier. Für die Berechnung der ganzzahligen simplizialen Homologie von $\Delta$-Komplexen sollte man sich die Datei VisualDeltaComplex.jar annschauen. <br>

In der ausführbaren Jar Datei "VisualDeltaComplex.jar" befindet sich ein GUI zur Eingabe von 2-dimensionalen $\Delta$-Komplexen. Dieses GUI befindet sich noch im Aufbau, kann aber schon genutzt werden. Mit dem GUI kann man die Homologie eines grafisch erstelltem 2-dimensionalen $\Delta$-Komplex errechnen. Der größte Unteschied zur vorheriger Version ist die automatische Vertex-Ordnung.
Das GUI unterteilt sich grob in 3 Teile:<br>
**Links**  befindet sich eine Darstellung des aktuellen $\Delta$-Komplex. Die Ordnung der Vertices ist direkt als erstes gegeben. (Das Programm erstellt selbst eine Ordnung der Verte-Menge). Darunter werden die im Komplex vorhandenen Simplizes angezeigt, wobei Seiten nicht angezeigt werden, aber durchaus im Komplex vorhanden sind. Darunter werden nichttriviale Äquivalenzklassen der Simplizes angezeigt; "=(1)(2)" bedeutet, dass die Vertices 1 und 2 äquivalent sind.<br>
**Rechts** befindet sich der grafische Zeichenbereich. Hier kann man mit Linksklick einen Vertex setzten und mit Rechtsklick markieren. Markiert man 2 Vertices so wird gemäß der Reihenfolge wie diese markiert wurden ein Pfeil gemalt. Wenn man nun verklebt so wird die von dem Pfeil induzierte Ordnung (Ursprung < Ziel) dem Komplex übermittelt, der dann, falls nötig, die Ordnung auf der Vertex-Menge erneuert. <br>
**Unten** befindet sich Knöpfe sowie der Ausgabeder Homologiegruppen. Hier ist eine kurze Beschreibung der Funktionen: <br>
1. **Vertex Äquivalenzklassen anzeigen** Box. Bei Deaktivierung werden äquivalente Vertices nicht besonders beschriftet. Die Äquivalenzklassen sind aus der Liste Links nach wie vor entnehmbar.
2. **Koeffizienten aus...**.Hier kann man auswählen, mit welchen Koeffizienten die Homologiegruppen gebildet werden sollen.
3. **Homologie berechnen**. Klar
4. **Simplex erzeugen** bildet aus den markierten (grünen) Vertices einen Simplex. Dabei ist die Reihenfolge der Markierung unrelevant.
5. **Vertices löschen** löscht die markierten Vertices. ACHTUNG: Verklebungen werden evtl. nicht gelöst, man beachtet die Ausgabe der Äquivalenzklassen!
6. **Verkleben mit...** Merkt sich den markierten Simplex (dieser ist nun gelb). Wählt man nun einen anderen Simplex kann man diese mit erneutem drücken des Buttons verkleben. War die Verklebung erfolgreich, so wird der zuletzt markierte Simplex gelb und man kann weiter mit diesen Simplizes verkleben. Das Drücken des **Auswahl aufheben** Buttons entfernt alle Markierungen. Ist eine Verklebung nicht möglich, da diese einen Zyklus induzieren würde, so wird der Zyklus in rot darsgestellt. Das Markieren eines Vertex entfernt diese rote Darstellung wieder.

Befindet sich die Maus im Zeichenbereich, so hat man auch folgende Tastenkürzel:
1. Leertaste -> Simplex erzeugen
2. Lösch Taste (Backspace) -> Vertices löschen
3. Enter -> Verkleben mit...
4. c -> Auswahl aufheben
5. STRG + Z -> letzte Komplex-Änderung rückgängig machen.

Das Speicherformat von Dateien ist ".vdlts". Im Ordner VDC_Beispiele sind 2 Beispiele dafür.
