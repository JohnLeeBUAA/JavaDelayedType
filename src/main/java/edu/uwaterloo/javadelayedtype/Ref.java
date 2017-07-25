package edu.uwaterloo.javadelayedtype;

/**
 * Class for a reference
 */
public class Ref {
  public String type;
  public boolean isDelayedType;
  public boolean isCompleted;
  public String objId;

  public Ref() {}

  public Ref(String type, boolean isDelayedType, boolean isCompleted, String objId) {
    this.type = type;
    this.isDelayedType = isDelayedType;
    this.isCompleted = isCompleted;
    this.objId = objId;
  }

  public Ref cloneRef() {
    Ref newRef = new Ref();
    newRef.type = this.type;
    newRef.isDelayedType = this.isDelayedType;
    newRef.isCompleted = this.isCompleted;
    newRef.objId = this.objId;
    return newRef;
  }
}
