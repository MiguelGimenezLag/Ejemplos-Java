package ejercicioladrones;

import java.util.Random;

public class HiloLadron implements Runnable {
	//variables de la clase
	private int id;
	private Banco banco;
	//Constructor, recibe la id del ladr�n y la misma instancia de banco para todos los Hilos/Ladrones.
	public HiloLadron (int id, Banco b) {
		this.id = id;
		this.banco = b;
	}
	
	/**
	 * El run de cada Ladr�n. 
	 * El array comprobandoTerminados se utilizar� para ir comprobando qu� ladrones han finalizado (ya sea porque han sido atrapados o porque
	 * no queda m�s que robar). En el momento en que un ladr�n finaliza, se hace un break al while terminando la ejecuci�n de dicho Hilo/Ladr�n.
	 */
	@Override
	public void run() {
		Random r = new Random();
		boolean [] comprobandoTerminados;  
		while(true) {
			this.banco.robaLingotes(this.id, r.nextInt(7)); 
			//Estructura para frenar la ejecuci�n de cada hilo. Sin ella, algunas de las salidas de consola pueden mostrarse fuera de posici�n.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Y a continuaci�n la comprobaci�n para saber si se debe finalizar la actividad del Hilo correspondiente
			comprobandoTerminados = banco.getLadronesTerminados();
			if (comprobandoTerminados[this.id - 1]) {  //id-1 ya que los �ndices empiezan en 0. El ladr�n 1 est� en �ndice 0, 2 en 1 etc.
				break;
			}
		}		
	}
}
