class Node {
  Node delayed_next;

  public void set(Node n) {
    this.delayed_next = n;
  }
}


public class Example {
  public static void main(String[] args) {
    Node n1 = new Node();
    Node n2 = new Node();
    n1.set(n2);
    Node n3 = n1.delayed_next;
  }
}
