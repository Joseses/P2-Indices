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
        if (raf.length() == 0) {
            insertarEn(posicionArchivo, registro);
            //actualizarIndice(posicionIndice);
        } else {
            busInsBloque(posicionIndice, registro);
        } //end else

        System.out.println("Se desborda? " + (((indiceDisperso.getLastIndex()+2)*(registro.length()*10))+registro.length()) +
                " " + raf.length());
        int posIndex = (indiceDisperso.size()-1);
        System.out.println("2. Tamaño de registro " + indiceDisperso.size());
        if (((posIndex+1)*(registro.length()*10))+registro.length() == raf.length()) { //Bloque desbordado
            System.out.println("Se ha desbordado! " + (((posIndex)*(registro.length()*10))+registro.length()) +
                    " " + raf.length());
            System.out.println("INSERCION MAESTRA, INSERCION MAESTRA!!!!!!!!!");
            Registro temp = new Registro();
            raf.seek(raf.length() - temp.length());
            temp.read(raf);
            indiceDisperso.insertarEn(posIndex+1, temp.getNumero(),
                        ((posIndex+1)*10));
        }

        if (indiceDisperso.getLiga(posicionIndice) == SIN_ASIGNAR)
            indiceDisperso.updateLiga(posicionIndice, posicionArchivo);


    }

    public boolean busquedaLineal(int clave) throws IOException{
        int tam = indiceDisperso.size();
        int x = 0;
        boolean encontrado = false;
        Registro reg = new Registro();
        int indPos = indiceDisperso.busquedaLineal(clave);
        if(indPos==-1) {
            System.out.println("Indice vacío");
            return encontrado;
        } else if (indPos==tam-1) {//Ultimo registro
            int i = indPos*(10*reg.length());
            raf.seek(i);
            reg.read(raf);
            while(raf.getFilePointer()!=raf.length()) {
                if(reg.getNumero()==clave) {
                    return encontrado = true;
                } else {
                    reg.read(raf);
                }
            }
        } else {
            int i = indPos*(10*reg.length());
            raf.seek(i);
            reg.read(raf);
            while(raf.getFilePointer()!=raf.length()) {
                if (reg.getNumero() == clave) {
                    encontrado = true;
                    raf.seek(raf.length());
                } else {
                    reg.read(raf);
                }
            }

        }
        return encontrado;
    }

    /*public void busInsBloque(int posicionIndice, Registro registro) throws IOException {
        Registro temp = new Registro();
        int i = posicionIndice*(10*registro.length());
        System.out.println("Posicion de i " + i );
        raf.seek(i);
        temp.read(raf);
        boolean stahp = false;
        int posicionArchivo = (int) raf.length() / registro.length();
        while(!stahp) {
            if(temp.getNumero()>registro.getNumero()) {
                int posInsert = (int) ((raf.getFilePointer())-temp.length())/temp.length();
                insertarEn(posInsert, registro);
//                actualizarIndice(posicionIndice);
                stahp = true;
                System.out.println("1. Tamaño de registro " + indiceDisperso.size());
//                if (posInsert == i/temp.length()) {
//                    System.out.println( posInsert + " (postInsert) es igual a (i) " + i);
//                    indiceDisperso.setLiga(i, posInsert, registro.getNumero());
//  //                  actualizarIndice(posicionIndice);
//                }
                actualizarIndice(posicionIndice);
                System.out.println("3. Tamaño de registro " + indiceDisperso.size());
            } else if(temp.getNumero()==registro.getNumero()) {
                System.out.println("Resulta que es igual...");
                stahp = true;
                raf.seek(raf.getFilePointer() - temp.length());
                registro.write( raf );
                int x = (int)raf.getFilePointer()-registro.length()/temp.length();
                if(x==i/temp.length()) {
                    indiceDisperso.setLiga(i,x,registro.getNumero());
                    actualizarIndice(posicionIndice);
                }
            } else {
                //raf.seek(raf.getFilePointer() + (temp.length()));
                if(raf.getFilePointer()==raf.length()) {
                    insertarEn((int)(raf.getFilePointer()/temp.length()), registro);
                    actualizarIndice(posicionIndice);
                    stahp = true;
                } else {
                    temp.read(raf);
                }
            }
        }
    }*/

    /*public void busInsBloque(int posicionIndice, Registro registro) throws IOException {
        Registro temp = new Registro();
        int i = posicionIndice*(10*registro.length());
        System.out.println("Posicion de i " + i );
        raf.seek(i);
        temp.read(raf);
        boolean stahp = false;
        int posicionArchivo = (int) raf.length() / registro.length();
        while(!stahp) {
            System.out.println(temp.getNumero() + " es igual a " + registro.getNumero() + "?");
            if(temp.getNumero()==registro.getNumero()) {
                System.out.println("Resulta que es igual...");
                stahp = true;
                raf.seek(raf.getFilePointer() - temp.length());
                registro.write( raf );
                int x = (int)raf.getFilePointer()-registro.length()/temp.length();
                if(x==i/temp.length()) {
                    indiceDisperso.setLiga(i, x, registro.getNumero());
                    actualizarIndice(posicionIndice);
                }
            } else if(temp.getNumero()>registro.getNumero()) {
                int posInsert = (int) ((raf.getFilePointer())-temp.length())/temp.length();
                insertarEn(posInsert, registro);
//                actualizarIndice(posicionIndice);
                stahp = true;
                System.out.println("1. Tamaño de registro " + indiceDisperso.size());
//                if (posInsert == i/temp.length()) {
//                    System.out.println( posInsert + " (postInsert) es igual a (i) " + i);
//                    indiceDisperso.setLiga(i, posInsert, registro.getNumero());
//  //                  actualizarIndice(posicionIndice);
//                }
                actualizarIndice(posicionIndice);
                System.out.println("3. Tamaño de registro " + indiceDisperso.size());
            } else {
                //raf.seek(raf.getFilePointer() + (temp.length()));
                if(raf.getFilePointer()==raf.length()) {
                    insertarEn((int)(raf.getFilePointer()/temp.length()), registro);
                    actualizarIndice(posicionIndice);
                    stahp = true;
                } else {
                    temp.read(raf);
                }
            }
        }
    }*/

    public void busInsBloque(int posicionIndice, Registro registro) throws IOException {
        Registro temp = new Registro();
        int i = posicionIndice*(10*registro.length());
        System.out.println("Posicion de i " + i );
        raf.seek(i);
        temp.read(raf);
        boolean stahp = false;
        int posicionArchivo = (int) raf.length() / registro.length();
        boolean numeroRepetido = false;
        if(posicionIndice!=0) {
            raf.seek((posicionIndice-1)*(10*registro.length()));
            temp.read(raf);
        }
        for(int x = 0; x<10; x++) {
            System.out.println("For: " + temp.getNumero() + " es igual a " + registro.getNumero() + "?");
            if (temp.getNumero() == registro.getNumero()) {
                numeroRepetido = true;
                x = 9;
            } else if (raf.getFilePointer() != raf.length()) {
                temp.read(raf);
            } else {
                x = 9;
                raf.seek(i);
                temp.read(raf);
            }
        }
        while(!stahp) {
            System.out.println(temp.getNumero() + " es igual a " + registro.getNumero() + "?");
            if (temp.getNumero() == registro.getNumero()) {
                System.out.println("Resulta que es igual...");
                stahp = true;
                raf.seek(raf.getFilePointer() - temp.length());
                registro.write(raf);
                int x = (int) raf.getFilePointer() - registro.length() / temp.length();
                if (x == i / temp.length()) {
                    indiceDisperso.setLiga(i, x, registro.getNumero());
                    actualizarIndice(posicionIndice);
                }
            } else if (temp.getNumero() > registro.getNumero()) {
                int posInsert = (int) ((raf.getFilePointer()) - temp.length()) / temp.length();
                insertarEn(posInsert, registro);
//                actualizarIndice(posicionIndice);
                stahp = true;
                System.out.println("1. Tamaño de registro " + indiceDisperso.size());
//                if (posInsert == i/temp.length()) {
//                    System.out.println( posInsert + " (postInsert) es igual a (i) " + i);
//                    indiceDisperso.setLiga(i, posInsert, registro.getNumero());
//  //                  actualizarIndice(posicionIndice);
//                }
                actualizarIndice(posicionIndice);
                System.out.println("3. Tamaño de registro " + indiceDisperso.size());
            } else {
                //raf.seek(raf.getFilePointer() + (temp.length()));
                if (raf.getFilePointer() == raf.length()) {
                    insertarEn((int) (raf.getFilePointer() / temp.length()), registro);
                    actualizarIndice(posicionIndice);
                    stahp = true;
                } else {
                    temp.read(raf);
                }
            }
        }
    }

    public void busInsEliminar(int posicionIndice, int cuenta) throws IOException {
        Registro temp = new Registro();
        int i = posicionIndice*(10*temp.length());
        System.out.println("Posicion de i " + i );
        raf.seek(i);
        temp.read(raf);
        int posicionArchivo = (int) raf.length() / temp.length();
        if(posicionIndice!=0) {
            raf.seek((posicionIndice-1)*(10*temp.length()));
            temp.read(raf);
        }
        for(int x = 0; x<10; x++) {
            System.out.println("For: " + temp.getNumero() + " es igual a " + cuenta + "?");
            if (temp.getNumero() == cuenta) {
                x = 9;
            } else if (raf.getFilePointer() != raf.length()) {
                temp.read(raf);
            } else {
                x = 9;
                raf.seek(i);
                temp.read(raf);
            }
        }
        for(int z = 0; z<10;z++) {
            System.out.println(temp.getNumero() + " es igual a " + cuenta + "?");
            if (temp.getNumero() == cuenta) {
                System.out.println("Resulta que es igual...");
                raf.seek(raf.getFilePointer() - temp.length());
                temp.setFlag(true);
                temp.write(raf);
                z = 9;

            } else {
                //raf.seek(raf.getFilePointer() + (temp.length()));
                if (raf.getFilePointer() == raf.length()) {
                    System.out.println("Número a eliminar no existe");
                    z=9;
                } else {
                    temp.read(raf);
                }
            }
        }
    }

    public void actualizarIndice(int posicionIndice) throws IOException {
        System.out.println("[ARCHIVO-actInd] Posicion del indice inicial" + posicionIndice);
        Registro temp1 = new Registro();
        for( int x = posicionIndice;
             x < indiceDisperso.size() && !(raf.getFilePointer()+temp1.length()>raf.length()) ; x++ )
        {

            System.out.println("[ARCHIVO-actInd] Incrementos de indice " + x);
            int posicionArchivo = ((temp1.length() * 10) * (x)) / temp1.length();
            System.out.println("[ARCHIVO-actInd] Posicion del archivo " + posicionArchivo);

            raf.seek(posicionArchivo * temp1.length());
            temp1.read(raf);
            System.out.println("[ARCHIVO-actInd] Se ha leido el registro " + temp1.getSucursal() +
                    " " + temp1.getNumero());
            System.out.println("4. Tamaño de registro (antes de set liga)" + indiceDisperso.size());
            indiceDisperso.setLiga(x, posicionArchivo, temp1.getNumero());
            System.out.println("4. Tamaño de registro (depues de set liga)" + indiceDisperso.size());
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
    
    /*-----------------------------------------------------------------
    / borra un registro del archivo
    /-----------------------------------------------------------------*/

    public void borrar(int numCuenta) throws IOException {
        int posicionIndice = indiceDisperso.findIndexPos(numCuenta);
        Registro registro = new Registro();
        busInsEliminar(posicionIndice, numCuenta);

    }
    
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
        
		System.out.println("Número de registros: " + size);
		raf.seek(0);
        
		for( int i = 0; i < size; i ++ ) {
            
			registro.read( raf );
            
			System.out.print( "( " + registro.getSucursal().trim() + ", "
                                     + registro.getNumero() + ", "
                                     + registro.getNombre().trim() + ", "
                                     + registro.getSaldo());
            if(registro.getBorrado()) {
                System.out.print(", " + "BORRADO" + ")");
                System.out.println();
            } else {
                System.out.print(")");
                System.out.println();
            }
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
