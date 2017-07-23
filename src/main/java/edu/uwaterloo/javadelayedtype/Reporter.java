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

  private int level;
  private String message;
  private List<String> src;

  public Reporter(String srcPath) throws IOException {
    level = 0;
    message = "";
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

  public void setLevel(int level) {
    this.level = level;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void report(int lineNo, int colNo, String refName) {
    lineNo--;
    colNo--;
    for (int i = 0; i < src.size(); i++) {
      System.out.println(src.get(i));
      if (lineNo == i) {
        System.out.println(StringUtils.repeat(" ", colNo) + "^");
        System.out.println(StringUtils.repeat(" ", colNo)
            + (this.level == 0 ? "ERROR: " : "WARNING: ") + refName + " " + this.message + ".\n");
      }
    }
    System.exit(1);
  }

  // TODO: delete this
  public static void main(String[] args) throws IOException {
    Reporter r = new Reporter("examples/test1.java");
    r.setLevel(Reporter.LEVEL_ERROR);
    r.setMessage("is null");
    r.report(2, 4, "n1.next");
  }
}
