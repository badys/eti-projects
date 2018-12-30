/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phylogenetic_tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Jakub Przesławski
 */
public class TreeOperations {
    
    public static int calculateTopologicalDistance(TreeNode A, TreeNode B) {
        int dist = 0;
        
        TreeNode[] nodesA = TreeParser.convertTreeNodesToArray(A);
        List<String> listA = getLeavesNames(nodesA);
        TreeNode[] nodesB = TreeParser.convertTreeNodesToArray(A);
        List<String> listB = getLeavesNames(nodesB);
        
        Collections.sort(listA);
        Collections.sort(listB);
        
        // assert leaves are the same
        if (!Arrays.equals(listA.toArray(), listB.toArray())) {
            System.err.println("Error: Leaves for both trees differ!");
            return -1;
        }
        
        List<Division> divisionsA = divideTreeTrivially(A);
        //System.out.println("***");
        List<Division> divisionsB = divideTreeTrivially(B);
        
        if (!Division.validateDivisionSet(divisionsA))
            System.err.println("divA invalid");
        
        if (!Division.validateDivisionSet(divisionsB))
            System.err.println("divB invalid");
        
        dist = divisionsA.size() + divisionsB.size();
        for(Division divA : divisionsA) {
            for(Division divB : divisionsB) {
                dist = Division.equals(divA, divB) ? dist - 2 : dist;
            }
        }
        System.out.println("topological distance = " + dist);
        return dist;
    }
    
    public static List<Division> divideTreeTrivially(TreeNode root) {
        
        List<Division> divisions = new ArrayList<Division>();
        
        TreeNode[] nodesArray = TreeParser.convertTreeNodesToArray(root);
        List<String> nodesList = getLeavesNames(nodesArray);
        for(TreeNode tn : nodesArray) {
            List<String> left = getLeavesNames(TreeParser.convertTreeNodesToArray(tn));
            List<String> right = nodesList;
            
            List<String> union = new ArrayList<String>(left);
            union.addAll(right);
            List<String> intersection = new ArrayList<String>(left);
            intersection.retainAll(right);
            union.removeAll(intersection);
            
            if (union.isEmpty() || left.isEmpty()) {
                continue;
            }
            Collections.sort(union);
            Collections.sort(left);
            
            boolean isDuplicate = false;
            for (Division d : divisions) {
                if ((Arrays.equals(left.toArray(new String[left.size()]), d.A) &&
                        Arrays.equals(union.toArray(new String[union.size()]), d.B)) ||
                        (Arrays.equals(left.toArray(new String[left.size()]), d.B) &&
                        Arrays.equals(union.toArray(new String[union.size()]), d.A))) {
                    //System.err.println("found duplicate");
                    isDuplicate = true;
                    break;
                }
            }
            
            if (!isDuplicate) {
                divisions.add(new Division(left.toArray(new String[left.size()]),
                        union.toArray(new String[union.size()])));
            }
            
            //System.out.println("<node " + tn.getName() + "> " +left + " : " + union);
        }
        
        
        return divisions;
    }
    
    
    public static List<String> reconstrucTreeFromDivisionSet(List<Division> divs, List<String> danglingLeaves, TreeNode node) {
        
        if (danglingLeaves.isEmpty()) {
            // zero iteration
            danglingLeaves = divs.get(0).getAllLeaves();
//            System.out.println("** first iter: leaves = " + danglingLeaves);
        }
        
//        List<String> newDanglingLeaves = Arrays.asList(pick.A).stream()
//                .collect(Collectors.toList());
        
        //dbg
//        System.out.println(" ************* divs:\n");
//        TreeOperations.showDivisions(divs);

        List<String> matchedLeaves = new ArrayList<String>();      
        List<String> leavesToMatch = danglingLeaves.stream()
                .filter(x -> !matchedLeaves.contains(x))
                .collect(Collectors.toList());
        //System.out.println("NODE's leaves: " + danglingLeaves);
        //System.out.println("NODE's matches: " + matchedLeaves);
        System.out.println("** to match in node ** " + leavesToMatch);
        
        

        
        while(!leavesToMatch.isEmpty()) {
            
            int maxVal = 0;
            Division pick = new Division();
            // find biggest division
            for(Division d : divs) {     
                if (danglingLeaves.containsAll(Arrays.asList(d.A))) {
                    // division is further branching in same direction
                    // check if it is "the biggest"
                    if (d.A.length > maxVal) {
                         maxVal = d.A.length;
                         pick = d;
                    }
                }
            }
            // picked right division to perform further branching
            System.out.print("Pick >>> ");
            pick.show();

            divs.remove(pick);
            //TreeOperations.showDivisions(divs);
            //System.out.print(" <<< ****** >>> ");


            if (maxVal == 1) {
                // only leaves nodes to add
                for(String s : leavesToMatch) {
                    TreeNode leaf = new TreeNode(s);
                    node.addChild(leaf);
                    matchedLeaves.add(s);
                    System.out.println("Matched leaf: " + s);
                }

                return matchedLeaves; 
            }




//            final Division pickFinal = pick;
//            List<Division> newDivs = divs.stream()
//                    .filter(x -> !x.equals(pickFinal))
//                    .collect(Collectors.toList());

            List<String> childsDanglingLeaves = Arrays.asList(pick.A).stream()
                    .collect(Collectors.toList());
            
            TreeNode child = new TreeNode();
            node.addChild(child);
            List<String> matched = reconstrucTreeFromDivisionSet(divs, childsDanglingLeaves, child);
            matchedLeaves.addAll(matched);
            leavesToMatch = danglingLeaves.stream()
                .filter(x -> !matchedLeaves.contains(x))
                .collect(Collectors.toList());
            System.out.println(matched + ", still to match: " + leavesToMatch);
            
        }
        System.out.println("returning " + matchedLeaves);
        return matchedLeaves;
    }
    
    
    public static List<Division> divideTreeNontrivially(TreeNode root) {
        
        List<Division> trivial = divideTreeTrivially(root);
        List<Division> nontrivial = new ArrayList<Division>();
        for(Division d : trivial) {
            if (d.A.length > 1 && d.B.length > 1)
                nontrivial.add(d);
        }
        return nontrivial;
    }
    
