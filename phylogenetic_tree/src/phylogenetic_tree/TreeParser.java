package phylogenetic_tree;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author badys
 */
public class TreeParser {

    private TreeNode root;

    List<String> parseFile(BufferedReader br) {
        return br.lines().collect(Collectors.toList());
    }

    TreeNode readNewickFormat(String newick) {
        this.root = readSubtree(newick.substring(0, newick.length() - 1));
        this.root.setRawFormat(newick);
        return root;
    }

    private TreeNode readSubtree(String mainString) {

        int leftParen = mainString.indexOf('(');
        int rightParen = mainString.lastIndexOf(')');

        if (leftParen != -1 && rightParen != -1) {

            String name = mainString.substring(rightParen + 1);
            String[] childrenString = split(mainString.substring(leftParen + 1, rightParen));

            TreeNode node = new TreeNode(name);
            for (String sub : childrenString) {
                TreeNode child = readSubtree(sub);
                node.addChild(child);
            }
            return node;
        } else if (leftParen == rightParen) {
            TreeNode node = new TreeNode(mainString);
            return node;
        } else {
            throw new RuntimeException();
        }
    }

    private String[] split(String s) {

        ArrayList<Integer> splitIndices = new ArrayList<>();

        int rightParenCount = 0;
        int leftParenCount = 0;
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '(':
                    leftParenCount++;
                    break;
                case ')':
                    rightParenCount++;
                    break;
                case ',':
                    if (leftParenCount == rightParenCount) {
                        splitIndices.add(i);
                    }
                    break;
            }
        }

        int numSplits = splitIndices.size() + 1;
        String[] splits = new String[numSplits];

        if (numSplits == 1) {
            splits[0] = s;
        } else {

            splits[0] = s.substring(0, splitIndices.get(0));

            for (int i = 1; i < splitIndices.size(); i++) {
                splits[i] = s.substring(splitIndices.get(i - 1) + 1, splitIndices.get(i));
            }

            splits[numSplits - 1] = s.substring(splitIndices.get(splitIndices.size() - 1) + 1);
        }

        return splits;
    }

    public static TreeNode[] convertTreeNodesToArray(TreeNode root) {
        List<TreeNode> retList = new ArrayList<>();
        List<TreeNode> children = root.getChildren();
        retList.add(root);
        expandTree(children, retList);
        return retList.toArray(new TreeNode[retList.size()]);
    }

    private static void expandTree(List<TreeNode> children, List<TreeNode> retList) {
        if (children != null) {
            children.stream().forEach(child -> {
//                if (!"".equals(child.getName())) {
                    retList.add(child);
//                }
                expandTree(child.getChildren(), retList);
            });
        }
    }

}
