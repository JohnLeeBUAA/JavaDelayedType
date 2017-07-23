package edu.uwaterloo.javadelayedtype;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsStmt;
import com.github.javaparser.ast.modules.ModuleOpensStmt;
import com.github.javaparser.ast.modules.ModuleProvidesStmt;
import com.github.javaparser.ast.modules.ModuleRequiresStmt;
import com.github.javaparser.ast.modules.ModuleUsesStmt;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.UnparsableStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.VoidVisitor;

public class Analyzer {
  private CompilationUnit compilationUnit;
  private Tracker tracker;

  public Analyzer(String srcPath) throws IOException {
    this.compilationUnit = JavaParser.parse(new FileInputStream(srcPath));
    this.tracker = new Tracker(srcPath);
  }

  // TODO: delete this
  public static void main(String[] args) throws IOException {
    String srcPath = "examples/test1.java";
    Analyzer analyzer = new Analyzer(srcPath);
    analyzer.analyze();
  }

  /**
   * Entrance method
   */
  public void analyze() {
    addClassAndFieldDefs();
    parseMethod(getMainBody());
  }

  /**
   * Add class and field definitions to Tracker
   */
  private void addClassAndFieldDefs() {
    NodeList<TypeDeclaration<?>> types = compilationUnit.getTypes();

    // add all user-defined class definitions
    for (TypeDeclaration<?> type : types) {
      if (type instanceof ClassOrInterfaceDeclaration) {
        ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) type;
        if (!this.tracker.addClassDef(clazz.getNameAsString())) {
          this.tracker.reporter.report(clazz.getBegin().get().line, clazz.getBegin().get().column,
              clazz.getNameAsString());
        }
      }
    }

