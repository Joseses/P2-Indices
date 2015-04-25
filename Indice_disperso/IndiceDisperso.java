import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by josema on 14/04/15.
 */
public class IndiceDisperso {

    public final static int SIN_ASIGNAR = -1;    // registro no encontrado

    public RegIndice registro = null;
    private RandomAccessFile raf = null;
    private static int claveMenor = -200;

    public IndiceDisperso( RandomAccessFile indice, int longitud ) {

        raf = indice;
        registro = new RegIndice( longitud );
    }

    public int size() throws IOException {
        return (int) raf.length() / registro.length();
    }

    public int getLiga( int posicion ) throws IOException {

        raf.seek(posicion * registro.length());
        registro.read(raf);

        return registro.getLiga();
    }

    public void updateLiga( int posicion, int liga ) throws IOException {

        raf.seek(posicion * registro.length());   // lee el registro
        registro.read(raf);

        registro.setLiga(liga);                   // actualiza la liga

        raf.seek( posicion * registro.length() );   // guarda el registro
        registro.write(raf);
    }

    public void setLiga (int posicion, int liga, int clave) throws IOException {
        registro.setLiga(liga);                   // actualiza la liga
        registro.setClave(clave);
        raf.seek(posicion * registro.length());   // guarda el registro
        registro.write(raf);
    }

    /*public int getPosicion( int clave ) throws IOException {

        if( size() == 0 )
            return insertarEn( 0, clave );
        else
            return buscarInsertar( clave, 0, size()-1 );
    }*/

    public int find( int clave ) throws IOException {

        if( size() == 0 )
            return SIN_ASIGNAR;
        else
            return busquedaBinaria(clave, 0, size() - 1);
    }

    public int getLastIndex() throws IOException {
        if(size()!=0) {
            int pos = (size()/registro.length());
            return pos;
        } else {
            return 0;
        }

    }

    private int busquedaBinaria( int clave, int izq, int der ) throws IOException {
        while( izq <= der ) {

            int mitad = izq + ( der - izq ) / 2;

            raf.seek(mitad * registro.length());
            registro.read( raf );

            if( registro.getClave() < clave )
                izq = mitad + 1;
            else if( registro.getClave() > clave )
                der = mitad - 1;
            else
                return mitad;
        }

        return SIN_ASIGNAR;
    }

    /*private int buscarInsertar( int clave, int izq, int der ) throws IOException{
        while( izq <= der ) {

            int mitad = izq + ( der - izq ) / 2;

            raf.seek(mitad * registro.length());
            registro.read( raf );

            if( registro.getClave() > clave ) {

                if( izq == der || ( mitad - 1 ) < 0 )
                    return insertarEn( mitad, clave );
                else
                    der = mitad;

            } else if( registro.getClave() < clave ) {

                if( izq == der )
                    return insertarEn( mitad + 1, clave );
                else
                    izq = mitad + 1;

            } else {

                return mitad;
            }
        }

        throw new IOException( "Archivo inconsistente" );
    }*/

    public int insertarEn( int posicion, int clave, int liga ) throws IOException {

        for( int i = size()-1; i >= posicion; i-- ) {
            System.out.println("[INDICE-insertarEn] Entrando a for");

            raf.seek( i * registro.length() );
            registro.read( raf );

            raf.seek( (i+1) * registro.length() );
            registro.write( raf );
        }

        raf.seek( posicion * registro.length() );
        System.out.println("[INDICE-insertarEn] Clave a escribir " + clave);
        registro.setClave( clave );
        registro.setLiga( liga );
        registro.write( raf );
        System.out.println("[INDICE-insertarEn] Tamaño de registro" + size());

        return posicion;
    }

    public int getIndexPos(int clave) throws IOException{
        if(size()==0) { //Se crea la primera entrada del indice
            System.out.println("[INDICE - getIndexPos] Se insertará la primera posición del index");
            claveMenor=clave;
            return insertarEn(0, clave, 0);
        } else {
            int izq = 0;
            int der = size()-1;
            while( izq <= der ) {

                int mitad = izq + (der - izq) / 2;

                raf.seek(mitad * registro.length());
                registro.read(raf);

                if (registro.getClave() > clave) {

                    if (izq == der || (mitad - 1) < 0)
                        return mitad;
                    else
                        der = mitad;

                } else if (registro.getClave() < clave) {

                    if (izq == der)
                        return mitad;
                    else
                        izq = mitad + 1;

                } else {

                    return mitad;
                }
            }
        } //end else
        return 0;
    }//end getIndexPos

    public void mostrar() throws Exception {

        System.out.println( "Número de entradas: " + size() );
        raf.seek( 0 );

        for( int i = 0; i < size(); i++ ) {

            registro.read( raf );

            System.out.println( "( Cuenta numero " + registro.getClave() + ", "
                    + registro.getLiga() + " )" );
        }
    }

    public int busquedaLineal (int clave) throws IOException{
        if(size()==0) {
            return -1;
        } else {
            raf.seek(0);

            for( int i = 0; i < size(); i++ ) {

                registro.read( raf );
                if(registro.getClave()>clave || registro.getClave()==clave) {
                    return i;
                }
            }
        }
        return size()-1; //La clave es mayor que el ultimo indice
    }

    public void cerrar() throws IOException { raf.close(); }
}
