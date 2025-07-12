package com.example.puzzel_game;

import com.example.puzzel_game.models.DatabaseManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;

public class Game extends Application {
    private int[][] board;
    private int emptyRow, emptyCol;
    private int moves;
    private Button[][] tiles;
    private Label movesLabel;
    private Label bestScoreLabel;
    private final int SIZE = 3; // Taille de la grille (3x3)
    private GridPane grid;
    private int currentPuzzleIndex = -1; // -1 for random, -2 for default reset
    private final int[][][] PREDEFINED_PUZZLES = {
            { // Puzzle 1
                    {0, 7, 3},
                    {2, 1, 4},
                    {5, 6, 8}
            },
            { // Puzzle 2
                    {1, 2, 4},
                    {8, 5, 7},
                    {0, 6, 3}
            },
            { // Puzzle 3
                    {2, 0, 4},
                    {1, 5, 3},
                    {8, 7, 6}
            },
            { // Puzzle 4
                    {6, 2, 4},
                    {8, 0, 1},
                    {7, 5, 3}
            },
            { // Puzzle 5
                    {6, 7, 0},
                    {1, 3, 2},
                    {5, 8, 4}
            },
            { // Puzzle 6
                    {7, 8, 1},
                    {6, 3, 5},
                    {2, 4, 0}
            },
            { // Puzzle 7
                    {2, 8, 0},
                    {1, 6, 3},
                    {5, 4, 7}
            }
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("8-Puzzle Game");
        showPuzzleSelection(primaryStage); // Show selection screen first
    }
    private void showPuzzleSelection(Stage primaryStage) {
        VBox selectionBox = new VBox(10);
        selectionBox.setAlignment(Pos.CENTER);
        selectionBox.setPadding(new Insets(20));

        Label title = new Label("Choose a Puzzle");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Add predefined puzzle buttons
        for (int i = 0; i < PREDEFINED_PUZZLES.length; i++) {
            Button puzzleBtn = new Button("Puzzle " + (i+1));
            final int puzzleIndex = i;
            puzzleBtn.setOnAction(e -> {
                currentPuzzleIndex = puzzleIndex;
                loadPuzzle(puzzleIndex);
                setupUI(primaryStage);
            });
            puzzleBtn.setMinWidth(120);
            selectionBox.getChildren().add(puzzleBtn);
        }

        // Add random puzzle button
        Button randomBtn = new Button("Random Puzzle");
        randomBtn.setOnAction(e -> {
            currentPuzzleIndex = -1;
            initializeGame(true);
            setupUI(primaryStage);
        });
        randomBtn.setMinWidth(120);

        selectionBox.getChildren().addAll(new Separator(), randomBtn);
        Scene selectionScene = new Scene(selectionBox, 250, 400);
        primaryStage.setScene(selectionScene);
        primaryStage.show();
    }
    private void shuffleBoard() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 8; i++) numbers.add(i);
        numbers.add(0); // 0 is empty space

        do {
            Collections.shuffle(numbers);
        } while (!isSolvable(numbers));

        int index = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = numbers.get(index++);
                if (board[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                }
            }
        }
    }

    private boolean isSolvable(List<Integer> numbers) {
        int inversions = 0;
        for (int i = 0; i < numbers.size(); i++) {
            for (int j = i + 1; j < numbers.size(); j++) {
                if (numbers.get(i) != 0 && numbers.get(j) != 0 && numbers.get(i) > numbers.get(j)) {
                    inversions++;
                }
            }
        }
        return inversions % 2 == 0;
    }

    private void loadPuzzle(int puzzleIndex) {
        board = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(PREDEFINED_PUZZLES[puzzleIndex][i], 0, board[i], 0, SIZE);
        }
        updateEmptyPosition();
        moves = 0;
    }

    private void updateEmptyPosition() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                    return;
                }
            }
        }
    }

    private void initializeGame(boolean random) {
        board = new int[SIZE][SIZE];
        if (random) {
            shuffleBoard();
        }
        // For predefined puzzles, board is set in loadPuzzle()
    }

    private void setupUI(Stage primaryStage) {
        // Main container
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Game title
        Label titleLabel = new Label("Puzzle Game");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        // Moves counter
        movesLabel = new Label("Mouvements: 0");
        movesLabel.setStyle("-fx-font-size: 14;");

        // Best score
        bestScoreLabel = new Label("Meilleur score: " + getBestScore());
        bestScoreLabel.setStyle("-fx-font-size: 14;");

        // Separator line
        Separator separator = new Separator();
        separator.setPrefWidth(200);
        separator.setStyle("-fx-padding: 10 0;");

        // Game grid
        grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        // Initialize tiles
        tiles = new Button[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Button btn = new Button();
                btn.setMinSize(60, 60);
                btn.setStyle("-fx-font-size: 16;");
                tiles[row][col] = btn;
                grid.add(btn, col, row);
            }
        }

        // Button container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // Reset current game button
        Button resetButton = new Button("Nouvelle partie");
        resetButton.setStyle("-fx-font-size: 14; -fx-padding: 5 15; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        resetButton.setOnAction(e -> resetGame());

        // Return to menu button
        Button menuButton = new Button("Menu Principal");
        menuButton.setStyle("-fx-font-size: 14; -fx-padding: 5 15; -fx-background-color: #2196F3; -fx-text-fill: white;");
        menuButton.setOnAction(e -> showPuzzleSelection(primaryStage));

        buttonBox.getChildren().addAll(resetButton, menuButton);

        // Add all components to root
        root.getChildren().addAll(
                titleLabel,
                movesLabel,
                bestScoreLabel,
                separator,
                grid,
                buttonBox
        );

        Scene scene = new Scene(root, 300, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        updateUI(); // Initialize the board display
    }

    private void handleTileClick(int row, int col) {
        if (board[row][col] != 0) {
            MOVE(board, row, col);
            moves++;
            movesLabel.setText("Mouvements: " + moves);

            if (chek(board)) {
                showVictory();
                saveScore();
                bestScoreLabel.setText("Meilleur score: " + getBestScore());
                resetGame();
            }
            updateUI();
        }
    }

    private static void permuter(int[][] M, int x1, int y1, int x2, int y2) {
        int temp = M[x1][y1];
        M[x1][y1] = M[x2][y2];
        M[x2][y2] = temp;
    }

    public static void MOVE(int[][] M, int x, int y) {
        if (y + 1 < M.length && M[x][y + 1] == 0) {
            permuter(M, x, y, x, y + 1);
        } else if (y - 1 >= 0 && M[x][y - 1] == 0) {
            permuter(M, x, y, x, y - 1);
        } else if (x - 1 >= 0 && M[x - 1][y] == 0) {
            permuter(M, x, y, x - 1, y);
        } else if (x + 1 < M.length && M[x + 1][y] == 0) {
            permuter(M, x, y, x + 1, y);
        }
    }

    public static boolean chek(int[][] M) {
        int[][] solution = {
                {1, 2, 3},
                {8, 0, 4},
                {7, 6, 5}
        };

        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                if (M[i][j] != solution[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        if (currentPuzzleIndex >= 0) {
            // Reload the selected predefined puzzle
            loadPuzzle(currentPuzzleIndex);
        } else if (currentPuzzleIndex == -1) {
            // Reshuffle random puzzle
            shuffleBoard();
        } else {
            // Default reset puzzle
            board = new int[][]{
                    {2, 0, 4},
                    {1, 5, 3},
                    {8, 7, 6}
            };
            updateEmptyPosition();
        }

        moves = 0;
        updateUI();
        movesLabel.setText("Mouvements: 0");
    }

    private void updateUI() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Button btn = tiles[row][col];
                if (board[row][col] != 0) {
                    btn.setText(String.valueOf(board[row][col]));
                    btn.setStyle("-fx-background-color: white;");
                    final int r = row, c = col;
                    btn.setOnAction(e -> handleTileClick(r, c));
                } else {
                    btn.setText("");
                    btn.setStyle("-fx-background-color: lightgray;");
                    btn.setOnAction(null);
                }
            }
        }
    }

    private void showVictory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Félicitations !");
        alert.setHeaderText("Vous avez résolu le puzzle !");
        alert.setContentText("Nombre de mouvements: " + moves);
        alert.showAndWait();
    }

    private void saveScore() {
        int best = DatabaseManager.getBestScore();
        if (moves < best || best == 0) {
            DatabaseManager.saveBestScore("Joueur", moves);
        }
    }

    private int getBestScore() {
        return DatabaseManager.getBestScore();
    }
}