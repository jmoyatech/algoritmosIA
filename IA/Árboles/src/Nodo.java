/**************************************************************************************************************************
 *             Clase Nodo. Define la estructura de un nodo que representa un estado en el problema a resolver             *
 **************************************************************************************************************************/



public class Nodo 
{

    /* Posiciones del jugador */
    int x;
    int y;
    /* Posiciones del adversario*/
    int ax;
    int ay;
    int profundidad; /* Profundidad del nodo. Funcion g */
    int valor;       /* Valor asignado al nodo según la función de evaluación */
    boolean min;     /* True -> Nodo min. False -> Nodo max */
    Nodo padre;
    static int infinito=-99999999;

    

    Nodo(int x, int y, int ax, int ay) 
    {
	this.x=x;
	this.y=y;
	this.ax=ax;
	this.ay=ay;
	valor=infinito;
    }


    /* Funciones para acceder a los atributos de la clase */
	
    int getX() 
    {
	return x;
    }
	
    int getY() 
    {
	return y;
    }

    void setX(int v) 
    {
	x=v;
    }

    void setY(int v) 
    {
	y=v;
    }

    int getAX() 
    {
	return ax;
    }
	
    int getAY() 
    {
	return ay;
    }

    void setAX(int v) 
    {
	ax=v;
    }

    void setAY(int v) 
    {
	ay=v;
    }
	
    int getProfundidad()
    {
	return profundidad;
    }

    void setProfundidad(int p)
    {
	profundidad=p;
    }

    int getValor()
    {
	return valor;
    }

    void setValor(int h)
    {
	valor=h;
    }

    
    Nodo getPadre()
    {
	return padre;
    }

    void setPadre(Nodo n)
    {
	padre=n;
    }
	
    /* Funcion para comparar dos nodos */

    boolean igual(Nodo n)
    {
	if(n.x!=x||n.y!=y) return false;
	else return true;
    }


    /* Imprime el nodo en una cadena */

    public String toString()
    {
	return("Nodo   x = " + x + " y = " + y + "   ax = " + ax + " ay = " + ay + " profundidad = " + profundidad + "  valor = " + valor + "  min = " + min + "\n"/*+ " padre = " +padre*/);
    }
	
}
