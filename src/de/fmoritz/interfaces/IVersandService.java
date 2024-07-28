package de.fmoritz.interfaces;

import de.fmoritz.Post;
import de.fmoritz.Versandform;

public interface IVersandService {

	public void versendePost(Post postObjekt, Versandform versandForm);

}
