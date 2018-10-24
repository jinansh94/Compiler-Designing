package cop5556sp18;
/* *
 * Initial code for SimpleParser for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Spring 2018.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Spring 2018 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2018
 */


import cop5556sp18.Scanner.Token;
//import cop5556sp18.AST.Express
import cop5556sp18.AST.*;
import cop5556sp18.Scanner.Kind;
import static cop5556sp18.Scanner.Kind.*;
import java.util.*;

import java.util.List;


public class Parser {
	
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}



	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}


	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	/*
	 * Program ::= Identifier Block
	 */
	public Program program() throws SyntaxException {
	//	System.out.println("Program");
		Token firstT = t; 
		Program p =null;
		
		if(isKind(firstIdentifier)) {
			Token programName = t;
			match(IDENTIFIER);
		
			Block p_block=block();
			p = new Program(firstT, programName,p_block);
			
			
			}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return p;
		
	}
	
	/*
	 * Block ::=  { (  (Declaration | Statement) ; )* }
	 */
	
	Kind[] firstDec = { KW_int, KW_boolean, KW_float, KW_filename,KW_image };
	Kind[] firstType = { KW_int, KW_boolean, KW_float, KW_filename };
	Kind[] firstStatement = { KW_input, KW_write, IDENTIFIER, KW_red, KW_green, KW_blue, KW_alpha, KW_while, KW_if, KW_show, KW_sleep};
	Kind[] firstImage = {KW_image};
	Kind[] firstInt =  { INTEGER_LITERAL};
	Kind[] firstFloat = { FLOAT_LITERAL};
	Kind[] firstFilename = { KW_filename};
	Kind[] firstBoolean = {KW_boolean};
	Kind[] firstInput = {KW_input};
	Kind[] firstWrite = {KW_write};
	Kind[] firstAssignment = {KW_red,KW_green,KW_blue,KW_alpha,IDENTIFIER};
	Kind[] firstWhile = {KW_while};
	Kind[] firstIf = {KW_if};
	Kind[] firstSleep = {KW_sleep};
	Kind[] firstShow = {KW_show};
	Kind[] firstIdentifier = {IDENTIFIER};
	Kind[] firstColor = {KW_red,KW_green,KW_blue,KW_alpha};
	Kind[] firstRed= {KW_red};
	Kind[] firstGreen = {KW_green};
	Kind[] firstBlue = {KW_blue};
	Kind[] firstAlpha = {KW_alpha};
	Kind[] firstPixelSelector = {LSQUARE};
	Kind[] firstExpression = {OP_MINUS, OP_PLUS, OP_EXCLAMATION,INTEGER_LITERAL,BOOLEAN_LITERAL,FLOAT_LITERAL,LPAREN,KW_sin,KW_cos,KW_atan,KW_abs,KW_log,KW_cart_x,KW_cart_y,KW_polar_a,KW_polar_r,KW_int,KW_float,KW_width,KW_height,KW_green,KW_red,KW_blue,KW_alpha,IDENTIFIER,KW_Z,KW_default_height,KW_default_width,LPIXEL};
	Kind[] firstQuestion = {OP_QUESTION};
	Kind[] firstEqual= {OP_EQ};
	Kind[] firstNotEqual= {OP_NEQ};
	Kind[] firstAnd = {OP_AND};
	Kind[] firstOr = {OP_OR};
	Kind[] firstLT = {OP_LT};
	Kind[] firstGT = {OP_GT};
	Kind[] firstLE = {OP_LE};
	Kind[] firstGE = {OP_GE};
	Kind[] firstPlus = {OP_PLUS};
	Kind[] firstMinus = {OP_MINUS};
	Kind[] firstMul = {OP_TIMES};
	Kind[] firstDivide = {OP_DIV};
	Kind[] firstMod = {OP_MOD};
	Kind[] firstPower = {OP_POWER};
	Kind[] firstExclamation = {OP_EXCLAMATION};
	Kind[] firstexpWithoutUnary = {OP_EXCLAMATION,INTEGER_LITERAL,BOOLEAN_LITERAL,FLOAT_LITERAL,LPAREN,KW_sin,KW_cos,KW_atan,KW_abs,KW_log,KW_cart_x,KW_cart_y,KW_polar_a,KW_polar_r,KW_int,KW_float,KW_width,KW_height,KW_green,KW_red,KW_blue,KW_alpha,IDENTIFIER,KW_Z,KW_default_height,KW_default_width,LPIXEL};
	Kind[] firstPrimary = {INTEGER_LITERAL,BOOLEAN_LITERAL,FLOAT_LITERAL,LPAREN,KW_sin,KW_cos,KW_atan,KW_abs,KW_log,KW_cart_x,KW_cart_y,KW_polar_a,KW_polar_r,KW_int,KW_float,KW_width,KW_height,KW_green,KW_red,KW_blue,KW_alpha,IDENTIFIER,KW_Z,KW_default_height,KW_default_width,LPIXEL};
	Kind[] firstLPAREN = {LPAREN};
	Kind[] firstFunctionApp = {KW_sin,KW_cos,KW_atan,KW_abs,KW_log,KW_cart_x,KW_cart_y,KW_polar_a,KW_polar_r,KW_int,KW_float,KW_width,KW_height,KW_green,KW_red,KW_blue,KW_alpha};
	Kind[] firstPredefinedName = {KW_Z,KW_default_width,KW_default_height};
	Kind[] firstPixelConstructor = {LPIXEL};
	Kind[] firstZ = {KW_Z};
	Kind[] firstDefaultWidth = {KW_default_width};
	Kind[] firstDefaultHeight = {KW_default_height};
	Kind[] firstSin = {KW_sin};
	Kind[] firstCos = {KW_cos};
	Kind[] firstAtan = {KW_atan};
	Kind[] firstAbs = {KW_abs};
	Kind[] firstLog = {KW_log};
	Kind[] firstCartX = {KW_cart_x};
	Kind[] firstCartY = {KW_cart_y};
	Kind[] firstPolarA = {KW_polar_a};
	Kind[] firstPolarR = {KW_polar_r};
	Kind[] firstIn = {KW_int};
	Kind[] firstFlo = {KW_float};
	Kind[] firstWidth = {KW_width};
	Kind[] firstHeight = {KW_height};
	Kind[] firstbool = {BOOLEAN_LITERAL};
	Kind[] firstlbrace = {LBRACE};
	
	public Block block() throws SyntaxException {
	//	System.out.println("Block");
		Token firstToken = t;
		List<ASTNode> dOrs = new ArrayList<ASTNode>();
		
		if(isKind(firstlbrace)) {
		//	System.out.println("Block in");
			match(LBRACE);
		//	System.out.println("LBRACE Checked");
			if((isKind(firstDec)|isKind(firstStatement))) {
			//	System.out.println("block ekdum under ayo");
				while (isKind(firstDec)|isKind(firstStatement)) {
					if (isKind(firstDec)) {
				//		System.out.println("going");
						dOrs.add(declaration());
			//			System.out.println("coming here");
					} 
					else if (isKind(firstStatement)) {
					//	System.out.println("first statement going");
						dOrs.add(statement());
					//	System.out.println("first statement going out");
					}
				match(SEMI);
				}
			}
			match(RBRACE);
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return new Block(firstToken,dOrs);
		
	}
	
	public Declaration declaration() throws SyntaxException {
	//	System.out.println("Declaration");
		Token first_T = t;
		Token type;
		Token name;
		Expression width = null;
		Expression height = null;
		Declaration d = null;
		if (isKind(firstType)){
			type = type();
			name = t;
			match(IDENTIFIER);
			d= new Declaration(first_T, type,name, width, height);           //Please Check
		}
		else if(isKind(firstImage)) {
			type =t;
			match(KW_image);
			name = t;
			match(IDENTIFIER);
	//		System.out.println("yes");
			if(isKind(firstPixelSelector)) {
		//		System.out.println("yes yessssssss");
				match(LSQUARE);
				width = expression();
				match(COMMA);
				height = expression();
				match(RSQUARE);	
				d = new Declaration(first_T,type,name,width,height);
				}															// Please Check else statement for error 
			d = new Declaration(first_T,type,name,width,height);
		}
		else {
		//throw new UnsupportedOperationException();
			throw new SyntaxException(t,"Syntax Error"+t.toString());
			}
		return d;
		
	}
	
	public Statement statement() throws SyntaxException {
	//	System.out.println("Statement");
		Statement s;
		if(isKind(firstInput)) {
			s= statementInput();
		}
		else if(isKind(firstWrite)) {
			s=statementWrite();
		}
		else if(isKind(firstAssignment)) {
			s=statementAssignment();
		}
		else if(isKind(firstWhile)) {
			s=statementWhile();
		}
		else if(isKind(firstIf)) {
	//		System.out.println("first statement  if going");
			s=statementIf();
	//		System.out.println("first statement  if going out");
		}
		else if(isKind(firstShow)) {
			s=statementShow();
		}
		else if(isKind(firstSleep)) {
			s=statementSleep();
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return s;
	}
	
	public Token type() throws SyntaxException {
		//System.out.println("Type");
		Token type = null;
		if(isKind(firstIn) | isKind(firstFlo) | isKind(firstBoolean) | isKind(firstFilename)) {
			if (isKind(firstIn)) {
				type =t;
				match(KW_int);
			}
			else if(isKind(firstFlo)) {
				type =t;
				match(KW_float);
			}
			else if (isKind(firstBoolean)) {
				type =t;
				match(KW_boolean);
			}
			else if(isKind(firstFilename)) {
				type =t;
				match(KW_filename);
			}
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return type;
		
	}
	
	public StatementInput statementInput() throws SyntaxException {
	//System.out.println(("statementInput"));
		Token first_T = t;
		StatementInput sInput = null;
		if(isKind(firstInput)) {
			match(KW_input);
			Token destName = t;
			match(IDENTIFIER);
			match(KW_from);
			match(OP_AT);
			Expression e = expression();
			sInput = new StatementInput(first_T,destName,e);
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return sInput;
	}
	
	public StatementWrite statementWrite() throws SyntaxException {
	//System.out.println(("statementWrite"));
		Token first_T = t;
		Token sourceName = null;
		Token destName = null;
		StatementWrite sWrite = null;
		
		if(isKind(firstWrite)) {
			match(KW_write);
			sourceName = t;
			match(IDENTIFIER);
			match(KW_to);
			destName = t;
			match(IDENTIFIER);
			sWrite = new StatementWrite(first_T,sourceName, destName);
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return sWrite;
	}
	
	
	public StatementAssign statementAssignment() throws SyntaxException {
		//System.out.println(("statementAssignment"));
		Token first_T = t;
		LHS lhs = null;
		Expression e = null;
		StatementAssign sAssign = null;
		if(isKind(firstAssignment)) {
			lhs = LHS();													   // Please check this  
			match(OP_ASSIGN);
			e = expression();
			sAssign = new StatementAssign(first_T,lhs,e);
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return sAssign;
		
	}
	
	public StatementWhile statementWhile() throws SyntaxException {
	//System.out.println(("statementWhile"));
		Token first_T = t;
		Expression guard = null;
		Block b = null;
		StatementWhile sWhile = null;
		if(isKind(firstWhile)) {
			match(KW_while);
			match(LPAREN);
			guard = expression();
			match(RPAREN);
			b = block();
			sWhile = new StatementWhile(first_T, guard, b);
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return sWhile;
	}
	
	public StatementIf statementIf() throws SyntaxException {
	//System.out.println(("statementIf"));
		Token first_T = t;
		Expression guard = null;
		Block b = null;
		StatementIf sIf = null; 
		
		if(isKind(firstIf)) {
			//System.out.println(("statementIf going in"));
			match(KW_if);
			match(LPAREN);
			guard = expression();
			//System.out.println(("expression compile kar liya"));
			match(RPAREN);
			b= block();
			sIf = new StatementIf(first_T, guard,b);
			//System.out.println(("statementIf going out"));
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return sIf;
	}
	
	public StatementShow statementShow() throws SyntaxException {
		//System.out.println(("statementShow"));
		Token firstToken = t;
		Expression e=  null;
		StatementShow sShow = null;
		if(isKind(firstShow)) {
			match(KW_show);
			e= expression();
			sShow = new StatementShow(firstToken, e);
			
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return sShow;
	}
	
	public StatementSleep statementSleep() throws SyntaxException {
	//System.out.println(("statementSleep"));
		Token firstToken = t;
		Expression e = null;
		StatementSleep sSleep =  null;
		if(isKind(firstSleep)) {
			match(KW_sleep);
			e = expression();
			sSleep =new StatementSleep(firstToken, e);
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return sSleep;
	}
	
	public Expression expression() throws SyntaxException{     // Please check this once
		Token firstToken = t;
		Expression ex0=null;
		Expression ex1=null;
		Expression ex2=null;
		Expression ex;
		if(isKind(firstExpression)) {
			ex0 = OrExpression();										// Check this once
			if(isKind(firstQuestion)) {
				match(OP_QUESTION);
				ex1 = expression();
				match(OP_COLON);
				ex2 = expression();
				ex = new ExpressionConditional(firstToken,ex0,ex1,ex2);
			}
			else {
				ex=ex0;
			}
			
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return ex;
	}
	
	public Expression OrExpression() throws SyntaxException{
		//System.out.println(("Orexprseeion"));
		Token firstToken = t;
		Expression leftExpression = null;
		Token op;
		Expression rightExpression = null;
		if(isKind(firstExpression)){
			leftExpression = AndExpression();
			while(isKind(firstOr)) {
				op = t;
				match(OP_OR);
				rightExpression = AndExpression();
				leftExpression = new ExpressionBinary(firstToken,leftExpression,op,rightExpression);
			}
			return leftExpression;	
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
	}
	
	public Expression AndExpression()throws SyntaxException{
		//System.out.println(("andexprseeion"));
		Token firstToken = t;
		Expression leftExpression = null;
		Token op;
		Expression rightExpression = null;
		if(isKind(firstExpression)) {
			leftExpression = EqExpression();
			while(isKind(firstAnd)) {
				op = t; 
				match(OP_AND);
				rightExpression = EqExpression();
				leftExpression = new ExpressionBinary(firstToken,leftExpression,op, rightExpression);
			}
			return leftExpression;
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		//System.out.println(("andexprseeion out"));
	}
	
	public Expression EqExpression() throws SyntaxException{
	//	System.out.println(("eqexprseeion"));
		Token firstToken = t;
		Expression lEx = null;
		Token op=null;
		Expression rEx = null;
		if(isKind(firstExpression)) {
			lEx = RelExpression();
			while(isKind(firstEqual) | isKind(firstNotEqual)) {
			//	System.out.println(("eqexprseeion under gaya"));
				if(isKind(firstEqual)) {
					op =t;
					match(OP_EQ);
				}
				else if(isKind(firstNotEqual)) {
					op = t;
					match(OP_NEQ);
				}
				rEx = RelExpression();
				lEx = new ExpressionBinary(firstToken,lEx,op,rEx);
			}
			return lEx;
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
	//	System.out.println(("eqexprseeion out"));
	}
	
	public Expression RelExpression() throws SyntaxException{
//	System.out.println(("relexprseeion"));
		Token firstToken = t;
		Expression lEx = null;
		Token op=null;
		Expression rEx = null;
		if(isKind(firstExpression)) {
			//System.out.println(("relexprseeion enter"));
			lEx = AddExpression();
			while(isKind(firstLT) | isKind(firstGT) | isKind(firstLE) | isKind(firstGE)) {
				if(isKind(firstLT)) {
					op = t;
					match(OP_LT);
				}
				else if(isKind(firstGT)) {
					op = t;
					match(OP_GT);
				}
				else if(isKind(firstLE)) {
					op = t;
					match(OP_LE);
				}
				else if(isKind(firstGE)) {
					op = t;
					match(OP_GE);
				}
				rEx = AddExpression();
				lEx = new ExpressionBinary(firstToken,lEx,op,rEx);
			}
			return lEx;
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
//	System.out.println(("relexprseeion out"));
	}
	
	public Expression AddExpression() throws SyntaxException{
	//System.out.println(("addexprseeion"));
		Token firstToken = t;
		Expression lEx = null;
		Token op=null;
		Expression rEx = null;
		if(isKind(firstExpression)) {
			lEx= MultExpression();
			while(isKind(firstPlus) | isKind(firstMinus)) {
				if(isKind(firstPlus)) {
					op = t;
					match(OP_PLUS);
				}
				else if(isKind(firstMinus)) {
					op = t;
					match(OP_MINUS);
				}
				rEx = MultExpression();
				lEx = new ExpressionBinary(firstToken, lEx, op, rEx);
			}
			return lEx;
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
	}
	
	public Expression MultExpression() throws SyntaxException{
		//System.out.println(("multexprseeion"));
		Token firstToken = t;
		Expression lEx = null;
		Token op=null;
		Expression rEx = null;
		//Expression exB=null;
		if(isKind(firstExpression)) {
			lEx = PowerExpression();
			while(isKind(firstMul) | isKind(firstDivide) | isKind(firstMod)) {
				if(isKind(firstMul)) {
					op =t;
					match(OP_TIMES);
				}
				else if(isKind(firstDivide)) {
					op= t;
					match(OP_DIV);
				}
				else if(isKind(firstMod)) {
					op=t;
					match(OP_MOD);
				}
				rEx = PowerExpression();
				lEx= new ExpressionBinary(firstToken,lEx,op,rEx);
			}
			return lEx;
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
	}
	
	public Expression PowerExpression() throws SyntaxException{
//	System.out.println(("powerexprseeion"));
		Token firstToken = t;
		Expression lEx = null;
		Token op=null;
		Expression rEx = null;
		Expression exB=null;
		if(isKind(firstExpression)) {
			lEx= UnaryExpression();
			if(isKind(firstPower)) {
				op =t;
				match(OP_POWER);
			rEx= PowerExpression();
			exB = new ExpressionBinary(firstToken,lEx,op,rEx);
			}
			else {
				exB = lEx;		
			}
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return exB;
	}
	
	public Expression UnaryExpression() throws SyntaxException{
//	System.out.println(("unaryexprseeion"));
		Token firstToken = t;
		Token op;
		Expression ex = null;
		Expression exU=null;
		if(isKind(firstExpression)) {
			if(isKind(firstPlus)) {
				op = t;
				match(OP_PLUS);
				ex = UnaryExpression();
				exU = new ExpressionUnary(firstToken,op,ex);
			}
			else if(isKind(firstMinus)) {
				op = t;
				match(OP_MINUS);
				ex = UnaryExpression();
				exU = new ExpressionUnary(firstToken,op,ex);
			}
			else if(isKind(firstexpWithoutUnary)) {
		//	System.out.println("withoutunary");
				exU = UnaryExpressionNotPlusMinus();	//Please Check Once;
			}
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return exU;
	
	}
	
	public Expression UnaryExpressionNotPlusMinus() throws SyntaxException {			// Please check once
	//System.out.println(("unarynotplusminusexprseeion"));
		Token firstToken = t;
		Token op=null;
		Expression ex = null;
		Expression exU=null;
		if(isKind(firstExclamation) | isKind(firstPrimary)){
			if(isKind(firstExclamation)) {
				op = t;
				match(OP_EXCLAMATION);
				ex = UnaryExpression();
				exU = new ExpressionUnary(firstToken,op,ex);
			}
			else if(isKind(firstPrimary)) {
				exU = Primary();
			}
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return exU;
	}
	
	public Expression Primary() throws SyntaxException {				// Please check once
	//System.out.println(("primary"));
		Token firstToken = t;
		Token op;
		Token name;
		PixelSelector px=null;
		Expression ex = null;
		if(isKind(firstPrimary)) {
			if(isKind(firstInt)){
				op = t;
				match(INTEGER_LITERAL);
				ex = new ExpressionIntegerLiteral(t,op);
			}
			else if(isKind(firstbool)) {
				op = t;
				match(BOOLEAN_LITERAL);
				ex = new ExpressionBooleanLiteral(t,op);
			}
			else if(isKind(firstFloat)) {
				op = t;
				match(FLOAT_LITERAL);
				ex = new ExpressionFloatLiteral(t,op);
			}
			else if(isKind(firstLPAREN)) {
				match(LPAREN);
		//System.out.println("ghjgjhgjgjgjhgjgjhgj");
				ex = expression();
				match(RPAREN);
			}
			else if(isKind(firstFunctionApp)) {
		//	System.out.println("fun app yes");
				ex = FunctionApplication();
			//	System.out.println(("funapp yes yes "));
			}
			else if(isKind(firstIdentifier)) {
				name = t;
				match(IDENTIFIER);
				if (isKind(firstPixelSelector)) {
					 px = PixelSelector();
					 ex = new ExpressionPixel(firstToken,name,px);
					
				}
				else{
					ex = new ExpressionIdent(firstToken,name);
				}
				
				
			}
			else if(isKind(firstPredefinedName)) {
				ex = FirstPredefinedName();
			}
			else if(isKind(firstPixelConstructor)) {
	//		System.out.println(("pc innnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"));
				ex = FirstPixelConstructor();
		//	System.out.println(("pc out"));
			}
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return ex;
//	System.out.println(("primary out"));
	}
	
	public Expression FunctionApplication() throws SyntaxException {					//have to do
	//System.out.println(("funapp"));
		Token firstToken = t;
		Token function;
		Expression e0;
		Expression e1;
		Expression ex=null;
		
		if(isKind(firstFunctionApp)) {
			function = FunctionName();
			if(isKind(firstLPAREN)) {
				match(LPAREN);
				e0 = expression();
				match(RPAREN);
				ex = new ExpressionFunctionAppWithExpressionArg(firstToken,function, e0 );
	//		System.out.println(("funapp out"));
			}
			else if(isKind(firstPixelSelector)) {
				match(LSQUARE);
				e0=expression();
				match(COMMA);
				e1=expression();
				match(RSQUARE);
				ex = new ExpressionFunctionAppWithPixel(firstToken,function,e0,e1);
			}
			else {
				throw new SyntaxException(t,"Syntax Error"+t.toString());
			}
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return ex;
	//	System.out.println(("going completly out"));
	}
	
	public Token FunctionName() throws SyntaxException {
	//	System.out.println(("funname"));
		Token function = null;
		if(isKind(firstFunctionApp)) {
			if(isKind(firstSin)) {
				function =t;
				match(KW_sin);
			}
			else if(isKind(firstCos)) {
				function =t;
				match(KW_cos);
			}
			else if(isKind(firstAtan)) {
				function =t;
				match(KW_atan);
			}
			else if(isKind(firstAbs)) {
				function =t;
				match(KW_abs);
			}
			else if(isKind(firstLog)) {
				function =t;
				match(KW_log);
			}
			else if(isKind(firstCartX)) {
				function =t;
				match(KW_cart_x);
			}
			else if(isKind(firstCartY)) {
				function =t;
				match(KW_cart_y);
		//	System.out.println("match thae gayu");
			}
			else if(isKind(firstPolarA)) {
				function =t;
				match(KW_polar_a);
			}
			else if(isKind(firstPolarR)) {
				function =t;
				match(KW_polar_r);
			}
			else if(isKind(firstIn)) {
				function =t;
				match(KW_int);
			}
			else if(isKind(firstFlo)) {
				function =t;
				match(KW_float);
			}
			else if(isKind(firstWidth)) {
				function =t;
				match(KW_width);
			}
			else if(isKind(firstHeight)) {
				function =t;
				match(KW_height);
			}
			else if(isKind(firstColor)) {
				function = color();
			}
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return function;
		
	}
	
	public Expression FirstPredefinedName() throws SyntaxException {
//	System.out.println(("funprename"));
		Token firstToken = t;
		Token name;
		ExpressionPredefinedName exPre;
		
		if(isKind(firstZ)) {
			name = t;
			match(KW_Z);
		}
		else if(isKind(firstDefaultWidth)) {
			name = t;
			match(KW_default_width);
		}
		else if(isKind(firstDefaultHeight)) {
			name = t;
			match(KW_default_height);
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		exPre = new ExpressionPredefinedName(firstToken,name);
		return exPre;
	}
	
	public Expression FirstPixelConstructor() throws SyntaxException {
	//System.out.println(("firstpixlcons"));
		Token firstToken =t;
		
		Expression ex0=null;
		Expression ex1=null;
		Expression ex2=null;
		Expression ex3=null;
		ExpressionPixelConstructor exP = null;
		
		if(isKind(firstPixelConstructor)) {
			match(LPIXEL);
	//		System.out.println((" 1 ex"));
			ex0 = expression();
	//	System.out.println((" 1 ex out"));
			match(COMMA);
	//	System.out.println((" 2 ex"));
			ex1 = expression();
	//	System.out.println((" 2 ex out"));
			match(COMMA);
		//	System.out.println((" 3 ex"));
			ex2 = expression();
	//System.out.println((" 3 ex out"));
			match(COMMA);
	//	System.out.println((" 4 ex"));
			ex3 = expression();
	//	System.out.println((" 4 ex out"));
			match(RPIXEL);
			exP = new ExpressionPixelConstructor(firstToken,ex0,ex1,ex2,ex3);
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return exP;
	}
	
	public LHS LHS() throws SyntaxException{				// how to reutn multiple objects from 1 method
		Token firstToken = t;
		Token name =null;
		PixelSelector pixelSelector = null;
		LHSIdent lhsIden= null;
		LHSPixel lhsPixel = null;
		LHSSample lhsSample = null;
		Token color;
	System.out.println(("lhs"));
		if(isKind(firstIdentifier)) {
			name = t;
			match(IDENTIFIER);
			if(isKind(firstPixelSelector)) {
				pixelSelector  = PixelSelector();
				lhsPixel = new LHSPixel(firstToken,name,pixelSelector);
				return lhsPixel;
			}														//Check the if else part once;
			else {
				lhsIden = new LHSIdent(firstToken, name);
				return lhsIden;
			}
		}
		else if(isKind(firstColor)) {
			color = color();
			match(LPAREN);
			name = t;
			match(IDENTIFIER);
			pixelSelector= PixelSelector();
			match(RPAREN);
			lhsSample = new LHSSample(firstToken, name,pixelSelector,color);    //check once
			return lhsSample;
		}
		else {
			throw new SyntaxException(t,"Syntax Error in LHS"+t.toString());
		}
		
	}
	
	public Token color() throws SyntaxException {
	//	System.out.println(("color"));
		Token color;										//Why should we not intialize this
			if(isKind(firstRed)) {
				color =t;
				match(KW_red);
			}
			else if(isKind(firstGreen)) {
				color =t;
				match(KW_green);
			}
			else if(isKind(firstBlue)) {
				color =t;
				match(KW_blue);
			}
			else if(isKind(firstAlpha)) {
				color =t;
				match(KW_alpha);
			}
			else {
				throw new SyntaxException(t,"Syntax Error"+t.toString());
			}
			return color;
		}
	
	public PixelSelector PixelSelector() throws SyntaxException {
	//	System.out.println(("pixel select"));
		Token firstToken = t;
		Expression e0 = null;
		Expression e1= null;
		PixelSelector pSelect;                           // Why no error on intialization
		if(isKind(firstPixelSelector)) {
			
			match (LSQUARE);
			e0 = expression();
			match(COMMA);
			e1 = expression();
			match(RSQUARE);
			pSelect = new PixelSelector(firstToken,e0,e1);
		}
		else {
			throw new SyntaxException(t,"Syntax Error"+t.toString());
		}
		return pSelect;
	}
	
	
	
	
	
	
	
	
	

	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}


	/**
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		throw new SyntaxException(t,"Syntax Error"+t.toString()); //TODO  give a better error message!
	}


	private Token consume() throws SyntaxException {
	//	System.out.println(t);
		Token tmp = t;
		if (isKind( EOF)) {
			throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!  
			//Note that EOF should be matched by the matchEOF method which is called only in parse().  
			//Anywhere else is an error. */
		}
		t = scanner.nextToken();
		return tmp;
	}


	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (isKind(EOF)) {
			return t;
		}
		throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
	}
	

}

