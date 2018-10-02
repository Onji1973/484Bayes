import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


//CsvReader is designed to read .csv file format data and parse it
public class CsvReader {

	private BufferedReader br;
	private int numattributes;
	private int numinstances;
	private String filename;
	private ArrayList<ArrayList<String>> databycols;
	private ArrayList<ArrayList<String>> databyrows;
	
	//constructor must be passed a filename, a number of attributes in that file
	//and the number of items in the file so if there are 5000 items with 4 attributes
	// the usage is filename, 4, 5000
	public CsvReader(String filename, int numattributes, int numinstances){
	    this.numattributes=numattributes;
	    this.numinstances=numinstances;
	    this.filename=filename;
	    this.databycols=new ArrayList<ArrayList<String>>();
	    this.databyrows=new ArrayList<ArrayList<String>>();
	    try{
	    	this.br= new BufferedReader(new FileReader(filename));
	    }
	    catch (IOException e){
	    	System.out.println("CSV Read Error: Filename was not read in"
	    			+ " correctly");
	    }
	    initData();
	}
	    
	//set up ArrayList of ArrayLists organized by columns and by rows
	//so data is stored from 2 different angles
	public void initData(){
		for(int i=0; i<this.numattributes; i++){
			this.databycols.add(i, new ArrayList<String>());
		}
		for(int i=0; i<this.numinstances; i++){
			this.databyrows.add(i, new ArrayList<String>());
		}
	}

	//return a single row from csv file
	public ArrayList<String> readALine() throws IOException{
		ArrayList<String> line=new ArrayList<String>();
		String fromfile="";
		fromfile=this.br.readLine();
		String[] prepared= fromfile.split(",");
		for(String attr: prepared){
			line.add(attr);
		}
		return line;
	}
	
	//these 2 will fill in column by column the data found 
	//will be useful for classifying data types/taking averages and getting
	//a total count of attribute subtypes
	public void runDataFillByCols(int mode) throws IOException{
		int index=0;
		if(mode==0){
			while(index<this.numinstances){
				ArrayList<String> line=readALine();
				fillInDataByCols(line, index);
				index++;
			}
		}
		if(mode==1){
			while(index<this.numinstances){
				ArrayList<String> line=readALine();
				fillInDataByRows(line, index);
				index++;
			}
		}
		//after call reset br to beggining of file.
		this.br=new BufferedReader(new FileReader(filename));
	}
	
	public void fillInDataByCols(ArrayList<String> line, int index){
		for(int j=0; j<this.numattributes; j++){
			this.databycols.get(j).add(index,line.get(j));
		}
	}
	
	//these 2 will fill in row by row as seen in file
	public void fillInDataByRows(ArrayList<String> line, int index){
		for(int j=0; j<line.size(); j++){
			this.databyrows.get(index).add(j,line.get(j));
		}
	}
	
	public ArrayList<ArrayList<String>> getDataByCols(){
		return this.databycols;
	}
	
	public ArrayList<ArrayList<String>> getDataByRows(){
		return this.databyrows;
	}
	
	public int getNumInstances(){
		return this.numinstances;
	}
	
	public int getNumAttributes(){
		return this.numattributes;
	}
	
	public String printDataByCols(){
		StringBuilder datastr=new StringBuilder();
		for(ArrayList<String> arr: this.databycols){
			datastr.append(arr.toString() + "\n");
		}
		return datastr.toString();
		
	}
	
	public String printDataByRows(){
		StringBuilder datastr=new StringBuilder();
		for(ArrayList<String> arr: this.databyrows){
			datastr.append(arr.toString() +"\n");
		}
		return datastr.toString();
		
	}

}

	

