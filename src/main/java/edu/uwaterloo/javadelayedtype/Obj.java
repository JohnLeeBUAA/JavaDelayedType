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
  public Map<String, String> fields;

  public Obj(ClassDef classDef) {
    this.id = UUID.randomUUID().toString();
    this.refCt = 0;
    this.type = classDef.className;
    this.fields = new HashMap<String, String>();

    for (Entry<String, FieldDef> entry : classDef.fieldDefs.entrySet()) {
      Ref temp = new Ref(entry.getValue().fieldType, entry.getValue().isDelayedType, false, "");
      this.fields.put(entry.getValue().fieldName, temp.id);
    }

    // TODO: delete this
    System.out.println("\nCreating object: " + classDef.className + " / " + this.id + "\n");
  }
}
