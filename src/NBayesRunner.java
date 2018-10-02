import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//an executing class for NBayes 
public class NBayesRunner {

	public static void main(String[] args) throws IOException {
		Scanner infile = null;
		if(args.length<1){
			infile=new Scanner(System.in);
			System.out.println("Please enter a source file hit the ENTER ");
			args=new String[1];
			//different data sets input here
			//args[0]=infile.nextLine();
			args[0]="C:/Users/Curtis/Desktop/484 Datasets/cars.csv";
			//args[0]="C:/Users/Curtis/Desktop/484 Datasets/adult-census-income.csv";
			//args[0]="C:/Users/Curtis/Desktop/484 Datasets/Abalone.csv";
			//args[0]="C:/Users/Curtis/Desktop/484 Datasets/Dataset_spine.csv";
			//args[0]="C:/Users/Curtis/Desktop/484 Datasets/falldeteciton.csv";
			//args[0]="C:/Users/Curtis/Desktop/484 Datasets/HR Analytics.csv";
			//args[0]="C:/Users/Curtis/Desktop/484 Datasets/Mushroom Dataset.csv";
			//args[0]="C:/Users/Curtis/Desktop/484 Datasets/StudentPerformance.csv";
			//args[0]="C:/Users/Curtis/Desktop/484 Datasets/vgsales.csv";
			//args[0]="C:/Users/Curtis/Desktop/484 Datasets/WA_Fn-UseC_-HR-Employee-Attrition.csv";
		}
		
		//data relevant to above datasets in number of items
		CsvReader c=new CsvReader(args[0],7,1730);
		//CsvReader c=new CsvReader(args[0],15,32563);
		//CsvReader c=new CsvReader(args[0],8,4177);
		//CsvReader c=new CsvReader(args[0],13,312);
		//CsvReader c=new CsvReader(args[0],7,16384);
		//CsvReader c=new CsvReader(args[0],10,15001);
		//CsvReader c=new CsvReader(args[0],22,8126);
		//CsvReader c=new CsvReader(args[0],17,482);
		//CsvReader c=new CsvReader(args[0],9,16600);
		//CsvReader c=new CsvReader(args[0],7,1730);
		
		//run processing
		c.runDataFillByCols(0);
		c.runDataFillByCols(1);
		
		//pass data to NBayes to accumulate class/attribute counts
		NBayes runBayes=new NBayes(c);
		runBayes.checkContinuous();
		runBayes.populateMaps();
		
		//pass data to classifier
		NaiveClassifier classifier=new NaiveClassifier(runBayes);
		classifier.testSetRunner();
		classifier.makeMatrices();
		classifier.calcAllMatrices();
		classifier.runMeasures();
	
		
		System.out.println(String.format("Accuracy of this data is=%f\n"
				+ "Recall of this data is %f\nF1 Measure for this data is %f\n"
				, classifier.getAcc(), classifier.getRecall(), classifier.getF1()));
		
		System.out.println("Data successfully stored");

		infile.close();
		System.exit(0);
	}

}
