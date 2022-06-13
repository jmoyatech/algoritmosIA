
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * Implementación del algoritmo genético SSGA (Steady-state Genetic Algorithm)
 * que puede consultarse en http://cs.gmu.edu/~sean/book/metaheuristics/Essentials.pdf (pág. 45)
 * 
 * En este algoritmo se trabaja directamente sobre la población inicial, es decir, no hay mecanismos de sustituación o reemplazo total
 * de una generación a otra (normalmente P=P') y eso lo convierte en más rápido que el AG simple.
 * Por otra parte, aunque gaste menos memoria y sea más rápido que el algoritmo general, los padres (aunque puedan ser eliminados aleatoriamente)
 * suelen estar presentes durante bastante tiempo lo que conlleva el riesgo de que converja prematuramente a un número reducido de individuos.
 * 
 * En el algoritmo original se puede observar que su condición de parada es que encuentre el mejor o que se agote el tiempo, 
 * en su lugar en este algoritmo se ha elegido un número elevado de generaciones.
 * 
 * En cuanto a la representación de los individuos se ha optado por una matriz bidimensional donde se almacena tanto el individuo como su fitness,
 * por ejemplo el individuo 3412 con fitness 4 estaría dentro de esta matriz como 34124. 
 * El operador de selección se hace en base a la proporcionalidad de su fitness (ruleta).
 * El operador de cruce se hace en base a un punto de cruce.
 * El operador de mutación muta, si se hace, un gen dentro de un individuo aleatoriamente.
 * seleccionarParaMatar() reemplaza aleatoriamente N individuos por N hijos ya tuvieran peor fitness o no (introduce diversidad).
 * 
 */
public class AlgoritmoGenetico1 extends Algoritmo {

    int tamSecuencia;
    int tamOportunidades;
    int numeroColores;
    int tamPoblacion;
    int nGeneraciones;
    double probMutacion;
    double[][] poblacion;
    double[][] padres;
    double[][] hijos;
    double puntuacionTotal;
    int ptoCruce;
    int nHijos;
    int nPadres;
    Random nAleatorio;

    void inicializa() {
        tamSecuencia = tablero.getDb().getTamSecuencia();
        tamOportunidades = tablero.getDb().getTamOportunidades();
        numeroColores = tablero.getDb().getNumeroColores();
        tamPoblacion = 200;
        nGeneraciones = 50;
        probMutacion = 0.1;
        ptoCruce = 2;
        puntuacionTotal = 0;
        nPadres = 2; //el número de hijos y padres han de coincidir
        nHijos = 2;
        poblacion = new double[tamPoblacion][tamSecuencia + 1];
        padres = new double[nPadres][tamSecuencia + 1];
        hijos = new double[nHijos][tamSecuencia + 1];
        nAleatorio = new Random();
    }

    void soluciona() {
        inicializa();
        int i = 0, j = 0;
        double[] mejorIndividuo = new double[tamSecuencia + 1];
        int[] mejorIndividuo2 = new int[tamSecuencia];
        generarPoblacionInicial();

        for (int k = 0; k < tamPoblacion; k++) //me quedo con el mejor para poder comparar con los hijos después
        {
            if (poblacion[k][4] >= mejorIndividuo[4]) {
                mejorIndividuo = poblacion[k];
            }
        }

        while (i < tamOportunidades - 1) { //Para cada n generaciones del AG tendremos una jugada
            j = 0;
            while (j < nGeneraciones) { //nGeneraciones debe ser un número elevado
                fitnessPoblacion();
                seleccionConRemplazo();
                cruzarPadres();
                mutarHijos();

                for (int k = 0; k < hijos.length; k++) { //Si hay un hijo con mejor fitness que el mejorIndividuo, ese hijo será el nuevo mejorIndividuo
                    hijos[k][4] = fitness(hijos[k]); //aunque es posible que sea "matado" (o reemplazado) en el siguiente operador.
                    if (hijos[k][4] > fitness(mejorIndividuo)) {
                        for (int l = 0; l < tamSecuencia + 1; l++) {
                            mejorIndividuo[l] = hijos[k][l];
                        }
                    }
                }

                if (mejorIndividuo[4] == 8.0) {
                    i = tamOportunidades - 1;
                    break;
                }
                seleccionarParaMatar();
                puntuacionTotal = 0;
                j++;

            }
            for (int k = 0; k < tamSecuencia; k++) { //Este for sirve para pasar a enteros (y así poder actualizar el tablero) al mejorIndividuo
                mejorIndividuo2[k] = (int) mejorIndividuo[k];
            }
            tablero.actualiza(mejorIndividuo2);
            i++;
            try {
                Thread.sleep(retardo);
            } catch (InterruptedException ie) {
                System.out.println(ie.getMessage());
            }
        }
        for (int h = 0; h < tamSecuencia; h++) { //Se copia a la solución el mejorIndividuo
            solucion[h] = mejorIndividuo2[h];
        }
        return;

    }

    void generarPoblacionInicial() { //Genera la población inicial aleatoriamente
        for (int i = 0; i < tamPoblacion; i++) {
            for (int j = 0; j < tamSecuencia; j++) {
                poblacion[i][j] = nAleatorio.nextInt(numeroColores);
            }
        }
    }

    void fitnessPoblacion() { //Obtenemos el fitness
        for (int i = 0; i < tamPoblacion; i++) {
            puntuacionTotal += fitness(poblacion[i]);
        }
        for (int i = 0; i < tamPoblacion; i++) {
            poblacion[i][4] = fitness(poblacion[i]) / puntuacionTotal;
        }
    }

    void seleccionConRemplazo() { //También conocido como selección en ruleta o visto en clase de teoría, selección proporcional al fitness con números aleatorios

        poblacion[0][4] = fitness(poblacion[0]) / puntuacionTotal;
        for (int i = 1; i < tamPoblacion; i++) {
            poblacion[i][4] = poblacion[i - 1][4] + poblacion[i][4];
        }

        for (int i = 0; i < hijos.length; i++) {
            double aleatorio = nAleatorio.nextDouble();
            for (int j = 0; j < tamPoblacion; j++) {
                if (aleatorio <= poblacion[j][4]) {
                    padres[i] = poblacion[j];
                    break;
                }
            }
        }
    }

    void cruzarPadres() { //cruza n padres por el punto cruce
        for (int i = 1; i < hijos.length; i++) {
            for (int j = 0; j < ptoCruce; j++) {
                hijos[i - 1][j] = padres[i - 1][j];
            }
            for (int k = ptoCruce; k < tamSecuencia; k++) {
                hijos[i - 1][k] = padres[i][k];
            }
            i++;
        }
        for (int i = 1; i < hijos.length; i++) {
            for (int j = 0; j < ptoCruce; j++) {
                hijos[i][j] = padres[i][j];
            }
            for (int k = ptoCruce; k < tamSecuencia; k++) {
                hijos[i][k] = padres[i - 1][k];
            }
            i++;
        }

    }

    void mutarHijos() { //si un hijo muta, lo hace a nivel de gen
        for (int i = 0; i < hijos.length; i++) {
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

    void seleccionarParaMatar() { //N individuos son elegidos aleatoriamente para ser reemplazados por N hijos
        int indices[] = new int[hijos.length];

        ArrayList<Integer> aleatorios = new ArrayList<Integer>(); //estas 3 líneas sirven para que los índices sean únicos
        for (int i = 0; i < tamPoblacion; i++) {
            aleatorios.add(i);
        }
        Collections.shuffle(aleatorios);

        for (int i = 0; i < hijos.length; i++) {
            indices[i] = aleatorios.get(i);
        }
        for (int i = 0; i < hijos.length; i++) {
            for (int j = 0; j < tamSecuencia + 1; j++) {
                poblacion[indices[i]][j] = hijos[i][j];
            }
        }
    }

    double fitness(double[] individuo) { //fitness donde las blancas valen 1.0 y las negras 2.0 (copiado del algoritmo HillClimbing)
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

    void imprimirArray(double[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println("");
        }
    }

    void imprimirVector(double[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }
}
