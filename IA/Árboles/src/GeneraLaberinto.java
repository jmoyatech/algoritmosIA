
import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.util.StringTokenizer;
import java.io.*;


/* Clase para generar un  laberinto */

class GeneraLaberinto
{
    int size;
    int laberinto[];
    int visitados[];
    Random aleatorio;
    
    public int entradax=0;
    public int entraday=1;
    public int salidax;
    public int saliday;     /* Se calcula en función del tamaño una vez generado el laberinto */

    /* Posicion de inicio del gato */
    public int posgatox;
    public int posgatoy;
    String nombreFichero;

    /* Los laberintos siempre tienen tamaño impar */
    GeneraLaberinto(String n) 
    {
	nombreFichero=n;
    }

    

    int [][] leeLaberintoFichero()
    {

	int [][]res=null;
	StringTokenizer strTok;

	try
	{
	    FileInputStream fstream = new FileInputStream(nombreFichero);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine=br.readLine();
	    strTok= new StringTokenizer(strLine);
	    if(!strTok.nextToken().equals("Tamx"))
	    {
		System.out.println("Error. Se esperaba la etiqueta Tamx");
		return null;
	    }

	    int tamx=new Integer(strTok.nextToken()).intValue();
	    strLine=br.readLine();
    	    strTok= new StringTokenizer(strLine);
	    if(!strTok.nextToken().equals("Tamy"))
	    {
		System.out.println("Error. Se esperaba la etiqueta Tamy");
		return null;
	    }
	    int tamy=new Integer(strTok.nextToken()).intValue();
	    res=new int[tamx][tamy];
	    int i,j;
	    for(i=0;i<tamx;i++)
		for(j=0;j<tamy;j++)
		    res[i][j]=0;

	    for(i=0;i<tamx;i++)
	    {
		int muro=-1;
		strLine=br.readLine();
		strTok= new StringTokenizer(strLine);
		while(strTok.hasMoreTokens())
		{
		    muro=new Integer(strTok.nextToken()).intValue();
		    if(muro<0||muro>=tamy)
		    {
			System.out.println("Error. Coordenada del muro en fila " + i + "  no valida");
			return null;
		    }
		    res[i][muro]=1;
		}
	    }
	    strLine=br.readLine();
    	    strTok= new StringTokenizer(strLine);
	    if(!strTok.nextToken().equals("Raton"))
	    {
		System.out.println("Error. Se esperaba la etiqueta Raton");
		return null;
	    }
	    entradax=new Integer(strTok.nextToken()).intValue();
	    entraday=new Integer(strTok.nextToken()).intValue();

	    strLine=br.readLine();
    	    strTok= new StringTokenizer(strLine);
	    if(!strTok.nextToken().equals("Gato"))
	    {
		System.out.println("Error. Se esperaba la etiqueta Gato");
		return null;
	    }
	    posgatox=new Integer(strTok.nextToken()).intValue();
	    posgatoy=new Integer(strTok.nextToken()).intValue();
	 

	    strLine=br.readLine();
    	    strTok= new StringTokenizer(strLine);
	    if(!strTok.nextToken().equals("Salida"))
	    {
		System.out.println("Error. Se esperaba la etiqueta Salida");
		return null;
	    }
	    salidax=new Integer(strTok.nextToken()).intValue();
	    saliday=new Integer(strTok.nextToken()).intValue();
	    res[salidax][saliday]=0;
	   
	    in.close();
	}
	catch (Exception e)
	{
	    //System.err.println("Error: " + e.getMessage());
            //e.printStackTrace();
	}
	return res;
    }



    int[][] hazLaberinto(int s)
    {
	return leeLaberintoFichero();
    }
}

