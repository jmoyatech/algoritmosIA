
import java.util.Stack;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

/* Clase para generar un  laberinto */

class GeneraLaberinto
{
    int size;
    int laberinto[];
    int visitados[];
    Random aleatorio;
    
    public int entradax=0;
    public int entraday=1;
    public int salidax;
    public int saliday;     /* Se calcula en función del tamaño una vez generado el laberinto */


    /* Los laberintos siempre tienen tamaño impar */
    GeneraLaberinto(boolean semilla) 
    {
	if(semilla==true) aleatorio=new Random(1234);
	else aleatorio=new Random();
    }

    
    int valor(int pos)
    {
	if(pos<0||pos>=size*size) return 1;
	else return laberinto[pos];
    }


    boolean cerrado(int pos)
    {
	if(pos<size||valor(pos-1)==1&&valor(pos+1)==1&&valor(pos-size)==1&&valor(pos+size)==1)
	   return true;
	   else return false;
    }
	

    ArrayList obtenVecinos(int pos, boolean desordena)
    {
	ArrayList temp=new ArrayList<Integer>();
	
	if(pos-2>=0&&(pos-2)%size!=size-1)   temp.add(pos-2);
	if((pos+2)<size*size&&(pos+2)%size!=0)  temp.add(pos+2);
	if(pos-(size*2)>0) temp.add(pos-(size*2));
        if(pos+2*size<size*size) temp.add(pos+2*size);
     
        if(desordena)
	{
	    for(int j=0;j<temp.size();j++)
	    {
		if(aleatorio.nextFloat()>0.5)
		{
		    Integer aux=(Integer)temp.get(j);
		    temp.set(j,temp.get((j+1)%temp.size()));
		    temp.set((j+1)%temp.size(),aux);
		}
	    }
	}

	return temp;
    }


    
    // convierte el laberinto unidimensional en bidimensional
    int [][] laberinto2D()
    {
	int res[][]=new int[size+2][size];
	for(int i=0;i<laberinto.length;i++)
	    res[(i/size)+1][(i%size)]=laberinto[i];
	
	// bordes del laberinto
	for(int i=0;i<size;i++)
	{
	    res[0][i]=1;
	    res[size+1][i]=1;
	}

	res[0][1]=0;
	res[size+1][size-2]=0;

	return res;
    }



    int[][] hazLaberinto(int s)
    {

	Stack pila=new Stack();

	if(s%2==0) s++;
	size=s;
	salidax=size+1;
	saliday=size-2;
	laberinto=new int [size*size];
	visitados=new int[size*size];
	for(int i=0;i<size*size;i++)
	 {
	     if((i/size)%2==1) laberinto[i]=1;
	     else laberinto[i]=((i+1)%2);
	     visitados[i]=0;
	 }



	int totalVisitados=1;
	int actual=1;
	visitados[actual]=1;	
	laberinto[actual]=0;


	


	do 
	{
	    ArrayList<Integer>vecinos=obtenVecinos(actual,true);	       
	    for(int i=0;i<vecinos.size();i++)
	    {
		int siguiente=vecinos.get(i);
		if(visitados[siguiente]==0)
		{	
		    pila.push(siguiente);
		    if(cerrado(siguiente)) laberinto[(siguiente+actual)/2]=0;
		 }
		
	    }
	    actual=(Integer)pila.pop();	  
	    visitados[actual]=1;
	    totalVisitados++;
	} while(!pila.isEmpty());
        return laberinto2D();		
    }
}

