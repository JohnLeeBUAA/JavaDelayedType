package edu.uwaterloo.javadelayedtype;

import java.io.FileInputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CuPrinter {

  public static void main(String[] args) throws Exception {
    // creates an input stream for the file to be parsed
    FileInputStream in = new FileInputStream("examples/Main.java");

    // parse the file
    CompilationUnit cu = JavaParser.parse(in);

    // prints the resulting compilation unit to default system output
    // System.out.println(cu.toString());

    // Analyzer analyzer = new Analyzer(0);
    // ASTVisitor visitor = new ASTVisitor();
    // visitor.visit(cu, analyzer);

    new Visitor().visit(cu, null);
  }

  /**
   * Simple visitor implementation for visiting MethodDeclaration nodes.
   */
  private static class Visitor extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(AssignExpr e, Void arg) {
      /*
       * here you can access the attributes of the method. this method will be called for all
       * methods in this CompilationUnit, including inner class methods
       */
      e.getValue().accept(this, arg);
      e.getTarget().accept(this, arg);
      // System.out.println(r.getTarget() + "///" + r.getValue());
    }
  }
}
