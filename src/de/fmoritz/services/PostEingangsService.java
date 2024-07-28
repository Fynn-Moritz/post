package de.fmoritz.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.fmoritz.Post;
import de.fmoritz.interfaces.IPostEingangsService;

public class PostEingangsService implements IPostEingangsService {

	public List<Post> holeZuVerschickendePost() {
		return IntStream.range(0, 1_000_000).mapToObj(val -> new Post("Posttext zu Postobjekt: " + val))
				.collect(Collectors.toList());
	}
}
