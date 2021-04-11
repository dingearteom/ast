package com.ast.shrink.printer;

import com.ast.shrink.ast.MyNode;

import static com.github.javaparser.utils.Utils.SYSTEM_EOL;
import static com.github.javaparser.utils.Utils.assertNotNull;

public class MyDotPrinter {
    private int nodeCount;

    public MyDotPrinter() {}

    public String output(MyNode node) {
        nodeCount = 0;
        StringBuilder output = new StringBuilder();
        output.append("digraph {");
        output(node, null, output);
        output.append(SYSTEM_EOL + "}");
        return output.toString();
    }

    public void output(MyNode node, String parentNodeName, StringBuilder builder) {
        String ndName = nextNodeName();
        builder.append(SYSTEM_EOL + ndName + " [label=\"" + escape(node.name) + "\"];");

        if (parentNodeName != null)
            builder.append(SYSTEM_EOL + parentNodeName + " -> " + ndName + ";");

        for (MyNode child: node.children) {
            assertNotNull(child);
            output(child, ndName, builder);
        }
    }

    private String nextNodeName() {
        return "n" + (nodeCount++);
    }

    private static String escape(String value) {
        return value.replace("\"", "\\\"");
    }
}
