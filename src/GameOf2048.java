// Baran Onalan
// 11 / 4 / 2022 (Last Edited)
// This class plays through a game of 2048 that starts with a random 2D array
// that has two 2's placed randomly on the board. It plays the game using
// Random Local Search Algorithm and Maximizing Local Search Algorithm.
// The game is played until there are no more valid moves OR 2048 is found.

import java.util.*;

public class GameOf2048 {
    // Initialize random here to use when placing new 2s or 4s
    private static Random random = new Random();
    private static String[] directions = new String[]{"U", "D", "L", "R"};

    // Determines the path that results in the highest score for Random Local Search Algorithm.
    public static Node scoreUsingRandomLSA(int[][] arr) {
        Node currentState = new Node (0, new ArrayList<String>(), arr);
        int score = 0;

        while (score >= 0) {
            Tuple tuple = moveAndValidateRandom(currentState.nodeArr());
            // There is no next move if return tuple is null.
            if (tuple == null) break;
            score = tuple.getInt();
            // Only adds to the queue when the move is valid.
            if (score >= 0) {
                List<String> path = currentState.nodePath();
                List<String> newPath = new ArrayList<String>(path);
                newPath.add(tuple.getDir());
                score += currentState.nodeScore();
                // Current state set into next current state.
                currentState = new Node(score, newPath, tuple.getArr());
                // End game if you find 2048.
                if (contains2048(tuple.getArr())) break;
            }
        }

        return currentState;
    }

    // Determines the path that results in the highest score for Maximizing Local Search Algorithm.
    public static Node scoreUsingMaximizingLSA(int[][] arr) {
        Node currentState = new Node (0, new ArrayList<String>(), arr);
        int score = 0;

        while (score >= 0) {
            Tuple tuple = moveAndValidateMaximizing(currentState.nodeArr());
            // There is no next move if return tuple is null.
            if (tuple == null) break;
            score = tuple.getInt();
            // Only adds to the queue when the move is valid.
            if (score >= 0) {
                List<String> path = currentState.nodePath();
                List<String> newPath = new ArrayList<String>(path);
                newPath.add(tuple.getDir());
                score += currentState.nodeScore();
                // Current state set into next current state.
                currentState = new Node(score, newPath, tuple.getArr());
                // End game if you find 2048.
                if (contains2048(tuple.getArr())) break;
            }
        }

        return currentState;
    }

    // Encapsulates the move logic in 4 sub steps:
    // Step 1: Using arrAfterShift() method, shift all numbers in X direction.
    //         This also returns whether a shift occurred or not.
    // Step 2: Using arrScoreAfterMerge() method, merge all numbers in X direction where possible.
    //         This also returns the score.
    // Step 3: Using arrAfterShift() method, shift all numbers in X direction.
    //         Shift only happens here if a merge occurred in step 2.
    // Step 4: Insert new number if move logic was valid. If invalid score is -1.
    //
    // Maximizing Local Best First Search Algorithm move and validate works such that
    // it will check all possible moves and take the local best score out of all options.
    // It calculates the best score by adding the current score and next score and committing
    // to the path that results in the highest one.
    private static Tuple moveAndValidateMaximizing(int[][] arr) {
        int currentAndNextScore = 0;
        int maxScore = -1;
        boolean contains2048 = false; // ?????????????????
        Tuple result = null;
        for (int dir1 = 0; dir1 < 4; dir1++) { // Direction 1
            for (int twoOrFour = 2; twoOrFour <= 4; twoOrFour = twoOrFour + 2) { // 2 or 4
;               for (int dir2 = 0; dir2 < 4; dir2++) { // Direction 2
                    Tuple tuple = arrAfterShift(arr, directions[dir1]);
                    int[][] arrResult = tuple.getArr();
                    boolean shifted = tuple.getInt() == 1;
                    tuple = arrScoreAfterMerge(arrResult, directions[dir1]);
                    arrResult = tuple.getArr();
                    int score = tuple.getInt();
                    tuple = arrAfterShift(arrResult, directions[dir1]);

                    // Move is only invalid if shift did not occur, and merge did not occur.
                    if (score == 0 && !shifted) {
                        // Move is invalid.
                        continue;
                    } else {
                        // Move is valid.
                        int numEmptyPos = numberOfEmptyPositions(tuple.getArr());
                        if (numEmptyPos == 0) { // ?????????????????????
                            if (currentAndNextScore >= maxScore) {
                                maxScore = currentAndNextScore;
                                result = new Tuple(tuple.getArr(), score, directions[dir1]);
                            }
                        }
                        for (int emptyPos = 0; emptyPos < numEmptyPos; emptyPos++) {
                            // Inserting new value in empty position.
                            int[][] arrAfterInsert = insertNewNumber(tuple.getArr(), emptyPos, twoOrFour);
                            Tuple nextTuple = arrAfterShift(arrAfterInsert, directions[dir2]);
                            int[][] arrNextResult = nextTuple.getArr();
                            nextTuple = arrScoreAfterMerge(arrNextResult, directions[dir2]);
                            currentAndNextScore = score + nextTuple.getInt();
                            if (currentAndNextScore >= maxScore) {
                                maxScore = currentAndNextScore;
                                result = new Tuple(arrAfterInsert, score, directions[dir1]);
                            }
                        }
                    }
                }
            }
        }

        // Will only get here if game over.
        return result;
    }

