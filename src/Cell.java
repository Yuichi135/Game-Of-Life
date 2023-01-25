import javafx.scene.shape.Rectangle;

public class Cell {
    private int x;
    private int y;
    private boolean isAlive;
    private boolean nextLife;
    private Rectangle square;

    public Cell(int x, int y, boolean isAlive, Rectangle square) {
        this.x = x;
        this.y = y;
        this.isAlive = isAlive;
        this.nextLife = false;
        this.square = square;

        this.square.setFill(GameOfLife.getColor(this.isAlive));

        this.square.setOnMouseDragEntered(event -> {
            this.revive();
            this.update();
        });
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public void kill() {
        this.nextLife = false;
    }

    public void revive() {
        this.nextLife = true;
    }

    public void update() {
        if (this.isAlive == this.nextLife) return;
        this.isAlive = this.nextLife;
        this.square.setFill(GameOfLife.getColor(this.isAlive));
    }
}
