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

    public static void main(String[] args) {
        String fileName = "file.txt";
        readFile(fileName);
        showMenu();
    }

    private static void readFile(String fileName) {
        File file = new File(fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            List<String> list = treeParser.parseFile(br);
            System.out.println(String.format("Found %d tree(s) in file", list.size()));
            for (int i = 1; i < list.size(); i++) {
                System.out.println(String.format("%d. %s", i, list.get(i - 1)));
                treeList.add(treeParser.readNewickFormat(list.get(i - 1)));
            }
        } catch (Exception e) {
            System.out.println("Error while reading file " + fileName);
            e.printStackTrace();
        }
    }

    private static void showMenu() {
        clearConsole();
        System.out.println("1. Show graph of selected tree");
        int menuIndex = selectItemFromMenu();
        switch (menuIndex) {
            case 1:
                TreeGraph.showTreeGraph(treeList);
                break;
            default:
                System.out.println("Menu doesn't have such number !");
        }

    }

    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException ioEx) {
            System.out.println("IOException while clearing console");
            ioEx.printStackTrace();
        } catch (InterruptedException intrEx) {
            System.out.println("InterruptedException while clearing console");
            intrEx.printStackTrace();
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

}
