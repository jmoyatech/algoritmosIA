
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.Date;



public class Main extends JApplet implements KeyListener 
{

    /* Objetos gráficos */
    DibujaLaberinto db;
    JPanel infoP=new JPanel();
    JTextField infoT=new JTextField(42);    
    Font font = new Font("Times New Roman", Font.PLAIN,13);
    GeneraLaberinto laberinto;
    int [][]mapa;
    int tam=8;
    int tam_limite=14;
    int incremento_tam=3;
    boolean graficos=true;
    int tamventana=600;

    long score=0;
    

    String []parametros={"-dimventana","-raton","-gato","-nivel-inicial", "-h"};

    enum Acciones{LABERINTO_SIMPLE, SUCESION_LABERINTOS, MUESTRA_LABERINTO}
    Acciones accion=Acciones.LABERINTO_SIMPLE;
    JFrame ventana = null;
    String claseBusquedaRaton="NULL", claseBusquedaGato="Gato";

    /* Objetos de la clase del Framework de Busqueda */

    BusquedaLaberinto raton=null;
    BusquedaLaberinto gato=null;

    BusquedaLaberinto actual, adversario;
    String nombreLaberinto=getClass().getClassLoader().getResource("escenarios/sc.1").getFile();
    //String nombreLaberinto="./escenarios/sc.1";
    

    public Main() throws HeadlessException {
    }
    int numNiveles=4;
    int nivelInicial=1;

    /* Movimiento según el teclado */


    Main(String s[])
    {

	leeParametros(s);
	infoT.setEditable(false);
	infoT.setFocusable(false);
	if(ventana!=null)
	{
	    ventana.addWindowListener(
		new WindowAdapter() 
		{
		    public void windowClosing(WindowEvent e) 
		    {
			System.exit(0);
		    }
		});

	}


    }

    /* Función auxiliar tomada del comecocos de Standford para obtener
     * en tiempo de ejecución el nombre e una clase */

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




    /* Métodos para gestionar los parámetros de entrada y ejecutar la acción correspondiente */


    void muestraUso()
    {
	System.out.println("java Main [parametros]\n\nParametros\n----------\n");
	System.out.println("   -h. Esta ayuda ");
	System.out.println("   -dimventana tam. Tamaño de la ventana en pixels");
	System.out.println("   -raton.Clase que implementa el comportamiento del raton");
	System.out.println("   -gato. Clase que implementa el comportamiento del gato");
	System.out.println("   -nivel-inicial Nivel inicial en el que comienza el juego");
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
	accion=Acciones.LABERINTO_SIMPLE;

	for(int i=0;i<s.length;i++)
	{	   
	    int a=parametroAEntero(s[i]);
	    switch(a)
	    {

	       case 0: if(i<s.length-1)
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

	       /* Clase que implementa el algoritmo de búsqueda para el ratón */
	       
	       case 1: if(i<s.length-1)
			{
			    claseBusquedaRaton=s[i+1];
			    i++;
			}
		       else
		       {
			   muestraUso();
			   System.exit(-1);
		       }
		       break;
		       
	       /* Clase que implementa el algoritmo de búsqueda para el gato */
	       
	       case 2: if(i<s.length-1)
		       {
			    claseBusquedaGato=s[i+1];
			    i++;
		       }
		       else
		       {
			   muestraUso();
			   System.exit(-1);
		       }
		       break;

  	       case 3: if(i<s.length-1)
		       {
			   try
                           {
                                nivelInicial=new Integer(s[i+1]).intValue();
                                i++;
                            }
                            
                            catch(NumberFormatException e)
                            {
                                nivelInicial=1;
                            }
		       }
		       break;
  	         case 4: muestraUso(); System.exit(-1);

	    }
	}
	


	int i=nivelInicial;
	while(i<numNiveles)
	 {
	     nombreLaberinto=nombreLaberinto.substring(0,nombreLaberinto.length()-1)+Integer.toString(i);
	     laberinto=new GeneraLaberinto(nombreLaberinto);
	     laberintoSimple();
	     i++;
	 }
	infoT.setText("PUNTUACION FINAL = " +  score);
        laberinto= new GeneraLaberinto(getClass().getClassLoader().getResource("escenarios/fin.sc").getFile());
	mapa=laberinto.hazLaberinto(tam);
	muestraLaberinto();

	try
	{
		Thread.sleep(15000);
	}

	catch (InterruptedException ie)
	{    
		System.out.println(ie.getMessage());
	}
	System.exit(-1);


    }
			   

    
    /* Método que ejecuta un único laberinto */

