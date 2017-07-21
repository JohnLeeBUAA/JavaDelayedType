package edu.uwaterloo.javadelayedtype;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Reporter {
  public static final int LEVEL_ERROR = 0;
  public static final int LEVEL_WARNING = 1;

  private List<String> src;

  public Reporter(String srcPath) throws IOException {
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

  public void report(int lineNo, int colNo, String message, int level) {
    for (int i = 0; i < src.size(); i++) {
      System.out.println(src.get(i));
      if (lineNo == i) {
        System.out.println(StringUtils.repeat(" ", colNo) + "^");
        System.out.println(StringUtils.repeat(" ", colNo) + (level == 0 ? "ERROR: " : "WARNING: ")
            + message + "\n");
      }
    }
  }

  public static void main(String[] args) throws IOException {
    Reporter r = new Reporter("examples/test1.java");
    r.report(2, 4, "testtesttest", Reporter.LEVEL_WARNING);
  }
}
