import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class endScreen extends JFrame implements ActionListener {
    JButton playAgain, mainMenu, nextLevel, searchWin;
    JLabel highScoreLabel, scoreLabel, deadLabel, gameOver, title;
    int currentScore, highScore, length;
    String line;
    ArrayList<Integer> scoreList = new ArrayList<Integer>();
    ArrayList<String> userList = new ArrayList<String>();
    public static int character;
    public static boolean win;

    public endScreen() throws FileNotFoundException {
      Font labelFont = new Font("Courier", Font.PLAIN, 25);

      // create buttons
      mainMenu = new JButton("Main Menu");
      playAgain = new JButton("Replay Level");
      nextLevel = new JButton("Next Level (N/A)");
      searchWin = new JButton("Leaderboard");

      // disable next level as a second level has not been made
      nextLevel.setEnabled(false);

      // prepare file to be read
      FileReader readFile = new FileReader("scores.txt");
      BufferedReader reader = new BufferedReader(readFile);
      length = -1;

      // winning screen
;      if (win) {
        try {
          // input scores from file to list and find current score
          while (true) {
            length++;
            line = reader.readLine();
            if (line == null) {
              break;
            }
            else {
              String bits[] = line.split(", ");
              scoreList.add(Integer.parseInt(bits[0]));
              userList.add(bits[1]);
            }
          }

          currentScore = scoreList.get(length-1);
          
          
          // bubble sort list and find high score
          for (int b = 0; b < length; b++) {
            for (int c = 0; c < (length - 1); c++) {
              if (scoreList.get(c) > scoreList.get(c+1)) {
                int temp = scoreList.get(c);
                scoreList.set(c, scoreList.get(c+1));
                scoreList.set(c+1, temp);

                String tempN = userList.get(c);
                userList.set(c, userList.get(c+1));
                userList.set(c+1, tempN);
              }
            }
          }

          highScore = scoreList.get(0);

          reader.close();
        }
        catch (IOException e) {
          System.out.println("Error reading file");
        }

        // create labels
        title = new JLabel("You Win!");
        scoreLabel = new JLabel("Your score: " + currentScore);
        highScoreLabel = new JLabel("Highest score: " + highScore);

        // set label fonts
        title.setFont(new Font("Impact", Font.PLAIN, 40));
        highScoreLabel.setFont(labelFont);
        scoreLabel.setFont(labelFont);

        // set label and button locations
        title.setBounds(230, 100, 400, 50);
        highScoreLabel.setBounds(180, 200, 400, 100);
        scoreLabel.setBounds(190, 250, 400, 100);

        nextLevel.setBounds(135, 380, 160, 50);
        playAgain.setBounds(305, 380, 160, 50);
        mainMenu.setBounds(135, 440, 160, 50);
        searchWin.setBounds(305, 440, 160, 50);

        // add everything
        add(scoreLabel);
        add(highScoreLabel);
        add(nextLevel);
        add(searchWin);
        add(title);
      }

      // losing screen
      else {
        gameOver = new JLabel();
        gameOver.setIcon(new ImageIcon("Game Over.png"));

        deadLabel = new JLabel();
        deadLabel.setIcon(new ImageIcon("p" + character + "Dead.png"));

        gameOver.setBounds(60, 20, 500, 250);
        deadLabel.setBounds(85, 200, 500, 200);
        playAgain.setBounds(135, 400, 160, 50);
        mainMenu.setBounds(305, 400, 160, 50);

        add(gameOver);
        add(deadLabel);
      }
      

      // add buttons and actionlistener
      add(playAgain);
      add(mainMenu);

      playAgain.addActionListener(this);
      mainMenu.addActionListener(this);
      searchWin.addActionListener(this);

      // gui
      setLayout(null);
      getContentPane().setBackground(Color.pink);
      setSize(600, 600);
      setVisible(true);
    }

    public void leaderboard() {
      // title of leaderboard chart
      String leaderboardText = "User   |   Score";
      
      //input each score and name into leaderboard
      for (int i = 0; i < scoreList.size(); i++) {
        leaderboardText+= "\n" + userList.get(i) + " | " + scoreList.get(i);
      }

      leaderboardText+="\n\nEnter username to search for score: (if you do not want to search, type 'n')";

      //output in joptionpane and get user input on search
      String searchUser = JOptionPane.showInputDialog(null, leaderboardText, "Leaderboard", JOptionPane.INFORMATION_MESSAGE);


      // search for username from searchuser
      boolean found = false;
      int count = 0;
    
      // linear search
      if (!searchUser.equals("n")) {
        for (int i = 0; i < userList.size(); i++) {
          if (userList.get(i).equals(searchUser)) {
            count++;
            JOptionPane.showMessageDialog(null, count + ".\nUser: " + userList.get(i) + "\nScore: " + scoreList.get(i), "User search", JOptionPane.INFORMATION_MESSAGE);
            found = true;
          }
        }
        if (!found) {
          System.out.println("User not found");
        }
      }



    }

    // actions
  public void actionPerformed(ActionEvent a) {
    if (a.getSource()==playAgain) {
      try {
        dispose();
        new level1();
      }
      catch (Exception e) {}
    }
    if (a.getSource()==mainMenu) {
      dispose();
      new Main();
    }
    if (a.getSource()==searchWin) {
      leaderboard();
    }
  }
}