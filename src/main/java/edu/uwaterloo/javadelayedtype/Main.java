package edu.uwaterloo.javadelayedtype;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    String srcPath = "examples/test1.java";
    Analyzer analyzer = new Analyzer(srcPath);
    analyzer.analyze();
  }
}
