package de.fmoritz.services;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import de.fmoritz.Post;
import de.fmoritz.Sachbearbeiter;
import de.fmoritz.Versandform;
import de.fmoritz.interfaces.IPostEingangsService;
import de.fmoritz.interfaces.IPostService;
import de.fmoritz.interfaces.ISachbearbeiterService;
import de.fmoritz.interfaces.IVersandService;
import de.fmoritz.interfaces.IZustaendikeitsService;

public class PostService implements IPostService{

	private final IZustaendikeitsService zustaendikeitsService;
	private final ISachbearbeiterService sachbearbeiterService;
	private final IVersandService versandService;
	private final IPostEingangsService postEingangsService;

	public PostService(IZustaendikeitsService zustaendikeitsService, ISachbearbeiterService sachbearbeiterService,
			IVersandService versandService, IPostEingangsService postEingangsService) {
		this.zustaendikeitsService = zustaendikeitsService;
		this.sachbearbeiterService = sachbearbeiterService;
		this.versandService = versandService;
		this.postEingangsService = postEingangsService;
	}

	public void verarbeite(Supplier<ExecutorService> executorSupplier) {
		// Postobjekte zur Verarbeitung ermitteln
		List<Post> zuVerschickendePost = postEingangsService.holeZuVerschickendePost();
		try (var exec = executorSupplier.get()) {
			// Jedes Postobjekt in der Liste verarbeiten
			zuVerschickendePost.forEach(postObjekt -> exec.submit(() -> verarbeitePost(postObjekt)));
		}
	}

	private void verarbeitePost(Post postObjekt) {
		// Nutzerkennung anhand des Aktenzeichens ermitteln
		final String nutzerkennung = zustaendikeitsService
				.holeNutzerkennungZuAktenzeichen(postObjekt.getAktenzeichen());
		// Anhand der Nutzerkennung weitere Informationen zum Sachbearbeiter ermitteln
		Sachbearbeiter sb = sachbearbeiterService.holeSachbearbeiterDaten(nutzerkennung);
		// Den ermittelten Sachbearbeiter eintragen
		postObjekt.setSb(sb);
		// Wenn der Sachbearbeiter vertreten wird soll zusätzlich der Vertreter
		// ermittelt und eingetragen werden
		if (sb.wirdVertreten()) {
			postObjekt.setVertreter(sachbearbeiterService.ermittleVertreter(sb.vertreter()));
		}
		// Anzahl an Seiten zählen um die Versandform zu ermitteln
		final long anzahlSeiten = postObjekt.getSeiten().stream().count();
		// Post versenden
		versandService.versendePost(postObjekt, Versandform.ermittleVersandform(anzahlSeiten));
	}
}
