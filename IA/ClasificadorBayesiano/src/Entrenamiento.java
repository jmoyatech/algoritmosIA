import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.lang.reflect.Constructor;
import java.util.Date;



public class Entrenamiento extends JApplet implements ActionListener
{


    /* Clase que almacena las muestras de entrenamiento */
    ColeccionImagenes training;   

    /* Modelo Naive-Bayes que representa un dígito */
    EntrenamientoModeloNaiveBayes modelo;

    /* Ventana principal */
    JFrame ventana = null;
    /* Disposición de los elementos del interfaz */
    

    /* Tamaño de las imagenes de entrenamiento */
    int tamImagen;

    /* Tamaño de las imagenes tal y como se muestran */
    int tamEscalado;
    

    /* Margenes de la ventana */
    int margenSuperior=20;
    int margenIzquierdo=20;


    /* Color de las etiquetas */
    Color colorEtiqueta=new Color (180,10,10);
    Color colorFondoEtiqueta=new Color(0,0,150);
    /* Objetos gráficos */



    /* Parte de las muestras */

    /* Panel del título de las muestras */
    JPanel jptm; 
    JLabel jltm;

    /* Panel del título del clasificador */
    JPanel jptc;
    JLabel jltc;

    /* Clase para pintar una imagen a partir de una matriz de bytes, 
       de flotantes, etc */

    PintaImagenes id, ic;

    /* Imagen Clasificador */
    JPanel jpic;;
    JLabel jlic;

    /* Panel donde se muestran los valores de las probabilidades del clasificador */
    JPanel jpvic;
    JLabel jlvic;
    JTextPane jtvic;
    JScrollPane jsvic; 
    Color cvic;


    /* Imagen dígitos */
    JPanel jpid;;
    JLabel jlid;

    double escalaid=4.0;


    /* Panel donde se muestran los valores de los dígitos */
    JPanel jpvid;
    JLabel jlvid;
    JTextPane jtvid;
    JScrollPane jsvid; 
    Color cvid;
    String cadtam="",cadtam2="";

    /* Panel inferior. Botones y contador muestras */

    JPanel jpinf;
    JButton jbsiguiente, jbtodas, jbfin;
    JLabel jlinf;
    JTextField jtcontador;

    /* Panel inferior. Etiqueta asociada al modelo */
    JPanel jpinf2;
    JLabel jlet;
    JTextField jtet;

    JTextField infoT=new JTextField(42);    
    Font font = new Font("Times New Roman", Font.PLAIN,13);

    

    long score=0;
    


    String []parametros={"-ficheroEntrenamiento","-modelo","-etiqueta"};

    int contadorMuestras=0;

    Timer timer;

    boolean batch=false;
    String ficheroSalida="";
    String etiqueta;
    byte umbral=0;

    Entrenamiento(String s[])
    {

	leeParametros(s);
	modelo=new EntrenamientoModeloNaiveBayes(tamImagen*tamImagen);
	etiqueta=training.etiqueta;
	modelo.etiqueta=etiqueta;
	if(batch)
	{
	    for(int i=0;i<training.numero;i++)
	    {
		System.out.println("Procesando muestra " + i);
		training.imagenes[i].binariza((byte)0);
		modelo.procesaMuestra(training.imagenes[i].getPixels());
	    }

	    modelo.guardaModelo(ficheroSalida);
	    System.out.println("Modelo entrenado y guardado en el fichero " + ficheroSalida);
	    System.exit(-1);
	}

	infoT.setEditable(false);
	infoT.setFocusable(false);
	if(ventana!=null)
	{
	    ventana.addWindowListener(
		new WindowAdapter() 
		{
		    public void windowClosing(WindowEvent e) 
		    {
			System.exit(0);
		    }
		});

	}

	muestraInterfaz();
    }



    /* Métodos para gestionar los parámetros de entrada y ejecutar la acción correspondiente */


