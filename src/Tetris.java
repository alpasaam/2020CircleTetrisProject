import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JPanel{
  /*Variable*/
	/*Array for all the pieces*/
	private final Point[][][] Tetraminos = {
			{	//Line
				{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
				{new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
				{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
				{new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
			},

			{	//Mirrored L
				{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
				{new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
				{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
				{new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)}
			},

			{	//L
				{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
				{new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
				{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
				{new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)}
			},

			{	//Square
				{new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
				{new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
				{new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
				{new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}
			},

			{	//S
				{new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
				{new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
				{new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
				{new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
			},

			{	//T
				{new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)},
				{new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
				{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
				{new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2)}
			},

			{	//Z
				{new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
				{new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)},
				{new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
				{new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)}
			}
	};

	/*This array will hold all the colors of the pieces*/
	private final Color[] tetraminoColors = {Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red};

	private Point oriPiece;
	private int nowPiece;
	private int rotation; //stores the rotation
	private ArrayList<Integer> nextPieces = new ArrayList<Integer>(); //list of upcoming pieces
	public int score; //hold the score
	private Color[][] well; //hold the color

	//makes the border.
	public void init(){
		well = new Color[12][24];
		for(int i=0;i<12;i++){
			for(int j=0;j<23;j++){
				if(i == 0 || i == 11 || j == 22) well[i][j] = Color.GRAY; //single line of if statment
				else well[i][j] = Color.BLACK;
			}
		}
		newPiece();
	}

	/*By setting the initial position, all new pieces will be placed at that point first before falling.
	  Whenever the queue is empty, new pieces are added to the queue with an angle of 0 degrees.
	  Those pieces are shuffled upon placement to ensure randomness.
	  As a piece is placed on the screen, it is removed from that queue, which eventually will make it empty.*/
	public void newPiece(){
		oriPiece = new Point(5, 2);
		rotation = 0;
		if(nextPieces.isEmpty()){
			Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
			Collections.shuffle(nextPieces);
		}
		nowPiece = nextPieces.get(0);
		nextPieces.remove(0);
	}

	/*Merely a collision test for falling pieces and the well to be used throughout the program.
	  It it made private as it is only used by other methods and not by any external actors.*/
	private boolean collidesAt(int x, int y, int rotation){
		for (Point p : Tetraminos[nowPiece][rotation]){
			if(well[p.x + x][p.y + y] != Color.BLACK) return true;
		}
		return false;
	}

	/*Rotates the current falling piece clockwise or counterclockwise using 4 integers
	  that represent the 4 possible angles. With every change, the piece must be redrawn.
	  This functionality will be accessible to the player through the up and down arrow keys.*/
	public void rotate(int i){
		int newRotation = (rotation + i) % 4;
		if(newRotation < 0) newRotation = 3;
		if(!collidesAt(oriPiece.x, oriPiece.y, newRotation)) rotation = newRotation;
		repaint();
	}

	/*Moves the current falling piece right or left simply by changing its coordinates.
	  With every change, the piece must be redrawn.
	  This functionality will be accessible to the player through the right and left arrow keys.*/
	public void move(int i){
		if(!collidesAt(oriPiece.x + i, oriPiece.y, rotation)) oriPiece.x += i;
		repaint();
	}

	/*Drops the current falling piece if possible, or fixes it to well when it reaches the bottom and collides.
	  With every change, the piece must be redrawn.*/
	public void dropDown(){
		if(!collidesAt(oriPiece.x, oriPiece.y + 1, rotation)) oriPiece.y += 1;
		else stayOnBoard();
		repaint();
	}

	/*fix the piece to the board*/
	public void stayOnBoard(){
		for(Point p : Tetraminos[nowPiece][rotation])  well[oriPiece.x + p.x][oriPiece.y + p.y] = tetraminoColors[nowPiece];
		clearRows();
		newPiece();
	}

	/*Deletes the row at the very bottom whenever called by other methods.
	  This happens through a process where all of the rows above move down by one row, or more if possible.*/
	public void deleteRow(int row){
		for(int j=row-1;j>0;j--){
			for(int i=1;i<11;i++) well[i][j+1] = well[i][j];
		}
	}

	/*Deletes completed rows from the field and awards score according to the number of simultaneously cleared rows.
	  It simply checks for gaps in the bottom row, and if there exists none then that row is deleted.
	  That process is then repeated for the rows above until a row with gaps is found.*/
	public void clearRows(){
		boolean gap;
		int numClears = 0;

		for(int j=21;j>0;j--){
			gap = false;
			for(int i=1;i<11;i++){
				if(well[i][j] == Color.BLACK){
					gap = true;
					break;
				}
			}
			if(!gap){
				deleteRow(j);
				j += 1;
				numClears += 1;
			}
		}

		switch(numClears){
		case 1:
			score += 100;
			break;
		case 2:
			score += 300;
			break;
		case 3:
			score += 500;
			break;
		case 4:
			score += 800;
			break;
		}
	}

	//Being passed a piece, this method draws it and gives the piece its corresponding color.
	private void drawPiece(Graphics g){
		g.setColor(tetraminoColors[nowPiece]);
		for (Point p : Tetraminos[nowPiece][rotation]) g.fillRect((p.x + oriPiece.x) * 26, (p.y + oriPiece.y) * 26, 25, 25);
	}

	//Paints the well and displays the score in white text while drawing the current falling pieces.
	@Override
	public void paintComponent(Graphics g){
		g.fillRect(0, 0, 26*12, 26*23);
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 23; j++) {
				g.setColor(well[i][j]);
				g.fillRect(26*i, 26*j, 25, 25);
			}
		}
		g.setColor(Color.WHITE);
		g.drawString("" + score, 19*12, 25);
		drawPiece(g);
	}
}
