/**
 * Starter code with JUnit tests for code generation used in the class project in COP5556 Programming Language Principles 
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

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.CodeGenUtils.DynamicClassLoader;
import cop5556sp18.AST.Program;

public class CodeGenTest {
	
	//determines whether show prints anything
	static boolean doPrint = true;
	
	static void show(Object s) {
	if (doPrint) {
	System.out.println(s);
	}
	}

	//determines whether a classfile is created
	static boolean doCreateFile = false;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	

	//values passed to CodeGenerator constructor to control grading and debugging output
	private boolean devel = true; //if true, print devel output
	private boolean grade = true; //if true, print grade output
	
//	private boolean devel = false; 
//	private boolean grade = false; 
	
	//sets the default width and height of newly created images.  Should be small enough to fit on screen.
	public static final int defaultWidth = 1024;
	public static final int defaultHeight = 1024;

	
	/**
	 * Generates bytecode for given input.
	 * Throws exceptions for Lexical, Syntax, and Type checking errors
	 * 
	 * @param input   String containing source code
	 * @return        Generated bytecode
	 * @throws Exception
	 */
	byte[] genCode(String input) throws Exception {
	
	//scan, parse, and type check
	Scanner scanner = new Scanner(input);
	show(input);
	scanner.scan();
	Parser parser = new Parser(scanner);
	Program program = parser.parse();
	TypeChecker v = new TypeChecker();
	program.visit(v, null);
//	show(program);  //It may be useful useful to show this here if code generation fails
	show(program);
	//generate code
	//LeblancSymbolTable.current_scope=-1;
	//LeblancSymbolTable.next_scope=0;
	CodeGenerator cv = new CodeGenerator(devel, grade, null, defaultWidth, defaultHeight);
	System.out.println("reached pre bytecode in genCode CodeGenTest.java");
	byte[] bytecode = (byte[]) program.visit(cv, null);/// statement to visit CodeGenerator
	System.out.println("reached post bytecode in genCode CodeGenTest.java");
	show(program); //doing it here shows the values filled in during code gen
	//display the generated bytecode
	show(CodeGenUtils.bytecodeToString(bytecode));
	
	//write byte code to file 
	if (doCreateFile) {
	String name = ((Program) program).progName;
	String classFileName = "bin/" + name + ".class";
	OutputStream output = new FileOutputStream(classFileName);
	output.write(bytecode);
	output.close();
	System.out.println("wrote classfile to " + classFileName);
	}
	
	//return generated classfile as byte array
	return bytecode;
	}
	
	/**
	 * Run main method in given class
	 * 
	 * @param className    
	 * @param bytecode    
	 * @param commandLineArgs  String array containing command line arguments, empty array if none
	 * @throws + 
	 * @throws Throwable 
	 */
	void runCode(String className, byte[] bytecode, String[] commandLineArgs) throws Exception  {
	RuntimeLog.initLog(); //initialize log used for grading.
	DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
	Class<?> testClass = loader.define(className, bytecode);
	@SuppressWarnings("rawtypes")
	Class[] argTypes = {commandLineArgs.getClass()};
	Method m = testClass.getMethod("main", argTypes );
	show("Output from " + m + ":");  //print name of method to be executed
	Object passedArgs[] = {commandLineArgs};  //create array containing params, in this case a single array.
	try {
	m.invoke(null, passedArgs);	
	}
	catch (Exception e) {
	Throwable cause = e.getCause();
	if (cause instanceof Exception) {
	Exception ec = (Exception) e.getCause();
	throw ec;
	}
	throw  e;
	}
	}
	

	/**
	 * When invoked from JUnit, Frames containing images will be shown and then immediately deleted.
	 * To prevent this behavior, waitForKey will pause until a key is pressed.
	 * 
	 * @throws IOException
	 */
	void waitForKey() throws IOException {
	System.out.println("enter any char to exit");
	System.in.read();
	}

	/**
	 * When invoked from JUnit, Frames containing images will be shown and then immediately deleted.
	 * To prevent this behavior, keepFrame will keep the frame visible for 5000 milliseconds.
	 * 
	 * @throws Exception
	 */
	void keepFrame() throws Exception {
	Thread.sleep(5000);
	}
	
	
	
	


	/**
	 * Since we are not doing any optimization, the compiler will 
	 * still create a class with a main method and the JUnit test will
	 * execute it.  
	 * 
	 * The only thing it will do is append the "entering main" and "leaving main" messages to the log.
	 * 
	 * @throws Exception
	 */
	@Test
	public void EMPTYPROG() throws Exception {
	String prog = "emptyProg";	
	String input = prog + "{}";
	byte[] bytecode = genCode(input);
	String[] commandLineArgs = {};
	runCode(prog, bytecode, commandLineArgs);
	show("Log:\n "+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}
	

	
	@Test
	public void integerLit() throws Exception {
	String prog = "intgegerLit";
	String input = prog + "{filename y;show 3;sleep(10);} ";	
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;3;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void floatCheck() throws Exception {
	String prog = "floatCheck";
	String input = prog + "{show 8.4;} ";	
	byte[] bytecode = genCode(input);	// starts entering our program here	
	String[] commandLineArgs = {}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;8.4;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void boolCheck() throws Exception {
	String prog = "boolCheck";
	String input = prog + "{show true;} ";	
	byte[] bytecode = genCode(input);	// starts entering our program here	
	String[] commandLineArgs = {}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;true;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void boolCheck_1() throws Exception {
	String prog = "boolCheck_1";
	String input = prog + "{show false;} ";	
	byte[] bytecode = genCode(input);	// starts entering our program here	
	String[] commandLineArgs = {}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;false;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void n1() throws Exception {
	String prog = "n1";
	String input = prog + "{image y;\n filename f;\n input y from @ 0 ; input f from @1; \n show y;write y to f;}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png","J:\\PLP Project Workspace Java\\Demo2.png"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}

	
/////////////////////////////////////////Done///////////////////////////////////////////////////
	@Test
	public void n2() throws Exception {
	String prog = "n2";
	String input = prog + "{image y[20,20]; show y[12,14]; y[12,14] := 1234567890; show y[12,14];}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"5"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;0;1234567890;leaving main;",RuntimeLog.globalLog.toString());
	}
/////////////////////////////////////////Done///////////////////////////////////////////////////
	
	
	
/////////////////////////////////////////Done///////////////////////////////////////////////////
	@Test
	public void n3() throws Exception {
	String prog = "si";
	String input = prog + "{image y[20,20]; show y[12,20]; y[12,20] := 1234567890; show y[12,20];}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {""}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;0;0;leaving main;",RuntimeLog.globalLog.toString());
	}
/////////////////////////////////////////Done///////////////////////////////////////////////////
	
	
	@Test
	public void n3_1() throws Exception {
	String prog = "n3_1";
	String input = prog + "{image y[20,20]; show y[12,19]; y[12,19] := 1234567890; show y[12,19];}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {""}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;0;1234567890;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void n4() throws Exception {
	String prog = "n4";
	String input = prog + "{ image im[256,256]; \nfilename f; \ninput f from @0; \nint x;\n int y; \nx := 0; \ny := 0; \nwhile (x < width(im)){ \n y := 0; while (y < height(im)){\nim[x,y] := <<15,255,0,0>>; \nint z; z := im[x,y];y := y + 1; \n};\nx := x + 1;};\nwrite im to f;}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"J:\\PLP Project Workspace Java\\Demo2.png"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void n5() throws Exception {
	String prog = "n5";
	String input = prog + "{int x; x:=5; if(true){ int x; x := 6; show x; }; show x;}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"5", "5.0"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;6;5;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void n6() throws Exception {
	String prog = "n6";
	String input = prog + "{if(true){ int x; }; int x; x := 5; show x;}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"5"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;5;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void n7() throws Exception {
	String prog = "n7";
	String input = prog + "{ int x; x := 5; \nif(true) { \n   int x; x := 6; \n   if(false) { \n      int x; x := 7; int y; \n   }; \n   show x; \n}; \nshow x; }";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"5", "5.0"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;6;5;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	
/////////////////////////////////////////check///////////////////////////////////////////////////
	@Test
	public void n8() throws Exception {
	String prog = "n8";
	String input = prog + "{image y; image copy[128,256]; input y from @ 0 ; show y; copy := y; show copy;}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	BufferedImage image=RuntimeLog.globalImageLog.get(1);
	System.out.println(image.getWidth());
	}
/////////////////////////////////////////check///////////////////////////////////////////////////
	
	
	@Test
	public void n9() throws Exception {
	String prog = "n9";
	String input = prog + "{ image im[512,256]; int x;\n int y; \nx := 0; \ny := 0; \nwhile (x < width(im)){ \n y := 0; while (y < height(im)){\nalpha(im[x,y]) := 255;\nred(im[x,y]) := 0;\ngreen(im[x,y]) := x+y;\nblue(im[x,y]) := 0; \ny := y + 1; \n};\nx := x + 1;};\nshow im;\n}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"5", "5.0"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void n10() throws Exception {
	String prog = "n10";
	String input = prog + "{ image im; \ninput im from @0; \nfilename f; \ninput f from @1; \nint x;\n int y; \nx := 0; \ny := 0;\nwhile (x < width(im)){ \n y := 0; while (y < height(im)){\nim[x,y] := <<15,255,0,0>>; \nint z; z := im[x,y];y := y + 1; \n};\nx := x + 1;};\nwrite im to f;\n}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png","J:\\PLP Project Workspace Java\\Demo3.png"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void n11() throws Exception {
	String prog = "n11";
	String input = prog + "{ image im[512,256]; int x;\n int y; \nx := 0; \ny := 0; \nwhile (x < width(im)){ \n y := 0; while (y < height(im)){\nim[x,y] := <<255,0,x+y,0>>; \ny := y + 1; \n};\nx := x + 1;};\nshow im;\n}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"5", "5.0"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void n12() throws Exception {
	String prog = "n12";
	String input = prog + "{ image im[256,256]; \nfilename f; \ninput f from @0; \nint x;\n int y; \nx := 0; \ny := 0; \nwhile (x < width(im)){ \n y := 0; while (y < height(im)){\nim[x,y] := <<255,255,0,0>>; \nint z; z := im[x,y];y := y + 1; \n};\nx := x + 1;};\nwrite im to f;}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"5", "5.0"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	
/////////////////////////////////////////Done///////////////////////////////////////////////////	
	@Test
	public void n13() throws Exception {
	String prog = "n13";
	String input = prog + "{image x; input x from @ 0 ; show x; image y; y := x; show y;}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}
/////////////////////////////////////////Done///////////////////////////////////////////////////

	
/////////////////////////////////////////Done///////////////////////////////////////////////////	
	@Test
	public void n14() throws Exception {
	String prog = "n14";
	String input = prog + "{image y[1000,1000]; image copy[1000,1000]; input y from @ 0 ; show y; copy := y; show copy;}";	
	System.out.println(input);
	byte[] bytecode = genCode(input);	
	String[] commandLineArgs = {"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"}; //create command line argument array to initialize params, none in this case	
	runCode(prog, bytecode, commandLineArgs);	
	show("Log:\n"+RuntimeLog.globalLog);
	assertEquals("entering main;leaving main;",RuntimeLog.globalLog.toString());
	}
/////////////////////////////////////////Done///////////////////////////////////////////////////
	

	@Test
	public void expressUni_1() throws Exception {
		String prog = "expressUni_1";
		String input =  prog + "{float x;x := 9.8; float y; y := 9.80; show x > y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;false;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void expressUni_2() throws Exception {
		String prog = "expressUni_2";
		String input =  prog + "{float x;x := 9.8; float y; y := 9.80; show x < y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;false;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void expressUni_3() throws Exception {
		String prog = "expressUni_3";
		String input =  prog + "{float x;x := 9.8; float y; y := 9.80; show x == y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;true;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void expressUni_4() throws Exception {
		String prog = "expressUni_4";
		String input =  prog + "{int x;x := 9; int y; y := 9; show x == y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;true;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void expressUni_5() throws Exception {
		String prog = "expressUni_5";
		String input =  prog + "{int x;x := 9; int y; y := 9; show x >= y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;true;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void expressUni_6() throws Exception {
		String prog = "expressUni_6";
		String input =  prog + "{int x;x := 9; int y; y := 9; show x <= y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;true;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void expressUni_7() throws Exception {
		String prog = "expressUni_7";
		String input =  prog + "{int x;x := 9; int y; y := 9; show x < y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;false;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void expressUni_8() throws Exception {
		String prog = "expressUni_8";
		String input =  prog + "{int x;x := 9; int y; y := 9; show x > y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;false;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void expressUni_9() throws Exception {
		String prog = "expressUni_9";
		String input =  prog + "{boolean x;x := true; boolean y; y := false; show x == y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;false;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void expressUni_10() throws Exception {
		String prog = "expressUni_10";
		String input =  prog + "{boolean x;x := true; boolean y; y := true; show x == y;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;true;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void if_1() throws Exception {
		String prog = "if_1";
		String input =  prog + "{float x; x:=9.8; if(true) { int x; x:=7 ;show x; }; show x;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;7;9.8;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void if_2() throws Exception {
		String prog = "if_2";
		String input =  prog + "{float x; x:=9.8; if(false) { int x; x:=7 ;show x; }; show x;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("entering main;9.8;leaving main;",RuntimeLog.globalLog.toString());
	}
	
	
	
}