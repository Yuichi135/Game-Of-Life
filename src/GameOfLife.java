import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class GameOfLife extends Application {
    private final int GRID_WIDTH = 800;
    private final int GRID_HEIGHT = 400;
    private final int TILE_SIZE = 2;
    private final int MENU_OFFSET = 25;
    private boolean[][] currentGrid;
    private boolean[][] nextGrid;
    private GraphicsContext graphicsContext;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane mainBox = new BorderPane();
        Group root = new Group();

        mainBox.setTop(this.createMenu());
        mainBox.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(mainBox, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE + MENU_OFFSET, Color.BLACK);
        Canvas canvas = new Canvas(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.graphicsContext.setFill(Color.WHITE);

        root.getChildren().add(canvas);
        mainBox.setCenter(root);

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

        scene.setOnMousePressed(this::editTile);
        scene.setOnMouseDragged(this::editTile);
    }

    private void editTile(MouseEvent mouseEvent) {
        int x = (int) Math.floor(mouseEvent.getX() / TILE_SIZE);
        int y = (int) Math.floor((mouseEvent.getY() - 25) / TILE_SIZE);

        Point point = new Point(x * TILE_SIZE, y * TILE_SIZE);

        if (x >= GRID_WIDTH || x < 0 || y >= GRID_HEIGHT || y < 0) {
            return;
        }

        if (mouseEvent.isPrimaryButtonDown()) {
            this.currentGrid[x][y] = true;
            this.drawTile(point);
        } else if (mouseEvent.isSecondaryButtonDown()) {
            this.currentGrid[x][y] = false;
            this.clearTile(point);
        }
    }

    private MenuBar createMenu() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Settings");

        MenuItem clear = new MenuItem("Clear");
        MenuItem reset = new MenuItem("Reset");
        MenuItem save = new MenuItem("Save");
        MenuItem load = new MenuItem("Load");

        menu.getItems().addAll(clear, reset, save, load);
        menuBar.getMenus().add(menu);


        clear.setOnAction(event -> this.clear());
        reset.setOnAction(event -> this.reset());
        save.setOnAction(event -> this.save());
        load.setOnAction(event -> this.load());

        return menuBar;
    }

    private void clear() {
        this.currentGrid = this.createEmptyGrid();
        this.draw();
    }

    private void reset() {
        this.initGrid();
        this.draw();
    }

    private void save() {
        try (PrintWriter printWriter = new PrintWriter("saveFile.txt")) {
            printWriter.println(this.gridToString(this.currentGrid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String gridToString(boolean[][] grid) {
        StringBuilder stringBuilder = new StringBuilder(GRID_WIDTH * GRID_HEIGHT);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                stringBuilder.append((grid[x][y]) ? 1 : 0);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private void load() {
        File file = new File("saveFile.txt");
        try (Scanner scanner = new Scanner(file)) {
            int x = 0;
            while (scanner.hasNext()) {
                char[] row = scanner.nextLine().toCharArray();

                for (int y = 0; y < row.length; y++) {
                    this.currentGrid[x][y] = row[y] == '1';
                }
                x++;
            }
            this.draw();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    this.drawTile(new Point(x, y));
                y += TILE_SIZE;
            }
            x += TILE_SIZE;
            y = 0;
        }
    }

    private void drawTile(Point point) {
        this.graphicsContext.fillRect(point.getX(), point.getY(), TILE_SIZE, TILE_SIZE);
    }

    private void clearTile(Point point) {
        // Needs 1px offset?
        this.graphicsContext.clearRect(point.getX() + 1, point.getY() + 1, (TILE_SIZE == 1) ? 1 : TILE_SIZE - 1, (TILE_SIZE == 1) ? 1 : TILE_SIZE - 1);
    }
}