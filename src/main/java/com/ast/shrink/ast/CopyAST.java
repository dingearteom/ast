package com.ast.shrink.ast;

import com.github.javaparser.ast.Node;
import static com.github.javaparser.utils.Utils.assertNotNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.metamodel.NodeMetaModel;
import com.github.javaparser.metamodel.PropertyMetaModel;

public class CopyAST {

    private CopyAST(){};

    public static MyNode copyAST(Node node){
        return copyAST(node, "root", null);
    }

    public static MyNode copyAST(Node node, String name, MyNode parent){
        assertNotNull(node);
        NodeMetaModel metaModel = node.getMetaModel();
        List<PropertyMetaModel> allPropertyMetaModels = metaModel.getAllPropertyMetaModels();
        List<PropertyMetaModel> attributes = allPropertyMetaModels.stream().filter(PropertyMetaModel::isAttribute)
                .filter(PropertyMetaModel::isSingular).collect(toList());
        List<PropertyMetaModel> subNodes = allPropertyMetaModels.stream().filter(PropertyMetaModel::isNode)
                .filter(PropertyMetaModel::isSingular).collect(toList());
        List<PropertyMetaModel> subLists = allPropertyMetaModels.stream().filter(PropertyMetaModel::isNodeList)
                .collect(toList());

        MyNode myNode = new MyNode(name + "(Type=" + metaModel.getTypeName() + ")");

        List<MyNode> children = new ArrayList<>();
        for (PropertyMetaModel a : attributes){
            MyNode nd = new MyNode(a.getName() + ": " + escapeValue(a.getValue(node).toString()), new ArrayList<MyNode>(), myNode);
            children.add(nd);
        }

        for (PropertyMetaModel sn : subNodes) {
            Node nd = (Node) sn.getValue(node);
            if (nd != null) {
                children.add(copyAST(nd, sn.getName(), myNode));
            }
        }

        for (PropertyMetaModel sl : subLists) {
            NodeList<? extends Node> nl = (NodeList<? extends Node>) sl.getValue(node);
            if (nl != null && nl.isNonEmpty()) {
                String rootName = sl.getName();
                String slName = sl.getName();
                slName = slName.endsWith("s") ? slName.substring(0, sl.getName().length() - 1) : slName;
                List<MyNode> childrenSubList = new ArrayList<>();
                MyNode rootNode = new MyNode(rootName);
                for (Node nd : nl)
                    childrenSubList.add(copyAST(nd, slName, rootNode));
                rootNode.setChildren(childrenSubList);
                rootNode.setParent(myNode);
                children.add(rootNode);
            }
        }

        myNode.setChildren(children);
        myNode.setParent(parent);

        return myNode;
    }

    private static String escapeValue(String value) {
        return "\"" + value
                .replace("\\", "\\\\")
                .replaceAll("\"", "\\\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("\b", "\\b")
                .replace("\t", "\\t") + "\"";
    }
}
