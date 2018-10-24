package cop5556sp18;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import cop5556sp18.AST.Declaration;

public class SymbolTable {
	static int count =0;
	static 	ArrayList<Integer> stack = new ArrayList<Integer>();
	static HashMap<String,LinkedHashMap<Integer, Declaration>> symbolT = new HashMap<String,LinkedHashMap<Integer, Declaration>>();
	LinkedHashMap<Integer, Declaration> lm  ;
	
	public static void enterScope() {
		if (stack.isEmpty()) {
			stack.add(0,0);
		}
		else {
		 count++;
		 stack.add(0,count);
		}
	 }
	
	public static void leavScope() {
		stack.remove(0);
	}
	
	public static void addVar(Declaration dec) {
		if(symbolT.containsKey(dec.name)) {
			LinkedHashMap<Integer, Declaration> lm_dummy = symbolT.get(dec.name);
				lm_dummy.put(stack.get(0), dec);
				symbolT.remove(dec.name);
				symbolT.put(dec.name,lm_dummy);
			}
		else {
		LinkedHashMap<Integer, Declaration> lm = new LinkedHashMap<>();
		lm.put(stack.get(0), dec);
		symbolT.put(dec.name,lm);
		}
	}
		
	public static Declaration lookDec(String decname) {
		if(symbolT.containsKey(decname)) {
			for(int i=0;i<stack.size();i++) {
				int j = stack.get(i);
				if(symbolT.get(decname).containsKey(j)) {
					return symbolT.get(decname).get(j);
				}
			}
		}
		return null;
	}
	

}
