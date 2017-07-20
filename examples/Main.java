package com.zijin.example;

class Node {
	Node next;
	
	public void setNext(Node node) {
		this.next = node;
	}
	
	public Node getNext() {
		return this.next;
	}
}

public class Main {
	public static void main(String[] args) {
		Node n1 = new Node();
		Node n2 = new Node();
		n2.next = n1.getNext();
	}
}