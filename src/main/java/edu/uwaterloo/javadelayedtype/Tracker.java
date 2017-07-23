package edu.uwaterloo.javadelayedtype;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class Tracker {
  public int indentLevel; // indent level for node printing, for debugging purpose
  public Reporter reporter; // reporter for error or warning
  public String temp; // store the temporary result for ASTVisitor

  private Map<String, ClassDef> classDefMap; // a map of class and field definitions
  private List<Map<String, Var>> varTable; // a stack of variable tables for method call
  private List<Map<String, String>> aliasTable; // a stack of alias tables for method call
  private Map<String, Obj> objTable; // a table for all objects

  public Tracker(String srcPath) throws IOException {
    this.indentLevel = 0;
    this.reporter = new Reporter(srcPath);
    this.temp = "";

    this.classDefMap = new HashMap<String, ClassDef>();
    this.varTable = new ArrayList<Map<String, Var>>();
    this.aliasTable = new ArrayList<Map<String, String>>();
    this.objTable = new HashMap<String, Obj>();
  }

  /**
   * Add a class definition
   * 
   * @param className The name of the class
   * @return
   */
  public boolean addClassDef(String className) {
    if (this.classDefMap.containsKey(className)) {
      this.reporter.setLevel(Reporter.LEVEL_ERROR);
      this.reporter.setMessage("has multiple definitions");
      return false;
    }
    this.classDefMap.put(className, new ClassDef(className));
    return true;
  }

  /**
   * Add a field definition, only care about user-defined types
   * 
   * @param className The name of the class
   * @param fieldName The name of the field
   * @param fieldType The type of the field
   * @return
   */
  public boolean addFieldDef(String className, String fieldName, String fieldType) {
    if (this.classDefMap.containsKey(fieldType)) {
      if (this.classDefMap.get(className).fieldDefs.containsKey(fieldName)) {
        this.reporter.setLevel(Reporter.LEVEL_ERROR);
        this.reporter.setMessage("has multiple definitions");
        return false;
      }
      boolean isDelayedType = fieldName.startsWith("delayed_");
      this.classDefMap.get(className).fieldDefs.put(fieldName,
          new FieldDef(fieldName, fieldType, isDelayedType));
    }
    return true;
  }

  /**
   * Print function TODO: delete this
   */
  public void printForDebugging() {
    for (Entry<String, ClassDef> entry : this.classDefMap.entrySet()) {
      System.out.println(entry.getKey() + "/" + entry.getValue().className);
      for (Entry<String, FieldDef> field : entry.getValue().fieldDefs.entrySet()) {
        System.out.println(field.getKey() + "/" + field.getValue().fieldName + "/"
            + field.getValue().fieldType + "/" + field.getValue().isDelayedType);
      }
      System.out.println("\n");
    }
  }

  /**
   * When entering a method, push new variable map and alias map into stack
   */
  public void callEntrance() {
    Map<String, Var> varMap = new HashMap<>();
    this.varTable.add(varMap);
    Map<String, String> aliasMap = new HashMap<>();
    this.aliasTable.add(aliasMap);
  }

  /**
   * When exiting a method, pop variable map and alias map from stack, update objects
   */
  public void callExit() {
    // TODO: pop two stacks, remove variables from varTable, update objects
  }

  /**
   * Add a variable to varTable
   * 
   * @param varName
   * @param varType
   * @param refId
   * @return
   */
  public boolean addVar(String varName, String varType, String refId) {
    // TODO: implement this
    return true;
  }

  private class ClassDef {
    public String className;
    public Map<String, FieldDef> fieldDefs;

    public ClassDef(String className) {
      this.className = className;
      this.fieldDefs = new HashMap<String, FieldDef>();
    }
  }

  private class FieldDef {
    public String fieldName;
    public String fieldType;
    public boolean isDelayedType;

    public FieldDef(String fieldName, String fieldType, boolean isDelayedType) {
      this.fieldName = fieldName;
      this.fieldType = fieldType;
      this.isDelayedType = isDelayedType;
    }
  }

  private class Obj {
    public String id;
    public String className;
    public Map<String, Ref> fields;

    public Obj() {
      this.id = UUID.randomUUID().toString();
    }
  }

  private class Var {

  }

  private class Ref {

  }
}
