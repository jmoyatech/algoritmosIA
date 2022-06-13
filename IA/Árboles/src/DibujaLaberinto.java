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
    Image gato;
    /* Posición exacta en la pantalla */
    int posHamster[]=new int[2];
    int posQueso[]=new int[2];
    int posGato[]=new int[2];


    /* Agente a mover. 0 -> raton, 1 -> gato */

    int agenteMover;

    Timer timer;
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



    DibujaLaberinto(int l[][],int tx, int ty, int iniciox,int inicioy, int finalx, int finaly, int gatox, int gatoy)
    {

	laberinto=l;
	tamventanax=tx;
	tamventanay=ty;

	tamParedx=(int) (double)(tamventanax-offset)/laberinto[0].length;
	tamParedy=(int) (double)(tamventanay-offset)/laberinto.length;
        // posHamster[1]=iniciox*tamParedx+(offset/2);
	// posHamster[0]=inicioy*tamParedy+(offset/2)+tamParedy/6;

	posHamster[1]=iniciox*tamParedy+(offset/2)+(tamParedy/7);
	posHamster[0]=inicioy*tamParedx+(offset/2)+(tamParedx/7);



	posQueso[0]=finaly*tamParedx+(offset/2)+tamParedx/6;
	posQueso[1]=finalx*tamParedy+(offset/2);

        // posHamster[1]=gatox*tamParedx+(offset/2);
	// posHamster[0]=gatoy*tamParedy+(offset/2)+tamParedy/6;
	//gatox=8;
	posGato[1]=gatox*tamParedy+(offset/2)+(tamParedy/7);
	posGato[0]=gatoy*tamParedx+(offset/2)+(tamParedx/7);
	System.out.println("posRaton = " + iniciox + "," + inicioy);
	System.out.println("posGato = " + gatox + "," + gatoy);

	int mintam=tamParedx;
	if(tamParedy<mintam) mintam=tamParedy;

	Image img1 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("imgs/hamster.png"));
	
	hamster = img1.getScaledInstance((int)(mintam*0.8), (int)(mintam*0.8), Image.SCALE_SMOOTH);
	Image img2 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("imgs/queso.png"));
	queso = img2.getScaledInstance((int)(mintam*0.8), (int)(mintam*0.8), Image.SCALE_SMOOTH);

	Image img3 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("imgs/gato.png"));
	gato = img3.getScaledInstance((int)(mintam*0.8), (int)(mintam*0.8), Image.SCALE_SMOOTH);
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

    
    public void paint(Graphics g) 
    {
	super.paint(g);
	Graphics2D g2 = (Graphics2D) g;
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2.drawImage(labImg,0,0,this);

	
	g2.drawImage(queso, posQueso[0],posQueso[1], this);
	if(posGato[0]!=posHamster[0]||posGato[1]!=posHamster[1])
	{
	    g2.drawImage(hamster, posHamster[0], posHamster[1], this);
	}
	g2.drawImage(gato, posGato[0], posGato[1], this);


	
	Toolkit.getDefaultToolkit().sync();
        g2.dispose();
	

    }
    

    public void pintaMovimiento(int px, int py, int pax, int pay, int agente)
    {
	fin=false;
	agenteMover=agente;

	direccionActual[1]=px-pax;
	direccionActual[0]=py-pay;
	if(direccionActual[1]!=0) contadorMovimiento=tamParedy;
	else contadorMovimiento=tamParedx;
	timer = new Timer(1, this);
        timer.start();
        fin=false;

    }
	

    public void actionPerformed(ActionEvent e) 
    {
	if(contadorMovimiento==0)
	{
	    timer.stop();
	    fin=true;
	}
	       
	else
	{
	    if(agenteMover==0)
	    {
		posHamster[0]+=direccionActual[0];
		posHamster[1]+=direccionActual[1];
	    }
	    else
	    {
		posGato[0]+=direccionActual[0];
		posGato[1]+=direccionActual[1];
	    }
	    contadorMovimiento--;

	
	}
	repaint();
    }

}