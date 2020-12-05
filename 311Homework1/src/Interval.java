/**
 * The Interval class represents intervals.
 * @author  Brad Warren: bawarren@iastate.edu
 *
 */
public class Interval {
	public int high;
	public int low;
	
	/**
	 * Constructor of an interval that takes in low and high as int parameters
	 * @param low
	 * @param high
	 */
	public Interval(int low, int high){
		this.low = low;
		this.high = high;
	}
	
	/**
	 * Returns low value of interval
	 * @return
	 */
	public int getLow() {
		return low;
	}
	
	/**
	 * Returns high value of interval
	 * @return
	 */
	public int getHigh() {
		return high;
	}
}
