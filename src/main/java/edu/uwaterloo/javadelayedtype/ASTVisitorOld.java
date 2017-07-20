package edu.uwaterloo.javadelayedtype;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ASTVisitorOld extends VoidVisitorAdapter<Analyzer> {
	@Override
	public void visit(MethodDeclaration n, Analyzer analyzer) {
		analyzer.increaseVal();
		System.out.println(n.getClass().getSimpleName() + " " + analyzer.getVal());
		n.getBody().ifPresent(b -> b.accept(this, analyzer));
		//n.getBody().ifPresent(b -> System.out.println(b.getClass().getSimpleName()));
	}
	
	@Override
	public void visit(BlockStmt b, Analyzer analyzer) {
		analyzer.increaseVal();
		System.out.println(b.getClass().getSimpleName() + " " + analyzer.getVal());
	}
}
