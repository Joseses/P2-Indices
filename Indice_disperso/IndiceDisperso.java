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

    public int insertarEn( int posicion, int clave, int liga ) throws IOException {

        for( int i = size()-1; i >= posicion; i-- ) {

            raf.seek( i * registro.length() );
            registro.read( raf );

            raf.seek( (i+1) * registro.length() );
            registro.write( raf );
        }

        raf.seek( posicion * registro.length() );
        registro.setClave( clave );
        registro.setLiga( liga );
        registro.write( raf );

        return posicion;
    }

    public int getIndexPos(int clave) throws IOException{
        if(size()==0) { //Se crea la primera entrada del indice
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

    public int findIndexPos(int clave) throws IOException{
        if(size()==0) { //Se crea la primera entrada del indice
            return 0;
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
    }//end findIndexPos

    public void mostrar() throws Exception {

        System.out.println( "Número de entradas: " + size() );
        raf.seek( 0 );

        for( int i = 0; i < size(); i++ ) {

            registro.read( raf );

            System.out.println( "( Cuenta numero " + registro.getClave() + ", "
                    + registro.getLiga() + " )" );
        }
    }

    
    
    /*-----------------------------------------------------------------
    / busqued lineal de un registro y regre su  posicion
    /-----------------------------------------------------------------*/
   
        public int busquedaLineal(int unaClave)
            throws IOException
	{
            RegIndice temp = new RegIndice();
            
            for(int i = 0; i<size(); i++) {
			raf.seek(i* registro.length());
                        //System.out.println(i +" "+ registro.length() +" "+ size()+registro.getClave());
			registro.read(raf);
                        
                        raf.seek((i+1)* registro.length());
                        temp.read(raf);
                        
			if( registro.getClave() < unaClave && temp.getClave() > unaClave) 
                        {
				return registro.getLiga();
			}
		}
            return SIN_ASIGNAR;
	}     

    public void cerrar() throws IOException { raf.close(); }
}
