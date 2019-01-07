package phylogenetic_tree;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author badys
 */
public class TreeGraph {

    public static final String HORIZONTAL = "\u2500\u2500";
    public static final String VERTICALWITHLINE = "\u251C";
    public static final String VERTICAL = "\u2502";

    public static void print(TreeNode root) {
        System.out.println("");
        for (int i = 0; i < root.getChildren().size(); i++) {
            print(root.getChildren().get(i), "", i, root.getChildren().size());
        }
    }

    private static void print(TreeNode root, String prefix, int i, int size) {
        String tail = VERTICALWITHLINE + HORIZONTAL + HORIZONTAL + " ";
        String spaces = "    ";
        System.out.println(prefix + tail + root.getName());
        List<TreeNode> children = root.getChildren();
        String whitespaces = spaces;
        for (int j = 0; j < children.size(); j++) {
            print(children.get(j), prefix + (i < size-1 ? VERTICAL : "   ") + whitespaces, j, children.size());
        }
    }

    public static void printTreeListInRawFormat(List<TreeNode> treeList) {
        for (int i = 1; i <= treeList.size(); i++) {
            System.out.println(String.format("%d. %s", i, treeList.get(i - 1).getRawFormat()));
        }
    }

}
