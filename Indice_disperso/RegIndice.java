import java.io.*;

public class RegIndice {
    
	private byte[] clave;
	private int liga;
    
    /*-----------------------------------------------------------------
    / constructor
    /-----------------------------------------------------------------*/
    
	public RegIndice( int longitud ) { clave = new byte[ longitud ]; }
    
    /*-----------------------------------------------------------------
    / métodos getters/setters
    /-----------------------------------------------------------------*/
    
	public String getClave() { return new String( clave ); }
    
	public void setClave( String valor ) {
        
		byte[] v = valor.getBytes();
        
		for( int i = 0; i < clave.length && i < v.length; i++ )
            clave[i] = v[i];
	}
    
    public int getLiga() { return liga; }
    
	public void setLiga( int posicion ) { liga = posicion; }
    
    /*-----------------------------------------------------------------
    / longitud en bytes y comparación del valor de la clave
    /-----------------------------------------------------------------*/
    
	public int length() { return clave.length + Integer.SIZE / 8; }
    
	public int compararCon( String valor ) {
        
		byte[] k = valor.getBytes();
		byte[] v = new byte[ clave.length ];
        
		for( int i = 0; i < clave.length && i < k.length; i++ )
             v[i] = k[i];
        
		return getClave().compareTo( new String(v) );
	}
    
    /*-----------------------------------------------------------------
    / m�todos para escribir y leer una entrada en el �ndice
    /-----------------------------------------------------------------*/
    
	public void read( RandomAccessFile raf ) throws IOException {
        
		raf.read( clave );
		liga = raf.readInt();
	}
    
	public void write( RandomAccessFile raf ) throws IOException {
        
		raf.write( clave );
		raf.writeInt( liga );
	}
}
