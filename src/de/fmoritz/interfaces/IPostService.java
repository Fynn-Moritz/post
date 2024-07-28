package de.fmoritz.interfaces;

import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public interface IPostService {
	public void verarbeite(Supplier<ExecutorService> executorSupplier);
}
