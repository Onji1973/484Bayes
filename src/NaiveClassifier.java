import java.util.*;
import java.util.Map.Entry;


//this class will do the computation for NBayes and predict what class
//an instance is. Folding occurred before
public class NaiveClassifier {
	
	private NBayes b;
	private ArrayList<HashMap<String, ArrayList<AttributeClassPair>>> foldedmap;
	private int numfolds;
	private int numattributes;
	private int instances;
	private ArrayList<AttributeClassPair> classCount;
	private ArrayList<ConfusionMatrix> confusionM;
	private HashMap<String, ArrayList<AttributeClassPair>> totalmap;
	private ArrayList<String> actualclass;
	private ArrayList<String> predictedclass;
	private ArrayList<String> classes;
	private ArrayList<ArrayList<String>> uniques;
	private double accTotal;
	private double recallTotal;
	private double f1Total;
	private double selfAcc;
	
	public NaiveClassifier(NBayes b){
		this.b=b;
		this.foldedmap=b.getFoldedMap();
		this.numfolds=foldedmap.size();
		this.numattributes=b.getNumAttributes();
		this.confusionM=new ArrayList<ConfusionMatrix>();
		this.actualclass = new ArrayList<String>();
		this.predictedclass = new ArrayList<String>();
		this.classes=b.getClasses();
		this.uniques=b.getUniqueAttributes();
		this.instances=b.getNumInstances();
	}
	
	
	//look through each portion of foldedmap and get total of classes
	//store them as an attributeclasspair, return arraylist of size
	//=#of different classes. Pass this function the index of the fold to avoid
	//that fold will represent the test set.
	public void classTotal(int avoid){
		
		this.totalmap=new HashMap<String, ArrayList<AttributeClassPair>>();
		
		String cls="";
		//accumulate training set totals
		
		for(int i=0; i<foldedmap.size(); i++){
			if(i!=avoid){
				for(Entry<String, ArrayList<AttributeClassPair>> entry: foldedmap.get(i).entrySet()){
					cls=entry.getKey();
					//traverse list
					for(int k=0; k<entry.getValue().size(); k++){
						//if totals already has 
						if(totalmap.containsKey(cls)){
							ArrayList<AttributeClassPair> templist;
							templist=totalmap.get(cls);
							AttributeClassPair tmp=entry.getValue().get(k);
							if(templist.contains(tmp)){
								AttributeClassPair base = templist.get(templist.indexOf(tmp));
								base.addToCount(tmp.getCount());
							}
							else{
								templist.add(tmp);
							}
						}	
						else{
							totalmap.put(cls, new ArrayList<AttributeClassPair>());
							totalmap.get(cls).add(entry.getValue().get(k));
						}
					}	
				}
			}
		}
	}
	

	//calculate probabilites based on training set
	public void calcProb(){
		//need to set probabilities each time 
		HashMap<String, ArrayList<AttributeClassPair>> trainingtotals= this.totalmap;
		
		for(Entry<String, ArrayList<AttributeClassPair>> entry: trainingtotals.entrySet()){
			//get total for class
			double classcount=0.0;
			//scan list for classcount
			for(int i=0; i<entry.getValue().size(); i++){
				if(entry.getValue().get(i).getClassifier().equals(entry.getValue().get(i).getAttribute())){
					classcount=(double)entry.getValue().get(i).getCount(); 
					break;
				}
			}
			
			//use classcount to calculate probabilites
			for(int i=0; i<entry.getValue().size(); i++){
				if(!(entry.getValue().get(i).getClassifier().equals(entry.getValue().get(i).getAttribute()))){
					double attrcount=(double)entry.getValue().get(i).getCount();
					AttributeClassPair check=entry.getValue().get(i);
					double prob=attrcount/classcount;
					check.setProbability(prob);
				}
			}
		}
	}
	
	//now bring in test set
	public void runTestSet(int index){

		ArrayList<ArrayList<String>> testset=this.b.testSet(index);
		
		//this will store one vote for each potential class
		AttributeClassPair[] probs=new AttributeClassPair[this.totalmap.size()];
		
		//crawl each instance and calculate it's chance to be in a class
		for(int i=0; i<testset.size(); i++){
			//move i up 2 for the first set to take out instances of attribute names and labels
			if(index==0 && i==0){
				i+=2;
			}
			//actual class of the instance
			String cls=testset.get(i).get(this.numattributes-1);
			//look through each class in totalmap to compare
			String key="";
			int where=-1;
			int trigger=0;
			for(Entry<String, ArrayList<AttributeClassPair>> entry: this.totalmap.entrySet()){
				key=entry.getKey();
				double prob=1.0;
				for(int j=0; j<testset.get(i).size()-1; j++){
					
					double oldprob=prob;
					double currentprob=prob;
					for(int k=0; k<entry.getValue().size(); k++){
						//if attributes are the same multiply by probability of class given attribute
						if(entry.getValue().get(k).getCol()==j){
							if( testset.get(i).get(j).equals(entry.getValue().get(k).getAttribute())){
								prob*=entry.getValue().get(k).getProb();
								currentprob=prob;
								break;
							}
						}
					}
					//here is where smoothing needs to happen, if we get through j w/o change to prob
					//smoothing will be required code does not implement going back to smooth all data
					if(oldprob==currentprob){
						prob*=(1/this.classes.size());
						trigger=1;
					}
				}
				where++;
				if(where<this.totalmap.size()){
					AttributeClassPair tester= new AttributeClassPair(key, key, i);
					tester.setProbability(prob);
					probs[where]=tester;
				}
			//when done calculating probability that instance is class store in probs
			}
			String guessed=getMax(probs).getClassifier();
			this.actualclass.add(cls);
			this.predictedclass.add(guessed);
		}
	}
	
