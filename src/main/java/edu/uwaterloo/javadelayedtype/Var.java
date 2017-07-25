package edu.uwaterloo.javadelayedtype;

/**
 * Class for a variable
 */
public class Var {
  public String varName;
  public String varType;
  public String objId;

  public Var() {}

  public Var(String varName, String varType, Obj obj) {
    this.varName = varName;
    this.varType = varType;
    if (obj != null) {
      this.objId = obj.id;
      obj.refCt++;
    } else {
      this.objId = "";
    }
  }

  public Var cloneVar() {
    Var newVar = new Var();
    newVar.varName = this.varName;
    newVar.varType = this.varType;
    newVar.objId = this.objId;
    return newVar;
  }
}
