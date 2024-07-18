package de.fmoritz;

public class VersandService {

	public void versendePost(Post postObjekt, Versandform versandForm) {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
