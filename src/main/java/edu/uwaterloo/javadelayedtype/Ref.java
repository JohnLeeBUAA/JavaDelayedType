package edu.uwaterloo.javadelayedtype;

/**
 * Class for a reference
 */
public class Ref {
  public String type;
  public boolean isDelayedType;
  public boolean isCompleted;
  public String objId;

  public Ref(String type, boolean isDelayedType, boolean isCompleted, String objId) {
    this.type = type;
    this.isDelayedType = isDelayedType;
    this.isCompleted = isCompleted;
    this.objId = objId;
  }
}