    void muestraUso()
    {
	System.out.println("java Main [parametros]\n\nParametros\n----------\n");
	System.out.println("   -h. Esta ayuda ");
	System.out.println("   -ficheroEntrenamiento [num]. Fichero que contiene las imagenes del fichero de entrenamiento. Si se especifica a continuación un número n, se cargarán las n primeras imágenes. Si no se especifica, se cargan todas");
	System.out.println("   -etiqueta etiqueta. Etiqueta de clase asignada al modelo aprendido ");
	System.out.println("   -modelo nombre_fichero. Realiza un entrenamiento en modo \"batch\" y almacena el modelo resultante en el fichero especificado");
    }


    int parametroAEntero(String s)
    {
	for(int i=0;i<parametros.length;i++)
	    if(s.equals(parametros[i])) return i;
	return (-1);
    }



    void leeParametros(String s[])
    {

        boolean semillaFija=false;
	String ficheroEntrenamiento="";

	int numMuestras=0;
        if(s.length<2||s[1].equals("-h")) 
	{
	    muestraUso();
	    System.exit(-1);
	}

	for(int i=0;i<s.length;i++)
	{	   
	    int a=parametroAEntero(s[i]);
	    switch(a)
	    {

	        /* Fichero con las muestras de entrenamiento */
	       
	       case 0: if(i<s.length-1)
		       {
			   ficheroEntrenamiento=s[++i];	
			   if(i<s.length-1&&s[i+1].charAt(0)!='-')			
			   {
			      numMuestras=new Integer(s[++i]).intValue();
			   }
			   else numMuestras=0;
		       }  		       
		       else
		       {
			   muestraUso();
			   System.exit(-1);
		       }
		       training=new ColeccionImagenes(ficheroEntrenamiento,numMuestras);
		       tamImagen=training.imagenes[0].tam;
		       tamEscalado=(int) (tamImagen*escalaid);
		       break;
	    
	    /* Entrenamiento BATCH. Se genera directamente el fichero de 
	       salida */
	       case 1: if(i>=s.length-1)
		       {
			   muestraUso();
			   System.exit(-1);
		       }
		       ficheroSalida=s[++i];
		       batch=true;
  		       break;

	       case 2:  if(i>=s.length-1)
		       {
			   muestraUso();
			   System.exit(-1);
		       }
		       etiqueta=s[++i];
  		       break;
	    }
	}
    }


   void muestraImagenTraining(int num)
   {

       /* Obtiene la imagen correspondiente a la matriz de pixels */
       if(training.imagenes[num].binarizada==false)
	   id.pintaImagen(training.imagenes[num].pixels,escalaid);   
       else
       {
	   byte auxpixels[]=new byte[tamImagen*tamImagen];
	   for(int i=0;i<tamImagen*tamImagen;i++) auxpixels[i]=(byte)(training.imagenes[num].pixels[i]*255);
	   id.pintaImagen(auxpixels,escalaid);   
       }


       /* Pinta la imagen */
       Rectangle r=jlid.getBounds();
       
       jpid.setBounds(margenIzquierdo,margenSuperior,tamEscalado, tamEscalado+margenSuperior);
       jpid.removeAll();
       jpid.add(jlid,BorderLayout.NORTH);
       jpid.add(id,BorderLayout.CENTER);

       
        /* Pinta la matriz de pixels */

       jtvid.setText("");
       Font font = new Font("Consolas", Font.PLAIN, 10);
       jtvid.setFont(font);
       StyledDocument doc = jtvid.getStyledDocument();
       Style style = jtvid.addStyle("", null);
       jtvid.setBackground(Color.BLACK);
       StyleConstants.setForeground(style, Color.red);
      
      
       try 
       { 
       	   doc.insertString(doc.getLength(), cadtam+"\n",style); 
       }
        catch (BadLocationException e)
       {}

     
       for(int i=0;i<tamImagen;i++)
       {
       	   StyleConstants.setForeground(style, Color.red);	   
       	   try 
       	   { 
       	       doc.insertString(doc.getLength(), new Integer(i).toString()+"  ",style); 	 
       	       if(i<10) doc.insertString(doc.getLength(),"  ",style); 	 
       	   }
       	   catch (BadLocationException e)
       	   {}
	   
       	   StyleConstants.setForeground(style, Color.white);	   
       	   String cad="";
       	   for(int j=0;j<tamImagen;j++)
       	   {
       	       cad=cad+training.imagenes[num].pixels[i*tamImagen+j]+"  ";  
       	       if(j>=10) cad=cad+" ";
       	   }
	   
       	   try 
       	   { 
       	       doc.insertString(doc.getLength(), cad+"\n",style); 
       	   }
       	   catch (BadLocationException e)
       	   {}

       }
       jtvid.setVisible(true);
       Rectangle r1=jpid.getBounds();
       Rectangle r2=jlvid.getBounds();

       jpvid.add(jlvid,BorderLayout.NORTH);
       jpvid.add(jtvid,BorderLayout.CENTER);
       jpvid.setBounds(margenIzquierdo,r1.height+margenSuperior*2,415,380);//r2.height+d1.height+r1.height+margenSuperior);

       jpinf.setBounds(margenIzquierdo,3*margenSuperior+jpvid.getBounds().height+jpid.getBounds().height,275,120);
       
   }


