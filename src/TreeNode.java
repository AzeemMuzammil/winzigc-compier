import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private String node_name;
    private List<TreeNode> child_nodes;

    public TreeNode(String node_name) {
        this.child_nodes = new ArrayList<>();
        this.node_name = node_name;
    }

    public int getChildNodesCount() {
        return child_nodes.size();
    }

    public void addChildNode(TreeNode child) {
        this.child_nodes.add(child);
    }

    public TreeNode deleteTreeNode(int index) {
        return child_nodes.remove(index);
    }

    public void addChildAtIndex(int index, TreeNode child) {
        child_nodes.add(index, child);
    }

    public void Traverse(int depth) {
        for (int i = 0; i < depth; i++)
            System.out.print(". ");
        System.out.print(this.node_name);
        System.out.println("(" + this.child_nodes.size() + ")");
        if (!isLeafNode()) {
            for (TreeNode node : child_nodes) {
                node.Traverse(depth + 1);
            }
        }
    }

    private boolean isLeafNode() {
        return this.child_nodes.size() == 0;
    }
}