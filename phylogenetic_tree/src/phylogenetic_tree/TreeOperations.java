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
    
}
