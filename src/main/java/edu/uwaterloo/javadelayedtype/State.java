package edu.uwaterloo.javadelayedtype;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

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
  public Deque<Deque<Map<String, Var>>> varTable; // a stack of stack of variable tables
  public Map<String, Obj> objTable; // a table for all objects

  public State() {
    this.accessPoint = null;
    this.assignPoint = null;
    this.argumentPoint = new ArrayList<Obj>();
    this.isValid = true;
    this.parent = null;
    this.childStateList = new ArrayList<State>();
    this.varTable = new ArrayDeque<Deque<Map<String, Var>>>();
    this.objTable = new HashMap<String, Obj>();
  }

  /**
   * When entering a method, push a new variable stack
   */
  public void methodEntrance() {
    Deque<Map<String, Var>> tempVarStack = new ArrayDeque<>();
    this.varTable.push(tempVarStack);
  }

  /**
   * When exiting a method, pop the current variable stack
   */
  public void methodExit() {
    this.varTable.pop();
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
    for (Entry<String, Var> entry : tempVarTable.entrySet()) {
      removeRefCt(entry.getValue().objId);
    }
    update();
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
   * Look up a variable by name in current call stack
   * 
   * @param varName
   * @return
   */
  public Var loopUpVariable(String varName) {
    Deque<Map<String, Var>> varStack = this.varTable.peek();
    Iterator<Map<String, Var>> iterator = varStack.iterator();
    while (iterator.hasNext()) {
      Map<String, Var> map = iterator.next();
      if (map.containsKey(varName)) {
        return map.get(varName);
      }
    }
    return null;
  }

  /**
   * Access a variable
   * 
   * @param varName
   */
  public void accessVariable(String varName) {
    Var var = loopUpVariable(varName);
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

  /**
   * Assign Obj to Var or Ref
   */
  public void assign() {
    if (this.assignPoint != null) {
      String oldObjId = "";
      if (this.assignPoint instanceof Var) {
        Var temp = ((Var) this.assignPoint);
        oldObjId = temp.objId;
        if (this.accessPoint != null) {
          temp.objId = this.accessPoint.id;
          this.accessPoint.refCt++;
        } else {
          temp.objId = "";
        }
      } else if (this.assignPoint instanceof Ref) {
        Ref temp = ((Ref) this.assignPoint);
        oldObjId = temp.objId;
        if (this.accessPoint != null) {
          temp.objId = this.accessPoint.id;
          this.accessPoint.refCt++;
        } else {
          temp.objId = "";
        }
      }
      removeRefCt(oldObjId);
      update();
    }
  }

  /**
   * Remove one reference count from an object, perform garbage collect if necessary
   * 
   * @param objId
   */
  public void removeRefCt(String objId) {
    if (this.objTable.containsKey(objId)) {
      Obj obj = this.objTable.get(objId);
      obj.refCt--;
      if (obj.refCt == 0) {
        this.objTable.remove(objId);
        for (Entry<String, Ref> entry : obj.fields.entrySet()) {
          removeRefCt(entry.getValue().objId);
        }
      }
    }
  }

  /**
   * Update the state of all delayed type after reference change. Edge counting is performed here.
   */
  public void update() {
    // partition the graph by delayed type connectivity
    // resulting in a list of set of objects
    // no delayed type pointers between any two sets
    List<Set<Obj>> partitions = new ArrayList<>();
    for (Entry<String, Obj> objEntry : this.objTable.entrySet()) {
      Obj curObj = objEntry.getValue();
      boolean visited = false;
      for (Set<Obj> partition : partitions) {
        if (partition.contains(curObj)) {
          visited = true;
          break;
        }
      }

      if (!visited) {
        Set<Obj> newPartition = new HashSet<>();
        Queue<Obj> objQueue = new LinkedList<>();
        objQueue.add(curObj);
        while (!objQueue.isEmpty()) {
          Obj temp = objQueue.poll();
          newPartition.add(temp);
          for (Entry<String, Ref> refEntry : temp.fields.entrySet()) {
            Obj toVisit = this.objTable.get(refEntry.getValue().objId);
            if (toVisit == null || newPartition.contains(toVisit)) {
              continue;
            } else {
              boolean inOldPartitions = false;
              for (Set<Obj> partition : partitions) {
                if (partition.contains(toVisit)) {
                  inOldPartitions = true;
                  newPartition.addAll(partition);
                  partitions.remove(partition);
                  break;
                }
              }
              if (!inOldPartitions) {
                objQueue.add(toVisit);
              }
            }
          }
        }
        partitions.add(newPartition);
      }
    }

    // for each partition, perform edge counting
    for (Set<Obj> partition : partitions) {
      Map<String, Set<String>> missingDelayedFields = new HashMap<>();
      Iterator<Obj> iterator = partition.iterator();
      while (iterator.hasNext()) {
        Obj tempObj = iterator.next();
        String fromType = tempObj.type;
        for (Entry<String, Ref> refEntry : tempObj.fields.entrySet()) {
          Ref tempRef = refEntry.getValue();
          if (tempRef.isDelayedType && tempRef.objId.equals("")) {
            String toType = tempRef.type;
            if (!missingDelayedFields.containsKey(fromType)) {
              Set<String> tempSet = new HashSet<>();
              missingDelayedFields.put(fromType, tempSet);
            }
            missingDelayedFields.get(fromType).add(toType);
          }
        }
      }

      // update all delayed type references
      Iterator<Obj> i = partition.iterator();
      while (i.hasNext()) {
        Obj tempObj = i.next();
        String fromType = tempObj.type;
        for (Entry<String, Ref> refEntry : tempObj.fields.entrySet()) {
          Ref tempRef = refEntry.getValue();
          if (tempRef.isDelayedType) {
            String toType = tempRef.type;
            if (missingDelayedFields.containsKey(fromType)
                && missingDelayedFields.get(fromType).contains(toType)) {
              tempRef.isCompleted = false;


            } else {
              tempRef.isCompleted = true;
            }
          }
        }
      }
    }
  }

  /**
   * Derive a state from current state
   */
  public State cloneState() {
    State newState = new State();
    if (this.accessPoint != null) {
      newState.accessPoint = this.accessPoint.cloneObj();
    }
    if (this.assignPoint != null) {
      if (this.assignPoint instanceof Var) {
        newState.assignPoint = ((Var) this.assignPoint).cloneVar();
      } else if (this.assignPoint instanceof Ref) {
        newState.assignPoint = ((Ref) this.assignPoint).cloneRef();
      }
    }
    for (Obj obj : this.argumentPoint) {
      if (obj == null) {
        newState.argumentPoint.add(null);
      } else {
        newState.argumentPoint.add(obj.cloneObj());
      }
    }
    Iterator<Deque<Map<String, Var>>> it1 = this.varTable.descendingIterator();
    while (it1.hasNext()) {
      Deque<Map<String, Var>> copyFrom = it1.next();
      Deque<Map<String, Var>> copyTo = new ArrayDeque<>();
      Iterator<Map<String, Var>> it2 = copyFrom.descendingIterator();
      while (it2.hasNext()) {
        Map<String, Var> mapCopyFrom = it2.next();
        Map<String, Var> mapCopyTo = new HashMap<>();
        for (Entry<String, Var> varEntry : mapCopyFrom.entrySet()) {
          mapCopyTo.put(varEntry.getKey(), varEntry.getValue().cloneVar());
        }
        copyTo.push(mapCopyTo);
      }
      newState.varTable.push(copyTo);
    }
    for (Entry<String, Obj> objEntry : this.objTable.entrySet()) {
      newState.objTable.put(objEntry.getKey(), objEntry.getValue().cloneObj());
    }
    return newState;
  }
}
