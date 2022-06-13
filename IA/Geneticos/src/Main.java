
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.Date;



public class Main extends JApplet {

    /* Objetos gráficos */
    DibujaTablero db;
    GeneraTablero tablero;
    int tamOportunidades=11;
    int tamSecuencia=4;
    int tamLimite=21;
    int incrementoTam=2;
    boolean semillaFija=false;
    int numeroColores=8;

    long tiempoEjecucion;

    int retardo=0;

    


    /* Parámetros:
        -tamSecuencia []: Lanza un tablero con el tam de secuencia indicado por argumento (por defecto 4)
	    -tamOportunidades []: Lanza un tablero con el tam de oportunidades indicado por argumento (por defecto 11)
	    -numeroColores []: Lanza un tablero con un número de colores de la solución menor o igual al argumento
		-practica [] []: Lanza un conjunto de tableros de tamaño cada vez mayor hasta un límite (opcional tras este parametro)
		-ag []: Indica que para resolver el tablero se va a usar la clase indicada por argumento
		-semilla-fija: Genera tableros iguales en todas las ejecuciones con los mismos argumentos.
		-retardo []: establece un retardo a cada iteración.
		-invisible: hace que los tableros no se muestren por pantalla (evita excepciones lanzadas por el compilador)
	*/

    String []parametros={"-tamSecuencia","-tamOportunidades","-numeroColores","-practica","-ag","-semilla-fija","-retardo","-invisible"};

    enum Acciones{TABLERO_SIMPLE, SUCESION_TABLEROS, MUESTRA_TABLERO}
    Acciones accion=Acciones.TABLERO_SIMPLE;
    JFrame ventana = null;
    String claseAlgoritmo="NULL";
    
    boolean visible=true;

    /* Objeto de la clase algoritmo */

    Algoritmo alg=null;


    // constructor
    
    Main(String s[]) {
		leeParametros(s);
		if(ventana!=null)
		    ventana.addWindowListener(
				new WindowAdapter() 
				{
				    public void windowClosing(WindowEvent e) 
				    {
					System.exit(0);
				    }
				}
			);
    }

    /* Función auxiliar tomada del comecocos de Standford para obtener
     * en tiempo de ejecución el nombre e una clase */

    public static Object getNewObjectByName(String className, Class[] classes, Object[] args) {
    	Object o = null;
		try {
		    Class classDefinition = Class.forName(className);
		    Constructor constructor = classDefinition.getConstructor(classes);
		    o = constructor.newInstance(args);
		} 
		catch (Exception e) {
		    System.err.println("Clase " + className + " no encontrada");
		    System.exit(-1);
		}    
		return o;
	}


    



    /* Métodos para gestionar los parámetros de entrada y ejecutar la acción correspondiente */


    void muestraUso() {
		System.out.println("java Main [parametros]\n\nParametros\n----------\n");
		System.out.println("   -tamSecuencia [valor] -> Ejecuta un tablero cuyo tamaño de secuencia viene definido por [valor]");
		System.out.println("   -tamOportunidades [valor] -> Ejecuta un tablero cuyo tamaño de oportunidades viene definido por [valor]");
		System.out.println("   -numeroColores [valor] -> Ejecuta un tablero cuyo número de colores distintos viene definido por [valor]");
		System.out.println("   -practica [tamaño_máximo] [incremento] -> Ejecuta una sucesión de tableros de tamaño creciente. Puede especificarse opcionalmente el tamaño máximo y el incremento de tamaño entre tableros.");
		System.out.println("   -ag [nombre_fichero] -> Utiliza el fichero .class especificado como algoritmo de búsqueda (debe contener una subclase de la clase Algoritmo) ");
		System.out.println("   -semilla-fija -> Utiliza una semilla fija para que los tableros no cambien de una ejecución a otra."); 
		System.out.println("   -retardo [valor] -> Establece un retardo adicional a cada una de las iteraciones");
		System.out.println("   -invisible -> Hace que los tableros no se muestren (así evitamos excepciones lanzadas por el compilador.).");
		System.out.println("\n Por defecto, si no se especifican parámetros ejecuta un laberinto único de tamaño 10 con los gráficos activados. \n");
    }


