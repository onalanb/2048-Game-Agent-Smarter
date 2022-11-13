// Baran Onalan
// 11 / 4 / 2022 (Last Edited)
// This is the driver class for the 2048 game that read input states
// from a 2D array it creates randomly and writes the iteration with
// best score the path it took with the number of steps and the final
// state of the 2D array to "2048_out.txt"

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameOf2048Driver {
    private static Random random = new Random();

    // A driver for basic testing and where output file is created.
    public static void main(String[] args) {
        // Random LSA
        int randomIteration = 0;
        int randomBestScore = 0;
        Node randomBestNode = null;

        // Maximizing LSA
        int maximizingIteration = 0;
        int maximizingBestScore = 0;
        Node maximizingBestNode = null;

        // Generate the random 2D array with two 2s.
        for (int i = 0; i < 25; i++) {
            int[][] initialState = new int[4][4];
            int row1 = random.nextInt(4);
            int col1 = random.nextInt(4);
            initialState[row1][col1] = 2;
            int row2 = random.nextInt(4);
            int col2 = random.nextInt(4);
            while (row1 == row2 && col1 == col2) {
                row2 = random.nextInt(4);
                col2 = random.nextInt(4);
            }
            initialState[row2][col2] = 2;

            // Randomizing LSA
            Node randomLSAResult = GameOf2048.scoreUsingRandomLSA(initialState);
            if (randomLSAResult.nodeScore() > randomBestScore) {
                randomBestScore = randomLSAResult.nodeScore();
                randomBestNode = randomLSAResult;
                randomIteration = i + 1;
            }

            // Maximizing LSA
            Node maximizingLSAResult =  GameOf2048.scoreUsingMaximizingLSA(initialState);
            if (maximizingLSAResult.nodeScore() > maximizingBestScore) {
                maximizingBestScore = maximizingLSAResult.nodeScore();
                maximizingBestNode = maximizingLSAResult;
                maximizingIteration = i + 1;
            }
        }
        createOutputFile(randomBestNode, randomIteration, maximizingBestNode, maximizingIteration);
    }

    // Creates an output file then writes the score and path from each node in nodes to file.
    private static void createOutputFile(Node randomBestNode, int randomIteration,
                                         Node maximizingBestNode, int maximizingIteration) {
        File file = new File("2048_out.txt");
        try {
            FileWriter fr = new FileWriter(file, false);
            int score;
            List<String> path;
            // Random Local Search Algorithm
            score = randomBestNode.nodeScore();
            path = randomBestNode.nodePath();
            fr.write("Random LSA Result:\n");
            // Print the iteration
            fr.write("Iteration: " + randomIteration + "\n");
            // Print score
            fr.write("Score: " + score + "\n");
            // Print the path
            fr.write("Path: " + path.size() + " Steps " + path.get(0));
            for (int i = 1; i < randomBestNode.nodePath().size(); i++) {
                fr.write("," + path.get(i));
            }
            // Print the final state 2D array
            fr.write("\nFinal State 2D Array:\n");
            int[][] arr = randomBestNode.nodeArr();
            for (int[] ints : arr) {
                fr.write(Arrays.toString(ints) + "\n");
            }

            // Maximizing Local Search Algorithm
            score = maximizingBestNode.nodeScore();
            path = maximizingBestNode.nodePath();
            fr.write("\nMaximizing LSA Result:\n");
            // Print the iteration
            fr.write("Iteration: " + maximizingIteration + "\n");
            // Print score
            fr.write("Score: " + score + "\n");
            // Print the path
            fr.write("Path: " + path.size() + " Steps " + path.get(0));
            for (int i = 1; i < maximizingBestNode.nodePath().size(); i++) {
                fr.write("," + path.get(i));
            }
            // Print the final state 2D array
            fr.write("\nFinal State 2D Array:\n");
            arr = maximizingBestNode.nodeArr();
            for (int[] ints : arr) {
                fr.write(Arrays.toString(ints) + "\n");
            }

            fr.close();
        } catch (IOException ioEx) {
            System.out.println("IO error occurred.");
        }
    }
}