   void muestraEstadoClasificador()
   {

       /* Obtiene la imagen correspondiente a la matriz de probabilidades  */
       ic.pintaImagen(modelo.probabilidades,escalaid);   

       /* Pinta la imagen */
       Rectangle r=jlic.getBounds();
       Rectangle r3=jtvid.getBounds();
       jpic.setBounds(450,margenSuperior,tamEscalado, tamEscalado+margenSuperior);
       jpic.removeAll();
       jpic.add(jlic,BorderLayout.NORTH);
       jpic.add(ic,BorderLayout.CENTER);

       
       /* Pinta la matriz de pixels */
       jtvic.setText("");
       Font font = new Font("Consolas", Font.PLAIN, 10);
       jtvic.setFont(font);
       StyledDocument doc = jtvic.getStyledDocument();
       Style style = jtvic.addStyle("", null);
       jtvic.setBackground(Color.BLACK);
       StyleConstants.setForeground(style, Color.red);
            
       try 
       { 
       	   doc.insertString(doc.getLength(), cadtam2+"\n",style); 
       }
        catch (BadLocationException e)
       {}

       for(int i=0;i<tamImagen;i++)
       {
       	   StyleConstants.setForeground(style, Color.red);	   
       	   try 
       	   { 
       	       doc.insertString(doc.getLength(), new Integer(i).toString()+"  ",style); 	 
       	       if(i<10) doc.insertString(doc.getLength(),"  ",style); 	 
       	   }
       	   catch (BadLocationException e)
       	   {}
	   
       	   StyleConstants.setForeground(style, Color.white);	   
       	   String cad="";
       	   String aux;
       	   for(int j=0;j<tamImagen;j++)
       	   {
       	       double prob=modelo.probabilidades[i*tamImagen+j];
       	       if(prob<0.99) 
       		   aux="."+String.format("%.2f",prob).substring(2,4);
	      
       	       else aux="1.0";
       	       cad=cad+aux+"  ";  
       	       if(j>=10) cad=cad+" ";
       	   }
	   
       	   try 
       	   { 
       	       doc.insertString(doc.getLength(), cad+"\n",style); 
       	   }
       	   catch (BadLocationException e)
       	   {}

       }
       jtvic.setVisible(true);
       Rectangle r1=jpic.getBounds();
       Rectangle r2=jlvic.getBounds();      
       jpvic.setBounds(450,r1.height+margenSuperior*2,670,380);       
       jpvic.add(jlvic,BorderLayout.NORTH);
       jpvic.add(jtvic,BorderLayout.CENTER);
       jpinf2.setBounds(450,3*margenSuperior+jpvic.getBounds().height+jpid.getBounds().height,230,90);

   }

  

