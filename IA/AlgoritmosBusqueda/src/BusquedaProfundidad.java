
import java.util.ArrayList;

public class BusquedaProfundidad extends BusquedaLaberinto {

    ArrayList<Nodo> visitados = new ArrayList();

    @Override
    void obtenCamino() {
        busquedaEnProfundidad(devuelveNodoInicio());

        //instrucciones para poder eliminar nodos diagonales en el camino
        ArrayList<Nodo> camino2 = (ArrayList) camino.clone();
        while (hayDiagonales(camino2)) {
            camino = quitarDiagonales(camino2);
            camino2 = camino;
        }

        System.out.println("Camino reconstruido: ");
        //imprimeCamino();
        System.out.println(camino.size());
        return;
    }

    void busquedaEnProfundidad(Nodo inicio) {

        ArrayList<Nodo> vecinos = new <Nodo>ArrayList();
        ArrayList<Nodo> vecinos2 = new <Nodo>ArrayList();
        
        visitados.add(inicio);

        camino.add(inicio); //puede haber movimientos diagonales

        if (esNodoObjetivo(inicio)) {
            return;
        }
        
        vecinos = obtenVecinos(inicio);
        vecinos2 = (ArrayList)vecinos.clone();

        for (Nodo m : vecinos) {
            if (estaContenido(visitados, m)) {
                elimina(vecinos2, m);
            }
        }

        for (Nodo m : vecinos2) {
            if (estaContenido(camino, nodoObjetivo)) {
                break;
            }
            busquedaEnProfundidad(m);
        }
    }

    boolean estaContenido(ArrayList<Nodo> a, Nodo n) {
        for (Nodo m : a) {
            if (n.igual(m)) {
                return true;
            }
        }
        return false;
    }

    void elimina(ArrayList<Nodo> a, Nodo n) {
        for (Nodo m : a) {
            if (n.igual(m)) {
                a.remove(m);
                break;
            }
        }
    }

    //función que nos permite averiguar si en un camino hay nodos diagonales
    boolean hayDiagonales(ArrayList<Nodo> a) {
        if (a.isEmpty()) {
            return false;
        }
        for (int i = 1; i < a.size(); i++) {

            int incx = a.get(i).getX() - a.get(i - 1).getX();
            int incy = a.get(i).getY() - a.get(i - 1).getY();
            if (Math.abs(incx + incy) != 1) {
                return true;
            }
        }

        return false;
    }

    //Función para eliminar nodos diagonales empezando desde el principio del camino
    ArrayList<Nodo> quitarDiagonales(ArrayList<Nodo> a) { 
        ArrayList<Nodo> aux = new ArrayList<Nodo>();
        if (a.isEmpty()) {
            return aux;
        }

        for (int i = 1; i<a.size(); i++) {
            int incx = a.get(i).getX() - a.get(i - 1).getX();
            int incy = a.get(i).getY() - a.get(i - 1).getY();
            if (Math.abs(incx + incy) == 1) //no hay movimiento diagonal
            {
                aux.add(a.get(i-1));


            }
        }
        aux.add(a.get(a.size()-1)); //meto el ultimo elemento

        return aux;
    }

}
