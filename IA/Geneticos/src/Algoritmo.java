import java.util.Random;


public class Algoritmo {

	GeneraTablero tablero;
	Random r;
	
	// Variable que retarda la aparición de las sucesivas iteraciones.
	int retardo;
	
	// Debe contener, al final de la ejecución de soluciona, la permutación solución del algoritmo.
	int []solucion;
	
	// Constructor vacío
	Algoritmo() {
		
	}
	
	// Método que hace las veces de constructor (inicializa)
	void defineTablero(GeneraTablero tablero, int retardo) {
		this.tablero=tablero;
		this.retardo=retardo;
		r=new Random(1);
		solucion=new int[tablero.getDb().getTamSecuencia()];
	}
	
	// Método que resuelve el tablero (a sobreescribir en las clases a implementar)
	void soluciona() {
		
		
	}
	
	// Método que comprueba que una solución dada es correcta
	boolean compruebaSolucion() {
		
		boolean flag=true;
		
		for (int i=0;i<tablero.getDb().getTamSecuencia();i++) if (solucion[i]!=tablero.getSol()[i]) flag=false;
		
		return flag;
	}
	
	
	
	// Devuelve el tablero
	GeneraTablero getTablero() {
		return tablero;
	}
}
