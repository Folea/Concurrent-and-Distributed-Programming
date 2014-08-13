package guardia_peatones_coches;

import java.util.Random;

public enum Via {
	A, B;

public static Via viaAleatoria(){
	Via[] vias = Via.values();
	Random generator = new Random();
	return vias[generator.nextInt(vias.length)];
	}
}
