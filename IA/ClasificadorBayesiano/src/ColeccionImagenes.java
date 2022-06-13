import java.io.BufferedReader; 
import java.io.FileReader; 

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.StringTokenizer;


class Imagen
{
  /* Tamano de los ficheros imagenes */
  int tam; 

  /* Pixels de la imagen (valores entre 0 255). Codificado como un
   * array unidimensional que representa un array bidimensional cuadrado */
  byte pixels[];

  /* Etiqueta de clase (dígito que representa) */
    
  /* Indica si la imagen ha sido binarizada */
  boolean binarizada=false;


  Imagen(byte []p)
  {
      tam=(int)Math.sqrt(p.length);
      pixels=new byte[tam*tam];
      for(int i=0;i<tam*tam;i++) pixels[i]=p[i];
  }

  byte getPixel(int x,int y)
  {
      if(x<0||x>=tam||y<0||y>=tam) return 0;
      else return pixels[y*tam+x];
  }

  byte []getPixels()
  {
      return pixels;
  }

  void setPixel(int x,int y, byte valor)
  {
      if(x>=0&&x<=tam&&y>=0&&y<=tam) 
      	  pixels[y*tam+x]=valor;
  }

  void binariza(byte umbral)
  {
      binarizada=true;
      for(int x=0;x<tam;x++)
	  for(int y=0;y<tam;y++)
	   {
	      if(pixels[y*tam+x]<umbral) pixels[y*tam+x]=1;
	      else pixels[y*tam+x]=0;
	   }
  }
     

  void pintaConsola()
  {
      for(int y=0;y<tam;y++)
      {
	  for(int x=0;x<tam;x++)
	  {
	      if(pixels[y*tam+x]==0) System.out.print("#");
	      else System.out.print(" ");
	      System.out.print(" ");
	  }

	  System.out.println("");
      }     
  }
}      

  
public class ColeccionImagenes
{
    /* Coleccion de imagenes */
    Imagen imagenes[];
    int numero;
    BufferedReader bf;
    String linea;
    int contador=0;
    byte pixels[];
    StringTokenizer strTok;

    String etiqueta="unknown"; //representa "desconocido"

    /* Parámetros: nombre del fichero que contiene las imágenes a cargar y número de imágenes a cargar (0 -> las carga todas) */

    ColeccionImagenes(String nombreFichero, int n)
    {

	try
	{
	    bf = new BufferedReader(new FileReader(nombreFichero));
	}

	catch(FileNotFoundException i)
	{
	    System.err.println("Error. No existe el fichero " + nombreFichero);
	    System.exit(-1);
	}
	try
	{
	    /* La primera línea contiene el numero de imágenes */
	    linea=bf.readLine();
	    strTok=new StringTokenizer(linea);
	    String aux=strTok.nextToken();
	    if(!aux.equals("TAM"))
	    {
		System.err.println("ERROR. La primera línea del fichero debe contener el número de imágenes (formato TAM xxx)");
		System.exit(-1);
	    }
	    numero=n;
	    int numeroFichero=new Integer(strTok.nextToken()).intValue();
	    if(numero==0) numero=numeroFichero;

	    imagenes = new Imagen[numero];
	
	    linea=bf.readLine();
	    etiqueta=linea;
	    while ((linea = bf.readLine())!=null&&contador<numero)
	    {
		/* La primera linea contiene el tamaño de la imagen */
		strTok=new StringTokenizer(linea);
		while(!strTok.hasMoreTokens())
		{
		    linea=bf.readLine();
		    if(linea==null)
		    {
			System.err.println("Error leyendo del fichero. Fin de fichero inesperado\n");
			System.exit(-1);
		    }
		    strTok=new StringTokenizer(linea);
		    
			
		}
		int tam1=new Integer(strTok.nextToken()).byteValue();
		int tam2=new Integer(strTok.nextToken()).byteValue();
		if(tam1!=tam2)
		{
		    System.err.println("Error. Las imágenes tienen que ser cuadradas \n");
		    System.exit(-1);
		}
		pixels=new byte[tam1*tam1];
		/* Se asume que si hay píxels no especificados, estos
		   toman un valor 0 */
		for(int i=0;i<tam1*tam1;i++) pixels[i]=0;
		int y=0;
		/* Se lee la etiqueta de clase */

	
		/* Se leen las filas de píxels */
		while(y<tam1)
		{		
		    linea = bf.readLine();
		    strTok=new StringTokenizer(linea);
		    int x=0;
		    while(strTok.hasMoreTokens())
		    {
			pixels[tam1*y+x]=new Integer(strTok.nextToken()).byteValue();
			x++;
		    }
		    y++;
		}
		imagenes[contador++]=new Imagen(pixels);
	    }
	}
	catch(IOException i)
	{
	    System.err.println("Error leyendo del fichero " + nombreFichero);
	    System.exit(-1);
	}
    }			
				
	    
  	    
    public static void main(String s[])
    {
	int numero=5;
	ColeccionImagenes coleccion=new ColeccionImagenes(s[0],0);
        for(int i=0;i<numero;i++)
	    coleccion.imagenes[i].pintaConsola();
    }

}

