package Deltas;

public interface RecordHIB <T extends RecordHIB<T>> {
    
    /**
     * Gibt eine Null zurück.
     * @return
     */
    public T Nullwert();

    /**
     * Gibt this*factor zurück
     * @return
     */
    public T times(T factor);

    public T plus(T summand);


    /**
     * Muss ein Array der Läge 3 zurückgeben, in dem bei 0 der ggT, bei 1 der linke Bez. Koeff und bei 2 der rechte Koeff. steht.
     * lWert ist this, rechter Wert ist b
     * @param b
     * @return
     */
    public T[] bezout( T b);


    /**
     * true, iff this | other
     * @param other
     * @return
     */
    public boolean divides(T other);


    /**
     * -this
     * @return
     */
    public T addiditiveInvers();

    /**
     * this / divisor. Es ist anzunehmen, dass dies Aufgeht.
     * @param divisor
     * @return
     */
    public T divide(T divisor);


    /**
     * true iff this eine Einheit
     * @return
     */
    public boolean isUnit();


    /**
     * Die Eins im Ring
     * @return
     */
    public T EinsWert();


    /**
     * wie das GruppenSymbol als String dargestellt werden soll. Z.B. bei ganzn Zahlen Z
     * @return
     */
    public String GroupRepr();


}
