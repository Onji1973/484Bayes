import java.util.*;

/* Use this class to make a hashmap of AttributeClassPair types
 * 
 */

public class CPBuilder {
	
	private HashMap<String, AttributeClassPair> classpairmap;
	ArrayList<String> valtypes;
	
	public CPBuilder(ArrayList<ArrayList<String>> data){
		this.classpairmap=new HashMap<String, AttributeClassPair>();
		this.valtypes=data.get(1);
	}
	
	public HashMap<String, AttributeClassPair> getClassPairMap(){
		return this.classpairmap;
	}
	
	public String printValTypes(){
		StringBuilder b=new StringBuilder();
		for(String j: this.valtypes){
			b.append(j);
		}
		return b.toString();
	}
}