    // Encapsulates the move logic in 4 sub steps:
    // Step 1: Using arrAfterShift() method, shift all numbers in X direction.
    //         This also returns whether a shift occurred or not.
    // Step 2: Using arrScoreAfterMerge() method, merge all numbers in X direction where possible.
    //         This also returns the score.
    // Step 3: Using arrAfterShift() method, shift all numbers in X direction.
    //         Shift only happens here if a merge occurred in step 2.
    // Step 4: Insert new number if move logic was valid. If invalid score is -1.
    //
    // Random Local Search Algorithm move and validate works such that it will pick
    // the first locally better score, meaning any value > 0 is the path that will be taken.
    // The path taken is random, the 2 or 4 being inserted is random, which empty space it picks is random.
    private static Tuple moveAndValidateRandom(int[][] arr) {
        Tuple allPathsZeroScore = null;
        int countNotChosenRetry = 0;
        HashSet<String> notChosen = new HashSet<>();
        int currentAndNextScore = 0;
        while (currentAndNextScore == 0 && countNotChosenRetry < 480) {
            int randomDirection1 = random.nextInt(4);
            String direction1 = directions[randomDirection1];
            Tuple tuple = arrAfterShift(arr, direction1);
            int[][] arrResult = tuple.getArr();
            boolean shifted = tuple.getInt() == 1;
            tuple = arrScoreAfterMerge(arrResult, direction1);
            arrResult = tuple.getArr();
            int score = tuple.getInt();
            tuple = arrAfterShift(arrResult, direction1);
            // Move is only invalid if shift did not occur, and merge did not occur.
            if (score == 0 && !shifted) {
                // Move is invalid.
                countNotChosenRetry++;
                continue;
            } else {
                // Move is valid.
                int numEmptyPos = numberOfEmptyPositions(tuple.getArr());
                // Game over condition.
                if (numEmptyPos == 0) {
                    return new Tuple(tuple.getArr(), score, direction1);
                }
                int randomEmptyPosition = random.nextInt(numEmptyPos);
                int rngTwoOrFour = random.nextInt(2);
                int twoOrFour = (rngTwoOrFour == 0) ? 2 : 4;
                // Inserting new value in empty position.
                int[][] arrAfterInsert = insertNewNumber(tuple.getArr(), randomEmptyPosition, twoOrFour);

                int randomDirection2 = random.nextInt(4);
                String direction2 = directions[randomDirection2];
                Tuple nextTuple = arrAfterShift(arrAfterInsert, direction2);
                int[][] arrNextResult = nextTuple.getArr();
                nextTuple = arrScoreAfterMerge(arrNextResult, direction2);
                currentAndNextScore = score + nextTuple.getInt();
                if (currentAndNextScore > 0) {
                    return new Tuple(arrAfterInsert, score, direction1);
                } else {
                    allPathsZeroScore = new Tuple(arrAfterInsert, score, direction1);
                    boolean isNewPath = notChosen.add(direction1 + "_" + randomEmptyPosition + "_" + twoOrFour + "_" + direction2);
                    if (!isNewPath) countNotChosenRetry++;
                }
            }
        }
        // Will only get here if game over OR all possible paths have zero current and next score.
        return allPathsZeroScore;
    }

