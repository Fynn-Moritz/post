package de.fmoritz.interfaces;

import de.fmoritz.Sachbearbeiter;

public interface ISachbearbeiterService {

	public Sachbearbeiter holeSachbearbeiterDaten(String nutzerkennung);
	public Sachbearbeiter ermittleVertreter(String nutzerkennung);
}