    // add field definitions
    for (TypeDeclaration<?> type : types) {
      if (type instanceof ClassOrInterfaceDeclaration) {
        ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) type;
        for (FieldDeclaration field : clazz.getFields()) {
          String fieldType = field.getElementType().toString();
          for (VariableDeclarator variable : field.getVariables()) {
            if (!this.tracker.addFieldDef(clazz.getNameAsString(), variable.getNameAsString(),
                fieldType)) {
              this.tracker.reporter.report(variable.getBegin().get().line,
                  variable.getBegin().get().column, variable.getNameAsString());
            }
          }
        }
      }
    }
  }

  /**
   * Get the body for main method
   * 
   * @return
   */
  private BlockStmt getMainBody() {
    NodeList<TypeDeclaration<?>> types = compilationUnit.getTypes();

    for (TypeDeclaration<?> type : types) {
      NodeList<BodyDeclaration<?>> members = type.getMembers();
      for (BodyDeclaration<?> member : members) {
        if (member instanceof MethodDeclaration) {
          MethodDeclaration method = (MethodDeclaration) member;
          if (method.getName().toString().equals("main")) {
            return method.getBody().get();
          }
        }
      }
    }
    return null;
  }

  // private BlockStmt getMethodBody(String className, String methodName) {
  // ClassOrInterfaceDeclaration clazz = compilationUnit.getClassByName(className).get();
  // MethodDeclaration method = clazz.getMethodsByName(methodName).get(0);
  // return method.getBody().get();
  // }

  private void parseMethod(BlockStmt blockStmt) {
    if (blockStmt != null) {
      this.tracker.callEntrance();
      // TODO: add things to latest alias map
      ASTVisitor visitor = new ASTVisitor();
      visitor.visit(blockStmt, this.tracker);
      this.tracker.callExit();
    }
  }

  /**
   * A private visitor class to parse AST
   */
  private class ASTVisitor implements VoidVisitor<Tracker> {
    /**
     * Print out node info for debugging purpose
     * 
     * @param n
     * @param indentLevel
     * @param info
     */
    public void printNode(Node n, int indentLevel, String info) {
      System.out.println(
          StringUtils.repeat('\t', indentLevel) + n.getClass().getSimpleName() + ": " + info);
    }

    @Override
    public void visit(@SuppressWarnings("rawtypes") NodeList n, Tracker arg) {

    }

    @Override
    public void visit(AnnotationDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(AnnotationMemberDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ArrayAccessExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ArrayCreationExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ArrayCreationLevel n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ArrayInitializerExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ArrayType n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(AssertStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(AssignExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(BinaryExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(BlockComment n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(BlockStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");
      if (n.getStatements() != null) {
        for (final Statement s : n.getStatements()) {
          arg.indentLevel++;
          s.accept(this, arg);
          arg.indentLevel--;
        }
      }
    }

    @Override
    public void visit(BooleanLiteralExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(BreakStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(CastExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(CatchClause n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(CharLiteralExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ClassExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ClassOrInterfaceType n, Tracker arg) {
      printNode(n, arg.indentLevel, n.getNameAsString());

    }

    @Override
    public void visit(CompilationUnit n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ConditionalExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ConstructorDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ContinueStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(DoStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(DoubleLiteralExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(EmptyStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(EnclosedExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(EnumConstantDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(EnumDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ExplicitConstructorInvocationStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ExpressionStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");
      arg.indentLevel++;
      n.getExpression().accept(this, arg);
      arg.indentLevel--;
    }

    @Override
    public void visit(FieldAccessExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");
      arg.indentLevel++;
      n.getScope().accept(this, arg);
      n.getName().accept(this, arg);
      arg.indentLevel--;
    }

    @Override
    public void visit(FieldDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ForStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ForeachStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(IfStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ImportDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(InitializerDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(InstanceOfExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(IntegerLiteralExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(IntersectionType n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(JavadocComment n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(LabeledStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(LambdaExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(LineComment n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(LocalClassDeclarationStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(LongLiteralExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(MarkerAnnotationExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(MemberValuePair n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(MethodCallExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(MethodDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(MethodReferenceExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(NameExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, n.getNameAsString());
    }

    @Override
    public void visit(Name n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(NormalAnnotationExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(NullLiteralExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ObjectCreationExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, n.toString());
      arg.indentLevel++;
      n.getScope().ifPresent(s -> s.accept(this, arg));
      n.getTypeArguments().ifPresent(tas -> tas.forEach(ta -> ta.accept(this, arg)));
      n.getType().accept(this, arg);
      if (n.getArguments() != null) {
        for (final Expression e : n.getArguments()) {
          e.accept(this, arg);
        }
      }
      n.getAnonymousClassBody().ifPresent(acb -> acb.forEach(m -> m.accept(this, arg)));
      arg.indentLevel--;
    }

    @Override
    public void visit(PackageDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(Parameter n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(PrimitiveType n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ReturnStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(SimpleName n, Tracker arg) {
      printNode(n, arg.indentLevel, n.getIdentifier());
    }

    @Override
    public void visit(SingleMemberAnnotationExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(StringLiteralExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(SuperExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(SwitchEntryStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(SwitchStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(SynchronizedStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ThisExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ThrowStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(TryStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(TypeExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(TypeParameter n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(UnaryExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(UnionType n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(UnknownType n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(VariableDeclarationExpr n, Tracker arg) {
      printNode(n, arg.indentLevel, n.toString());
      arg.indentLevel++;
      for (final VariableDeclarator v : n.getVariables()) {
        v.accept(this, arg);
      }
      arg.indentLevel--;
    }

    @Override
    public void visit(VariableDeclarator n, Tracker arg) {
      printNode(n, arg.indentLevel, n.toString());
      String varName = n.getNameAsString();
      String varType = n.getType().asString();
      arg.temp = "";
      arg.indentLevel++;
      n.getInitializer().ifPresent(i -> i.accept(this, arg));
      arg.indentLevel--;
      String refId = arg.temp;
      if (!arg.addVar(varName, varType, refId)) {
        arg.reporter.report(n.getBegin().get().line, n.getBegin().get().column,
            n.getNameAsString());
      }
    }

    @Override
    public void visit(VoidType n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(WhileStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(WildcardType n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ModuleDeclaration n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ModuleRequiresStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ModuleExportsStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ModuleProvidesStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ModuleUsesStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(ModuleOpensStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

    @Override
    public void visit(UnparsableStmt n, Tracker arg) {
      printNode(n, arg.indentLevel, "");

    }

  }
}