    // contains2048 method will check a 2D array state and return
    // TRUE: If it contains 2048
    // FALSE: If it does not contain 2048
    private static boolean contains2048(int[][] arr) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (arr[i][j] == 2048) {
                    return true;
                }
            }
        }
        return false;
    }

    // Returns the number of empty positions for current state in our 2048 game.
    private static int numberOfEmptyPositions(int[][] arr) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (arr[i][j] == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    // Inserts new number (2) in the first available position from left to right, top to bottom.
    private static int[][] insertNewNumber(int[][] arr, int emptyPosition, int twoOrFour) {
        int count = 0;
        int[][] result = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // copy input arr into result arr
                result[i][j] = arr[i][j];
                // Position is empty.
                if (result[i][j] == 0) {
                    // Specific nth empty position.
                    if (emptyPosition == count) {
                        result[i][j] = twoOrFour;
                    }
                    count++;
                }
            }
        }
        // Array is full, and you lose, or path is illegal.
        // We will never arrive here anyway since we don't insert for invalid moves.
        return result;
    }

    // Merges and calculates the score based on the merge that occurred.
    // Input: Is the shifted array we get from arrAfterShift()
    // Input: directionOfMerge U = up, D = down, L = left, R = right
    // Output: Tuple of the resulting array and the score.
    private static Tuple arrScoreAfterMerge(int[][] arr, String directionOfMerge) {
        int[][] result = new int[4][4];
        int score = 0;
        if (directionOfMerge.equalsIgnoreCase("U")) {
            // Merge up.
            for (int i = 0; i < 4; i++) {
                int position = 0;
                for (int j = 0; j < 3; j++) {
                    if (arr[j][i] == arr[j + 1][i]) {
                        score += arr[j][i] * 2;
                        result[position][i] = arr[j][i] * 2;
                        arr[j + 1][i] = 0;
                        position++;
                    } else if (arr[j][i] != arr[j + 1][i]) {
                        result[position][i] = arr[j][i];
                        position++;
                    }
                }
                result[3][i] = arr[3][i];
            }
        } else if (directionOfMerge.equalsIgnoreCase("D")) {
            // Merge down.
            for (int i = 0; i < 4; i++) {
                int position = 3;
                for (int j = 3; j > 0; j--) {
                    if (arr[j][i] == arr[j - 1][i]) {
                        score += arr[j][i] * 2;
                        result[position][i] = arr[j][i] * 2;
                        arr[j - 1][i] = 0;
                        position--;
                    } else if (arr[j][i] != arr[j - 1][i]) {
                        result[position][i] = arr[j][i];
                        position--;
                    }
                }
                result[0][i] = arr[0][i];
            }
        } else if (directionOfMerge.equalsIgnoreCase("L")) {
            // Merge left.
            for (int i = 0; i < 4; i++) {
                int position = 0;
                for (int j = 0; j < 3; j++) {
                    if (arr[i][j] == arr[i][j + 1]) {
                        score += arr[i][j] * 2;
                        result[i][position] = arr[i][j] * 2;
                        arr[i][j + 1] = 0;
                        position++;
                    } else if (arr[i][j] != arr[i][j + 1]) {
                        result[i][position] = arr[i][j];
                        position++;
                    }
                }
                result[i][3] = arr[i][3];
            }
        } else if (directionOfMerge.equalsIgnoreCase("R")) {
            // Merge right.
            for (int i = 0; i < 4; i++) {
                int position = 3;
                for (int j = 3; j > 0; j--) {
                    if (arr[i][j] == arr[i][j - 1]) {
                        score += arr[i][j] * 2;
                        result[i][position] = arr[i][j] * 2;
                        arr[i][j - 1] = 0;
                        position--;
                    } else if (arr[i][j] != arr[i][j - 1]) {
                        result[i][position] = arr[i][j];
                        position--;
                    }
                }
                result[i][0] = arr[i][0];
            }
        } else {
            System.out.println("Not a valid direction");
        }
        return new Tuple(result, score);
    }

    // Shifts the input array.
    // Input: Is the original or merged array.
    // Input: directionOfMerge U = up, D = down, L = left, R = right
    // Output: Tuple of the resulting array and the validity of the move.
    // NOTE: The resulting array doesn't get used if the shift is invalid.
    private static Tuple arrAfterShift(int[][] arr, String directionOfShift) {
        int[][] result = new int[4][4];
        int counter = 0;
        boolean shifted = false;
        boolean seenZero = false;
        if (directionOfShift.equalsIgnoreCase("U")) {
            // Shift up.
            for (int i = 0; i < 4; i++) {
                int position = 0;
                seenZero = false;
                shifted = false;
                for (int j = 0; j < 4; j++) {
                    if (arr[j][i] != 0) {
                        result[position][i] = arr[j][i];
                        position++;
                        if (seenZero) {
                            shifted = true;
                        }
                    } else {
                        seenZero = true;
                    }
                }
                if (shifted) {
                    counter++;
                }
            }
        } else if (directionOfShift.equalsIgnoreCase("D")) {
            // Shift down.
            for (int i = 0; i < 4; i++) {
                int position = 3;
                seenZero = false;
                shifted = false;
                for (int j = 3; j >= 0; j--) {
                    if (arr[j][i] != 0) {
                        result[position][i] = arr[j][i];
                        position--;
                        if (seenZero) {
                            shifted = true;
                        }
                    } else {
                        seenZero = true;
                    }
                }
                if (shifted) {
                    counter++;
                }
            }
        } else if (directionOfShift.equalsIgnoreCase("L")) {
            // Shift left.
            for (int i = 0; i < 4; i++) {
                int position = 0;
                seenZero = false;
                shifted = false;
                for (int j = 0; j < 4; j++) {
                    if (arr[i][j] != 0) {
                        result[i][position] = arr[i][j];
                        position++;
                        if (seenZero) {
                            shifted = true;
                        }
                    } else {
                        seenZero = true;
                    }
                }
                if (shifted) {
                    counter++;
                }
            }
        } else if (directionOfShift.equalsIgnoreCase("R")) {
            // Shift right.
            for (int i = 0; i < 4; i++) {
                int position = 3;
                seenZero = false;
                shifted = false;
                for (int j = 3; j >= 0; j--) {
                    if (arr[i][j] != 0) {
                        result[i][position] = arr[i][j];
                        position--;
                        if (seenZero) {
                            shifted = true;
                        }
                    } else {
                        seenZero = true;
                    }
                }
                if (shifted) {
                    counter++;
                }
            }
        } else {
            System.out.println("Not a valid direction");
        }
        if (counter != 0) {
            // 1 is true (valid shift)
            return new Tuple(result, 1);
        } else {
            // -1 is false (potentially invalid shift)
            return new Tuple(result, -1);
        }
    }
}