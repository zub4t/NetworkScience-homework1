package bfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import nodes.Node;

public class BFS {

    public static List<Set<Node>> doBFS(List<Node> list) {

        Stack<Node> forVisiting = new Stack<>();
        forVisiting.push(list.get((int) Math.random() * list.size()));
        List<Set<Node>> componentes = new ArrayList<>();

        do {
            Set<Node> visited = new TreeSet<>();

            while (!forVisiting.isEmpty()) {
                Node nn = forVisiting.pop();
                if (!visited.contains(nn)) {
                    for (Node n : nn.getConnectedTo()) {
                        forVisiting.push(n);
                    }
                    visited.add(nn);
                    list.remove(nn);
                }
            }
            componentes.add(visited);

            if (list.size() > 0) {
                forVisiting.push(list.get(0));
            } else {
                break;
            }

        } while (true);
        return componentes;
    }

}
