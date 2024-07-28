package de.fmoritz.services;

import de.fmoritz.Post;
import de.fmoritz.Versandform;
import de.fmoritz.interfaces.IVersandService;

public class VersandService implements IVersandService{

	public void versendePost(Post postObjekt, Versandform versandForm) {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
