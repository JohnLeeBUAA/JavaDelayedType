package edu.uwaterloo.javadelayedtype;

public class Analyzer {
	private int val;
	
	public Analyzer(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}
	
	public void increaseVal() {
		this.val++;
	}
}
