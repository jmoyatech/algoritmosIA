import java.io.*;
import java.lang.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

 

class DibujaTablero extends Frame {

	// variables para el dibujo del tablero
	static int tamx;
	static int tamy;
	int principiox=70;
	int principioy=38;
	int finx;
	int finy;
	int incrementox=40;
	int incrementoy=33;
	int principiox2;
	int finx2;
	int incrementox2=25;
	
	// variables útiles de la clase
	int tamOportunidades;
	int tamSecuencia;
	int numeroColores;
	boolean visible;
	
	// aquí se almacenarán las permutaciones solución enviadas por cada iteración del algoritmo
	public int soluciones[][];
	
	// aquí se almacenarán los pines de las soluciones de cada algoritmo
	public int pines[][];
	
    BufferedImage labImg;
	Graphics2D g;
	
	// imágenes a cargar
	Toolkit t;
	Image vacia2;
	Image vacia3;
	Image negra;
	Image blanca;
	Image cero;
	Image uno;
	Image dos;
	Image tres;
	Image cuatro;
	Image cinco;
	Image seis;
	Image siete;
	
     
	//Constructor de la clase
	DibujaTablero(int tamOportunidades, int tamSecuencia, int numeroColores, boolean visible) {      
		
		// asignación de valores de las variables útiles
		this.tamOportunidades=tamOportunidades;
		this.tamSecuencia=tamSecuencia;
		this.numeroColores=numeroColores;
		this.visible=visible;
		
		
		// defino los límites del tablero a partir de tamOportunidades y tamSecuencia
		tamx=70+(tamSecuencia*incrementox)+15+(tamSecuencia*incrementox2)+70;
		principiox2=70+(tamSecuencia*incrementox)+20;
		finx=70+(tamSecuencia*incrementox)-1;
		finx2=70+(tamSecuencia*incrementox)+15+(tamSecuencia*incrementox2)-1;
		tamy=(tamOportunidades*33)+40;
		finy=principioy+((tamOportunidades-1)*incrementoy);
		
		
		// cargo las imágenes (para poder usarlas cuando quiera)
		cargaImagenes();
		
		// inicializo las soluciones de las distintas iteraciones y los pines correspondientes
		
		soluciones=new int[tamOportunidades][tamSecuencia];
		pines=new int[tamOportunidades][tamSecuencia];
		for (int i=0;i<tamOportunidades;i++) {
			for (int j=0;j<tamSecuencia;j++) {
				soluciones[i][j]=-1;
				pines[i][j]=-1;
			}
		}
		
        setBackground(Color.white);
        setSize(tamx,tamy);
        setLocationRelativeTo(null);    //Centrar
        setResizable(false);
        

        // ### ESCUCHADOR DE VENTANA ###

        //Para que al dar clic en la x de la esquina de este Frame se cierre

        addWindowListener(new WindowAdapter() {      
	            public void windowClosing(WindowEvent e) {
	                System.exit(0);
	            }
	            public void windowClosed(WindowEvent e) {
	                System.exit(0);        
	            }
	     
        	}
        );

    }

	
	// Carga las imágenes del tablero (para poder usarlas cuando se necesiten)
	public void cargaImagenes() {
		
		t = Toolkit.getDefaultToolkit ();
		
		vacia3 = t.getImage (getClass().getClassLoader().getResource("imgs/vacia3.png"));
		vacia2 = t.getImage (getClass().getClassLoader().getResource("imgs/vacia2.png"));
		negra = t.getImage (getClass().getClassLoader().getResource("imgs/negra.png"));
		blanca = t.getImage (getClass().getClassLoader().getResource("imgs/blanca.png"));
		cero = t.getImage (getClass().getClassLoader().getResource("imgs/0.png"));
		uno = t.getImage (getClass().getClassLoader().getResource("imgs/1.png"));
		dos = t.getImage (getClass().getClassLoader().getResource("imgs/2.png"));
		tres = t.getImage (getClass().getClassLoader().getResource("imgs/3.png"));
		cuatro = t.getImage (getClass().getClassLoader().getResource("imgs/4.png"));
		cinco = t.getImage (getClass().getClassLoader().getResource("imgs/5.png"));
		seis = t.getImage (getClass().getClassLoader().getResource("imgs/6.png"));
		siete = t.getImage (getClass().getClassLoader().getResource("imgs/7.png"));
		
		
	}
	
	
	
