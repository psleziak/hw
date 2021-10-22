/**
 * Assignment 6 -- Prisoner's Dilemma -- 2ip90
 * part PlayingField
 * 
 * @author FILL IN
 * @author FILL IN
 * assignment group FILL IN
 * 
 * assignment copyright Kees Huizing
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

class PlayingField extends JPanel implements ActionListener, MouseListener {
    private final static int ROWS = 50;
    private final static int COLUMNS = 50;

    private final static int TIMER_DELAY = 1000;

    private Patch[][] grid;
    private double alpha;    
    private Timer timer;
    private int cellSize;
    private int offsetX;
    private boolean preferOwnStrategy;
    
    // random number genrator
    private static final long SEED = 37L; // seed for random number generator; any number goes
    public static final Random random = new Random( SEED );         
    
    public PlayingField() {
        this.timer = new Timer(TIMER_DELAY, this);

        this.resetGrid();

        this.addMouseListener(this);
    }

    /**
     * calculate and execute one step in the simulation 
     */
    public void step( ) {
        boolean[][] next = new boolean[this.grid.length][this.grid[0].length];

        for (int row = 0; row < this.grid.length; row++ ) {
            for (int column = 0; column < this.grid[0].length; column++ ) {
                Patch current = this.grid[row][column];
                next[row][column] = current.getNextStrategy(this.preferOwnStrategy);
            }
        }

        for (int row = 0; row < this.grid.length; row++ ) {
            for (int column = 0; column < this.grid[0].length; column++ ) {
                Patch current = this.grid[row][column];
                boolean previousStrategy = current.isCooperating();
                current.setCooperating(next[row][column]);
                if (previousStrategy != current.isCooperating()) {
                    current.markAsSwitched();
                }
            }
        }

        this.repaint();
    }
    
    public void setAlpha( double alpha ) {
        this.alpha = alpha;

        for (int row = 0; row < this.grid.length; row++ ) {
            for (int column = 0; column < this.grid[0].length; column++ ) {
                this.grid[row][column].setAlpha(this.alpha);
            }
        }
    }
    
    public double getAlpha( ) {
        return this.alpha;
    }
    
    // return grid as 2D array of booleans
    // true for cooperators, false for defectors
    // precondition: grid is rectangular, has non-zero size and elements are non-null
    public boolean[][] getGrid() {
        boolean[][] resultGrid = new boolean[grid.length][grid[0].length];
        for (int x = 0; x < grid.length; x++ ) {
            for (int y = 0; y < grid[0].length; y++ ) {
                resultGrid[x][y] = grid[x][y].isCooperating();
            }
        }
        
        return resultGrid; 
    }
    
    // sets grid according to parameter inGrid
    // a patch should become cooperating if the corresponding
    // item in inGrid is true
    public void setGrid( boolean[][] inGrid) {
        this.grid = new Patch[inGrid.length][inGrid[0].length];

        for (int row = 0; row < inGrid.length; row++ ) {
            for (int column = 0; column < inGrid[0].length; column++ ) {
                Patch patch = new Patch();
                patch.setCooperating(inGrid[row][column]);
                patch.setAlpha(this.alpha);
                this.grid[row][column] = patch;
            }
        }

        for (int row = 0; row < inGrid.length; row++ ) {
            for (int column = 0; column < inGrid[0].length; column++ ) {
                this.grid[row][column].setNeighbours(new Patch[] {
                    this.getNeighbour(row - 1, column),     // top
                    this.getNeighbour(row - 1, column + 1), // top-right
                    this.getNeighbour(row, column + 1),     // right
                    this.getNeighbour(row + 1, column + 1), // bottom-right
                    this.getNeighbour(row + 1, column),     // bottom
                    this.getNeighbour(row + 1, column - 1), // bottom-left
                    this.getNeighbour(row, column - 1),     // left
                    this.getNeighbour(row - 1, column - 1), // top-left
                });
            }
        }

        this.repaint();
    }   

    public void resetGrid() {
        boolean[][] newGrid = new boolean[ROWS][COLUMNS];
        for (int row = 0; row < ROWS; row++ ) {
            for (int column = 0; column < COLUMNS; column++ ) {
                newGrid[row][column] = PlayingField.random.nextBoolean();
            }
        }

        this.setGrid(newGrid);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.cellSize = Math.min(this.getWidth(), this.getHeight()) / Math.max(this.grid.length, this.grid[0].length);        
        int arcSize = this.cellSize / 5;
        this.offsetX = (this.getWidth() - (this.grid[0].length * this.cellSize)) / 2;

        g.setColor(Color.BLACK);
        g.fillRect(this.offsetX, 0, this.cellSize * this.grid[0].length, this.cellSize * this.grid.length);

        for (int row = 0; row < this.grid.length; row++) {
            for (int column = 0; column < this.grid[0].length; column++) {
                Patch current = this.grid[row][column];
                if (current.hasSwitched()) {
                    g.setColor(current.isCooperating() ? new Color(51, 153, 255) : Color.ORANGE);
                } else {
                    g.setColor(current.isCooperating() ? Color.BLUE : Color.RED);
                }                
                g.fillRoundRect(this.offsetX + (column * this.cellSize), row * this.cellSize, this.cellSize, this.cellSize, arcSize, arcSize);
            }
        }
    }

    public boolean isRunning() {
        return this.timer.isRunning();
    }

    public void toggleTimer() {
        if (this.timer.isRunning()) {
            this.timer.stop();
        } else {
            this.timer.start();
        }
    }

    public void setSpeed(int seconds) {
        this.timer.setDelay(seconds * 1000);
    }

    public int getSpeed() {
        return this.timer.getDelay() / 1000;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.timer) {
            this.step();
        }
    }

    private Patch getNeighbour(int row, int column) {
        row = this.fixRowColumnIndex(row, this.grid.length);
        column = this.fixRowColumnIndex(column, this.grid[0].length);

        return this.grid[row][column];
    }

    private int fixRowColumnIndex(int index, int maxValue) {
        return index < 0 ? maxValue - 1 : index % maxValue;
    }

    public void setPreferOwnStrategy(boolean preferOwnStrategy) {
        this.preferOwnStrategy = preferOwnStrategy;
    }
    
    public boolean getPreferOwnStrategy() {
        return this.preferOwnStrategy;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() - this.offsetX;
        int y = e.getY();

        if (x < 0 || y < 0) {
            return;
        }

        int row = y / this.cellSize;
        int column = x / this.cellSize;

        if (row >= this.grid.length || column >= this.grid[0].length) {
            return;
        }

        this.grid[row][column].toggleStrategy();

        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}

