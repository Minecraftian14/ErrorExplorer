package be.jdevelopment.tools.validation.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

class Body {

    JFrame frame = new JFrame();
    GridBagLayout layout;

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Errors");

    JTree tree;

    public Body(DefaultMutableTreeNode root, String name) {
        initFrame();
        initTitle(name);
        initTree(root);
        initView();
        finalFrame();
    }

    private void initFrame() {
        frame = new JFrame() {{
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setUndecorated(true);
            getContentPane().setBackground(new Color(243, 255, 115));

            setLayout(layout = new GridBagLayout());
            layout.setConstraints(add(new JButton("X") {{
                setFont(new Font("Comic Sans MS", Font.BOLD, 15));
                setMargin(new Insets(0, 5, 0, 5));
                setForeground(new Color(137, 149, 0));
                setBorderPainted(false);
                setFocusPainted(false);
                setBackground(new Color(251, 255, 214));
                addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

            }}), new GridBagConstraints(1, 0, 1, 1, 1, 1, 13, 3, new Insets(10, 3, 2, 10), 0, 0));
        }};
    }

    private void initTitle(String name) {
        frame.add(new JLabel(name, SwingConstants.CENTER) {{
            layout.setConstraints(this, new GridBagConstraints(0, 0, 1, 1, 1, 1, 10, 1, new Insets(10, 10, 2, 10), 0, 0));
            setBorder(null);
            setFont(new Font("Courier New", Font.BOLD, 24));
        }});
    }

    void initTree(DefaultMutableTreeNode root) {
        this.root = root;
        this.root.setUserObject("Error List");
        tree = new JTree(this.root) {{
            setBackground(new Color(251, 255, 214));
            setCellRenderer(new CellRenderer());
            setBorder(null);
        }};
    }

    void initView() {
        Bar pane = new Bar(tree);
        pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pane.setBackground(new Color(251, 255, 214));
        layout.setConstraints(pane, new GridBagConstraints(0, 1, 2, 1, 1, 1, 10, 1, new Insets(2, 10, 10, 10), 200, 10));
        frame.add(pane);
    }

    void finalFrame() {
        frame.pack();
        frame.setShape(new RoundRectangle2D.Double(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight(), 20, 20));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
