class Node {
  Node delayed_next;
}


public class Example {
  public static void main(String[] args) {
    Node n1 = new Node();
    Node n2 = new Node();
    n1.delayed_next = n2;
    Node n3 = n1.delayed_next;
  }
}