    public static void showNontrivialDivisionsForTree(TreeNode root) {
        List<Division> divisions = divideTreeNontrivially(root);
        int i = 1;
        for(Division d : divisions) {
            System.out.println(i++ + ") " + Arrays.asList(d.A) + " : " + Arrays.asList(d.B));
        }
    }
    
    public static void showTrivialDivisionsForTree(TreeNode root) {
        List<Division> divisions = divideTreeTrivially(root);
        int i = 1;
        for(Division d : divisions) {
            System.out.println(i++ + ") " + Arrays.asList(d.A) + " : " + Arrays.asList(d.B));
        }
    }
    
    public static void showDivisions(List<Division> divs) {
        int i = 1;
        for(Division d : divs) {
            System.out.println(i++ + ") " + Arrays.asList(d.A) + " : " + Arrays.asList(d.B));
        }
    }
    
    private static List<String> getLeavesNames(TreeNode[] nodesSerial) {
        List<String> names = new ArrayList<String>();
        Arrays.asList(nodesSerial).stream().forEach(n -> {
            if (!n.getName().equals(""))
                names.add(n.getName());
        });
        return names;
    }
    
    private static List<String> getChildNames(TreeNode node) {
        List<String> retList = new ArrayList();
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            node.getChildren().stream().forEach(child -> {
                //retList.add(!"".equals(child.getName()) ? child.getName() : " ");
                if(!"".equals(child.getName())) {
                    retList.add(child.getName());
                }
                retList.addAll(getChildNames(child));
            });
        }
        if (!"".equals(node.getName())) {
            retList.add(node.getName());
        }
        retList.sort(Comparator.naturalOrder());
        return retList.stream().distinct().collect(Collectors.toList());
    }

    public static TreeNode cutTreeToSubTree(TreeNode root) {
        main.scanner.nextLine(); //reset scanner
        TreeGraph.print(root);
        TreeNode[] nodeArray = TreeParser.convertTreeNodesToArray(root);
        List<String> leavesArray = getLeavesNames(nodeArray);
        //Collecting characters only matching leaves names
        List<String> distinctList = new ArrayList();
        while (distinctList.isEmpty()) {
            System.out.println("Pick leaves to create subtree");
            String selectedLeaves = main.scanner.nextLine().replaceAll(" ", "");
            distinctList = Arrays.stream(selectedLeaves.split(""))
                    .filter(single_char -> leavesArray.stream().anyMatch(leaf -> leaf.equalsIgnoreCase(single_char)))
                    .distinct()
                    .map(String::toUpperCase) // uppercasing for easier sort
                    .collect(Collectors.toList());

            if (distinctList.isEmpty()) {
                System.out.println("Input doesn't match any of the leaves !");
            }
        }
        distinctList.sort(Comparator.naturalOrder()); //sorting
        List<String> selectedLeaves = distinctList; // making variable effectively final
        TreeNode leftNode = Arrays.stream(nodeArray)
                .filter(node -> Arrays.equals(getChildNames(node).toArray(), selectedLeaves.toArray()))
                .findFirst()
                .orElse(null);
        
        if (leftNode == null) {
            System.out.println("Selected tree was not possible to cut");
        }

        return leftNode;
    }
    
}
