package edu.uwaterloo.javadelayedtype;

import java.io.FileInputStream;

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

public class ASTParser {
	public static void main(String[] args) throws Exception {
		ASTParser astParser = new ASTParser();
		astParser.setCu(JavaParser.parse(new FileInputStream("examples/Main.java")));
		astParser.parseMethod(astParser.getMainBody());
	}

	private CompilationUnit cu;

	public CompilationUnit getCu() {
		return cu;
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}

	public BlockStmt getMainBody() {
		NodeList<TypeDeclaration<?>> types = cu.getTypes();
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

	public BlockStmt getMethodBody(String className, String methodName) {
		ClassOrInterfaceDeclaration clazz = cu.getClassByName(className).get();
		MethodDeclaration method = clazz.getMethodsByName(methodName).get(0);
		return method.getBody().get();
	}

	public void parseMethod(BlockStmt blockStmt) {
		ASTVisitor visitor = new ASTVisitor();
		visitor.visit(blockStmt, 0);
	}

	private class ASTVisitor implements VoidVisitor<Integer> {
		public void out(Node n, int indentLevel) {
			String indent = "";
			for (int i = 0; i < indentLevel; i++) {
				indent += "\t";
			}
			System.out.print("\n" + indent + n.getClass().getSimpleName());

		}

		@Override
		public void visit(NodeList n, Integer arg) {

		}

		@Override
		public void visit(AnnotationDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(AnnotationMemberDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ArrayAccessExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ArrayCreationExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ArrayCreationLevel n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ArrayInitializerExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ArrayType n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(AssertStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(AssignExpr n, Integer arg) {
			out(n, arg);
			n.getTarget().accept(this, arg + 1);
			n.getValue().accept(this, arg + 1);
		}

		@Override
		public void visit(BinaryExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(BlockComment n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(BlockStmt n, Integer arg) {
			out(n, arg);
			if (n.getStatements() != null) {
				for (final Statement s : n.getStatements()) {
					s.accept(this, arg + 1);
				}
			}
		}

		@Override
		public void visit(BooleanLiteralExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(BreakStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(CastExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(CatchClause n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(CharLiteralExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ClassExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ClassOrInterfaceType n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(CompilationUnit n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ConditionalExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ConstructorDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ContinueStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(DoStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(DoubleLiteralExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(EmptyStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(EnclosedExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(EnumConstantDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(EnumDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ExplicitConstructorInvocationStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ExpressionStmt n, Integer arg) {
			out(n, arg);
			n.getExpression().accept(this, arg + 1);
		}

		@Override
		public void visit(FieldAccessExpr n, Integer arg) {
			out(n, arg);
			n.getScope().accept(this, arg + 1);
			n.getName().accept(this, arg + 1);
		}

		@Override
		public void visit(FieldDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ForStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ForeachStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(IfStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ImportDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(InitializerDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(InstanceOfExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(IntegerLiteralExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(IntersectionType n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(JavadocComment n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(LabeledStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(LambdaExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(LineComment n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(LocalClassDeclarationStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(LongLiteralExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(MarkerAnnotationExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(MemberValuePair n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(MethodCallExpr n, Integer arg) {
			out(n, arg);
			n.getScope().ifPresent(s -> s.accept(this, arg + 1));
			n.getTypeArguments().ifPresent(tas -> tas.forEach(ta -> ta.accept(this, arg + 1)));
			n.getName().accept(this, arg + 1);
			if (n.getArguments() != null) {
				for (final Expression e : n.getArguments()) {
					e.accept(this, arg + 1);
				}
			}
			parseMethod(getMethodBody("Node", "getNext"));
		}

		@Override
		public void visit(MethodDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(MethodReferenceExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(NameExpr n, Integer arg) {
			out(n, arg);
			System.out.print("\t" + n.toString());
		}

		@Override
		public void visit(Name n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(NormalAnnotationExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(NullLiteralExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ObjectCreationExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(PackageDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(Parameter n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(PrimitiveType n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ReturnStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(SimpleName n, Integer arg) {
			out(n, arg);
			System.out.print("\t" + n.toString());
		}

		@Override
		public void visit(SingleMemberAnnotationExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(StringLiteralExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(SuperExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(SwitchEntryStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(SwitchStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(SynchronizedStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ThisExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ThrowStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(TryStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(TypeExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(TypeParameter n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(UnaryExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(UnionType n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(UnknownType n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(VariableDeclarationExpr n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(VariableDeclarator n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(VoidType n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(WhileStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(WildcardType n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ModuleDeclaration n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ModuleRequiresStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ModuleExportsStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ModuleProvidesStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ModuleUsesStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(ModuleOpensStmt n, Integer arg) {
			out(n, arg);

		}

		@Override
		public void visit(UnparsableStmt n, Integer arg) {
			out(n, arg);

		}
	}
}
