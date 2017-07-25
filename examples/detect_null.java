class Node {
  Node non_delayed_next;
}


public class Example {
  public static void main(String[] args) {
    Node n1 = new Node();
    Node n3 = n1.non_delayed_next.non_delayed_next;
  }
}
