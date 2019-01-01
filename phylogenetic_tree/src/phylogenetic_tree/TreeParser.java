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
        return br.lines()
                .filter(line -> !line.startsWith("#"))
                .collect(Collectors.toList());
    }

    TreeNode readNewickFormat(String newick) {
        this.root = readSubtree(newick.substring(0, newick.length() - 1));
        this.root.setRawFormat(newick);
        return root;
    }

    private TreeNode readSubtree(String main) {

        int leftParen = main.indexOf('(');
        int rightParen = main.lastIndexOf(')');

        if (leftParen != -1 && rightParen != -1) {

            String name = main.substring(rightParen + 1);
            String[] childrenString = split(main.substring(leftParen + 1, rightParen));

            TreeNode node = new TreeNode(name);
            for (String subString : childrenString) {
                TreeNode child = readSubtree(subString);
                node.addChild(child);
            }
            return node;
        } else if (leftParen == rightParen) {
            TreeNode node = new TreeNode(main);
            return node;
        } else {
            System.out.println("Error while parsing tree");
            return null;
        }
    }

    private String[] split(String s) {

        List<Integer> splitIdx = new ArrayList<>();

        int rightCount = 0;
        int leftCount = 0;
        char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            switch(charArray[i]) {
                case '(':
                    leftCount++;
                    break;
                case ')':
                    rightCount++;
                    break;
                case ',':
                    if (leftCount == rightCount) {
                        splitIdx.add(i);
                    }
                    break;
                default:
                    break;
            }
        }

        int splitsSize = splitIdx.size() + 1;
        String[] splits = new String[splitsSize];

        if (splitsSize == 1) {
            splits[0] = s;
        } else {
            splits[0] = s.substring(0, splitIdx.get(0));
            for (int i = 1; i < splitIdx.size(); i++) {
                splits[i] = s.substring(splitIdx.get(i - 1) + 1, splitIdx.get(i));
            }
            splits[splitsSize - 1] = s.substring(splitIdx.get(splitIdx.size() - 1) + 1);
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
                retList.add(child);
                expandTree(child.getChildren(), retList);
            });
        }
    }

}
