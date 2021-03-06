package edu.uwaterloo.javadelayedtype;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * This class stores class and field definitions, and all possible states of the program contains
 * the core logic to analyze delayed type and provide API for parser
 */
public class Tracker {
  public static boolean checkerOn; // indicating checker turn on, only turn on for user-defined
                                   // objects
  public static int mode; // indicating the mode, 0 - normal, 1 - parsing target in assignment
                          // statement, 2- parsing method arguments
  public static Map<String, ClassDef> classDefMap; // a map of class and field definitions
  public int indentLevel; // indent level for node printing, for debugging purpose
  public State state; // the current possible state, all possible states stored in a tree structure

  public Tracker() throws IOException {
    Tracker.checkerOn = false;
    Tracker.mode = 0;
    Tracker.classDefMap = new HashMap<String, ClassDef>();
    this.indentLevel = 0;
    this.state = new State();
  }

  /**
   * Add a class definition
   * 
   * @param className The name of the class
   */
  public void addClassDef(String className) {
    if (!Tracker.classDefMap.containsKey(className)) {
      Tracker.classDefMap.put(className, new ClassDef(className));
    }
  }

  /**
   * Add a field definition, only care about user-defined types
   * 
   * @param className The name of the class
   * @param fieldName The name of the field
   * @param fieldType The type of the field
   */
  public void addFieldDef(String className, String fieldName, String fieldType) {
    if (Tracker.classDefMap.containsKey(fieldType)
        && !Tracker.classDefMap.get(className).fieldDefs.containsKey(fieldName)) {
      boolean isDelayedType = fieldName.startsWith("delayed_");
      Tracker.classDefMap.get(className).fieldDefs.put(fieldName,
          new FieldDef(fieldName, fieldType, isDelayedType));
    }
  }

  /**
   * Entering a method on current state and all child states
   */
  public void methodEntrance() {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.methodEntrance();
      }
    }
  }

  /**
   * Exiting a method on current state and all child states
   */
  public void methodExit() {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.methodExit();
      }
    }
  }

  /**
   * Add arguments to varTable
   * 
   * @param argNames
   * @param argTypes
   */
  public void methodAddArguments(List<String> argNames, List<String> argTypes) {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.methodAddArguments(argNames, argTypes);
      }
    }
  }

  /**
   * Entering a block statement on current state and all child states
   */
  public void blockEntrance() {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.blockEntrance();
      }
    }
  }

  /**
   * Exiting a block statement on current state and all child states
   */
  public void blockExit() {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.blockExit();
      }
    }
  }

  /**
   * Clear access point of current state and all child states for analyzing a new AST node
   */
  public void clearAccessPoint() {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.clearAccessPoint();
      }
    }
  }

  /**
   * Clear assign point of current state and all child states
   */
  public void clearAssignPoint() {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.clearAssignPoint();
      }
    }
  }

  /**
   * Clear the argument point
   */
  public void clearArgumentPoint() {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.clearArgumentPoint();
      }
    }
  }

  /**
   * Create an object
   */
  public void createObject(String className) {
    if (Tracker.classDefMap.containsKey(className)) {
      Queue<State> stateQueue = new LinkedList<>();
      stateQueue.add(this.state);
      while (!stateQueue.isEmpty()) {
        State tempState = stateQueue.poll();
        for (State state : tempState.childStateList) {
          stateQueue.add(state);
        }
        if (tempState.isValid) {
          tempState.createObject(className);
        }
      }
    }
  }

  /**
   * Create a variable
   */
  public void createVariable(String varName, String varType) {
    if (Tracker.classDefMap.containsKey(varType)) {
      Queue<State> stateQueue = new LinkedList<>();
      stateQueue.add(this.state);
      while (!stateQueue.isEmpty()) {
        State tempState = stateQueue.poll();
        for (State state : tempState.childStateList) {
          stateQueue.add(state);
        }
        if (tempState.isValid) {
          tempState.createVariable(varName, varType);
        }
      }
    }
  }

  /**
   * Access a variable
   * 
   * @param varName
   */
  public void accessVariable(String varName) {
    if (this.state.loopUpVariable(varName) != null) {
      Tracker.checkerOn = true;

      Queue<State> stateQueue = new LinkedList<>();
      stateQueue.add(this.state);
      while (!stateQueue.isEmpty()) {
        State tempState = stateQueue.poll();
        for (State state : tempState.childStateList) {
          stateQueue.add(state);
        }
        if (tempState.isValid) {
          tempState.accessVariable(varName);
        }
      }
    } else {
      Tracker.checkerOn = false;
    }
  }

  /**
   * Access field on current access point
   * 
   * @param fieldName
   */
  public void accessField(String fieldName) {
    if (Tracker.checkerOn) {
      Reporter.reset();
      Queue<State> stateQueue = new LinkedList<>();
      stateQueue.add(this.state);
      while (!stateQueue.isEmpty()) {
        State tempState = stateQueue.poll();
        for (State state : tempState.childStateList) {
          stateQueue.add(state);
        }
        if (tempState.isValid) {
          tempState.accessField(fieldName);
        }
      }
      Reporter.report();
    }
  }

  /**
   * Access null literal
   */
  public void accessNull() {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.accessNull();
      }
    }
  }

  /**
   * Assign Obj to Var or Ref
   */
  public void assign() {
    Queue<State> stateQueue = new LinkedList<>();
    stateQueue.add(this.state);
    while (!stateQueue.isEmpty()) {
      State tempState = stateQueue.poll();
      for (State state : tempState.childStateList) {
        stateQueue.add(state);
      }
      if (tempState.isValid) {
        tempState.assign();
      }
    }
  }

  /**
   * Create a new state from current state and go to new state
   */
  public void newStateEntrance() {
    State newState = this.state.cloneState();
    newState.parent = this.state;
    this.state.childStateList.add(newState);
    this.state = newState;
  }

  /**
   * Create a new state from current state and go to new state. Created all possible states from
   * current state, mark current state as invalid
   */
  public void newStateEntranceCloseParentState() {
    State newState = this.state.cloneState();
    newState.parent = this.state;
    this.state.childStateList.add(newState);
    this.state.isValid = false;
    this.state = newState;
  }

  /**
   * Exit a state and go back to parent state
   */
  public void newStateExit() {
    this.state = this.state.parent;
  }


}