	// Dibuja el tablero inicial (sólo con la solución al mismo y el resto de oportunidades y pines vacíos)
	public void dibujaInicio() {
		
		this.setVisible(visible);
	    
		labImg=new BufferedImage(tamx, tamy, BufferedImage.TYPE_INT_RGB);
		g=labImg.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		//DIBUJAMOS LOS BORDES DEL TABLERO
		
        // polígono lateral izquierdo
		Polygon poligono=new Polygon();
		poligono.addPoint(20, 0);
		poligono.addPoint(50, 0);
		poligono.addPoint(50, tamy);
		poligono.addPoint(20, tamy);
		g.setColor(Color.black);
		g.fillPolygon(poligono);
		
		//polígono lateral derecho
		Polygon poligono2=new Polygon();
		poligono2.addPoint(tamx-20, 0);
		poligono2.addPoint(tamx-50, 0);
		poligono2.addPoint(tamx-50, tamy);
		poligono2.addPoint(tamx-20, tamy);
		g.setColor(Color.black);
		g.fillPolygon(poligono2);
		
		// polígono superior
		Polygon poligono3=new Polygon();
		poligono3.addPoint(20,25);
		poligono3.addPoint(20, 30);
		poligono3.addPoint(tamx-20, 30);
		poligono3.addPoint(tamx-20, 25);
		g.setColor(Color.black);
		g.fillPolygon(poligono3);
		
		// polígono inferior
		Polygon poligono4=new Polygon();
		poligono4.addPoint(20, tamy);
		poligono4.addPoint(20, tamy-10);
		poligono4.addPoint(tamx-20, tamy-10);
		poligono4.addPoint(tamx-20, tamy);
		g.setColor(Color.black);
		g.fillPolygon(poligono4);
		
		
		// línea gorda justo debajo de la solución
		Polygon poligono5=new Polygon();
		poligono5.addPoint(50, 30+incrementoy);
		poligono5.addPoint(tamx-50, 30+incrementoy);
		poligono5.addPoint(tamx-50, 33+incrementoy);
		poligono5.addPoint(50, 33+incrementoy);
		g.setColor(Color.black);
		g.fillPolygon(poligono5);


		// líneas del tablero
		int contador=0;
		contador=30+incrementoy;
		while (contador<tamy) {
			g.drawLine(50, contador, tamx-50, contador);
			contador+=incrementoy;
		}
		g.drawLine(70+(tamSecuencia*incrementox), 30, 70+(tamSecuencia*incrementox), tamy-10);
		g.setBackground(Color.WHITE);
		super.paint(g);
          
		
	}
	
	
	// Método que actualiza el estado del tablero
	public void paint(Graphics g2) {

    	
    	super.paint(g2);
    	Graphics2D g = (Graphics2D) g2;
    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g.drawImage(labImg,0,0,this);
	    	
	    	
    	int i,j,k=0,l;
    	
    	
    	// pintamos las soluciones de las distintas iteraciones en el tablero
        for (i=principiox;i<=finx;i+=incrementox) {
        	l=0;
        	for (j=principioy;j<=finy;j+=incrementoy) {
        		if (soluciones[l][k]==-1) {
        			g.drawImage (vacia3, i, j, this);
        		}
        		else if (soluciones[l][k]==0) {
        			g.drawImage (cero, i, j, this);
        		}
        		else if (soluciones[l][k]==1) {
        			g.drawImage (uno, i, j, this);
        		}
        		else if (soluciones[l][k]==2) {
        			g.drawImage (dos, i, j, this);
        		}
        		else if (soluciones[l][k]==3) {
        			g.drawImage (tres, i, j, this);
        		}
        		else if (soluciones[l][k]==4) {
        			g.drawImage (cuatro, i, j, this);
        		}
        		else if (soluciones[l][k]==5) {
        			g.drawImage (cinco, i, j, this);
        		}
        		else if (soluciones[l][k]==6) {
        			g.drawImage (seis, i, j, this);
        		}
        		else if (soluciones[l][k]==7) {
        			g.drawImage (siete, i, j, this);
        		}
        		l++;
        	}
        	k++;
        }
        

        
        // pintamos los pines vacios
        for (i=principiox2;i<=finx2;i+=incrementox2) {
        	for (j=principioy;j<=finy;j+=incrementoy) {
        		g.drawImage (vacia2, i, j, this);
        	}
        }
        

        // rellenamos los pines ya actualizados (1 -> negra, 0 -> blanca)
        k=0;
        for (i=principiox2;i<=finx2;i+=incrementox2) {
        	l=0;
        	for (j=principioy;j<=finy;j+=incrementoy) {
        		if (pines[l][k]==1) {
        			g.drawImage (negra, i+5, j+4, this);
        		}
        		else if (pines[l][k]==0) {
        			g.drawImage (blanca, i+5, j+5, this);
        		}
        		l++;
        	}
        	k++;
        }
        

    }
	
 
	// Actualiza las soluciones
	public void setSoluciones(int soluciones[][]) {
		for (int i=0;i<tamOportunidades;i++) {
			for (int j=0;j<tamSecuencia;j++) {
				this.soluciones[i][j]=soluciones[i][j];
			}
		}
	}
	
	// Devuelve las soluciones
	public int[][] getSoluciones() {
		return soluciones;
	}
	
	// Actualiza los pines
	public void setpines(int pines[][]) {
		for (int i=0;i<tamOportunidades;i++) {
			for (int j=0;j<tamSecuencia;j++) {
				this.pines[i][j]=pines[i][j];
			}
		}
	}
	
	// Devuelve los pines
	public int[][] getPines() {
		return pines;
	}
	
	// Devuelve el tamOportunidades
	public int getTamOportunidades() {
		return tamOportunidades;
	}
	
	// Devuelve el tamSecuencia
	public int getTamSecuencia() {
		return tamSecuencia;
	}
	

	public int getNumeroColores() {
    	return numeroColores;
    }
    

    

    


     


      

             

}

