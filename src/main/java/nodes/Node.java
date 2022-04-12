package nodes;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Node implements Comparable<Node> {
    Integer ID;
    List<Node> connectedTo = new ArrayList<>();
    Integer degree;
    int x;
    int y;

    @Override
    public int compareTo(Node o) {
        return this.ID.compareTo(o.getID());
    }

}
