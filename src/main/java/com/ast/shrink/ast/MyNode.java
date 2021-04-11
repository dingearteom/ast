package com.ast.shrink.ast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyNode implements Serializable {
    private static final long serialVersionUID = 1L;
    public MyNode parent;
    public final String name;
    public List<MyNode> children;

    MyNode(String name){
        this(name, new ArrayList<>());
    }

    MyNode(String name, List<MyNode> children){
        this(name, children, null);
    }
    MyNode(String name, List<MyNode> children, MyNode parent){
        this.name = name;
        this.children = children;
        this.parent = parent;
    }

    public void setParent(MyNode parent){
        this.parent = parent;
    }

    public void setChildren(List<MyNode> children){
        this.children = children;
        for (MyNode child : children){
            child.parent = this;
        }
    }

    public void addChildren(List<MyNode> children){
        this.children.addAll(children);
        for (MyNode child : children){
            child.parent = this;
        }
    }


    public void replace(MyNode oldChild, MyNode newChild){
        children.remove(oldChild);
        children.add(newChild);
        oldChild.parent = null;
        newChild.parent = this;
    }


}
