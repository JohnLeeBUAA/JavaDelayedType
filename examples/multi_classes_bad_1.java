class A {
  A delayed_A;
  B delayed_B;
}

class B {
  B delayed_B;
}

public class Example {
  public static void main(String[] args) {
    A A1 = new A();
    A A2 = new A();
    A A3 = new A();
    B B1 = new B();
    B B2 = new B();
    
    A1.delayed_A = A2;
    A1.delayed_B = B1;
    A2.delayed_A = A3;
    A2.delayed_B = B1;
    A3.delayed_A = A2;
    B1.delayed_B = B2;
    
    B B3 = A1.delayed_B;
  }
}
