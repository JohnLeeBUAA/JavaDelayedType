package edu.uwaterloo.javadelayedtype;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

/**
 * Class for an object
 */
public class Obj {
  public String id;
  public int refCt;
  public String type;
  public Map<String, Ref> fields;

  public Obj() {}

  public Obj(ClassDef classDef) {
    this.id = UUID.randomUUID().toString();
    this.refCt = 0;
    this.type = classDef.className;
    this.fields = new HashMap<String, Ref>();

    for (Entry<String, FieldDef> entry : classDef.fieldDefs.entrySet()) {
      Ref temp = new Ref(entry.getValue().fieldType, entry.getValue().isDelayedType, false, "");
      this.fields.put(entry.getValue().fieldName, temp);
    }
  }

  public Obj cloneObj() {
    Obj newObj = new Obj();
    newObj.id = this.id;
    newObj.refCt = this.refCt;
    newObj.type = this.type;
    newObj.fields = new HashMap<String, Ref>();
    for (Entry<String, Ref> entry : this.fields.entrySet()) {
      newObj.fields.put(entry.getKey(), entry.getValue().cloneRef());
    }
    return newObj;
  }
}
