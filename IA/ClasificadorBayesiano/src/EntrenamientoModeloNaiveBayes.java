
import java.io.*;

class EntrenamientoModeloNaiveBayes {
    /* Se asume que cada muestra es una vector de tam elementos */

    int tam;
    /* Clasificador incremental. Almacena los contadores de cada
    elemento. Por simplicidad se utilizan arrays unidimensionales
    de tam x tam */
    int contadores[];
    /* Después de procesar cada muestra  se escriben las probabilidades
    en este array */
    double probabilidades[];

    /* Número de muestras vistas hasta la fecha */
    int contadorMuestras;
    /* Etiqueta de clase a la que representa el modelo */
    String etiqueta;

    EntrenamientoModeloNaiveBayes(int t) {
        tam = t;
        contadores = new int[tam];
        probabilidades = new double[tam];
        contadorMuestras = 0;
    }

    void setEtiqueta(String e) {
        etiqueta = e;
    }

    void procesaMuestra(byte[] muestra) {
        for (int i = 0; i < muestra.length; i++) {
            contadores[i] += muestra[i];
        }
        contadorMuestras++;

        /* Ésta debe ser la última línea del método */
        calculaProbabilidades();
    }

    void calculaProbabilidades() {
        for (int i = 0; i < probabilidades.length; i++) {
            if (contadores[i] != 0) {
                probabilidades[i] = (double) ((double) contadores[i] / (double) contadorMuestras);
            }
        }
    }

    void guardaModelo(String nombreFichero) {
        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(nombreFichero));
        } catch (IOException i) {
            System.err.println("Error. No se pudo abrir el fichero de modelos (1) " + nombreFichero);
            System.exit(-1);
        }
        try {
            bf.write(etiqueta);
            for (int i = 0; i < probabilidades.length; i++) {
                if (i % (tam / 28) == 0) {
                    bf.write("\n");
                }
                bf.write(probabilidades[i] + " ");
            }
            bf.flush();
        } catch (IOException i) {
            System.err.println("Error escribiendo en fichero de modelos " + nombreFichero);
            System.exit(-1);
        }

    }
}
