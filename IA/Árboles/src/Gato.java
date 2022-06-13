import java.util.Random;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;

public class Gato extends BusquedaLaberinto
{


    int evalua(Nodo n)
    {
	return Math.abs(n.getX()-n.getAX())+Math.abs(n.getY()-n.getAY());
    }


    Nodo raton;

    void obtenMovimiento(int posAdversarioX, int posAdversarioY)
    {	

	posActual.setAX(posAdversarioX);
	posActual.setAY(posAdversarioY);
	ArrayList<Nodo> vecinos=obtenVecinos(posActual);
	int mejorDist=evalua(posActual);
	Nodo mejorNodo=posActual;
	for(int i=0;i<vecinos.size();i++)
	{
	    if(evalua(vecinos.get(i))<mejorDist)
	    {
		mejorNodo=vecinos.get(i);
		mejorDist=evalua(vecinos.get(i));
	    }
	}
		
	movimiento.add(mejorNodo);

	vecinos=obtenVecinos(mejorNodo);
	for(int i=0;i<vecinos.size();i++)
	{
	    if(evalua(vecinos.get(i))<mejorDist)
	    {
		mejorNodo=vecinos.get(i);
		mejorDist=evalua(vecinos.get(i));
	    }
	}
	
	movimiento.add(mejorNodo);
    }
}


 		   
