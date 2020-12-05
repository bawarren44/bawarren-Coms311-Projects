import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * The IntervalTreap class represents an interval treap.
 * @author  Brad Warren: bawarren@iastate.edu
 *
 */
public class IntervalTreap {
	private Node root;
	private int size;
	
	/**
	 * Constructor method used to create a IntervalTreap
	 */
	public IntervalTreap(){
		root = null;
	}
	
	/**
	 * Returns a reference to the root node.
	 * @return
	 */
	public Node getRoot() {
		return root;
	}
	
	/**
	 * Returns the number of nodes in the IntervalTreap.
	 * @return
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Returns the height of the IntervalTreap
	 * @return
	 */
	public int getHeight() {
		if(root != null) {
			return root.height;
		}else {
			return 0;
		}
	}
	
	
	/**
	 * Adds node z, whose interv attribute references an Interval object, to the IntervalTreap.
	 * @param z
	 */
	public void intervalInsert(Node z) {
		z.imax = z.interv.high;
		Node x = this.root;
		Node use = null;
		
		while(x != null) { //Traverses the treap down to the correct placement of z onto the treap
			use = x;
			x.imax = Math.max(x.imax,z.interv.high);
			if(x.interv.low > z.interv.low) {
				x = x.left;
			}else {
				x = x.right;
			}
		}
		
		//Sets z to be the child of use
		z.parent = use;
		if(use == null) { //Sets z to be the root if no other node exists
			this.root = z;
		}else if(z.interv.low < use.interv.low) {
			use.left = z;
		}else {
			use.right = z;
		}
		size++;
		x = z;

		if(x != this.root) {
			while(x.priority <= x.parent.priority) { //As treap property is violated
	            if(x == x.parent.left) { //Right rotation on parent
	                rightRotate(x);
	            } else { //Left rotation on parent
	                leftRotate(x);
	            }
	            if(x.parent == null) {
	            	break;
	            }
	        }
		}
		
		//Fixing height and imax for nodes that the traverse above doesnt hit. O(logn)
		recursiveDetermineHImax(x);
	}
		
	/**
	 * Removes node z from the interval treap. This operation maintains the required interval treap properties. The running time of this method should be O(logn) on an n-node interval treap.
	 * @param z
	 */
	public void intervalDelete(Node z) {
		if(z == this.root && z.left == null && z.right == null) { //Case where z is the only node on the treap
			this.root = null;
			size--;
			return;
		}
		
		if(z.left == null && z.right == null) { //Case 1
			if(z == z.parent.left) {
				z.parent.left = null;
			} else if(z == z.parent.right) {
				z.parent.right = null;
			}
			recursiveDetermineHImax(z.parent);
		}else if(z.left != null && z.right == null) { //Case 2 left side isnt null
			if(z == z.parent.left) {
				z.left.parent = z.parent;
				z.parent.left = z.left;
			}else if(z == z.parent.right) {
				z.left.parent = z.parent;
				z.parent.right = z.left;
			}
			recursiveDetermineHImax(z.left);
		}else if(z.left == null && z.right != null) { //Case 2 right side isnt null
			if(z == z.parent.left) {
				z.right.parent = z.parent;
				z.parent.left = z.right;
			}else if(z == z.parent.right) {
				z.right.parent = z.parent;
				z.parent.right = z.right;
			}
			recursiveDetermineHImax(z.right);
		}else { //Case 3 both z's children aren't null
			Node left = z.left;
			Node right = z.right;
			Node parent = z.parent;
			Node successor = Minimum(z.right);
			Node sucParent = successor.parent;
			boolean checkLeft = false; //False if z = parent.right, else true
			
			if(successor == z.right) { //When successor is the child of deleted node
				if(z.left != null) {
					successor.left = left;
					left.parent = successor;
				}
			}else if(successor == successor.parent.left) { //Regular case 
				sucParent.left = null;
				if(successor.right != null) {
					sucParent.left = successor.right;
					successor.right.parent = successor.parent;
				}
				if(left != null) {
					left.parent = successor;
					successor.left = left;
				}
				if(right != null) {
					right.parent = successor;
					successor.right = right;
				}
			}
			
			if(z == this.root) { //Where z was the root
				this.root = successor;
				successor.parent = null;
			}else { //Where z is anywhere else but the root
				if(z == parent.left) {
					parent.left = successor;
					checkLeft = true;
				}else if(z == parent.right) {
					parent.right = successor;
					checkLeft = false;
				}
				successor.parent = parent;
			}
			
			//Fix height and imax of everything effected. O(logn) time
			if(checkLeft == false) {
				recursiveDetermineHImax(sucParent.right);
			}else {
				recursiveDetermineHImax(sucParent.left);
			}
			
			//Rotations to get the deleted nodes replacement to go down according to priority. O(logn) time.
			while(goDown(successor)) {
				if(successor.left != null && successor.right != null) {
					if(successor.left.priority < successor.right.priority) {
						rightRotate(successor.left);
					}else {
						leftRotate(successor.right);
					}
				}else if(successor.left != null && successor.left.priority < successor.priority){
					rightRotate(successor.left);
				}else if(successor.right != null && successor.right.priority < successor.priority){
					leftRotate(successor.right);
				}
			}	
		}
		size--;
	}
	
