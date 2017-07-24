package edu.uwaterloo.javadelayedtype;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * This class reports errors or warnings detected by Tracker. Reports two kind of errors: accessing
 * field or call method on a null object, or accessing a delayed field which is not completed. Count
 * the error and total number of cases for all possible states, report error or warning accordingly.
 */
public class Reporter {
  public static List<String> src;
  public static int lineNo;
  public static int colNo;
  public static int null_case_error;
  public static int null_case_total;
  public static int delayed_case_error;
  public static int delayed_case_total;

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
  
  public static void reset() {
    Reporter.null_case_error = 0;
    Reporter.null_case_total = 0;
    Reporter.delayed_case_error = 0;
    Reporter.delayed_case_total = 0;
  }

  public static void report() {
    StringBuilder message = new StringBuilder();
    if (Reporter.null_case_error != 0) {
      if (Reporter.null_case_error == Reporter.null_case_total) {
        message.append("Error: ");
      } else {
        message.append("Warning: maybe ");
      }
      message.append("accessing on a null object. ");
    }
    if (Reporter.delayed_case_error != 0) {
      if (Reporter.delayed_case_error == Reporter.delayed_case_total) {
        message.append("Error: ");
      } else {
        message.append("Warning: maybe ");
      }
      message.append("accessing on a delayed field that is not completed. ");
    }

    if (!message.toString().equals("")) {
      lineNo--;
      colNo--;
      System.out.println("===========================================");
      for (int i = 0; i < src.size(); i++) {
        System.out.println(src.get(i));
        if (lineNo == i) {
          System.out.println(StringUtils.repeat(" ", colNo) + "^");
          System.out.println(StringUtils.repeat(" ", colNo) + message.toString() + "\n");
        }
      }
      System.out.println("===========================================\n");
    }
  }
}
