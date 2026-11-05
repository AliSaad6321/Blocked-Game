package view;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    public GameFrame(GamePanel gamePanel) {
        setTitle("BLOCKED.exe — The Block Burst Challenge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
    }
}