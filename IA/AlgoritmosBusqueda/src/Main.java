
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.Date;



public class Main extends JApplet
{

    /* Objetos gr�ficos */
    DibujaLaberinto db;
    JPanel infoP=new JPanel();
    JTextField infoT=new JTextField(42);
    Font font = new Font("Times New Roman", Font.PLAIN,13);
    GeneraLaberinto laberinto;
    int [][]mapa;
    int tam=10;
    int tam_limite=14;
    int incremento_tam=3;
    boolean graficos=true;
    int tamventana=600;

    long tiempoEjecucion;


    


    /* Par�metros:
        -tam : Lanza un laberinto �nico de un tama�o determinado (por defecto 10)
	-practica: Lanza un conjunto de laberintos de tama�o cada vez mayor hasta un l�mite (opcional tras este par�metro)
	-batch: No muestra el interfaz gr�fico, s�lo los resultados del algoritmo de b�squeda */

    String []parametros={"-tama�o","-practica","-batch","-dimventana","-busqueda","-semilla-fija"};

    enum Acciones{LABERINTO_SIMPLE, SUCESION_LABERINTOS, MUESTRA_LABERINTO}
    Acciones accion=Acciones.LABERINTO_SIMPLE;
    JFrame ventana = null;
    String claseBusqueda="NULL";

    /* Objeto de la clase del Framework de Busqueda */

    BusquedaLaberinto busqueda=null;


    Main(String s[])
    {
	leeParametros(s);
	if(ventana!=null)
	    ventana.addWindowListener(
		new WindowAdapter() 
		{
		    public void windowClosing(WindowEvent e) 
		    {
			System.exit(0);
		    }
		});

    }

    /* Funci�n auxiliar tomada del comecocos de Standford para obtener
     * en tiempo de ejecuci�n el nombre e una clase */

    public static Object getNewObjectByName(String className, Class[] classes, Object[] args) 
    {
	Object o = null;
	try 
	{
	    Class classDefinition = Class.forName(className);
	    Constructor constructor = classDefinition.getConstructor(classes);
	    o = constructor.newInstance(args);
	} 
	catch (Exception e) 
	{
	    System.err.println("Clase " + className + " no encontrada");
	    System.exit(-1);
	    
	}    
	return o;
    }


    



    /* M�todos para gestionar los par�metros de entrada y ejecutar la acci�n correspondiente */


    void muestraUso()
    {
	System.out.println("java Main [parametros]\n\nParametros\n----------\n");
	System.out.println("   -busqueda nombre_fichero. Utiliza el fichero .class especificado como algoritmo de b�squeda (debe contener una subclase de la clase BusquedaLaberinto) ");
	System.out.println("   -tama�o valor. Ejecuta un laberinto �nico cuyo tama�o viene definido por \"valor\"");
	System.out.println("   -practica [tama�o_maximo] [incremento]. Ejecuta una sucesi�n de laberintos de tama�o creciente. Puede especificarse opcionalmente el tama�o m�ximo y el incremento de tama�o entre laberintos.");
	System.out.println("   -dimventana tam. Tama�o de la ventana en pixels");
	System.out.println("   -batch. No muestra el laberinto gr�ficamente. S�lo muestra los resultados en forma de texto.");
	System.out.println("   -semilla-fija Utiliza una semilla fija para que los laberintos no cambien de una ejecuci�n a otra."); 
	System.out.println("\n Por defecto, si no se especifican par�metros ejecuta un laberinto �nico de tama�o 10 con los gr�ficos activados. \n");
    }


    int parametroAEntero(String s)
    {
	for(int i=0;i<parametros.length;i++)
	    if(s.equals(parametros[i])) return i;
	return (-1);
    }



