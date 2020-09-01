import java.util.HashMap;
import java.io.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import javax.swing.JFileChooser;

/**
 * Die Klasse besitzt eine Methode zur Kompression und eine Methode zur Dekompression eines Bilds.
 * Das Kompressionsverfahren ist die Huffman-Kodierung.
 * 
 * Die Klasse besitzt auch andere Methoden, die für die Operationen notwendig sind.
 * 
 * 
 * Gestartet wird das Programm über die Main-Methode und der Benutzer wählt seine Datei aus.
 * !ACHTUNG! Nur .jpeg, .jpg und .png -dateien zur Kodierung erlaubt. Diese sind in dem beigefügten Ordner "Testimages"!
 * 
 * 
 * @author (Cornelius Engel)
 * @version (17.03.2020)
 */
public class HuffmanAlgorithm {
    /*Listen zum Encoding
     * Sie sollen Binärbaume speichern, die je ein Objekt von ColorManager besitzen
     * Für jede RGB Grundfarbe eine Liste
     */
    LinkedList<BinaryTree<ColorManager>> red = new LinkedList<>();
    LinkedList<BinaryTree<ColorManager>> green = new LinkedList<>();
    LinkedList<BinaryTree<ColorManager>> blue = new LinkedList<>();

    /*Listen zum Decoding
     * Sie sollen Binärbaume speichern, die je ein Objekt von ColorManager besitzen
     * Für jede RGB Grundfarbe eine Liste
     */
    LinkedList<BinaryTree<ColorManager>> decRed = new LinkedList<>();
    LinkedList<BinaryTree<ColorManager>> decGreen = new LinkedList<>();
    LinkedList<BinaryTree<ColorManager>> decBlue = new LinkedList<>();

    /**
     * Main-Methode. 
     * Hierüber wird ein Objekt der Klasse erstellt und dessen go()-Methode aufgerufen.
     */
    public static void main(String[] args) {
        HuffmanAlgorithm hma = new HuffmanAlgorithm();
        hma.go();
    }

    /**
     * Die Methode öffnet ein Fenster, wo die gewünschte Datei ausgewaehlt werden kann.
     * Es wird ueberprueft, ob das Dateiformat valide ist.
     * Ausserdem wird die Encoding-Methode aufgerufen, wenn es eine .jpg, .jpeg oder .png Datei ist
     * andernfalls, wenn es eine .hffmn Datei ist, wird die Decoding-Methode aufgerufen,
     * 
     * sonst wiederholt sich der Dateiauswahlvorgang.
     * 
     */
    public void go() {
        boolean retry = true; 
        while (retry) { //solange keine valide Datei ausgewählt wurde, wiederholt sich der Vorgang
            //Das Fenster wird geoffnet und der Benutzer aufgefordert, etwas auszuwaehlen
            System.out.println("Please choose your File...");
            JFileChooser jfc = new JFileChooser();
            int rueckgabeWert = jfc.showOpenDialog(null);

            String path = "";
            if (rueckgabeWert == JFileChooser.APPROVE_OPTION)
                path += jfc.getSelectedFile().getAbsolutePath(); //Wenn eine Datei ausgewählt wurde, wird ihr Pfad gespeichert

            
            String[] parts = path.split("\\."); 
            /*
             * Der String, der den Pfad speichert, wird an allen punkten gesplittet
             * 
             * Es wird im Folgenden ueberprueft, was die Dateiendung ist und jenachdem wird die Kompression, die Dekompression 
             * oder gar nichts gestartet und der User aufgefordert, eine neue Datei auszuwaehlen.
             */           
            System.out.println(parts[parts.length - 1]);
            if (parts[parts.length - 1].trim().equals("hffmn")) {
                decodeImage(path);
                retry = false;
            } else if (parts[parts.length - 1].equals("jpg") || parts[parts.length - 1].equals("jpeg") || parts[parts.length - 1].equals("bmp")
            || parts[parts.length - 1].equals("png")) {
                encodeImage(path);
                retry = false;
            } else {
                System.out.println("Wrong file format. Please choose another File, e.g. \"jpeg\" ");
            }
        }
    }

