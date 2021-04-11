package com.ast.shrink.ast;

import com.ast.shrink.utils.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Shrinks AST trees.
 */
public class ShrinkAST {
    private int count = 0;

    public ShrinkAST(){}

    /**
     *
     * @param root - root of our AST.
     * @param numberOfShrinks number of times it'll shrink the tree
     * @return root of shrinked AST.
     */
    public MyNode shrink(MyNode root, Integer numberOfShrinks){
        MyNode result = root;
        for (int i = 0; i < numberOfShrinks; i++){
            Optional<MyNode> resultOptional = shrinkOnce(result);
            if (resultOptional.isPresent()) {
                result = resultOptional.get();
            }
            else{
                break;
            }
        }
        return result;
    }

    private Optional<MyNode> shrinkOnce(MyNode root){
        Optional<Pair<String, String>> pairToShrinkOptinal = findPairTypesToShrink(root);
        if (!pairToShrinkOptinal.isPresent())
            return Optional.empty();
        Pair<String, String> pairToShrink = pairToShrinkOptinal.get();
        String newNodeName = getNextNodeName();
        MyNode result = root;
        while(true){
            Optional<Pair<MyNode, MyNode>>  pairNodes = findPairNodes(result, pairToShrink);
            if (pairNodes.isPresent()){
                MyNode parent = pairNodes.get().first;
                MyNode child = pairNodes.get().second;
                result = mergeNodes(parent, child, newNodeName);
            }
            else {
                break;
            }
        }
        return Optional.of(result);
    }

    /**
     * Merges two nodes in the tree. The name of resulting node will be NodeN where N - arbitrary number.
     * @param parent
     * @param child
     * @param newNodeName
     * @return root of transformed tree.
     */
    public MyNode mergeNodes(MyNode parent, MyNode child, String newNodeName){
        MyNode newNode = new MyNode(newNodeName);
        List<MyNode> children = parent.children.stream()
                                               .filter((MyNode x) -> !x.equals(child))
                                               .collect(Collectors.toList());

        children.addAll(child.children);
        newNode.setChildren(children);

        if (parent.parent == null)
            return newNode;
        else{
            MyNode pparent = parent.parent;
            pparent.replace(parent, newNode);
            while(pparent.parent != null){
                pparent = pparent.parent;
            }
            return pparent;
        }
    }

    /*
    First node is parent node, second is a child node.
     */

    /**
     * Finds pair of Nodes to merge. It has to have names corresponding to pairToShrink
     * @param node
     * @param pairToShrink
     * @return
     */
    public Optional<Pair<MyNode, MyNode>> findPairNodes(MyNode node, Pair<String, String> pairToShrink) {
        for (MyNode child : node.children){
            Pair<String, String> pair = new Pair<>(node.name, child.name);
            if (pairToShrink.equals(pair)){
                return  Optional.of(new Pair<>(node, child));
            }
        }

        for (MyNode child: node.children){
            Optional<Pair<MyNode, MyNode> > pairNodes = findPairNodes(child, pairToShrink);
            if (pairNodes.isPresent())
                return pairNodes;
        }
        return Optional.empty();
    }

    /**
     * Finds most frequently encountered pair of types in the tree.
     * @param root
     * @return
     */
    private static Optional<Pair<String, String>> findPairTypesToShrink(MyNode root){
        Map<Pair<String, String>, Integer> count = new HashMap<>();

        class Walk {
            public Walk(){}
            public void dfs(MyNode node) {
                Map<Pair<String, String>, Boolean> cntPairsWithChildren = new HashMap<>();

                for (MyNode child : node.children){
                    Pair<String, String> pair = new Pair<>(node.name, child.name);
                    if (!cntPairsWithChildren.containsKey(pair)){
                        cntPairsWithChildren.put(pair, true);
                    }
                    dfs(child);
                }
                for (Map.Entry<Pair<String, String>, Boolean> entry : cntPairsWithChildren.entrySet()){
                    Pair<String, String> key = entry.getKey();
                    if (count.containsKey(key)){
                        count.put(key, count.get(key) + 1);
                     }
                    else{
                        count.put(key, 1);
                    }
                }
            }
        }

        Walk walk = new Walk();
        walk.dfs(root);

        Map.Entry<Pair<String, String>, Integer> maxValue = null;
        for (Map.Entry<Pair<String, String>, Integer> entry : count.entrySet()){
            if (maxValue == null || maxValue.getValue() < entry.getValue()){
                maxValue = entry;
            }
        }
        if (maxValue == null){
            return Optional.empty();
        }
        else {
            return Optional.of(maxValue.getKey());
        }
    }

    private String getNextNodeName(){
        count++;
        return "Node" + count;
    }
}
