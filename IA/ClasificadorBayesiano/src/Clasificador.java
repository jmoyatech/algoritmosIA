
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;

class ModeloDigitoNaiveBayes {

    String etiqueta;
    double[] probabilidades;

    ModeloDigitoNaiveBayes(String nomFichero) {
        probabilidades = new double[28 * 28];
        BufferedReader bf = null;
        String linea = "";
        try {
            bf = new BufferedReader(new FileReader(nomFichero));
        } catch (IOException i) {
            System.err.println("Error. No se pudo abrir el fichero de modelos (2) " + nomFichero);
            System.exit(-1);
        }
        try {
            etiqueta = bf.readLine();
            linea = etiqueta;
            StringTokenizer strTok = new StringTokenizer(linea);
            int y = 0;
            while (y < 28) {
                linea = bf.readLine();
                strTok = new StringTokenizer(linea);
                int x = 0;
                while (strTok.hasMoreTokens()) {
                    probabilidades[28 * y + x] = new Double(strTok.nextToken()).doubleValue();
                    x++;
                }
                y++;
            }
            bf.close();
        } catch (IOException i) {
            System.err.println("Error leyendo fichero de modelos " + nomFichero);
            System.exit(-1);
        }

    }

    double probabilidad(byte[] vector) {

        double probabilidad = 1.0;
        
        for(int i=0;i<vector.length;i++)
        {
            if(vector[i]==0)
                probabilidad += Math.log(1-probabilidades[i]);
            else probabilidad += Math.log(probabilidades[i]);
	    
        }
        return probabilidad;
    }

    void imprimeModelo() {
        for (int i = 0; i < probabilidades.length; i++) {
            System.out.print(probabilidades[i] + " ");
        }
        System.out.println("");
    }
}

public class Clasificador {

    ModeloDigitoNaiveBayes modelos[];
    double probabilidades[];

    Clasificador(String ficheroModelos) {

        BufferedReader bf = null;
        String linea = "";
        List<String> nombres = new ArrayList<String>();
        //	List nombres=new ArrayList();

        try {
            bf = new BufferedReader(new FileReader(ficheroModelos));
        } catch (FileNotFoundException i) {
            System.err.println("Error. No existe el fichero " + ficheroModelos);
            System.exit(-1);
        }
        try {
            linea = bf.readLine();
            while (linea != null) {
                /* La primera línea contiene la etiqueta de clase */
                nombres.add(linea);
                linea = bf.readLine();
            }
        } catch (IOException i) {
            System.err.println("Error leyendo del fichero " + ficheroModelos);
            System.exit(-1);
        }
        String ficheros[] = nombres.toArray(new String[nombres.size()]);
        modelos = new ModeloDigitoNaiveBayes[ficheros.length];
        probabilidades = new double[ficheros.length];

        for (int i = 0; i < ficheros.length; i++) {
            modelos[i] = new ModeloDigitoNaiveBayes(ficheros[i]);
        }

    }

    String clasifica(byte vector[]) {
	    
        for (int i = 0; i < probabilidades.length; i++) {
            probabilidades[i] = modelos[i].probabilidad(vector);
        }
        int indice = 0;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i<probabilidades.length;i++) {
            if(probabilidades[i]>max) { 
                max = probabilidades[i];
                indice = i;
            }
            
        }

        return modelos[indice].etiqueta;
    }

    public static void main(String s[]) {
        Clasificador cl = new Clasificador(s[0]);
    }
}
