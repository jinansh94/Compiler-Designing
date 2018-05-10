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
import cop5556sp18.Scanner.Kind;
import static cop5556sp18.Scanner.Kind.*;


public class SimpleParser {
	
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

	SimpleParser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}


	public void parse() throws SyntaxException {
		program();
		matchEOF();
	}

	/*
	 * Program ::= Identifier Block
	 */
	public void program() throws SyntaxException {
		//System.out.println("Program");
		match(IDENTIFIER);
		block();
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
	
	public void block() throws SyntaxException {
		//System.out.println("Block");
		match(LBRACE);
		while (isKind(firstDec)|isKind(firstStatement)) {
	     if (isKind(firstDec)) {
	    	 //System.out.println("going");
			declaration();
			//System.out.println("coming here");
		} else if (isKind(firstStatement)) {
			statement();
		}
			match(SEMI);
		}
		match(RBRACE);
	}
	
	public void declaration() throws SyntaxException {
		//System.out.println("Declaration");
		if (isKind(firstType)){
			type();
			match(IDENTIFIER);
		}
		else if(isKind(firstImage)) {
			match(KW_image);
			match(IDENTIFIER);
			//System.out.println("yes");
			if(isKind(firstPixelSelector)) {
				//System.out.println("yes yessssssss");
				match(LSQUARE);
				expression();
				match(COMMA);
				expression();
				match(RSQUARE);	
				}
//			else {
//																		//To Do
//			}
			}
		else {
		throw new UnsupportedOperationException();
		}
	}
	
	public void statement() throws SyntaxException {
		//System.out.println("Statement");
		if(isKind(firstInput)) {
			statementInput();
		}
		else if(isKind(firstWrite)) {
			statementWrite();
		}
		else if(isKind(firstAssignment)) {
			statementAssignment();
		}
		else if(isKind(firstWhile)) {
			statementWhile();
		}
		else if(isKind(firstIf)) {
			statementIf();
		}
		else if(isKind(firstShow)) {
			statementShow();
		}
		else if(isKind(firstSleep)) {
			statementSleep();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}
	
	public void type() throws SyntaxException {
		//System.out.println("Type");
		if(isKind(firstIn) | isKind(firstFlo) | isKind(firstBoolean) | isKind(firstFilename)) {
			if (isKind(firstIn)) {
				match(KW_int);
			}
			else if(isKind(firstFlo)) {
				match(KW_float);
			}
			else if (isKind(firstBoolean)) {
				match(KW_boolean);
			}
			else if(isKind(firstFilename)) {
				match(KW_filename);
			}
		}
		
	}
	
	public void statementInput() throws SyntaxException {
	//	System.out.println(("statementInput"));
		if(isKind(firstInput)) {
			match(KW_input);
			match(IDENTIFIER);
			match(KW_from);
			match(OP_AT);
			expression();
		}
	}
	
	public void statementWrite() throws SyntaxException {
	//	System.out.println(("statementWrite"));
		if(isKind(firstWrite)) {
			match(KW_write);
			match(IDENTIFIER);
			match(KW_to);
			match(IDENTIFIER);
		}
	}
	
	
	public void statementAssignment() throws SyntaxException {
		//System.out.println(("statementAssignment"));
		if(isKind(firstAssignment)) {
			LHS();
			match(OP_ASSIGN);
			expression();
		}
	}
	
	public void statementWhile() throws SyntaxException {
	//	System.out.println(("statementWhile"));
		if(isKind(firstWhile)) {
			match(KW_while);
			match(LPAREN);
			expression();
			match(RPAREN);
			block();
		}
	}
	
	public void statementIf() throws SyntaxException {
	//	System.out.println(("statementIf"));
		if(isKind(firstIf)) {
			match(KW_if);
			match(LPAREN);
			expression();
			match(RPAREN);
			block();
		}
	}
	
	public void statementShow() throws SyntaxException {
	//	System.out.println(("statementShow"));
		if(isKind(firstShow)) {
			match(KW_show);
			expression();
		}
	}
	
	public void statementSleep() throws SyntaxException {
	//	System.out.println(("statementSleep"));
		if(isKind(firstSleep)) {
			match(KW_sleep);
			expression();
		}
	}
	public void expression() throws SyntaxException{
	//	System.out.println(("expression"));
		if(isKind(firstExpression)) {
			OrExpression();
			if(isKind(firstQuestion)) {
				match(OP_QUESTION);
				expression();
				match(OP_COLON);
				expression();
			}
			else {
																	// To Do
			}
		}
		
	}
	
	public void OrExpression() throws SyntaxException{
		//System.out.println(("Orexprseeion"));
		if(isKind(firstExpression)){
			AndExpression();
			while(isKind(firstOr)) {
				match(OP_OR);
				AndExpression();
			}
			
		}
	}
	
	public void AndExpression()throws SyntaxException{
	//	System.out.println(("andexprseeion"));
		if(isKind(firstExpression)) {
			EqExpression();
			while(isKind(firstAnd)) {
				match(OP_AND);
				EqExpression();
			}
		}
	}
	
	public void EqExpression() throws SyntaxException{
		//System.out.println(("eqexprseeion"));
		if(isKind(firstExpression)) {
			RelExpression();
			while(isKind(firstEqual) | isKind(firstNotEqual)) {
				if(isKind(firstEqual)) {
					match(OP_EQ);
				}
				else if(isKind(firstNotEqual)) {
					match(OP_NEQ);
				}
				RelExpression();
			}
		}
	}
	
	public void RelExpression() throws SyntaxException{
	//	System.out.println(("relexprseeion"));
		if(isKind(firstExpression)) {
			AddExpression();
			while(isKind(firstLT) | isKind(firstGT) | isKind(firstLE) | isKind(firstGE)) {
				if(isKind(firstLT)) {
					match(OP_LT);
				}
				else if(isKind(firstGT)) {
					match(OP_GT);
				}
				else if(isKind(firstLE)) {
					match(OP_LE);
				}
				else if(isKind(firstGE)) {
					match(OP_GE);
				}
				AddExpression();
			}
		}
	}
	
	public void AddExpression() throws SyntaxException{
	//	System.out.println(("addexprseeion"));
		if(isKind(firstExpression)) {
			MultExpression();
			while(isKind(firstPlus) | isKind(firstMinus)) {
				if(isKind(firstPlus)) {
					match(OP_PLUS);
				}
				else if(isKind(firstMinus)) {
					match(OP_MINUS);
				}
				MultExpression();
			}
		}
	}
	
	public void MultExpression() throws SyntaxException{
	//	System.out.println(("multexprseeion"));
		if(isKind(firstExpression)) {
			PowerExpression();
			while(isKind(firstMul) | isKind(firstDivide) | isKind(firstMod)) {
				if(isKind(firstMul)) {
					match(OP_TIMES);
				}
				else if(isKind(firstDivide)) {
					match(OP_DIV);
				}
				else if(isKind(firstMod)) {
					match(OP_MOD);
				}
				PowerExpression();
			}
		}
	}
	
	public void PowerExpression() throws SyntaxException{
	//	System.out.println(("powerexprseeion"));
		if(isKind(firstExpression)) {
			UnaryExpression();
			if(isKind(firstPower)) {
				match(OP_POWER);
				PowerExpression();
			}
			else {
																	// To Do
			}
		}
	}
	
	public void UnaryExpression() throws SyntaxException{
	//	System.out.println(("unaryexprseeion"));
		if(isKind(firstExpression)) {
			if(isKind(firstPlus)) {
				match(OP_PLUS);
				UnaryExpression();
			}
			else if(isKind(firstMinus)) {
				match(OP_MINUS);
				UnaryExpression();
			}
			else if(isKind(firstexpWithoutUnary)) {
			//	System.out.println("withoutunary");
				UnaryExpressionNotPlusMinus();
			}
		}
	}
	
	public void UnaryExpressionNotPlusMinus() throws SyntaxException {
	//	System.out.println(("unarynotplusminusexprseeion"));
		if(isKind(firstExclamation) | isKind(firstPrimary)){
			if(isKind(firstExclamation)) {
				match(OP_EXCLAMATION);
				UnaryExpression();
			}
			else if(isKind(firstPrimary)) {
				Primary();
			}
		}
	}
	
	public void Primary() throws SyntaxException {
	//	System.out.println(("primary"));
		if(isKind(firstPrimary)) {
			if(isKind(firstInt)){
				match(INTEGER_LITERAL);
			}
			else if(isKind(firstbool)) {
				match(BOOLEAN_LITERAL);
			}
			else if(isKind(firstFloat)) {
				match(FLOAT_LITERAL);
			}
			else if(isKind(firstLPAREN)) {
				match(LPAREN);
		//		System.out.println("ghjgjhgjgjgjhgjgjhgj");
				expression();
				match(RPAREN);
			}
			else if(isKind(firstFunctionApp)) {
			//	System.out.println("fun app yes");
				FunctionApplication();
			//	System.out.println(("funapp yes yes "));
			}
			else if(isKind(firstIdentifier)) {
				match(IDENTIFIER);
				if (isKind(firstPixelSelector)) {
					PixelSelector();
				}
				else {
																		//To Do
				}
			}
			else if(isKind(firstPredefinedName)) {
				FirstPredefinedName();
			}
			else if(isKind(firstPixelConstructor)) {
		//		System.out.println(("pc innnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"));
				FirstPixelConstructor();
			//	System.out.println(("pc out"));
			}
		}
	//	System.out.println(("primary out"));
	}
	
	public void FunctionApplication() throws SyntaxException {
	//	System.out.println(("funapp"));
		if(isKind(firstFunctionApp)) {
			FunctionName();
			if(isKind(firstLPAREN)) {
				match(LPAREN);
				expression();
				match(RPAREN);
			//	System.out.println(("funapp out"));
			}
			else if(isKind(firstPixelSelector)) {
				match(LSQUARE);
				expression();
				match(COMMA);
				expression();
				match(RSQUARE);
			}
		}
	//	System.out.println(("going completly out"));
	}
	
	public void FunctionName() throws SyntaxException {
	//	System.out.println(("funname"));
		if(isKind(firstFunctionApp)) {
			if(isKind(firstSin)) {
				match(KW_sin);
			}
			else if(isKind(firstCos)) {
				match(KW_cos);
			}
			else if(isKind(firstAtan)) {
				match(KW_atan);
			}
			else if(isKind(firstAbs)) {
				match(KW_abs);
			}
			else if(isKind(firstLog)) {
				match(KW_log);
			}
			else if(isKind(firstCartX)) {
				match(KW_cart_x);
			}
			else if(isKind(firstCartY)) {
				match(KW_cart_y);
		//		System.out.println("match thae gayu");
			}
			else if(isKind(firstPolarA)) {
				match(KW_polar_a);
			}
			else if(isKind(firstPolarR)) {
				match(KW_polar_r);
			}
			else if(isKind(firstIn)) {
				match(KW_int);
			}
			else if(isKind(firstFlo)) {
				match(KW_float);
			}
			else if(isKind(firstWidth)) {
				match(KW_width);
			}
			else if(isKind(firstHeight)) {
				match(KW_height);
			}
			else if(isKind(firstColor)) {
				color();
			}
		}
		
	}
	
	public void FirstPredefinedName() throws SyntaxException {
	//	System.out.println(("funprename"));
		if(isKind(firstZ)) {
			match(KW_Z);
		}
		else if(isKind(firstDefaultWidth)) {
			match(KW_default_width);
		}
		else if(isKind(firstDefaultHeight)) {
			match(KW_default_height);
		}
	}
	
	public void FirstPixelConstructor() throws SyntaxException {
	//	System.out.println(("firstpixlcons"));
		if(isKind(firstPixelConstructor)) {
			match(LPIXEL);
		//	System.out.println((" 1 ex"));
			expression();
		//	System.out.println((" 1 ex out"));
			match(COMMA);
		//	System.out.println((" 2 ex"));
			expression();
	//		System.out.println((" 2 ex out"));
			match(COMMA);
		//	System.out.println((" 3 ex"));
			expression();
	//		System.out.println((" 3 ex out"));
			match(COMMA);
	//		System.out.println((" 4 ex"));
			expression();
	//		System.out.println((" 4 ex out"));
			match(RPIXEL);
		}
	}
	
	public void LHS() throws SyntaxException{
//		System.out.println(("lhs"));
		if(isKind(firstIdentifier)) {
			match(IDENTIFIER);
			if(isKind(firstPixelSelector)) {
				PixelSelector();
			}
			else {													// TO Do
				
			}
		}
		else if(isKind(firstColor)) {
			color();
			match(LPAREN);
			match(IDENTIFIER);
			PixelSelector();
			match(RPAREN);
		}
	}
	
	public void color() throws SyntaxException {
	//	System.out.println(("color"));
			if(isKind(firstRed)) {
				match(KW_red);
			}
			else if(isKind(firstGreen)) {
				match(KW_green);
			}
			else if(isKind(firstBlue)) {
				match(KW_blue);
			}
			else if(isKind(firstAlpha)) {
				match(KW_alpha);
			}
		}
	
	public void PixelSelector() throws SyntaxException {
	//	System.out.println(("pixel select"));
		if(isKind(firstPixelSelector)) {
			
			match (LSQUARE);
			expression();
			match(COMMA);
			expression();
			match(RSQUARE);
		}
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
		//System.out.println(t);
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

