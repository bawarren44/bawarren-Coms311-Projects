
import java.util.Random;

/**
 * The Node class represents the nodes of the treap.
 * @author  Brad Warren: bawarren@iastate.edu
 *
 */
public class Node {
	public Interval interv;
	public int priority;
	public Node parent;
	public Node left;
	public Node right;
	public int imax;
	public int height;
	
	/**
	 * Constructor that takes an Interval object i as its parameter. The constructor must generate a priority for the node.
	 *  Therefore, after creation of a Node object, getPriority() (defined below) must return the priority of this node.
	 * @param interval
	 */
	public Node(Interval interval){
		this.interv = interval;
		parent = null;
		left = null;
		right = null;
		this.height = 0;
		Random rand = new Random();
	    this.priority = rand.nextInt(Integer.MAX_VALUE) + 1; //Keeps value from ever being 0 and goes to Integer.MAX_VALUE
	}
	
	/**
	 * Returns parent of a node
	 * @return
	 */
	public Node getParent() {
		return parent;
	}
	
	/**
	 * Returns left child of a node
	 * @return
	 */
	public Node getLeft() {
		return left;
	}
	
	/**
	 * Returns right child of a node
	 * @return
	 */
	public Node getRight() {
		return right;
	}
	
	/**
	 * Returns interval of a node
	 * @return
	 */
	public Interval getInterv() {
		return interv;
	}
	
	/**
	 * Returns imax value of a node
	 * @return
	 */
	public int getIMax() {
		return imax;
	}
	
	/**
	 * Returns priority of a node
	 * @return
	 */
	public int getPriority() {
		return priority;
	}
	
}
