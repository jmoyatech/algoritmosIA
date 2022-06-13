
public class AlgoritmoAleatorio extends Algoritmo {

	int tamSecuencia;
	int tamOportunidades;
	int numeroColores;
	
	
	// inicializamos los valores del tablero (comodidad)
	void inicializa() {
		tamSecuencia=tablero.getDb().getTamSecuencia();
		tamOportunidades=tablero.getDb().getTamOportunidades();
		numeroColores=tablero.getDb().getNumeroColores();
	}
	
	
	
	
	
	
	
	// Resolvemos el tablero de forma aleatoria
	void soluciona() {
		
		inicializa();
		
		int []guess=new int[tamSecuencia];
		for (int i=0;i<tamOportunidades-1;i++) {
			
			// creamos una permutaci�n aleatoria
			for (int j=0;j<tamSecuencia;j++) guess[j]=r.nextInt(numeroColores);
			
			// actualizamos el tablero con ella
			tablero.actualiza(guess);
			
			// esto s�lo se ejecuta si hay retardo
			try
			 {
			     Thread.sleep(retardo);  
			 } catch (InterruptedException ie)
			 {
				System.out.println(ie.getMessage());
			 }
		}
		
		// copiamos la soluci�n de nuestro algoritmo en el atributo soluci�n.
		for (int i=0;i<tamSecuencia;i++) solucion[i]=guess[i];
	}
	
	
}
