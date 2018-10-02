/*this class is to make a confusion matrix for the data.
//it uses 4 categories TP= true positive, TN= True negative,
 * FP= false positive, FN= false negative
 */
public class ConfusionMatrix {

	private int TP;
	private int TN;
	private int FP;
	private int FN;
	private String actual;
	private String predicted;
	
	public ConfusionMatrix(int TP, int TN, int FP, int FN){
		this.TP=TP;
		this.TN=TN;
		this.FP=FP;
		this.FN=FN;
	}
	
	public void setAt2(String actual){
		this.actual=actual;
	}
	
	public void setAt1(String predicted){
		this.predicted=predicted;
	}
	
	public String getAt1(){
		return this.predicted;
	}
	
	public String getAt2(){
		return this.actual;
	}
	
	//add to num TP
	public void addTP(){
		this.TP++;
	}
	
	//add to num TN
	public void addTN(){
		this.TN++;
	}
	
	//add to num FN
	public void addFN(){
		this.FN++;
	}
	
	//add to num FP
	public void addFP(){
		this.FP++;
	}
	
	public double calculateF1(){
		double f1=(2*this.TP)/(2*this.TP+this.FN+this.FP);
		return f1;
	}
	
	//calculate the recall rate
	public double recall(){
		double rec=this.TP/(this.FN+this.TP);
		return rec;
	}
	
	//calculate the accuracy 
	public double accuracy(){
		double acc= (this.TP+this.TN)/(this.TP+this.TN+this.FN+this.FP);
		return acc;
	}
	
	//print out results
	public String calculations(){
		double acc=accuracy();
		double rec=recall();
		double f1=calculateF1();
		String str=String.format("accuracy=%f\n recall=%f\n F1 measure=%f\n", acc, rec, f1);
		return str;
	}
	
	//they are equal matrices if have same actual and predicted classes
	@Override
	public boolean equals(Object Other){
		if(Other!=null && Other instanceof ConfusionMatrix){
			ConfusionMatrix test=(ConfusionMatrix)Other;
			if(test.getAt1().equals(this.getAt1())){
				if(test.getAt2().equals(this.getAt2())){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString(){
		String str="TP\t\tFP\t\tTN\t\tFN\n";
		String str2=String.format("%d\t\t%d\t\t%d\t\t%d", this.TP, this.FP, this.TN, this.FN);
		System.out.println("");
		return str+str2;
	}
}
