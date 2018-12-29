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
        
        
        
        return dist;
    }
    
    private static List<String> getLeavesNames(TreeNode[] nodesSerial) {
        List<String> names = new ArrayList<String>();
        Arrays.asList(nodesSerial).stream().forEach(n -> {
            names.add(n.getName());
        });
        return names;
    }
    
    // TODO: proper english term ???
    public class Rozbicie {
        public final String[] A, B;
        
        public Rozbicie(String[] A, String[] B) {
            this.A = A;
            this.B = B;
        }
        
        public boolean equals(Rozbicie other) {
            return Arrays.equals(A, other.A) && Arrays.equals(B, other.B);
        }
        
    }
    
    
    
}
