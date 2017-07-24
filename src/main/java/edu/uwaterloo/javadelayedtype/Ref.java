package edu.uwaterloo.javadelayedtype;

import java.util.UUID;

/**
 * Class for a reference
 */
public class Ref {
  public String id;
  public String type;
  public boolean isDelayedType;
  public boolean isCompleted;
  public String objId;

  public Ref(String type, boolean isDelayedType, boolean isCompleted, String objId) {
    this.id = UUID.randomUUID().toString();
    this.type = type;
    this.isDelayedType = isDelayedType;
    this.isCompleted = isCompleted;
    this.objId = objId;
  }
}
