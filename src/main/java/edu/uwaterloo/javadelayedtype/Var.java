package edu.uwaterloo.javadelayedtype;

/**
 * Class for a variable
 */
public class Var {
  public String varName;
  public String varType;
  public String objId;

  public Var(String varName, String varType, Obj obj) {
    this.varName = varName;
    this.varType = varType;
    if (obj != null) {
      this.objId = obj.id;
      obj.refCt++;
    }
    else {
      this.objId = "";
    }
    
    // TODO: delete this
    System.out.println("\nCreating variable: " + varName + " / " + varType + " / " + (obj == null ? "null" : obj.id) + "\n");
  }
}