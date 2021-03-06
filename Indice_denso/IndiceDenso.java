/******************************************************************
/  clase: IndiceDenso
/
/  autor: Dr. JosŽ Luis Zechinelli Martini
/******************************************************************/

import java.io.*;

public class IndiceDenso {
    
    public final static int SIN_ASIGNAR = -1;    // registro no encontrado
    
	private RegIndice registro = null;
	private RandomAccessFile raf = null;
    
    /*-----------------------------------------------------------------
    / constructor
    /-----------------------------------------------------------------*/
    
	public IndiceDenso( RandomAccessFile indice, int longitud ) {
        
		raf = indice;
		registro = new RegIndice( longitud );
	}
    
    /*-----------------------------------------------------------------
    / consulta del n�mero total de entradas, de la
    /              posici�n de un registro y del contenido de su liga
    /-----------------------------------------------------------------*/
    
    public int size() throws IOException {
        
        return (int) raf.length() / registro.length();
    }
    
	public int find( String clave ) throws IOException {
        
		if( size() == 0 )
			return SIN_ASIGNAR;
		else
			return busquedaBinaria( clave, 0, size() - 1 );
	}
    
    public int getLiga( int posicion ) throws IOException {
        
		raf.seek( posicion * registro.length() );
		registro.read( raf );
        
		return registro.getLiga();
	}
    
    /*-----------------------------------------------------------------
    / m�todos de inserci�n y actualizaci�n
    /-----------------------------------------------------------------*/
    
	public int getPosicion( String clave ) throws IOException {
        
		if( size() == 0 )
			return insertarEn( 0, clave );
		else
			return buscarInsertar( clave, 0, size()-1 );
	}
    
	public void updateLiga( int posicion, int liga ) throws IOException {
        
		raf.seek( posicion * registro.length() );   // lee el registro
		registro.read( raf );
        
		registro.setLiga( liga );                   // actualiza la liga
        
		raf.seek( posicion * registro.length() );   // guarda el registro
		registro.write( raf );
	}
    
    /*-----------------------------------------------------------------
    / busca un registro en el �ndice y regresa su posici�n
    /-----------------------------------------------------------------*/
    
	private int busquedaBinaria( String clave, int izq, int der )
    
            throws IOException
	{
		while( izq <= der ) {
            
			int mitad = izq + ( der - izq ) / 2;
            
			raf.seek( mitad * registro.length() );
			registro.read( raf );
            
			if( registro.compararCon( clave ) < 0 )
				izq = mitad + 1;
			else if( registro.compararCon( clave ) > 0 )
				der = mitad - 1;
			else
				return mitad;
		}
        
		return SIN_ASIGNAR;
	}
     /*-----------------------------------------------------------------
    / busqued lineal de un registro y regre su  posicion
    /-----------------------------------------------------------------*/
   
        public int busquedaLineal( String clave)
            throws IOException
	{
            for(int i = 0; i<size(); i++) {
			raf.seek(i* registro.length());
                        //System.out.println(i +" "+ registro.length() +" "+ size()+registro.getClave());
			registro.read(raf);
			if(/*registro.getClave()*/registro.compararCon(clave) == 0) 
                        {
				return registro.getLiga();
			}
		}
            return SIN_ASIGNAR;
	}     
    
        
        
    /*-----------------------------------------------------------------
    / busca un registro y si no lo encuentra lo crea
    /-----------------------------------------------------------------*/
    
    private int buscarInsertar( String clave, int izq, int der )
    
            throws IOException
    {
		while( izq <= der ) {
            
			int mitad = izq + ( der - izq ) / 2;
            
			raf.seek( mitad * registro.length() );
			registro.read( raf );
            
			if( registro.compararCon( clave ) > 0 ) {
                
				if( izq == der || ( mitad - 1 ) < 0 )
					return insertarEn( mitad, clave );
				else
					der = mitad;
                
			} else if( registro.compararCon( clave ) < 0 ) {
                
				if( izq == der )
					return insertarEn( mitad + 1, clave );
				else
					izq = mitad + 1;
                
			} else {
                
				return mitad;
			}
		}
        
		throw new IOException( "Archivo inconsistente" );
	}

    /*-----------------------------------------------------------------
    / desplaza registros para insertar un registro en el �ndice
    /-----------------------------------------------------------------*/
    
	private int insertarEn( int posicion, String clave ) throws IOException {
        
		for( int i = size()-1; i >= posicion; i-- ) {
            
			raf.seek( i * registro.length() );
			registro.read( raf );
            
			raf.seek( (i+1) * registro.length() );
			registro.write( raf );
		}
        
		raf.seek( posicion * registro.length() );
		registro.setClave( clave );
        registro.setLiga( SIN_ASIGNAR );
		registro.write( raf );
        
		return posicion;
	}
    
    /*-----------------------------------------------------------------
    / borra una entrada del �ndice compactando el archivo
    /-----------------------------------------------------------------*/
    
    public void borrarEntrada( int posicion ) throws Exception {
        
        if( posicion >= 0 && posicion <= size()-1 ) {
            
            for( int i = posicion + 1; i < size(); i++ ) {
                raf.seek( i * registro.length() );
                registro.read( raf );
                
                raf.seek( (i-1) * registro.length() );
                registro.write( raf );
            }
            
            raf.setLength( raf.length() - registro.length() );
            
        } else {
            
            throw new Exception( "Posici�n inv�lida" );
        }
    }
    
    /*-----------------------------------------------------------------
    / presenta los registros del �ndice
    /-----------------------------------------------------------------*/
    
	public void mostrar() throws Exception {
        
		System.out.println( "N�mero de entradas: " + size() );
		raf.seek( 0 );
        
		for( int i = 0; i < size(); i++ ) {
            
			registro.read( raf );
            
			System.out.println( "( " + registro.getClave() + ", "
                                     + registro.getLiga() + " )" );
		}
	}
    
    /*-----------------------------------------------------------------
    / cierra el archivo �ndice
    /-----------------------------------------------------------------*/
    
    public void cerrar() throws IOException { raf.close(); }
}
