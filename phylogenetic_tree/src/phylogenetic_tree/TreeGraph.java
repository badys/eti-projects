package phylogenetic_tree;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author badys
 */
public class TreeGraph {

    public static final String TAIL = "\u2514";
    public static final String HORIZONTAL = "\u2500\u2500";
    public static final String VERTICALWITHLINE = "\u251C";
    public static final String VERTICAL = "\u2502";

    public static void print(TreeNode root) {
        System.out.println("");
        root.getChildren().forEach(tree -> print(tree, ""));
    }

    private static void print(TreeNode root, String prefix) {
        String tail = VERTICALWITHLINE + HORIZONTAL + HORIZONTAL + " ";
        String spaces = "    ";
        System.out.println(prefix + tail + root.getName());
        List<TreeNode> children = root.getChildren();
        if (children == null) {
            return;
        }
        String whitespaces = spaces;
        children.forEach(child -> print(child, prefix + VERTICAL + whitespaces));
    }

    public static void printTreeListInRawFormat(List<TreeNode> treeList) {
        for (int i = 1; i <= treeList.size(); i++) {
            System.out.println(String.format("%d. %s", i, treeList.get(i - 1).getRawFormat()));
        }
    }

}
