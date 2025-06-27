package edu.curtin.app.grids;

import edu.curtin.app.models.Terrain;

public class Grid {
    private final int rows;
    private final int cols;
    private final Square[][] squares;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.squares = new Square[rows][cols];

        /**
         * Initialize with default flat terrain*/
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                squares[i][j] = new Square(Terrain.FLAT);
            }
        }
    }

    /**get all rows*/
    public int getRows() {
        return rows;
    }

    /**get all colms*/
    public int getCols() {
        return cols;
    }

    /**just in case method for get grid square*/
    public Square getGridSquare(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Invalid grid square coordinates: (" + row + ", " + col + ")");
        }
        return squares[row][col];
    }

    /**just in case method for set grid square*/
    public void setGridSquare(int row, int col, Square square) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Invalid grid square coordinates: (" + row + ", " + col + ")");
        }
        squares[row][col] = square;
    }

    /**just in case method for clear grid*/
    public void clearGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                squares[row][col] = null;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                sb.append(squares[row][col] != null ? squares[row][col].toString() : "null").append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
