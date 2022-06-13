
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
public class RatonAlfaBeta2 extends BusquedaLaberinto {

    private final int profundidad = 2;
    ArrayList<Nodo> recorridos = new ArrayList<Nodo>();

    @Override
    void obtenMovimiento(int posAdversarioX, int posAdversarioY) {
        int alfa = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();
        Nodo mejorNodo = posActual;
        int mejorValor = Integer.MIN_VALUE;
        boolean max = true;
        vecinos = obtenVecinos(posActual);
        if (posActual.padre != null) {
            posActual.profundidad = posActual.padre.profundidad + 1;
        }

        for (Nodo m : vecinos) {
            m.padre = posActual;
            m.profundidad = posActual.profundidad + 1;
            if (esNodoObjetivo(m)) {
                movimiento.add(m);
                break;
            }

            int valorAB = minimaxAB(m, profundidad, alfa, beta, max);
            for (Nodo m2 : recorridos) {
                if (m.igual(m2)) { //si hay cabeceo (ya se ha recorrido) se penaliza ese nodo (m) con -100
                    int penalizacion = -100;
                    valorAB += penalizacion;
                }
            }
            m.setValor(valorAB);
            System.out.println("nodo: " + m + " " + valorAB);
            if (valorAB > mejorValor) {
                mejorValor = valorAB;
                mejorNodo = m;
            }
        }
        recorridos.add(mejorNodo);
        //System.out.println(recorridos);
        if (fin) {
            recorridos.clear();
        }
        movimiento.add(mejorNodo);
    }

    int minimaxAB(Nodo x, int profundidad, int alfa, int beta, boolean max) {

        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();

        if (profundidad == 0) {
            return utilidad(x);
        }

        if (distanciaAdversario(x) == 0) {
            return Integer.MIN_VALUE;
        }

        if (esNodoObjetivo(x)) {
            return Integer.MAX_VALUE;
        }

        if (max) {
            vecinos = obtenVecinos(x);

            for (Nodo n : vecinos) {
                int valorAB = minimaxAB(n, profundidad - 1, alfa, beta, !max);
                if (alfa < valorAB) {
                    alfa = valorAB;
                }
                if (alfa >= beta) {
                    break;
                }
            }
            return alfa;
        }

        if (!max) {
            vecinos = obtenVecinosAdversario(x);
            for (Nodo n : vecinos) {
                int valorAB = minimaxAB(n, profundidad - 1, alfa, beta, !max);
                if (beta > valorAB) {
                    beta = valorAB;
                }
                if (alfa >= beta) {
                    break;
                }
            }
            return beta;
        }

        return 0;
    }

    int utilidad(Nodo n) {
        int m1 = Math.abs(n.getX() - n.getAX()) + Math.abs(n.getY() - n.getAY());
        int m2 = Math.abs(n.getX() - nodoObjetivo.getX()) + Math.abs(n.getY() - nodoObjetivo.getY());
        return 3*m1-m2;
    }
}
