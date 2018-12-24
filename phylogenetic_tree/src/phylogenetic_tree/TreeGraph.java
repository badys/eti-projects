package phylogenetic_tree;

import java.util.List;

/**
 *
 * @author badys
 */
public class TreeGraph {

    public static void showTreeGraph(List<TreeNode> treeList) {
        main.clearConsole();
        System.out.println("Select tree to show a graph of");
        for (int i = 1; i < treeList.size(); i++) {
                System.out.println(String.format("%d. %s", i, treeList.get(i - 1).getRawFormat()));
            }
        int menuIndex = main.selectItemFromMenu();
        print(treeList.get(menuIndex-1));
    }

    public static void print(TreeNode root) {
        print(root, "", true);
    }

    private static void print(TreeNode root, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + root.getName());
        List<TreeNode> children = root.getChildren();
        if (children == null) return;
        children.forEach(child -> {
        print(child ,prefix + (isTail ? "    " : "│   "), false);
        });
    }
    
}
