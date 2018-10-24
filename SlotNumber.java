package cop5556sp18;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class SlotNumber {
	static HashMap<String,LinkedHashMap<Integer, Integer>> slotNumber = new HashMap<String,LinkedHashMap<Integer, Integer>>();
	static int count=1;
	
	static LinkedHashMap<Integer, Integer> argsSlot = new LinkedHashMap<Integer, Integer>();
	
	
	public static void addSlot(String name) {
		if(slotNumber.containsKey(name)) {
			LinkedHashMap<Integer, Integer> lm_dummy = slotNumber.get(name);
				lm_dummy.put(SymbolTable.stack.get(0), count);
				slotNumber.remove(name);
				slotNumber.put(name,lm_dummy);
			}
	else {
		LinkedHashMap<Integer, Integer> lm = new LinkedHashMap<>();
		lm.put(SymbolTable.stack.get(0), count);
		slotNumber.put(name,lm);
		}
	}	
	
	public static int lookSlot(String name) {
		if(slotNumber.containsKey(name)) {
			for(int i=0;i<SymbolTable.stack.size();i++) {
				int j = SymbolTable.stack.get(i);
				if(slotNumber.get(name).containsKey(j)) {
					return slotNumber.get(name).get(j);
				}
			}
		}
		return -1;
	}
	
	
	
}
