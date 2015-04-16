import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by josema on 14/04/15.
 */
public class RegIndice {

    private int clave;
    private int liga;

    /*-----------------------------------------------------------------
    / constructor
    /-----------------------------------------------------------------*/

    public RegIndice(){}

    public RegIndice( int longitud ) { this.clave = longitud; }

    public int length() { return ((Integer.SIZE / 8)*2); }

    public int getClave() { return this.clave; }

    public void setClave( int num ) {
        clave = num;
    }

    public int getLiga() { return liga; }

    public void setLiga( int posicion ) { liga = posicion; }

    public void read( RandomAccessFile raf ) throws IOException {

        clave = raf.readInt();
        liga = raf.readInt();
    }

    public void write( RandomAccessFile raf ) throws IOException {

        raf.writeInt( clave );
        System.out.println("Clave a escribir: " + clave);
        raf.writeInt(liga);
        System.out.println("Liga a escribir: " + liga);
    }

}
