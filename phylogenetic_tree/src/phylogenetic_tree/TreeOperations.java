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
        assert(Arrays.equals(listA.toArray(), listB.toArray()));
        
        List<Rozbicie> divisionsA = divideTreeTrivially(A);
        //System.out.println("***");
        List<Rozbicie> divisionsB = divideTreeTrivially(B);
        
        dist = divisionsA.size() + divisionsB.size();
        for(Rozbicie divA : divisionsA) {
            for(Rozbicie divB : divisionsB) {
                dist = Rozbicie.equals(divA, divB) ? dist - 2 : dist;
            }
        }
        System.out.println("topological distance = " + dist);
        return dist;
    }
    
    public static List<Rozbicie> divideTreeTrivially(TreeNode root) {
        
        List<Rozbicie> divisions = new ArrayList<Rozbicie>();
        
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
            
            divisions.add(new Rozbicie(left.toArray(new String[left.size()]),
                    union.toArray(new String[union.size()])));
            
            //System.out.println("<node " + tn.getName() + "> " +left + " : " + union);
        }
        
        
        return divisions;
    }
    
    private static List<String> getLeavesNames(TreeNode[] nodesSerial) {
        List<String> names = new ArrayList<String>();
        Arrays.asList(nodesSerial).stream().forEach(n -> {
            if (!n.getName().equals(""))
                names.add(n.getName());
        });
        return names;
    }
    
    // TODO: proper english term ???
    public static class Rozbicie {
        public final String[] A, B;
        
        public Rozbicie(String[] A, String[] B) {
            this.A = A;
            this.B = B;
            Arrays.sort(this.A);
            Arrays.sort(this.B);
        }
        
        public static boolean equals(Rozbicie first, Rozbicie second) {
            return Arrays.equals(first.A, second.A) && Arrays.equals(first.B, second.B);
        }
        
    }
    
    
    
}