   void muestraInterfaz()
   {

        /* Ventana principal */
	ventana=new JFrame("Clasificador Digitos");
	ventana.getContentPane().setBackground(new Color(80,80,80));
	ventana.getContentPane().setLayout(null);
	ventana.setSize(1150,750);


	jptm=new JPanel();
	jptm.setBackground(new Color(50,50,50));
	jptm.setLayout(new BorderLayout());
	jptm.setBounds(tamEscalado+margenIzquierdo*2,margenSuperior,235,60);
	jltm=new JLabel("Información Muestras");
	Font fonte1 = new Font("Times New Roman", Font.PLAIN, 16);
	jltm.setHorizontalAlignment(SwingConstants.CENTER);
	jltm.setFont(fonte1);
	jltm.setForeground(Color.cyan);
	jptm.add(jltm,BorderLayout.CENTER);
	ventana.getContentPane().add(jptm);

	jptc=new JPanel();
	jptc.setBackground(new Color(50,50,50));
	jptc.setLayout(new BorderLayout());
	jptc.setBounds(580,margenSuperior,235,60);
	jltc=new JLabel("Información Clasificador");
	jltc.setHorizontalAlignment(SwingConstants.CENTER);
	jltc.setFont(fonte1);
	jltc.setForeground(Color.cyan);
	jptc.add(jltc,BorderLayout.CENTER);
	ventana.getContentPane().add(jptc);


	/* Panel donde se muestran la imagen actual */
	jpid=new JPanel();
	jpid.setVisible(true);


	jpid.setBackground(colorFondoEtiqueta);
	jpid.setLayout(new BorderLayout());
	id=new PintaImagenes();
	jlid=new JLabel("Muestra");
	jlid.setBorder(new EmptyBorder(5,5,5,5));
	jlid.setHorizontalAlignment(SwingConstants.CENTER);
	jpid.setBounds(margenIzquierdo,margenSuperior,tamEscalado+margenIzquierdo,tamEscalado+jlid.getBounds().height);
	jlid.setForeground(colorEtiqueta);
	jlid.setBackground(colorFondoEtiqueta);

	jtvid=new JTextPane();

	ventana.getContentPane().add(jpid);

	/* Panel donde se muestran los valores de la imagen actual */
	jpvid=new JPanel();
	jpvid.setVisible(true);
	cvid=new Color(0,0,150);

	cadtam="      ";
        for(int i=0;i<tamImagen;i++) 
	{
	    cadtam=cadtam+new Integer(i).toString()+" ";
	    if(i<10) cadtam=cadtam+" ";
	}

	
	jpvid.setBackground(colorFondoEtiqueta);
	jpvid.setLayout(new BorderLayout());


	jlvid=new JLabel("Matriz Pixels");
	jlvid.setForeground(colorEtiqueta);
	jlvid.setBorder(new EmptyBorder(5,5,5,5));
	jlvid.setHorizontalAlignment(SwingConstants.CENTER);
	jlvid.setSize(50,10);



	ventana.getContentPane().add(jpvid);


	/* Panel donde se muestran la imagen que representa al clasificador */
	jpic=new JPanel();
	jpic.setVisible(true);
	jpic.setBackground(colorFondoEtiqueta);
	jpic.setLayout(new BorderLayout());
	ic=new PintaImagenes();
	jlic=new JLabel("Clasificador");
	jlic.setBorder(new EmptyBorder(5,5,5,5));
	jlic.setHorizontalAlignment(SwingConstants.CENTER);

	jlic.setForeground(colorEtiqueta);
	jlic.setBackground(colorFondoEtiqueta);

	jtvic=new JTextPane();

	ventana.getContentPane().add(jpic);

	/* Panel donde se muestran las probabilidades del modelo */
	jpvic=new JPanel();
	jpvic.setVisible(true);
	cvic=new Color(0,0,150);

	cadtam2="      ";
        for(int i=0;i<tamImagen;i++) 
	{
	    if(i>=10) cadtam2=cadtam2+new Integer(i).toString()+"    ";
	    else cadtam2=cadtam2+new Integer(i).toString()+"     ";

	}

	
	jpvic.setBackground(colorFondoEtiqueta);
	jpvic.setLayout(new BorderLayout());
	jlvic=new JLabel("Matriz Probabilidades");
	jlvic.setForeground(colorEtiqueta);
	jlvic.setBorder(new EmptyBorder(5,5,5,5));
	jlvic.setHorizontalAlignment(SwingConstants.CENTER);
	//jpvic.add(jlvic,BorderLayout.NORTH);
	ventana.getContentPane().add(jpvic);



	/* Panel Inferior. Botones y contador de muestras */

	jpinf=new JPanel();
	jpinf.setLayout(new FlowLayout());
	jpinf.setBackground(new Color(0,0,90));
	jbsiguiente=new JButton("Siguente");
	jbtodas=new JButton("Todas");
	jbfin=new JButton("Guardar");
	jlinf=new JLabel("Muestras procesadas");
	jlinf.setBorder(new EmptyBorder(10,20,10,20));
	jlinf.setForeground (Color.CYAN);
	jtcontador=new JTextField(3);
	jtcontador.setBackground(Color.BLACK);
	jtcontador.setForeground(Color.GREEN);
        Font fontc = new Font("Consolas", Font.PLAIN, 12);
	jtcontador.setFont(fontc);
	jtcontador.setText("1");
	jtcontador.setHorizontalAlignment(JTextField.RIGHT);
	jpinf.add(jlinf);
	jpinf.add(jtcontador);



	jbsiguiente.addActionListener (this);
	jbsiguiente.setActionCommand ("siguiente");

	jbtodas.addActionListener (this);
	jbtodas.setActionCommand ("todas");

	jbfin.addActionListener (this);
	jbfin.setActionCommand ("fin");

	

	jpinf.add(jbsiguiente);
	jpinf.add(jbtodas);
	jpinf.add(jbfin);

	ventana.getContentPane().add(jpinf);

	/* Panel con la etiqueta del modelo */

	jpinf2=new JPanel();
	jpinf2.setLayout(new FlowLayout());
	jpinf2.setBackground(new Color(0,0,90));
	jlet=new JLabel("Etiqueta de Clase");
	Font jletfont=new Font("Times New Roman", Font.PLAIN,15);
	jlet.setBorder(new EmptyBorder(10,20,10,20));
	jlet.setFont(jletfont);
	jlet.setForeground (Color.CYAN);
	jtet=new JTextField(12);
	jtet.setFont(jletfont);
	jtet.setBackground(Color.BLACK);
	jtet.setForeground(Color.YELLOW);

	jtet.setText(etiqueta);
	jpinf2.add(jlet);
	jpinf2.add(jtet);

	ventana.getContentPane().add(jpinf2);

	ventana.setVisible(true);

	timer = new Timer(50, new ActionListener() 
	{
	    public void actionPerformed(ActionEvent e) 
	    {
		if(contadorMuestras<training.numero)
		    procesaMuestra();
	    }
	    });
	timer.setRepeats(true);
    }

