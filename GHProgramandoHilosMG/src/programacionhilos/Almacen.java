package programacionhilos;

public class Almacen {
	private int [] colores; //para almacenar los m�ximos de cada terna, en el �ndice 0 los rojos, en el 1 los verdes, y en el 2 los azules
	private int [] totalColores; //igual, pero almacenando el valor total de cada color
	
	public Almacen() {
		this.colores = new int[3];
		this.totalColores = new int[3];
	}
	/**
	 * Recibe un id que identifica un color que ha sido dominante en su terna.
	 * Se emplea el array colores, increment�ndolo de 1 en 1 seg�n se identifique dicho color dominante
	 * Debe estar sincronizado para evitar errores si dos hilos intentaran simult�neamente incrementar el mismo �ndice
	 * @param id identifica cu�l de los tres colores ha sido dominante en su terna.
	 * @return void
	 */
	public synchronized void contadorColor(int id) {
		if (id == 1) {
			this.colores[0]++;
		}else if (id == 2) {
			this.colores[1]++;
		}else {
			this.colores[2]++;
		}
	}
	/**
	 * Recibe un valor y un identificador que indica a qu� color se corresponde.
	 * Va sumando todos los valores en el �ndice de su color correspondiente en el array totalColores
	 * De nuevo, debe estar sincronizado para evitar errores si dos hilos trabajan sobre el mismo �ndice.
	 * @param id identifica el color
	 * @param num indica el valor de dicho color
	 */
	public synchronized void sumaTotal(int id, int num) {
		if (id == 1) {
			this.totalColores[0] += num;
		}else if (id == 2) {
			this.totalColores[1] += num;
		}else {
			this.totalColores[2] += num;
		}
		
	}
	//getters para recoger los arrays con la cuenta y los valores totales en el main.
	public int [] getContadorColores() {
		return colores;
	}
	public int [] getTotalColores() {
		return totalColores;
	}
}