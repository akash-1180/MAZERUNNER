package mazerunner.engine;

import java.awt.*;
import java.util.Random;
import java.util.Scanner;

public class GameEngine {

    char[][] board = new char[10][10];

    int stamina = 12;
    int gold = 5;
    int difficulty = -1;
    int apples;
    boolean isGameRunning = false;
    int[][] traps;

    Point trapPoint;

    Scanner s = new Scanner(System.in);
    Random rd = new Random();

    public GameEngine() {
        difficultyChoice();
        setBoard();
        setGold();
        setApples();
        setTraps();
    }

    public void startGame() {
        boolean isGameOver = false;
        printRules();
        while (!isGameOver) {
            printScore();
            printBoard();
            isGameOver = moveChoices();
            if (isGameRunning) {
                printScore();
                printBoard();
                break;
            }
        }
    }

    private void printRules() {
        System.out.println("1.\tThe player can move in 4 directions: left, right, up, and down inside the map. Moving out of the grid map is not allowed.\n" +
                "2.\tEach move reduces the stamina by 1.\n" +
                "3.\tVisiting a trap reduces 1 gold coin from the player’s collection. If the player has no gold coin at the time of entering a trap, the player loses the game. The trap remains there after the visit.\n" +
                "4.\tVisiting an apple increases the stamina by 3. The apple is eaten, and the cell becomes empty.\n" +
                "5.\tVisiting a gold coin increases 1 gold coin in the player’s collection. The coin is picked up, and the cell becomes empty.\n" +
                "6.\tThe game finishes when \n" +
                "\t•\t(Win) The player is at the exit cell. The score is the number of gold coins in the collection.\n" +
                "\t•\t(Lose) The player has 0 stamina but not at the exit. The score is -1.\n" +
                "\t•\t(Lose) The player falls in a trap but with no gold to consume. The score is -1.\n"+
                "7.\tM is player who is playing games\n"+
                "8.\tA is apples to increase your stamina by 3 points\n"+
                "9.\tT is trap in which you lose your gold coins\n"+
                "10.\tG is a gold coin to increase your gold\n\n");
    }

    private boolean moveChoices() {
        boolean isGameOver = true;
        System.out.print("\nSelect Your choice: \n1 - Up\n2 - Down\n3 - Right\n4 - Left\n5. Exit Game\nEnter Your Choice : ");
        int c = s.nextInt();
        switch (c) {
            case 1:
                isGameOver = toUp();
                break;
            case 2:
                isGameOver = toDown();
                break;
            case 3:
                isGameOver = toRight();
                break;
            case 4:
                isGameOver = toLeft();
                break;
            case 5:
                isGameOver = true;
                break;

            default:
                isGameOver = false;
                isGameRunning = true;
                System.out.println("Invalid choice!\n Try Again");
        }
        return isGameOver;
    }

    private void printScore() {
        System.out.println("\nStamina : " + stamina);
        System.out.println("Gold : " + gold + "");
    }

    private void difficultyChoice() {
        do {
            System.out.print("""
                    Do you wanna set difficulty level or continue with 5 difficulty level?\s
                    Enter Y to set Difficulty level by yourself between 0 to 10\s
                    Enter N to continue with difficulty level 5\s
                    Enter your choice(Y/N): \s""");

            String choice = s.next();
            if (choice.equals("Y") || choice.equals("y")) {
                setDifficulty();
            } else if (choice.equals("N") || choice.equals("n")) {
                difficulty = 5;
            } else {
                System.out.println("ERROR!");
                System.out.println("Invalid Choice");
            }
        } while (difficulty == -1);

    }

    private void setDifficulty() {
        while (difficulty < 0 || difficulty > 10) {
            System.out.print("Enter difficulty level between 0-10: ");
            difficulty = s.nextInt();
            if (difficulty < 0 || difficulty > 10) {
                System.out.println("ERROR!");
                System.out.println("Difficulty must be between 0 to 10");
            }
        }
    }

