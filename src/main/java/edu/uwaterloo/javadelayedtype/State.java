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
                             // or Obj
  public boolean isValid; // indicate whether this state is valid
  public State parent; // the state from which this state is derived from
  public List<State> childStateList; // states derive form this state
  public Stack<Stack<Map<String, Var>>> varTable; // a stack of stack of variable tables
  public Map<String, Obj> objTable; // a table for all objects

  public State() {
    this.accessPoint = null;
    this.assignPoint = null;
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
   * Create an object
   */
  public void createObject(String className) {
    Obj obj = new Obj(Tracker.classDefMap.get(className));
    this.accessPoint = obj;
    this.objTable.put(obj.id, obj);
  }

  /**
   * Create a variable
   */
  public void createVariable(String varName, String varType) {
    Var var = new Var(varName, varType, this.accessPoint);
    this.varTable.peek().peek().put(varName, var);
  }
}