    void procesaMuestra()
    {
	training.imagenes[contadorMuestras].binariza((byte) 0);
	muestraImagenTraining(contadorMuestras);
	modelo.procesaMuestra(training.imagenes[contadorMuestras].getPixels());
	muestraEstadoClasificador();
	contadorMuestras++;
	jtcontador.setText(new Integer(contadorMuestras).toString());
	this.repaint();

    }
	

   public void actionPerformed(ActionEvent e) 
   {
       String accion;
       accion = e.getActionCommand ();
       if (accion.equals("siguiente")) 
       {
	   if(contadorMuestras<training.numero)
	   {
	       procesaMuestra();
	   }

       }

       else if(accion.equals("todas"))
       {
	   if(contadorMuestras<training.numero)
	       timer.start();
       }

       else if(accion.equals("fin"))
       {

	   String nombre;
	   String dir;
	   JFileChooser jfc=new  JFileChooser();
	   jfc.setAcceptAllFileFilterUsed(false);
	   int res = jfc.showSaveDialog(this);
	   System.out.println("res = " + res);
	   if(res == JFileChooser.APPROVE_OPTION) 
	   {
	       //r=jfc.get
	       modelo.setEtiqueta(jtet.getText());
	       nombre=jfc.getSelectedFile().getAbsolutePath();
	       System.out.println("Guardando clasificador con nombre = " +  nombre);
	       modelo.guardaModelo(nombre);
	   }

       }
   }
    
   public static void main(String s[])
    {
	    Entrenamiento e=new Entrenamiento(s);	   
	    e.procesaMuestra();
	    e.muestraImagenTraining(0);
	    e.muestraEstadoClasificador();	 
    }
}