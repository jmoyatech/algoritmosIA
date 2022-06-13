import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

/* Clase para generar un tablero */

class GeneraTablero {

    Random r;
    DibujaTablero db;
    int sol[];
    int tamOportunidades;
    int tamSecuencia;
    int numeroColores;
    int iters;
    
    int valor;


    // Constructor de la clase
    GeneraTablero(boolean semilla, int tamOportunidades, int tamSecuencia, int numeroColores, boolean visible) {
    	
    	iters=1;
    	
		if(semilla==true) r=new Random(tamOportunidades);
		else r=new Random();
		
		this.tamOportunidades=tamOportunidades;
		this.tamSecuencia=tamSecuencia;
		this.numeroColores=numeroColores;
		
		db=new DibujaTablero(tamOportunidades, tamSecuencia, numeroColores, visible);
		
		creaSolucion();
    }
    
    // Crea una solución aleatoria al problema
    public void creaSolucion() {
    	int [][]soluciones=db.getSoluciones();
    	int [][]pines=db.getPines();
    	
    	for (int j=0;j<tamSecuencia;j++) soluciones[0][j]=r.nextInt(numeroColores);
    	for (int j=0;j<tamSecuencia;j++) pines[0][j]=1;
    	
    	sol=new int[tamSecuencia];
    	for (int j=0;j<tamSecuencia;j++) sol[j]=soluciones[0][j];
    }
    
    
    // Método a sobreescribir en las clases implementadas
    void soluciona() {
    	
    }
    
    
    // Método que, dada una permutación resultado de una iteración de algoritmos genéticos, actualiza el estado del gráfico
    public int [] actualiza(int perm[]) {
    	int [][]soluciones=db.getSoluciones();
    	int [][]pines=db.getPines();
    	int i,j;
    	int []visitadosSol=new int[tamSecuencia];
    	int []visitadosPerm=new int[tamSecuencia];
    	int indVisitados;
    	
    	if (perm==null) iters=iters-1;
    	
    	// para controlar que no haya repeticiones innecesarias en las cadenas
    	for (i=0;i<tamSecuencia;i++) visitadosSol[i]=-1;
    	for (i=0;i<tamSecuencia;i++) visitadosPerm[i]=-1;
    	indVisitados=0;
    	
    	boolean flag=true; // flag que controla si se ha llegado a la solución
    	
    	// recorremos la secuencia de la solución buscando posiciones iguales o desordenadas en la permutación pasada por argumento
    	
    	// primero rellenamos los pines negros
    	for (i=0;i<tamSecuencia;i++) {
    		if (perm!=null) soluciones[tamOportunidades-iters][i]=perm[i];
    		if (sol[i]==soluciones[tamOportunidades-iters][i]) {
    			pines[tamOportunidades-iters][indVisitados]=1;
    			visitadosSol[indVisitados]=i;
				visitadosPerm[indVisitados++]=i;
				valor+=2;
    		}
    		else flag=false;
    	}
    	
    	// si no es la permutación solución, rellenamos los pines blancos
    	if (flag==false) {
	    	for (i=0;i<tamSecuencia;i++) {
    			for (j=0;j<tamSecuencia;j++) {
    				if ((!estaEnArray(j,visitadosPerm,indVisitados))&&(!estaEnArray(i,visitadosSol,indVisitados))) {
    					if (sol[i]==soluciones[tamOportunidades-iters][j]) {
	    					pines[tamOportunidades-iters][indVisitados]=0;
	    					visitadosSol[indVisitados]=i;
	    					visitadosPerm[indVisitados++]=j;
	    					j=tamSecuencia;
	    					valor++;
    					}
    				}
    			}
    		}
    	}
    	
    	//desordenamos el orden en que salen los pines blancos y negros
    	desordena(pines[tamOportunidades-iters],indVisitados);
    	
    	iters++;
    	
    	db.repaint();
    	
    	return pines[tamOportunidades-iters+1];
    }
    
    
    // Devuelve le dibujo del tablero
    DibujaTablero getDb() {
    	return db;
    }
    
    
    
    // Busca si num está guardado dentro de perm
    boolean estaEnArray(int num, int perm[], int tam) {
    	for (int i=0;i<tam;i++) if (perm[i]==num) return true;
    	return false;
    }
    
    
    // Cambia el orden en que aparecen los pines en el juego
    void desordena(int perm[], int tam) {
    	int aux, aleat;
    	for (int i=0;i<tam;i++) {
    		aleat=r.nextInt(tam);
    		aux=perm[i];
    		perm[i]=perm[aleat];
    		perm[aleat]=aux;
    	}
    }
    
    // Devuelve las iteraciones
    int getIters() {
    	return iters;
    }
    
    int getValor() {
    	return valor;
    }
    
    int []getSol() {
    	return sol;
    }
    
    Random getR() {
    	return r;
    }
    
}

    
    


    





