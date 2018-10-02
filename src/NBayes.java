import java.util.*;
import java.util.Map.Entry;
import java.io.*;

//this class will accumulate the data required to run the naive bayes
//classifier folds are specified at 10% and each 10% of the data will be 
//represented in it's own arrayList for later processing
public class NBayes {
	
	private CsvReader data;
	private ArrayList<HashMap<String, ArrayList<AttributeClassPair>>> foldedmap;
	private double[] avgs;
	private int numInstances;
	private int numAttributes;
	private int sizeofolds;
	private int numfolds;
	private ArrayList<String> classes;
	private ArrayList<ArrayList<String>> uniqueAttributeLabels;
	
	public NBayes(CsvReader data){
		this.data=data;
		this.foldedmap=new ArrayList<HashMap<String, ArrayList<AttributeClassPair>>>(); 
		this.numAttributes=data.getNumAttributes();
		this.numInstances=data.getNumInstances();
		this.avgs=new double[numAttributes];
		this.sizeofolds=(int) ((this.numInstances-2)*.1);
		this.numfolds=(int)this.numInstances/this.sizeofolds;
		this.classes=new ArrayList<String>();
		this.uniqueAttributeLabels=new ArrayList<ArrayList<String>>();
		populateClasses();
		populateAttributes();
		initAvg();
	}
	

	
	//populate foldedmaps based by making a new Hashmap for each array index
	//when that folded size is hit make a new Hashmap for next array index and populate it
	public void populateMaps(){
		int index=-1;
		ArrayList<ArrayList<String>> temp=this.data.getDataByRows();
		//traverse data making a new hashmap every time the data hits size of a fold
		//this will represent folding the data into k partitions
		for(int j=2; j<this.numInstances; j++){
			int newlist=(j-2)%this.sizeofolds;
			//add a new list 
			if(newlist==0){
				this.foldedmap.add(new HashMap<String, ArrayList<AttributeClassPair>>());
				index++;
			}
			
			//get current Hashmap
			HashMap<String, ArrayList<AttributeClassPair>> tm= this.foldedmap.get(index);
			
			for(int k=0; k<this.numAttributes; k++){
				String label=temp.get(j).get(this.numAttributes-1);
				String attr=temp.get(j).get(k);
				//this will flag when a column was continuous. replace those values with
				//> or <= depending on where the value falls
				if(avgs[k]!=-1.0){
					double avg=avgs[k];
					double attrval=0.0;
					if(isDouble(attr)){
						attrval= Double.parseDouble(attr);
					}
					if(attrval>avg){
						attr=String.format(">%f", avg);
					}
					//let those that have no value assigned fall in here
					else{
						attr=String.format("<=%f", avg);
					}
				}
				AttributeClassPair pair=new AttributeClassPair(label, attr, k);
				//check if map already has label if so check list at that label
				//if list has pair already then add to it's count. otherwise
				//add pair to the list
				if(tm.containsKey(label)){
					ArrayList<AttributeClassPair> templist;
					templist=tm.get(label);
					if(templist.contains(pair)){
						templist.get(templist.indexOf(pair)).addToCount();
					}
					else{
						templist.add(pair);
					}
				}
				//need to put the key,val pair into map the first time
				else{
					tm.put(label, new ArrayList<AttributeClassPair>());
					tm.get(label).add(pair);
				}
			}
		}
		
	}
	public boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
	
	//use this function to extract the test set.
	public ArrayList<ArrayList<String>> testSet(int index){
		ArrayList<ArrayList<String>> temp=this.data.getDataByRows();
		ArrayList<ArrayList<String>> result=new ArrayList<ArrayList<String>>();
		//run from i=index*size of a fold up 
		int upperbound=this.sizeofolds;
		int i=this.sizeofolds*index;
		int count=0;
		
		//add each row to the test set then return test set
		while(count<upperbound){
			if(count+i>=this.numInstances){
				break;
			}
			result.add(temp.get(count+i));
			count++;
		}
		return result;
	}
	
	//use this function to check continuous data first and discretize it
	//this checks the data by column organization
	public void checkContinuous(){
		int index=0;
		for(ArrayList<String> arr: this.data.getDataByCols()){
			//start by looking at label then process average and store it
			//into avgs index corresponding to it's column number in the data
			for(int i=1; i<arr.size(); i++){
				if(arr.get(i).equals("C")){
					
					double attrAvg=attributeAvg(arr);
					this.avgs[index]=attrAvg;
					System.out.println(String.format("Continuous data found: "
							+ "Discretizing on average %f for attribute"
							+ "%s", attrAvg, arr.get(0)));
				}
				
				else{
					break;
				}
			}
			index++;
		}
	}
	
	
	//get classes, remove 0 for label and data label then generate unique values
	public void populateClasses(){
		ArrayList<String> c=this.data.getDataByCols().get(this.data.getDataByCols().size()-1);
		c.remove(0);
		ArrayList<String> result=new ArrayList<String>();
		for(String str: c){
			if(!(result.contains(str))){
				if(!(str.equals("Class"))){
					result.add(str);
				}
			}
		}
		this.classes=result;
	}
	
	//populate array of unique values that each column of data contains
	public void populateAttributes(){
		int index=0;
		for(ArrayList<String> arr: this.data.getDataByCols()){
			ArrayList<String> result= new ArrayList<String>();
			this.uniqueAttributeLabels.add(result);
			for(int i=2; i<this.data.getDataByCols().size(); i++){
				if(!(this.uniqueAttributeLabels.get(index).contains(arr.get(i)))){
					String temp=(String) arr.get(i);
					result.add(temp);
				}
			}
			
			index++;
		}
	}
	
	//start at second index of column and total an average from the continuous data
	private double attributeAvg(ArrayList<String> attributevals){
		double average;
		double total=0.0;
		int i=2;
		for(i=2; i<attributevals.size(); i++){
			String val=attributevals.get(i);
			double ofval=Double.parseDouble(val);
			total+=ofval;
		}
		
		average=total/(i-2);
		
		return average;
	}
	
	public ArrayList<ArrayList<String>> getUniqueAttributes(){
		return this.uniqueAttributeLabels;
	}
	
	public ArrayList<HashMap<String, ArrayList<AttributeClassPair>>> getFoldedMap(){
		return this.foldedmap;
	}
	
	public int getNumInstances(){
		return this.numInstances;
	}
	
	public int getNumAttributes(){
		return this.numAttributes;
	}
	
	public int getNumFolds(){
		return this.numfolds;
	}
	
	public ArrayList<String> getClasses(){
		return this.classes;
	}
	//initialize values of array to 0.0 for doubles;
	private void initAvg(){
		for(int i=0; i<this.avgs.length; i++){
			this.avgs[i]=-1.0;
		}
	}
	
	
	
	
}
