import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/**
 *
 * @author Garrett
 * @param <T>
 */
public class DirGraph<T> implements Graph<T>{
    private Object[] nodes; //Will hold all of the different nodes
    public static int DEFAULT_CAPACITY = 25;
    private int capacity;   //Max number of nodes that can be held
    private int size;   //Current number of nodes
    private boolean[][] adjMatrix;  //Holds the directed connections between nodes
    
    //No-args constructor creates a DirGraph of default capacity 25
    public DirGraph(){
        this(DEFAULT_CAPACITY);
    }
    
    //Constructor creates a DirGraph of parameter initialCapacity
    public DirGraph(int initialCapacity){
        this.capacity = initialCapacity;
        nodes = new Object[capacity];
        adjMatrix = new boolean[capacity][capacity];
    }

    //Returns current number of nodes in DirGraph
    @Override
    public int size() {
        return size;
    }

    //Set size to 0, clear DirGraph by creating brand new arrays
    @Override
    public void clear() {
        size = 0;
        adjMatrix = new boolean[capacity][capacity];
        nodes = new Object[capacity];
    }

    //Iterate through nodes[] and comparing with obj, 
    //first checking if the same object is contained within,
    //and second, checking if they have the same value.  If so, return true; 
    //otherwise, obj is not contained in nodes[], so return false
    @Override
    public boolean contains(T obj) {
        for(int i = 0; i < size; i++){
            if(obj == ((Node)nodes[i]).getValue())
                return true;
            else if(obj != null && obj.equals(((Node)nodes[i]).getValue())){
                return true;
            }
        }
        return false;
    }

    //Creates and adds a new node object to nodes[] if not at capacity
    @Override
    public void addNode(T obj) {
        if(size == capacity)
            throw new IllegalStateException("At capacity");
        Node<T> n = new Node<>(obj);
        nodes[size] = n;
        size++;
    }

    //Creates a one-way, directed edge from n1 to n2, as long as both nodes exist
    //Returns true if successful, false if a node doesn't exist
    @Override
    public boolean addEdge(Node n1, Node n2) {
        int index1 = getIndexOfNode(n1);
        int index2 = getIndexOfNode(n2);
        if(index1 < 0 || index2 < 0)
            return false;
        adjMatrix[index1][index2] = true;
        return true;
    }

    //Does the same process as contains to find a node, but now if there is a match,
    //the node is returned; if not found, returns null
    @Override
    public Node<T> getNode(T obj) {
        for(int i = 0; i < size; i++){
            if(obj == ((Node)nodes[i]).getValue())
                return (Node<T>)nodes[i];
            else if (obj != null && obj.equals(((Node)nodes[i]).getValue()))
                return (Node<T>)nodes[i];
        }
        return null;
    }

    //If the node exists, calls removeNode with that node to remove it 
    //Returns the removed node or null if node doesn't exist
    @Override
    public Node<T> removeNode(T obj) {
        Node<T> n = getNode(obj);
        if(n != null)
            removeNode(n);
        return n;
    }

    //First, check if node is in nodes[].  If not, return false.
    //If it is, replace it with the final node in nodes[] and decrement size.
    //Replace its row with the final row in adjMatrix.  Then, replace its column
    //with the last column.  Now, all references to the node are gone.
    //For the final row and column, which replaced the removed node, there are 
    //two copies right now.  Go through the second set of row and column,
    //and change all values to false so there is only one copy now:
    //the one that replaces the removed node.  Return true;
    @Override
    public boolean removeNode(Node<T> node) {
        int index = getIndexOfNode(node);
        if(index < 0)
            return false;
        nodes[index] = nodes[--size];
        adjMatrix[index] = adjMatrix[size];
        for(int i = 0; i < size; i++){
            adjMatrix[i][index] = adjMatrix[i][size];
            adjMatrix[i][size] = false;
        }
        for(int i = 0; i < size + 1; i++){
            adjMatrix[size][i] = false;
        }
        return true;
    }
    
