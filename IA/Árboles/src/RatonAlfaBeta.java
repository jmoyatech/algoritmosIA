
import java.util.ArrayList;

/* API:
 * boolean esNodoObjetivo(Nodo n) 
 * boolean posicionValida(Nodo nodo)
 * boolean muro(int x, int y)
 * boolean posicionValidaAdversario(Nodo nodo)
 * boolean movimientoValido()
 * ArrayList<Nodo> obtenVecinos(Nodo nodo)
 * int distanciaAdversario(Nodo nodo)
 * ArrayList<Nodo> obtenVecinosAdversario(Nodo nodo)
 */

/* Adaptacion del algoritmo
   function alphabeta(node, depth, ?, ?, Player)
    if  depth = 0 or node is a terminal node
        return the heuristic value of node
    if  Player = MaxPlayer
        for each child of node
            ? := max(?, alphabeta(child, depth-1, ?, ?, not(Player) ))
            if ? ? ?
                break                             (* Beta cut-off *)
        return ?
    else
        for each child of node
            ? := min(?, alphabeta(child, depth-1, ?, ?, not(Player) ))
            if ? ? ?
                break                             (* Alpha cut-off *)
        return ?
(* Initial call *)
alphabeta(origin, depth, -infinity, +infinity, MaxPlayer)

http://en.wikipedia.org/wiki/Alpha-beta_pruning
 */

public class RatonAlfaBeta extends BusquedaLaberinto {

    ArrayList<Nodo> recorridos = new ArrayList<Nodo>();
    private int profundidad = 2;

    @Override
    void obtenMovimiento(int posAdversarioX, int posAdversarioY) {

        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();
        Nodo mejorNodo = posActual;
        recorridos.add(mejorNodo);
        int alfa = Integer.MIN_VALUE; //inicializamos alfa y beta a -infinito y +infinito respectivamente
        int beta = Integer.MAX_VALUE;

        if (posActual.padre != null) {
            posActual.profundidad = posActual.padre.profundidad + 1;
        }

        int mejorValor = Integer.MIN_VALUE;
        vecinos = obtenVecinos(posActual);

        for (Nodo m : vecinos) {
            m.padre = posActual;
            int valorMINIMAX = MAX(m, profundidad, alfa, beta); //llamamos a MINIMAX con alfa y beta

            for (Nodo m2 : recorridos) {
                if (m.igual(m2)) { //si hay cabeceo (ya se ha recorrido) se penaliza ese nodo (m) con -100
                    int penalizacion = -100;
                    valorMINIMAX += penalizacion;
                }
            }
            m.setValor(valorMINIMAX);
            //System.out.println("nodo: "+m+" "+valorMINIMAX);
            if (valorMINIMAX > mejorValor) {
                mejorValor = valorMINIMAX;
                mejorNodo = m;

            }
        }
        //System.out.println("mejor Nodo: " + mejorNodo);
        //System.out.println("padre de mejorNodo: "+mejorNodo.padre);
        //System.out.println(recorridos);
        if (fin) {
            recorridos.clear(); //si se pasa de laberinto se limpia la lista de recorridos
        }
        movimiento.add(mejorNodo);



    }

    int MAX(Nodo n, int profundidad, int alfa, int beta) {

        if (esNodoObjetivo(n)) { //si se gana
            return Integer.MAX_VALUE;
        }

        if (distanciaAdversario(n) == 0) { //si se pierde
            return Integer.MIN_VALUE;
        }

        if (profundidad == 0) {
            return utilidad(n);
        }

        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();
        vecinos = obtenVecinos(n);

        for (Nodo m : vecinos) {
            int valorMin = MIN(m, profundidad - 1, alfa, beta);
            if (valorMin > alfa) { //nos quedamos con el maximo de los valores de MIN y ponemos en alfa ese valor
                alfa = valorMin;
            }
            if (alfa >= beta) { //se corta esa rama
                break;
            }
        }

        return alfa;
    }

    int MIN(Nodo n, int profundidad, int alfa, int beta) {

        if (esNodoObjetivo(n)) { //si se gana
            return Integer.MAX_VALUE;
        }

        if (distanciaAdversario(n) == 0) { //si se pierde
            return Integer.MIN_VALUE;
        }

        if (profundidad == 0) {
            return utilidad(n);
        }

        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();
        vecinos = obtenVecinosAdversario(n);

        for (Nodo m : vecinos) {
            int valorMax = MAX(m, profundidad - 1, alfa, beta);
            if (valorMax < beta) { //nos quedamos con minimo de los valores de MAX y ponemos en beta ese valor
                beta = valorMax;
            }
            if (alfa >= beta) { //se corta esa rama
                break;
            }
        }

        return beta;
    }

    int utilidad(Nodo n) { //funcion de utilidad; factorizamos para que pese mas la distancia que hay entre raton y gato
        int m1 = Math.abs(n.getX() - n.getAX()) + Math.abs(n.getY() - n.getAY());
        int m2 = Math.abs(n.getX() - nodoObjetivo.getX()) + Math.abs(n.getY() - nodoObjetivo.getY());
        return 3 * m1 - m2;
    }
}