package be.jdevelopment.tools.validation.ui;

import be.jdevelopment.tools.validation.error.Failure;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.HashSet;

public class ErrorExplorer {

    Body body = new Body();

    public ErrorExplorer(HashSet<Failure> failures) {
        for (Failure failure : failures) {
            String[] parts = failure.getCode().split("\\.");

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(parts[0]);

            DefaultMutableTreeNode last_node = node;
            for (int i = 1; i < parts.length; i++) {
                if (i == parts.length - 1 && parts[i].contains(":")) {

                    DefaultMutableTreeNode sub = new DefaultMutableTreeNode(parts[i].substring(0, parts[i].indexOf(":")));
                    sub.add(new DefaultMutableTreeNode(parts[i].substring(parts[i].indexOf(":") + 1)));
                    last_node.add(sub);
                    last_node = sub;


                } else {
                    DefaultMutableTreeNode sub = new DefaultMutableTreeNode(parts[i]);
                    last_node.add(sub);
                    last_node = sub;
                }
            }

            body.root.add(node);
        }
    }

    public void show() {
        body.setVisible();
    }
}
