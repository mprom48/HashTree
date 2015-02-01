/* HashTree Program 
 * Author: Matthew Prom
 * Adapted from HashTree class implementation details found at 
 * http://jmeter.apache.org/api/org/apache/jorphan/collections/HashTree.html
 * January 25, 2015
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileNotFoundException;
import org.json.simple.parser.ParseException;
import org.json.JSONArray;

public class HashTree implements Serializable, Map {
    protected Map data; 
    
    //creates new empty HashTree
    public HashTree() { 
        data = new HashMap(); 
    }
    
    //creates new HashTree  and adds given object as top-level node
    public HashTree(Object key) { 
        data = new HashMap(); 
        data.put(key, new HashTree()); 
    } 
    
    //Map must also be given a HashTree, or else UOE is thrown
    public void putAll(Map map) { 
        if (map instanceof HashTree) { 
            this.add((HashTree) map);  
        }
        else { 
            throw new UnsupportedOperationException(
                "You can only putAll on other HashTree objects."); 
        }
    }
    
    //Must be implemented for Map interface
    public Set entrySet() { 
        return data.entrySet(); 
    }
    
    //Must be implemented for Map interface
    public boolean containsValue(Object value) { 
        return data.containsValue(value); 
    }
    
    // adds key value pair to HashTree
    //similar to calling HashTree.add(key,value)
    public Object put(Object key, Object value) { 
        Object previous = data.get(key); 
        add(key, value); 
        return previous; 
    }
    
    //Clears all contents from HashTree
    public void clear() { 
        data.clear(); 
    }
    
    //Returns collection of all subtrees of current HashTree
    public Collection values() { 
        return data.values(); 
    }
    
    //Adds key as a node, then adds the provided HashTree to that node
    public void add(Object key, HashTree subtree){ 
        add(key); 
        getTree(key).add(subtree); 
    }
    
    // Adds all nodes and branches of given tree to HashTree
    public void add(HashTree newTree) { 
        Iterator iter = newTree.list().iterator();
        while(iter.hasNext()) { 
            Object item = iter.next(); 
            add(item); 
            getTree(item).add(newTree.getTree(item)); 
        }
    }
    
    //Creates new HashTree and adds all objects in collection as top level nodes in tree
    public HashTree(Collection keys) { 
        data = new HashMap(); 
        Iterator it = keys.iterator(); 
        while(it.hasNext()) { 
            data.put(it.next(), new HashTree()); 
        }
    }
    
    //Creates new HashTree and adds all objects in passed array as top level nodes in tree
    public HashTree(Object[] keys) { 
        data = new HashMap(); 
        for (int i = 0; i < keys.length(); i++) { 
            data.put(keys[x], new HashTree()); 
        }
    }
    
    //If HashTree contains given object as a key at top level, returns true; otherwise returns false
    public boolean containsKey(Object o) { 
        return data.containsKey(o); 
    }
    
    //if HashTree is empty, returns true; otherwise, returns false
    public boolean isEmpty() { 
        return data.isEmpty(); 
    }
    
    //Sets a key and its value in the HashTree. 
    public void set(Object key, Object value) { 
        data.put(key,createNewTree(value)); 
    }
    
    //Sets a key into HashTree and assigns it a HashTree as its subtree
    public void set(Object key, HashTree t) { 
        data.put(key, t); 
    }
    
    //Sets a key and its values into the HashTree
    public void set(Object key, Object[] values) { 
        data.put(key, createNewTree(Arrays.asList(values))); 
    }
    
    //Sets a key and its collection of values into the HashTree
    public void set(Object key, Collection values) { 
        data.put(key, createNewTree(values)); 
    }
    
    /*Sets a key and its values into the HashTree. It sets up a key in the current node, 
     * then recurses into the next node through that key, and adds second object in 
     * the array. Continues recursing in this way all the way through first array, at which 
     * point all values of second array are placed into the bottom-most node. All previous
     * keys of that bottom-most node are removed.  
     */
    public void set(Object[] treePath, Object[] values) { 
        if(treePath != null && values != null) { 
            set(Arrays.asList(treePath), Arrays.asList(values)); 
        } 
    }
    
    /*Sets a series of keys into the HashTree. It sets up the first object in the key array 
    * as a key in the current node, recurses into the next HashTree node through that key 
    * and adds the second object in the array. Continues recursing in this manner until the end 
    * of the first array is reached, at which point all the values of the second array are set 
    * as keys to the bottom-most node. All previous keys of that bottom-most node are removed.
    */
    public void set(Object[] treePath, Collection values) { 
        if(treePath != null) { 
            set(Arrays.asList(treePath), values); 
        }
    }
    //Similar to previous set methods, type of treePath and values differs.
    public void set(Collection treePath, Object[] values) { 
        HashTree tree = addTreePath(treePath); 
        tree.set(Arrays.asList(values));
    }
    
    //Sets nodes of current tree to be objects of given collection. 
    public void set(Collection values){ 
        clear(); 
        this.add(values); 
    } 
    
    //Similar to previous set methods, type of treePath and values differs.
    public void set(Collection treePath, Collection values) { 
        HashTree tree = addTreePath(treePath); 
        tree.set(values); 
    }
    
    //Adds key into HashTree at current level
    public HashTree add(Object key) { 
        if(!data.containsKey(key)) { 
            HashTree newTree = createNewTree(); 
            data.put(key, newTree); 
            return newTree; 
        }
        else { 
            return getTree(key); 
        }
    }
    
    //Adds array of objects as nodes in tree at current level
    public void add(Object[] keys) { 
        for(int i = 0; i < keys.length(); i++) { 
            add(keys[i]); 
        }
    }
    
    //Adds collection of keys to tree at current level
    public void add(Collection keys) { 
        Iterator iter = keys.iterator(); 
        while (iter.hasNext()){ 
            add(iter.next()); 
        }
    }
    
    /*Adds key and its value to the HashTree. First argument becomes a node at current level, 
    * and all values in the array added to new node. 
    */
    public void add(Object key, Object[] values) { 
        add(key); 
        getTree(key).add(values); 
    }
    
    //Adds key as node at current level, then all values in the collection as nodes of new node
    public void add(Object key, Collection values) { 
        add(key); 
        getTree(key).add(values); 
    }
    
    /*Adds series of nodes into tree using given path. First argument is an array that represents a 
    * path to specific node in the tree. If path doesn't exist, it's created. At end of path, all objects
    * in second argument are added as nodes.
    */ 
    public void add(Object[] treePath, Object[] values) { 
        if (treePath != null) { 
            add(Arrays.asList(treePath), Arrays.asList(values)); 
        }
    }
    //Same basic idea as previous method, different types for parameters
    public void add(Object[] treePath, Collection values) { 
        if (treePath != null) { 
            add(Arrays.asList(treePath), values); 
        }
    }
    
    public HashTree add(Object[] treePath,Object value) {
        return add(Arrays.asList(treePath),value);
    }
    public void add(Collection treePath, Object[] values) { 
        HashTree tree = addTreePath(treePath); 
        tree.add(Arrays.asList(values)); 
    }
    public HashTree add(Collection treePath, Object value) { 
        HashTree tree = addTreePath(treePath); 
        return tree.add(value); 
    } 
    public void add(Collection treePath, Collection values) { 
        HashTree tree = addTreePath(treePath); 
        tree.add(values); 
    }
    protected HashTree addTreePath(Collection treePath) { 
        HashTree tree = this; 
        Iterator iter = treePath.iterator(); 
        while(iter.hasNext()) { 
            Object temp = iter.next(); 
            tree.add(temp); 
            tree = tree.getTree(temp); 
        }
        return tree; 
    }
    
    //Gets HashTree mapped to given key
    public HashTree getTree(Object key) { 
        return (HashTree) data.get(key); 
    }
    
    //Returns HashTree object associated w given key
    public Object get(Object key) { 
        return getTree(key); 
    }
    
    /* Gets the HashTree object mapped to the last key in the array by recursing through 
     * the HashTree structure one key at a time.
     */
    public HashTree getTree(Object[] treePath) { 
        if(treePath != null) { 
            return getTree(Arrays.asList(treePath)); 
        }
        else { 
            return this; 
        }
    }
    
    /* Creates a new tree. This method exists to allow inheriting classes to generate the 
     * appropriate types of nodes.
     */
    protected HashTree createNewTree() { 
        return new HashTree(); 
    }
    protected HashTree createNewTree(Object key) { 
        return new HashTree(key); 
    }
    protected HashTree createNewTree(Collection values) { 
        return new HashTree(values); 
    }
    
    /* Gets the HashTree object mapped to the last key in the SortedSet by recursing through the 
     * HashTree structure one key at a time.
     */
    public HashTree getTree(Collection treePath) { 
        return getTreePath(treePath); 
    }
    //Gets collection of all keys in current HashTree node.
    public Collection list() { 
        return data.keySet(); 
    }
    
    //Gets a set of all keys in HashTree mapped to given key of current HashTree.  
    public Collection list(Object key) { 
        HashTree temp = (HashTree) data.get(key); 
        if (temp != null) { 
            return temp.list(); 
        }
        else { 
            return null; 
        }    
    }
    
    //Removes entire branch specified by given key. 
    public Object remove(Object key) { 
        return data.remove(key); 
    }
    
    /* Recurses down into the HashTree stucture using each subsequent key in the array of keys, 
     * and returns the Set of keys of the HashTree object at the end of the recursion. 
     */
    public Collection list(Object[] treePath) { 
        if(treePath != null) { 
            return list(Arrays.asList(treePath)); 
        }
        else { 
            return list(); 
        }
    }
    //Same as previous method, adjusted for different param type
    public Collection list(Collection treePath) { 
        return getTreePath(treePath).list(); 
    }
    
    //Replaces current key given in params with new key specified
    public void replace(Object currentKey, Object newKey) { 
        HashTree tree = getTree(currentKey); 
        data.remove(currentKey); 
        data.put(newKey, tree); 
    }
    
    //Gets array of all keys in current HashTree node
    public Object[] getArray() { 
        return data.keySet().toArray(); 
    }
    //Gets array of all keys in HashTree mapped to given key of current HashTree object
    public Object[] getArray(Object key) { 
        return getTree(key).getArray(); 
    }
    
    /* Recurses down into the HashTree stucture using each subsequent key in the array of keys, 
     * and returns an array of keys of the HashTree object at the end of the recursion.
     */
    public Object[] getArray(Object[] treePath) { 
        if (treePath != null) { 
            return getArray(Arrays.asList(treePath)); 
        }
        else { 
            return getArray(); 
        }
    }
    
    //Same idea as previous method, different parameter type
    public Object[] getArray(Collection treePath) { 
        HashTree tree = getTreePath(treePath); 
        return tree.getArray(); 
    }
    
    protected HashTree getTreePath(Collection treePath) { 
        HashTree tree = this; 
        Iterator iter = treePath.iterator(); 
        while(iter.hasNext()) { 
            Object temp = iter.next(); 
            tree = tree.getTree(temp); 
        }
        return tree; 
    }
    
    //Returns hashcode for this HashTree
    public int hashCode() { 
        return data.hashCode(); 
    }
    
    //Returns set of all keys in top level of this HashTree.
    public Set keySet() { 
        return data.keySet(); 
    }
    
    //Searches hashtree for given key
    public HashTree search(Object key){ 
        HashTree result = getTree(key); 
        if(result != null) { 
            return result; 
        }
        TreeSearcher searcher = new TreeSearcher(key); 
        try { 
            traverse(searcher);
        }
        catch(Exception e) { 
            
        }
        return searcher.getResult();
    }
    
    //Compares all objects in the tree and verifies that the two trees contain the same objects 
    //at the same tree levels. Returns true if they do, false otherwise.
    public boolean equals(Object o) { 
        if(!(o instanceof HashTree)) 
            return false; 
        
        HashTree oo = (HashTree) o; 
        if(oo.size() != this.size())
            return false; 
        return data.equals(oo.data); 
    }
    
    // Used to read objects into program
    void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException { 
        ois.defaultReadObject();
    }
    //File writing method
    void writeObject(ObjectOutputStream oos) throws IOException { 
        oos.defaultWriteObject(); 
    }
    //Returns number of top level entries in the HashTree. 
    public int size() { 
        return data.size(); 
    }
    //Allows any implementation of HashTreeTraverser interface to easily traverse (depth-first)
    //all the nodes of the HashTree
    public void traverse(HashTreeTraverser visitor) { 
        Iterator iter = list().iterator(); 
        while (iter.hasNext()) { 
            Object item = iter.next(); 
            visitor.addNode(item, getTree(item)); 
            getTree(item).traverseInto(visitor); 
        }
    }
    //Recursive method that accomplishes tree traversal
    private void traverseInto(HashTreeTraverser visitor){ 
        if (list().size() == 0) { 
            visitor.processPath(); 
        }
        else { 
            Iterator iter = list().iterator();
            while(iter.hasNext()) { 
                Object item = iter.next(); 
                visitor.addNode(item, getTree(item)); 
                getTree(item).traverseInto(visitor);
            }
        }
        visitor.subtractNode(); 
    }
    public String toString(){ 
        ConvertToString c = new ConvertToString(); 
        traverse(c); 
        return c.toString(); 
    }
    private class ConvertToString implements HashTreeTraverser { 
        StringBuffer string = new StringBuffer(getClass().getName() + "{"); 
        StringBuffer spaces = new StringBuffer(); 
        int depth = 0; 
        
        public void addNode(Object key, HashTree tree) { 
            depth++; 
            string.append("\n" + getSpaces() + key ); 
        }
        public void subtractNode() { 
            string.append("\n" + getSpaces()); 
        }
        public void processPath(){ 
        }
        public String toString() { 
            string.append("}"); 
            return string.toString();
        }
        private String getSpaces() { 
//            if (spaces.length() < depth) { 
//                while(spaces.length() < depth) { 
//                    spaces.append(" ");
//                }
//            }
//            else if (spaces.length() > depth) { 
//                spaces.setLength(depth); 
//            }
            return spaces.toString(); 
        }
    }
    private class TreeSearcher implements HashTreeTraverser { 
        Object target; 
        HashTree result; 
        
        public TreeSearcher(Object t) { 
            target = t; 
        }
        public HashTree getResult() { 
            return result; 
        }
        public void addNode(Object node, HashTree tree) { 
            result = tree.getTree(target); 
            if(result != null) {
                throw new RunTimeException("found"); 
            }
        }
        public void processPath() { 
            
        }
        public void subtractNode(){ 
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	System.out.println("01/23/15: ATL 103 OKC 93"); 
    	System.out.println("Press enter to continue...");
        try
        {
            System.in.read();
        }  
        catch(Exception e)
        {}
        Collection okc = Arrays.asList(new String[] {"Durant", "Westbrook", "Ibaka", "Adams", "Roberson", "Waiters", "Jackson", "Morrow", "Perkins", "Lamb", "Jones", "Smith" }); 
        HashTree tree = new HashTree(okc); 
        tree.add("Durant", Arrays.asList(new String[] {"21 pts", "3 reb", "3 ast"}));
        tree.add("Westbrook", Arrays.asList(new String[] {"22 pts", "3 reb", "11 ast" })); 
        tree.add("Ibaka", Arrays.asList(new String[] { "13 pts", "10 reb", "1 ast"})); 
        tree.add("Adams", Arrays.asList(new String[] { "9 pts", "7 reb", "1 ast"}));
        tree.add("Roberson", Arrays.asList(new String[]{ "6 pts", "1 reb", "2 ast"})); 
        tree.add("Waiters", Arrays.asList(new String[] { "8 pts", "1 reb", "1 ast"}));
        tree.add("Jackson", Arrays.asList(new String[] { "7 pts", "4 reb", "3 ast"}));
        tree.add("Morrow", Arrays.asList(new String[] { "0 pts", "1 reb", "0 ast"}));
        tree.add("Perkins", Arrays.asList(new String[] { "4 pts", "6 reb", "1 ast"}));
        tree.add("Lamb", Arrays.asList(new String[] { "3 pts", "0 reb", "0 ast"})); 
        tree.add("Jones", Arrays.asList(new String[] { "0 pts", "0 reb", "0 ast"}));
        tree.add("Smith", Arrays.asList(new String[] { "0 pts", "0 reb", "0 ast"})); 
        
        System.out.println(tree);
        System.out.println("Press enter to continue...");
        try
        {
            System.in.read();
        }  
        catch(Exception e)
        {}
        
        Collection atl = Arrays.asList(new String[] { "Millsap", "Korver", "Horford", "Carroll", "Teague", "Antic", "Schroder", "Bazemore", "Sefolosha", "Scott"}); 
        HashTree tree2 = new HashTree(atl); 
        tree2.add("Millsap", Arrays.asList(new String[] { "22 pts", "10 reb", "3 ast"})); 
        tree2.add("Korver", Arrays.asList(new String[] { "10 pts", "4 reb", "2 ast"}));
        tree2.add("Horford", Arrays.asList(new String[] { "14 pts", "12 reb", "3 ast"})); 
        tree2.add("Carroll", Arrays.asList(new String[] { "13 pts", "4 reb", "3 ast"}));
        tree2.add("Teague", Arrays.asList(new String[] { "17 pts", "2 reb", "9 ast"})); 
        tree2.add("Antic", Arrays.asList(new String[] { "8 pts", "4 reb", "2 ast"}));
        tree2.add("Schroder", Arrays.asList(new String[] { "13 pts", "4 reb", "5 ast"}));
        tree2.add("Bazemore", Arrays.asList(new String[] { "5 pts", "2 reb", "0 ast"})); 
        tree2.add("Sefolosha", Arrays.asList(new String[] { "4 pts", "4 reb", "0 ast"})); 
        tree2.add("Scott", Arrays.asList(new String[] { "2 pts", "1 reb", "0 ast"}));
        
        System.out.println(tree2); 
        
   
    }
    
}