    void leeParametros(String s[])
    {

        boolean semillaFija=false;

	for(int i=0;i<s.length;i++)
	{	   
	    int a=parametroAEntero(s[i]);
	    switch(a)
	    {

		/* Laberinto �nico */
  	        case 0: if(i<s.length-1)
		        {
			    try
			    {
				tam=new Integer(s[i+1]).intValue();
				i++;
			    }

			    catch(NumberFormatException e)
			    {
				System.err.println("Despu�s del par�metro -tam hay que especificar un entero");
				System.exit(-1);
			    }
			}
		        else
		        {
			    System.err.println("Despu�s del par�metro -tam hay que especificar un entero");
			    System.exit(-1);
			}
			    
			/* No se permiten tama�os muy peque�os o muy grandes */
		        if(tam<4||tam>5000) tam=10;
			accion=Acciones.LABERINTO_SIMPLE;
			break;

  	       /* Sucesi�n de laberintos cada vez mayores */
   	       case 1: 
 		        /* Se intenta leer el tama�o maximo, si lo hay */
		        if(i<s.length-1)
		        {
			    try
			    {
				tam_limite=new Integer(s[i+1]).intValue();
				i++;
			    }
			    
			    catch(NumberFormatException e)
			    {
				tam_limite=100;
			    }
			}
			
			/* Se intenta leer el incremento de tama�o, si lo hay */
			if(i<s.length-1)
 		        {
				try
				{
				    incremento_tam=new Integer(s[i+1]).intValue();
				    if (incremento_tam%2==1) incremento_tam++;
				    i++;
				}

				catch(NumberFormatException e)
				{
				    incremento_tam=3;
				}
				
			}
	    
  		        accion=Acciones.SUCESION_LABERINTOS;
			break;
  	       /* Desactivar gr�ficos */		
   	       case 2: graficos=false;
		       break;

	       /* Tama�o de la ventana */	       
	       
	       case 3: if(i<s.length-1)
		        {
			    try
			    {
				tamventana=new Integer(s[i+1]).intValue();
				i++;
			    }

			    catch(NumberFormatException e)
			    {
				muestraUso();
				System.exit(-1);
			    }
			}
		       else
		       {
			   muestraUso();
			   System.exit(-1);
		       }
		       break;

	       /* Clase que implementa el algoritmo de b�squeda */	       
	       
	       case 4: if(i<s.length-1)
			{
			    claseBusqueda=s[i+1];
			    i++;
			}
		       else
		       {
			   muestraUso();
			   System.exit(-1);
		       }
		       break;
		       
 	        case 5:	semillaFija=true;
		        break;
 	       default: muestraUso();
 		        System.exit(-1);
	    }
	}
	if(claseBusqueda=="NULL") accion=Acciones.MUESTRA_LABERINTO;

	laberinto=new GeneraLaberinto(semillaFija);
	
	switch(accion)
	{
  	    case LABERINTO_SIMPLE:laberintoSimple();
	                          break;
	    	
	    case SUCESION_LABERINTOS:sucesionLaberintos();  			                                  break;
	    case MUESTRA_LABERINTO: mapa=laberinto.hazLaberinto(tam);
 		                    muestraLaberinto();
				    break;
	}
    }
			   

    
    /* M�todo que ejecuta un �nico laberinto */

    boolean laberintoSimple()
    {

	boolean resuelto=false;
	String sResuelto;

		
	mapa=laberinto.hazLaberinto(tam);

	/* Inicializar objeto de la clase del Framework */
	busqueda=(BusquedaLaberinto) getNewObjectByName(claseBusqueda, new Class[0], new Object[0]);
	
	/* Pasar al objeto el laberinto generado */
	
	busqueda.defineLaberinto(mapa, laberinto.entradax, laberinto.entraday, laberinto.salidax, laberinto.saliday);


	if(graficos)
	{
	    muestraLaberinto();
	    infoT.setText("Buscando ...");
	    if(busqueda!=null) busqueda.setRetardo(20);	    
	}


	/* C�lculo del tiempo de ejecuci�n */
	Date tiempoInicio = new Date();
	/* Llamada al m�todo que calcula el camino. Cambiar por el del
	 * framework */
	busqueda.obtenCamino();
	Date tiempoFinal = new Date();
	resuelto=busqueda.compruebaCamino();
	tiempoEjecucion = tiempoFinal.getTime() - tiempoInicio.getTime();
	if(resuelto) sResuelto="SI.";
	    else sResuelto="NO.";
	if(graficos)
	{	 
	    infoT.setText("Resuelto : " +sResuelto+ " Tiempo = " +  String.valueOf(tiempoEjecucion)+ "ms. Expandidos = " + busqueda.getNodosExpandidos()+". Long. camino = " + busqueda.camino.size());
	    muestraCamino(busqueda.arrayCamino());
	}

	else
	{
	    System.out.println("\nLaberinto tama�o " + tam);
	    System.out.println("---------------------\n");
	    System.out.println("Resuelto = " + sResuelto);
	    System.out.println("Tiempo = " + String.valueOf(tiempoEjecucion));
	    System.out.println("Nodos Expandidos = " + String.valueOf(busqueda.getNodosExpandidos()));
            System.out.println("Longitud Camino = " + String.valueOf(busqueda.camino.size()));
	    System.out.println("");
	}
	return resuelto;
    }

