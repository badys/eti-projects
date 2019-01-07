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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Jakub Przesławski
 */
public class TreeOperations {
    
    public static int calculateTopologicalDistance(TreeNode A, TreeNode B) {
        int dist = 0;
        
        TreeNode[] nodesA = TreeParser.convertTreeNodesToArray(A);
        List<String> listA = getLeavesNames(nodesA);
        TreeNode[] nodesB = TreeParser.convertTreeNodesToArray(B);
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
                dist = divA.equals(divB) ? dist - 2 : dist;
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
        }

        return divisions;
    }
    
    public static TreeNode findExpansionTree(List<TreeNode> treeList) {
        
        
        List<List<Division>> fullSet = new ArrayList<List<Division>>();
        List<Division> expansion = new ArrayList<Division>();
        
        List<String> listA = getLeavesNames(TreeParser.convertTreeNodesToArray(treeList.get(0)));
        Collections.sort(listA);
        
        for(TreeNode node : treeList) {
            List<String> listB = getLeavesNames(TreeParser.convertTreeNodesToArray(treeList.get(0)));
            Collections.sort(listB);
            if (!Arrays.equals(listA.toArray(), listB.toArray())) {
                System.err.println("Error: Leaves for both trees differ!");
                return new TreeNode();
            }
            fullSet.add(divideTreeTrivially(node));
            
        }
        Map<Division, Integer> hm = new HashMap<Division, Integer>(); 
  
        for(List<Division> dl : fullSet) {
            for (Division d : dl) {
                if (hm.get(d) == null && hm.get(d.flip()) == null) {
                    hm.put(d, 1);
                }
            }
        }
  
        // displaying the occurrence of elements in the arraylist 
        for (Map.Entry<Division, Integer> val : hm.entrySet()) { 
            val.getKey().show();
            expansion.add(val.getKey());
        }

        if (!Division.validateDivisionSet(expansion)) {
            System.out.println("Error: Invalid division set, cannot create expansion tree");
            return null;
        }

        TreeNode expansionTree = new TreeNode();
        reconstrucTreeFromDivisionSet(expansion, new ArrayList<String>(), expansionTree, 0);
        return expansionTree;
    }
    public static TreeNode findConsensusTree(List<TreeNode> treeList, int ratio) {
        
        
        List<List<Division>> fullSet = new ArrayList<List<Division>>();
        List<Division> consensus = new ArrayList<Division>();
        
        List<String> listA = getLeavesNames(TreeParser.convertTreeNodesToArray(treeList.get(0)));
        Collections.sort(listA);
        
        for(TreeNode node : treeList) {
            List<String> listB = getLeavesNames(TreeParser.convertTreeNodesToArray(treeList.get(0)));
            Collections.sort(listB);
            if (!Arrays.equals(listA.toArray(), listB.toArray())) {
                System.err.println("Error: Leaves for both trees differ!");
                return new TreeNode();
            }
            fullSet.add(divideTreeTrivially(node));
            
        }
        Map<Division, Integer> hm = new HashMap<Division, Integer>(); 
  
        for(List<Division> dl : fullSet) {
            for (Division d : dl) {
                if (hm.get(d) == null && hm.get(d.flip()) == null)
                    hm.put(d, 1);
                else {
                    if (hm.get(d) != null)
                        hm.put(d, hm.get(d) + 1);
                    else
                        hm.put(d.flip(), hm.get(d.flip()) + 1);
                }
                
            }
        }
  
        // displaying the occurrence of elements in the arraylist 
        for (Map.Entry<Division, Integer> val : hm.entrySet()) { 
            System.out.println("Division " + Arrays.asList(val.getKey().A) + ":"
                    + Arrays.asList(val.getKey().B) + " occurs"
                    + ": " + val.getValue() + " times"); 
        } 
        
        System.out.println("consensus set: ");
        for (Map.Entry<Division, Integer> val : hm.entrySet()) { 
            if (((double)val.getValue() * 100.0) / (double)treeList.size() >= ratio) {
                val.getKey().show();
                consensus.add(val.getKey());
            }
        } 

        TreeNode consensusTree = new TreeNode();
        if (!consensus.isEmpty()) {
            reconstrucTreeFromDivisionSet(consensus, new ArrayList<String>(), consensusTree, 0);
        }
        return consensusTree;
    }
    
    public static List<String> reconstrucTreeFromDivisionSet(List<Division> divs, List<String> danglingLeaves, TreeNode node, int level) {
        
        if (danglingLeaves.isEmpty()) {
            // zero iteration
            danglingLeaves = divs.get(0).getAllLeaves();
        }

        List<String> matchedLeaves = new ArrayList<String>();      
        List<String> leavesToMatch = danglingLeaves.stream()
                .filter(x -> !matchedLeaves.contains(x))
                .collect(Collectors.toList());
        
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

            divs.remove(pick);

            if (maxVal == 1) {
                // only leaves nodes to add
                for(String s : leavesToMatch) {
                    TreeNode leaf = new TreeNode(s);
                    node.addChild(leaf);
                    matchedLeaves.add(s);
                }

                return matchedLeaves; 
            }

            List<String> childsDanglingLeaves = Arrays.asList(pick.A).stream()
                    .collect(Collectors.toList());
            
            TreeNode child = new TreeNode();
            node.addChild(child);
            List<String> matched = reconstrucTreeFromDivisionSet(divs, childsDanglingLeaves, child, level+1);
            matchedLeaves.addAll(matched);
            leavesToMatch = danglingLeaves.stream()
                .filter(x -> !matchedLeaves.contains(x))
                .collect(Collectors.toList());

        }
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
    
    public static List<Division> cutPls(TreeNode root) {
        TreeNode cut = new TreeNode();
        List<Division> divs = divideTreeTrivially(root);
        List<Division> afterCut = new ArrayList<>();
       
        main.scanner.nextLine(); //reset scanner
        String[] distinctList;
        do {
            System.out.println("Pick leaves to create subtree");
            String selectedLeaves = main.scanner.nextLine().replaceAll(" ", "");
            distinctList = Arrays.stream(selectedLeaves.split(""))
                    .distinct()
                    .map(String::toUpperCase) // uppercasing for easier sort
                    .toArray(String[]::new);
            if (distinctList.length == 0) {
                System.out.println("Input doesn't match any of the leaves !");
            }
        } while (distinctList.length == 0);
        Arrays.sort(distinctList);
        
        TreeNode[] nodeArray = TreeParser.convertTreeNodesToArray(root);
        List<String> leavesArray = getLeavesNames(nodeArray);
        leavesArray.removeAll(Arrays.asList(distinctList));
        for(Division d : divs) {
            List<String> A = new ArrayList(Arrays.asList(d.A));
            List<String> B = new ArrayList(Arrays.asList(d.B));
            A.removeAll(leavesArray);      
            B.removeAll(leavesArray);
            
            if (!A.isEmpty() && !B.isEmpty())
                afterCut.add(new Division(A.toArray(new String[A.size()]), B.toArray(new String[B.size()])));
        }
        
        afterCut = afterCut.stream().distinct().collect(Collectors.toList());
        
        return afterCut;
        
    }
    
    public static TreeNode cutTreeToSubTree(TreeNode root) {
        main.scanner.nextLine(); //reset scanner
        TreeGraph.print(root);
        TreeNode rootClone = new TreeNode(root);
        TreeNode[] nodeArray = TreeParser.convertTreeNodesToArray(rootClone);
        List<String> leavesArray = getLeavesNames(nodeArray);
        List<Division> triviallyDivided = divideTreeTrivially(rootClone);
        List<String[]> divisionsArray = new ArrayList();
        triviallyDivided.stream().forEach(x -> {
            divisionsArray.add(x.A);
            divisionsArray.add(x.B);
        });

        //Collecting characters only matching leaves names
        String[] distinctList;
        do {
            System.out.println("Pick leaves to create subtree");
            String selectedLeaves = main.scanner.nextLine().replaceAll(" ", "");
            distinctList = Arrays.stream(selectedLeaves.split(""))
                    .filter(single_char -> leavesArray.stream().anyMatch(leaf -> leaf.equalsIgnoreCase(single_char)))
                    .distinct()
                    .map(String::toUpperCase) // uppercasing for easier sort
                    .toArray(String[]::new);
            if (distinctList.length == 0) {
                System.out.println("Input doesn't match any of the leaves !");
            }
        } while (distinctList.length == 0);
        Arrays.sort(distinctList);
        String[] finalList = distinctList;
        String[] divA = divisionsArray.stream()
                .filter(x -> Arrays.equals(x, finalList))
                .findFirst()
                .orElse(null);

        if (divA == null) {
            System.out.println("Selected tree was not possible to cut");
            return null;
        }
        TreeNode newTree = new TreeNode();

        List<String> t = new ArrayList(Arrays.asList(divA));
        List<TreeNode> p = Arrays.stream(nodeArray)
                .filter(node -> t.contains(node.getName()))
                .collect(Collectors.toList());

        p.forEach(treeNodeFromArray -> {
            if (t.contains(treeNodeFromArray.getName())) {
                TreeNode parent = treeNodeFromArray.getParent();
                while (parent.getParent() != null) {
                    parent = parent.getParent();
                }
                List<TreeNode> topLevelChildren = parent.getChildren().stream()
                        .filter(child -> t.contains(child.getName()))
                        .collect(Collectors.toList());
                List<TreeNode> subChilds = parent.getChildren().stream()
                        .filter(child -> "".equals(child.getName()))
                        .collect(Collectors.toList());
                if (!subChilds.isEmpty()) {
                    List<TreeNode> retList = expandTreeUsingChildrenList(subChilds, t);
                    newTree.addChildren(topLevelChildren);
                    if (!retList.isEmpty()) {
                        newTree.addChildren(retList);
                    }
                } else {
                    newTree.addChildren(topLevelChildren);
                }
                t.removeAll(topLevelChildren.stream().map(TreeNode::getName).collect(Collectors.toList()));
            }
        });

        return newTree;
    }
    
    private static List<TreeNode> expandTreeUsingChildrenList(List<TreeNode> subChilds, List<String> t) {
            List<TreeNode> namelessNodesList = new ArrayList();
            subChilds.forEach(subChild -> {
                TreeNode namelessNode = new TreeNode();
                boolean shouldAdd = false;
                List<TreeNode> children = subChild.getChildren().stream().filter(child -> t.contains(child.getName())).collect(Collectors.toList());
                if (!children.isEmpty()) {
                    namelessNode.addChildren(children);
                    t.removeAll(children.stream().map(TreeNode::getName).collect(Collectors.toList()));
                    shouldAdd = true;
                }
                List<TreeNode> subSubChilds = subChild.getChildren().stream().filter(child -> "".equals(child.getName())).collect(Collectors.toList());
                if (!subSubChilds.isEmpty()) {
                    List<TreeNode> retList = expandTreeUsingChildrenList(subSubChilds, t);
                    if (namelessNode.getChildren().isEmpty()) {
                        namelessNodesList.addAll(retList);
                        shouldAdd = false;
                    } else {
                        namelessNode.addChildren(retList);
                        shouldAdd = true;
                    }   
                }
                if (shouldAdd) {
                    namelessNodesList.add(namelessNode);
                }
            });
            return namelessNodesList;
    }
    
}
