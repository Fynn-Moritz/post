package de.fmoritz;

import java.util.HashMap;
import java.util.Map;

public class SachbearbeiterService {

	static Map<String, Sachbearbeiter> as;
	static {
		as = new HashMap<String, Sachbearbeiter>(10);
		as.put("bpaul", new Sachbearbeiter("bpaul", "Paul", "Brunos", "1548", false, null));
		as.put("hmetz", new Sachbearbeiter("hmetz", "Henio", "Metz", "1468", false, null));
		as.put("khauser", new Sachbearbeiter("khauser", "Kirsten", "Hauser", "1237", false, null));
		as.put("fkanz", new Sachbearbeiter("fkanz", "Frank", "Kanz", "1173", true, "khauser"));
		as.put("bpistus", new Sachbearbeiter("bpistus", "Boris", "Pistus", "1496", false, null));
		as.put("sbolt", new Sachbearbeiter("sbolt", "Sascha", "Bolt", "1845", false, null));
		as.put("jelemtar", new Sachbearbeiter("jelemtar", "Josy", "Elemtar", "1384", false, null));
		as.put("ilenno", new Sachbearbeiter("ilenno", "Ingo", "Lenno", "1898", true, "sbolt"));
		as.put("thauber", new Sachbearbeiter("thauber", "Thomas", "Hauber", "1733", false, null));
		as.put("awasser", new Sachbearbeiter("awasser", "Anne", "Wasser", "1951", false, null));
	}

	public Sachbearbeiter holeSachbearbeiterDaten(String nutzerkennung) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return as.get(nutzerkennung);
	}

	public Sachbearbeiter ermittleVertreter(String nutzerkennung) {
		String vertreter = as.get(nutzerkennung).vertreter();
		return as.get(vertreter);
	}
}
