
import java.util.Random;
/*
 * Algoritmo gen�tico sencillo donde la codificaci�n de los cromosomas estar� representada en un array bidimensional ([][]poblacion) con enteros.
 * Su fitness ser� un vector de manera que fitness[k] ser� el fitness del individuo/cromosoma k.
 * La selecci�n se realizar� en base a n�meros aleatorios y los fitness acumulados. Tambi�n est� la posibilidad de hacerlo con torneo (seleccion2()). 
 * Los elegidos se guardar�n en el array candidatos.
 * El cruce ser� en un punto y la mutaci�n ser� a nivel de individuo con n�meros aleatorios de forma que si un individuo muta, mutar� aleatoriamente un gen.
 * El cruce tambi�n podr� realizarse con una m�scara y la mutaci�n tambi�n podr� variar todos los genes de un individuo (gen+1). cruce2() y mutacion2() respectivamente.
 * Los individuos resultantes de la seleccion, cruce y mutaci�n ser�n los hijos (array hijos) que a su vez tendr�n un fitness asociado (fitnessHijos).
 * 
 */

public class AlgoritmoGenetico extends Algoritmo {

    int tamSecuencia;
    int tamOportunidades;
    int numeroColores;
    int tamPoblacion;
    int nGeneraciones;
    double probMutacion;
    int[][] poblacion;
    int[][] candidatos;
    int[][] hijos;
    double[] fitness;
    double[] fitnessAcumulado;
    double[] fitnessHijos;
    int ptoCruce;
    double puntuacionTotal;
    Random nAleatorio;
    int[] mascara;

    void inicializa() { //inicializo variables (comodidad)
        tamSecuencia = tablero.getDb().getTamSecuencia();
        tamOportunidades = tablero.getDb().getTamOportunidades();
        numeroColores = tablero.getDb().getNumeroColores();
        tamPoblacion = 200; //ha de ser un n�mero par para que al cruzarse no d� problemas
        nGeneraciones = 20;
        probMutacion = 0.1;
        ptoCruce = 2;
        mascara = new int[tamSecuencia];
        poblacion = new int[tamPoblacion][tamSecuencia];
        candidatos = new int[tamPoblacion][tamSecuencia];
        hijos = new int[tamPoblacion][tamSecuencia];
        fitness = new double[tamPoblacion];
        fitnessHijos = new double[tamPoblacion];
        fitnessAcumulado = new double[tamPoblacion];
        puntuacionTotal = 0;
        nAleatorio = new Random();
    }

    void soluciona() {
        inicializa();

        int i = 0, j;
        int[] mejorIndividuo = new int[tamSecuencia];
        int[] mejorIndividuoGeneracion = new int[tamSecuencia];
        generarPoblacionInicial(); //genero una poblaci�n inicial aleatoria

        while (i < tamOportunidades - 1) { //para cada n generaciones del AG tendremos una jugada
            j = 0;
            //generarMascara(); //Hay que descomentarlo en el caso de hacer las pruebas con cruce2()
            while (j < nGeneraciones) { //criterio de parada del AG para una jugada: se ha llegado al n�mero de generaciones m�ximas 
                System.out.println("************************************************");
                System.out.println("GENERACI�N " + (j + 1) + " ITERACI�N " + (i + 1));
                System.out.println("************************************************");
                System.out.println("PADRES: ");
                imprimirArray(poblacion);
                fitnessPoblacion();
                fitnessAcumulado(); //En el caso de hacer las pruebas con fitnessPoblacion2() hay que comentarlo.
                seleccion();
                cruce();
                mutacion();
                fitnessHijos();

                double fitnessMejorIndividuoGeneracion = 0;
                for (int k = 0; k < tamPoblacion; k++) { //con este for obtengo el mejorindividuo de una generaci�n y su fitness
                    if (fitnessHijos[k] > fitnessMejorIndividuoGeneracion) {
                        fitnessMejorIndividuoGeneracion = fitnessHijos[k];
                        mejorIndividuoGeneracion = hijos[k];
                    }
                }

                if (fitness(mejorIndividuoGeneracion) >= fitness(mejorIndividuo)) { //me voy quedando con el mejor individuo de todas las generaciones
                    System.arraycopy(mejorIndividuoGeneracion, 0, mejorIndividuo, 0, tamSecuencia);
                }

                System.out.println("HIJOS:");
                imprimirArray(hijos);
                System.out.println("mejorIndividuo: ");
                imprimirVector(mejorIndividuo);
                System.out.println("con fitness: " + fitness(mejorIndividuo));

                if (fitness(mejorIndividuo) == 2 * tamSecuencia) { //si hemos llegado a la soluci�n se para de generar e iterar
                    i = tamOportunidades - 1;
                    break;
                }

                for (int l = 0; l < tamPoblacion; l++) { //poblacion=hijos para la siguiente generaci�n
                    for (int m = 0; m < tamSecuencia; m++) {
                        poblacion[l][m] = hijos[l][m];
                    }
                }
                puntuacionTotal = 0;
                j++;

            }
            i++;
            tablero.actualiza(mejorIndividuo); //Se actualiza el tablero cada jugada (n generaciones)
            try {
                Thread.sleep(retardo);
            } catch (InterruptedException ie) {
                System.out.println(ie.getMessage());
            }
        }


        for (int h = 0; h < tamSecuencia; h++) {
            solucion[h] = mejorIndividuo[h]; //se copia al mejor indidivuo en la soluci�n
        }
        return;
    }

