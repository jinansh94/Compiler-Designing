package cop5556sp18;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.Token;
import cop5556sp18.Types.Type;
import cop5556sp18.AST.ASTVisitor;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
import cop5556sp18.AST.Expression;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionBooleanLiteral;
import cop5556sp18.AST.ExpressionConditional;
import cop5556sp18.AST.ExpressionFloatLiteral;
import cop5556sp18.AST.ExpressionFunctionAppWithExpressionArg;
import cop5556sp18.AST.ExpressionFunctionAppWithPixel;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.ExpressionPixel;
import cop5556sp18.AST.ExpressionPixelConstructor;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.ExpressionUnary;
import cop5556sp18.AST.LHS;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;
import cop5556sp18.Types;
public class TypeChecker implements ASTVisitor {


	TypeChecker() {
		SymbolTable.symbolT=new HashMap<String, LinkedHashMap<Integer, Declaration>>();
		SymbolTable.stack=new ArrayList<Integer>();
	}
	
	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}

	
	
	// Name is only used for naming the output file. 
	// Visit the child block to type check program.
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		program.block.visit(this, arg);
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		SymbolTable.enterScope();
		//System.out.println("block visited");
		for(int i =0;i<block.decsOrStatements.size();i++) {
			//System.out.println("entering call");
			block.decsOrStatements.get(i).visit(this, arg);
			//System.out.println("exiting call");
		}
		SymbolTable.leavScope();
		return null;
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg) throws Exception {
		//System.out.println("declaration visited@@@@@@@@");
		String decName = declaration.name;
		if(SymbolTable.symbolT.containsKey(decName)&&SymbolTable.symbolT.get(decName).containsKey(SymbolTable.stack.get(0))) {
			//System.out.println("visit declaration called found in symbol table"+SymbolTable.symbolT.containsKey(decName));
			throw new SemanticException(declaration.firstToken, "The declaration " + declaration.name +" is already defined in these scope.");
		}
		else {
			//System.out.println("visit declaration called");
			Type decType = Types.getType(declaration.type);
			Expression e0 = declaration.width;
			if(e0 == null) {
				Expression e1 = declaration.height;
				if(e1 == null) {
				SymbolTable.addVar(declaration);
				return null;
				}
				else {
					throw new SemanticException(declaration.firstToken, "The height and width of the declaration " + declaration.name + "should be null");
				}
			}
			else {
				e0.visit(this, arg);
				Type e0_Type = e0.type;
				if ((e0_Type == Type.INTEGER && decType == Type.IMAGE)) {
					Expression e1 = declaration.height;
					e1.visit(this, arg);
					Type e1_Type = e1.type;
					if((e1_Type == Type.INTEGER && decType == Type.IMAGE)) {
						SymbolTable.addVar(declaration);
						return null;
					}
					else {
						throw new SemanticException(declaration.firstToken, "Declaration");
					}
				}
				else {
					throw new SemanticException(declaration.firstToken, "The height and width of the declaration " + declaration.name + "should be INTEGER and declaration type should be IMAGE");
				}	
			}
		}
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg) throws Exception {
		//System.out.println("StatementWrite visited");
		Declaration decName = SymbolTable.lookDec(statementWrite.sourceName);
		Declaration decName1 = SymbolTable.lookDec(statementWrite.destName);
		if(decName != null) {
			if(decName1 != null) {
				Type scrDecType = Types.getType(decName.type);
				Type destDecType = Types.getType(decName1.type);
				if(scrDecType == Type.IMAGE && destDecType == Type.FILE) {
					return null;
				}
				else {
					throw new SemanticException(statementWrite.firstToken, "StatementWrite should have " + decName + " as IMAGE and " + decName1 + " as FILE.");
				}
			}
			else {
				throw new SemanticException(statementWrite.firstToken, "StatementWrite should have a defined FILE type of declaration in 'to' and not null");
			}
		}
		else {
			throw new SemanticException(statementWrite.firstToken, "StatementWrite should have a defined IMAGE type of declaration and not null");
		}
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg) throws Exception {
		//System.out.println("Statment INput visited");
		Declaration decName = SymbolTable.lookDec(statementInput.destName);
		if(decName != null) {
			Expression ex = statementInput.e;
			ex.visit(this, arg);
			Type ex_Type = ex.type;
			if(ex_Type==Type.INTEGER) {
				return null;
			}
			else {
				throw new SemanticException(statementInput.firstToken, "StatementInput should have " + decName + " as INTEGER.");
			}
			
		}
		else {
			throw new SemanticException(statementInput.firstToken, "StatementInput should have a defined INTEGER type of declaration and not null");
		}
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		//System.out.println("PixelSelector visited");
		Expression ex0 = pixelSelector.ex;
		ex0.visit(this, arg);
		Expression ex1 = pixelSelector.ey;
		ex1.visit(this, arg);
		Type ex0_Type = ex0.type;
		Type ex1_Type = ex1.type;
		if(ex0_Type == ex1_Type) {
			if(ex0_Type == Type.INTEGER || ex0_Type == Type.FLOAT) {
				//System.out.println("PixelSelector closed");
				return null;
			}
			else {
				throw new SemanticException(pixelSelector.firstToken, "PixelSelector should have either have a defined INTEGER type or FLOAT type of expression");
			}
		}
		else {
			throw new SemanticException(pixelSelector.firstToken, "PixelSelector should have either have both it's expression type eqaul either INTEGER or FLOAT");
		}
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		//System.out.println("ExpressionConditional visited");
		Expression ex1 = expressionConditional.guard;
		ex1.visit(this, arg);
		Expression ex2 = expressionConditional.trueExpression;
		ex2.visit(this, arg);
		Expression ex3 = expressionConditional.falseExpression;
		ex3.visit(this, arg);
		Type ex1Type = ex1.type;
		Type ex2Type = ex2.type;
		Type ex3Type = ex3.type;
		if (ex1Type == Type.BOOLEAN) {
			if (ex2Type == ex3Type) {
				expressionConditional.type = ex2Type;
				return ex2Type;
			}
			else {
				throw new SemanticException(expressionConditional.firstToken, "ExpressionConditional should have both it's true expression and false expression of same type");
			}
		}
		else {
			throw new SemanticException(expressionConditional.firstToken, "ExpressionConditional should have it's guard of BOLEAN type");
		}
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception {
	//	System.out.println("ExpressionBinary visited");
		Expression leftEx = expressionBinary.leftExpression;
		leftEx.visit(this, arg);
		Expression rightEx = expressionBinary.rightExpression;
		rightEx.visit(this, arg);
		Type leftEx_type = leftEx.type;
		Type rightEx_type = rightEx.type;
		Kind opFun = expressionBinary.op;
		if(leftEx_type == Type.INTEGER && rightEx_type == Type.INTEGER) {
			if(opFun == Kind.OP_PLUS || opFun == Kind.OP_MINUS || opFun == Kind.OP_TIMES || opFun == Kind.OP_DIV || opFun == Kind.OP_MOD || opFun == Kind.OP_POWER || opFun == Kind.OP_AND || opFun == Kind.OP_OR) {
				expressionBinary.type = Type.INTEGER;
				return Type.INTEGER;
			}
			else if(opFun == Kind.OP_EQ || opFun == Kind.OP_NEQ || opFun == Kind.OP_GT || opFun == Kind.OP_GE || opFun == Kind.OP_LT || opFun == Kind.OP_LE) {
				expressionBinary.type = Type.BOOLEAN; 
				return Type.BOOLEAN;
			}
			else {
				throw new SemanticException(expressionBinary.firstToken, "ExpressionBinary: The operations cannot be performed on the given INTEGER type");
			}
		}
		else if(leftEx_type == Type.FLOAT && rightEx_type == Type.FLOAT) {
			if(opFun == Kind.OP_PLUS || opFun == Kind.OP_MINUS || opFun == Kind.OP_TIMES || opFun == Kind.OP_DIV || opFun == Kind.OP_POWER) {
				expressionBinary.type = Type.FLOAT;
				return Type.FLOAT;
			}
			else if(opFun == Kind.OP_EQ || opFun == Kind.OP_NEQ || opFun == Kind.OP_GT || opFun == Kind.OP_GE || opFun == Kind.OP_LT || opFun == Kind.OP_LE) {
				expressionBinary.type = Type.BOOLEAN;
				return Type.BOOLEAN;
			}
			else {
				throw new SemanticException(expressionBinary.firstToken, "ExpressionBinary: The operations cannot be performed on the given FLOAT type");
			}
		}
		else if(leftEx_type == Type.FLOAT && rightEx_type == Type.INTEGER) {
			if(opFun == Kind.OP_PLUS || opFun == Kind.OP_MINUS || opFun == Kind.OP_TIMES || opFun == Kind.OP_DIV || opFun == Kind.OP_POWER) {
				expressionBinary.type = Type.FLOAT;
				return Type.FLOAT;
			}
			else {
				throw new SemanticException(expressionBinary.firstToken, "ExpressionBinary: The operation cannot be performed on the given left expression which is INTEGER and right expression which is FLOAT");
			}
		}
		else if(leftEx_type == Type.INTEGER && rightEx_type == Type.FLOAT) {
			if(opFun == Kind.OP_PLUS || opFun == Kind.OP_MINUS || opFun == Kind.OP_TIMES || opFun == Kind.OP_DIV || opFun == Kind.OP_POWER) {
				expressionBinary.type = Type.FLOAT;
				return Type.FLOAT;
			}
			else {
				throw new SemanticException(expressionBinary.firstToken, "ExpressionBinary: The operation cannot be performed on the given left expression which is FLOAT and right expression which is INTEGER");
			}
		}
		else if(leftEx_type == Type.BOOLEAN && rightEx_type == Type.BOOLEAN) {
			if(opFun == Kind.OP_AND || opFun == Kind.OP_OR) {
				expressionBinary.type = Type.BOOLEAN;
				return Type.BOOLEAN;
			}
			else if(opFun == Kind.OP_EQ || opFun == Kind.OP_NEQ || opFun == Kind.OP_GT || opFun == Kind.OP_GE || opFun == Kind.OP_LT || opFun == Kind.OP_LE) {
				expressionBinary.type = Type.BOOLEAN;
				return Type.BOOLEAN;
			}
			else {
				throw new SemanticException(expressionBinary.firstToken, "ExpressionBinary: The operation cannot be performed on the given BOOLEAN type left and right expressions");
			}
		}
		else {
			throw new SemanticException(expressionBinary.firstToken, "ExpressionBinary: The left and right expressions are not properly matched, it should be either INTEGER,INTEGER FLOAT,FLOAT INTEGER,FLOAT FLOAT,INTEGER or BOOLEAN,BOOLEAN");
		}
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		//System.out.println("ExpressionUnary visited");
		Expression ex = expressionUnary.expression;
		Kind opName = expressionUnary.op;
		Type ex_type = (Type) ex.visit(this, arg);
		switch (ex_type) {
			case INTEGER:{
				if (opName == Kind.OP_EXCLAMATION || opName == Kind.OP_MINUS || opName == Kind.OP_PLUS) {
					expressionUnary.type= ex_type;
				}
				else {
					throw new SemanticException(expressionUnary.firstToken, "ExpressionUnary: Unary Expressions on INTEGER can only be applied on Unary not, Unary plus and Unary Minus");
				}
			}
			break;
			case FLOAT:{
				if (opName == Kind.OP_MINUS || opName == Kind.OP_PLUS) {
					expressionUnary.type= ex_type;
				}
				else {
					throw new SemanticException(expressionUnary.firstToken, "ExpressionUnary: Unary Expressions on FLOAT can only be applied on Unary plus and Unary Minus");
				}
			}
			break;
			case BOOLEAN:{
				if (opName == Kind.OP_EXCLAMATION) {
					expressionUnary.type= ex_type;
				}
				else {
					throw new SemanticException(expressionUnary.firstToken, "ExpressionUnary: Unary Expressions on BOOLEAN can only be applied on Unary not");
				}
				
			}
			break;
			default:{
				throw new SemanticException(expressionUnary.firstToken, "ExpressionUnary: Unary Expressions can only be applied on INTEGER,FLOAT or BOOLEAN");
			}
			
		}
		//expressionUnary.type= ex_type;
		return ex_type;
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		//System.out.println("ExpressionIntegerLiteral visited");
		expressionIntegerLiteral.type=Type.INTEGER;
		return Type.INTEGER;
	}

	@Override
	public Object visitBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception {
		//System.out.println("ExpressionBooleanLiteral visited");
		expressionBooleanLiteral.type=Type.BOOLEAN;
		return Type.BOOLEAN;
	}

	@Override
	public Object visitExpressionPredefinedName(ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		//System.out.println("ExpressionPredefinedName visited");
		expressionPredefinedName.type = Type.INTEGER;
			return Types.Type.INTEGER;
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		//System.out.println("ExpressionFloatLiteral visited");
		expressionFloatLiteral.type=Type.FLOAT;
		return Type.FLOAT;
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg, Object arg)
			throws Exception {
		//System.out.println("ExpressionFunctionAppWithExpressionArg visited");
		
			Kind name = expressionFunctionAppWithExpressionArg.function;
			Expression ex = expressionFunctionAppWithExpressionArg.e;
			ex.visit(this, arg);
			Type ex_type = ex.type;
			if(ex_type == Type.INTEGER) {
				if (name == Kind.KW_abs || name == Kind.KW_red || name == Kind.KW_green || name == Kind.KW_blue || name == Kind.KW_alpha ) {
					expressionFunctionAppWithExpressionArg.type = Type.INTEGER;
					return Type.INTEGER;
				}
				else if (name ==Kind.KW_float) {
					expressionFunctionAppWithExpressionArg.type = Type.FLOAT;
					return Type.FLOAT;
				}
				else if (name == Kind.KW_int) {
					expressionFunctionAppWithExpressionArg.type = Type.INTEGER;
					return Type.INTEGER;
				}
				else {
					throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken, "ExpressionFunctionAppWithExpressionArg: " + name + " should be either abs,red,green,blue,alpha,float or int");
				}
			}
			else if(ex_type == Type.FLOAT) {
				if(name == Kind.KW_abs || name == Kind.KW_sin || name == Kind.KW_cos || name == Kind.KW_atan || name == Kind.KW_log) {
					expressionFunctionAppWithExpressionArg.type = Type.FLOAT;
					return Type.FLOAT;
				}
				else if ( name ==Kind.KW_float) {
					expressionFunctionAppWithExpressionArg.type = Type.FLOAT;
					return Type.FLOAT;
				}
				else if ( name == Kind.KW_int) {
					expressionFunctionAppWithExpressionArg.type = Type.INTEGER;
					return Type.INTEGER;
				}
				else {
					throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken, "ExpressionFunctionAppWithExpressionArg: " + name + " should be either abs,sin,cos,atan,log,float or int");
				}
			}
			else if (ex_type ==Type.IMAGE) {
				if(name ==Kind.KW_width || name == Kind.KW_height) {
					expressionFunctionAppWithExpressionArg.type = Type.INTEGER;
					return Type.INTEGER;
				}
				else {
					throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken, "ExpressionFunctionAppWithExpressionArg: " + name + " should be either width or height");
				}
			}
			else {
				throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken, "ExpressionFunctionAppWithExpressionArg: "+ ex_type + "should either be INTEGER,FLOAT or IMAGE");
			}
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		//System.out.println("ExpressionFunctionAppWithPixel visited");
		//System.out.println("visitExpressionFunctionAppwithpixel called");
		Kind name = expressionFunctionAppWithPixel.name;
		Expression e_0 = expressionFunctionAppWithPixel.e0;
		e_0.visit(this, arg);
		Expression e_1 = expressionFunctionAppWithPixel.e1;
		e_1.visit(this, arg);
		Type e0_type = e_0.type;
		Type e1_type = e_1.type;
		if(name == Kind.KW_cart_x || name == Kind.KW_cart_y) {
			if(e0_type == Type.FLOAT && e1_type==Type.FLOAT) {
				expressionFunctionAppWithPixel.type = Type.INTEGER;
				return Type.INTEGER;
			}
			else {
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken, "ExpressionFunctionAppWithPixel: Both expression type should be FLOAT with cart_x or cart_y");
			}
		}
		else if(name == Kind.KW_polar_a || name == Kind.KW_polar_r) {
			if(e0_type == Type.INTEGER && e1_type==Type.INTEGER) {
				expressionFunctionAppWithPixel.type = Type.FLOAT;
				return Type.FLOAT;
			}
			else {
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken, "ExpressionFunctionAppWithPixel: Both expression type should be INTEGER with polar_a or polar_r");
			}
		}
		else {
			throw new SemanticException(expressionFunctionAppWithPixel.firstToken, "ExpressionFunctionAppWithPixel: " + name + " is not expected either the folloeing is expected cart_x,cart_y,polar_a or polar_r");
		}
	}

	@Override
	public Object visitExpressionPixelConstructor(ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		//System.out.println("ExpressionPixelConstructor visited");
		Expression ex_1 = expressionPixelConstructor.alpha;
		ex_1.visit(this, arg);
		Expression ex_2 = expressionPixelConstructor.red;
		ex_2.visit(this, arg);
		Expression ex_3 = expressionPixelConstructor.green;
		ex_3.visit(this, arg);
		Expression ex_4 = expressionPixelConstructor.blue;
		ex_4.visit(this, arg);
		Type ex_1_type = ex_1.type;
		Type ex_2_type = ex_2.type;
		Type ex_3_type = ex_3.type;
		Type ex_4_type = ex_4.type;
		if(ex_1_type == Type.INTEGER && ex_2_type == Type.INTEGER && ex_3_type == Type.INTEGER && ex_4_type == Type.INTEGER) {
			expressionPixelConstructor.type = Type.INTEGER;
			//System.out.println("ExpressionPixelConstructor closed");
			return Type.INTEGER;
		}
		else {
			throw new SemanticException(expressionPixelConstructor.firstToken, "ExpressionPixelConstructor: All the expression type should be INTEGER");
		}
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws Exception {
		//System.out.println("StatementAssign visited");
		statementAssign.lhs.visit(this, arg); 
		Expression ex = statementAssign.e; 
		ex.visit(this, arg);
		
		Type ex_type = ex.type;
		Type l_type = statementAssign.lhs.type ;
		if(l_type == ex_type) {
			return null;
		}
		else {
			throw new SemanticException(statementAssign.firstToken, "StatementAssign: The LHS type and expression type should match");
		}
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg) throws Exception {
		//System.out.println("StatementShow visited");
		Expression ex = statementShow.e;
		ex.visit(this, arg);
		Type ex_Type = ex.type;
		if(ex_Type == Type.INTEGER || ex_Type == Type.BOOLEAN || ex_Type == Type.FLOAT || ex_Type == Type.IMAGE) {
			return null;
		}
		else {
			throw new SemanticException(statementShow.firstToken, "StatementShow: The expression type should either be INTEGER or BOOLEAN or FLOAT or IMAGE");
		}
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel, Object arg) throws Exception {
		//System.out.println("ExpressionPixel visited");
		Declaration expressionPixelDec = SymbolTable.lookDec(expressionPixel.name);
		expressionPixel.pixelSelector.visit(this, arg);
		if (expressionPixelDec != null) {
			Type decName = Types.getType(expressionPixelDec.type);
			if(decName == Type.IMAGE) {
				expressionPixel.type = Type.INTEGER;
				System.out.println("ExpressionPixel closed");
				return Type.INTEGER;
			}
			else {
				throw new SemanticException(expressionPixel.firstToken, "ExpressionPixel: " + decName + "should be IMAGE" );
			}
		}
		else {
		throw new SemanticException(expressionPixel.firstToken, "ExpressionPixel: The declaration is not defined, it should not be null");}
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws Exception {
		//System.out.println("ExpressionIdent visited");
		Declaration expressionPixelDec = SymbolTable.lookDec(expressionIdent.name);
		//System.out.println("expressionPixelDec" + expressionPixelDec);
		if (expressionPixelDec != null) {
			expressionIdent.type = Types.getType(expressionPixelDec.type);
			//System.out.println("ExpressionIdent closing");
			return expressionIdent.type;
			
		}
		else {
			throw new SemanticException(expressionIdent.firstToken, "ExpressionIdent: The declaration is not defined, it should not be null");
		}
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg) throws Exception {
		//System.out.println("LHSSample visited");
		Declaration expressionPixelDec = SymbolTable.lookDec(lhsSample.name);
		lhsSample.pixelSelector.visit(this, arg);
		if(expressionPixelDec != null) {
			Type decName = Types.getType(expressionPixelDec.type);
			if(decName == Type.IMAGE) {
				lhsSample.type = Type.INTEGER;
				return Type.INTEGER;
			}
			else {
				throw new SemanticException(lhsSample.firstToken, "LHSSample: "+ decName + " should be a IMAGE");
			}
		}
		else {
			throw new SemanticException(lhsSample.firstToken, "LHSSample: The declaration is not defined, it should not be null");
		}
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg) throws Exception {
		//System.out.println("LHSPixel visited");
		Declaration expressionPixelDec = SymbolTable.lookDec(lhsPixel.name);
		lhsPixel.pixelSelector.visit(this, arg);
		if(expressionPixelDec != null) {
			Type decName = Types.getType(expressionPixelDec.type);
			if(decName == Type.IMAGE) {
				lhsPixel.type = Type.INTEGER;
				return Type.INTEGER;
			}
			else {
				throw new SemanticException(lhsPixel.firstToken, "LHSPixel: " + decName + " should be a IMAGE");
			}
		}
		else {
			throw new SemanticException(lhsPixel.firstToken, "LHSPixel: The declaration is not defined, it should not be null");
		}
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg) throws Exception {
		//System.out.println("LHSIdent visited");
		Declaration expressionPixelDec = SymbolTable.lookDec(lhsIdent.name);
		
		if(expressionPixelDec != null) {
			Type decName = Types.getType(expressionPixelDec.type);
			lhsIdent.type = decName;
			return decName;
		}
		else {
			throw new SemanticException(lhsIdent.firstToken, "LHSIdent: The declaration is not defined, it should not be null");
		}
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg) throws Exception {
		//System.out.println("StatementIf visited");
		statementIf.b.visit(this, arg);
		Expression ex = statementIf.guard;
		ex.visit(this, arg);
		Type ex_type = ex.type ;
		if(ex_type == Type.BOOLEAN) {
			return null;
		}
		else {
			throw new SemanticException(statementIf.firstToken, "StatementIf: Gurad expression should have a BOOLEAN type");
		}
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws Exception {
		//System.out.println("StatementWhile visited");
		statementWhile.b.visit(this, arg);
		Expression ex = statementWhile.guard;
		ex.visit(this, arg);
		Type ex_type = ex.type;
		if(ex_type == Type.BOOLEAN) {
			return null;
		}
		else {
			throw new SemanticException(statementWhile.firstToken, "StatementWhile: Gurad expression should have a BOOLEAN type");
		}
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg) throws Exception {
		//System.out.println("StatementSleep visited");
		Expression ex = statementSleep.duration;
		ex.visit(this, arg);
		Type ex_Type = ex.type;
		if(ex_Type == Type.INTEGER) {
			return null;
		}
		else {
			throw new SemanticException(statementSleep.firstToken, "StatementSleep: Duration expression should be INTEGER");
		}
	}

	
	
	
	
	
	
}


