import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import util.PriorityQueue;

public class BusquedaAestrella extends BusquedaLaberinto {

    @Override
    void obtenCamino() {
        busquedaestrella(devuelveNodoInicio());
        //busquedaPrimeroElMejor(devuelveNodoInicio());

       
           //instrucciones para poder eliminar nodos diagonales en el camino
        ArrayList<Nodo> camino2 = (ArrayList) camino.clone();
        while (hayDiagonales(camino2)) {
            camino = quitarDiagonales(camino2);
            camino2 = camino;
        }
        
        System.out.println("Camino reconstruido: ");
        //imprimeCamino();
        System.out.println(camino.size());
        


    }
  void datosnodo(Nodo inicio) { //datos del nodo
        System.out.println("datos del nodo: ");
        System.out.println("(" + inicio.x + " , " + inicio.y + ")");
        System.out.println("Profundidad "+inicio.profundidad);
        System.out.println("heuristica " + inicio.heuristica);
        System.out.println("Padre " + inicio.padre);
        System.out.println("--------------------------------------");
    }

    void busquedaestrella(Nodo inicio) {

        //creación de la cola con prioridad haciendo uso de la clase PriorityQueue
        PriorityQueue<Nodo> cola = new PriorityQueue<Nodo>();

        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();

        inicio.heuristica=(int)heuristica(inicio)+inicio.profundidad;
        inicio.padre=inicio;

        //Trabajamos con la cola añadiendo el primer nodo y su prioridad
        cola.add(inicio, (int)heuristica(inicio));

        //añadimos al camino el primer nodo
        camino.add(inicio);

        while (!cola.isEmpty()) {
            Nodo padre = cola.removeFirst();

            if (esNodoObjetivo(padre)) {
                camino.add(padre);
                return;
            }
            vecinos = obtenVecinos(padre);

            for (Nodo n : vecinos) {
                n.padre=padre;
                n.profundidad=padre.profundidad+1;
                n.heuristica=(int)heuristica(n)+n.profundidad;
              // datosnodo(n);

                if (!estaContenido(camino, n)) {
                    camino.add(n);
                    //Calculamos el coste acumulado de todo el camino hasta el nodo n: f(n)=-1*(g(n-1)+h(n))
                    cola.add(n, -1* n.heuristica);
                    //la cola irá ordenada de mayor a menor (-1,-2,-14,...); en la cabeza tendremos el nodo con el mejor coste
                }
            }
        }
        return;
    }

    int heuristica(Nodo a) { //distancia Manhattan
        Nodo b = nodoObjetivo;
        int h = Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
        return h;
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

    boolean estaContenido(ArrayList<Nodo> a, Nodo n) {
        for (Nodo m : a) {
            if (n.igual(m)) {
                return true;
            }
        }
        return false;
    }


   double heuristica2(Nodo a) { //distancia euclidea, h2(n)
        Nodo b = nodoObjetivo;
        double h = Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
        return h;
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

        for (int i = a.size()-1; i > 0; i--) {
            int incx = a.get(i).getX() - a.get(i - 1).getX();
            int incy = a.get(i).getY() - a.get(i - 1).getY();
            if (Math.abs(incx + incy) != 1) //hay movimiento diagonal
            {
                a.remove(a.get(i-1));
            }
        }
        return a;
    }



 //Clase utilizada para ordenar una Lista de nodos según su heuristica
 class ComparadorNodos implements Comparator<Nodo> {

        @Override
        public int compare(Nodo a, Nodo b) {
            return a.getHeuristica() - b.getHeuristica();
        }
    }



}
