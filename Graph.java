
import java.util.Iterator;

/**
 *
 * @author Garrett
 * @param <T>
 */
public interface Graph<T> {
    public int size();
    public void clear();
    public boolean contains(T obj);
    public void addNode(T obj);
    public boolean addEdge(Node n1, Node n2);
    public Node <T> getNode(T obj);
    public Node <T> removeNode(T obj);
    public boolean removeNode(Node <T> node);
    public boolean connected();
    public Iterator<T> dftIterator(T startpoint);
    public Iterator<T> bftIterator(T startpoint);
}


