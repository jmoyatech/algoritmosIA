import java.util.Random;


public class AlgoritmoHillClimbing extends Algoritmo {

	int tamSecuencia;
	int tamOportunidades;
	int numeroColores;
	
	
	
	// inicializamos los valores del tablero (comodidad)
	void inicializa() {
		tamSecuencia=tablero.getDb().getTamSecuencia();
		tamOportunidades=tablero.getDb().getTamOportunidades();
		numeroColores=tablero.getDb().getNumeroColores();
	}
	
	
	
	// M�todo que caracteriza lo buena que es o no una permutaci�n mirando los pines negros y blancos que tiene con respecto a la soluci�n (c�digo similar a tablero.actualiza())
	// En los algoritmos que se implementen se supone que debe haber una funci�n "parecida"
		int caracteriza(int []perm) {
			int tamSecuencia=tablero.getDb().getTamSecuencia();
	    	int i,j;
	    	int []visitadosSol=new int[tamSecuencia];
	    	int []visitadosPerm=new int[tamSecuencia];
	    	int indVisitados;
	    	int contador=0;
	    	
	    	int []sol=tablero.getSol();
	    	
	    	// para controlar que no haya repeticiones innecesarias en las cadenas
	    	for (i=0;i<tamSecuencia;i++) visitadosSol[i]=-1;
	    	for (i=0;i<tamSecuencia;i++) visitadosPerm[i]=-1;
	    	indVisitados=0;
	    	
	    	boolean flag=true; // flag que controla si se ha llegado a la soluci�n
	    	
	    	// recorremos la secuencia de la soluci�n buscando posiciones iguales o desordenadas en la permutaci�n pasada por argumento
	    	
	    	// primero rellenamos los pines negros
	    	for (i=0;i<tamSecuencia;i++) {
	    		if (sol[i]==perm[i]) {
	    			contador+=2;
	    			visitadosSol[indVisitados]=i;
					visitadosPerm[indVisitados++]=i;
	    		}
	    		else flag=false;
	    	}
	    	
	    	// si no es la permutaci�n soluci�n, rellenamos los pines blancos
	    	if (flag==false) {
		    	for (i=0;i<tamSecuencia;i++) {
	    			for (j=0;j<tamSecuencia;j++) {
	    				if ((!tablero.estaEnArray(j,visitadosPerm,indVisitados))&&(!tablero.estaEnArray(i,visitadosSol,indVisitados))) {
	    					if (sol[i]==perm[j]) {
	    						contador++;
		    					visitadosSol[indVisitados]=i;
		    					visitadosPerm[indVisitados++]=j;
		    					j=tamSecuencia;
	    					}
	    				}
	    			}
	    		}
	    	}
	    	
	    	//desordenamos el orden en que salen los pines blancos y negros
	    	
	    	
	    	
	    	//si no hemos terminado, devolvemos false.
	    	return contador;
		}
	
	
	
	
	// sobreescribimos el m�todo de la clase algoritmo a implementar para solucionar el tablero
    @Override
	void soluciona() {
		
		inicializa();
		
		int []guess=new int[tablero.getDb().getTamSecuencia()];
		for (int j=0;j<tablero.getDb().getTamSecuencia();j++) guess[j]=r.nextInt(numeroColores-1);
		int []pines=tablero.actualiza(guess);
		int []newGuess=new int[tamOportunidades];
		
		
		
		int i=0;
		while (i<tamOportunidades-2) {
			if (caracteriza(guess)==2*tamSecuencia) { // si hemos llegado a la soluci�n, ya hemos terminado (condici�n de final de bucle)
				i=tamOportunidades-2;
			}
			else if (caracteriza(newGuess)>=caracteriza(guess)) { // si la actualizaci�n de la permutaci�n es buena, la introducimos como nueva soluci�n al tablero
			
				pines=tablero.actualiza(newGuess);
				try
				 {
				     Thread.sleep(retardo);  
				 } catch (InterruptedException ie)
				 {
					System.out.println(ie.getMessage());
				 }
				 tablero.getDb().repaint();
				 guess=newGuess;
				 i++;
			}
			
			newGuess=renueva(guess,pines); // actualizamos la permutaci�n soluci�n
		}
		
		for (i=0;i<tamSecuencia;i++) solucion[i]=guess[i]; // copiamos la soluci�n
	}
	
	// actualiza la permutaci�n soluci�n a partir de la permutaci�n anterior y sus pines
	public int[] renueva(int []guess, int []pines) {
		int []newGuess=new int[tamSecuencia];
		int []posUtilizadas=new int[tamSecuencia];
		int []pos2Utilizadas;
		int i,j,pos,pos2;
		int tam=tamSecuencia;
		
		
		// inicializamos
		for (j=0;j<tamSecuencia;j++) {
			newGuess[j]=-1;
			posUtilizadas[j]=j;
		}
		
		// buscamos el n�mero de pines blancos y negros que tiene la permutaci�n
		int numeroNegras=0, numeroBlancas=0;
		for (i=0;i<tamSecuencia;i++) {
			if (pines[i]==1) {
				numeroNegras++;
			}
			else if (pines[i]==0) {
				numeroBlancas++;
			}
		}
		
		// rellenamos las negras aleatoriamente
		int aux,aux2;
		i=0;
		while (i<numeroNegras) {
			pos=r.nextInt(tam);
			
			// intercambio las posiciones pos y tam-1 para reducir el array
			aux=posUtilizadas[pos];
			posUtilizadas[pos]=posUtilizadas[tam-1];
			posUtilizadas[tam-1]=-1;
			tam--;
			
			newGuess[aux]=guess[aux];
			i++;
		}
		
		// rellenamos las blancas aleatoriamente
		pos2Utilizadas=new int[tam];
		for (i=0;i<tam;i++) pos2Utilizadas[i]=posUtilizadas[i];
		i=0;
		while (i<numeroBlancas) {
			pos=r.nextInt(tam);
			
			// intercambio las posiciones pos y tam-1 para reducir el array
			aux=posUtilizadas[pos];
			posUtilizadas[pos]=posUtilizadas[tam-1];
			posUtilizadas[tam-1]=-1;
			pos2=r.nextInt(tam);
			if (pos2==pos) {
				pos2=r.nextInt(tam);
			}
			
			// intercambio las posiciones pos2 y tam-1 para reducir el array
			aux2=pos2Utilizadas[pos2];
			pos2Utilizadas[pos2]=pos2Utilizadas[tam-1];
			pos2Utilizadas[tam-1]=-1;
			tam--;
			
			
			newGuess[aux]=guess[aux2];
			i++;
		}
		
		
		// rellenamos los huecos restantes aleatoriamente
		for (i=0;i<tamSecuencia;i++) {
			if (newGuess[i]==-1) {
				newGuess[i]=r.nextInt(numeroColores);
			}
		}
		
		return newGuess;
	}
	
	
	
}
