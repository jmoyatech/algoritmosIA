import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.lang.reflect.Constructor;
import java.util.Date;

public class Test extends JApplet implements ActionListener
{

    /* Clase que almacena las muestras de entrenamiento */
    ColeccionImagenes test;   

    /* Modelo Naive-Bayes que representa un dígito */
    Clasificador clasificador;

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


    /* Clase para pintar una imagen a partir de una matriz de bytes, 
       de flotantes, etc */

    PintaImagenes id, ic;

    /* Imagen dígitos */
    JPanel jpid;;
    JLabel jlid;

    
    /* Panel Botones */

    JPanel jpbotones;
    JButton jbclasifica;
    JButton jbborra;
    JButton jbsiguiente;
    JButton jbtodas;


    /* Panel resultado */

    JPanel jpresultado;
    JLabel jlresultado;
    JTextField jtfresultado;
    JTextField jtfclasificacion;

    /* Panel probabilidades */

    JPanel jpprobabilidades;
    JLabel jlprobabilidades;
    JTextArea jtaprobabilidades;
    
    double escalaid=8.0;



    /* Panel Tasas */

    JPanel jptasas;
    JLabel jltasas1;
    JTextField jtftasas1;

    JPanel jptasas2;
    JLabel jltasas2;
    JTextField jtftasas2;


    

    long score=0;
    

    String []parametros={"-ficheroTest","-clasificador","-tasa"};

    int contadorMuestras=0;
    int contadorFallos=0;


    Timer timer;

    boolean batch=false;

    Test(String s[])
    {

	leeParametros(s);
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
	if(batch) calculaTasaError();
	else muestraInterfaz();
    }

    void calculaTasaError()
	{
	    int errores=0;
	    for(int i=0;i<test.numero;i++)
	    {
		System.out.print("Clasificando muestra " + i + "  ");
		test.imagenes[i].binariza((byte)0);
		String res=clasificador.clasifica(test.imagenes[i].getPixels());
		System.out.println(" resultado = " + res + "  clase real = " + test.etiqueta);
		if(!res.equals(test.etiqueta)) errores++;
	    }

	    System.out.println("Muestras totales clasificadas = " + test.numero + " Tasa de Error = " + (double) errores/test.numero);
	    System.exit(0);
	}

   
    /* Métodos para gestionar los parámetros de entrada y ejecutar la acción correspondiente */


