// Baran Onalan
// 11 / 4 / 2022 (Last Edited)
// This class is used to encapsulate the graph node in BFS method for 2048 game states.
import java.util.List;

public class Node {
    // Stores path to current state/node.
    private List<String> pathToNode;
    // Stores cumulative score to current state/node.
    private int score;
    // Stores up-to-date game state for current node.
    private int[][] array;

    public Node (int score, List<String> pathToNode, int[][] array) {
        this.pathToNode = pathToNode;
        this.score = score;
        this.array = array;
    }

    // Getter for the cumulative node score.
    public int nodeScore() { return this.score; }

    // Getter for the node path.
    public List<String> nodePath() { return this.pathToNode; }

    // Getter for the node level, how many turns have occurred after initial state.
    public int nodeLevel() { return this.pathToNode.size(); }

    // Getter for the current node array state.
    public int[][] nodeArr() { return this.array; }
}
