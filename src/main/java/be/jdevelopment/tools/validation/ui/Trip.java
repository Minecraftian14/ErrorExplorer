package be.jdevelopment.tools.validation.ui;

import javax.swing.tree.DefaultMutableTreeNode;

class Trip {

    DefaultMutableTreeNode node;
    int parentIndex;
    int depthIndex;

    public Trip( DefaultMutableTreeNode node, int parentIndex, int depthIndex) {
        this.node = node;
        this.parentIndex = parentIndex;
        this.depthIndex = depthIndex;
    }

    @Override
    public String toString() {
        return "\"Trip\": {" +
                "\"node\":\"" + node +
                "\",\"parentIndex\":" + parentIndex +
                ",\"depthIndex\":" + depthIndex +
                '}';
    }
}
