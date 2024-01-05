package tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    static final Scanner scanner = new Scanner(System.in);
    static char[][] matrix = new char[3][3];

    public static void main(String[] args) {
        // write your code here
        menu();
    }

    public static void menu() {
        List<String> options = List.of("user", "easy", "medium", "hard");
        String option = "";
        do {
            System.out.print("Input command: ");
            option = scanner.nextLine();
            String[] params = option.split("\\s+");
            if (!option.equals("exit")) {
                if (params.length == 3) {
                    String firstParam = params[0], secondParam = params[1], thirdParam = params[2];
                    if (firstParam.equals("start") && options.contains(secondParam) && options.contains(thirdParam)) {
                        fillMatrix();
                        drawMatrix();
                        initGame(secondParam, thirdParam);
                    }
                } else {
                    System.out.println("Bad parameters!");
                }
            }
        } while (!option.equals("exit"));
    }

    public static void fillMatrix() {
        int i = 0, j = 0;
        while (i < 3) {
            matrix[i][j++] = ' ';
            if (j == 3) {
                i++;
                j = 0;
            }
        }
    }

    public static void printDashes() {
        for (int i = 0; i < 9; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    public static void drawMatrix() {
        printDashes();

        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println("|");
        }

        printDashes();
    }

    public static void move(char symbol) {
        boolean flag = true;
        while (flag) {
            System.out.print("Enter the coordinates: ");
            String line = scanner.nextLine();
            if (line.matches("^\\d+\\s+\\d+$")) {
                line = line.replaceAll("\\s+", " ");
                String[] coordinates = line.split(" ");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                if (x < 1 || x > 3 || y < 1 || y > 3) System.out.println("Coordinates should be from 1 to 3!");
                else {
                    char ch = matrix[x - 1][y - 1];
                    if (ch != ' ') System.out.println("This cell is occupied! Choose another one!");
                    else {
                        matrix[x - 1][y - 1] = symbol;
                        flag = false;
                    }
                }
            } else {
                System.out.println("You should enter numbers!");
            }
        }
    }

    public static boolean hasThreeInARow(char[][] matrix, int index, char symbol) {
        int contInRow = 0, contInColumn = 0, contDFLeft = 0, contDFRight = 0, j = matrix.length - 1;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[index][i] == symbol) contInRow++;
            if (matrix[i][index] == symbol) contInColumn++;
            if (matrix[i][i] == symbol) contDFLeft++;
            if (matrix[i][j--] == symbol) contDFRight++;
        }
        return contInRow == 3 || contInColumn == 3 || contDFLeft == 3 || contDFRight == 3;
    }

    public static boolean checkIsMatrixHasAnyEmptyCell() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix[i][j] == ' ') return true;
            }
        }
        return false;
    }

    public static char isFinished(char[][] matrix) {
        int i = 0;
        while (i < matrix.length) {
            if (hasThreeInARow(matrix, i, 'X')) return 'X';
            if (hasThreeInARow(matrix, i, 'O')) return 'O';
            i++;
        }
        return checkIsMatrixHasAnyEmptyCell() ? ' ' : 'T';
    }

    public static void initGame(String firstPlayer, String secondPlayer) {
        while (isFinished(matrix) == ' ') {
            //MOVE X
            if (firstPlayer.equals("user")) {
                move('X');
            } else {
                moveAI(firstPlayer, 'X');
            }

            drawMatrix();

            if (isFinished(matrix) == ' ') {
                //MOVE AI
                if (secondPlayer.equals("user")) {
                    move('O');
                } else {
                    moveAI(secondPlayer, 'O');
                }

                drawMatrix();
            }
        }
        char state = isFinished(matrix);
        if (state == 'X') System.out.println("X wins");
        else if (state == 'O') System.out.println("O wins");
        else System.out.println("Draw");
    }

    public static void moveAI(String levelAI, char symbol) {
        System.out.printf("Making move level \"%s\"%n", levelAI);

        List<Integer> emptyCells = cellsEmpty(matrix);
        int cell = (levelAI.equals("medium"))
                ? indexInThreeInARow(matrix, emptyCells, symbol)
                : (levelAI.equals("hard"))
                ? bestMove(symbol)
                : -1;

        //EASY
        if (cell == -1) cell = emptyCells.get((int) (Math.random() * (emptyCells.size() - 1)));
        int x = cell / 3;
        int y = cell % 3;
        matrix[x][y] = symbol;
    }

    public static List<Integer> cellsEmpty(char[][] matrix) {
        List<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                char cell = matrix[i][j];
                if (cell == ' ') {
                    emptyCells.add(i * matrix.length + j);
                }
            }
        }
        return emptyCells;
    }

    public static char[][] copyMatrix(char[][] matrix) {
        char[][] copy = new char[3][3];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                copy[i][j] = matrix[i][j];
            }
        }
        return copy;
    }

    //MEDIUM
    public static int indexInThreeInARow(char[][] matrix, List<Integer> emptyCells, char turn) {
        char[][] copyMatrix = copyMatrix(matrix);
        //FIND IS THERE ANY CELL THAT COULD BE IN A ROW
        char nextPlayer = turn == 'X' ? 'O' : 'X';
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < emptyCells.size(); i++) {
            int index = emptyCells.get(i);
            int x = index / 3;
            int y = index % 3;
            //CHECK WITH X
            copyMatrix[x][y] = turn;
            if (hasThreeInARow(copyMatrix, x, turn) || hasThreeInARow(copyMatrix, y, turn)) {
                moves.add(10);
            }

            if (moves.size() != i + 1) {
                copyMatrix[x][y] = nextPlayer;
                if (hasThreeInARow(copyMatrix, x, nextPlayer) || hasThreeInARow(copyMatrix, y, nextPlayer)) {
                    moves.add(-10);
                } else {
                    moves.add(0);
                }
            }
            copyMatrix[x][y] = ' ';
        }
        int bestMove = moves.indexOf(10);
        if (bestMove != -1) return emptyCells.get(bestMove);

        bestMove = moves.indexOf(-10);
        if (bestMove != -1) return emptyCells.get(bestMove);

        return -1;
    }

    public static int bestMove(char turn) {
        int bestScore = turn == 'X' ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int x = -1, y = -1;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == ' ') {
                    matrix[i][j] = turn;
                    int score = minimax(matrix, 0, turn, turn != 'X');
                    matrix[i][j] = ' ';
                   if (turn == 'X' && score > bestScore) {
                       bestScore = score;
                       x = i;
                       y = j;
                   } else if (turn == 'O' && score < bestScore) {
                       bestScore = score;
                       x = i;
                       y = j;
                   }
                }
            }
        }
        if (x != -1 && y != -1)
            return x * 3 + y;
        return -1;
    }

    public static int minimax(char[][] matrix, int depth, char turn, boolean isMaximizing) {
        char state = isFinished(matrix);
        if (state != ' ') {
            if (state == 'T') return 0;
            else if (state == 'X') return 1;
            else return -1;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    if (matrix[i][j] == ' ') {
                        matrix[i][j] = 'X';
                        int score = minimax(matrix, depth + 1, turn, false);
                        matrix[i][j] = ' ';
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    if (matrix[i][j] == ' ') {
                        matrix[i][j] = 'O';
                        int score = minimax(matrix, depth + 1, turn, true);
                        matrix[i][j] = ' ';
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }
}
