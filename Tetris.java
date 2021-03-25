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

	/*Following the Information Hiding principle presented by David Parnas, all of the following variables
	  are made private to avoid any unintentional coupling caused by future extensions of this game.*/

	private static final long serialVersionUID = -8715353373678321308L;

	/*3 dimensional array to hold the 7 Tetris pieces. Each piece can be referenced by an index ranging from 0 to 6.
	  There exists the following pieces: I, J, L, O, S, T, Z. They are placed in this array alphabetically.
	  These are the shapes that will be falling from top to bottom in the game.
	  Using such an array simplifies the accessibility of each piece throughout the program.*/
	private final Point[][][] Tetraminos = {
			{	//I
				{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
				{new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
				{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
				{new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
			},

			{	//J
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

			{	//O
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

	/*Again, using an array to hold the colors of the pieces simplifies the accessibility of the colors
	  throughout the program, as they can be referenced by an index ranging from 0 to 6.
	  Each of these colors is given the same index number as the shape that they are assigned to.
	  This means that I's will be cyan, J's will be blue, L's will be orange, O's will be yellow,
	  S's will be green, T's will be pink, and finally Z's will be red.*/
	private final Color[] tetraminoColors = {Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red};

	private Point pieceOrigin; //Holds the initial place of each piece. This is the location where each piece will start falling from.
	private int currentPiece; //Indexes the current falling piece.
	private int rotation; //Represents the current angle of the current falling piece.
	private ArrayList<Integer> nextPieces = new ArrayList<Integer>(); //Acts as a queue in which all of the following pieces are placed in order.
	private int score; //Holds the score as a long value in case the player's score increases over the maximum that an integer can hold.
	private Color[][] well; //Holds the colors of the well in an array for easy access (will also be used to check for gaps).

	//Creates a black and gray border around the well and initializes a new falling piece.
	private void init(){
		well = new Color[12][24];
		for(int i=0;i<12;i++){
			for(int j=0;j<23;j++){
				if(i == 0 || i == 11 || j == 22) well[i][j] = Color.GRAY;
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
		pieceOrigin = new Point(5, 2);
		rotation = 0;
		if(nextPieces.isEmpty()){
			Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
			Collections.shuffle(nextPieces);
		}
		currentPiece = nextPieces.get(0);
		nextPieces.remove(0);
	}

	/*Merely a collision test for falling pieces and the well to be used throughout the program.
	  It it made private as it is only used by other methods and not by any external actors.*/
	private boolean collidesAt(int x, int y, int rotation){
		for (Point p : Tetraminos[currentPiece][rotation]){
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
		if(!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) rotation = newRotation;
		repaint();
	}

	/*Moves the current falling piece right or left simply by changing its coordinates.
	  With every change, the piece must be redrawn.
	  This functionality will be accessible to the player through the right and left arrow keys.*/
	public void move(int i){
		if(!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) pieceOrigin.x += i;
		repaint();
	}

	/*Drops the current falling piece if possible, or fixes it to well when it reaches the bottom and collides.
	  With every change, the piece must be redrawn.*/
	public void dropDown(){
		if(!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) pieceOrigin.y += 1;
		else fixToWell();
		repaint();
	}

	/*Makes the current falling piece part of the well and therefore available for collision testing against other falling pieces.
	  Also, when a new piece is fixed to the well, the program checks if there are any rows to be cleared, and clears available rows.
	  Lastly, a new piece is created and starts falling as soon as the current falling piece is fixed.*/
	public void fixToWell(){
		for(Point p : Tetraminos[currentPiece][rotation])  well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
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
		g.setColor(tetraminoColors[currentPiece]);
		for (Point p : Tetraminos[currentPiece][rotation]) g.fillRect((p.x + pieceOrigin.x) * 26, (p.y + pieceOrigin.y) * 26, 25, 25);
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

	//Main method that starts the game.
	public static void main(String[] args){

		/*JFrame is used to create a window that appears on the player's screen, labeled "Tetris" with given dimensions.
		  To satisfy the Java compiler, a so-called "close operation" has to be set,
		  which simply is set to terminate the program when the user closes that window.*/
		JFrame f = new JFrame("Tetris");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(12*26+10, 26*23+25);
		f.setVisible(true);

		/*For the player to actually be able to play the game, an instance of the game must be created,
		  calling the initializer method of that instance, and then adding it to the JFrame window.*/
		final Tetris game = new Tetris();
		game.init();
		f.add(game);

		//This is the one source of input in which the player interacts and stimulates the game.
		f.addKeyListener(new KeyListener(){

			/*Ensures that nothing occurs when a key is pressed while a modifier (shift) is also pressed.
			  This empty keyTyped method along with the following keyPressed method ensure that the same event takes place
			  whether the player presses on one of the arrow keys individually, or also while holding down a modifier key.*/
			public void keyTyped(KeyEvent e){}

			/*The following allows the player to rotate and move the falling pieces, one at a time,
			  as well as dropping them to the bottom. These functionalities are possible using the arrow keys and the space bar.*/
			public void keyPressed(KeyEvent e){
				switch (e.getKeyCode()){
				case KeyEvent.VK_UP:
					game.rotate(-1);
					break;
				case KeyEvent.VK_DOWN:
					game.rotate(+1);
					break;
				case KeyEvent.VK_LEFT:
					game.move(-1);
					break;
				case KeyEvent.VK_RIGHT:
					game.move(+1);
					break;
				case KeyEvent.VK_SPACE:
					game.dropDown();
					game.score += 1;
					break;
				}
			}

			/*Ensures that nothing occurs when a key is released.
			  This is required as in Tetris it is best to have the game respond to an action upon pressing a button.
			  If actions were to occur at the release of a button, an unwanted delay will be simulated.*/
			public void keyReleased(KeyEvent e){}
		});

		//Falling pieces are simulated using parallel threads which run once per second.
		new Thread(){
			@Override public void run(){
				while(true){ //Always running, with no terminating condition (no game over).
					try{
						Thread.sleep(1000); //This value can be modified to make the game easier or harder.
						game.dropDown();
					} catch(InterruptedException e){}
				}
			}
		}.start();
	}
}
