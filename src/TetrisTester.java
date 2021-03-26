import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TetrisTester {
  public static void main(String[] args){
    JFrame f = new JFrame("Tetris");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
    f.setVisible(true);

    final Tetris game = new Tetris();
    game.init();
    f.add(game);

    //source of input
    f.addKeyListener(new KeyListener(){

      public void keyTyped(KeyEvent e){}

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

      /*Makes sure nothing happens when the player releases the keys*/
      public void keyReleased(KeyEvent e){}
    });

    //Falling pieces are run using parallel threads which run once per second.
    new Thread(){
      @Override public void run(){
        while(true){ //Always running, with no terminating condition (no game over).
          try{
            Thread.sleep(1000); //Difficulty of the game
            game.dropDown();
          } catch(InterruptedException e){}
        }
      }
    }.start();
  }
}
