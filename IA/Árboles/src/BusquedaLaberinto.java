import java.util.ArrayList;
import java.util.Stack;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.Random;


public class BusquedaLaberinto  implements ActionListener, Runnable
{

    int [][]mapa;  /* Espacio de búsqueda. Array bidimensional que almacena el laberinto.
		      En cada posición, 0 -> casilla libre, 1-> pared. */
    int tam;  // Tamaño del laberinto 


    Nodo nodoInicio;    
    Nodo nodoObjetivo;

    Nodo posActual; /* Posicion actual del agente */

    
    ArrayList <Nodo> movimiento; /* Movimiento a realizar: 1 elemento en el caso del ratón y 2 en el caso del gato */
    
    boolean perseguido;                /* Indica si es el agente perseguido (true)  o el perseguidor (false) */
    int numeroMovimientos;           /* 1 para el ratón y 2 para el gato */
    int numeroMovimientosAdversario; /* 2 para el ratón y 1 para el gato */

    boolean teclado=false; /* Indica si la clase se maneja por teclado */
    /* Para los movimientos por teclado */
    /* Indica si se han terminado los movimientos por teclado
       para el turno actual (1 para el ratón y 2 para el gato) */
    boolean teclaPulsada;
    Nodo movimientoTeclado;
    int indiceMov; /* Indica si hay varios movimientos por turno (gato) cuál es el que toca pintar */
    Timer timer= new Timer(600000, this); /* Tiempo límite por movimiento (1 minuto) */
    Random  random=new Random();
    boolean movimientoAleatorio;
    Thread th;
    boolean fin;
    
    BusquedaLaberinto()
    {
    }
   
    /*  Método para definir el laberinto sobre el que realizar la búsqueda

    Parámetros:
       
       m -> Array bidimensional de enteros con la configuración del laberinto. En cada posición: 1-> pared, 0-> hueco
       iniciox, inicioy: Coordenadas x e y de la entrada del laberinto
       finx, finy: Coordenadas x e y de la salida del laberinto 
 
    */  
      
        
    void defineLaberinto(int [][]m, int entradax, int entraday, int salidax, int saliday) 
    {
	mapa=m;
	tam=m.length;
	movimiento=new ArrayList<Nodo>();
	nodoInicio=new Nodo(entradax,entraday,0,0);
	posActual=nodoInicio;
	nodoObjetivo=new Nodo(salidax,saliday,0,0);
     }

    void setPerseguido(boolean per)
    {
	perseguido=per;
	if(perseguido) 
	{
	    numeroMovimientos=1;
	    numeroMovimientosAdversario=2;
	}
	else 
        {
	    numeroMovimientos=2;
	    numeroMovimientosAdversario=1;
	}
    }       


    /***************************************************************************************************************************
     *                                API para implementar algorimos de búsqueda sobre laberintos                              *
     ***************************************************************************************************************************/



    /* API.  Comprueba si un nodo cualquiera es nodo objetivo (Test Objetivo) */

    boolean esNodoObjetivo(Nodo n) 
    {
        return n.igual(nodoObjetivo);
    }


    /* Comprueba si un nodo representa una posición válida del laberinto (no hay pared y está dentro de los límites del laberinto). Es un método
       auxiliar para simplificar la función getVecinos */

    boolean posicionValida(Nodo nodo)
    {
	int x=nodo.getX();
	int y=nodo.getY();

	if(x<0||y<0||x>=mapa.length||y>=mapa[0].length||mapa[x][y]!=0) return false;	
	else return true;
    }


    /* Devuelve cierto si en la posición especificada hay un muro */

    boolean muro(int x, int y)
    {
	if(x<0||y<0||x>=mapa.length||y>=mapa[0].length||mapa[x][y]==0) return false;	
	else return true;
    }


    /* Comprueba si la posición del adversario almacenada en el nodo es válida */

    boolean posicionValidaAdversario(Nodo nodo)
    {
	int x=nodo.getAX();
	int y=nodo.getAY();
	
	if(x<0||y<0||x>=mapa.length||y>=mapa[0].length||mapa[x][y]!=0) return false;
	else return true;
    }


    /* Comprueba si un movimiento es un movimiento válido */

    boolean movimientoValido()
    {      
	int incx=movimiento.get(0).getX()-posActual.getX();
	int incy=movimiento.get(0).getY()-posActual.getY();
	if(!posicionValida(movimiento.get(0))) return false;
	for(int i=1;i<movimiento.size();i++)
	{	    
	    if(Math.abs(incx)+Math.abs(incy)>1)
	    {
		System.err.println("ERROR. El movimiento es inválido (sólo se puede avanzar una posición en horizontal o vertical");
		return false;
	    }
	    if(!posicionValida(movimiento.get(i))) return false;
	    incx=movimiento.get(i).getX()-movimiento.get(i-1).getX();
	    incy=movimiento.get(i).getY()-movimiento.get(i-1).getY();	    
	}
	    
	return true;
    }
	

    /* API. Devuelve la lista de movimientos vecinos a uno dado */

    ArrayList<Nodo> obtenVecinos(Nodo nodo)
    {

        ArrayList<Nodo> vecinos=new ArrayList<Nodo>();

        for(int i=-1;i<=1;i++)
            for(int j=-1;j<=1;j++)
                if(Math.abs(i+j)==1) 
                 {
                     Nodo aux=new Nodo(nodo.getX()+i,nodo.getY()+j,nodo.getAX(), nodo.getAY());
                     if(posicionValida(aux)) vecinos.add(aux);
                 }
	return vecinos;
    }


