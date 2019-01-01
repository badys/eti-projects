package phylogenetic_tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author badys
 */
public class main {

    public static final Scanner scanner = new Scanner(System.in);
    private static final TreeParser treeParser = new TreeParser();
    private static final List<TreeNode> treeList = new ArrayList<>();
    private static boolean exit = false;
    private static String fileName = "";

    public static void main(String[] args) {
        fileName = "file.txt";
        readFile(fileName);
        while (!exit) {
            showMenu();
        }
        closeProgram();
    }

    private static void readFile(String fileName) {
        File file = new File(fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            List<String> list = treeParser.parseFile(br);
            System.out.println(String.format("Found %d tree(s) in file", list.size()));
            for (int i = 1; i <= list.size(); i++) {
                System.out.println(String.format("%d. %s", i, list.get(i - 1)));
                treeList.add(treeParser.readNewickFormat(list.get(i - 1)));
            }
        } catch (Exception e) {
            System.out.println("Error while reading file " + fileName);
            e.printStackTrace();
        }
    }

    private static void showMenu() {
        System.out.println("\n1. Show graph of selected tree");
        System.out.println("2. Show nontrivial divisions of selected tree");
        System.out.println("3. Show trivial divisions of selected tree");
        System.out.println("4. Calculate topological distance between two trees");
        System.out.println("5. Reconstruct tree from divisions");
        System.out.println("6. Cut tree");
        System.out.println("9. Exit program");
        int menuIndex = selectItemFromMenu();
        switch (menuIndex) {
            case 1:
                System.out.println("Select tree to show a graph of");
                TreeGraph.printTreeListInRawFormat(treeList);
                menuIndex = selectItemFromMenu() - 1;
                TreeGraph.showTreeGraph(treeList.get(menuIndex));
                break;
            case 2:
                System.out.print("Select tree: ");
                menuIndex = selectItemFromMenu() - 1;
                TreeOperations.showNontrivialDivisionsForTree(treeList.get(menuIndex));
                break;
            case 3:
                System.out.print("Select tree: ");
                menuIndex = selectItemFromMenu() - 1;
                TreeOperations.showTrivialDivisionsForTree(treeList.get(menuIndex));
                break;
            case 4:
                int first,second;
                System.out.println("Select first item:");
                first = selectItemFromMenu() - 1;
                System.out.println("Select second item:");
                second = selectItemFromMenu() - 1;
                TreeOperations.calculateTopologicalDistance(treeList.get(first), treeList.get(second));
                break;
            case 5:
                System.out.print("Select tree: ");
                menuIndex = selectItemFromMenu() - 1;
                TreeNode reconstructedTree = new TreeNode();
                TreeOperations.reconstrucTreeFromDivisionSet(TreeOperations.divideTreeTrivially(treeList.get(menuIndex)), new ArrayList<String>(), reconstructedTree);
                TreeGraph.print(reconstructedTree);
                break;
            case 6:
                System.out.print("Select tree: ");
                menuIndex = selectItemFromMenu() - 1;
                TreeNode newNode = TreeOperations.cutTreeToSubTree(treeList.get(menuIndex));
                if (newNode != null) TreeGraph.print(newNode);
                break;
            case 7:             
                System.out.print("Ratio: ");
                menuIndex = selectItemFromMenu();
                TreeGraph.print(TreeOperations.findConsensusTree(treeList, menuIndex));
                break;
            case 9:
                exit = true;
                break;
            default:
                System.out.println("Menu doesn't have such number !");
        }

    }

    public static int selectItemFromMenu() {
        while (true) {
            String selection = scanner.next();
            int menuIndex = -1;
            try {
                menuIndex = Integer.valueOf(selection);
            } catch (NumberFormatException nfe) {
                System.out.println("Please input a number !");
                continue;
            }
            return menuIndex;
        }
    }

    private static void closeProgram() {
        scanner.close();
    }

}
