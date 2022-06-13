
import java.util.ArrayList;
import java.util.LinkedList;

public class BusquedaAnchura extends BusquedaAleatoria {

    ArrayList<Nodo> visitados = new ArrayList();

    @Override
    void obtenCamino() {
       busquedaEnAnchura(devuelveNodoInicio());
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

    void busquedaEnAnchura(Nodo inicio) {

        ArrayList<Nodo> vecinos = new <Nodo>ArrayList();
        ArrayList<Nodo> visitados = new <Nodo>ArrayList();
        LinkedList<Nodo> cola = new <Nodo>LinkedList();

        cola.add(inicio);
        visitados.add(inicio);

        while (!cola.isEmpty()) {
            Nodo padre = cola.remove();
           
            if (!estaContenido(camino, padre)) {
                camino.add(padre);
            }

            if (esNodoObjetivo(padre)) {
                break;
            }
            vecinos = obtenVecinos(padre);
            for (Nodo m : vecinos) {
                if (!estaContenido(visitados, m)) {
                    cola.add(m);
                    visitados.add(m);
                }
                

            }

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

}
