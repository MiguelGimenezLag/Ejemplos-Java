package programacionhilos;

public class RHilo implements Runnable {
	private int posIni, posFin;
	private int [][] matriz;
	private Almacen al;
	
	public RHilo (int ini, int fin, int [][] mat, Almacen a) {
		this.posIni = ini;
		this.posFin = fin;
		this.matriz = mat;
		this.al = a;
				
	}
	/**
	 * El run de cada Hilo. Con el primer for se indican las filas de la matriz, con el segundo, las columnas.
	 * En el segundo se comparan las ternas y se determina cu�l es el color mayor, 
	 * llamando al m�todo contadorColor de Almac�n. Se emplea 1,2,3 como identificador del color.
	 * Por �ltimo, se mandan todos los valores con al m�todo de Almac�n sumaTotal, que recibe tambi�n identificador del color
	 */
	@Override
	public void run() {	
		for (int i = this.posIni; i < this.posFin; i++) { //cada hilo trabaja sobre las filas correspondientes
			//se incrementa la j de 3 en 3 para ir distribuyendo las ternas de colores, siendo j el rojo, j + 1 el verde y j + 2 el azul  
			for (int j = 0; j < matriz[0].length; j += 3) {  
				if ((matriz[i][j] > matriz[i][j+1]) && (matriz[i][j] > matriz[i][j+2])){
					al.contadorColor(1);
				}else if((matriz[i][j+1] > matriz[i][j]) && (matriz[i][j+1] > matriz[i][j+2])){
					al.contadorColor(2); 
				}else if((matriz[i][j+2] > matriz[i][j]) && (matriz[i][j+2] > matriz[i][j+1])){
					al.contadorColor(3);
				}
				al.sumaTotal(1, matriz[i][j]);
				al.sumaTotal(2, matriz[i][j+1]);
				al.sumaTotal(3, matriz[i][j+2]);
			}
		}
	}
}