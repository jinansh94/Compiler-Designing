/**
 * Starter code for CodeGenerator.java used n the class project in COP5556 Programming Language Principles 
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
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp18.Types.Type;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.ASTVisitor;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
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
//import jdk.vm.ci.meta.Assumptions.LeafType;
import cop5556sp18.CodeGenUtils;
import cop5556sp18.Scanner.Kind;

public class CodeGenerator implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */

	static final int Z = 255;

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	final int defaultWidth;
	final int defaultHeight;
	// final boolean itf = false;
	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 * @param defaultWidth
	 *            default width of images
	 * @param defaultHeight
	 *            default height of images
	 */
	public CodeGenerator(boolean DEVEL, boolean GRADE, String sourceFileName,
			int defaultWidth, int defaultHeight) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
		SlotNumber.slotNumber = new HashMap<String, LinkedHashMap<Integer, Integer>>();
		SymbolTable.stack=new ArrayList<Integer>();
		SlotNumber.argsSlot.put(0,0);
		SlotNumber.slotNumber.put("args",SlotNumber.argsSlot);
	}
	
	
	
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		SymbolTable.enterScope();
		for (ASTNode node : block.decsOrStatements) {
			node.visit(this, null);
		}
		SymbolTable.leavScope();
		return null;
	}

	@Override
	public Object visitBooleanLiteral(
			ExpressionBooleanLiteral expressionBooleanLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionBooleanLiteral.value);
		return null;
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg)
			throws Exception {
		
		String fieldName = declaration.name;
		Type decType = Types.getType(declaration.type);
		String fieldType;
		FieldVisitor fv;
		
		switch(decType) {
			case INTEGER: {
				mv.visitLdcInsn(0);
				mv.visitVarInsn(ISTORE,SlotNumber.count);
				
				SlotNumber.addSlot(fieldName);
				SlotNumber.count++;
			}
			break;
			case FLOAT: {
				mv.visitLdcInsn(0);
				mv.visitInsn(I2F);
				mv.visitVarInsn(FSTORE,SlotNumber.count);
				SlotNumber.addSlot(fieldName);
				SlotNumber.count++;
			}
			break;
			case BOOLEAN: {
				mv.visitLdcInsn(0);
				mv.visitVarInsn(ISTORE,SlotNumber.count);
				SlotNumber.addSlot(fieldName);
				SlotNumber.count++;
			}
			break;
			case IMAGE: {
				fieldType="Ljava/awt/image/BufferedImage;";
				if (declaration.width == null && declaration.height == null ) {
					mv.visitLdcInsn(defaultWidth);
					mv.visitLdcInsn(defaultHeight);
					mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "makeImage", "(II)Ljava/awt/image/BufferedImage;", false);
					mv.visitVarInsn(ASTORE, SlotNumber.count);
					SlotNumber.addSlot(fieldName);
					SlotNumber.count++;
				}
				else if(declaration.width != null && declaration.height != null){
					declaration.width.visit(this, arg);
					declaration.height.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "makeImage", "(II)Ljava/awt/image/BufferedImage;", false);
					mv.visitVarInsn(ASTORE, SlotNumber.count);
					SlotNumber.addSlot(fieldName);
					SlotNumber.count++;
				}
			}
			break;
			case FILE: {
				mv.visitLdcInsn("");
				mv.visitVarInsn(ASTORE,SlotNumber.count);
				SlotNumber.addSlot(fieldName);
				SlotNumber.count++;
			}
			break;
		}
		
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary,
			Object arg) throws Exception {
		Type expType = expressionBinary.type;
		Kind op = expressionBinary.op;
		
		expressionBinary.leftExpression.visit(this, arg);
		if(expressionBinary.rightExpression.type == Type.FLOAT && expressionBinary.leftExpression.type == Type.INTEGER) {
			if(op == Kind.OP_POWER) {
				mv.visitInsn(I2D);
			}else{
				mv.visitInsn(I2F);
			}
		}else if(expressionBinary.rightExpression.type == Type.INTEGER && expressionBinary.leftExpression.type == Type.INTEGER) {
			if(op == Kind.OP_POWER) {
				mv.visitInsn(I2D);
			}
		}else if(expressionBinary.rightExpression.type == Type.FLOAT && expressionBinary.leftExpression.type == Type.FLOAT) {
			if(op == Kind.OP_POWER) {
				mv.visitInsn(F2D);
			}
		}
		else if(expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.INTEGER ) {
			if(op == Kind.OP_POWER) {
				mv.visitInsn(F2D);
			}
		}
		
		
		
		expressionBinary.rightExpression.visit(this, arg);
		if(expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.INTEGER ) {
			if(op == Kind.OP_POWER) {
				mv.visitInsn(I2D);
			}
			else{
				mv.visitInsn(I2F);
			}
		}else if(expressionBinary.rightExpression.type == Type.INTEGER && expressionBinary.leftExpression.type == Type.INTEGER) {
			if(op == Kind.OP_POWER) {
				mv.visitInsn(I2D);
			}
		}else if(expressionBinary.rightExpression.type == Type.FLOAT && expressionBinary.leftExpression.type == Type.FLOAT) {
			if(op == Kind.OP_POWER) {
				mv.visitInsn(F2D);
			}
		}
		else if(expressionBinary.rightExpression.type == Type.FLOAT && expressionBinary.leftExpression.type == Type.INTEGER) {
			if(op == Kind.OP_POWER) {
				mv.visitInsn(F2D);
			}
		}
		
		switch (expType) {
			case INTEGER:{
				switch (op) {
					case OP_PLUS:{
						mv.visitInsn(IADD);
					}
					break;
					case OP_MINUS:{
						mv.visitInsn(ISUB);
					}
					break;
					case OP_TIMES:{
						mv.visitInsn(IMUL);
					}
					break;
					case OP_DIV:{
						mv.visitInsn(IDIV);
					}
					break;
					case OP_MOD:{
						mv.visitInsn(IREM);
					}
					break;
					case OP_POWER:{
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
						mv.visitInsn(D2I);
					}
					break;
					case OP_AND:{
						mv.visitInsn(IAND);
					}
					break;
					case OP_OR:{
						mv.visitInsn(IOR);
					}
					break;
				}
				
			}
			break;
			case FLOAT:{
				switch (op) {
					case OP_PLUS:{
						mv.visitInsn(FADD);
					}
					break;
					case OP_MINUS:{
						mv.visitInsn(FSUB);
					}
					break;
					case OP_TIMES:{
						mv.visitInsn(FMUL);
					}
					break;
					case OP_DIV:{
						mv.visitInsn(FDIV);
					}
					break;
					case OP_POWER:{
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
						mv.visitInsn(D2F);
					}
					break;
				}
			}
			break;
			case BOOLEAN:{
				switch (op) {
				case OP_AND:{
					mv.visitInsn(IAND);
				}
				break;
				case OP_OR:{
					mv.visitInsn(IOR);
				}
				break;
				case OP_EQ:{
					if((expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) || (expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)) {
						Label l3 = new Label();
						mv.visitJumpInsn(IF_ICMPNE, l3);
						mv.visitInsn(ICONST_1);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
						//mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(l4);
					}
					else if(expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
						mv.visitInsn(FCMPL);
						Label l3 = new Label();
						mv.visitJumpInsn(IFNE, l3);
						mv.visitInsn(ICONST_1);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
						//mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.FLOAT, Opcodes.FLOAT}, 0, null);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(l4);
					}
				}
				break;
				case OP_NEQ:{
					if((expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) || (expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)) {
							Label l3 = new Label();
							mv.visitJumpInsn(IF_ICMPEQ, l3);
							mv.visitInsn(ICONST_1);
							Label l4 = new Label();
							mv.visitJumpInsn(GOTO, l4);
							mv.visitLabel(l3);
							//mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
							mv.visitInsn(ICONST_0);
							mv.visitLabel(l4);
						}
						else if(expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
							mv.visitInsn(FCMPL);
							Label l3 = new Label();
							mv.visitJumpInsn(IFEQ, l3);
							mv.visitInsn(ICONST_1);
							Label l4 = new Label();
							mv.visitJumpInsn(GOTO, l4);
							mv.visitLabel(l3);
							mv.visitInsn(ICONST_0);
							mv.visitLabel(l4);
							
						}
				}
				break;
				case OP_GT:{
					if((expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) || (expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)) {
						Label l3 = new Label();
						mv.visitJumpInsn(IF_ICMPLE, l3);
						mv.visitInsn(ICONST_1);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
					//	mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(l4);
					}
					else if(expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
						mv.visitInsn(FCMPL);				
						Label l3 = new Label();
						mv.visitJumpInsn(IFGT, l3);
						mv.visitInsn(ICONST_0);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(l4);
					}
				}
				break;
				case OP_GE:{
					if((expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) || (expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)) {
						Label l3 = new Label();
						mv.visitJumpInsn(IF_ICMPLT, l3);
						mv.visitInsn(ICONST_1);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(l4);
					}
					else if(expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
						mv.visitInsn(FCMPL);
						Label l3 = new Label();
						mv.visitJumpInsn(IFLT, l3);
						mv.visitInsn(ICONST_1);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
						//mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.FLOAT, Opcodes.FLOAT}, 0, null);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(l4);
					}
				}
				break;
				case OP_LT:{
					if((expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) || (expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)) {
						Label l3 = new Label();
						mv.visitJumpInsn(IF_ICMPGE, l3);
						mv.visitInsn(ICONST_1);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
						//mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(l4);
					}
					else if(expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
						mv.visitInsn(FCMPG);
						Label l3 = new Label();
						mv.visitJumpInsn(IFGE, l3);
						mv.visitInsn(ICONST_1);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
						//mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.FLOAT, Opcodes.FLOAT}, 0, null);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(l4);
					}
				}
				break;
				case OP_LE:{
					if((expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) || (expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)) {
						Label l3 = new Label();
						mv.visitJumpInsn(IF_ICMPGT, l3);
						mv.visitInsn(ICONST_1);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
						//mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(l4);
					}
					else if(expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
						mv.visitInsn(FCMPG);
						Label l3 = new Label();
						mv.visitJumpInsn(IFGT, l3);
						mv.visitInsn(ICONST_1);
						Label l4 = new Label();
						mv.visitJumpInsn(GOTO, l4);
						mv.visitLabel(l3);
						//mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.FLOAT, Opcodes.FLOAT}, 0, null);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(l4);
					}
				}
				break;
				}
			}
			break;
		}
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionConditional(
			ExpressionConditional expressionConditional, Object arg)
			throws Exception {
		
		expressionConditional.guard.visit(this,arg);
		Label l2 = new Label();
		mv.visitJumpInsn(IFEQ, l2);
		expressionConditional.trueExpression.visit(this,arg);
		Label l3 = new Label();
		mv.visitJumpInsn(GOTO, l3);
		mv.visitLabel(l2);
		expressionConditional.falseExpression.visit(this,arg);
		mv.visitLabel(l3);
		
		return null;
	}

	@Override
	public Object visitExpressionFloatLiteral(
			ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionFloatLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg,
			Object arg) throws Exception {
			expressionFunctionAppWithExpressionArg.e.visit(this, arg);
			Type exType = expressionFunctionAppWithExpressionArg.e.type;
			Kind name = expressionFunctionAppWithExpressionArg.function;
			switch(exType) {
				case INTEGER:{
					if(name == Kind.KW_abs) {
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false);
						return null;
					}
					else if(name == Kind.KW_red) {
						mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getRed","(I)I" , false);
						return null;
					}
					else if(name == Kind.KW_green) {
						mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getGreen","(I)I" , false);
						return null;
	
					}
					else if(name == Kind.KW_blue) {
						mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getBlue","(I)I" , false);
						return null;
					}
					else if(name == Kind.KW_alpha) {
						mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "getAlpha","(I)I" , false);
						return null;
					}
					else if(name == Kind.KW_float) {
						mv.visitInsn(I2F);
					}
					
				}
				break;
				case FLOAT:{
					if(name == Kind.KW_abs) {
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F", false);
					}
					else if(name == Kind.KW_sin) {
						mv.visitInsn(F2D);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
						mv.visitInsn(D2F);
					}
					else if(name == Kind.KW_cos) {
						mv.visitInsn(F2D);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
						mv.visitInsn(D2F);
					}
					else if(name == Kind.KW_atan) {
						mv.visitInsn(F2D);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan", "(D)D", false);
						mv.visitInsn(D2F);
					}
					else if(name == Kind.KW_log) {
						mv.visitInsn(F2D);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "log", "(D)D", false);
						mv.visitInsn(D2F);
					}
					else if(name == Kind.KW_int) {
							mv.visitInsn(F2I);
					}
				}
				break;
				case IMAGE: {
					if(name == Kind.KW_width) {
							mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "getWidth", "(Ljava/awt/image/BufferedImage;)I" , false);
					}
					else if (name == Kind.KW_height) {
							mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "getHeight", "(Ljava/awt/image/BufferedImage;)I" , false);						
					}
				
				}
				break;
			}
			return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(
			ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
			Kind fun = expressionFunctionAppWithPixel.name;
			switch(fun) {
			
				case KW_cart_x:{ 
					expressionFunctionAppWithPixel.e0.visit(this,arg);
					expressionFunctionAppWithPixel.e1.visit(this,arg);
					mv.visitInsn(F2D);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
					mv.visitInsn(D2F);
					mv.visitInsn(FMUL);
					mv.visitInsn(F2I);
				}
				break;
				case KW_cart_y:{ 
					expressionFunctionAppWithPixel.e0.visit(this,arg);
					expressionFunctionAppWithPixel.e1.visit(this,arg);
					mv.visitInsn(F2D);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
					mv.visitInsn(D2F);
					mv.visitInsn(FMUL);
					mv.visitInsn(F2I);
				}
				break;
				case KW_polar_a:{
					expressionFunctionAppWithPixel.e1.visit(this,arg);
					mv.visitInsn(I2D);
					expressionFunctionAppWithPixel.e0.visit(this,arg);
					mv.visitInsn(I2D);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan2", "(DD)D", false);
					mv.visitInsn(D2F);
				}
				break;
				case KW_polar_r:{
					expressionFunctionAppWithPixel.e0.visit(this,arg);
					mv.visitInsn(I2D);
					expressionFunctionAppWithPixel.e1.visit(this,arg);
					mv.visitInsn(I2D);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "hypot", "(DD)D", false);
					mv.visitInsn(D2F);
				}
				break;
			}
			return null;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent,
			Object arg) throws Exception {
		String name = expressionIdent.name;
		Type expType = expressionIdent.type;
		switch(expType) {
			case INTEGER: {
				mv.visitVarInsn(ILOAD, SlotNumber.lookSlot(name));
			}
			break;
			case FLOAT: {
				mv.visitVarInsn(FLOAD, SlotNumber.lookSlot(name));
			}
			break;
			case BOOLEAN: {
				mv.visitVarInsn(ILOAD, SlotNumber.lookSlot(name));
			}
			break;
			case IMAGE: {
				mv.visitVarInsn(ALOAD, SlotNumber.lookSlot(name));
			}
			break;
			case FILE:{
				mv.visitVarInsn(ALOAD, SlotNumber.lookSlot(name));
			}
		}
		return null;
	}

	@Override
	public Object visitExpressionIntegerLiteral(
			ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		// This one is all done!
		mv.visitLdcInsn(expressionIntegerLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel,
			Object arg) throws Exception {
			int ident = SlotNumber.lookSlot(expressionPixel.name);
			mv.visitVarInsn(ALOAD,ident);
			expressionPixel.pixelSelector.visit(this,arg);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "getPixel", "(Ljava/awt/image/BufferedImage;II)I", false);
			return null;
	}

	@Override
	public Object visitExpressionPixelConstructor(
			ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		expressionPixelConstructor.alpha.visit(this,arg);
		expressionPixelConstructor.red.visit(this,arg);
		expressionPixelConstructor.green.visit(this,arg);
		expressionPixelConstructor.blue.visit(this,arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimePixelOps", "makePixel", "(IIII)I", false);
		return null;
	}

	@Override
	public Object visitExpressionPredefinedName(
			ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
			Kind preName = expressionPredefinedName.name;
			if (preName == Kind.KW_Z) {
				mv.visitIntInsn(SIPUSH, 255);
			}
			else if (preName == Kind.KW_default_height) {
				mv.visitLdcInsn(defaultHeight);
			}
			else if (preName == Kind.KW_default_width) {
				mv.visitLdcInsn(defaultWidth);
			}
			return null;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary,
			Object arg) throws Exception {
		expressionUnary.expression.visit(this, arg);
		Type unar = expressionUnary.type;
		Kind op = expressionUnary.op;
		switch(unar) {
			case INTEGER:{
				if(op == Kind.OP_EXCLAMATION) {
					mv.visitInsn(ICONST_M1);
					mv.visitInsn(IXOR);
					}
				else if(op == Kind.OP_MINUS) {
					mv.visitInsn(INEG);
				}
				else if(op == Kind.OP_PLUS) {
				
				}
			}
			break;
			case FLOAT:{
				if(op == Kind.OP_MINUS) {
					mv.visitInsn(FNEG);
				}
				else if(op == Kind.OP_PLUS) {
				
				}
			}
			break;
			case BOOLEAN:{
				if(op == Kind.OP_EXCLAMATION) {
					Label l2 = new Label();
					mv.visitJumpInsn(IFNE, l2);
					mv.visitInsn(ICONST_1);
					Label l3 = new Label();
					mv.visitJumpInsn(GOTO, l3);
					mv.visitLabel(l2);
					mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(l3);
					mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
					}
			}
			break;
		}
		return null;
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg)
			throws Exception {
		Type lhsType = lhsIdent.type;
		String name = lhsIdent.name;
		switch(lhsType) {
			case INTEGER:{
				mv.visitVarInsn(ISTORE, SlotNumber.lookSlot(name));
			}
			break;	
			case FLOAT:{
				mv.visitVarInsn(FSTORE, SlotNumber.lookSlot(name));
			}
			break;
			case BOOLEAN:{
				mv.visitVarInsn(ISTORE, SlotNumber.lookSlot(name));
			}
			break;
			case FILE:{
				mv.visitVarInsn(ASTORE, SlotNumber.lookSlot(name));
			}
			break;
			case IMAGE:{
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "deepCopy", "(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
				mv.visitVarInsn(ASTORE, SlotNumber.lookSlot(name));
			}
			break;
		}
		return null;
		
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg)
			throws Exception {
		String n = lhsPixel.name;
		int ni = SlotNumber.lookSlot(n);
		mv.visitVarInsn(ALOAD, ni);
		lhsPixel.pixelSelector.visit(this,arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "setPixel", "(ILjava/awt/image/BufferedImage;II)V", false);
		return null;
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg)
			throws Exception {
		String n = lhsSample.name;
		int ni = SlotNumber.lookSlot(n);
		mv.visitVarInsn(ALOAD, ni);
		lhsSample.pixelSelector.visit(this,arg);
		Kind color = lhsSample.color;
		switch(color) {
			case KW_alpha:{
				mv.visitLdcInsn(0);
			}
			break;
			case KW_red:{
				mv.visitLdcInsn(1);
			}
			break;
			case KW_green:{
				mv.visitLdcInsn(2);
			}
			break;
			case KW_blue:{
				mv.visitLdcInsn(3);
			}
			break;
		
		}
		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "updatePixelColor", "(ILjava/awt/image/BufferedImage;III)V", false);
		return null;
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg)
			throws Exception {
		pixelSelector.ex.visit(this,arg);
		pixelSelector.ey.visit(this,arg);
		
		return null;
		}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		// cw = new ClassWriter(0); //If the call to mv.visitMaxs(1, 1) crashes,
		// it is
		// sometime helpful to
		// temporarily run it without COMPUTE_FRAMES. You probably
		// won't get a completely correct classfile, but
		// you will be able to see the code that was
		// generated.
		className = program.progName;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null,
				"java/lang/Object", null);
		cw.visitSource(sourceFileName, null);

		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
				"([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();

		// add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);

		CodeGenUtils.genLog(DEVEL, mv, "entering main");

		program.block.visit(this, arg);

		// generates code to add string to log
		CodeGenUtils.genLog(DEVEL, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart,
				mainEnd, 0);
		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor,
		// asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily change the parameter in the ClassWriter constructor
		// from COMPUTE_FRAMES to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitMaxs(0, 0);

		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();
		
		// generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign,
			Object arg) throws Exception {
		statementAssign.e.visit(this, arg);
		statementAssign.lhs.visit(this, arg);
		return null;
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg)
			throws Exception {
			statementIf.guard.visit(this,arg);
			Label l3 = new Label();
			mv.visitJumpInsn(IFEQ, l3);
			statementIf.b.visit(this,arg);
			mv.visitLabel(l3);
			return null;
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		ExpressionIntegerLiteral arrayPos = (ExpressionIntegerLiteral)(statementInput.e);
		mv.visitLdcInsn(arrayPos.value);
		mv.visitInsn(AALOAD);
		Type decType = Types.getType(SymbolTable.lookDec(statementInput.destName).type);
		String name = SymbolTable.lookDec(statementInput.destName).name;
		Declaration dec = SymbolTable.lookDec(statementInput.destName);
		switch(decType) {
			case INTEGER: {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
				mv.visitVarInsn(ISTORE, SlotNumber.lookSlot(name));
			}
			break;
			case FLOAT: {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F", false);
				mv.visitVarInsn(FSTORE, SlotNumber.lookSlot(name));
			}
			break;
			case IMAGE: {
				if(dec.height == null && dec.width==null) {
					mv.visitInsn(ACONST_NULL);
					mv.visitInsn(ACONST_NULL);
				}
				else if(dec.height != null && dec.width != null) {
					SymbolTable.lookDec(statementInput.destName).width.visit(this,arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
					SymbolTable.lookDec(statementInput.destName).height.visit(this,arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				}
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "readImage", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/awt/image/BufferedImage;", false);
				mv.visitVarInsn(ASTORE, SlotNumber.lookSlot(name));
				
			}
			break;
			case FILE: {
				mv.visitVarInsn(ASTORE, SlotNumber.lookSlot(name));
			}
			break;
			case BOOLEAN: {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
				mv.visitVarInsn(ISTORE, SlotNumber.lookSlot(name));
			}
			break;
			
		}
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg)
			throws Exception {
		/**
		 * TODO refactor and complete implementation.
		 * 
		 * For integers, booleans, and floats, generate code to print to
		 * console. For images, generate code to display in a frame.
		 * 
		 * In all cases, invoke CodeGenUtils.genLogTOS(GRADE, mv, type); before
		 * consuming top of stack.
		 */
		statementShow.e.visit(this, arg);
		Type type = statementShow.e.type;
		System.out.println(type);
		switch (type) {
			case INTEGER : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(I)V", false);
			}
				break;
			case BOOLEAN : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(Z)V", false);
			}
			break;
			case FLOAT : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(F)V", false);
			}
			break;
			case IMAGE : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "makeFrame", "(Ljava/awt/image/BufferedImage;)Ljavax/swing/JFrame;", false);
				mv.visitInsn(POP);
			}
			break;

		}
		return null;
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg)
			throws Exception {
			statementSleep.duration.visit(this,arg);
			mv.visitInsn(I2L);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
			return null;
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg)
			throws Exception {
		
		Label l1 = new Label();
		mv.visitLabel(l1);
		Label l2 = new Label();
		statementWhile.guard.visit(this, arg);
		mv.visitJumpInsn(IFEQ, l2);
		statementWhile.b.visit(this, arg);
		mv.visitJumpInsn(GOTO, l1);
		mv.visitLabel(l2);
		
		return null;
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg)
			throws Exception {
			String source = statementWrite.sourceName;
			String dest = statementWrite.destName;
			int s = SlotNumber.lookSlot(source);
			int d = SlotNumber.lookSlot(dest);
			mv.visitVarInsn(ALOAD,s);
			mv.visitVarInsn(ALOAD, d);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp18/RuntimeImageSupport", "write", "(Ljava/awt/image/BufferedImage;Ljava/lang/String;)V", false);
			return null;
	}
	
	

}