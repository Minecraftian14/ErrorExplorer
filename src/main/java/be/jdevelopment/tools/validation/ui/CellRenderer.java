package be.jdevelopment.tools.validation.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CellRenderer implements TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return new JLabel(value.toString()) {{
            if(row == 1) {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        
                    }
                });
            }
            setFont(new Font("Courier New", Font.BOLD, 16));
            if (leaf || row==0) setForeground(new Color(87, 90, 0));
            else if (expanded) setForeground(new Color(200, 205, 0));
            else setForeground(new Color(137, 149, 0));
        }};
    }

}
