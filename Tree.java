
/**
 * Define a Huffman coding tree
 * 
 */
public class Tree implements Comparable<Tree> {
   Node root; // The root of the tree

   /**
    * Create a tree with two subtrees
    * 
    * @param t1 The left subtree
    * @param t2 The right subtree
    */
   public Tree(Tree t1, Tree t2) {
      root = new Node();
      root.left = t1.root;
      root.right = t2.root;
      root.weight = t1.root.weight + t2.root.weight;
   }

   /**
    * Create a tree containing a leaf node
    * 
    * @param weight  The weight of the subtree
    * @param element The element of the subtree
    * 
    */
   public Tree(int weight, char element) {
      root = new Node(weight, element);
   }

   @Override /** Compare trees based on their weights */
   public int compareTo(Tree t) {
      if (root.weight < t.root.weight) // Purposely reverse the order
         return 1;
      else if (root.weight == t.root.weight)
         return 0;
      else
         return -1;
   }
}