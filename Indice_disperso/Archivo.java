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

        int posicionArchivo = (int) raf.length() / registro.length();
        if (raf.length() == 0) {
            insertarEn(posicionArchivo, registro);
            //actualizarIndice(posicionIndice);
        } else {
            busInsBloque(posicionIndice, registro);
        } //end else
        int posIndex = (indiceDisperso.size()-1);
        if (((posIndex+1)*(registro.length()*10))+registro.length() == raf.length()) { //Bloque desbordado
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

    public void busInsBloque(int posicionIndice, Registro registro) throws IOException {
        Registro temp = new Registro();
        int i = posicionIndice*(10*registro.length());
        raf.seek(i);
        temp.read(raf);
        boolean stahp = false;
        if(posicionIndice!=0) {
            raf.seek((posicionIndice-1)*(10*registro.length()));
            temp.read(raf);
        }
        for(int x = 0; x<10; x++) {
            if (temp.getNumero() == registro.getNumero()) {
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
            if (temp.getNumero() == registro.getNumero()) {
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
                stahp = true;
                actualizarIndice(posicionIndice);
            } else {
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
        raf.seek(i);
        temp.read(raf);
        if(posicionIndice!=0) {
            raf.seek((posicionIndice-1)*(10*temp.length()));
            temp.read(raf);
        }
        for(int x = 0; x<10; x++) {
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
            if (temp.getNumero() == cuenta) {
                raf.seek(raf.getFilePointer() - temp.length());
                temp.setFlag(true);
                temp.write(raf);
                z = 9;
                System.out.println("¡Número eliminado con éxito!");
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
        Registro temp1 = new Registro();
        for( int x = posicionIndice;
             x < indiceDisperso.size() && !(raf.getFilePointer()+temp1.length()>raf.length()) ; x++ )
        {

            int posicionArchivo = ((temp1.length() * 10) * (x)) / temp1.length();

            raf.seek(posicionArchivo * temp1.length());
            temp1.read(raf);
            indiceDisperso.setLiga(x, posicionArchivo, temp1.getNumero());
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
    
    /*------------------------------------------------------------------
    /Busqueda Lineal
    */
    public void busquedaLineal(int clave, Registro registro) throws IOException {
        
		int n = (int) raf.length() / registro.length();
                boolean encontrado = false;
                int j=0;
                
                int posicion = indiceDisperso.busquedaLineal(clave);
                
               

		if(posicion != -1  )
                {
                    
                    raf.seek(posicion*registro.length());
                    registro.read(raf);
                    
                    System.out.println("Numero de cuenta existe en el indice"/*+" "+posicion+" "+registro.compareTo(clave)*/);
                    int i = posicion;
                    while(i < n && j < 10 && !registro.deleteFlag() )
                    //for(int i = posicion; i < n && registro.compareTo(clave)== 0;  i++) 
                    {
			
                       // System.out.println(posicion+" "+i+" "+n+" "+registro.compareTo(clave)+" "+registro.getNumero()+" "+registro.getSucursal());
                        
			if(registro.getNumero() == clave )
                        {
                            System.out.println("El registro se encuentra en la posicón "+ i +" del archivo "+"\n"
                            + registro.getSucursal() + " "+ registro.getNombre() + " "+ registro.getSaldo());
                            encontrado = true;
			}
                        
                        i++;
                        if(i < n){
                            raf.seek(i* registro.length());
                            registro.read(raf);}
                        j++;
                    }
                }
                else
                {
                     System.out.println("La sucursal no existe en el indice");       
                }
                if(!encontrado)
                    System.out.println("No existe el registro con tal numero de cuenta");
		
    }
    
    
    public void cerrar() throws IOException {
        
        raf.close();
        indiceDisperso.cerrar();
    }
}