    /**
     * Methode zur Bildkompression
     * 
     * @param path
     *          der übergebene Pfad der Datei wird benötigt, um sie zu oeffnen und am Ende die komprimierte Version zu speichern.
     */
    public void encodeImage(String path) {
        //Strings, die spaeter in das Textdokument geschriben werden, werden angelegt
        String encodedColors = "";
        String header = "";
        String codeSet = "";

        LinkedList<BinaryTree<ColorManager>>[] allLists = new LinkedList[] { red, green, blue };
        /*Alle 3 Listen von oben werden in ein Array gespackt,
         * um for-loops anzuwenden
         */
        int encWidth;
        int encHeight;
        //Variablen, um die Bildmaße zu speichern, wurden angelegt.

        try {
            BufferedImage image = ImageIO.read(new File(path)); //das Bild wird als BufferdImage mit Namen image gespeichert
            encWidth = image.getTileWidth();
            encHeight = image.getTileHeight();
            // Die Maße des Bildes wurden ihren Variablen zugewiesen

            //Es wird ein Array von Color angelegt, in dem der RGB-Wert jeden Pixels gespeichert wird:
            Color[] colors = new Color[encWidth * encHeight];
            int colorNum = 0;
            for (int w = 0; w < encWidth; w++) {
                for (int h = 0; h < encHeight; h++) {
                    colors[colorNum] = new Color(image.getRGB(w, h));
                    colorNum++;
                }
            }

            //Die Bildmaße werden in Character umgewandelt und zum Header String hinzugefuegt.
            header += (char) encWidth;
            header += (char) encHeight;

            for (LinkedList<BinaryTree<ColorManager>> linlis : allLists) { //Fuer jede Grundfarbe wird Folgendes gemacht:
                for (Color c : colors) {
                    insertColorInList(linlis, c); //Alle Grundfarben aus dem Color-Array werden in ihre Liste mit der speziellen Methode eingefuegt.
                }

                for (BinaryTree<ColorManager> binaryTree : linlis) {
                    int occ = binaryTree.getContent().getOccurence();
                    binaryTree.getContent().setOccurence(occ / linlis.size());

                }//Die Haeufigkeiten der Farben werden relativ gemacht.

                for (BinaryTree<ColorManager> binaryTree : linlis) {
                    codeSet += (char) binaryTree.getContent().getValue();
                    codeSet += (char) binaryTree.getContent().getOccurence();
                }//Jeder Wert wird mit seiner Haeufigkeit zum String Codeset als Char hinzugefuegt.
                header += (char) (codeSet.length() + 5); 
                /*
                 * Am Ende ist das codeset hinter dem Header und es wird als Array gehandled. Dafür gibt die oben zum Header hinzugefuegte
                 * Zahl an, ab wo das Codeset fuer einen neuen Baum beginnt.
                 * Der Header besitzt spaeter die Groeße 5. Das Codeset fuer den roten Baum beginnt also an Index 5. Index 5 plus die Laenge des Codesets
                 * des roten Baums ergeben den Index, ab wo das codeset fuer gruen startet.
                 */

                generateTree(linlis); //in der Liste wird ein Baum aus allen Einzelknoten gebildet

                HashMap<Integer, LinkedList<Boolean>> hashMap = new HashMap<>(); //Hashmap fuer das Codebuch wird angelegt
                LinkedList<Boolean> tmpList = new LinkedList<Boolean>(); //temporaere Booleanliste fuer Traversierung
                traverse(linlis.getFirst(), hashMap, tmpList);
                //zur Hashmap werden die Farbwerte mit zugoerigem Pfad hinzugefuegt

                LinkedList<Boolean> boolList = new LinkedList<Boolean>();
                for (Color color : colors) {
                    for (Boolean b : hashMap.get(getColor(linlis, color))) { 
                        boolList.add(b);
                        // Fuer jede Farbe aus dem Color-Array wird der zugehoerige Pfad aus der Hashmap in eine große Boolean-Liste eingefuegt.

                    }
                }
                encodedColors += printChars(boolList); //Die Booleans aus der Liste werden in chars umgewandelt und dem String dafuer angehangen.

            }

            //Der Dateipfad, bzw. der Dateiname in dem Pfad wird umbenannt
            String[] parts = path.split("/");
            parts[parts.length - 1] = "(" + parts[parts.length - 1] + ")_" + "compressed.hffmn ";
            String newPath = "";

            for (int i = 0; i < parts.length; i++) {
                newPath += parts[i];
                if (i != parts.length - 1)
                    newPath += "/";
            }
            // Umbenennung fertig

            //Ein BufferedWriter und eine neue Datei zum Schreiben von Dateien wird erzeugt

            BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newPath)));
            //Der Codeset- und encodedColors-String werden an den Header String gehaengt, um nur einmal in die Datei zu schreiben
            header += codeSet;
            header += encodedColors;
            bufWriter.write(header); //Alle Informationen werden in die Datei geschrieben

            bufWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode zur Dekompression von mit Huffman koprimierten Bildern
     * 
     * @param path
     *          der übergebene Pfad der Datei wird benötigt, um sie zu oeffnen und am Ende die entkomprimierte Version zu speichern.
     */
    public void decodeImage(String path) {
        try {
            /*Alle 3 Listen von oben werden in ein Array gespackt,
             * um for-loops anzuwenden
             */
            LinkedList<BinaryTree<ColorManager>>[] decLists = new LinkedList[] { decRed, decGreen, decBlue };

            //Die Datei, zu der der Pfad gehoert, wird mit dem BufferedReader geoeffnet und der Inhalt in ein char-Array kopiert
            BufferedReader bufReader = new BufferedReader(new FileReader(path));
            File f = new File(path);
            char[] dataArr = new char[(int) f.length()]; //Das Array, in dem der ganze Inhalt gespeichert wird. 
            bufReader.read(dataArr);
            bufReader.close();

            //Die Bildmaße werden dem Array entnommen und in Variablen gespeichert.
            int decWidth = (int) dataArr[0];
            int decHeigth = (int) dataArr[1];

            /*Die Listen werden befuellt mit Binaerbaeumen, die Objekte von ColorManager besitzen. Sie bekommen einen Wert und eine Haufigkeit zugewiesen
             * Die Indizes die angeben, ab wo ein neues codeset mit allen Farbwerten und Haeufigkeiten beginnt, wurden beim Encoding im Header festgeschrieben.
             */
            for (int i = 5; i < dataArr[2]; i++) {
                decRed.add(new BinaryTree<>(new ColorManager(dataArr[i])));
                decRed.getLast().getContent().setOccurence(dataArr[++i]);
            }
            for (int i = dataArr[2]; i < dataArr[3]; i++) {
                decGreen.add(new BinaryTree<>(new ColorManager(dataArr[i])));
                decGreen.getLast().getContent().setOccurence(dataArr[++i]);
            }
            for (int i = dataArr[3]; i < dataArr[4]; i++) {
                decBlue.add(new BinaryTree<>(new ColorManager(dataArr[i])));
                decBlue.getLast().getContent().setOccurence(dataArr[++i]);
            }

            //In allen 3 Listen werden aus allen einzelnen Baeumen je ein großer erstellt
            for (LinkedList<BinaryTree<ColorManager>> linlis : decLists) {
                generateTree(linlis);
            }

            boolean[] boolArr = new boolean[(dataArr.length - dataArr[4]) * 8]; //Boolean-Array, was alle in chars verpackten Booleans beinhalten soll.
            int currentPosit = 0; //index des Array mit den chars
            for (int i = dataArr[4]; i < dataArr.length; i++) { //Alle chars, die zu booleans werden sollen, werden durchlaufen
                boolean[] tmp = fromCharToBool(dataArr[i]); //temporaeres boolean-Array, was aus einem Character erstellt wurde
                for (int bArr = 0; bArr < tmp.length && currentPosit < boolArr.length; bArr++) {
                    boolArr[currentPosit++] = tmp[bArr]; //Alle Booleans werden der Reihe nach zum großen Array hinzugefuegt
                }
            }

            //Ein Array fuer jede Grundfarbe, in dem der Wert fuer jeden Pixel gespeichert wird:
            int[] redArr = new int[decWidth * decHeigth];
            int[] greenArr = new int[decWidth * decHeigth];
            int[] blueArr = new int[decWidth * decHeigth];

            int indexCurrBool = 0; //Variable fuer die aktuelle Position im Boolean-Array

            //Die Booleans werden der Reihe nach gecheckt und so nach und nach alle Arrays mit den Farben gefuellt

            for (int i = 0; i < redArr.length; i++) {
                BinaryTree<ColorManager> b = decRed.getFirst();
                while (b.getContent().getValue() == -1) {
                    if (boolArr[indexCurrBool++])
                        b = b.getRightTree();
                    else
                        b = b.getLeftTree();
                }
                redArr[i] = b.getContent().getValue();
            }

            for (int i = 0; i < greenArr.length; i++) {
                BinaryTree<ColorManager> b = decGreen.getFirst();
                while (b.getContent().getValue() == -1) {
                    if (boolArr[indexCurrBool++])
                        b = b.getRightTree();
                    else
                        b = b.getLeftTree();
                }
                greenArr[i] = b.getContent().getValue();
            }

            for (int i = 0; i < blueArr.length; i++) {
                BinaryTree<ColorManager> b = decBlue.getFirst();
                while (b.getContent().getValue() == -1) {
                    if (boolArr[indexCurrBool++])
                        b = b.getRightTree();
                    else
                        b = b.getLeftTree();
                }
                blueArr[i] = b.getContent().getValue();
            }
            //Der Dateipfad, bzw. der Dateiname in dem Pfad wird umbenannt
            String[] parts = path.split("/");
            String[] subParts = parts[parts.length - 1].split("[/(||/)]");
            parts[parts.length - 1] = "(uncompressed)_" + subParts[1];
            String newPath = "";
            for (int i = 0; i < parts.length; i++) {
                newPath += parts[i];
                if (i != parts.length - 1)
                    newPath += "/";
            }
            //Umbenennung fertig
            File p = new File(newPath);
            BufferedImage decImage = new BufferedImage(decWidth, decHeigth, BufferedImage.TYPE_INT_RGB);
            //Eine neue leere Bilddatei wurde angelegt

            //Das Bild wird erstellt:
            int colorIndex = 0;
            for (int x = 0; x < decWidth; x++) {
                for (int y = 0; y < decHeigth; y++) { //Pixel fuer Pixel wird das Pild bearbeitet:
                    Color rgbColor = new Color(redArr[colorIndex], greenArr[colorIndex], blueArr[colorIndex++]);
                    /*Ein Color-Objekt wird erstellt und nimmt aus jedem Grundfarben-Array einen Wert an -
                     *Die Position im Array passt genau zu der aktuellen Kordinate im Bild
                     **/
                    int rgb = rgbColor.getRGB(); //Der RGB-Wert von dem Objekt wird einem int zugewiesen
                    decImage.setRGB(x, y, rgb); //Der Farbwert wird an der spezifischen Koordinate im Bild eingetragen
                }
            }

            ImageIO.write(decImage, "PNG", p); //Das Bild wird in eine Datei exportiert
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ein Integer mit einem maximalen Wert von 255 wird binaer aufgesplittet.
     * Statt 0 und 1 sind es booleans.
     * Zurückgegeben wird es in einem Array.
     * 
     * @param rest
     *          Die Groeße des integers
     *          
     * @return b
     *          Das boolean-Array, indem die Zahl binaer aufgeteilt wurde
     */
    public boolean[] fromCharToBool(int rest) {

        boolean[] b = new boolean[8]; 
        int indexOfBoolArr = 0;
        while (rest > 0) {
            if (rest == 0)
                b[indexOfBoolArr++] = false;
            else {
                if(rest % 2 == 0)
                    b[indexOfBoolArr++] = false;
                else
                    b[indexOfBoolArr++] = true;
                rest /= 2;
            }
        }
        return b;
    }

    /**
     * Alle Booleans einer Liste, aber maximal acht zusammen werden zu Zahlenwerten als chars verrechnet.
     * Dies wird in binaerer Zaehlweise gemacht
     * 
     * @param b
     *          Die Liste mit den Booleans
     *          
     * @return s
     *          der String, mit allen aus den Booleans gebauten Strings
     */
    public <T> String printChars(LinkedList<Boolean> b) {
        String s = ""; //leeren String anlegen
        int listLength = b.size(); 
        while (!b.isEmpty()) {
            char ch = 0;
            int i;
            //Die i-te Zweierpotenz wird zu ch hinzugefuegt, wenn der aktuelle Boolean true ist.
            for (i = 0; i < 8 && i < listLength; i++) {
                if (b.removeFirst())
                    ch += (char) Math.pow(2, i);
                //chars koennen wie ints behandelt werden
            }
            listLength -= i;

            s += ch; //Der char wird an den String gehaengt
        }
        return s;
    }

    /**
     * Die uebergebene Liste wird sortiert.
     * Die prioritaet liegt folgend:
     *      Haeufigkeit des ColorManagerobjekts des Baumknotens
     *      Tiefe des ColorManagerobjekts des Baumknotens
     * 
     * Angewendet wird dabei der aus der Schule bekannte Quicksort.
     * 
     * @param list
     *          Die zu sortierende Liste
     */
    public <T> void sortList(LinkedList<BinaryTree<ColorManager>> list) {
        if (!list.isEmpty()) {
            LinkedList<BinaryTree<ColorManager>> smaller = new LinkedList<>();
            LinkedList<BinaryTree<ColorManager>> greater = new LinkedList<>();
            BinaryTree<ColorManager> pivot = list.remove();
            int pivOcc = pivot.getContent().getOccurence();

            while (!list.isEmpty()) {
                int thisOcc = list.getFirst().getContent().getOccurence();
                if (thisOcc > pivOcc) {
                    greater.add(list.remove());
                    continue;
                } else if (thisOcc == pivOcc) {
                    if (list.getFirst().getContent().getDepth() > pivot.getContent().getDepth()) {
                        greater.add(list.remove());
                        continue;
                    }
                }

                smaller.add(list.remove());
            }

            sortList(smaller);
            sortList(greater);
            list.addAll(smaller);
            list.add(pivot);
            list.addAll(greater);
        }
    }

    /**
     * Die Methode fuegt eine Farbe in die Liste ein, sofern sie noch nicht drin ist.
     * Andernfalls wird die Haufigkeit von dem ColorManager, der diesen Farbwert enthaelt, erhoeht.
     * 
     * @param list
     *          Die Liste, in die die Farbe eingefuegt werden soll.
     *          
     * @param color
     *          Die Farbe, die eingefuegt werden soll.
     */
    public <T> void insertColorInList(LinkedList<BinaryTree<ColorManager>> list, Color color) {
        int cValue = getColor(list, color); //Der Wert der einelnen Farbe wird anhand der Liste und des Colorobjekts ermittelt
        for (BinaryTree<ColorManager> b : list) {
            if (b.getContent().getValue() == cValue) {
                b.getContent().increaseOccurence();
                return;
            }
        }
        list.add(new BinaryTree<>(new ColorManager(cValue)));
    }

    /**
     * Der Farbwert wird anhand der Liste und dem Objekt von Color ermittelt und zurueckgegeben.
     * Ist die Liste nicht bekannt, wird -1 returnt.
     * 
     * @param list
     *          Die Liste,die die benoetigt wird, um zu wissen, um welche Farbe es sich handelt
     *          
     * @param c
     *          Das Colorobjekt, aus dem der bestimmte extrahiert werden soll
     *          
     * @return den gewuenschten Farbwert
     */
    public <T> int getColor(LinkedList<BinaryTree<ColorManager>> list, Color c) {
        if (list.equals(red))
            return c.getRed();
        if (list.equals(green))
            return c.getGreen();
        if (list.equals(blue))
            return c.getBlue();
        return -1;

    }

    /**
     * Alle Pfade, die der Baum besitzt werden in dieser Methode in eine Hashmap geschrieben. Der Pfad ist jeweils der Value zum Farbwert, dem Key.
     * Der Pfad wird dabei fortlaufend aktualisiert und wenn ein Blatt erreicht wird, wird der Wert in die Hashmap eingetragen und die Liste mit dem 
     * aktuellen Pfad als Kopie mit eingefuegt.
     * Die Methode arbeitet rekursiv.
     * 
     * @param b
     *          Der Baum, der traversiert wird
     *          
     * @param h
     *          Die Hashmap, in die die Werte mit den Pfaden geschrieben werden
     *          
     * @param hashmapBools
     *          Die Liste, die den aktuellen Pfad speichert, der sich immer veraendert.
     */
    public <T> void traverse(BinaryTree<ColorManager> b, HashMap<Integer, LinkedList<Boolean>> h,
    LinkedList<Boolean> hashmapBools) {
        if (b.isEmpty()) {
            return;
        } else if (b.getContent().getValue() != -1) {
            LinkedList<Boolean> hMapCopy = new LinkedList<Boolean>();
            for (Boolean bool : hashmapBools) {
                hMapCopy.add(bool); //die Pfadliste wird in die temporaere Liste kopiert
            }
            h.put(b.getContent().getValue(), hMapCopy); //Hashmapt wird mit Wert und temporaerer Liste befuellt
            return;

        }

        //Traversietung: Wird der linke Sohn fuer den naechsten Schritt genommen, wird in die Liste ein false eingefuegt, sonst ein true.
        hashmapBools.add(false);
        traverse(b.getLeftTree(), h, hashmapBools);
        hashmapBools.removeLast();

        hashmapBools.add(true);
        traverse(b.getRightTree(), h, hashmapBools);
        hashmapBools.removeLast();

    }

    /**
     * Solange die Liste groeßer als 1 ist, wird die Liste sortiert und die ersten beiden Baeume zu den Soehnen eines neuen Baums.
     * Das Objekt in diesem Baum nimmt sie Summe der Haeufigkeiten der Objekte seiner Soehne als eigene. 
     * Außerdem bekommt das Objekt im neuen Baum die Tiefe des Baums.
     * Ist die Liste zu Beginn schon nur 1 groß, wird ein neuer Baum erstellt, der den vorhandenen Baum nur als linken Sohn nimmt.
     * 
     * @param list
     *          Die Liste, in der ein Baum erstellt werden soll
     * 
     */
    private void generateTree(LinkedList<BinaryTree<ColorManager>> list) {
        if (list.size() == 1)
            list.add(new BinaryTree<ColorManager>(new ColorManager(-1), list.remove(), null));
        else {
            while (list.size() > 1) {
                sortList(list);
                int leftDepth = list.get(0).getContent().getDepth();
                int rightDepth = list.get(1).getContent().getDepth();
                list.add(0, new BinaryTree<ColorManager>(new ColorManager(-1), list.remove(0), list.remove(0)));

                //Die tiefste Tiefe der beiden Baeume wird ermittelt und dem neuen Baum zugeschrieben.
                if (leftDepth < rightDepth)
                    list.getFirst().getContent().setDepth(rightDepth);
                else
                    list.getFirst().getContent().setDepth(leftDepth);
            }
        }
    }

}
