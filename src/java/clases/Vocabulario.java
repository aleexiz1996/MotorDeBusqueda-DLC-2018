
package clases;

import BD.ConexionBD;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author mayur
 */
public class Vocabulario implements Serializable {
    
    private Hashtable<String, Termino> vocabulario;
    
    
    public Vocabulario()
            
    {
        this.vocabulario = new Hashtable();
    }
    
    public Vocabulario(Hashtable v)
    {
        this.vocabulario = v;
    }
    
    public void agregarTermino(Termino t)
    {
        //recibe un termino y agrega a la hash table
        if (this.vocabulario.containsKey(t.getPalabra())) 
        {
            Termino aux = this.vocabulario.get(t.getPalabra()); //trae el termino ya existente que es igual al que se intenta ingresar
            aux.setNr(aux.getNr()+1); //numeros de documentos donde aparece la palabra
            if (aux.getMaxFrecuenia() < t.getMaxFrecuenia() ) 
            {
                aux.setMaxFrecuenia(t.getMaxFrecuenia()); //cambia la frecuencia maxima en caso de ser necesario
            }
            
            return;
        }
        this.vocabulario.put(t.getPalabra(), t); //ingresa el termino en caso de que no esté en la hastable (vocabulario actual)
    }
    
    public void agregarDocumento(String archivo) throws FileNotFoundException, Exception 
    {
        //toma un documento ya parseado e ingresa los terminos encontrados en este con sus respectivos datos
        //ingresa en la base de datos el documento tantas veces como terminos tenga (con su frecuencia observada)
        
        LectorDocumento l = new LectorDocumento("DocumentosTP1/" + archivo);
        
        Hashtable<String, Integer> aux = l.palabrasObtenidas();
        
        ConexionBD bd = new ConexionBD();
        bd.MySQLConnection();
        
        for(Iterator i = aux.keySet().iterator(); i.hasNext() ;)
        {
            String palabra = (String) i.next();
            int frecuencia = aux.get(palabra);
            
            Termino t = new Termino(palabra, frecuencia);
            this.agregarTermino(t);
            
            bd.insertData("palabraXDocumento", palabra, archivo, frecuencia);
            
        }
        bd.closeConnection();
    }
    
    
    public Termino get(String palabra)
    {
        return this.vocabulario.get(palabra);
    }
    
    public void agregarCarpetaDocumentos() throws FileNotFoundException, Exception
    {
        //Selecciona una carpeta (y solo una carpeta) y carga todos los archivos .txt que contenga
        // esta carpeta en el vocabulario y el posteo(bd)
        
        
        FilenameFilter filter = new FilenameFilter() // crea filtro para sólo archivos .txt
        {
            public boolean accept(File dir, String fileName)
            {
                return fileName.endsWith("txt");
            }
        };

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(new JFrame());
        File f= fc.getSelectedFile();
        String [] fileList=f.list(filter); //implementa filtro para sólo leer archivos .txt
        
        
        //itera para agregar cada documento de la carpeta
        for (int i=0; i < fileList.length; i++)
        {
            this.agregarDocumento(fileList[i]);
        }
    }
    
    
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CONTENIDO: ");
        for(Iterator i = this.vocabulario.keySet().iterator(); i.hasNext() ;)
        {
            String palabra = (String) i.next();
            Termino t = this.vocabulario.get(palabra);
            sb.append("\n Palabra:  " + t.getPalabra() + " Frecuencia: " + t.getMaxFrecuenia());
            
        }
        return sb.toString();
    }
    
    
}
