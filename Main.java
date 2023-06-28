// Aasees Badesha
// 05/15/23
// 2D Platform Game

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Main extends JFrame implements ActionListener {
  JLabel back = new JLabel(), title = new JLabel();
  JButton levels, controls;
  int gameState;
  public static boolean playGame;

  public Main() {
    // background
    back.setIcon(new ImageIcon("back.jpg"));
    back.setBounds(0, 0, 600, 600);
    back.setLayout(null);

    // buttons
    this.levels = new JButton("Levels");
    levels.setBounds(30, 100, 160, 50);
    this.controls = new JButton("Instructions");
    controls.setBounds(210, 100, 160, 50);

    // title
    this.title = new JLabel("Penguin Run");
    title.setFont(new Font("Impact", Font.BOLD, 40));
    title.setBounds(30, 40, 400, 50);

    // add to screen
    add(back);
    back.add(title);
    back.add(levels);
    back.add(controls);

    // check for click
    levels.addActionListener(this);
    controls.addActionListener(this);

    // gui
    setSize(600, 600);
    setVisible(true);
  }
  

  // actions
  public void actionPerformed(ActionEvent a) {
    if (a.getSource()==levels) {
      dispose();

      new levels();
    }
    if (a.getSource()==controls) {
      JOptionPane.showMessageDialog (back,
        "The up, left, and right arrow keys are used to move the black penguin.\n" +
        "The W, A, and D keys are used to move the pink penguin\n" + 
        "Pink and black buttons can only be pushed by their respective colour penguins (e.g. only the pink penguin can push the pink button).\n" + 
        "Blue objects and buttons can be pushed by any character.\n" +
        "Touching purple objects will kill the user.\n" +
        "The objective is to get both penguins to the igloo near the top of the screen.\n" +
        "Try to finish the game in the least amount of time as possible—the LOWER the score, the better.\n" +
        "Have fun!", "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }
  }


  public static void main(String[] args) {
    new Main();
  }
}