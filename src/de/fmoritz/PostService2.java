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
	private final PostEingangsService postEingangsService;

	public PostService(ZustaendikeitsService zustaendikeitsService, SachbearbeiterService sachbearbeiterService,
			VersandService versandService, PostEingangsService postEingangsService) {
		this.zustaendikeitsService = zustaendikeitsService;
		this.sachbearbeiterService = sachbearbeiterService;
		this.versandService = versandService;
		this.postEingangsService = postEingangsService;
	}

	public void verarbeiteAsynchron(Supplier<ExecutorService> executorSupplier) {
		List<Post> zuVerschickendePost = postEingangsService.holeZuVerschickendePost();
		ExecutorService exec = executorSupplier.get();

		try {
			List<CompletableFuture<Void>> futures = zuVerschickendePost.stream()
					.map(postObjekt -> verarbeitePostAsynchron(postObjekt, exec)).collect(Collectors.toList());

			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		} finally {
			exec.shutdown();
			try {
				if (!exec.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
					exec.shutdownNow();
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
}
