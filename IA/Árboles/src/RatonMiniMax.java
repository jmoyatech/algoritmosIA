
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


public class RatonMiniMax extends BusquedaLaberinto {

    ArrayList<Nodo> recorridos = new ArrayList<Nodo>(); //En esta lista se añaden los nodos ya recorridos
    private int profundidad = 2; //con profundidad 2 resuelve bien los dos primeros laberintos

    @Override
    void obtenMovimiento(int posAdversarioX, int posAdversarioY) { //estos dos parametros no se usan

        ArrayList<Nodo> vecinos = new ArrayList<Nodo>();
        Nodo mejorNodo = posActual; //en un principio inicializamos mejorNodo a la primera posicion
        recorridos.add(posActual); //vamos añadiendo en recorridos los nodos que ya hemos recorrido o visitado

        if (posActual.padre != null) {
            posActual.profundidad = posActual.padre.profundidad + 1; //calculamos la profundidad de cada nodo
        }

        int mejorValor = Integer.MIN_VALUE;
        vecinos = obtenVecinos(posActual);

        for (Nodo m : vecinos) {
            m.padre = posActual; //indicamos que el nodo padre de cada hijo es posActual
            int valorMINIMAX = MAX(m, profundidad); //para cada hijo de posActual ejecutamos el algorimo MiniMax

            for (Nodo m2 : recorridos) {
                if (m.igual(m2)) { //si hay cabeceo (ya se ha recorrido) se penaliza ese nodo (m) con -100
                    int penalizacion = -100;
                    valorMINIMAX += penalizacion;
                }
            }
            m.setValor(valorMINIMAX); //para cada hijo de posActual le ponemos su valor MiniMax
            //System.out.println("nodo: "+m+" "+valorMINIMAX); //sirve para ver los hijos de posActual y su valor MiniMax
            if (valorMINIMAX > mejorValor) { //nos quedamos con el mejor sucesor despues de aplicar MiniMax a cada uno
                mejorValor = valorMINIMAX;
                mejorNodo = m;

            }
        }
        //System.out.println("mejor Nodo: " + mejorNodo); //sirve para ver el mejor nodo devuelto por MiniMax
        //System.out.println("padre de mejorNodo: "+mejorNodo.padre); //y quien es su padre
        //System.out.println(recorridos); //sirve para ver la lista de recorridos
        if (fin) {
            recorridos.clear(); //si se cambia de laberinto limpiamos la lista de recorridos
        }
        movimiento.add(mejorNodo); //devolvemos el mejorNodo



    }

    int MAX(Nodo n, int profundidad) {
        int valor = Integer.MIN_VALUE;

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
        vecinos = obtenVecinos(n); //obtengo los sucesores de MAX y llamo a MIN con ellos

        for (Nodo m : vecinos) {
            int valorMin = MIN(m, profundidad - 1);
            if (valorMin > valor) {
                valor = valorMin;
            }
        }

        return valor;
    }

    int MIN(Nodo n, int profundidad) {

        int valor = Integer.MAX_VALUE;

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
        vecinos = obtenVecinosAdversario(n); //obtengo los sucesores de MIN y llamo a MAX con ellos

        for (Nodo m : vecinos) {
            int valorMax = MAX(m, profundidad - 1);
            if (valorMax < valor) {
                valor = valorMax;
            }
        }

        return valor;
    }

    int utilidad(Nodo n) { //funcion de utilidad; factorizamos para que pese mas la distancia que hay entre raton y gato
        int m1 = Math.abs(n.getX() - n.getAX()) + Math.abs(n.getY() - n.getAY());
        int m2 = Math.abs(n.getX() - nodoObjetivo.getX()) + Math.abs(n.getY() - nodoObjetivo.getY());
        return 3*m1 - m2;
    }
}