    //Iterates through the node[] until a match is found
    //Returns index of matching node, or -1 if there is no match
    private int getIndexOfNode(Node n){
        for(int i = 0; i < size; i++){
            if(n == nodes[i])
                return i;
        }
        return -1;
    }

    //Returns true if, for some point P in the graph, a dft or bft traversal
    //returns all points in the graph.  Returns false if not.
    @Override
    public boolean connected() {
    //What I'm trying to do here is going through each node and calling both 
    //dftIterator and bftIterator.  If the iterator has the same number of items
    //as the size of nodes[], then all of them are reachable, so it is weakly connected,
    //and true is returned.  If all of the nodes are used, and none returned true,
    //then it is not weakly connected, so false is returned.
    
        //Keeps track of size of the iterator
        int it_size = 0;
        //Runs through each node in nodes[]
        for(int i = 0; i < size; i++){
            Iterator it = dftIterator((T)nodes[i]);
            //Count items in dftIterator
            while(it.hasNext()){
                it_size++;
                it.next();
            } 
            //If items in dftIterator equals # of nodes, return true
            if(it_size == size)
                return true;
            
            it_size = 0;    //Reset counter
            
            it = bftIterator((T)nodes[i]);
            //Count items in bftIterator
            while(it.hasNext()){
                it_size++;
                it.next();
            }
            //If items in bftIterator equals # of nodes, return true
            if(it_size == size)
                return true;
            
            //reset counter
            it_size = 0;
        }
        //None of the iterators were the right size, so return false, not weakly connected
        return false;
    }

    //Returns an iterator using Depth-first Traversal
    @Override
    public Iterator<T> dftIterator(T startpoint) {
        HashSet<T> visited = new HashSet<>();  //Keeps track of visited nodes, ensures no repeats
        Stack<T> stack = new Stack<>(); //The stack of nodes to check for a branch next
        ArrayList<T> dftOrder = new ArrayList<>(); //List to keep track of the order of the nodes
        
        //Push starting point onto the stack
        stack.push(startpoint);
        //Starting node is first in the order
        dftOrder.add(startpoint);
        //Mark startpoint visited
        visited.add(startpoint);
        
        //While the stack isn't empty, repeat this process
        while(!stack.empty()){
            //Pop the top of the stack
            T current = stack.pop();
            //If it has an edge with another node in the adjMatrix that hasn't been visited, push both onto stack
            for(int i = 0; i < size; i++){
                if(adjMatrix[getIndexOfNode((Node)current)][i] == true){
                    if(visited.contains((T)nodes[i]))    //If node has been visited, skip over
                        continue;
                    //If not visited, mark visited, add to order, push nodes onto stack
                    visited.add((T)nodes[i]);
                    dftOrder.add((T)nodes[i]);
                    stack.push(current);
                    stack.push((T)nodes[i]);
                }
            }
        }
        
        //Return iterator for the order
        return dftOrder.iterator();
    }

    //Returns an iterator using Breadth-first Traversal
    @Override
    public Iterator<T> bftIterator(T startpoint) {
        HashSet<T> visited = new HashSet<>();  //Keeps track of visited nodes, ensures no repeats
        LinkedList<T> queue = new LinkedList<>();   //The queue of nodes to check for branches next
        ArrayList<T> bftOrder = new ArrayList<>(); //List to keep track of the order of the nodes 
        
        //Add starting point to the queue first
        queue.add(startpoint);
        
        //Repeat this process until queue is empty
        while(!queue.isEmpty()){
            //Remove front of the queue
            T current = queue.remove();
            //If already visited, end this iteration of the loop
            if(visited.contains(current)){
                continue;
            }
            //Mark as visited now, add to the order
            visited.add(current);
            bftOrder.add(current);
            
            //If it has an edge with another node in the adjMatrix, add those nodes to the queue
            for(int i = 0; i < size; i++){
                if(adjMatrix[getIndexOfNode((Node)current)][i] == true){
                    queue.add((T)nodes[i]);
                }
            }
        }
        
        //Return the iterator for order
        return bftOrder.iterator();
    }
    
}