    void generarPoblacionInicial() { //Se genera una poblaci�n inicial aleatoriamente
        for (int i = 0; i < tamPoblacion; i++) {
            for (int j = 0; j < tamSecuencia; j++) {
                poblacion[i][j] = nAleatorio.nextInt(numeroColores);
            }
        }
    }

    void fitnessPoblacion() { //obtengo el fitness de la poblaci�n de manera que fitness[k] es el fitness de k
        for (int i = 0; i < tamPoblacion; i++) {
            puntuacionTotal += fitness(poblacion[i]);
        }
        for (int i = 0; i < tamPoblacion; i++) {
            fitness[i] = fitness(poblacion[i]) / puntuacionTotal;
        }

    }

    void fitnessAcumulado() { //Obtengo los fitness acumulados en [0,1] para poder realizar la selecci�n.
        fitnessAcumulado[0] = fitness[0] / puntuacionTotal;
        for (int i = 1; i < tamPoblacion; i++) {
            fitnessAcumulado[i] = fitnessAcumulado[i - 1] + fitness[i];
        }
    }

    void seleccion() { //La selecci�n se hace en base a n�meros aleatorios y al fitness acumulado
        for (int i = 0; i < tamPoblacion; i++) {
            double aleatorio = nAleatorio.nextDouble();
            for (int j = 0; j < tamPoblacion; j++) {
                if (aleatorio <= fitnessAcumulado[j]) {
                    candidatos[i] = poblacion[j];
                    break;
                }
            }
        }
    }

    void cruce() {
        //Cruce por un punto (definido en inicializa()) de manera que si los padres fueran
        //5505 y 1348 y el pto. de cruce estuviera en 2, los hijos ser�an h1=5508 e h2=1345
        for (int i = 1; i < tamPoblacion; i++) {
            for (int j = 0; j < ptoCruce; j++) {
                hijos[i - 1][j] = candidatos[i - 1][j];
            }
            for (int k = ptoCruce; k < tamSecuencia; k++) {
                hijos[i - 1][k] = candidatos[i][k];
            }
            i++;
        }
        for (int i = 1; i < tamPoblacion; i++) {
            for (int j = 0; j < ptoCruce; j++) {
                hijos[i][j] = candidatos[i][j];
            }
            for (int k = ptoCruce; k < tamSecuencia; k++) {
                hijos[i][k] = candidatos[i - 1][k];
            }
            i++;
        }
    }

    void mutacion() { //Se muta a nivel de individuo un gen aleatoriamente siempre y cuando ese gen cambie
        for (int i = 0; i < tamPoblacion; i++) {
            if (nAleatorio.nextDouble() <= probMutacion) {
                for (int j = 0; j < tamSecuencia; j++) {
                    int pos = nAleatorio.nextInt(tamSecuencia);
                    int aleatorio = nAleatorio.nextInt(numeroColores);
                    while (hijos[i][pos] == aleatorio) {
                        aleatorio = nAleatorio.nextInt(numeroColores);
                    }
                    hijos[i][pos] = aleatorio;
                    break;
                }
            }
        }
    }

