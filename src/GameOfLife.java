import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class GameOfLife extends Application {
    public static final byte CELLSIZE = 4;
    public static final int GRIDWIDTH = 400;
    public static final int GRIDHEIGHT = 200;
    public static final Color ALIVE_COLOR = Color.WHITE;
    public static final Color DEAD_COLOR = Color.BLACK;
    private final ArrayList<ArrayList<Cell>> cells = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        GridPane gridPane = new GridPane();
        for (int x = 0; x < GRIDWIDTH; x++) {
            cells.add(new ArrayList<>());

            for (int y = 0; y < GRIDHEIGHT; y++) {
                Rectangle square = new Rectangle(CELLSIZE, CELLSIZE);
                cells.get(x).add(new Cell(x, y, this.randomBool(), square));

                gridPane.add(square, x, y);
            }
        }

        gridPane.setOnDragDetected(event -> gridPane.startFullDrag());

        Scene scene = new Scene(gridPane);

        stage.setScene(scene);
        stage.setTitle("Conway's Game of Life");
        stage.show();

        Timeline continuousUpdate = new Timeline(new KeyFrame(Duration.millis(50), (ActionEvent event) -> this.update()));
        continuousUpdate.setCycleCount(Timeline.INDEFINITE);
        continuousUpdate.play();

        scene.setOnKeyReleased(event -> {
            if (continuousUpdate.getStatus() == Animation.Status.STOPPED)
                continuousUpdate.play();
            else
                continuousUpdate.stop();
        });
    }

    private boolean randomBool() {
        return Math.random() > .5;
    }

    public static Color getColor(boolean bool) {
        return (bool) ? ALIVE_COLOR : DEAD_COLOR;
    }

    private static Color randomColor() {
        return Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }

    private boolean isAliveNextUpdate(Cell cell) {
        boolean isCurrentlyAlive = cell.isAlive();

        int x = cell.getX();
        int y = cell.getY();
        byte neighbours = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                try {
                    if (this.cells.get(i + x).get(j + y).isAlive())
                        neighbours++;
                } catch (Exception ignored) {
                }
            }
        }
        if (isCurrentlyAlive)
            neighbours--;

        if (isCurrentlyAlive) {
            return neighbours == 2 || neighbours == 3;
        } else {
            return neighbours == 3;
        }
    }

    public void update() {
        for (ArrayList<Cell> cellList : cells) {
            for (Cell cell : cellList) {
                if (this.isAliveNextUpdate(cell)) {
                    cell.revive();
                } else {
                    cell.kill();
                }
            }
        }

        for (ArrayList<Cell> cellList : cells) {
            for (Cell cell : cellList) {
                cell.update();
            }
        }
    }
}
