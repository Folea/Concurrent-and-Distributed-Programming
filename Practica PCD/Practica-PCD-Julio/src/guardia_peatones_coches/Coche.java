package guardia_peatones_coches;

import messagepassing.RemoteServerException;
/**
 * Clase que representa a un coche
 * 
 * @author Folea Ilie Cristian
 *
 */
public class Coche implements Runnable {
	private final Guardia guardia;
	private final Via via;
	private static int numeroCoches = 0;
	private int id;
	
	/**
	 * Constructor de la clase coche.
	 * @param guardia Se le indica el guardia(controlador).
	 * @param via Se le indica la via por la que va a pasar.
	 */
	public Coche(Guardia guardia, Via via){
		this.guardia = guardia;
		this.via = via;
		numeroCoches++;
		id = numeroCoches;
	}
	
	/**
	 * Metodo que se utiliza para imprimir por pantalla la petici�n para el pase de un coche por una via. Esta impresi�n se hace en exlcusi�n
	 * mutua utilizando monitores.
	 * @throws InterruptedException En el caso de que no se puede utilizar el monitor devuelve la excepci�n.
	 */
	private void imprimirPeticionEntrada() throws InterruptedException {
		Guardia.monitor.lock();
		try{
			if(Guardia.escribiendoPantalla){
				Guardia.escribirPantalla.await();
			}
				Guardia.escribiendoPantalla = true;
				System.out.println("El coche numero " + id + " esta pidiendo acceso a la v�a " + via);
				Guardia.escribiendoPantalla = false;
				Guardia.escribirPantalla.signal();
			}finally{
				Guardia.monitor.unlock();
			}
	}
	
	/**
	 * Este metodo se utiliza para imprimir por la pantalla que el coche ha recibido acceso para cruzar la via y empieza a cruzarla.
	 * Debe imprimirlo en exclusi�n mutua y lo hace utilizando monitores.
	 * @throws InterruptedException En el caso de que no se puede utilizar el monitor devuelve la excepci�n.
	 */
	private void imprimirAccesoVia() throws InterruptedException {
		Guardia.monitor.lock();
		try{
			if(Guardia.escribiendoPantalla){
				Guardia.escribirPantalla.await();
			}
				Guardia.escribiendoPantalla = true;
				System.out.println("El coche numero " + id + " recibe permiso y accede a la v�a " + via);
				Guardia.escribiendoPantalla = false;
				Guardia.escribirPantalla.signal();
			}finally{
				Guardia.monitor.unlock();
			}
	}
	
	/**
	 * Este metodo se utiliza para imprimir por pantalla que el coche ha cruzado la via. Debe imprimirlo en exclusi�n mutua
	 * y lo hace utilizando monitores.
	 * @throws InterruptedException En el caso de que no se puede utilizar el monitor devuelve la excepci�n.
	 */
	private void imprimirSalidaVia() throws InterruptedException{
		Guardia.monitor.lock();
		try{
			if(Guardia.escribiendoPantalla){
				Guardia.escribirPantalla.await();
			}
				Guardia.escribiendoPantalla = true;
				System.out.println("El coche numero " + id + " ha cruzado la v�a " + via);
				Guardia.escribiendoPantalla = false;
				Guardia.escribirPantalla.signal();
			}finally{
				Guardia.monitor.unlock();
			}
	}
	
	@Override
	public void run(){
		while(true){
			/*Solicita el acceso a la via, esta llamada se hace para que el controlador pueda tener conocimiento 
			 * de que hay un coche que quiere entrar en la v�a y asi controlar el paso de los coches por la otra v�a
			 * para que en un determinado momento los coches de esta v�a reciban permiso para pasar
			 */
			try{
				if(via == Via.A){
					guardia.call("peticionEntrarA", null);
					try{
						imprimirPeticionEntrada();
					} catch (InterruptedException e) {
					}	
				}
				else if(via == Via.B){
					guardia.call("peticionEntrarB", null);
					try{
						imprimirPeticionEntrada();
					} catch (InterruptedException e) {
					}	
				}
			} catch (RemoteServerException e) {
				e.printStackTrace();
			}
			
			// Una vez que la via activa sea la de este coche, el coche debe poder tener acceso a cruzar.
			try{
				if(via == Via.A){
					guardia.call("entrarA", null);
					try{
						imprimirAccesoVia();
					} catch (InterruptedException e){
					}
				}
				else if(via == Via.B){
					guardia.call("entrarB", null);
					try{
						imprimirAccesoVia();
					} catch (InterruptedException e){
					}
				}
			} catch (RemoteServerException e) {
				e.printStackTrace();
			}
			
			// Tiempo de cruce de la v�a
			
			try{
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Indicar que ha salido de la v�a;
			try{
				if(via == Via.A){
					try{
						imprimirSalidaVia();
					} catch (InterruptedException e){
					}
					guardia.call("salirA", null);

				}
				else if(via == Via.B){
					try{
						imprimirSalidaVia();
					} catch (InterruptedException e){
					}
					guardia.call("salirB", null);

				}
			} catch (RemoteServerException e) {
				e.printStackTrace();
			}
		}
	}
}
