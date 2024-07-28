package de.fmoritz;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

import de.fmoritz.interfaces.IPostService;
import de.fmoritz.services.PostEingangsService;
import de.fmoritz.services.PostServiceAsynchron;
import de.fmoritz.services.SachbearbeiterService;
import de.fmoritz.services.VersandService;
import de.fmoritz.services.ZustaendikeitsService;

public class PostBatch {

	public static void main(String[] args) {
		final int MAX_THREAD_ANZAHL = 10000;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		String formattedTime = LocalTime.now().format(formatter);
		System.out.println("Start: " + formattedTime);
		
//		IPostService postService = new PostService(new ZustaendikeitsService(), new SachbearbeiterService(), new VersandService(), new PostEingangsService());
		IPostService postService = new PostServiceAsynchron(new ZustaendikeitsService(), new SachbearbeiterService(), new VersandService(), new PostEingangsService());
		
		for (int i = 0; i < 10; i++) { 
			System.out.println("Lauf " + i);
			long startTime = System.currentTimeMillis();

			postService.verarbeite(() -> Executors.newVirtualThreadPerTaskExecutor());
			postService.verarbeite(() -> Executors.newFixedThreadPool(MAX_THREAD_ANZAHL));
			postService.verarbeite(() -> Executors.newFixedThreadPool(Integer.parseInt(args[0])));

			long endTime = System.currentTimeMillis();

			System.out.println("Elapsed time: " + (endTime - startTime) + " milliseconds");
		}
	}
}
