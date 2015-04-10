/******************************************************************
/  clase: Archivo
/
/  autor: Dr. José Luis Zechinelli Martini
/******************************************************************/

import java.io.*;

public class Archivo {
    
    private final int SIN_ASIGNAR = IndiceDenso.SIN_ASIGNAR;
    
	private RandomAccessFile raf = null;
	private IndiceDenso indiceDenso = null;
    
    /*-----------------------------------------------------------------
    / constructor: índice denso con una clave de búsqueda de 20 bytes
    /-----------------------------------------------------------------*/
    
	public Archivo( RandomAccessFile archivo,
                    RandomAccessFile indice )
	{
		raf = archivo;
		indiceDenso = new IndiceDenso( indice, 20 );
	}
    
    /*-----------------------------------------------------------------
    / inserta un registro al archivo
    /-----------------------------------------------------------------*/
    
	public void insertar( Registro registro ) throws IOException {
        
		int posicionIndice = indiceDenso.getPosicion( registro.getSucursal() );
        
		if( posicionIndice == indiceDenso.size()-1 ) {
            
			int posicionArchivo = (int) raf.length() / registro.length();
			insertarEn( posicionArchivo, registro );
            
            if( indiceDenso.getLiga( posicionIndice ) == SIN_ASIGNAR )
				indiceDenso.updateLiga( posicionIndice, posicionArchivo );
            
            } else {
            
			int posicionArchivo = indiceDenso.getLiga( posicionIndice + 1 );
			insertarEn( posicionArchivo, registro );
            
			if( indiceDenso.getLiga( posicionIndice ) == SIN_ASIGNAR )
				indiceDenso.updateLiga( posicionIndice, posicionArchivo );
            
			for( posicionIndice ++;
                 posicionIndice < indiceDenso.size(); posicionIndice ++ )
            {
				posicionArchivo = indiceDenso.getLiga( posicionIndice ) + 1;
				indiceDenso.updateLiga( posicionIndice, posicionArchivo );
			}
		}
	}
    
    /*-----------------------------------------------------------------
    / borra un registro del archivo
    /-----------------------------------------------------------------*/
    
    public boolean borrar( String nomSuc ) throws Exception {
        
        int posicionIndice = indiceDenso.find( nomSuc );
        
        if( posicionIndice == SIN_ASIGNAR ) { return false; }
        
        else {
            Registro registro = new Registro();
            int posicion = indiceDenso.getLiga( posicionIndice );
            
            raf.seek( posicion * registro.length() );
            registro.read( raf );
            registro.setFlag( true );
            registro.setSucursal( "@Eliminado!@" ); // se puede quitar
            
            raf.seek( posicion * registro.length() );
            registro.write( raf );
            
            if( raf.getFilePointer() == raf.length() ) {
                                                // compacta el archivo
                indiceDenso.borrarEntrada( posicionIndice );
                
            } else {
                
                registro.read( raf );           // lee el siguiente registro
                
                if( registro.compareTo( nomSuc ) == 0 ) {
                                                // actualiza la liga
                    indiceDenso.updateLiga( posicionIndice, posicion + 1 );
                    
                } else {
                                                // compacta el archivo
                    indiceDenso.borrarEntrada( posicionIndice );
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
    / presenta los registros tanto del archivo como de su índice
    /-----------------------------------------------------------------*/
    
    public void mostrar() throws Exception {
        
		Registro registro = new Registro();
		int size = (int) raf.length() / registro.length();
        
		indiceDenso.mostrar();
        
		System.out.println( "Número de registros: " + size );
		raf.seek( 0 );
        
		for( int i = 0; i < size; i ++ ) {
            
			registro.read( raf );
            
			System.out.println( "( " + registro.getSucursal() + ", "
                                     + registro.getNumero() + ", "
                                     + registro.getNombre() + ", "
                                     + registro.getSaldo() + " )" );
		}
	}
    
    /*-----------------------------------------------------------------
    / cierra el archivo de datos
    /-----------------------------------------------------------------*/
    
    public void cerrar() throws IOException {
        
        raf.close();
        indiceDenso.cerrar();
    }
}
