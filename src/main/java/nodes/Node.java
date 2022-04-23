package nodes;


import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Node implements Comparable<Node> {
    Integer ID;
    Set<Node> connectedTo = null;

    int x;
    int y;

    @Override
    public int compareTo(Node o) {
        return this.ID.compareTo(o.getID());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            Node nn = (Node) o;
            return this.ID.equals(nn.getID());
        }

        return false;
    }

}
