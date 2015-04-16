/******************************************************************
/  clase: Archivo
/
/  autor: Dr. Jos� Luis Zechinelli Martini
/******************************************************************/

import java.io.*;

public class Archivo {

    public final static int SIN_ASIGNAR = -1;

    private RandomAccessFile raf = null;
    private IndiceDisperso indiceDisperso = null;
    
    /*-----------------------------------------------------------------
    / constructor: Índice disperso con una clave de búsqueda de 20 bytes
    /-----------------------------------------------------------------*/

    public Archivo(RandomAccessFile archivo,
                   RandomAccessFile indice) {
        raf = archivo;
        indiceDisperso = new IndiceDisperso(indice, 20);
    }
    
    /*-----------------------------------------------------------------
    / inserta un registro al archivo
    /-----------------------------------------------------------------*/

    public void insertar(Registro registro) throws IOException {

        int posicionIndice = indiceDisperso.getIndexPos(registro.getNumero());
        System.out.println("[ARCHIVO-insertar] la posicion del indice es " + posicionIndice);

        int posicionArchivo = (int) raf.length() / registro.length();
        System.out.println("Primer Else de insertar...");
        if (raf.length() == 0) {
            insertarEn(posicionArchivo, registro);
        } else {
            busInsBloque(posicionIndice, registro);
        } //end else


        if (((posicionIndice + 1) * (registro.length() * 11)) == raf.length()) { //Bloque desbordado
            Registro temp = new Registro();
            raf.seek(raf.length() - temp.length());
            temp.read(raf);
            indiceDisperso.insertarEn(posicionIndice, temp.getNumero(),
                            (int)(raf.length()-temp.length())/temp.length());
        }

        if (indiceDisperso.getLiga(posicionIndice) == SIN_ASIGNAR)
            indiceDisperso.updateLiga(posicionIndice, posicionArchivo);


    }

    public void busInsBloque(int posicionIndice, Registro registro) throws IOException {
        Registro temp = new Registro();
        int i = posicionIndice*(10*registro.length());
        System.out.println("Posicion de i " + i );
        raf.seek(i);
        temp.read(raf);
        boolean stahp = false;
        int posicionArchivo = (int) raf.length() / registro.length();
        while(!stahp) {
            System.out.println(temp.getNumero() + " es mayor que " + registro.getNumero());
            if(temp.getNumero()>registro.getNumero()) {
                System.out.println("Efectivamente");
                int posInsert = (int) ((raf.getFilePointer())-temp.length())/temp.length();
                insertarEn(posInsert, registro);
                stahp = true;
                System.out.println("Valor de posInsert " + posInsert);
                if (posInsert == i/temp.length()) {
                    System.out.println( posInsert + " (postInsert) es igual a (i) " + i);
                    indiceDisperso.setLiga(i, posInsert, registro.getNumero());
                    actualizarIndice(posicionIndice+1);
                }
            } else if(temp.getNumero()==registro.getNumero()) {
                System.out.println("Resulta que es igual...");
                stahp = true;
                raf.seek(raf.getFilePointer() - temp.length());
                registro.write( raf );
                int x = (int)raf.getFilePointer()-registro.length()/temp.length();
                if(x==i/temp.length()) {
                    indiceDisperso.setLiga(i,x,registro.getNumero());
                    actualizarIndice(posicionIndice+1);
                }
            } else {
                System.out.println("Resulta que es menor...");
                //raf.seek(raf.getFilePointer() + (temp.length()));
                if(raf.getFilePointer()==raf.length()) {
                    insertarEn((int)(raf.getFilePointer()/temp.length()), registro);
                    stahp = true;
                } else {
                    temp.read(raf);
                }
            }
        }



    }

    public void actualizarIndice(int posicionIndice) throws IOException {
        System.out.println("[ARCHIVO-actInd] Posicion del indice inial" + posicionIndice);
        for( int x = posicionIndice;
             x < indiceDisperso.size(); x++ )
        {
            System.out.println("[ARCHIVO-actInd] Incrementos de indice " + posicionIndice);
            Registro temp1 = new Registro();
            int posicionArchivo = (temp1.length()*10)*(posicionIndice)/ temp1.length();
            System.out.println("[ARCHIVO-actInd] Posicion del archivo " + posicionArchivo);

            raf.seek(posicionArchivo*temp1.length());
            temp1.read(raf);
            System.out.println("[ARCHIVO-actInd] Se ha leido el registro " + temp1.getSucursal() +
                    " " + temp1.getNumero());
            indiceDisperso.setLiga(posicionIndice, posicionArchivo, temp1.getNumero());
        }
    }

