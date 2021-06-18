package ejercicioladrones;

public class Banco {
	private int lingotes, numLadrones;
	private int turno, ladronesAtrapados, lingotesPerdidos;
	private boolean [] ladronesTerminados;
	private int [] robaCadaLadron; 
	
	/*
	 * El constructor de banco. Recibe desde el main el número de lingotes y de ladrones.
	 * Además, inicializa la variable turno a 1 para que empiece robando el Ladrón 1
	 * También se lleva la cuenta de los ladrones atrapados (inicialmente 0).
	 * El array booleano ladronesTerminados empieza todo a false y se va poniendo a True según termina cada Ladrón (por distintas causas).
	 * El array robaCadaLadron lleva la cuenta del botín que va acumulando cada ladrón.
	 * Por último, lingotesPerdidos se empleará para almacenar los lingotes que hayan sido robados por ladrones finalmente atrapados.
	 */
	public Banco (int lingo, int numLadr) {
		this.lingotes = lingo;
		this.numLadrones = numLadr;
		this.turno = 1;
		this.ladronesAtrapados = 0;
		this.ladronesTerminados = new boolean [numLadrones];
		this.robaCadaLadron = new int [numLadrones];
		this.lingotesPerdidos = 0;
	}
	
	/**
	 * Aquí se indica la acción de cada ladrón. Desde el run de los hilos recibe dos parámetros
	 * @param idLadron identifica al ladrón
	 * @param robaLingo recibe un número aleatorio de 0 a 6, siendo la cantidad de lingotes que intentará sacar el ladrón.
	 */
	public synchronized void robaLingotes(int idLadron, int robaLingo) {
		//Con este primer while se pone a esperar a los hilos hasta que sea su turno.
		while (turno != idLadron) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Superado el while, el ladrón que tenga el turno comprueba qué ocurre en función de su "tirada" de robo y de los lingotes que quedan.
		
		if(robaLingo == 0){ //A — Si obtuvo un 0, es atrapado.
			System.out.println("El ladrón "+idLadron+" es ATRAPADO");
			this.ladronesAtrapados++;  //se incrementa el contador de ladrones capturados.
			this.turno++;				//se pasa el turno
			this.ladronesTerminados[idLadron - 1] = true;  //se indica que este hilo/ladrón ha terminado.
			this.lingotesPerdidos += this.robaCadaLadron[idLadron - 1];  //se actualiza en valor de lingotesPerdidos, ni robados ni recuperados.
			this.robaCadaLadron[idLadron - 1] = 0;     //Y se pone su botín a 0, en caso de que hubiera llegado a robar antes de ser atrapado.
		
		}else if (robaLingo <= this.lingotes) {   //B- Si intenta robar menos lingotes de los que quedan, lo consigue.
			this.lingotes -= robaLingo;  //se actualiza el total de lingotes.
			System.out.println("El ladrón "+idLadron+" ha robado "+robaLingo+" lingotes. Quedan "+this.lingotes); 
			this.robaCadaLadron[idLadron - 1] += robaLingo;  //se incrementa el botín personal de este ladrón.
			this.turno++;	//y se pasa turno.
		
		}else if (this.lingotes > 0){ //C- Si intenta robar más lingotes de los que quedan, decide huir y finaliza.
			System.out.println("El ladrón "+idLadron+" no puede sacar "+robaLingo+" lingotes y HUYE con su botín antes de que le atrapen");
			this.turno++;  //se pasa turno.
			this.ladronesTerminados[idLadron - 1] = true;  //se indica que este hilo/ladrón ha terminado.
		
		}else { 					//D- Si ya no quedan lingotes. Los ladrones huyen. Alguno podría verse atrapado ya que para llegar a comprobar si quedan lingotes realiza su tirada y podría obtener un 0
			System.out.println("El banco está vacío. El ladrón "+idLadron+" HUYE con su botín");
			this.turno++; //se pasa turno.
			this.ladronesTerminados[idLadron - 1] = true; //se indica que este hilo/ladrón ha terminado.
		}
		
		/*
		 * Así se reinicia el turno cuando ya han robado todos los ladrones. 
		 * Supongamos que hay 5. Cuando el quinto realiza su "tirada" y acción consecuente, se pasa al turno 6, pero ya no hay un 6 ladrón, así que se vuelve al primero.
		 */
		if (this.turno == this.numLadrones + 1) {
			this.turno = 1;
		}
		/*Y con esta estructura se controla que si se llega al turno de un Hilo que ya ha terminado, se cambia al primer hilo que aún no haya finalizado.
		 *El problema es que aquí es cuando se rompe el orden. Si el dos ha finalizado y le llega el turno al 1, de acuerdo a esta estructura irá repitiendo siempre el 1 hasta que finalice.
		 * Solo entonces pasaría al 3, 4... si aún estuvieran activos. He probado distintas soluciones, ninguna plenamente satisfactoria, así que de momento lo dejo así. 
		 * Este fallo se hace evidente cuando se introduce una cantidad de lingotes muy alta, con muchos ladrones.
		 * Si consigo una solución mejor actualizaré el programa.
		 */
		if (this.ladronesTerminados[this.turno-1] == true) { //se comprueba si ya se ha terminado
			for (int i = 0; i < ladronesTerminados.length; i++) {  //y se recorre el array de booleanos buscando el primer hilo que aún puede actuar.
				if (ladronesTerminados[i] == false) {
					this.turno = i + 1;
					break;  //quitando este break se haría que en vez de repetirse el primer Hilo aún activo, se vaya repitiendo el último.
				}
			}
		}
		notifyAll(); //al final del método se despierta a todos los hilos para que comprueben en el bucle while si es su turno. En caso contrario, vuelven a dormir.
	}
	
	//Distintos getter utilizados en el main para mostrar el balance final del robo.
	public int getLingotes() {
		return this.lingotes;
	}
	public int getLingotesPerdidos() {
		return this.lingotesPerdidos;
	}
	public int getLadronesAtrapados() {
		return this.ladronesAtrapados;
	}
	public boolean [] getLadronesTerminados() {
		return this.ladronesTerminados;
	}
	public int [] getRobaCadaLadron() {
		return this.robaCadaLadron;
	}
}
