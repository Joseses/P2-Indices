/******************************************************************
 /  clase: Prueba
 /
 /  autor: Dr. José Luis Zechinelli Martini
 /******************************************************************/

import java.io.*;

public class Prueba {
    
    /*-----------------------------------------------------------------
    / métodos de prueba
    /-----------------------------------------------------------------*/
    
	private static void crear() {
        
		try {
            // metadatos del archivo de datos y del archivo índice
            File datos = new File( "Archivo.Datos" );
            File denso = new File( "Indice.Denso" );
            
            // handlers para manipular el contenido de los archivos
			RandomAccessFile archivoRaF = new RandomAccessFile( datos, "rw" );
			RandomAccessFile indiceRaF = new RandomAccessFile( denso, "rw" );
            
            // archivo indexado usando una clave de búsqueda de 20 bytes
			Archivo archivo = new Archivo( archivoRaF, indiceRaF );
			Registro registro;
            
            /*
            registro = new Registro( "Sucursal 3", 3, "Cliente 3", 300.0 );
            archivo.insertar( registro );
            registro = new Registro( "Sucursal 2", 2, "Cliente 2", 200.0 );
            archivo.insertar( registro );
            registro = new Registro( "Sucursal 0", 0, "Cliente 1", 100.0 );
            archivo.insertar( registro );
            registro = new Registro( "Sucursal 1", 1, "Cliente 0", 0.0 );
            archivo.insertar( registro );
            */
            
            for( int num = 1, i = 1; i <= 8; i++ ) {
                for( int j = 1; j <= 2; j++ ) {
                    for( int k = 1; k <= 1; k++, num++ ) {
                        
                        String suc = "Sucursal " + String.format( "%3d", i );
                        String nom = "Cliente " + j;
                        
                        double salMin = 100.0, salMax = 30000.6;
                        double sal = Math.random() * (salMax - salMin) + salMin;
                        
                        archivo.insertar( new Registro( suc, num, nom, sal ) );
                    }
                }
            }
            
            for( int i = 0; i < 2; i ++ )
                 archivo.borrar( "Sucursal   8" );
            
            for( int i = 0; i < 2; i ++ )
                 archivo.borrar( "Sucursal   6" );
            
			archivo.cerrar();
            
		} catch( Exception e ) {
            
			System.out.println( "Exception:" );
			e.printStackTrace();
		}
	}
    
	private static void mostrar() {
        
		try {
            // metadatos del archivo de datos y del archivo índice
            File datos = new File( "Archivo.Datos" );
            File denso = new File( "Indice.Denso" );
            
            // handlers para manipular el contenido de los archivos
			RandomAccessFile archivoRaF = new RandomAccessFile( datos, "rw" );
			RandomAccessFile indiceRaF = new RandomAccessFile( denso, "rw" );
            
            // archivo indexado usando una clave de búsqueda de 20 bytes
			Archivo archivo = new Archivo( archivoRaF, indiceRaF );
            
            // imprime los registros del índice y los del archivo
            archivo.mostrar();
            archivo.cerrar();
            
		} catch( Exception e ) {
            
			System.out.println( "Exception:" );
			e.printStackTrace();
		}
	}
    
    /*-----------------------------------------------------------------
    / método principal
    /-----------------------------------------------------------------*/
    
	public static void main( String[] args ) {
        
		crear();
		mostrar();
	}
}
