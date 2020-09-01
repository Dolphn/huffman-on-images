/**
 * Klasse ColorManager
 * 
 * Die Klasse speichert einen Wert, dessen Häufigkeit und eine Tiefe für den Baum.
 * Sonst verfügt die Klasse nur über Getter- und Settermethoden.
 * 
 */
public class ColorManager {

    //private Werte, die die Klasse verwaltet.
    int value;
    int occurrence;
    int depth;

    /**
     * Constructer für Objekte dieser Klasse
     * Die Tiefe ist standartmäßig 0.
     * Die Haufikkeit besitzt standartmäßig den Wert 0.
     * 
     * @param value
     *          Der Wert, der in dem Object gespeichert werden soll
     *  
     */
    public ColorManager(int value)
    {
        this.value = value;
        occurrence=1;
        depth = 0;
    }

    /**
     * Die übergebene Tiefe ist nur die höchste von den Sohnen des Teilbaums, in dessen Wurzel sich dieses Objekt befindet.
     * Diese Tiefe wird um 1 erhöht und diesem Objekt zugewiesen.
     * 
     * @param depth
     *          Die größte Tiefe der Söhne des Teilbaums, in dessen Wurzel sich das Objekt befindet.
     */
    public void setDepth(int depth)
    {
        this.depth = depth + 1;
    }

    /**
     * Gibt die Tiefe des Teilbaums, in dessen Wurzel sich das Objekt befindet, zurück.
     * 
     * @return Die Tiefe des Teilbaums.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Gibt den Wert des Objekts zurück
     * 
     * @return den Wert des Objeks
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Erhöht die Häufigkeit, die der Wert hat, um 1.
     */
    public void increaseOccurence()
    {
        occurrence++;
    }

    /**
     * Setzt die Häufigkeit, mit der de Wert vorkommt, auf den übergebenen Wert. Das ist zB. nötig,
     * wenn ein Objekt der Klasse die Summe der Häufigkeiten der Objekte in den Söhnen des Baumknotens, in dem
     * sich dieses Objekt befindet, annehmen soll.
     * 
     * @param die neue Häufigkeit
     */
    public void setOccurence(int occ)
    {
        occurrence = occ;
    }

    /**
     * Gibt die Häufigkeit des angenommenen Werts zurück
     * 
     * @return die Häufigkeit
     */
    public int getOccurence(){
        return occurrence;
    }
    
}