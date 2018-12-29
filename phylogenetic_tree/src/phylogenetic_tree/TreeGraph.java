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

    public static void showTreeGraph(List<TreeNode> treeList) {
        System.out.println("Select tree to show a graph of");
        for (int i = 1; i <= treeList.size(); i++) {
            System.out.println(String.format("%d. %s", i, treeList.get(i - 1).getRawFormat()));
        }
        int menuIndex = main.selectItemFromMenu();
        print(treeList.get(menuIndex - 1));
        try {
            System.out.print("Press enter key to leave preview.");
            System.in.read();
        } catch (IOException ex) {
            System.out.println("IOException while reading input");
        }
    }

    public static void print(TreeNode root) {
        print(root, "", true);
    }

    private static void print(TreeNode root, String prefix, boolean isTail) {
        String tail = TAIL + HORIZONTAL + " ";
        String spaces = "      ";
        if (!isTail) {
            tail = VERTICALWITHLINE + HORIZONTAL + HORIZONTAL + " ";
        }
        System.out.println(prefix + tail + root.getName());
        List<TreeNode> children = root.getChildren();
        if (children == null) {
            return;
        }
        String whitespaces = spaces;
        children.forEach(child -> print(child, prefix + (isTail ? "    " : VERTICAL + whitespaces), false));
    }

}
