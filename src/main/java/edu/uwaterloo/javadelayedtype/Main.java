package edu.uwaterloo.javadelayedtype;

import java.io.IOException;

/**
 * Entrance class
 */
public class Main {
  public static void main(String[] args) throws IOException {
    String srcPath = "examples/test1.java";
    Reporter.initialize(srcPath);
    Analyzer analyzer = new Analyzer(srcPath);
    analyzer.analyze();
  }
}
