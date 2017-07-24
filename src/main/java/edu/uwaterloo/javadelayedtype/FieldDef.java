package edu.uwaterloo.javadelayedtype;

/**
 * Class for field definition
 */
public class FieldDef {
  public String fieldName;
  public String fieldType;
  public boolean isDelayedType;

  public FieldDef(String fieldName, String fieldType, boolean isDelayedType) {
    this.fieldName = fieldName;
    this.fieldType = fieldType;
    this.isDelayedType = isDelayedType;
  }
}