    public int busquedaBin(Registro registro, int otro, int otrox) throws IOException{
        int izq = otrox;
        int der = otro;
        Registro temp = registro;
        while( izq <= der ) {

            int mitad = izq + (der - izq) / 2;

            raf.seek(mitad * temp.length());
            temp.read(raf);

            if (temp.getNumero() > registro.getNumero()) {

                if (izq == der || (mitad - 1) < 0) {
                    System.out.println("[ARCHIVO-busquedaBin] Se regresa la mitad... " + mitad);
                    return mitad;
                }else
                    der = mitad;

            } else if (temp.getNumero() < registro.getNumero()) {

                if (izq == der) {
                    System.out.println("[ARCHIVO-busquedaBin] Se regresa la mitad... " + mitad);
                    return mitad;
                }else
                    izq = mitad + 1;

            } else {
                System.out.println("[ARCHIVO-busquedaBin]Llegamos al return mitad, solo mitad..." +  mitad);
                return mitad;
            }
        }
        System.out.println("Llegamos al return 0");
        return 0;
    }//end busquedaBin

    public void insertar2( Registro registro ) throws IOException {
        int posicion = indiceDisperso.getIndexPos(registro.getNumero());

    }
    
    /*-----------------------------------------------------------------
    / borra un registro del archivo
    /-----------------------------------------------------------------*/
    
    /*public boolean borrar( int numCuenta ) throws Exception {

        int posicionIndice = indiceDisperso.find( numCuenta );

        if( posicionIndice == SIN_ASIGNAR ) {
            return false;
        } else {
            Registro registro = new Registro();
            int posicion = indiceDisperso.getLiga( posicionIndice );

            raf.seek( posicion * registro.length() );
            registro.read( raf );
            registro.setFlag( true );
            registro.setSucursal( "@Eliminado!@" ); // se puede quitar

            raf.seek( posicion * registro.length() );
            registro.write( raf );

            if( raf.getFilePointer() == raf.length() ) {
                                                // compacta el archivo
                indiceDisperso.borrarEntrada( posicionIndice );

            } else {

                registro.read( raf );           // lee el siguiente registro

                if( registro.compareTo( nomSuc ) == 0 ) {
                                                // actualiza la liga
                    indiceDisperso.updateLiga( posicionIndice, posicion + 1 );

                } else {
                                                // compacta el archivo
                    indiceDisperso.borrarEntrada( posicionIndice );
                }
            }

            return true;
        }
    }*/
    
    /*-----------------------------------------------------------------
    / desplaza registros para insertar un registro en el archivo
    /-----------------------------------------------------------------*/
    
	private void insertarEn( int posicion, Registro registro ) throws IOException {
        
		int n = (int) raf.length() / registro.length();
        
		for( int i = n-1; i >= posicion; i -- ) {
            
			Registro temp = new Registro();
            
			raf.seek( i * temp.length() );
			temp.read( raf );
            
			raf.seek( (i+1) * temp.length() );
			temp.write( raf );
		}
        
		raf.seek( posicion * registro.length() );
		registro.write( raf );
	}
    
    /*-----------------------------------------------------------------
    / presenta los registros tanto del archivo como de su �ndice
    /-----------------------------------------------------------------*/
    
    public void mostrar() throws Exception {
        
		Registro registro = new Registro();
		int size = (int) raf.length() / registro.length();
        
		indiceDisperso.mostrar();
        
		System.out.println( "Número de registros: " + size );
		raf.seek( 0 );
        
		for( int i = 0; i < size; i ++ ) {
            
			registro.read( raf );
            
			System.out.println( "( " + registro.getSucursal().trim() + ", "
                                     + registro.getNumero() + ", "
                                     + registro.getNombre().trim() + ", "
                                     + registro.getSaldo() + " )" );
		}
	}
    
    /*-----------------------------------------------------------------
    / cierra el archivo de datos
    /-----------------------------------------------------------------*/
    
    public void cerrar() throws IOException {
        
        raf.close();
        indiceDisperso.cerrar();
    }
}
