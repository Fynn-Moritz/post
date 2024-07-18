package de.fmoritz;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

public class PostBatch {

	public static void main(String[] args) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		String formattedTime = LocalTime.now().format(formatter);
		System.out.println("Start: " + formattedTime);
		
		for (int i = 0; i < 10; i++) {
			System.out.println("Lauf " + i);
			long startTime = System.currentTimeMillis();
			var postService = new PostService(new ZustaendikeitsService(), new SachbearbeiterService(), new VersandService());

//			postService.verarbeite(() -> Executors.newFixedThreadPool(Integer.parseInt(args[0])));
			postService.verarbeite(() -> Executors.newVirtualThreadPerTaskExecutor());
//			postService.verarbeiteAsynchron(() -> Executors.newFixedThreadPool(Integer.parseInt(args[0])));

			long endTime = System.currentTimeMillis();

			System.out.println("Elapsed time: " + (endTime - startTime) + " milliseconds");
		}
	}
}