    void muestraUso()
    {
	System.out.println("java Main [parametros]\n\nParametros\n----------\n");
	System.out.println("   -h. Esta ayuda ");
	System.out.println("   -clasificador fichero_modelos. Carga un clasificador basado en el conjunto de modelos especificados en el fichero de texto pasado como parámetro. Cada línea de dicho fichero contendrá el nombre de un fichero que almacena uno de los modelos\n");

	System.out.println("   -ficheroTest [num]. Fichero que contiene las imagenes del fichero de test. Si se especifica a continuación un número n, se cargarán las n primeras imágenes. Si no se especifica, se cargan todas");
	System.out.println("   -tasa. Realiza la clasificación en modo batch, obteniendo la tasa de error de clasificación");
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
	String ficheroTest="";
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

	        /* Fichero con las muestras de test */
	       
	       case 0: if(i<s.length-1)
		       {
			   ficheroTest=s[++i];
			   if(i<s.length-1&&s[i+1].charAt(0)!='-')			 
			   {
			      numMuestras=new Integer(s[++i]).intValue();
			   }
		       }  		       
		       else
		       {
			   muestraUso();
			   System.exit(-1);
		       }
		       test=new ColeccionImagenes(ficheroTest,numMuestras);
		       tamImagen=test.imagenes[0].tam;
		       tamEscalado=(int) (tamImagen*escalaid);
		       break;

	    case 1: if(i<s.length-1&&s[i+1].charAt(0)!='-')
			   clasificador=new Clasificador(s[++i]);
		       else
		       {
			   muestraUso();
			   System.exit(-1);
		       }
  		    break;

	    case 2: batch=true;
  		    break;

	    }

	}

    }


   void muestraImagenTest(int num)
   {

       /* Obtiene la imagen correspondiente a la matriz de pixels */
       if(test.imagenes[num].binarizada==false)
	   id.pintaImagen(test.imagenes[num].pixels,escalaid);   
       else
       {
	   byte auxpixels[]=new byte[tamImagen*tamImagen];
	   for(int i=0;i<tamImagen*tamImagen;i++) auxpixels[i]=(byte)(test.imagenes[num].pixels[i]*255);
	   id.pintaImagen(auxpixels,escalaid);   
       }


       jpid.setBounds(margenIzquierdo,margenSuperior,tamEscalado, tamEscalado+margenSuperior);
       jpid.removeAll();
       jpid.add(jlid,BorderLayout.NORTH);
       jpid.add(id,BorderLayout.CENTER);          
       
   }


  

   void muestraInterfaz()
   {

        /* Ventana principal */
	ventana=new JFrame("Clasificador Digitos");
	ventana.getContentPane().setBackground(new Color(80,80,80));
	ventana.getContentPane().setLayout(null);
	ventana.setSize(550,460);


	/* Panel donde se muestran la imagen actual */
	jpid=new JPanel();
	jpid.setVisible(true);
	jpid.setBackground(colorFondoEtiqueta);
	jpid.setLayout(new BorderLayout());
	id=new PintaImagenes();
	jlid=new JLabel("Muestra Test");	
	jlid.setBorder(new EmptyBorder(5,5,5,5));
	jlid.setHorizontalAlignment(SwingConstants.CENTER);
	jpid.setBounds(margenIzquierdo,margenSuperior,tamEscalado+margenIzquierdo,tamEscalado+jlid.getBounds().height);
	jlid.setForeground(colorEtiqueta);
	jlid.setBackground(colorFondoEtiqueta);
	ventana.getContentPane().add(jpid);


	/* Panel de botones */
	jpbotones=new JPanel();
	jpbotones.setBackground(new Color(0,80,0,0));
	jpbotones.setBounds(margenIzquierdo,10+margenSuperior+jlid.getBounds().height+tamEscalado,tamEscalado+20,95);
	jpbotones.setLayout(new FlowLayout(FlowLayout.LEFT,10,20));
	jbclasifica=new JButton("Clasifica");
	jbborra=new JButton("Borra");
	jpbotones.add(jbclasifica);
	jpbotones.add(jbborra);
	jbsiguiente=new JButton("Siguiente");
	jbtodas=new JButton("Todas");
	jpbotones.add(jbsiguiente);
	jpbotones.add(jbtodas);
	ventana.getContentPane().add(jpbotones);

	jbclasifica.addActionListener (this);
        jbclasifica.setActionCommand ("clasifica");
	jbsiguiente.addActionListener (this);
        jbsiguiente.setActionCommand ("siguiente");
	jbborra.addActionListener (this);
        jbborra.setActionCommand ("borra");
	jbtodas.addActionListener (this);
        jbtodas.setActionCommand ("todas");




	/* Panel resultado */
	jpresultado = new JPanel();
	jpresultado.setBackground(new Color(0,0,90));
	jpresultado.setBounds(3*margenIzquierdo+tamEscalado,margenSuperior,tamEscalado,90);
	jpresultado.setLayout(new FlowLayout());
	jlresultado=new JLabel("Etiqueta de clase");
	jlresultado.setHorizontalAlignment(SwingConstants.CENTER);
	jlresultado.setForeground(Color.WHITE);
	Font font = new Font("Times New Roman", Font.PLAIN,16);
	jlresultado.setFont(font);
	jpresultado.add(jlresultado);//,BorderLayout.NORTH);

	jtfresultado=new JTextField(10);
	jtfresultado.setFont(font);
	jtfresultado.setBackground(Color.BLACK);
	jtfresultado.setForeground(Color.CYAN);
	jpresultado.add(jtfresultado);//,BorderLayout.CENTER);
	
	jtfclasificacion=new JTextField(5);
	jtfclasificacion.setBackground(Color.BLACK);
	jtfclasificacion.setFont(font);
	jpresultado.add(jtfclasificacion);//,BorderLayout.SOUTH);
	ventana.getContentPane().add(jpresultado);
	
	/* Panel probabilidades */

	jpprobabilidades = new JPanel();
	jpprobabilidades.setBackground(new Color(0,0,90));
	jpprobabilidades.setBounds(3*margenIzquierdo+tamEscalado,5+margenSuperior+jpresultado.getBounds().height,tamEscalado,tamEscalado);
	jpprobabilidades.setLayout(new BorderLayout());
	

	jlprobabilidades=new JLabel("Probabilidades");
	jlprobabilidades.setHorizontalAlignment(SwingConstants.CENTER);
	jlprobabilidades.setForeground(Color.WHITE);
	jpprobabilidades.add(jlprobabilidades,BorderLayout.NORTH);


	jtaprobabilidades=new JTextArea();
	jtaprobabilidades.setBackground(Color.BLACK);
	jtaprobabilidades.setForeground(Color.WHITE);
	jpprobabilidades.add(jtaprobabilidades,BorderLayout.CENTER);

	ventana.getContentPane().add(jpprobabilidades);


	/* Panel Tasas */

	jptasas=new JPanel();
	jptasas.setBackground(new Color(0,0,90));
	jptasas.setBounds(margenIzquierdo,2*margenSuperior+jpid.getBounds().height+jpbotones.getBounds().height,480,50);
	jltasas1=new JLabel("Muestras vistas");
	jltasas1.setForeground(Color.WHITE);
	jptasas.add(jltasas1);

	jtftasas1=new JTextField(4);
	jtftasas1.setBackground(Color.BLACK);
	jtftasas1.setForeground(Color.WHITE);
	jptasas.add(jtftasas1);

	jltasas2=new JLabel("Tasa error");
	jltasas2.setBorder(new EmptyBorder(0,80,0,0));
	jltasas2.setForeground(Color.WHITE);
	jptasas.add(jltasas2);

	jtftasas2=new JTextField(4);
	jtftasas2.setBackground(Color.BLACK);
	jtftasas2.setForeground(Color.WHITE);
	jptasas.add(jtftasas2);
	
	ventana.getContentPane().add(jptasas);

	ventana.setVisible(true);


	timer = new Timer(50, new ActionListener() 
	{
	    public void actionPerformed(ActionEvent e) 
	    {
		if(contadorMuestras<test.numero)
		    procesaMuestra();
	    }
	    });
	timer.setRepeats(true);

    }

    void procesaMuestra()
    {
	muestraImagenTest(contadorMuestras);
	String res=clasificador.clasifica(test.imagenes[contadorMuestras].pixels);
	jtfresultado.setText(res);
	if(res.equals(test.etiqueta))
	{
	    jtfclasificacion.setForeground(Color.green);
	    jtfclasificacion.setText("OK");
	}
	else
	{
	    jtfclasificacion.setForeground(Color.red);
	    jtfclasificacion.setText("ERROR");
	    contadorFallos++;
	}
	String sprobs="";
	jtaprobabilidades.setText("");
	for(int i=0;i<clasificador.modelos.length;i++)
	{
	    sprobs=clasificador.modelos[i].etiqueta+"\t"+clasificador.probabilidades[i]+"\n";
	    jtaprobabilidades.append(sprobs);
	 }
	contadorMuestras++;
	jtftasas1.setText(new String(new Integer(contadorMuestras).toString()));
	String aux=new Double((double) contadorFallos/contadorMuestras).toString();
	if(aux.length()>6) aux=aux.substring(0,5);
	jtftasas2.setText(aux);
    }
	

   public void actionPerformed(ActionEvent e) 
   {      
       String accion;
       accion = e.getActionCommand ();
       if (accion.equals("siguiente")) 
       {
           if(contadorMuestras<test.numero)
           {
	       muestraImagenTest(contadorMuestras);
           }

       }
       else if(accion.equals("clasifica"))
       {
	   byte pixels[]=id.getPixels((int)escalaid);
	   id.pintaImagen(pixels,escalaid);   

	   String res=clasificador.clasifica(pixels);
	   jtfresultado.setText(res);
	   if(res.equals(test.etiqueta))
	   {
	       jtfclasificacion.setForeground(Color.green);
	       jtfclasificacion.setText("OK");
	   }
	   else
	   {
	       jtfclasificacion.setForeground(Color.red);
	       jtfclasificacion.setText("ERROR");
	       contadorFallos++;
	   }
	   String sprobs="";
	   jtaprobabilidades.setText("");
	   for(int i=0;i<clasificador.modelos.length;i++)
	   {
	       sprobs=clasificador.modelos[i].etiqueta+"\t"+clasificador.probabilidades[i];
	       if(sprobs.length()>15) sprobs=sprobs.substring(0,14);
	       sprobs=sprobs+"\n";
	       jtaprobabilidades.append(sprobs);
	   }
 	   contadorMuestras++;
	   jtftasas1.setText(new String(new Integer(contadorMuestras).toString()));
   	   String aux=new Double((double) contadorFallos/contadorMuestras).toString();
	   if(aux.length()>6) aux=aux.substring(0,5);
	   jtftasas2.setText(aux);


       }
       else if(accion.equals("borra"))
       {
	   id.borra();
       }

       else if(accion.equals("todas"))
       {
	   if(contadorMuestras<test.numero)
	       timer.start();
       }


   }
    
   public static void main(String s[])
    {
	    Test t=new Test(s);	   
	    t.muestraImagenTest(0);
    }
}