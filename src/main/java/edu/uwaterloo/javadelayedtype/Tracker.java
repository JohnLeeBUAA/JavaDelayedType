package edu.uwaterloo.javadelayedtype;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * This class stores class and field definitions, and all possible states of the program contains
 * the core logic to analyze delayed type and provide API for parser
 */
public class Tracker {
  public static boolean assignTargetMode;
  public static Map<String, ClassDef> classDefMap; // a map of class and field definitions
  public int indentLevel; // indent level for node printing, for debugging purpose
  private State state; // the current possible state, all possible states stored in a tree structure

  public Tracker() throws IOException {
    Tracker.assignTargetMode = false;
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
   * Print function for debugging TODO: delete this
   */
  public void printClassAndFieldDefs() {
    for (Entry<String, ClassDef> entry : Tracker.classDefMap.entrySet()) {
      System.out.println(entry.getKey() + "/" + entry.getValue().className);
      for (Entry<String, FieldDef> field : entry.getValue().fieldDefs.entrySet()) {
        System.out.println(field.getKey() + "/" + field.getValue().fieldName + "/"
            + field.getValue().fieldType + "/" + field.getValue().isDelayedType);
      }
      System.out.println("\n");
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
}
