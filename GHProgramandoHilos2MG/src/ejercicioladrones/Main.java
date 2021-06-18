/* 2021
 * Programador: Miguel Gim�nez Lag
 */
package ejercicioladrones;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		/* 
		 * El siguiente ejercicio es una adaptaci�n desarrollada de uno visto en clase.
		 * La idea original es que el usuario introduzca un n�mero de lingotes. Entonces, dos ladrones alternativamente ir�an "tirando" un dado de 6 caras.
		 * De esta forma se determinar�a la cantidad que ir�an robando. En el momento en el que no pudieran sacar tantos lingotes porque no queden, finalizar�an.
		 * He adaptado este planteamiento para que funcione con un n�mero indeterminado de hilos, intentando que a�n as� vayan actuando en orden,
		 * aunque en determinados casos puede darse que un mismo ladr�n repita varias veces seguidas (hasta finalizar completamente su actividad), en caso de que los siguientes hayan sido atrapados o hayan huido.
		 * Los ladrones terminan en 3 casos:
		 * 	1 - Que intenten robar 0 lingotes, supuesto en el que se considera que son atrapados.
		 * 	2 - Que intenten robar m�s lingotes de los que quedan en la caja, supuesto en el que se dan por satisfechos y huyen.
		 *  3 - Que ya no queden lingotes en el banco, momento en el que huyen (a�n podr�an verse atrapados si sacan un 0 en su "tirada" de robo antes de comprobar que no quedan lingotes).
		 *  Al final del ejercicio, se hace un balance del robo, indicando cu�ntos ladrones han sido atrapados, cu�nto se ha robado (y por qui�n) y cu�nto queda en el Banco. 
		 */
		Scanner sc = new Scanner (System.in);
		//Estructura para que el usuario introduzca el n�mero de lingotes. Se controla que sea positivo y entero. 
		int lingotes = -1;
		int x;
		while (lingotes == -1) {
			try {
				System.out.println("Introduce el n�mero de lingotes que habr� en el banco. Debe ser un n�mero entero positivo");
				x = sc.nextInt();
				if (x > 0) {
					lingotes = x;
				}else {
					System.out.println("El n�mero debe ser positivo");
				}
			}catch(Exception e) {
				System.err.println("NO se ha introducido un ENTERO");
				sc.nextLine();
			}	
		}
		//Estructura para que el usuario introduzca el n�mero de ladrones. Se controla que sea un n�mero positivo y entero.
		int numLadrones = - 1;
		int y;
		while (numLadrones == -1) {
			try {
				System.out.println("Introduce el n�mero de ladrones que asaltar�n el banco. Debe ser un n�mero entero y positivo");
				y = sc.nextInt();
				if (y > 0) {
					numLadrones = y;
				}else {
					System.out.println("El n�mero debe ser positivo");
				}
			}catch(Exception e) {
				System.err.println("NO se ha introducido un ENTERO");
				sc.nextLine();
			}	
		}
		sc.close();  //cierre del Scanner, ya que no se va a emplear m�s.
		
		//Objeto Banco, sobre el que se sincronizar� la actuaci�n de los ladrones.
		Banco banco = new Banco (lingotes, numLadrones);
		
		//Se lanzan tantos hilos como ladrones
		Thread [] th = new Thread [numLadrones];
		for (int i = 0; i < th.length; i++) {
			th[i] = new Thread (new HiloLadron(i+1, banco));  //cada uno recibe un n�mero identificativo y la misma instancia de banco.
			th[i].start();
		}
		//Aqu� se espera a que todos los hilos finalicen su tarea.
		for (int i = 0; i < th.length; i++) {
			try {
				th[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Y tras ello se obtiene la informaci�n sobre el asalto.
		System.out.println("--------------------------------------------------");
		System.out.println("BALANCE FINAL");
		int atrapados = banco.getLadronesAtrapados();
		//Si todos los ladrones son atrapados se considera que el robo es un fracaso y no se detalla nada m�s.
		if (atrapados == numLadrones) { 
			System.out.println("Todos los ladrones fueron atrapados. El robo fracas� y se recuperaron todos los lingotes");
		}else {  //en caso contrario:
			if (atrapados == 0) {  //Se indica si no se atrap� a ning�n ladr�n
			System.out.println("Ning�n ladr�n fue atrapado por los guardias");
			}else   { //o a uno o m�s.
				if (atrapados == 1) {
					System.out.println("Se atrap� "+atrapados+" ladr�n");
				}else {
					System.out.println("Se atraparon "+atrapados+" ladrones");
				}
			}
			System.out.println("El bot�n se ha repartido de la siguiente manera:\n*Se considera que los lingotes que hayan sido robados por ladrones posteriormente atrapados se pierden (ni los recupera el banco ni se los queda el ladr�n)");
			int [] roboTotal = banco.getRobaCadaLadron(); //con el getter de la Clase Banco se obtiene la informaci�n de lo que han ido robando.
			int totalRobado = 0;							// aqu� para acumular el total.
			for (int i = 0; i < roboTotal.length; i++) {  //se va recorriendo el array con lo robado por cada uno y se muestra.
				if (roboTotal[i] != 0) {
					System.out.println("\tEl ladr�n "+(i+1)+" ha robado "+roboTotal[i]+" lingotes");
					totalRobado += roboTotal[i];
				}else {
					System.out.println("\tEl ladr�n "+(i+1)+" fue atrapado y perdi� el bot�n");
				}
			}
			if (totalRobado != 0) {   //Y a modo de conclusi�n se detalla lo que qued� en el banco, el total robado y lo que se perdi� (ni robado ni recuperado).
				System.out.println("Tras el asalto, en el banco  de los "+lingotes+" lingotes originales ha(n) quedado "+banco.getLingotes()+", habi�ndose robado efectivamente "+totalRobado+" y perdi�ndose "+banco.getLingotesPerdidos());
			}
		}
	}//fin del main
}//fin de la clase