
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import util.PriorityQueue;

public class BusquedaAestrella2 extends BusquedaLaberinto {

    @Override
    void obtenCamino() {
        busquedaAestrella(devuelveNodoInicio());

        /* Muestra camino con uso de diagonales
        System.out.println("Camino con diagonales: ");
        imprimeCamino();
        System.out.println(camino.size());
        System.out.println("_____________________________");
         */

        //instrucciones para poder eliminar nodos diagonales en el camino
        ArrayList<Nodo> camino2 = (ArrayList) camino.clone();
        while (hayDiagonales(camino2)) {
            camino = quitarDiagonales(camino2);
            camino2 = camino;
        }
        datosNodos();
             /*  ArrayList<Nodo> camino3 = (ArrayList) camino.clone();
        while (hayDiagonales(camino3)) {
        camino = quitarDiagonales2(camino3);
        camino3 = camino;
        }*/

        System.out.println("Camino reconstruido: ");
        //imprimeCamino();
        System.out.println(camino.size());

        return;

    }

    void busquedaAestrella(Nodo inicio) {

        LinkedList<Nodo> cola = new LinkedList<Nodo>();
        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();

        //inicializo el primer nodo
        cola.add(inicio);
        camino.add(inicio);
        inicio.setPadre(null);
        inicio.setHeuristica(heuristica(inicio));
        inicio.setProfundidad(0);

        while (!cola.isEmpty()) {
            Nodo padre = cola.remove(); //obtengo el elemento en cabeza
            if (esNodoObjetivo(padre)) {
                camino.add(padre);
                break;
            }
            vecinos = obtenVecinos(padre);

            for (Nodo n : vecinos) {
                if (!estaContenido(camino, n)) {
                    n.setPadre(padre); //inicializo sus hijos
                    n.setProfundidad(padre.getProfundidad() + 1);
                    n.setHeuristica(heuristica(n) + padre.getProfundidad() + 1); //h(n)+g(n)
                    cola.add(n);
                    camino.add(n);
                }
            }
            Collections.sort(cola, new ComparadorNodos()); //ordena de menor a mayor
        }
        return;
    }

    void busquedaAestrella2(Nodo inicio) { //A* usando PriorityQueue
        //expande demasiados nodos
        //No usa la informacion de cada nodo (profundidad, heuristica, padre, etc)

        PriorityQueue<Nodo> cola = new PriorityQueue<Nodo>();

        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();

        HashMap<Nodo, Integer> costes = new HashMap<Nodo, Integer>();

        cola.add(inicio, heuristica(inicio));

        costes.put(inicio, 0);
        camino.add(inicio);

        while (!cola.isEmpty()) {
            Nodo padre = cola.removeFirst();

            if (esNodoObjetivo(padre)) {
                camino.add(padre);
                return;
            }
            vecinos = obtenVecinos(padre);

            for (Nodo n : vecinos) {
                if (!estaContenido(camino, n)) {
                    //coste g(n-1)+1
                    costes.put(n, costes.get(padre) + 1);
                    //f(n)=-1*(g(n-1)+1+h(n)) 
                    cola.add(n, -1 * (costes.get(n) + heuristica(n)));
                    //la cola irá ordenada de mayor a menor (-1,-2,-14,...); en la cabeza tendremos el nodo con el mejor valor
                    camino.add(n);
                }
            }
        }
        return;
    }

    void busquedaPrimeroElMejor(Nodo inicio) {

        LinkedList<Nodo> cola = new LinkedList<Nodo>();
        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();

        //inicializo el primer nodo
        cola.add(inicio);
        camino.add(inicio);
        inicio.setPadre(null);
        inicio.setHeuristica(heuristica(inicio));
        inicio.setProfundidad(0);

        while (!cola.isEmpty()) {
            Nodo actual = cola.remove(); //obtengo el elemento en cabeza
            if (esNodoObjetivo(actual)) {
                camino.add(actual);
                break;
            }
            vecinos = obtenVecinos(actual);

            for (Nodo n : vecinos) {
                if (!estaContenido(camino, n)) {
                    n.setPadre(actual); //inicializo sus hijos
                    n.setProfundidad(actual.profundidad+1);
                    n.setHeuristica(heuristica(n));
                    cola.add(n);
                    camino.add(n);
                }
            }
            Collections.sort(cola, new ComparadorNodos()); //ordena de menor a mayor
        }
        return;
    }

    int heuristica(Nodo a) { //distancia Manhattan, h(n)
        Nodo b = nodoObjetivo;
        int h = Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
        return h;
    }

    double heuristica2(Nodo a) { //distancia euclidea, h2(n)
        Nodo b = nodoObjetivo;
        double h = Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
        return h;
    }

    class ComparadorNodos implements Comparator<Nodo> {

        @Override
        public int compare(Nodo a, Nodo b) {
            return a.getHeuristica() - b.getHeuristica();
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
    //Función para eliminar nodos diagonales empezando desde el final del camino

    ArrayList<Nodo> quitarDiagonales(ArrayList<Nodo> a) {
        if (a.isEmpty()) {
            return a;
        }

        for (int i = a.size() - 1; i > 0; i--) {
            int incx = a.get(i).getX() - a.get(i - 1).getX();
            int incy = a.get(i).getY() - a.get(i - 1).getY();
            if (Math.abs(incx + incy) != 1) //hay movimiento diagonal
            {
                a.remove(a.get(i - 1));
            }
        }
        return a;
    }
    //funcion para quitar diagonales desde el principio del camino

    ArrayList<Nodo> quitarDiagonales2(ArrayList<Nodo> a) {
        ArrayList<Nodo> aux = new ArrayList<Nodo>();
        if (a.isEmpty()) {
            return aux;
        }

        for (int i = 1; i < a.size(); i++) {
            int incx = a.get(i).getX() - a.get(i - 1).getX();
            int incy = a.get(i).getY() - a.get(i - 1).getY();
            if (Math.abs(incx + incy) == 1) //no hay movimiento diagonal
            {
                aux.add(a.get(i - 1));


            }
        }
        aux.add(a.get(a.size() - 1)); //meto el ultimo elemento

        return aux;
    }

    void datosNodos() {
        for (Nodo a : camino) {
            System.out.println("--------------");
            System.out.println("Nodo (" + a.x + "," + a.y + ")");
            System.out.println("Profundidad: " + a.profundidad);
            System.out.println("Heuristica: " + a.heuristica);
            if(a.padre!=null) System.out.println("Padre (" + a.padre.x + "," + a.padre.y + ")");
        }
    }
}
