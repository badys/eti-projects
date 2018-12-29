package phylogenetic_tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author badys
 */
public class main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final TreeParser treeParser = new TreeParser();
    private static final List<TreeNode> treeList = new ArrayList<>();
    private static boolean exit = false;

    public static void main(String[] args) {
        String fileName = "file.txt";
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
        System.out.println("1. Show graph of selected tree");
        System.out.println("9. Exit program");
        int menuIndex = selectItemFromMenu();
        switch (menuIndex) {
            case 1:
                TreeGraph.showTreeGraph(treeList);
                break;
            case 2:
                TreeOperations.calculateTopologicalDistance(treeList.get(0), treeList.get(1));
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
