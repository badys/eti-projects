package phylogenetic_tree;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author badys
 */
public class TreeGraph {

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
        String tail = "└── ";
        String spaces = "      ";
        if (!isTail) {
            String weight = String.valueOf(root.getWeight());
            tail = "├──" + weight + "── ";
            for (int i = 0; i < weight.length(); i++) {
                spaces += " ";
            }
        }
        System.out.println(prefix + tail + root.getName());
        List<TreeNode> children = root.getChildren();
        if (children == null) {
            return;
        }
        String whitespaces = spaces;
        children.forEach(child -> print(child, prefix + (isTail ? "    " : "│" + whitespaces), false));
    }

}
