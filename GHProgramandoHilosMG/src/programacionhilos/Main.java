/* 2021
 * Programador: Miguel Gim�nez Lag
 */
package programacionhilos;

import java.util.Random;
import java.util.Scanner;

public class Main {

	static Scanner sc = new Scanner(System.in);
	static Random r = new Random();
	public static void main(String[] args) {
		/* 
		 * El presente ejercicio fue parte de mi examen final de Programaci�n de Servicios y Procesos, por el profesor Pedro Pablo Garc�a.
		 * Obtuve la m�xima puntuaci�n posible en dicho examen. Con esta afirmaci�n solo intento demostrar que no he copiado la soluci�n 
		 * que, por otra parte, no llegamos a ver en clase.
		 * S� debo se�alar que el c�digo a continuaci�n presentado incluye algunos retoques y  mejoras, adem�s de comentarios explicativos.
		 * Se ped�a:
		 * 1 - Generar una matriz cuadrada cuyas dimensiones deben ser 6 o m�s y siempre m�ltiplo de 3. 
		 * El usuario introduce el tama�o y se controla que cumpla la condici�n.
		 * 2 - Rellenar dicha matriz con n�meros aleatorios de 1 a 255
		 * 3 - Mediante Hilos (tantos como n�cleos tenga el equipo), recorrer la matriz. Hay que analizar las columnas de tres en tres. 
		 * La primera columna de cada terna indica valores rojos,
		 * la segunda, verdes, y la tercera, azules. Por cada tr�o se ir� comprobando cu�l de los tres valores es mayor,
		 * si el rojo, el verde o el azul, guardando .
		 * Adem�s, se sumar�n todos los valores correspondientes a cada color.
		 * 4 - Se mostrar� el total de cada color, indicando cu�l es el tono predominante.
		 * Tambi�n se mostrar� para cada color cu�ntas veces ha sido el mayor de su terna, y el porcentaje que respresenta sobre el total.
		 */
		
		//Variable empleada para almacenar valores mandados desde los hilos, sincroniz�ndolos para evitar errores
		Almacen al = new Almacen();
		
		int [][] matriz = generaMatrizAleatoria();
		mostrarMatriz(matriz);
		
		//Para saber cu�ntos n�cleos tiene el equipo empleo Runtime, y a partir de este n�mero se generar�n los Hilos.
		Runtime rt = Runtime.getRuntime();
		int numCores = rt.availableProcessors();
		//Con este condicional se controla que no se generen m�s Hilos de los necesarios para evitar su infrautilizaci�n.
		if (numCores > matriz.length) {  
			numCores = matriz.length;	
		}
		/*Java trunca la divisi�n por defecto, pero para que quede m�s claro
		que debe redondear a la baja dejo expl�cito el math.floor 
		Con estas variables se definen los rangos de actuaci�n de cada hilo (cu�ntas filas va a examinar cada uno)*/
		int rango = (int) Math.floor(matriz.length/numCores);   
		int posIni = 0;										  
		int posFin = rango;
		
		//Se crea un array de Hilos con las dimensiones obtenidas para el n�mero de n�cleos
		Thread [] th = new Thread [numCores];
	    /*Y se lanzan los hilos, recibiendo como par�metros d�nde empiezan a analizar, d�nde acaban, la matriz y el Almac�n
		 *Las posiciones de inicio y fin se van actualizando para cada hilo, 
		 *controlando con el if final que el �ltimo hilo realice toda la tarea restante, 
		 *en caso de que la divisi�n con la que se obtiene el rango no fuera exacta
		 */
		for (int i = 0; i < th.length; i++) {
			th[i] = new Thread (new RHilo(posIni, posFin, matriz, al));
			th[i].start();
			posIni = posFin;
			posFin += rango;
			if (i == th.length-2) {
				posFin = matriz.length;
			}
		}
		//Nuevo for para esperar a que todos los hilos finalicen su tarea
		for (int i = 0; i < th.length; i++) {
			try {
				th[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//En el array colores se recoge la cuenta de los colores dominantes en cada terna, calculados en la clase Almacen
		int [] colores = al.getContadorColores();
		//En el array totalColores se recoge el valor total de cada color (la suma de todas sus columnas), calculado en la clase Almacen
		int [] totalColores = al.getTotalColores();
		//Se muestran los totales
		System.out.println("La suma total de los colores rojos es "+totalColores[0]+", de los verdes "+totalColores[1]+" y de los azules "+totalColores[2]);
		//Se calcula cu�l es el tono general
		if ((totalColores[0] > totalColores[1]) && (totalColores[0]>totalColores[1])) {
			System.out.println("El tono general de la imagen es rojizo");
		}else if((totalColores[1] > totalColores[0]) && (totalColores[1]>totalColores[2])) {
			System.out.println("El tono general de la imagen es verdoso");
		}else {
			System.out.println("El tono general de la imagen es azulado");
		}
		//Se saca la suma total
		double sumaTodo = (double) (totalColores[0]+ totalColores[1] + totalColores[2]);
		//Y se calculan los porcentajes, peque�o arreglo para que solo salgan dos decimales
		double porcentajeRojo = (totalColores[0]*100)/sumaTodo;
		porcentajeRojo = Math.round(porcentajeRojo * 100.0)/ 100.0;
		double porcentajeVerde = (totalColores[1]*100)/sumaTodo;
		porcentajeVerde = Math.round(porcentajeVerde * 100.0)/ 100.0;
		double porcentajeAzul = (totalColores[2]*100)/sumaTodo;
		porcentajeAzul = Math.round(porcentajeAzul * 100.0)/ 100.0;
		
		//Muestra cu�ntas veces ha sido dominante cada color y el porcentaje que representa respecto al total
		System.out.println("El n�mero total de rojos es "+colores[0]+" y su porcentaje es "+porcentajeRojo);
		System.out.println("El n�mero total de verdes es "+colores[1]+" y su porcentaje es "+porcentajeVerde);
		System.out.println("El n�mero total de azules es "+colores[2]+" y su porcentaje es "+porcentajeAzul);
	

	}
	/**
	 * Recibe la matriz generada y la muestra por consola. Para que las columnas queden alineadas,
	 * se controla la longitud de cada n�mero y en funci�n de ella se dan m�s o menos espacios.
	 * @param matriz generada previamente con el m�todo generaMatrizAleatoria().
	 * @return void
	 */
	private static void mostrarMatriz(int[][] matriz) {
		for (int i = 0; i < matriz.length; i++) {
			for (int j = 0; j < matriz[i].length; j++) {
				if (matriz[i][j] < 10) {
					System.out.print(matriz[i][j]+"    ");
				}else if (matriz[i][j] < 100) {
					System.out.print(matriz[i][j]+"   ");
				}else {
					System.out.print(matriz[i][j]+"  ");
				}
				
			}
			System.out.println();
		}
		
	}
	/**
	 * M�todo invocado desde la declaraci�n de una matriz.
	 * El usuario debe introducir las dimensiones cumpliendo las condiciones dadas. Se controla que el usuario podr�a introducir un dato err�neo, 
	 * ya sea un entero que no cumpla las condiciones u otro tipo de dato.
	 * Hecho esto, se declara una matriz resultado con las dimensiones dadas y se rellena con n�meros aleatorios.
	 * @return la matriz resultado, con las dimensiones deseadas y ya rellenada, que se almacenar� en la matriz declarada con la llamada del m�todo.
	 */
	private static int[][] generaMatrizAleatoria() {
		int tama = -1;
		int n = -1;
		boolean seguir = true;
		do {
			try {
				System.out.println("Introduce las dimensiones de una matriz cuadrada. Debe ser un entero mayor que 6 y m�ltiplo de 3");
				n = sc.nextInt();
			} catch (Exception e) {
				System.err.println("NO se ha introducido un ENTERO");
				sc.nextLine();
			}
			if ((n >= 6) && (n % 3 == 0)) {
				tama = n;
				seguir = false;
			}
		}while(seguir);
		
		int [][] resu = new int [tama][tama];
		
		for (int i = 0; i < resu.length; i++) {
			for (int j = 0; j < resu[i].length; j++) {
				resu[i][j] = r.nextInt(255)+1; 
			}
		}
		return resu;
	}
}