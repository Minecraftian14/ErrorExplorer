package be.jdevelopment.tools.validation.ui;

import be.jdevelopment.tools.validation.error.Failure;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class ErrorExplorer {

    Body body;
    DefaultMutableTreeNode root = new DefaultMutableTreeNode();

    public ErrorExplorer(HashSet<Failure> failures) {
        /*
         * Input:
         *   a, b, c, d, e, f
         *   a, b, c, g, h
         *   a, i, j
         *   k, l
         * */

        AtomicInteger max_depth = new AtomicInteger();
        ArrayList<Trip> list = new ArrayList<>();

        /*
         * Process I:
         * conversion to (x, y, z) where:
         *   x -> the node
         *   y -> index of parent, -1 if root
         *   Z -> depth
         *
         *   (a, r, 1)
         *   (b, a, 2)
         *   (c, b, 3)
         *   (d, c, 4)
         *   (e, d, 5)
         *   (f, e, 6)
         *   (g, c, 4)
         *   (h, h, 5)
         *   (i, a, 2)
         *   (j, i, 3)
         *   (k, r, 1)
         *   (l, k, 2)
         *
         * */

        failures.forEach(failure -> {
            String[] path = failure.getCode().split("[.]|[:]");

            for (int i = 0; i < path.length; i++) {

                if (i == 0) {
                    int finalI1 = i;
                    list.stream()
                            .filter(trip -> trip.depthIndex == 0)
                            .filter(trip -> trip.node.getUserObject().toString().equals(path[finalI1]))
                            .findFirst()
                            .ifPresentOrElse(
                                    trip -> System.out.flush()
                                    , () -> list.add(new Trip(new DefaultMutableTreeNode(path[finalI1]), -1, finalI1))
                            );

                } else {
                    int finalI = i;
                    max_depth.set(Math.max(max_depth.get(), finalI));
                    list.stream()
                            .filter(
                                    trip -> trip.node.getUserObject().toString().equals(path[finalI - 1])
                            )
                            .reduce((first, second) -> second)
                            .ifPresentOrElse(
                                    trip -> list.add(new Trip(
                                            new DefaultMutableTreeNode(path[finalI]),
                                            list.lastIndexOf(trip),
                                            finalI)),
                                    () -> System.out.println("PARENT LESS")
                            );
                }

            }

        });

        /*
         * Process II:
         * Starting from the highest depth (ioc 5)
         * the node is added to it's parent.
         *
         * 6:
         *   e.add(f);
         * 5:
         *   d.add(e);
         *   g.add(h);
         * 4:
         *   c.add(d);
         *   c.add(g);
         * 3:
         *   b.add(c);
         *   i.add(j);
         * 2:
         *   a.add(b);
         *   a.add(i);
         *   k.add(l);
         * 1:
         *   root.add(a);
         *   root.add(k);
         *
         * */

        for (int i = max_depth.get(); i >= 0; i--) {
            int finalI = i;

            list.stream()
                    .filter(trip -> trip.depthIndex == finalI)
                    .forEach(trip -> {
                                if (finalI != 0)
                                    list.stream()
                                            .filter(parent -> list.indexOf(parent) == trip.parentIndex)
                                            .findFirst()
                                            .ifPresentOrElse(
                                                    parent -> parent.node.add(trip.node),
                                                    () -> System.out.println("PARENT NOT FOUND")
                                            );
                                else root.add(trip.node);
                            }
                    );

        }

    }

    public void show(String title) {
        body = new Body(root, title);
    }
}
