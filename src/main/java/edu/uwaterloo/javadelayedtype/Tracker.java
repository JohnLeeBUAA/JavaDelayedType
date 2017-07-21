package edu.uwaterloo.javadelayedtype;

import java.io.IOException;
import java.util.UUID;

public class Tracker {
  public String temp;
  public String curObj;
  public int indentLevel;

  private Reporter reporter;

  public Tracker(String srcPath) throws IOException {
    this.temp = null;
    this.curObj = null;
    this.indentLevel = 0;
    this.reporter = new Reporter(srcPath);
  }
  
  public void accessObj(String refName) {
    if (refName.equals("n1")) {
      this.curObj = "n1Obj";
    }
  }
  
  public void accessField(String fieldName) {
    if (this.curObj.equals("n1Obj") && fieldName.equals("next")) {
      this.curObj = "n1ObjNext";
    }
    if (this.curObj.equals("n1ObjNext") && fieldName.equals("prev")) {
      this.curObj = "n1ObjNextPrev";
    }
  }
}
