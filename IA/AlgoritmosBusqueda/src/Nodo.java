/**************************************************************************************************************************
 *             Clase Nodo. Define la estructura de un nodo que representa un estado en el problema a resolver             *
 **************************************************************************************************************************/



public class Nodo 
{
    int x;
    int y;
    int profundidad; /* Profundidad del nodo. Función g */
    int heuristica;  /* Valor de la función heurística. Función f */
    Nodo padre;
    

    Nodo(int x, int y) 
    {
	this.x=x;
	this.y=y;
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

    int getProfundidad()
    {
	return profundidad;
    }

    void setProfundidad(int p)
    {
	profundidad=p;
    }

    int getHeuristica()
    {
	return heuristica;
    }

    void setHeuristica(int h)
    {
	heuristica=h;
    }

    
    Nodo getPadre()
    {
	return padre;
    }

    void setPadre(Nodo n)
    {
	padre=n;
    }
	
    /* Función para comparar dos nodos */

    boolean igual(Nodo n)
    {
	if(n.x!=x||n.y!=y) return false;
	else return true;
    }


    /* Imprime el nodo en una cadena */

    public String toString()
    {
	return("Nodo \n  x = " + x + "\n  y = " + y + "\n   profundidad = " + profundidad + "\n   heuristica = " + heuristica + "\n");
    }
	
}