    /* M�todo que ejecuta una sucesi�n de laberintos de tama�o cada vez mayor */  

    void sucesionLaberintos()
    {

    /* Estad�sticas de la b�squeda en modo BATCH */

    long tiempoAcumulado=0; 
    long nodosExpandidosTotales=0;
    int laberintosResueltos=0;
    int longitudCaminos=0;
    int numeroLaberintos=0;


	while(tam<tam_limite)
	{
	    if(laberintoSimple()) laberintosResueltos++;
	    tam+=incremento_tam;
	    tiempoAcumulado+=tiempoEjecucion;
	    nodosExpandidosTotales+=busqueda.getNodosExpandidos();
	    longitudCaminos+=busqueda.camino.size();
	    numeroLaberintos++;
	}
	
	System.out.println("\n\nRESULTADOS FINALES");
	System.out.println("------------------\n");
	System.out.println("LABERINTOS INTENTADOS = " + numeroLaberintos);
	System.out.println("LABERINTOS RESUELTOS = " + laberintosResueltos);
	System.out.println("TIEMPO TOTAL B�SQUEDA = " + tiempoAcumulado + " ms." );
	System.out.println("NODOS TOTALES EXPANDIDOS = " + nodosExpandidosTotales);
        System.out.println("LONGITUD CAMINO = " + longitudCaminos);
	System.out.println("");


    }

	 


    /* M�todo que muestra el laberinto gr�ficamente */

    void muestraLaberinto()
    {
	if(ventana!=null)
	{
	    ventana.dispose();
	    ventana=null;
	 }
	ventana=new JFrame("Laberinto");
	ventana.getContentPane().setBackground(Color.BLACK);
	ventana.getContentPane().setLayout(new BorderLayout());
	db=new DibujaLaberinto(mapa,tamventana,tamventana,laberinto.entradax,laberinto.entraday,laberinto.salidax, laberinto.saliday);	

	ventana.setSize(tamventana,tamventana+30);
	ventana.getContentPane().add(db);
	infoP.setBackground(Color.BLACK);
	ventana.getContentPane().add(infoP,BorderLayout.SOUTH);
	infoP.add(infoT,BorderLayout.NORTH);
	infoT.setBackground(Color.BLACK);
	infoT.setForeground(Color.CYAN);
	infoT.setFont(font);
	infoP.setVisible(true);
	infoT.setVisible(true);

	ventana.setBackground(Color.BLACK);
	ventana.setVisible(true);

	
    }

    /* M�todo que muestra el camino obtenido gr�ficamente */

    void muestraCamino(int [][]c)
    {
	db.pintaCamino(c);
	while(db.fin==false) 
	{
	  try
	  {
	      Thread.sleep(100);  
 
	  }catch (InterruptedException ie)
	  {
	      System.out.println(ie.getMessage());
	  }
	}
	try
	{
	    Thread.sleep(2000);  
	}catch (InterruptedException ie)
	{
	    System.out.println(ie.getMessage());
	}
    }


		
    public static void main(String s[])
    {
	    Main m=new Main(s);
    }
}