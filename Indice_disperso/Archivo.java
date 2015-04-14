/******************************************************************
/  clase: Archivo
/
/  autor: Dr. Jos� Luis Zechinelli Martini
/******************************************************************/

import java.io.*;

public class Archivo {
    
    private final int SIN_ASIGNAR = IndiceDisperso.SIN_ASIGNAR;
    
	private RandomAccessFile raf = null;
	private IndiceDisperso indiceDisperso = null;
    
    /*-----------------------------------------------------------------
    / constructor: Índice disperso con una clave de búsqueda de 20 bytes
    /-----------------------------------------------------------------*/
    
	public Archivo( RandomAccessFile archivo,
                    RandomAccessFile indice )
	{
		raf = archivo;
		indiceDisperso = new IndiceDisperso( indice, 20 );
	}
    
    /*-----------------------------------------------------------------
    / inserta un registro al archivo
    /-----------------------------------------------------------------*/
    
	public void insertar( Registro registro ) throws IOException {
        
		int posicionIndice = indiceDisperso.getPosicion( registro.getSucursal() );
        
		if( posicionIndice == indiceDisperso.size()-1 ) {
            
			int posicionArchivo = (int) raf.length() / registro.length();
			insertarEn( posicionArchivo, registro );
            
            if( indiceDisperso.getLiga( posicionIndice ) == SIN_ASIGNAR )
				indiceDisperso.updateLiga( posicionIndice, posicionArchivo );
            
            } else {
            
			int posicionArchivo = indiceDisperso.getLiga( posicionIndice + 1 );
			insertarEn( posicionArchivo, registro );
            
			if( indiceDisperso.getLiga( posicionIndice ) == SIN_ASIGNAR )
				indiceDisperso.updateLiga( posicionIndice, posicionArchivo );
            
			for( posicionIndice ++;
                 posicionIndice < indiceDisperso.size(); posicionIndice ++ )
            {
				posicionArchivo = indiceDisperso.getLiga( posicionIndice ) + 1;
				indiceDisperso.updateLiga( posicionIndice, posicionArchivo );
			}
		}
	}
    
    /*-----------------------------------------------------------------
    / borra un registro del archivo
    /-----------------------------------------------------------------*/
    
    public boolean borrar( String nomSuc ) throws Exception {
        
        int posicionIndice = indiceDisperso.find( nomSuc );
        
        if( posicionIndice == SIN_ASIGNAR ) { return false; }
        
        else {
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
