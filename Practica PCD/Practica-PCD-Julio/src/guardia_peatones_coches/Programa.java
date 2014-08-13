package guardia_peatones_coches;

import messagepassing.Channel;

public class Programa {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Channel[] canalesPeatones = new Channel[11];
		Peaton[] peatones = new Peaton[11];
		Thread[] threadsPeatones = new Thread[11]; 
		Thread[] threadCoches = new Thread[10];
		
		//Inicializar los canales 
		for(int i = 0; i < canalesPeatones.length; i++){
			canalesPeatones[i] = new Channel();
		}
		
		//Consideramos 3 peatones para el marcaje 1, 2 para el marcaje 2, 4 para el marcaje 3
		// y 2 para el marcaje 4 y los inicializamos;
		for(int i = 0; i < peatones.length; i++){
			if(i < 3){ 
				peatones[i] = new Peaton(canalesPeatones[i], 1);
			}
			else if(i < 5){
				peatones[i] = new Peaton(canalesPeatones[i], 2);
			}
			else if(i < 9){
				peatones[i] = new Peaton(canalesPeatones[i], 3);
			}
			else{
				peatones[i] = new Peaton(canalesPeatones[i], 4);
			}
		}
		
		Guardia guardia = new Guardia(canalesPeatones);
		
		//Creación de threads
		for(int i = 0; i < threadsPeatones.length; i++){
			threadsPeatones[i] = new Thread(peatones[i]);
			threadsPeatones[i].start();
		}
		
		Thread threadGuardia =  new Thread(guardia);
		threadGuardia.start();
		
		for(int i = 0; i < threadCoches.length; i++){
			if(i < 5){
				threadCoches[i] = new Thread(new Coche(guardia, Via.A));
			}
			else{
				threadCoches[i] = new Thread(new Coche(guardia, Via.B));
			}
			threadCoches[i].start();
		}
		
		
		try{
			for(int i = 0; i < threadsPeatones.length; i++){
				threadsPeatones[i].join();
			}
			threadGuardia.join();
			for(int i = 0; i < threadCoches.length; i++){
				threadCoches[i].join();
			}
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	}
}
