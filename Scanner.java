/*
* Initial code for the Scanner for the class project in COP5556 Programming Language Principles 
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

package cop5556sp18;

import java.util.ArrayList;
import java.util.Arrays;

//import java.util.HashMap;

public class Scanner {

	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {

		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}

		public int getPos() {
			return pos;
		}
	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, FLOAT_LITERAL,
		KW_Z/* Z */, KW_default_width/* default_width */, KW_default_height/* default_height */, 
		KW_width /* width */, KW_height /* height*/, KW_show/*show*/, KW_write /* write */, KW_to /* to */,
		KW_input /* input */, KW_from /* from */, KW_cart_x/* cart_x*/, KW_cart_y/* cart_y */, 
		KW_polar_a/* polar_a*/, KW_polar_r/* polar_r*/, KW_abs/* abs */, KW_sin/* sin*/, KW_cos/* cos */, 
		KW_atan/* atan */, KW_log/* log */, KW_image/* image */, KW_int/* int */, KW_float /* float */, 
		KW_boolean/* boolean */, KW_filename/* filename */, KW_red /* red */, KW_blue /* blue */, 
		KW_green /* green */, KW_alpha /* alpha*/, KW_while /* while */, KW_if /* if */,KW_sleep, OP_ASSIGN/* := */, 
		OP_EXCLAMATION/* ! */, OP_QUESTION/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, 
		OP_GE/* >= */, OP_LE/* <= */, OP_GT/* > */, OP_LT/* < */, OP_AND/* & */, OP_OR/* | */, 
		OP_PLUS/* +*/, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, OP_POWER/* ** */, 
		OP_AT/* @ */, LPAREN/*( */, RPAREN/* ) */, LSQUARE/* [ */, RSQUARE/* ] */, LBRACE /*{ */, 
		RBRACE /* } */, LPIXEL /* << */, RPIXEL /* >> */, SEMI/* ; */, COMMA/* , */, DOT /* . */, EOF;
	}

	/**
	 * Class to represent Tokens.
	 * 
	 * This is defined as a (non-static) inner class which means that each Token
	 * instance is associated with a specific Scanner instance. We use this when
	 * some token methods access the chars array in the associated Scanner.
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos; // position of first character of this token in the input. Counting starts at 0
								// and is incremented for every character.
		public final int length; // number of characters in this token

		public Token(Kind kind, int pos, int length) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		public String getText() {
			return String.copyValueOf(chars, pos, length);
		}

		/**
		 * precondition: This Token's kind is INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		/**
		 * precondition: This Token's kind is FLOAT_LITERAL]
		 * 
		 * @returns the float value represented by the token
		 */
		public float floatVal() {
			assert kind == Kind.FLOAT_LITERAL;
			return Float.valueOf(String.copyValueOf(chars, pos, length));
		}

		/**
		 * precondition: This Token's kind is BOOLEAN_LITERAL
		 * 
		 * @returns the boolean value represented by the token
		 */
		public boolean booleanVal() {
			assert kind == Kind.BOOLEAN_LITERAL;
			return getText().equals("true");
		}

		/**
		 * Calculates and returns the line on which this token resides. The first line
		 * in the source code is line 1.
		 * 
		 * @return line number of this Token in the input.
		 */
		public int line() {
			return Scanner.this.line(pos) + 1;
		}

		/**
		 * Returns position in line of this token.
		 * 
		 * @param line.
		 *            The line number (starting at 1) for this token, i.e. the value
		 *            returned from Token.line()
		 * @return
		 */
		public int posInLine(int line) {
			return Scanner.this.posInLine(pos, line - 1) + 1;
		}

		/**
		 * Returns the position in the line of this Token in the input. Characters start
		 * counting at 1. Line termination characters belong to the preceding line.
		 * 
		 * @return
		 */
		public int posInLine() {
			return Scanner.this.posInLine(pos) + 1;
		}

		public String toString() {
			int line = line();
			return "[" + kind + "," + String.copyValueOf(chars, pos, length) + "," + pos + "," + length + "," + line
					+ "," + posInLine(line) + "]";
		}

		/**
		 * Since we override equals, we need to override hashCode, too.
		 * 
		 * See
		 * https://docs.oracle.com/javase/9/docs/api/java/lang/Object.html#hashCode--
		 * where it says, "If two objects are equal according to the equals(Object)
		 * method, then calling the hashCode method on each of the two objects must
		 * produce the same integer result."
		 * 
		 * This method, along with equals, was generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + pos;
			return result;
		}

		/**
		 * Override equals so that two Tokens are equal if they have the same Kind, pos,
		 * and length.
		 * 
		 * This method, along with hashcode, was generated by eclipse.
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (pos != other.pos)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is associated with.
		 * 
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}// Token

	/**
	 * Array of positions of beginning of lines. lineStarts[k] is the pos of the
	 * first character in line k (starting at 0).
	 * 
	 * If the input is empty, the chars array will have one element, the synthetic
	 * EOFChar token and lineStarts will have size 1 with lineStarts[0] = 0;
	 */
	int[] lineStarts;

	int[] initLineStarts() {
		ArrayList<Integer> lineStarts = new ArrayList<Integer>();
		int pos = 0;

		for (pos = 0; pos < chars.length; pos++) {
			lineStarts.add(pos);
			char ch = chars[pos];
			while (ch != EOFChar && ch != '\n' && ch != '\r') {
				pos++;
				ch = chars[pos];
			}
			if (ch == '\r' && chars[pos + 1] == '\n') {
				pos++;
			}
		}
		// convert arrayList<Integer> to int[]
		return lineStarts.stream().mapToInt(Integer::valueOf).toArray();
	}

	int line(int pos) {
		int line = Arrays.binarySearch(lineStarts, pos);
		if (line < 0) {
			line = -line - 2;
		}
		return line;
	}

	public int posInLine(int pos, int line) {
		return pos - lineStarts[line];
	}

	public int posInLine(int pos) {
		int line = line(pos);
		return posInLine(pos, line);
	}

	/**
	 * Sentinal character added to the end of the input characters.
	 */
	static final char EOFChar = 128;

	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;

	/**
	 * An array of characters representing the input. These are the characters from
	 * the input string plus an additional EOFchar at the end.
	 */
	final char[] chars;

	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFChar;
		tokens = new ArrayList<Token>();
		lineStarts = initLineStarts();
	}




	 private enum State {START,S_ZERO,S_COMMENT,S_IDENT,S_OP_EQ, S_EQL_EQL, S_STAR, S_FLOAT, S_INT, S_UNDEFINE};  //TODO:  this is incomplete

	 StringBuilder checkKeyword = new StringBuilder();
	 StringBuilder checkInteger = new StringBuilder();
	 StringBuilder checkFloat = new StringBuilder();
	 //TODO: Modify this to deal with the entire lexical specification
	public Scanner scan() throws LexicalException {
		int pos = 0;
		State state = State.START;
		int startPos = 0;
		char op_check = '0', int_check='0';
		boolean comment = false;
		boolean dot_op_b = false;
		while (pos < chars.length) {
			char ch = chars[pos];
			switch(state) {
				case START: {
					startPos = pos;
					switch (ch) {
						case ' ':
						case '\n':
						case '\r':
						case '\t':
						case '\f': {
							pos++;
						}
						break;
						case EOFChar: {
							tokens.add(new Token(Kind.EOF, startPos, 0));
							pos++; // next iteration will terminate loop
						}
						break;
						case ';': {
							tokens.add(new Token(Kind.SEMI, startPos, pos - startPos + 1));
							pos++;
						}
						break;
						
						case '(': {
							tokens.add(new Token(Kind.LPAREN, startPos, pos - startPos + 1));
							pos++;
						}
						break;
						
						case ')': {
							tokens.add(new Token(Kind.RPAREN, startPos, pos - startPos + 1));
							pos++;
						}
						break;
						
						case '[': {
							tokens.add(new Token(Kind.LSQUARE, startPos, pos - startPos + 1));
							pos++;
						}
						break;
						
						case ']': {
							tokens.add(new Token(Kind.RSQUARE, startPos, pos - startPos + 1));
							pos++;
						}
						break;
						
						case '{': {
							tokens.add(new Token(Kind.LBRACE, startPos, pos - startPos + 1));
							pos++;
						}
						break;
						
						case '}': {
							tokens.add(new Token(Kind.RBRACE, startPos, pos - startPos + 1));
							pos++;
						}
						break;
						
						case ',': {
							tokens.add(new Token(Kind.COMMA, startPos, pos - startPos + 1));
							pos++;
						}
						break;
						
						case '?': {
							tokens.add(new Token(Kind.OP_QUESTION, startPos, pos-startPos + 1));
							pos++;
						}
						break;
						case '&': {
							tokens.add(new Token(Kind.OP_AND, startPos, pos-startPos + 1));
							pos++;
						}
						break;
						case '|': {
							tokens.add(new Token(Kind.OP_OR, startPos, pos-startPos + 1));
							pos++;
						}
						break;
						case '+': {
							tokens.add(new Token(Kind.OP_PLUS, startPos, pos-startPos + 1));
							pos++;
						}
						break;
						case '-':  {
							tokens.add(new Token(Kind.OP_MINUS, startPos, pos-startPos + 1));
							pos++;
						}
						break;
						case '/': {	
							int d= pos +1;
							if (chars[d] == '*') {
								comment = true;
								state = State.S_COMMENT;
								pos++;
							}
							else {
								tokens.add(new Token(Kind.OP_DIV, startPos, pos-startPos + 1));
								pos++;
							}
						}
						break;
						case '%': {
							tokens.add(new Token(Kind.OP_MOD, startPos, pos-startPos + 1));
							pos++;
						}
						break;
						case '@': {
							tokens.add(new Token(Kind.OP_AT, startPos, pos-startPos + 1));
							pos++;
						}
						break;
						
						case '.':{
				//			System.out.println("dot entered");
							checkFloat.setLength(0);
							state=State.S_FLOAT;
							pos++;
							checkFloat.append(ch);
							dot_op_b=true;
							int_check =ch;
						}
						break;
						
						case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i': case 'j': case 'k': case 'l': case 'm':case 'n':case 'o': case 'p': case 'q': case 'r': case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z': case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': case 'M':case 'N':case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': case 'Y': 
						case 'Z': {
							state = State.S_IDENT;
							checkKeyword.setLength(0);
							pos++;
							checkKeyword.append(ch);
						}
						break;
						
						case'0':{
		//					System.out.println("0 detect");
							state=State.S_ZERO;
							checkFloat.setLength(0);
							int_check=ch;
							pos++;
							checkFloat.append(ch);
						}
						break;
						
						case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': 
						case '9': {
							state = State.S_INT;
							checkInteger.setLength(0);
							checkFloat.setLength(0);
							pos++;
							int_check = ch;
			//				System.out.println(ch);
							checkInteger.append(ch);
							checkFloat.append(ch);
						}
						break;
						
						case '>': case '<': case '!': 
							case ':': {
							state = State.S_OP_EQ;
							op_check = ch;
							pos++;
						}
						break;
							
						case '*': {
							//int star_d=pos;
							state = State.S_STAR;
							op_check=ch;
							pos++;
						}
						break;
						
						case '=': {
							state = State.S_EQL_EQL;
							op_check=ch;
							pos++;
		//					System.out.println("====== me ja raha he ");
						}
						break;
												
						default: {
							error(pos, line(pos), posInLine(pos), "illegal char");
						}
					}
				}
				break;
				
				case S_COMMENT:{
					pos++;
					int pos_d =pos;
					
					while(pos_d < chars.length-1) {
						char ch_d = chars[pos_d];
						int x= pos_d+1;
					
						if (ch_d =='*' && chars[x]=='/' ) {
						pos_d++;
						pos_d++;
						pos=pos_d;
						comment = false;
						state=State.START;
						break;
						}
						
						pos_d++;
					}
			
					if (comment == true) {
						pos=pos_d;
						error(pos_d,line(pos_d), posInLine(pos_d), "unexpected EOF in comment at "+pos_d);
					}
					
				}
				break;
				
				case S_FLOAT:{
				
					System.out.println("float ma ave che aheyaaaaaaaaa");
					
					if (isNum(ch)) {
						pos++;
					//	System.out.println(ch);
						dot_op_b=false;
						checkFloat.append(ch);
						int_check  = ch;
					}
					else if (ch=='.' && dot_op_b==true) {
						System.out.println("aheya keve rite ave che alu?");
						tokens.add(new Token(Kind.DOT,startPos,1));
						state=State.START;
						dot_op_b=false;
					}
					else if(ch!='.' && !isNum(ch) && !isNum(int_check) && int_check=='.'){
						tokens.add(new Token(Kind.DOT,startPos,pos-startPos));
						state=State.START;
						dot_op_b=false;
						//dot_op_b=false;
					}
					else {
						try {
							float float_d = Float.parseFloat((checkFloat.toString()));
							if (Float.isFinite(float_d)) {
								tokens.add(new Token(Kind.FLOAT_LITERAL,startPos,pos-startPos));
								state=State.START;
								}
								else {
									error(startPos,line(pos), posInLine(pos),"float literal is out of float range at "+startPos);
								}
							}
							catch(NumberFormatException e) {
								error(startPos,line(pos), posInLine(pos),"float literal is out of float range at "+startPos);
							}
					}	
				}
				break;
				
				case S_ZERO:{
					if(ch == '.') {
						state=State.S_FLOAT;
						checkFloat.append(ch);
						pos++;
					}
					else {
					tokens.add(new Token(Kind.INTEGER_LITERAL,startPos,1));
					state=State.START;
					}
				}
				break;
				
				case S_INT:{
							if (isNum(ch)) {
								pos++;
								checkInteger.append(ch);
								checkFloat.append(ch);
							}
							else if (ch=='.') {
								state=State.S_FLOAT;
								checkFloat.append(ch);
								System.out.println(pos);
								pos++;
								System.out.println("ave che aheya");
							}
							else {
								try {
								long int_d = Long.parseLong((checkInteger.toString()));
								if (int_d<= Integer.MAX_VALUE && int_d >= Integer.MIN_VALUE) {
									tokens.add(new Token(Kind.INTEGER_LITERAL,startPos,pos-startPos));
									state = State.START;}
									else 
									{
										pos = startPos;
										error(pos,line(pos), posInLine(pos),"Number Literal is out of integer range at " + startPos);
									}
								}
								catch(NumberFormatException e) {
									pos = startPos;
									error(pos,line(pos), posInLine(pos),"Number Literal is out of integer range at " + startPos);
								}
								
							}
				}
				break;
				
				case S_STAR: {
					if (op_check == '*' && ch == '*') {
						tokens.add(new Token(Kind.OP_POWER,startPos,pos-startPos+1));
						state = State.START;
						pos++;		
					}
					else if(op_check == '*' && ch !='*') {
						tokens.add(new Token(Kind.OP_TIMES,startPos,pos-startPos));
						state=State.START;
					}
				}
				break;
				
				case S_EQL_EQL: {
					if (op_check == '=' && ch == '=') {
						tokens.add(new Token(Kind.OP_EQ,startPos,pos-startPos+1));
						state = State.START;
						pos++;		
					}
					else if(op_check == '=' && ch !='=') {
						error(pos,line(pos), posInLine(pos),"= expected at "+pos);
						
					}
				}
				break;
				
				case S_IDENT: {
					if(isValidIdentifier(ch)) {
						pos++;
						checkKeyword.append(ch);
					}
					else if (isKeyword(checkKeyword,startPos,pos)){
						state=State.START;
					}
					else if (checkKeyword.toString().equals("true") || checkKeyword.toString().equals("false")){
						
						tokens.add(new Token(Kind.BOOLEAN_LITERAL,startPos,pos-startPos));
						state=State.START;
					}
					else {
						tokens.add(new Token(Kind.IDENTIFIER,startPos,pos-startPos));
						state=State.START;
					}
				}
				break;
				
				case S_OP_EQ: {			
					if(ch == '=') {
							switch(op_check) {
								case('>'):{
									tokens.add(new Token(Kind.OP_GE, startPos, pos-startPos+1));
									state=State.START;
									pos++;
								}
								break;
								case('<'):{
									tokens.add(new Token(Kind.OP_LE, startPos, pos-startPos+1));
									state=State.START;
									pos++;
								}
								break;
								case('!'):{
									tokens.add(new Token(Kind.OP_NEQ, startPos, pos-startPos+1));
									state=State.START;
									pos++;
								}
								break;
								case(':'):{
									tokens.add(new Token(Kind.OP_ASSIGN, startPos, pos-startPos+1));
									state=State.START;
									pos++;
								}
								break;
							}
					}
					
					else if(ch == '>') {
						if (op_check == '>' && ch == '>') {
							tokens.add(new Token(Kind.RPIXEL,startPos, pos-startPos+1));
							state=State.START;
							pos++;
						}
						else if (op_check !='>' && ch=='>') {
							switch(op_check) {
								case ('<'):{
									tokens.add(new Token(Kind.OP_LT,startPos,1));
									tokens.add(new Token(Kind.OP_GT,startPos,1));
									state=State.START;
									pos++;
								}
								break;
								case ('!'):{
									tokens.add(new Token(Kind.OP_EXCLAMATION,startPos,1));
									tokens.add(new Token(Kind.OP_GT,startPos,1));
									state=State.START;
									pos++;
								}
								break;
								case (':'):{
									tokens.add(new Token(Kind.OP_COLON,startPos,1));
									tokens.add(new Token(Kind.OP_GT,startPos,1));
									state=State.START;
									pos++;
								}
								break;
							}
						}	
					}
					
					else if(ch == '<') {
						if (op_check == '<' && ch == '<') {
							tokens.add(new Token(Kind.LPIXEL,startPos, pos-startPos+1));
							state=State.START;
							pos++;
						}
						else if (op_check !='<' && ch=='<') {
							switch(op_check) {
								case ('>'):{
									tokens.add(new Token(Kind.OP_GT,startPos,1));
									tokens.add(new Token(Kind.OP_LT,startPos,1));
									state=State.START;
									pos++;
								}
								break;
								case ('!'):{
									tokens.add(new Token(Kind.OP_EXCLAMATION,startPos,1));
									tokens.add(new Token(Kind.OP_LT,startPos,1));
									state=State.START;
									pos++;
								}
								break;
								case (':'):{
									tokens.add(new Token(Kind.OP_COLON,startPos,1));
									tokens.add(new Token(Kind.OP_LT,startPos,1));
									state=State.START;
									pos++;
								}
								break;
							}
						}	
					}
					
					else {
						switch(op_check) {
							case('>'): {
								tokens.add(new Token(Kind.OP_GT,startPos,1));
								state = State.START;
							}
							break;
							case('<'): {
								tokens.add(new Token(Kind.OP_LT,startPos,1));
								state = State.START;
							}
							break;
							case('!'): {
								tokens.add(new Token(Kind.OP_EXCLAMATION,startPos,1));
								state = State.START;
							}
							break;
							case(':'): {
								tokens.add(new Token(Kind.OP_COLON,startPos,1));
								state = State.START;
							}
							break;
							default :{
								state = State.START;
							}
						}
					}
						
					
				}
				break;
				
				default: {
					error(pos, line(pos), posInLine(pos), "undefined state");
				}
			}
		} 
		return this;
	}
	
	public boolean isNum(char x) {
		if (x == '0' || x =='1' ||x == '2' || x =='3' ||x == '4' || x =='5' ||x == '6' || x =='7' ||x == '8' || x =='9' ) {
			return true;
		}
		else return false;
	}
	
	public boolean isValidIdentifier(char x) {
		if (x == 'a' || x =='b' || x == 'c' || x =='d' ||x == 'e' || x =='f' ||x == 'g' || x =='h' ||x == 'i' || x =='j' ||x == 'k' || x =='l' ||x == 'm' || x =='n' ||x == 'o' || x =='p' ||x == 'q' || x =='r' ||x == 's' || x =='t' ||x == 'u' || x =='v' ||x == 'w' || x =='x' ||x == 'y' || x =='z' ||x == 'A' || x =='B' ||x == 'C' || x =='D' ||x == 'E' || x =='F' ||x == 'G' || x =='H' ||x == 'I' || x =='J' ||x == 'K' || x =='L' ||x == 'M' || x =='N' ||x == 'O' || x =='P' ||x == 'Q' || x =='R' ||x == 'S' || x =='T' ||x == 'U' || x =='V' ||x == 'W' || x =='X' ||x == 'Y' || x =='Z' ||x == '$' || x =='_' || x == '0' || x =='1' ||x == '2' || x =='3' ||x == '4' || x =='5' ||x == '6' || x =='7' ||x == '8' || x =='9' ){
			return true;	
		}
		else return false;
	}
	
	public boolean isKeyword(StringBuilder c1,int sp1, int p1) {
		String dummy = c1.toString();
		switch (dummy) {
			case("Z"):{
				tokens.add(new Token(Kind.KW_Z,sp1,p1-sp1));
				return true;
			}
			case("default_width"):{
				tokens.add(new Token(Kind.KW_default_width,sp1,p1-sp1));
	            return true;  		
			}
			case("default_height"):{
				tokens.add(new Token(Kind.KW_default_height,sp1,p1-sp1));
				return true;
			}
			case("show"):{
				tokens.add(new Token(Kind.KW_show,sp1,p1-sp1));
				return true;
			}
			case("write"):{
				tokens.add(new Token(Kind.KW_write,sp1,p1-sp1));
				return true;
			}
			case("to"):{
				tokens.add(new Token(Kind.KW_to,sp1,p1-sp1));
				return true;
			}
			case("input"):{
				tokens.add(new Token(Kind.KW_input,sp1,p1-sp1));
				return true;
			}
			case("from"):{
				tokens.add(new Token(Kind.KW_from,sp1,p1-sp1));
				return true;
			}
			case("cart_x"):{
				tokens.add(new Token(Kind.KW_cart_x,sp1,p1-sp1));
				return true;
			}
			case("cart_y"):{
				tokens.add(new Token(Kind.KW_cart_y,sp1,p1-sp1));
				return true;
			}
			case("polar_a"):{
				tokens.add(new Token(Kind.KW_polar_a,sp1,p1-sp1));
				return true;
			}
			case("polar_r"):{
				tokens.add(new Token(Kind.KW_polar_r,sp1,p1-sp1));
				return true;
			}
			case("abs"):{
				tokens.add(new Token(Kind.KW_abs,sp1,p1-sp1));
				return true;
			}
			case("sin"):{
				tokens.add(new Token(Kind.KW_sin,sp1,p1-sp1));
				return true;
			}
			case("cos"):{
				tokens.add(new Token(Kind.KW_cos,sp1,p1-sp1));
				return true;
			}
			case("atan"):{
				tokens.add(new Token(Kind.KW_atan,sp1,p1-sp1));
				return true;
			}
			case("log"):{
				tokens.add(new Token(Kind.KW_log,sp1,p1-sp1));
				return true;
			}
			case("image"):{
				tokens.add(new Token(Kind.KW_image,sp1,p1-sp1));
				return true;
			}
			case("int"):{
				tokens.add(new Token(Kind.KW_int,sp1,p1-sp1));
				return true;
			}
			case("float"):{
				tokens.add(new Token(Kind.KW_float,sp1,p1-sp1));
				return true;
			}
			case("filename"):{
				tokens.add(new Token(Kind.KW_filename,sp1,p1-sp1));
				return true;
			}
			case("boolean"):{
				tokens.add(new Token(Kind.KW_boolean,sp1,p1-sp1));
				return true;
			}
			case("red"):{
				tokens.add(new Token(Kind.KW_red,sp1,p1-sp1));
				return true;
			}
			case("blue"):{
				tokens.add(new Token(Kind.KW_blue,sp1,p1-sp1));
				return true;
			}
			case("green"):{
				tokens.add(new Token(Kind.KW_green,sp1,p1-sp1));
				return true;
			}
			case("alpha"):{
				tokens.add(new Token(Kind.KW_alpha,sp1,p1-sp1));
				return true;
			}
			case("while"):{
				tokens.add(new Token(Kind.KW_while,sp1,p1-sp1));
				return true;
			}
			case("if"):{
				tokens.add(new Token(Kind.KW_if,sp1,p1-sp1));
				return true;
			}
			case("width"):{
				tokens.add(new Token(Kind.KW_width,sp1,p1-sp1));
				return true;
			}
			case("height"):{
				tokens.add(new Token(Kind.KW_height,sp1,p1-sp1));
				return true;
			}
			case("sleep"):{
				tokens.add(new Token(Kind.KW_sleep,sp1,p1-sp1));
				return true;
			}
			default:{
				return false;
			}
		
		}
	
		
	}


	private void error(int pos, int line, int posInLine, String message) throws LexicalException {
		String m = (line + 1) + ":" + (posInLine + 1) + " " + message;
		throw new LexicalException(m, pos);
	}

	/**
	 * Returns true if the internal iterator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that the next
	 * call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition: hasTokens()
	 * 
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}

	/**
	 * Returns the next Token, but does not update the internal iterator. This means
	 * that the next call to nextToken or peek will return the same Token as
	 * returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition: hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}

	/**
	 * Resets the internal iterator so that the next call to peek or nextToken will
	 * return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens and line starts
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		sb.append("Line starts:\n");
		for (int i = 0; i < lineStarts.length; i++) {
			sb.append(i).append(' ').append(lineStarts[i]).append('\n');
		}
		return sb.toString();
	}

}
