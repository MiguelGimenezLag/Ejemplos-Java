package ejercicioladrones;

import java.util.Random;

public class HiloLadron implements Runnable {
	//variables de la clase
	private int id;
	private Banco banco;
	//Constructor, recibe la id del ladrón y la misma instancia de banco para todos los Hilos/Ladrones.
	public HiloLadron (int id, Banco b) {
		this.id = id;
		this.banco = b;
	}
	
	/**
	 * El run de cada Ladrón. 
	 * El array comprobandoTerminados se utilizará para ir comprobando qué ladrones han finalizado (ya sea porque han sido atrapados o porque
	 * no queda más que robar). En el momento en que un ladrón finaliza, se hace un break al while terminando la ejecución de dicho Hilo/Ladrón.
	 */
	@Override
	public void run() {
		Random r = new Random();
		boolean [] comprobandoTerminados;  
		while(true) {
			this.banco.robaLingotes(this.id, r.nextInt(7)); 
			//Estructura para frenar la ejecución de cada hilo. Sin ella, algunas de las salidas de consola pueden mostrarse fuera de posición.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Y a continuación la comprobación para saber si se debe finalizar la actividad del Hilo correspondiente
			comprobandoTerminados = banco.getLadronesTerminados();
			if (comprobandoTerminados[this.id - 1]) {  //id-1 ya que los índices empiezan en 0. El ladrón 1 está en índice 0, 2 en 1 etc.
				break;
			}
		}		
	}
}
