package de.fmoritz;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PostService {

	private final ZustaendikeitsService zustaendikeitsService;
	private final SachbearbeiterService sachbearbeiterService;
	private final VersandService versandService;

	public PostService(ZustaendikeitsService zustaendikeitsService, SachbearbeiterService sachbearbeiterService,
			VersandService versandService) {
		this.zustaendikeitsService = zustaendikeitsService;
		this.sachbearbeiterService = sachbearbeiterService;
		this.versandService = versandService;
	}

	public void verarbeite(Supplier<ExecutorService> executor) {
		List<Post> zuVerschickendePost = holeZuVerschickendePost();
		try (var exec = executor.get()) {
			zuVerschickendePost.forEach(postObjekt -> exec.submit(() -> verarbeitePost(postObjekt)));
		}
	}

	private void verarbeitePost(Post postObjekt) {
		final String nutzerkennung = zustaendikeitsService
				.holeNutzerkennungZuAktenzeichen(postObjekt.getAktenzeichen());
		Sachbearbeiter sb = sachbearbeiterService.holeSachbearbeiterDaten(nutzerkennung);
		if (sb.wirdVertreten()) {
			postObjekt.setVertreter(sachbearbeiterService.ermittleVertreter(nutzerkennung));
		}
		postObjekt.setSb(sb);
		final long anzahlSeiten = postObjekt.getSeiten().stream().count();
		versandService.versendePost(postObjekt, Versandform.ermittleVersandform(anzahlSeiten));
	}

	public void verarbeiteAsynchron(Supplier<ExecutorService> executorSupplier) {
		List<Post> zuVerschickendePost = holeZuVerschickendePost();
		ExecutorService executor = executorSupplier.get();

		try {
			List<CompletableFuture<Void>> futures = zuVerschickendePost.stream()
					.map(postObjekt -> verarbeitePostAsynchron(postObjekt, executor)).collect(Collectors.toList());

			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		} finally {
			executor.shutdown();
			try {
				if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
					executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}

	private CompletableFuture<Void> verarbeitePostAsynchron(Post postObjekt, ExecutorService executor) {
		return CompletableFuture
				.supplyAsync(() -> zustaendikeitsService.holeNutzerkennungZuAktenzeichen(postObjekt.getAktenzeichen()),
						executor)
				.thenComposeAsync(nutzerkennung -> CompletableFuture
						.supplyAsync(() -> sachbearbeiterService.holeSachbearbeiterDaten(nutzerkennung), executor)
						.thenApplyAsync(sb -> {
							if (sb.wirdVertreten()) {
								postObjekt.setVertreter(sachbearbeiterService.ermittleVertreter(nutzerkennung));
							}
							postObjekt.setSb(sb);
							return postObjekt;
						}, executor), executor)
				.thenAcceptAsync(post -> {
					long anzahlSeiten = post.getSeiten().stream().count();
					versandService.versendePost(post, Versandform.ermittleVersandform(anzahlSeiten));
				}, executor).exceptionally(ex -> {
					ex.printStackTrace();
					return null;
				});
	}

	private List<Post> holeZuVerschickendePost() {
		return IntStream.range(0, 1_000_000).mapToObj(val -> new Post("Posttext zu Postobjekt: " + val))
				.collect(Collectors.toList());
	}
}
