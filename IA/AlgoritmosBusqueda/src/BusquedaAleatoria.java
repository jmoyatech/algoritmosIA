import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;

public class BusquedaAleatoria extends BusquedaLaberinto
{
    /* Algoritmo de búsqueda aleatoria */

    void obtenCamino()
    {
        Nodo previo,actual;
	int indiceVecino;
	ArrayList <Nodo> vecinos;
	Random r=new Random();
	int nodosExplorados=0;

	/* Busqueda */
	actual=nodoInicio;
	camino.add(nodoInicio);
	do
	{
	    vecinos=obtenVecinos(actual);
	    /* Se incluye en el camino un vecino al azar intentando
	       evitar ciclos cortos */
	    previo=actual;
	    indiceVecino=r.nextInt(vecinos.size());
	    if(vecinos.get(indiceVecino).igual(actual))
		indiceVecino=(indiceVecino+1)%vecinos.size();
	    actual=vecinos.get(indiceVecino);
	    camino.add(actual);
	    nodosExplorados++;
	} while(!esNodoObjetivo(actual)&&nodosExplorados<100);
    }

		
}
    



    