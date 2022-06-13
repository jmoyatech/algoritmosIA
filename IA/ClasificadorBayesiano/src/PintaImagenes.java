import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;

public class PintaImagenes extends JPanel implements MouseListener, MouseMotionListener
{    


    Graphics2D labG;
    BufferedImage digito;
    Color fondo=Color.GRAY;
    
    
    PintaImagenes()
    {
	setBackground(fondo);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }


    void pintaImagen(byte values[],double escala)
    {
	int tam=(int) Math.sqrt(values.length);

	BufferedImage aux=new BufferedImage (tam,tam, BufferedImage.TYPE_BYTE_GRAY);

	WritableRaster wr = aux.getRaster();
	wr.setDataElements (0, 0, tam, tam, values);
	digito = new BufferedImage((int)(tam*escala), (int)(tam*escala), BufferedImage.TYPE_BYTE_GRAY);
	Graphics2D graphics2D = digito.createGraphics();
	graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	graphics2D.drawImage(aux, 0, 0, (int)(tam*escala), (int)(tam*escala), null);
	graphics2D.dispose();
	repaint();
    }


    /* Pinta una imagen en función de valores de probabilidad */
    void pintaImagen(double values[],double escala)
    {
	int tam=(int) Math.sqrt(values.length);

	byte auxpixels[]=new byte[values.length];

	for(int i=0;i<tam*tam;i++)
	{
	    auxpixels[i]=(byte) (values[i] * 255);//+127);	    	    
	}

	BufferedImage aux=new BufferedImage (tam,tam, BufferedImage.TYPE_BYTE_GRAY);

	WritableRaster wr = aux.getRaster();
	wr.setDataElements (0, 0, tam, tam, auxpixels);
	digito = new BufferedImage((int)(tam*escala), (int)(tam*escala), BufferedImage.TYPE_BYTE_GRAY);
	Graphics2D graphics2D = digito.createGraphics();
	graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	graphics2D.drawImage(aux, 0, 0, (int)(tam*escala), (int)(tam*escala), null);
	graphics2D.dispose();
	repaint();
    }

    
    public void paint(Graphics g) 
    {

	 super.paintComponent (g); 
	 g.drawImage (digito, 0, 0, this );

	// super.paint(g);
	// Graphics2D g2 = (Graphics2D) g;
	// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	// g2.drawImage(digito,20,20,this);
	// Toolkit.getDefaultToolkit().sync();
        // g2.dispose();
	
    }


    void borra()
    {
	Graphics2D g = digito.createGraphics();
	g.setColor(Color.BLACK);
	g.fillRect(0,0,digito.getWidth(),digito.getHeight());
	g.setColor(Color.WHITE);
	this.repaint();
    }


    byte []getPixels(int escala)
    {

	Image aux=digito.getScaledInstance(digito.getWidth()/escala,digito.getHeight()/escala,Image.SCALE_SMOOTH);
	BufferedImage escaledImage = new BufferedImage (digito.getWidth()/escala,digito.getHeight()/escala,BufferedImage.TYPE_BYTE_GRAY);
	escaledImage.createGraphics().drawImage(aux, 0, 0, this);

	System.out.println("en Get Pixels ..");
	
	byte[] res = ((DataBufferByte) escaledImage.getRaster().getDataBuffer()).getData();
	System.out.println("HECHO !");
        return res;
    }
    	
//============================================================= mousePressed
    public void mousePressed(MouseEvent e) {

    }
    
    //============================================================= mouseDragged
    public void mouseDragged(MouseEvent e) 
    {
	int radio=10;
	Graphics2D g = digito.createGraphics();
	Point p=e.getPoint();
	g.fillOval(p.x - radio, p.y - radio, 2 * radio, 2 * radio);
        this.repaint();            // After change, show new shape
    }
    
    //============================================================ mouseReleased
    public void mouseReleased(MouseEvent e) 
    {
        
    }

    public void mouseMoved  (MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited (MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
   
}