    /* Mide la distancia (filas+columnas) entre los dos agentes */

    int distanciaAdversario(Nodo nodo)
    {
	return Math.abs(nodo.getX()-nodo.getAX())+Math.abs(nodo.getY()-nodo.getAY());
    }


    /* Devuelve el conjunto de nodos asociados a un movimiento del adversario desde
       el nodo pasado como parámetro */

    ArrayList<Nodo> obtenVecinosAdversario(Nodo nodo)
    {

	ArrayList<Nodo> vecinos=new ArrayList<Nodo>();
	ArrayList<Nodo> vecinosaux=new ArrayList<Nodo>();
	int prevDist=distanciaAdversario(nodo);

        for(int i=-1;i<=1;i++)
            for(int j=-1;j<=1;j++)
		if(Math.abs(i+j)==1) 
                {
		    Nodo aux=new Nodo(nodo.getX(),nodo.getY(),nodo.getAX()+i,nodo.getAY()+j);
		    if(posicionValidaAdversario(aux)&&distanciaAdversario(aux)<=prevDist) vecinosaux.add(aux);
                }
	

	for(int k=0;k<vecinosaux.size();k++)
	{
	    Nodo v=vecinosaux.get(k);
	    prevDist=distanciaAdversario(v);

	    int anadidos=0;
	    for(int i=-1;i<=1;i++)
		for(int j=-1;j<=1;j++)
		    if(Math.abs(i+j)==1) 
		    {
			Nodo aux=new Nodo(v.getX(),v.getY(),v.getAX()+i,v.getAY()+j);
			if(posicionValidaAdversario(aux)&&distanciaAdversario(aux)<=prevDist) 
			{
			    boolean encontrado=false;
			    for(int m=0;m<vecinos.size()&&!encontrado;m++)
				if(aux.igual(vecinos.get(m)))
				    encontrado=true;
			    
			    if(!encontrado)
			    {
				vecinos.add(aux);		      
				anadidos++;
			    }
			}		
		    }
	    if(anadidos==0) vecinos.add(v);
	}
	if(vecinos.size()==0) vecinos.add(new Nodo(nodo.getX(),nodo.getY(),nodo.getAX(), nodo.getAY()));
	return vecinos;
    }


    /* API. Obtiene el siguiente movimiento a realizar por parte del agente. Método a implementar por
       las clases derivadas que codifiquen el algoritmo MinimMax */

    void obtenMovimiento(int posAdversarioX, int posAdversarioy)
    {
	/* Como resultado, este método debe guardar en el atributo movimiento  (definido como un ArrayList de nodos)
	 los movimientos hay realizar (sólo uno para el caso del ratón) */ 
    }

    /***************************************************************************************************************************
     *                                                    Fin del API                                                          *
     ***************************************************************************************************************************/


    /* Realiza un movimiento y comprueba si es correcto */
    boolean validaMovimiento(Nodo pA)	
    {
	movimientoAleatorio=false;
	th=new Thread(this, "hilo busqueda");
	posActual.setAX(pA.getX());
	posActual.setAY(pA.getY());
	timer.stop();
	fin=false; 
	
	th.start();
	while(!fin)
  	try
	{ 
	    Thread.sleep(1);  
	} 
	catch (InterruptedException ie)
	{    
	    System.out.println(ie.getMessage());
	}
 
	if(th!=null)
	{
	    th.interrupt();
	    th=null;
	}
	indiceMov=0;
	return movimientoValido();
    }


    Nodo siguientePosicion()
    {
	if(indiceMov>=numeroMovimientos) return null;

	if(teclado)
	{   
	    while(!teclaPulsada)
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
	    teclaPulsada=false;
	    indiceMov++;
	    return movimientoTeclado;
	}

	else  return movimiento.get(indiceMov++);
		    	    
    }

    
    public void run() 
    {

	try 
	{
	    timer.start();
	    obtenMovimiento(posActual.getAX(), posActual.getAY());
	    timer.stop();
            Thread.sleep(1);
        } 
	catch( InterruptedException e ) 
	{
	fin=true;
        }

	fin=true;
    }


    void inicializaTeclado()
    {
	teclaPulsada=false;
	indiceMov=0;
    }

    void movimientoTeclado(Movimiento m)
    {
	Nodo temp=new Nodo(posActual.getX(),posActual.getY(),0,0);

	switch(m)
	{
	      case ARRIBA:temp.setX(posActual.getX()-1);
         		  break;
	      case ABAJO:temp.setX(posActual.getX()+1);
             		  break;
	      case IZQUIERDA:temp.setY(posActual.getY()-1);
       		          break;
	      case DERECHA:temp.setY(posActual.getY()+1);
		   	  break;
  	      default:  break;
	}
	
	 if(posicionValida(temp))
	 {
	    movimientoTeclado=temp;
	 }
	 teclaPulsada=true;	    
    }


   
    public void actionPerformed(ActionEvent e) 
    {
	if(th!=null)
        {
	    th.interrupt();
	    th.stop();
	    th=null;	 
	}

	ArrayList <Nodo> vecinos=obtenVecinos(posActual);
	int rnd=random.nextInt(vecinos.size());
	movimiento.add(vecinos.get(rnd));
	movimientoAleatorio=true;
	fin=true;

    }

}