    void fitnessHijos() { //Se obtiene los fitness de los hijos
        int puntuacionT = 0;
        for (int i = 0; i < tamPoblacion; i++) {
            puntuacionT += fitness(hijos[i]);
        }
        for (int i = 0; i < tamPoblacion; i++) {
            fitnessHijos[i] = fitness(hijos[i]) / puntuacionT;
        }

    }

    //------- Operadores distintos para hacer las pruebas
    void fitnessPoblacion2() { //Se calcula el fitness de cada individuo
        for (int i = 0; i < tamPoblacion; i++) {
            fitness[i] = fitness(poblacion[i]);
        }
    }

    void seleccion2() { //Selecci�n basada en torneo
        for (int i = 0; i < tamPoblacion; i++) {
            int alea1 = nAleatorio.nextInt(tamPoblacion);
            int alea2 = nAleatorio.nextInt(tamPoblacion);
            candidatos[i] = poblacion[torneo(alea1, alea2)];
        }
    }

    int torneo(int individuo1, int individuo2) { //torneo de tama�o 2
        return fitness(candidatos[individuo1]) >= fitness(candidatos[individuo2]) ? individuo1 : individuo2;
    }

    void cruce2() { //se hace el cruce en base a una m�scara aleatoria
        for (int i = 0; i < tamPoblacion; i++) {
            for (int j = 0; j < tamSecuencia; j++) {
                hijos[i][j] = candidatos[i][j] ^ mascara[j];
            }
        }
    }

    void generarMascara() { //Se genera una m�scara aleatoria para cruce2()
        for (int i = 0; i < tamSecuencia; i++) {
            mascara[i] = nAleatorio.nextInt(numeroColores);
        }
    }

    void mutacion2() { //en mutacion2 se muta a nivel de individuo sumando +1 a todos sus genes. En caso de ser 7 pasar�a a 0.
        for (int i = 0; i < tamPoblacion; i++) {
            if (nAleatorio.nextDouble() <= probMutacion) {
                for (int j = 0; j < tamSecuencia; j++) {
                    if (hijos[i][j] == 7) {
                        hijos[i][j] = hijos[i][j] % numeroColores;
                    } else {
                        hijos[i][j] = hijos[i][j] + 1;
                    }

                }
            }
        }
    }

    void fitnessHijos2() { //Se obtiene el fitness de los hijos
        for (int i = 0; i < tamPoblacion; i++) {
            fitnessHijos[i] = fitness(hijos[i]);
        }
    }

    double fitness(int[] individuo) { //fitness donde las blancas valen 1.0 y las negras 2.0 (copiado del algoritmo HillClimbing)
        int i, j;
        int[] visitadosSol = new int[tamSecuencia];
        int[] visitadosPerm = new int[tamSecuencia];
        int indVisitados;
        int contador = 0;

        int[] sol = tablero.getSol();

        for (i = 0; i < tamSecuencia; i++) {
            visitadosSol[i] = -1;
        }
        for (i = 0; i < tamSecuencia; i++) {
            visitadosPerm[i] = -1;
        }
        indVisitados = 0;
        boolean flag = true;

        for (i = 0; i < tamSecuencia; i++) {
            if (sol[i] == individuo[i]) {
                contador += 2;
                visitadosSol[indVisitados] = i;
                visitadosPerm[indVisitados++] = i;
            } else {
                flag = false;
            }
        }
        if (flag == false) {
            for (i = 0; i < tamSecuencia; i++) {
                for (j = 0; j < tamSecuencia; j++) {
                    if ((!tablero.estaEnArray(j, visitadosPerm, indVisitados)) && (!tablero.estaEnArray(i, visitadosSol, indVisitados))) {
                        if (sol[i] == individuo[j]) {
                            contador++;
                            visitadosSol[indVisitados] = i;
                            visitadosPerm[indVisitados++] = j;
                            j = tamSecuencia;
                        }
                    }
                }
            }
        }
        return contador;
    }

    void imprimirArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println("");
        }
    }

    void imprimirVector(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }
}