	/**
	 * Returns a reference to a node x in the interval treap such that x:interv overlaps interval i, or null if no such element is in the treap.
	 * @param i
	 * @return
	 */
	public Node intervalSearch(Interval i) {
		if(root == null) {
            return null;
        }
		Node x = root;
		//Checks if the interval of x overlaps i
		while(x != null && !(i.low <= x.interv.high && x.interv.low <= i.high)) {
			if(x.left != null && x.left.imax >= i.low) {
				x = x.left;
			}else {
				x = x.right;
			}
		}
		return x;
	}
	
	/**
	 * Returns a reference to a Node object x in the IntervalTreap such that x:interv:low = i:low and x:interv:high = i:high, or null if 
	 * no such node exists.
	 * @param i
	 * @return
	 */
	public Node intervalSearchExactly(Interval i) {
		if(root == null) {
            return null;
        }
		Node x = this.root;
		//If x,interv overlaps i
		while( x != null && x.interv.low != i.low && x.interv.high != i.high) {
			if(i.high > x.imax) {
				return null;
			}else if(i.low < x.interv.low) { //Case for going left
				x = x.left;
			}else if(i.low > x.interv.low) { //Case for going right
				x = x.right;
			}else {
				return null;
			}
		}
		return x;
	}
	
	/**
	 * Returns a list all intervals in the IntervalTreap that overlap i. Calls recursiveOverlappingIntervals to construct overlappers list to return.
	 * @param i
	 * @return
	 */
	public List<Interval> overlappingIntervals(Interval i) {
		if(root == null) {
			return null;
		}
		//List of overlapping intervals
		List<Interval> overlappers = new ArrayList<>();
		recursiveOverlappingIntervals(i,this.root,overlappers); //Recursive call to get all of the overapping intervals

	    return overlappers;
	}
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// Helper methods below //////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Recursive method used to construct the overlappers list filled with intervals within the tree that overlap interval i.
	 * @param i
	 * @param z
	 * @param overlappers
	 */
	private void recursiveOverlappingIntervals(Interval i, Node z, List<Interval> overlappers) {
		if(z.left != null) { //Checks left side for overlapping intervals
			Interval bottomUp = new Interval(Minimum(z.left).interv.low, z.left.imax); //Creates an interval of the lowest interval.low value and imax of z.left 
			if(i.low <= bottomUp.high && bottomUp.low <= i.high) {
				recursiveOverlappingIntervals(i,z.left,overlappers);
			}
		}
		
		if(i.low <= z.interv.high && z.interv.low <= i.high) { //Checks if z.interv overlaps i
			overlappers.add(z.interv); //Adds z.interv if it overlaps i
		}
		
		if(z.right != null) { //Checks right side for overlapping intervals
			Interval bottomUp = new Interval(Minimum(z.right).interv.low, z.right.imax); //Creates an interval of the lowest interval.low value and imax of z.right
			if(i.low <= bottomUp.high && bottomUp.low <= i.high) {
				recursiveOverlappingIntervals(i,z.right,overlappers);
			}
		}
	}
	
	/**
	 * Determines the imax of the Node, imax equals:
	 * 
	 * 1. z.interv.high, if z is a leaf
	 * 2. max{z.interv.high,z.left.imax}, if z.right == null
	 * 3. max{z.interv.high,z.right.imax}, if z.left == null
	 * 4. max{z.interv.high,z.left.imax,z.right.imax} otherwise.
	 * 
	 * @param z
	 */
	private void determineImax(Node z) {
		if(z.left == null && z.right == null) {
			z.imax = z.interv.high;
		}else if(z.right == null) {
			z.imax = Math.max(z.interv.high,z.left.imax);
		}else if(z.left == null) {
			z.imax = Math.max(z.interv.high, z.right.imax);
		}else {
			int max1 = Math.max(z.interv.high, z.left.imax);
			z.imax = Math.max(max1,z.right.imax);
		}
	}
	
	/**
	 * Determines the height of the Node
	 * @param z
	 */
	private void determineHeight(Node z) {
	    int left = 0, right = 0;
		if(z.left == null && z.right == null) { //Case for left and right = null
			z.height = 0;
			return;
		}
		if(z.left != null) { //Case for when z.left exists
			left = z.left.height;
		}
		if (z.right != null) { //Case for when z.right exists
			right = z.right.height;
		}
		z.height = Math.max(left,right) + 1; //Determines z.height
	}
	
