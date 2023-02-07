import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameOfLife extends Application {
    private final int GRID_WIDTH = 800;
    private final int GRID_HEIGHT = 400;
    private final int TILE_SIZE = 2;
    private boolean[][] currentGrid;
    private boolean[][] nextGrid;
    private GraphicsContext graphicsContext;

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE, Color.BLACK);
        Canvas canvas = new Canvas(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.graphicsContext.setFill(Color.WHITE);

        root.getChildren().add(canvas);
        stage.setScene(scene);
        stage.setTitle("Conway's Game of Life");
        stage.setResizable(false);
        stage.show();

        this.initGrid();
        this.draw();
        Timeline continuousUpdate = new Timeline(new KeyFrame(Duration.millis(25), event -> {
            this.update();
            this.draw();
        }));

        continuousUpdate.setCycleCount(Animation.INDEFINITE);
        continuousUpdate.play();

        scene.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.SPACE) return;
            if (continuousUpdate.getStatus() == Animation.Status.STOPPED)
                continuousUpdate.play();
            else
                continuousUpdate.stop();
        });
    }

    private void initGrid() {
        this.currentGrid = this.createEmptyGrid();

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                this.currentGrid[x][y] = this.randomBool();
            }
        }
    }

    private boolean[][] createEmptyGrid() {
        boolean[][] grid = new boolean[GRID_WIDTH][];
        for (int x = 0; x < GRID_WIDTH; x++) {
            grid[x] = new boolean[GRID_HEIGHT];
            for (int y = 0; y < GRID_HEIGHT; y++) {
                grid[x][y] = false;
            }
        }

        return grid;
    }

    private boolean randomBool() {
        return Math.random() > 0.5;
    }
    private int getNeighbours(int x, int y) {
        int neighbours = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (this.currentGrid[(i + x + GRID_WIDTH) % GRID_WIDTH][(j + y + GRID_HEIGHT) % GRID_HEIGHT])
                    neighbours++;
            }
        }

        if (this.currentGrid[x][y])
            neighbours--;

        return neighbours;
    }

    private void clearCanvas() {
        this.graphicsContext.clearRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
    }


    private void update() {
        this.nextGrid = this.createEmptyGrid();

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (this.currentGrid[x][y]) {
                    this.nextGrid[x][y] = this.getNeighbours(x, y) == 2 || this.getNeighbours(x, y) == 3;
                } else {
                    this.nextGrid[x][y] = this.getNeighbours(x, y) == 3;
                }
            }
        }

        System.arraycopy(this.nextGrid, 0, this.currentGrid, 0, this.currentGrid.length);
    }

    private void draw() {
        this.clearCanvas();

        int x = 0;
        int y = 0;
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (this.currentGrid[i][j])
                    this.graphicsContext.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                y += TILE_SIZE;
            }
            x += TILE_SIZE;
            y = 0;
        }
    }
}