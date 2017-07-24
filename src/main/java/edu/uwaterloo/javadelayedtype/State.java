package edu.uwaterloo.javadelayedtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * This class stores a single possible state of a program
 */
public class State {
  public Obj accessPoint; // store the access point, always an object if there's any
  public Object assignPoint; // store the access point for the target in assignment statement, Var
                             // or Ref
  public List<Obj> argumentPoint; // store arguments of a method call
  public boolean isValid; // indicate whether this state is valid
  public State parent; // the state from which this state is derived from
  public List<State> childStateList; // states derive form this state
  public Stack<Stack<Map<String, Var>>> varTable; // a stack of stack of variable tables
  public Map<String, Obj> objTable; // a table for all objects

  public State() {
    this.accessPoint = null;
    this.assignPoint = null;
    this.argumentPoint = new ArrayList<Obj>();
    this.isValid = true;
    this.parent = null;
    this.childStateList = new ArrayList<State>();
    this.varTable = new Stack<Stack<Map<String, Var>>>();
    this.objTable = new HashMap<String, Obj>();
  }

  /**
   * When entering a method, push a new variable stack
   */
  public void methodEntrance() {
    Stack<Map<String, Var>> tempVarStack = new Stack<>();
    this.varTable.push(tempVarStack);
  }

  /**
   * When exiting a method, pop the current variable stack
   */
  public void methodExit() {
    Stack<Map<String, Var>> tempVarStack = this.varTable.pop();
    // TODO: delete this
    if (tempVarStack.size() != 0) {
      System.out.println("variable table count is not zero when exiting a method");
    }
  }

  /**
   * When entering a block, push a new variable table
   */
  public void blockEntrance() {
    Map<String, Var> tempVarTable = new HashMap<>();
    this.varTable.peek().push(tempVarTable);
  }

  /**
   * When exiting a block, pop the current variable table
   */
  public void blockExit() {
    Map<String, Var> tempVarTable = this.varTable.peek().pop();
    // TODO: delete all variables in tempVarTable
  }

  /**
   * Clear the access point for analyzing a new AST node
   */
  public void clearAccessPoint() {
    this.accessPoint = null;
  }

  /**
   * Clear the assign point
   */
  public void clearAssignPoint() {
    this.assignPoint = null;
  }

  /**
   * Clear the argument point
   */
  public void clearArgumentPoint() {
    this.argumentPoint.clear();
  }

  /**
   * Create an object
   * 
   * @param className
   */
  public void createObject(String className) {
    Obj obj = new Obj(Tracker.classDefMap.get(className));
    this.accessPoint = obj;
    this.objTable.put(obj.id, obj);
  }

  /**
   * Create a variable
   * 
   * @param varName
   * @param varType
   */
  public void createVariable(String varName, String varType) {
    Var var = new Var(varName, varType, this.accessPoint);
    this.varTable.peek().peek().put(varName, var);
  }

  /**
   * Access a variable
   * 
   * @param varName
   */
  public void accessVariable(String varName) {
    Var var = this.varTable.peek().peek().get(varName);
    switch (Tracker.mode) {
      case 0: {
        String objId = var.objId;
        if (objId.equals("")) {
          this.accessPoint = null;
        } else {
          this.accessPoint = this.objTable.get(objId);
        }
      }
        break;
      case 1: {
        this.assignPoint = var;
      }
        break;
      case 2: {
        String objId = var.objId;
        if (objId.equals("")) {
          this.argumentPoint.add(null);
        } else {
          this.argumentPoint.add(this.objTable.get(objId));
        }
      }
    }
  }

  /**
   * Access field on current access point
   * 
   * @param fieldName
   */
  public void accessField(String fieldName) {
    Reporter.null_case_total++;
    switch (Tracker.mode) {
      case 0: {
        if (this.accessPoint == null) {
          Reporter.null_case_error++;
        } else {
          Ref ref = this.accessPoint.fields.get(fieldName);
          if (ref.isDelayedType) {
            Reporter.delayed_case_total++;
            if (!ref.isCompleted) {
              Reporter.delayed_case_error++;
              break;
            }
          }
          String objId = ref.objId;
          if (objId.equals("")) {
            this.accessPoint = null;
          } else {
            this.accessPoint = this.objTable.get(objId);
          }
        }
      }
        break;
      case 1: {
        String objId = "";
        if (this.assignPoint instanceof Var) {
          objId = ((Var) assignPoint).objId;
        } else if (this.assignPoint instanceof Ref) {
          objId = ((Ref) assignPoint).objId;
        }
        Obj obj = this.objTable.get(objId);
        if (obj == null) {
          Reporter.null_case_error++;
        } else {
          this.assignPoint = obj.fields.get(fieldName);
        }
      }
        break;
      case 2: {
        Obj obj = this.argumentPoint.get(this.argumentPoint.size() - 1);
        if (obj == null) {
          Reporter.null_case_error++;
        } else {
          Ref ref = obj.fields.get(fieldName);
          if (ref.isDelayedType) {
            Reporter.delayed_case_total++;
            if (!ref.isCompleted) {
              Reporter.delayed_case_error++;
              break;
            }
          }
          String objId = ref.objId;
          if (objId.equals("")) {
            this.argumentPoint.set(this.argumentPoint.size() - 1, null);
          } else {
            this.argumentPoint.set(this.argumentPoint.size() - 1, this.objTable.get(objId));
          }
        }
      }
    }
  }

  /**
   * Access null literal
   */
  public void accessNull() {
    switch (Tracker.mode) {
      case 0: {
        this.accessPoint = null;
      }
        break;
      case 1:
        // should never be here
        break;
      case 2: {
        this.argumentPoint.add(null);
      }
    }
  }
}
