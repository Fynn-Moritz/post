package de.fmoritz;

import java.util.List;
import java.util.Random;

public class ZustaendikeitsService {
	
	private final Random rand;
	public ZustaendikeitsService() {
		rand = new Random();
	}

	private static List<String> nutzerkennungen = List.of("bpaul", "hmetz", "khauser", "fkanz", "bpistus", "sbolt",
			"jelemtar", "ilenno", "thauber", "awasser");

	public String holeNutzerkennungZuAktenzeichen(String aktenzeichen) {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return nutzerkennungen.get(rand.nextInt(0, 10));
	}
}
