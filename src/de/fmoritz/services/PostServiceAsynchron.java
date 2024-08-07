package de.fmoritz.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
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

public class PostServiceAsynchron implements IPostService {

	private final IZustaendikeitsService zustaendikeitsService;
	private final ISachbearbeiterService sachbearbeiterService;
	private final IVersandService versandService;
	private final IPostEingangsService postEingangsService;

	public PostServiceAsynchron(IZustaendikeitsService zustaendikeitsService,
			ISachbearbeiterService sachbearbeiterService, IVersandService versandService,
			IPostEingangsService postEingangsService) {
		this.zustaendikeitsService = zustaendikeitsService;
		this.sachbearbeiterService = sachbearbeiterService;
		this.versandService = versandService;
		this.postEingangsService = postEingangsService;
	}

	public void verarbeite(Supplier<ExecutorService> executorSupplier) {
		// Postobjekte zur Verarbeitung ermitteln
		List<Post> zuVerschickendePost = postEingangsService.holeZuVerschickendePost();
		try (ExecutorService exec = executorSupplier.get()) {
			// Jedes Postobjekt in der Liste verarbeiten
			CompletableFuture.allOf(zuVerschickendePost.stream().map(postObjekt -> verarbeitePost(postObjekt, exec))
					.toArray(CompletableFuture[]::new)).join();
		}
	}

	private CompletableFuture<Void> verarbeitePost(Post postObjekt, ExecutorService executor) {
		return CompletableFuture
				// Nutzerkennung anhand des Aktenzeichens ermitteln
				.supplyAsync(() -> zustaendikeitsService.holeNutzerkennungZuAktenzeichen(postObjekt.getAktenzeichen()),
						executor)
				// Anhand der Nutzerkennung weitere Informationen zum Sachbearbeiter ermitteln
				.thenComposeAsync((String nutzerkennung) -> CompletableFuture
						.supplyAsync(() -> sachbearbeiterService.holeSachbearbeiterDaten(nutzerkennung), executor)
						.thenApplyAsync((Sachbearbeiter sb) -> {
							// Den ermittelten Sachbearbeiter eintragen
							postObjekt.setSb(sb);
							// Wenn der Sachbearbeiter vertreten wird soll zusätzlich der Vertreter
							// ermittelt und eingetragen werden
							if (sb.wirdVertreten()) {
								postObjekt.setVertreter(sachbearbeiterService.ermittleVertreter(sb.vertreter()));
							}
							return postObjekt;
						}, executor), executor)
				.thenAcceptAsync((Post post) -> {
					// Anzahl an Seiten zählen um die Versandform zu ermitteln
					final long anzahlSeiten = post.getSeiten().stream().count();
					// Post versenden
					versandService.versendePost(post, Versandform.ermittleVersandform(anzahlSeiten));
				}, executor).exceptionally((Throwable ex) -> {
					ex.printStackTrace();
					return null;
				});
	}
}