    boolean laberintoSimple()
    {

	boolean resuelto=false;
	String sResuelto;
	int numMovimientos=0;
	
		
	mapa=laberinto.hazLaberinto(tam);
	
	/* Inicializar los agentes de búsqueda */
	if(claseBusquedaRaton!="NULL")
	    raton=(BusquedaLaberinto) getNewObjectByName(claseBusquedaRaton, new Class[0], new Object[0]);
	else
	{ 
	    raton=new BusquedaLaberinto();
	    raton.teclado=true;
	}
	raton.setPerseguido(true);
	if(raton.teclado) raton.inicializaTeclado();

	/* Pasar al objeto ratón el laberinto generado */       
	raton.defineLaberinto(mapa, laberinto.entradax, laberinto.entraday, laberinto.salidax, laberinto.saliday);
	if(claseBusquedaGato!="NULL")
	    gato=(BusquedaLaberinto) getNewObjectByName(claseBusquedaGato, new Class[0], new Object[0]);
	else
	{
	    gato=new BusquedaLaberinto();
	    gato.teclado=true;
	}
	gato.setPerseguido(false);
	if(gato.teclado) gato.inicializaTeclado();
	/* Pasar al objeto gato el laberinto generado */       
	gato.defineLaberinto(mapa, laberinto.posgatox, laberinto.posgatoy, laberinto.salidax, laberinto.saliday);	
	
	actual=raton;
	adversario=gato;


	if(graficos)
	{
	    muestraLaberinto();
	}

	boolean fin=false;
	int mov;
	int jugador=0;
	String sactual="";
	System.out.println(" Comienza el juego ");
	while(!fin&&numMovimientos<100)
	{
	    
	    if(jugador==0) {actual=raton;adversario=gato;sactual="RATÓN";}
	    else {actual=gato;adversario=raton;sactual="GATO";}
	    infoT.setText("TURNO DEL " + sactual);
	    /************* Turno de juego ********************/	    
	    actual.movimiento.clear();
	    if(actual.teclado==false)
	    {		
		if(!actual.validaMovimiento(adversario.posActual))
		{
		    System.out.println("El " + sactual+ "  realizó un movimiento inválido");
		    System.exit(-1);
		}
		if(actual.movimientoAleatorio==true) 
		{
		    infoT.setText("TIEMPO SUPERADO !!! Movimiento aleatorio");
		    try
		    {
			Thread.sleep(500);
		    }

		    catch (InterruptedException ie)
		    {    
			System.out.println(ie.getMessage());
		    }
		}

	    }
	    else 
	      actual.inicializaTeclado();
	    try
	    {
		Thread.sleep(500);
	    }

	    catch (InterruptedException ie)
	    {    
			System.out.println(ie.getMessage());
	    }

	    Nodo movActual=actual.siguientePosicion();
	    while(movActual!=null&&!fin)
	    {
		if(graficos) db.pintaMovimiento(movActual.getX(),movActual.getY(),actual.posActual.getX(), actual.posActual.getY(),jugador);
		while(db.fin==false)
		{	
		    try
		    { 
			Thread.sleep(2);  
		    } 
		    catch (InterruptedException ie)
		    {    
			System.out.println(ie.getMessage());
		    }
		}
		actual.posActual=movActual;
		if(raton.esNodoObjetivo(raton.posActual))
		{
		    fin=true;
		    resuelto=true;
		}
		else
		    if(gato.posActual.getX()==raton.posActual.getX()&&
		       gato.posActual.getY()==raton.posActual.getY())
		{
		    fin=true;
		    resuelto=false;
		}
		movActual=actual.siguientePosicion();
	    }
	    jugador=(jugador+1)%2;
	    numMovimientos++;	
	}
	if(fin)
	    if(resuelto) score+=1000+2*(100-numMovimientos);
	    else score+=-500+numMovimientos;
	else score+=numMovimientos;
	
	return resuelto;
    }


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
	ventana.addKeyListener(this);
	db=new DibujaLaberinto(mapa,tamventana,tamventana,laberinto.entradax,laberinto.entraday,laberinto.salidax, laberinto.saliday, laberinto.posgatox, laberinto.posgatoy);	
	db.addKeyListener(this);

	ventana.setSize(tamventana,tamventana+30);
	ventana.getContentPane().add(db);
	infoP.setBackground(Color.BLACK);
	ventana.getContentPane().add(infoP,BorderLayout.SOUTH);
	infoP.add(infoT,BorderLayout.NORTH);
	infoT.setBackground(Color.BLACK);
	infoT.setForeground(Color.CYAN);
	infoT.setVisible(false);
	infoT.addKeyListener(this);
	infoT.setFont(font);
	infoP.setVisible(true);
	infoT.setVisible(true);

	ventana.setBackground(Color.BLACK);
	ventana.setVisible(true);
	
    }


    /* Métodos de gestión del teclado. Sólo se utiliza  el "keyPressed" */

    public void keyPressed(KeyEvent e)
    {
	int keyCode = e.getKeyCode();
	switch( keyCode ) 
	{ 
           case KeyEvent.VK_UP: actual.movimientoTeclado(Movimiento.ARRIBA);
	                        break;
 	   case KeyEvent.VK_DOWN:actual.movimientoTeclado(Movimiento.ABAJO);
	                        break;
	   case KeyEvent.VK_LEFT:actual.movimientoTeclado(Movimiento.IZQUIERDA);
 	                        break; 
   	   case KeyEvent.VK_RIGHT: actual.movimientoTeclado(Movimiento.DERECHA);
	                        break;
 	   default:     break;
     }
    }
		
    public void keyReleased(KeyEvent e)
    {   
    }		

    public void keyTyped(KeyEvent e)
    {
    }		

   public static void main(String s[])
    {
	    Main m=new Main(s);
    }
}