    // Pasa los parámetros a enteros (para el switch)
    int parametroAEntero(String s) {
    	for(int i=0;i<parametros.length;i++)
	    if(s.equals(parametros[i])) return i;
    	return (-1);
    }



    // Lee los parámetros de ejecución del programa y realiza las acciones correspondientes a ellos.
    void leeParametros(String s[]) {

		for(int i=0;i<s.length;i++) {	 
			
		    int a=parametroAEntero(s[i]);
		    switch(a) {
		    
		    	// tamSecuencia
			    case 0: 
			    	if(i<s.length-1) {
					    try {
					    	tamSecuencia=new Integer(s[i+1]).intValue();
					    	//if(tamSecuencia<1||tamSecuencia>13) tamSecuencia=4;
							i++;
							break;
					    }
					    catch(NumberFormatException e) {
							System.err.println("Después del parámetro -tamSecuencia hay que especificar un entero");
							System.exit(-1);
					    }
					}
			        else {
					    System.err.println("Después del parámetro -tamSecuencia hay que especificar un entero");
					    System.exit(-1);
			        }
				    
					/* No se permiten tamaños muy pequeños o muy grandes */
				    if(tamSecuencia<1||tamSecuencia>20) {
				    	System.out.println("tamSecuencia máximo truncado en 20. Para cambiarlo, ir a leeParametros en la clase Main y buscar este println. Para esta ejecución, cambiado a 4.");
				    	tamSecuencia=4;
				    }
				    break;
	
				// tamOportunidades
	  	        case 1: 
	  	        	if(i<s.length-1) {
					    try {
					    	tamOportunidades=new Integer(s[i+1]).intValue()+1;
				    		i++;
					    }
		
					    catch(NumberFormatException e) {
							System.err.println("Después del parámetro -tamOportunidades hay que especificar un entero");
							System.exit(-1);
					    }
					}
			        else {
					    System.err.println("Después del parámetro -tamOportunidades hay que especificar un entero");
					    System.exit(-1);
			        }
				    
				/* No se permiten tamaños muy pequeños o muy grandes */
			        if(tamOportunidades<2||tamOportunidades>100) {
			        	System.out.println("tamOportunidades máximo truncado en 100. Para cambiarlo, ir a leeParametros en la clase Main y buscar este println. Para esta ejecución, cambiado a 15.");
			        	tamOportunidades=15;
			        }
			        break;
			   
			// numeroColores
	  	        case 2: 
	  	        	if(i<s.length-1) {
					    try {
					    	numeroColores=new Integer(s[i+1]).intValue();
				    		i++;
					    }
		
					    catch(NumberFormatException e) {
							System.err.println("Después del parámetro -numeroColores hay que especificar un entero");
							System.exit(-1);
					    }
					}
			        else {
					    System.err.println("Después del parámetro -numeroColores hay que especificar un entero");
					    System.exit(-1);
			        }
				    
				/* No se permiten tamaños muy pequeños o muy grandes */
			        if(numeroColores<2||numeroColores>8) {
			        	System.out.println("El número de colores del problema debe estar entre 2 y 8 (incluídos). Para esta ejecución, cambiado a 8.");
			        	numeroColores=8;
			        }
			        break;
			   
			   // practica
	   	       case 3: 
	 		        /* Se intenta leer el tamaño maximo, si lo hay */
			        if(i<s.length-1) {
					    try {
							tamLimite=new Integer(s[i+1]).intValue()+1;
							i++;
					    }
					    
					    catch(NumberFormatException e) {
					    	tamLimite=21;
					    }
			        }
				
				/* Se intenta leer el incremento de tamaño, si lo hay */
					if(i<s.length-1) {
						try {
						    incrementoTam=new Integer(s[i+1]).intValue();
						    i++;
						}
		
						catch(NumberFormatException e) {
						    incrementoTam=2;
						}
					}
		  		    accion=Acciones.SUCESION_TABLEROS;
		  		    
		  	   break;
	  	       
	
       
		       // ag
		       case 4: 
		    	   if(i<s.length-1) {
		    		   claseAlgoritmo=s[i+1];
		    		   i++;
		    	   }
			       else {
					   muestraUso();
					   System.exit(-1);
			       }
			   break;
			       
		       // semilla fija
		       case 5:	
		    	   semillaFija=true;
		       break;
		       
		       // retardo
		       case 6:
		    	   if(i<s.length-1) {
					    try {
					    	retardo=new Integer(s[i+1]).intValue();
							i++;
					    }
					    catch(NumberFormatException e) {
							System.err.println("Después del parámetro -retardo hay que especificar un entero");
							System.exit(-1);
					    }
					}
			        else {
					    System.err.println("Después del parámetro -retardo hay que especificar un entero");
					    System.exit(-1);
			        }
				    
					/* No se permiten tamaños muy pequeños o muy grandes */
				    if(retardo<0||retardo>50000) retardo=2000;
				break;
				
		       case 7:
		    	   visible=false;
		    	   break;
			        
	 	       default: 
	 	    	   muestraUso();
	 	    	   System.exit(-1);
		    }
		}
		if(claseAlgoritmo=="NULL") accion=Acciones.MUESTRA_TABLERO;
	
		tablero=new GeneraTablero(semillaFija,tamOportunidades,tamSecuencia,numeroColores,visible);
		
		
		switch(accion) {
		
	  	    case TABLERO_SIMPLE:
	  	    	tableroSimple();
	  	    	break;
	  	    	
		    case SUCESION_TABLEROS:
		    	sucesionTableros();  	
		    	break;
		    	
		    case MUESTRA_TABLERO: 
		    	tablero.getDb().dibujaInicio();
		    	break;
		}
	}
			   

    
    
