package edu.uwaterloo.javadelayedtype;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for class definition
 */
public class ClassDef {
  public String className;
  public Map<String, FieldDef> fieldDefs;

  public ClassDef(String className) {
    this.className = className;
    this.fieldDefs = new HashMap<String, FieldDef>();
  }
}
