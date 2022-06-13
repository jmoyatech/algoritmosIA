import java.util.ArrayList;
import java.util.Stack;


public class BusquedaLaberinto
{

    int [][]mapa;  /* Espacio de búsqueda. Array bidimensional que almacena el laberinto.
		      En cada posición, 0 -> casilla libre, 1-> pared. */
    int tam;  // Tamaño del laberinto 


    Nodo nodoInicio;     /* Los nodos inicial y objetivo de la búsqueda son únicos (la entrada y salida del laberinto) */
    Nodo nodoObjetivo;

    ArrayList<Nodo> camino;   /* La solución se codifica como una array de contiene las posiciones a recorrer para salir del laberinto */ 

    int retardo; /* Ralentiza la búsqueda */
    
   
    /* Atributos para almacenar algunas estadísticas relativas a la búsqueda */

    public long nodosExpandidos;

	

    	
    /* Constructor. Vacío */

    BusquedaLaberinto()
    {
	nodosExpandidos=0;
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
	nodoInicio=new Nodo(entradax,entraday);
	//	nodo_final=new Nodo(tam+1,tam-2);
	nodoObjetivo=new Nodo(salidax,saliday);
	camino=new ArrayList<Nodo>();
	nodosExpandidos=0;
     }


    /***************************************************************************************************************************
     *                                API para implementar algorimos de búsqueda sobre laberintos                              *
     ***************************************************************************************************************************/

    /* API.  Devuelve el nodo inicial para el algoritmo de búsqueda */
	
    Nodo devuelveNodoInicio() 
    {
	return nodoInicio;
    }


    /* API.  Comprueba si un nodo cualquiera es nodo inicial */

    boolean esNodoInicio(Nodo n) 
    {
        return n.igual(nodoInicio);
    }


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
	
	if(x<0||y<0||x>=tam||y>=tam||mapa[x][y]!=0) return false;
	else return true;
    }



    /* API. Devuelve la lista de movimientos vecinos a uno dado */

    ArrayList<Nodo> obtenVecinos(Nodo nodo)
    {
	ArrayList<Nodo> vecinos=new ArrayList<Nodo>();
	if(nodosExpandidos>25000)
	{
	    System.err.println("ERROR. Se alcanzó el límite de 25000 nodos expandidos");
	    return vecinos;
	}

	for(int i=1;i>=-1;i--)
	    for(int j=1;j>=-1;j--)
		if(Math.abs(i+j)==1) 
		 {
		     Nodo aux=new Nodo(nodo.getX()+i,nodo.getY()+j);
		     if(posicionValida(aux)) vecinos.add(aux);
		 }
	nodosExpandidos++;


	/* El retardo se aplica al expandir un nodo */
	if(retardo>0)
	 try
	 {
	     Thread.sleep(retardo);  
	 } catch (InterruptedException ie)
	 {
		System.out.println(ie.getMessage());
	 }

	return vecinos;
    }



    /* API. Obtiene el camino para salir del laberinto. Este es el método que hay que sobrecargar en las clases derivadas */

    void obtenCamino()
    {
	/* Como resultado, este método debe guardar en el atributo camino (definido como un ArrayList) el conjunto 
	   de nodos desde el nodo inicial (entrada del laberinto) hasta el nodo objetivo (salida) */

    }

    /* API. Comprueba si el camino encontrado es correcto */

    boolean compruebaCamino()
    {
	if(camino.size()==0) return false;
	if(!camino.get(0).igual(nodoInicio))
	{
	    System.err.println("ERROR. El camino no parte del estado inicial");
	    return false;
	}
	
	if(!camino.get(camino.size()-1).igual(nodoObjetivo))
	{
	    
	    System.err.println("ERROR. El camino no llega al estado objetivo");
	    return false;
	}


	for(int i=1;i<camino.size();i++)
	{
	    if(mapa[camino.get(i).getX()][camino.get(i).getY()]>0)
	    {
		System.err.println("ERROR. El camino atraviesa un muro");
		return false;
	    }

	    int incx=camino.get(i).getX()-camino.get(i-1).getX();
	    int incy=camino.get(i).getY()-camino.get(i-1).getY();
	    if(Math.abs(incx+incy)!=1) 
	    {
		System.err.println("ERROR. El camino hace un movimiento inválido (sólo se puede avanzar una posición en horizontal o vertical");
		return false;
	    }
	}
	return true;
    }
	    

    /* API. Imprime el camino por consola */
    void imprimeCamino()
    {
	for(int i=0;i<camino.size();i++)
	    System.out.println("("+camino.get(i).getX()+","+camino.get(i).getY()+")");
    }
    


    /***************************************************************************************************************************
     *                                                    Fin del API                                                          *
     ***************************************************************************************************************************/



    /* Convierte el camino en un array bidimensional */


    int [][] arrayCamino()
    {
	int [][]res=new int[camino.size()][2];
	for(int i=0;i<camino.size();i++)
	{
	    res[i][1]=camino.get(i).getX();
	    res[i][0]=camino.get(i).getY();
	}
	return res;
    }



    /* Fija un retardo al expandir los nodos */
    void setRetardo(int r)
    {
	retardo=r;
    }

    /* Obtiene el número de nodos expandidos */

    public long getNodosExpandidos()
    {
	return nodosExpandidos;
    }
}