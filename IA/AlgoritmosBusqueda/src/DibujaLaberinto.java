

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.image.BufferedImage;


public class DibujaLaberinto extends JPanel implements ActionListener
{    

    /* Tama~no de la ventana */
    int tamVentana;
    /* Tama~no de la pared */
    int tamParedx;
    int tamParedy;
 
    /* Margen de la ventana */
    int offset=50;  
    /* Array que contiene la configuración del laberinto */
    int laberinto[][];

    Image hamster;
    Image queso;
    /* Posición exacta en la pantalla */
    int posHamster[]=new int[2];
    int posQueso[]=new int[2];
    Timer timer;
    int camino[][];
    int indiceCamino=0;
    boolean fin=false;
    int tamventanax;
    int tamventanay;
    int espacioSobrante;
    // Vector de dirección del movimiento
    int []direccionActual=new int[2];
    // Numero de pixels que faltan para alcanzar la siguiente casilla
    int contadorMovimiento;

    
    Color colorPared=new Color((float) 0.4,(float) 0.6, (float) 0.4);
    Color colorFondo=new Color((float) 0.0,(float) 0.0, (float) 0.1);
    Graphics2D labG;
    BufferedImage labImg;



    DibujaLaberinto(int l[][],int tx, int ty, int iniciox,int inicioy, int finalx, int finaly)
    {

	laberinto=l;
	tamventanax=tx;
	tamventanay=ty;

	tamParedx=(int) (double)(tamventanax-offset)/laberinto[0].length;
	tamParedy=(int) (double)(tamventanay-offset)/laberinto.length;
        posHamster[1]=iniciox*tamParedx+(offset/2);
	posHamster[0]=inicioy*tamParedy+(offset/2)+tamParedy/6;
	posQueso[0]=finaly*tamParedx+(offset/2)+tamParedy/6;
	posQueso[1]=finalx*tamParedy+(offset/2);

	Image img1 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("imgs/hamster.png"));
	hamster = img1.getScaledInstance((int)tamParedy, (int)tamParedy, Image.SCALE_SMOOTH);
	Image img2 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("imgs/queso.png"));
	queso = img2.getScaledInstance((int)(tamParedy*0.8), (int)(tamParedy*0.8), Image.SCALE_SMOOTH);
	creaImagenLaberinto();
	
	
	
	this.setVisible(true);
    }

    
    void creaImagenLaberinto()
    {
       labImg=new BufferedImage(tamventanax, tamventanay, BufferedImage.TYPE_INT_RGB);
       labG=labImg.createGraphics();
       labG.setColor(colorFondo);
       labG.fillRect(0, 0, getWidth(), getHeight());
       labG.setColor(colorPared);
       for(int i=0;i<laberinto.length;i++)
	   for(int j=0;j<laberinto[0].length;j++)
	       if(laberinto[i][j]>0)
		   labG.fill3DRect((int)(j*tamParedx+(offset/2.0)), (int)(i*tamParedy+(offset/2.0)), tamParedx, tamParedy,true);
      // setForeground(Color.white);
      
    }

    public void pintaCamino(int c[][])
    {
	camino=c;
	indiceCamino=0;
        // posHamster[0]=tamParedx+(offset/2);
	// posHamster[1]=offset/2;
	contadorMovimiento=0;
	timer = new Timer(1, this);
	timer.start();
	fin=false;
    }
    
    public void paint(Graphics g) 
    {
	super.paint(g);
	Graphics2D g2 = (Graphics2D) g;
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2.drawImage(labImg,0,0,this);

	
	if(fin==false) g2.drawImage(queso, posQueso[0],posQueso[1], this);


	g2.drawImage(hamster, posHamster[0], posHamster[1], this);


	
	Toolkit.getDefaultToolkit().sync();
        g2.dispose();
	

    }
    

    public void actionPerformed(ActionEvent e) 
    {
	if(contadorMovimiento==0)
	{
		indiceCamino++;
		if(indiceCamino>=camino.length)
		{
		    timer.stop();
		    fin=true;
		}
		else
		{
		    direccionActual[0]=camino[indiceCamino][0]-camino[indiceCamino-1][0];
		    direccionActual[1]=camino[indiceCamino][1]-camino[indiceCamino-1][1];
		    if(direccionActual[0]==0) contadorMovimiento=tamParedy;
		    else contadorMovimiento=tamParedx;
		}
	}
    
	    else
	    {
		posHamster[0]+=direccionActual[0];
		posHamster[1]+=direccionActual[1];
		contadorMovimiento--;
	    }

	repaint();
	
    }

}