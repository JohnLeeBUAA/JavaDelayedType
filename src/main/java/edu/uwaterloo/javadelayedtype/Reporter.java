package edu.uwaterloo.javadelayedtype;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * This class reports errors or warnings detected by Tracker
 */
public class Reporter {
  public static final int LEVEL_ERROR = 0;
  public static final int LEVEL_WARNING = 1;

  public static List<String> src;
  public static int level;
  public static String message;
  public static int lineNo;
  public static int colNo;
  public static String subject;

  public static void initialize(String srcPath) throws IOException {
    src = new ArrayList<String>();

    BufferedReader br = new BufferedReader(new FileReader(srcPath));
    try {
      String line;
      while ((line = br.readLine()) != null) {
        src.add(line);
      }
    } finally {
      br.close();
    }
  }

  public static void report() {
    lineNo--;
    colNo--;
    for (int i = 0; i < src.size(); i++) {
      System.out.println(src.get(i));
      if (lineNo == i) {
        System.out.println(StringUtils.repeat(" ", colNo) + "^");
        System.out.println(StringUtils.repeat(" ", colNo) + (level == 0 ? "ERROR: " : "WARNING: ")
            + subject + " " + message + ".\n");
      }
    }
    System.exit(1);
  }

  // TODO: delete this
  public static void main(String[] args) throws IOException {
    Reporter.initialize("examples/test1.java");
    Reporter.level = Reporter.LEVEL_ERROR;
    Reporter.message = "Test Message";
    Reporter.lineNo = 2;
    Reporter.colNo = 3;
    Reporter.subject = "n1";
    Reporter.report();
  }
}
