/* 2021
 * Programador: Miguel Giménez Lag
 */
package ejercicioladrones;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		/* 
		 * El siguiente ejercicio es una adaptación desarrollada de uno visto en clase.
		 * La idea original es que el usuario introduzca un número de lingotes. Entonces, dos ladrones alternativamente irían "tirando" un dado de 6 caras.
		 * De esta forma se determinaría la cantidad que irían robando. En el momento en el que no pudieran sacar tantos lingotes porque no queden, finalizarían.
		 * He adaptado este planteamiento para que funcione con un número indeterminado de hilos, intentando que aún así vayan actuando en orden,
		 * aunque en determinados casos puede darse que un mismo ladrón repita varias veces seguidas (hasta finalizar completamente su actividad), en caso de que los siguientes hayan sido atrapados o hayan huido.
		 * Los ladrones terminan en 3 casos:
		 * 	1 - Que intenten robar 0 lingotes, supuesto en el que se considera que son atrapados.
		 * 	2 - Que intenten robar más lingotes de los que quedan en la caja, supuesto en el que se dan por satisfechos y huyen.
		 *  3 - Que ya no queden lingotes en el banco, momento en el que huyen (aún podrían verse atrapados si sacan un 0 en su "tirada" de robo antes de comprobar que no quedan lingotes).
		 *  Al final del ejercicio, se hace un balance del robo, indicando cuántos ladrones han sido atrapados, cuánto se ha robado (y por quién) y cuánto queda en el Banco. 
		 */
		Scanner sc = new Scanner (System.in);
		//Estructura para que el usuario introduzca el número de lingotes. Se controla que sea positivo y entero. 
		int lingotes = -1;
		int x;
		while (lingotes == -1) {
			try {
				System.out.println("Introduce el número de lingotes que habrá en el banco. Debe ser un número entero positivo");
				x = sc.nextInt();
				if (x > 0) {
					lingotes = x;
				}else {
					System.out.println("El número debe ser positivo");
				}
			}catch(Exception e) {
				System.err.println("NO se ha introducido un ENTERO");
				sc.nextLine();
			}	
		}
		//Estructura para que el usuario introduzca el número de ladrones. Se controla que sea un número positivo y entero.
		int numLadrones = - 1;
		int y;
		while (numLadrones == -1) {
			try {
				System.out.println("Introduce el número de ladrones que asaltarán el banco. Debe ser un número entero y positivo");
				y = sc.nextInt();
				if (y > 0) {
					numLadrones = y;
				}else {
					System.out.println("El número debe ser positivo");
				}
			}catch(Exception e) {
				System.err.println("NO se ha introducido un ENTERO");
				sc.nextLine();
			}	
		}
		sc.close();  //cierre del Scanner, ya que no se va a emplear más.
		
		//Objeto Banco, sobre el que se sincronizará la actuación de los ladrones.
		Banco banco = new Banco (lingotes, numLadrones);
		
		//Se lanzan tantos hilos como ladrones
		Thread [] th = new Thread [numLadrones];
		for (int i = 0; i < th.length; i++) {
			th[i] = new Thread (new HiloLadron(i+1, banco));  //cada uno recibe un número identificativo y la misma instancia de banco.
			th[i].start();
		}
		//Aquí se espera a que todos los hilos finalicen su tarea.
		for (int i = 0; i < th.length; i++) {
			try {
				th[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Y tras ello se obtiene la información sobre el asalto.
		System.out.println("--------------------------------------------------");
		System.out.println("BALANCE FINAL");
		int atrapados = banco.getLadronesAtrapados();
		//Si todos los ladrones son atrapados se considera que el robo es un fracaso y no se detalla nada más.
		if (atrapados == numLadrones) { 
			System.out.println("Todos los ladrones fueron atrapados. El robo fracasó y se recuperaron todos los lingotes");
		}else {  //en caso contrario:
			if (atrapados == 0) {  //Se indica si no se atrapó a ningún ladrón
			System.out.println("Ningún ladrón fue atrapado por los guardias");
			}else   { //o a uno o más.
				if (atrapados == 1) {
					System.out.println("Se atrapó "+atrapados+" ladrón");
				}else {
					System.out.println("Se atraparon "+atrapados+" ladrones");
				}
			}
			System.out.println("El botín se ha repartido de la siguiente manera:\n*Se considera que los lingotes que hayan sido robados por ladrones posteriormente atrapados se pierden (ni los recupera el banco ni se los queda el ladrón)");
			int [] roboTotal = banco.getRobaCadaLadron(); //con el getter de la Clase Banco se obtiene la información de lo que han ido robando.
			int totalRobado = 0;							// aquí para acumular el total.
			for (int i = 0; i < roboTotal.length; i++) {  //se va recorriendo el array con lo robado por cada uno y se muestra.
				if (roboTotal[i] != 0) {
					System.out.println("\tEl ladrón "+(i+1)+" ha robado "+roboTotal[i]+" lingotes");
					totalRobado += roboTotal[i];
				}else {
					System.out.println("\tEl ladrón "+(i+1)+" fue atrapado y perdió el botín");
				}
			}
			if (totalRobado != 0) {   //Y a modo de conclusión se detalla lo que quedó en el banco, el total robado y lo que se perdió (ni robado ni recuperado).
				System.out.println("Tras el asalto, en el banco  de los "+lingotes+" lingotes originales ha(n) quedado "+banco.getLingotes()+", habiéndose robado efectivamente "+totalRobado+" y perdiéndose "+banco.getLingotesPerdidos());
			}
		}
	}//fin del main
}//fin de la clase