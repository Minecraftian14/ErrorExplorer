package be.jdevelopment.tools.validation.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

class Body {

    JFrame frame = new JFrame() {{
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setBackground(new Color(243, 255, 115));
    }};
    GridBagLayout layout = new GridBagLayout() {{
        frame.setLayout(this);
    }};

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Errors");

    JTree tree;

    public void setVisible() {
        initTree();
        initView();
        initFrame();
    }

    void initTree() {
        tree = new JTree(root) {{
            setBackground(new Color(251, 255, 214));
            setCellRenderer(new CellRenderer());
        }};
    }

    void initView() {
        JScrollPane pane = new JScrollPane(tree, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        layout.setConstraints(pane, new GridBagConstraints(0, 0, 1, 1, 1, 1, 10, 1, new Insets(10, 10, 10, 10), 200, 10));
        frame.add(pane);
    }

    void initFrame() {
        frame.pack();
        frame.setShape(new RoundRectangle2D.Double(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight(), 20, 20));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