	//this is a class to implement running the algorithm on all the data
	//first runs class total then calculates probabilities then
	//runs against test set
	public void testSetRunner(){
		for(int i=0; i<this.foldedmap.size(); i++){
			classTotal(i);
			calcProb();
			runTestSet(i);
		}
	}
	
	//finding highest probability
	public AttributeClassPair getMax(AttributeClassPair[] probs){
		AttributeClassPair maxprob=probs[0];
		double max=0.0;
		for(AttributeClassPair prob: probs){
			if(prob.getProb()>=max){
				max=prob.getProb();
				maxprob=prob;
			}
		}
		return maxprob;
	}
	
	//function will populate matrix list wih all possible combinations of matrices
	public void makeMatrices(){
		String actual="";
		String pred="";
		for(int i=0; i<this.classes.size(); i++){
			actual=this.classes.get(i);
			for(int j=i; j<this.classes.size(); j++){
				pred=this.classes.get(j);
				//only make matrices where values are unique
				if(!actual.equals(pred)){
					ConfusionMatrix mat=new ConfusionMatrix(0,0,0,0);
					mat.setAt2(actual);
					mat.setAt1(pred);
					this.confusionM.add(mat);
				}
			}
		}
	}
	
	/*public void Smooth(){
		for(int i=0; i<this.uniques.size(); i++){
			for(int j=0; j<this.uniques.get(i).size(); j++){
				
			}
		}
	}*/
	
	//this function will calculate the rates for a matrix
	public void calcMatrix(int index){
		ConfusionMatrix temp=this.confusionM.get(index);
		String attr2= temp.getAt2();
		String attr1= temp.getAt1();
		for(int i=0;i<this.actualclass.size(); i++){
			//actual class of instance
			String a=this.actualclass.get(i);
			//class predicted by algorithm
			String p=this.predictedclass.get(i);
			//make sure we have the right info for this matrix
			if(a.equals(attr1) || p.equals(attr1)){
						//TP
						if(attr1.equals(a) && p.equals(a)){
							temp.addTP();
						}
						//FN
						else if(attr1.equals(a) && !p.equals(a)){
							temp.addFN();
						}
						//FP
						else if(attr2.equals(a) && !p.equals(a)){
							temp.addFP();
						}
						//TN
						else if(attr2.equals(p) && a.equals(p)){
							temp.addTN();
						}
					}
				}	
			
	}
	
	//this function is to populate all matrices with their respecitive information
	public void calcAllMatrices(){
		for(int i=0;i<this.confusionM.size();i++){
			calcMatrix(i);
		}
	}
	
	//take averages from all matrices
	public void runMeasures(){
		ArrayList<Double> acc=new ArrayList<Double>();
		ArrayList<Double> rec=new ArrayList<Double>();
		ArrayList<Double> f1=new ArrayList<Double>();
		double accavg=0.0;
		double recavg=0.0;
		double f1avg=0.0;
		
 		for(ConfusionMatrix cm: this.confusionM){
 			f1.add(cm.calculateF1());
 			acc.add(cm.accuracy());
 			rec.add(cm.recall());
		}
 		
 		for(Double d: acc){
 			accavg+=d;
 		}
 		accavg/=acc.size();
 		for(Double r:rec){
 			recavg+=r;
 		}
 		recavg/=rec.size();
 		for(Double f: f1){
 			f1avg+=f;
 		}
 		f1avg/=f1.size();
 		this.accTotal=accavg;
 		this.f1Total=f1avg;
 		this.recallTotal=recavg;
	}
	
	public void printSets(){
		System.out.println(this.actualclass.toString());
		System.out.println(this.predictedclass.toString());
	}
	
	public String getConfusionM0(){
		return this.confusionM.get(0).toString();
	}
	
	public double getAcc(){
		return this.accTotal;
	}
	
	public double getRecall(){
		return this.recallTotal;
	}
	public double getF1(){
		return this.f1Total;
	}
	
	
}
	

