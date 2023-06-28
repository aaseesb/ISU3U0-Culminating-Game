import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class level1 extends JFrame implements KeyListener{
  // variables
  boolean buttonPressed;
  int wallY, score, iceX, iceY;
  double gravity, jumpStrength;
  ImageIcon back, spikes, iceBlock, endPoint, iceWall, button, jumpButton;
  Rectangle jumpRect, endBounds, wallBounds, buttonBounds;
  String username;

  // arrays ---- some arrays are created with the length of one for efficiency in creating new levels with multiple of the object
  double[] velocity = new double[4];
  boolean[] left = new boolean[2], right = new boolean[2], onGround = new boolean[2], blockOnGround = new boolean[2], isJump = new boolean[2], isDead = new boolean[2];
  ImageIcon[] ground = new ImageIcon[7], player = new ImageIcon[2];
  Rectangle[] groundBounds = new Rectangle[ground.length], pBounds = new Rectangle[2], spikeBounds = new Rectangle[1], iceBounds = new Rectangle[2];
  int[] posX = new int[2], posY = new int[2], platformX = new int[groundBounds.length], platformY = new int[groundBounds.length];

  // file writer
  FileWriter scoreFile;
  BufferedWriter input;

  // constructor
  public level1() throws FileNotFoundException {
    // user input username to later input to file
    username = JOptionPane.showInputDialog("Enter your username: ");

    try {
      scoreFile = new FileWriter("scores.txt", true);
      input = new BufferedWriter(scoreFile);
    }
    catch (IOException e) {}
    
    // jumping, gravity, and motion
      for (int i = 0; i < 2; i++) {
        left[i] = right[i] = isJump[i] = false;
        velocity[i] = 0;
        velocity[i+2] = 0;
      }
      jumpStrength = 15;
      gravity = 2.5;
    
    // dying
    isDead[0] = false;
    isDead[1] = false;

    // background
    back = new ImageIcon("back.jpg");

    // players
      // coordinates
      posX[0] = 10;
      posX[1] = 60;
      posY[0] = posY[1] = 494;
    for (int i = 0; i < pBounds.length; i++) {
      this.player[i] = new ImageIcon("p" + i + ".png");
      this.pBounds[i] = new Rectangle();
      pBounds[i].setSize(player[i].getIconWidth()-16, player[i].getIconHeight());
    }

    // platforms
      // coordinates
      platformX[0] = 0; platformY[0] = 535;
      platformX[1] = 150; platformY[1] = 470;
      platformX[2] = 320; platformY[2] = 410;
      platformX[3] = 150; platformY[3] = 290;
      platformX[4] = 10; platformY[4] = 410;
      platformX[5] = 400; platformY[5] = 190;
      platformX[6] = 50; platformY[6] = 200;
    this.ground[0] = new ImageIcon("ground.png");
    this.ground[1] = new ImageIcon("platform1.png");
    this.ground[2] = new ImageIcon("platform2.png");
    this.ground[3] = new ImageIcon("platform2.png");
    this.ground[4] = new ImageIcon("platform1.png");
    this.ground[5] = new ImageIcon("platform2.png");
    this.ground[6] = new ImageIcon("platform1.png");
    for (int i = 0; i < groundBounds.length; i++) {
      this.groundBounds[i] = new Rectangle();
      groundBounds[i].setSize(ground[i].getIconWidth(), ground[i].getIconHeight()-20);
    }

    // spikes
    this.spikes = new ImageIcon("spikes.png");
    this.spikeBounds[0] = new Rectangle();
    spikeBounds[0].setSize(spikes.getIconWidth()-5, spikes.getIconHeight()-5);

    // ice
    this.iceBlock = new ImageIcon("iceBlock.png");
    for (int i = 0; i < 1; i++) {
      iceBounds[i] = new Rectangle();
      iceBounds[i].setSize(iceBlock.getIconWidth(), iceBlock.getIconHeight());
    }
    iceX = 90; iceY = 150;
    this.iceWall = new ImageIcon("iceWall.png");
    wallBounds = new Rectangle();
    wallBounds.setSize(iceWall.getIconWidth(), iceWall.getIconHeight());
    wallY = 90;

    // power up
    jumpButton = new ImageIcon("button2.png");
    jumpRect = new Rectangle();
    jumpRect.setSize(jumpButton.getIconWidth(), jumpButton.getIconHeight());

    // button
    button = new ImageIcon("button1.png");
    buttonBounds = new Rectangle();
    buttonBounds.setSize(button.getIconWidth(), button.getIconHeight());
    buttonPressed = false;

    // end point/igloo
    this.endPoint = new ImageIcon("igloo.png");
    this.endBounds = new Rectangle();
    endBounds.setSize(endPoint.getIconWidth()-20, endPoint.getIconHeight()-30);

    addKeyListener(this);

    // setup screen
    setSize(600, 600);
    setVisible(true); 
  }


  // main paint method
  public void paint(Graphics g) {
    back.paintIcon(this, g, 0, 0);

    // draw images
    buttons(g);
    elements(g);
    platforms(g);
    players(g);
    drawScore(g);

    // change animation of player based on actions
    for (int i = 0; i < 2; i++) {
      changeAnimation(i);
    }

    // move player
    left();
    right();

    // platform/block
    checkFall();
    // spike/dying/winning
    checkDieWin();
    // check collide with wall and keep on screen
    checkCollide();

    repaint();

    try {
      Thread.sleep(30);
    }
    catch (Exception e) {}
  }

// --------- GRAPHICS
  // score
  public void drawScore(Graphics g) {
    if (!isDead[0] || !isDead[1]) {
      score+=1;
    }
    
    g.setFont(new Font("Courier", Font.PLAIN, 15));
    g.drawString("Score: " + score, 480, 40);
  }
  
  // draw players
  public void players(Graphics g) {
    player[0].paintIcon(this, g, posX[0], posY[0]);
    pBounds[0].setLocation(posX[0]+8, posY[0]);

    player[1].paintIcon(this, g, posX[1], posY[1]);
    pBounds[1].setLocation(posX[1]+8, posY[1]);
  }

  // create platforms and their collision rects
  public void platforms(Graphics g) {
    // platform rectangles
    for (int i = 0; i < groundBounds.length; i++) {
      ground[i].paintIcon(this, g, platformX[i], platformY[i]);
      groundBounds[i].setLocation(platformX[i], platformY[i]);
    }
  }

  // obstacles + igloo
  public void elements(Graphics g) {
    spikes.paintIcon(this, g, 390, 390);
    spikeBounds[0].setLocation(393, 395);

    iceBlock.paintIcon(this, g, iceX, iceY);
    iceBounds[0].setLocation(iceX, iceY);
  
    iceWall.paintIcon(this, g, 400, wallY);
    wallBounds.setLocation(400, wallY);

    endPoint.paintIcon(this, g, 440, 130);
    endBounds.setLocation(450, 160);
  }

  public void buttons(Graphics g) {
    // move wall button
    button.paintIcon(this, g, 320, 275);
    buttonBounds.setLocation(320, 275);
    buttonPressed = false;
    button = new ImageIcon("button1.png");
    for (int i = 0; i < 2; i++) {
      if (pBounds[i].intersects(buttonBounds)) {
        buttonPressed = true;
      }
    }
    if (iceBounds[0].intersects(buttonBounds)) {
      buttonPressed = true;
    }
    if (buttonPressed) {
      if (wallY != 200) {
        wallY+=5;
      }
    }
    else {
      if (wallY != 90) {
        wallY-=5;
      }
    }

    // power up button
    jumpButton.paintIcon(this, g, 475, 395);
    jumpRect.setLocation(475, 395);

    if (pBounds[1].intersects(jumpRect)) {
      jumpButton = new ImageIcon("buttonPressed2.png");
      jumpStrength = 25;

      g.setColor(Color.black);
      g.setFont(new Font("Courier", Font.PLAIN, 10));
      g.drawString("Jump Strength Increased!", 450, 350);
    }
    else {
      jumpButton = new ImageIcon("button2.png");
      jumpStrength = 15;
    }
  }

// --------- DYING/WINNING
public void checkDieWin() {
  // dying
  for (int player = 0; player < 2; player++) {
    for (int i = 0; i < spikeBounds.length; i++) {
      if (pBounds[player].intersects(spikeBounds[i])) {
        isDead[player] = true;

        endScreen.win = false;
        endScreen.character = player;
        dispose();
        
        try {
          new endScreen();
        }
        catch (FileNotFoundException e) {
          System.out.println("Error opening file"); 
        }
      }
    }
  }

  // winning level
    if (pBounds[0].intersects(endBounds) && pBounds[1].intersects(endBounds)) {
      // append score to file
      try {
        Thread.sleep(1000);
        if (username.length() == 0) {
          input.append(score + ", null\n");
        }
        else {
          input.append(score + ", " + username + "\n");
        }
        input.flush();
        input.close();
        scoreFile.close();

        endScreen.win = true;
        dispose();
        new endScreen();
      }
      catch (Exception e) {}
    }
}

// --------- COLLISION (stopping characters, not powerups endpoint etc)
  // checking for fall
  public void checkFall() {
    for (int i = 0; i < 2; i++) {
      // check if player on ground
      if (checkOnGround(i)) {
        velocity[i] = 0;
        isJump[i] = false;
      }
      else {
        fall(i);
      }
      // check if block on ground
      if (checkBlockOnGround(0)) {
        velocity[3] = 0;
      }
      else {
        fall(i+2);
      }
    }
  }

  // checking for collision
  public boolean checkOnGround(int player) {
    onGround[player] = false;

    int bottom = (int)(posY[player] + pBounds[player].getHeight()); // bottom side of player rect
    
    //player on ground
    for (int i = 0; i < groundBounds.length; i++ ) {
      if (pBounds[player].intersects(groundBounds[i])) {
        // on platform
        if (bottom > platformY[i] && bottom <= platformY[i] + 25) { // must only happen if bottom of player touches top of platform
          onGround[player] = true;
          posY[player] = (int)(platformY[i] - 39);
        }
        //on ground
        else if (i == 0) {
          onGround[player] = true;
          posY[player] = (int)(platformY[i] - 39);
        }

        /* 
        //touching sides
        else if (posX[player] < platformX[i]) {
          posX[player] = (int)(platformX[i] - pBounds[player].getWidth());
        }*/
      }
    }

    // player on ice 
    if (pBounds[player].intersects(iceBounds[0])) {
      // on block
      if (bottom > iceY && bottom <= iceY + 25) { // must only happen if bottom of player touches top of platform
        onGround[player] = true;
        posY[player] = (iceY - 39);
      }
      else {
        if (posX[player] > (iceX + 10)) {
          iceX-=5;
        }
        else if (posX[player] < (iceX + 10)) {
          iceX+=5;
        }
      }
    }

    return onGround[player];
  }

  // block
  public boolean checkBlockOnGround(int block) {
    blockOnGround[block] = false;
    int bottom = (int)(iceY + iceBounds[block].getHeight());
    
    // block on platforms
    for (int i = 0; i < groundBounds.length; i++ ) {
      if (iceBounds[block].intersects(groundBounds[i])) {
        // on platform
        if (bottom >= platformY[i] && bottom <= platformY[i] + 30) {
          blockOnGround[block] = true;
          iceY = (int)(platformY[i] - 34);
        }
        //on ground
        else if (i == 0) {
          blockOnGround[block] = true;
          iceY = (int)(platformY[i] - 34);
        }
      }
    }

    return(blockOnGround[block]);
  }
  
  // keep players on screen and allow obstacles to stop them
  public void checkCollide() {
    for (int i = 0; i < 2; i++) {
      if (posX[i] < 0) {
        posX[i] = 0;
      }
      else if (posX[i] > 565) {
        posX[i] = 565;
      } 
      if (posY[i] < 0) {
        posY[i] = 0;
      }
      if (pBounds[i].intersects(wallBounds)) {
        if (posX[i] < 395 && right[i]) {
          posX[i] = 375;
        }
        if (posX[i] > 395) {
          posX[i] = 415;
        }
      }
    }
  }

  
// -------- ANIMATON
  public void changeAnimation(int i) {
    // move right
    if (right[i]) {
      if (isJump[i]) {
        player[i] = new ImageIcon("p" + i + "JumpRight.png");
      }
      else {
        player[i] = new ImageIcon("p" + i + ".png");
      }
    }
    // move left
    else if (left[i]) {
      if (isJump[i]) {
        player[i] = new ImageIcon("p" + i + "JumpLeft.png");
      }
      else {
        player[i] = new ImageIcon("p" + i + "Left.png");
      }
    }
    if (isDead[i]) {
      player[i] = new ImageIcon("p" + i + "Dead.png");
    }
    if (buttonPressed) {
      button = new ImageIcon("buttonPressed1.png");
    }
  }



// --------- ACTIONS
  // falling
  public void fall(int i) {
    //player
    if (i < 2) {
      posY[i]+=velocity[i];
      velocity[i]+=gravity;
    }
    //block
    else {
      iceY+=velocity[i];
      velocity[i]+=gravity;
    }


  }
  public void jump(int player) {
    if (checkOnGround(player)) {
      isJump[player] = true;
      velocity[player] = -jumpStrength;
      fall(player);
    }
  }
  public void left() {
    for (int i = 0; i < 2; i++) {
      if (left[i]) {
        posX[i]-=5;
      }
    }
  }
  public void right() {
    for (int i = 0; i < 2; i++) {
      if (right[i]) {
        posX[i]+=5;
      }
    }
  }


  // Key Listener
  public void keyPressed(KeyEvent e) {
    // player 1
    switch(e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        left[0] = true;
        break;
      case KeyEvent.VK_RIGHT:
        right[0] = true;
        break;
      case KeyEvent.VK_UP:
        jump(0);
        break;
    }
  }    
  
  public void keyReleased (KeyEvent e) { 
    // player 1
    switch(e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        left[0] = false;
        break;
      case KeyEvent.VK_RIGHT:
        right[0] = false;
        break;
    }
    // player 2
    switch(e.getKeyChar()) {
      case 'a':
        left[1] = false;
        break;
      case 'd':
        right[1] = false;
        break;
    }
  }    
  
  public void keyTyped (KeyEvent e) { 
    // player 2
    switch(e.getKeyChar()) {
      case 'a':
        left[1] = true;
        break;
      case 'd':
        right[1] = true;
        break;
      case 'w':
        jump(1);
        break;
    }
  }
} 