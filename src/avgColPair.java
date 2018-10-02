//simple convenience class to gather avgs from columns
//will report backt he average and the number of the specific
//column.
public class avgColPair {
	
	private double avg;
	private int colnum;
	
	public avgColPair(double avg, int colnum){
		this.avg=avg;
		this.colnum=colnum;
	}
	
	public double getAvg(){
		return this.avg;
	}
	
	public int getColNum(){
		return this.colnum;
	}
	
}
