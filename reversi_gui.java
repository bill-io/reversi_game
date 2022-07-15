/**
 * EDA132
 * @author Andreas Hansson, Fredrik Österberg
 */

package reversi;

import java.awt.EventQueue;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class reversi_gui {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    reversi_gui window = new reversi_gui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Create the application.
     */
    public reversi_gui() {
        initialize();
        initialState();
        b = new board(gamestate);
        paintboard();
        suggestMoves();//SOS
    }

    public void initialState() {

        gamestate = new int[8][8];
        gamestate[3][3] = 1;
        gamestate[4][4] = 1;
        gamestate[3][4] = 2;
        gamestate[4][3] = 2;
    }

    int[][] gamestate;
    board b;

    public void move(int x, int y) {

        if (b.getValidMoves(2).size() == 0) {
            initialState();
            paintboard();
            return;
        }

        if (b.gamestate[x][y] != 0)
            return;

        if (!b.makeMove(x, y, 2))
            return;

        lblPlayer.setText("Player: " + Integer.toString(b.calculateValue(2)));
        lblNewLabel.setText("Computer: " + Integer.toString(b.calculateValue(1)));

        paintboard();

        reversi.startTime = System.currentTimeMillis();
        reversi.maxTime = Integer.parseInt(textField_1.getText()) * 1000;
        reversi.cutOff = false;

        int mx = Integer.parseInt(textField.getText());

        int[][] incpy = board.cloneGrid(b.gamestate);

        for (int i = 1; i <= mx && !reversi.cutOff; i++) {

            reversi.maxLevel = i;
            reversi.minimax(true, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, new board(incpy));

            if (!reversi.cutOff) {
                b = reversi.best;
            } else {
                System.out.println("Max depth completed: " + (i - 1));
            }
        }

        if (!reversi.cutOff)
            System.out.println("Max depth completed: " + mx + " (full)");

        lblPlayer.setText("Player: " + Integer.toString(b.calculateValue(2)));
        lblNewLabel.setText("Computer: " + Integer.toString(b.calculateValue(1)));

        paintboard();

        suggestMoves();
    }

    public void suggestMoves() {
        ArrayList<board> moves = b.getValidMoves(2);

        for (board i : moves) {
            grid[i.movedX][i.movedY].setBackground(new Color(50, 160, 70));
        }
    }

    void paintboard() {
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++) {
                setColor(x, y, b.gamestate[x][y]);
            }
    }

    JLabel lblPlayer, lblNewLabel;
    // JTextField textField, textField_1;

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.getContentPane().setForeground(Color.BLUE);
        frame.setBounds(100, 100, 600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        lblNewLabel = new JLabel("Computer: 2");
        lblNewLabel.setForeground(Color.RED);
        lblNewLabel.setBounds(490, 105, 104, 16);
        frame.getContentPane().add(lblNewLabel);

        lblPlayer = new JLabel("Player: 2");
        lblPlayer.setBounds(490, 133, 61, 16);
        frame.getContentPane().add(lblPlayer);

        textField = new JTextField();
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setText("10");
        textField.setBounds(490, 214, 50, 26);
        frame.getContentPane().add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setHorizontalAlignment(SwingConstants.CENTER);
        textField_1.setText("1");
        textField_1.setBounds(490, 267, 53, 26);
        frame.getContentPane().add(textField_1);
        textField_1.setColumns(10);

        JLabel lblMaxDepth = new JLabel("Max depth");
        lblMaxDepth.setBounds(491, 200, 76, 16);
        frame.getContentPane().add(lblMaxDepth);

        JLabel lblMaxTimes = new JLabel("Max time (s)");
        lblMaxTimes.setBounds(490, 252, 77, 16);
        frame.getContentPane().add(lblMaxTimes);

        grid = new JButton[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[j][i] = new JButton("");
                grid[j][i].setSize(new Dimension(50, 50));
                grid[j][i].setBounds(30 + 53 * j, 30 + 53 * i, 50, 50);
                grid[j][i].setOpaque(true);
                grid[j][i].setBorderPainted(false);
                grid[j][i].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        clickedget((JButton) e.getSource());
                        e.consume();
                    }
                });
                frame.getContentPane().add(grid[j][i]);
            }
        }

    }

    public void clickedget(JButton j) {
        Rectangle r = j.getBounds();
        int x = (r.x - 30) / 53;
        int y = (r.y - 30) / 53;
        System.out.println("Pressed (" + x + " , " + y + ")");
        move(x, y);
    }

    public void setColor(int x, int y, int c) {
        switch (c) {
            case 0:
                grid[x][y].setBackground(new Color(50, 110, 70));
                break;
            case 1:
                grid[x][y].setBackground(Color.BLACK);
                break;
            case 2:
                grid[x][y].setBackground(Color.WHITE);
                break;
        }

    }

    JButton[][] grid;
    private JTextField textField;
    private JTextField textField_1;
}