    private void setBoard() {
        System.out.println("\n\n");
        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {
                board[i][j] = '.';
            }
        }
        board[9][0] = 'M';
        board[rd.nextInt(10)][rd.nextInt(10)] = 'X';
    }

    private void setGold() {
        int row, column;
        for (int i = 0; i < gold; i++) {
            do {
                row = rd.nextInt(10);
                column = rd.nextInt(10);
                if (board[row][column] == '.') {
                    board[row][column] = 'G';
                }
            } while (board[row][column] != 'G');
        }
    }

    private void setApples() {
        apples = 10 - difficulty;
        int row, column;
        for (int i = 0; i < apples; i++) {
            do {
                row = rd.nextInt(10);
                column = rd.nextInt(10);
                if (board[row][column] == '.') {
                    board[row][column] = 'A';
                }
            } while (board[row][column] != 'A');
        }
    }

    private void setTraps() {
        traps = new int[difficulty][2];
        int row, column;
        int indexRow = 0;
        for (int i = 0; i < difficulty; i++) {
            do {
                row = rd.nextInt(10);
                column = rd.nextInt(10);
                if (board[row][column] == '.') {
                    board[row][column] = 'T';
                    traps[indexRow][0] = row;
                    traps[indexRow][1] = column;
                    indexRow++;
                }
            } while (board[row][column] != 'T');
        }
    }

    private void printBoard() {

        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {
                System.out.print(" " + board[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    private boolean toUp() {
        boolean isGameOver = false;
        Point playerIndex = getPlayerIndex();
        if (playerIndex != null) {
            if (playerIndex.x != 0) {
                board[playerIndex.x][playerIndex.y] = '.';
                for (int i = 0; i < traps.length; i++) {
                    if (traps[i][0] == playerIndex.x && traps[i][1] == playerIndex.y) {
                        board[playerIndex.x][playerIndex.y] = 'T';
                    }
                }
                if (board[playerIndex.x - 1][playerIndex.y] != '.') {
                    changeInValues(playerIndex.x - 1, playerIndex.y);
                }

                board[playerIndex.x - 1][playerIndex.y] = 'M';
                stamina--;
                isGameOver = checkGold();
                if (!isGameOver) {
                    isGameOver = checkStamina();
                }
            } else {
                System.out.println("You can not move further up and move out of the map :(");
                System.out.println("Kindly retry!");
            }
        }

        return isGameOver;
    }

    private boolean toDown() {
        boolean isGameOver = false;
        Point playerIndex = getPlayerIndex();
        if (playerIndex != null) {
            if (playerIndex.x != 9) {
                board[playerIndex.x][playerIndex.y] = '.';
                for (int i = 0; i < traps.length; i++) {
                    if (traps[i][0] == playerIndex.x && traps[i][1] == playerIndex.y) {
                        board[playerIndex.x][playerIndex.y] = 'T';
                    }
                }
                if (board[playerIndex.x + 1][playerIndex.y] != '.') {
                    changeInValues(playerIndex.x + 1, playerIndex.y);
                }

                board[playerIndex.x + 1][playerIndex.y] = 'M';
                stamina--;
                isGameOver = checkGold();
                if (!isGameOver) {
                    isGameOver = checkStamina();
                }
            } else {
                System.out.println("You can not move further Down and move out of the map :(");
                System.out.println("Kindly retry!");
            }
        }

        return isGameOver;
    }

    private boolean toLeft() {
        boolean isGameOver = false;
        Point playerIndex = getPlayerIndex();
        if (playerIndex != null) {
            if (playerIndex.x != 0) {
                board[playerIndex.x][playerIndex.y] = '.';
                for (int i = 0; i < traps.length; i++) {
                    if (traps[i][0] == playerIndex.x && traps[i][1] == playerIndex.y) {
                        board[playerIndex.x][playerIndex.y] = 'T';
                    }
                }
                if (board[playerIndex.x][playerIndex.y-1] != '.') {
                    changeInValues(playerIndex.x, playerIndex.y-1);
                }

                board[playerIndex.x][playerIndex.y-1] = 'M';
                stamina--;
                isGameOver = checkGold();
                if (!isGameOver) {
                    isGameOver = checkStamina();
                }
            } else {
                System.out.println("You can not move further up and move out of the map :(");
                System.out.println("Kindly retry!");
            }
        }

        return isGameOver;
    }

    private boolean toRight() {
        boolean isGameOver = false;
        Point playerIndex = getPlayerIndex();
        if (playerIndex != null) {
            if (playerIndex.x != 0) {
                board[playerIndex.x][playerIndex.y] = '.';
                for (int i = 0; i < traps.length; i++) {
                    if (traps[i][0] == playerIndex.x && traps[i][1] == playerIndex.y) {
                        board[playerIndex.x][playerIndex.y] = 'T';
                    }
                }
                if (board[playerIndex.x][playerIndex.y+1] != '.') {
                    changeInValues(playerIndex.x, playerIndex.y+1);
                }

                board[playerIndex.x][playerIndex.y+1] = 'M';
                stamina--;
                isGameOver = checkGold();
                if (!isGameOver) {
                    isGameOver = checkStamina();
                }
            } else {
                System.out.println("You can not move further up and move out of the map :(");
                System.out.println("Kindly retry!");
            }
        }

        return isGameOver;
    }

    private Point getPlayerIndex() {

        for (int rowIndex = 0; rowIndex < board.length; rowIndex++) {
            char[] row = board[rowIndex];
            if (row != null) {
                for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                    if (row[columnIndex] == 'M') {
                        return new Point(rowIndex, columnIndex);
                    }
                }
            }
        }
        return null;
    }

    private void changeInValues(int x, int y) {
        char block = board[x][y];
        switch (block) {
            case 'X' -> {
                gameOverOnExit();
            }
            case 'G' -> {
                gold++;
            }
            case 'A' -> {
                stamina = stamina + 3;
            }
            case 'T' -> {
                gold--;
            }
        }
    }

    private void gameOverOnExit() {
        System.out.println("Game cleared");
        System.out.println("Score : " + gold);
        System.out.println("Thank you for playing GoldMiner");
        isGameRunning = true;
    }

    private void gameOverOnNoStamina() {
        System.out.println("Game Over");
        System.out.println("You don't have enough stamina to play further.");
        System.out.println("Score : " + gold);
        System.out.println("Thank you for playing GoldMiner");
        isGameRunning = true;
    }

    private void gameOverOnNoGold() {
        System.out.println("Game Over");
        System.out.println("You don't have enough gold to play further.");
        System.out.println("Score : " + gold);
        System.out.println("Thank you for playing GoldMiner");
        isGameRunning = true;
    }

    private boolean checkGold() {
        if (gold == 0) {
            gameOverOnNoGold();
            return true;
        }
        return false;
    }

    private boolean checkStamina() {
        if (stamina == 0) {
            gameOverOnNoStamina();
            return true;
        }
        return false;
    }
}
