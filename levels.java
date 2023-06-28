import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class levels extends JFrame implements ActionListener {
    JButton level1;

    public levels() {
        // create buttons
        this.level1 = new JButton("Level 1");
        level1.setBounds(220, 100, 160, 50);

        // add buttons
        add(level1);

        level1.addActionListener(this);

        // gui
        setLayout(null);
        getContentPane().setBackground(Color.pink);
        setSize(600, 600);
        setVisible(true);
    }

    // actions
  public void actionPerformed(ActionEvent a) {
    if (a.getSource()==level1) {

      try {
        dispose();
        new level1();
      }
      catch (Exception e) {
        System.out.println("Error opening file");
      }
    }
  }
}