	/**
	 * Follows up the path that n took down the tree to fix height and imax.
	 * @param n
	 */
	private void recursiveDetermineHImax(Node z) {
        if(z == null) {
            return;
        }
        determineHeight(z); //Determines height
        determineImax(z); //Determines imax
        recursiveDetermineHImax(z.parent);
    }
	
	/**
	 * Gets the successor
	 * @param node
	 * @return
	 */
	private Node Minimum(Node z) { 
        Node last = null; 
        while (z != null) { 
        	last = z;
            z = z.left; 
        } 
        return last; 
    }
	
	/**
	 * Determines if the replacement for the deleted node needs to continue down the treap. Depends on priority.
	 * @param z
	 * @return
	 */
	private boolean goDown(Node z) {
		if(z.left == null && z.right == null) { //If z == leaf
			return false;
		}else if(z.left == null && (z.right != null && z.right.priority > z.priority)) { //z's only child.priority > z.priority
			return false;
		}else if(z.right == null && (z.left != null && z.left.priority > z.priority)) { //z's only child.priority > z.priority
			return false;
		}else if((z.right != null && z.right.priority > z.priority) 
				&& (z.left != null && z.left.priority > z.priority)){ //Boths z's children.priority > z.priority
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * Carries out a left rotation at a node such that after the rotation its former
	 * parent becomes its left child.
	 * 
	 * Calls link().
	 * 
	 * @param current
	 */
	private void leftRotate(Node current) {
		if (current.parent.parent != null) { // If currents grandparent is not the root
			if (current == current.parent.right) { // Current is a right child
				Node parent = current.parent;
				Node parentP = current.parent.parent;
				Node left = current.left;
				//Sets current and parent relationship
				current.left = parent;
				parent.parent = current;
				parent.right = null;
				if (left != null) { //Sets previous left and parent relationship
					parent.right = left;
					left.parent = parent;
				}
				if(parentP.left != null && parentP.left.equals(parent)) { //Sets new parent and current relationship
					parentP.left = current;
					current.parent = parentP;
				}else if(parentP.right != null && parentP.right.equals(parent)) { //Sets new parent and current relationship
					parentP.right = current;
					current.parent = parentP;
				}
				
				//Determines height and imax
                if(left != null) {
                	determineHeight(left);
                    determineImax(left);
                }
                determineHeight(parent);
                determineImax(parent);
				determineHeight(current);
                determineImax(current);
                determineHeight(parentP);
                determineImax(parentP);
			}
		} else { //parent is the root
			if (current == current.parent.right) { // Current is a right child
				Node parent = current.parent;
				Node left = current.left;
				//Sets current and parent relationship
				current.left = parent;
				parent.parent = current;
				parent.right = null;
				if (left != null) { //Sets previous left and parent relationship
					parent.right = left;
					left.parent = parent;
				}
				current.parent = null; //Sets current to root
				this.root = current;
				
				//Determine height and imax
				if(left != null) {
                	determineHeight(left);
                    determineImax(left);
                }
                determineHeight(parent);
                determineImax(parent);
				determineHeight(current);
                determineImax(current);
			}
		}
	}
	
	
	/**
	 * Carries out a right rotation at a node such that after the rotation its
	 * former parent becomes its right child.
	 * 
	 * Calls link().
	 * 
	 * @param current
	 */
	private void rightRotate(Node current) {
		if (current.parent.parent != null) { // If currents grandparent is not the root
			if (current == current.parent.left) { // Current is a left child
				Node parent = current.parent;
				Node parentP = current.parent.parent;
				Node right = current.right;
				//Sets current and parent relationship
				current.right = parent;
				parent.parent = current;
				parent.left = null;
				if (right != null) { //Sets parent and previous right relationship  
					parent.left = right;
					right.parent = parent;
				}
				if(parentP.left != null && parentP.left.equals(parent)) { //Sets new parent and current relationship
					parentP.left = current;
					current.parent = parentP;
				}else if(parentP.right != null && parentP.right.equals(parent)){ //Sets new parent and current relatrionship
					parentP.right = current;
					current.parent = parentP;
				}
				
				//Sets height and imax values
				if(right != null) {
	                	determineHeight(right);
	                	determineImax(right);
	            }
                determineHeight(parent);
                determineImax(parent);
				determineHeight(current);
                determineImax(current);
                determineHeight(parentP);
                determineImax(parentP);
               
			}
		} else { // Current's parent is the root
			if (current == current.parent.left) { // Current is a left child
				Node parent = current.parent;
				Node right = current.right;
				//Sets current and parent relationship
				current.right = parent;
				parent.parent = current;
				parent.left = null;
				if (right != null) { //Sets parent and previous right relationship
					parent.left = right;
					right.parent = parent;
				}
				current.parent = null;
				this.root = current; //Sets current as root
				
				//Sets height and imax values
				if(right != null) {
                	determineHeight(right);
                    determineImax(right);
                }
                determineHeight(parent);
                determineImax(parent);
				determineHeight(current);
                determineImax(current);
			}
		}
	}	
}