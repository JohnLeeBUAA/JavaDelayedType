package edu.uwaterloo.javadelayedtype;

import java.io.IOException;

/**
 * Entrance class
 */
public class Main {
  public static void main(String[] args) throws IOException {
    String srcPath = "examples/simple_example_bad.java";
    // String srcPath = "examples/simple_example_good.java";
    // String srcPath = "examples/detect_null.java";
    // String srcPath = "examples/multi_classes_bad_1.java";
    // String srcPath = "examples/multi_classes_bad_2.java";
    // String srcPath = "examples/multi_classes_good.java";
    // String srcPath = "examples/multi_states_uncertain.java";
    // String srcPath = "examples/multi_states_certain_bad.java";
    // String srcPath = "examples/multi_states_certain_good.java";
    // String srcPath = "examples/method_call_bad.java";
    // String srcPath = "examples/method_call_good.java";

    Reporter.initialize(srcPath);
    Analyzer analyzer = new Analyzer(srcPath);
    analyzer.analyze();
  }
}
