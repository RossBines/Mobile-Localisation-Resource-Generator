package com.binesy.localisation;

import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Generator generator = new Generator(args);
		generator.generateFiles();
		System.out.println("Generation completed!");
	}
}