    /* Método que ejecuta un único tablero */
    boolean tableroSimple() {

		boolean resuelto=false;
		String sResuelto;
			
		tablero=new GeneraTablero(semillaFija,tamOportunidades,tamSecuencia,numeroColores,visible);
		
		
		tablero.getDb().dibujaInicio();
	
		/* Inicializar objeto de la clase del Framework */
		alg=(Algoritmo) getNewObjectByName(claseAlgoritmo, new Class[0], new Object[0]);
		
		/* Pasar al objeto el laberinto generado */
		
		alg.defineTablero(tablero,retardo);
	
	
		/* Cálculo del tiempo de ejecución */
		Date tiempoInicio = new Date();
		/* Llamada al método que calcula el camino. Cambiar por el del
		 * framework */
		alg.soluciona();
		Date tiempoFinal = new Date();
		resuelto=alg.compruebaSolucion();
		tiempoEjecucion = tiempoFinal.getTime() - tiempoInicio.getTime();
		if(resuelto) sResuelto="SI.";
		else sResuelto="NO.";
	
	    System.out.println("\nTablero tamaño " + tamOportunidades);
	    System.out.println("---------------------\n");
	    System.out.println("Resuelto = " + sResuelto);
	    System.out.println("Tiempo = " + String.valueOf(tiempoEjecucion));
	    System.out.println("Iteraciones utilizadas = " + String.valueOf(alg.getTablero().getIters()-1));
	    System.out.println("");
	    
		return resuelto;
    }

    
    /* Método que ejecuta una sucesión de tableros de tamaño cada vez mayor */  
    void sucesionTableros() {

    /* Estadísticas de la búsqueda en modo BATCH */

	    long tiempoAcumulado=0; 
	    long itersTotales=0;
	    int tablerosResueltos=0;
	    int numeroLaberintos=0;
	
	
		while(tamOportunidades<=tamLimite) {
		    if(tableroSimple()) tablerosResueltos++;
		    tamOportunidades+=incrementoTam;
		    tiempoAcumulado+=tiempoEjecucion;
		    itersTotales+=alg.getTablero().getIters()-1;
		    numeroLaberintos++;
		}
		
		System.out.println("\n\nRESULTADOS FINALES");
		System.out.println("------------------\n");
		System.out.println("TABLEROS INTENTADOS = " + numeroLaberintos);
		System.out.println("TABLEROS RESUELTOS = " + tablerosResueltos);
		System.out.println("TIEMPO TOTAL BÚSQUEDA = " + tiempoAcumulado + " ms." );
		System.out.println("ITERACIONES TOTALES = " + itersTotales);
		System.out.println("");


    }

		
    // Main del proyecto
    public static void main(String s[])
    {
	    new Main(s);
    }

}