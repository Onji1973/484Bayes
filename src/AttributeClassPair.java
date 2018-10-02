//this represents a pairing of a class and an attribute it will be initialized
//with a column to represent it's place in the data and will include a count
//field to increment when like objects are found. Will hold the total times
//and attribute occurred for a given class
public class AttributeClassPair{
	
	private String classifier;
	private String attribute;
	private int count;
	private int col;
	private double probability;

	public AttributeClassPair(String classifier, String attribute, int col){
		this.classifier=classifier;
		this.attribute=attribute;
		this.col=col;
		this.count=1;
		this.probability=0.0;
	}
	
	public void addToCount(){
		this.count++;
	}
	
	public String getClassifier(){
		return this.classifier;
	}
	
	public String getAttribute(){
		return this.attribute;
	}
	
	public int getCol(){
		return this.col;	
	}
	
	public int getCount(){
		return this.count;
	}
	
	public double getProb(){
		return this.probability;
	}
	
	//overload addToCount
	public void addToCount(int countmod){
		this.count+=countmod;
	}
	
	//set likely hood of this attribute, to give weight to specific
	//attributes
	public void setProbability(double prob){
		this.probability=prob;
	}
	
	//override for object equality: requires to be of proper type
	//have same class, attribute and col.
	@Override
	 public boolean equals(Object other){
	  if(other!=null && other instanceof AttributeClassPair){
	   AttributeClassPair test=(AttributeClassPair)other;
	   if(this.getClass().equals(test.getClass())){
		   if(this.getAttribute().equals(test.getAttribute())){
			   if(this.col==test.col){
			   		return true;
			   }
		   }
	   }
	  }
	  return false;
	 }
	 
	//hashcode will just rely on String's hashcode method. Adding both 
	//hash of classifier and hash of attribute together
	 @Override
	 public int hashCode(){
		 return this.classifier.hashCode() + this.attribute.hashCode();
	 }
	
}
