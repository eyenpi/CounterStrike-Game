package CounterStrike;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class OnlineGame extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int height = 15, weight = 25;
	private JLabel[][] tiles = new JLabel[15][25];

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Client c = new Client();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Server is not ready");
			return;
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OnlineGame frame = new OnlineGame();
					frame.setExtendedState(MAXIMIZED_BOTH);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public OnlineGame() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Client.close();
				dispose();
			}
		});

		// use Game readImages method to load icons
		Game.readImages();
		addKeyListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(height, weight, 2, 2));
		setContentPane(contentPane);

		Font f = new Font("Tahoma", Font.PLAIN, 20);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < weight; j++) {
				JLabel tile = new JLabel();
				contentPane.add(tile);
				tiles[i][j] = tile;
				tiles[i][j].setHorizontalTextPosition(JLabel.CENTER);
				tiles[i][j].setVerticalTextPosition(JLabel.CENTER);
				tiles[i][j].setFont(f);
				tiles[i][j].setForeground(Color.RED);
			}
		}
		updateBoard();
	}

	/**
	 * its a method that been called every 50 milliseconds (20 fps). It updates
	 * board from board variable that get updates every 50 milliseconds
	 */
	public void updateBoard() {
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < weight; j++) {
						tiles[i][j].setText("");
					}
				}

				for (int i = 0; i < height; i++) {
					for (int j = 0; j < weight; j++) {
						if (Client.board[i][j] == 0) {
							tiles[i][j].setIcon(Game.main);
						}
						if (Client.board[i][j] == 1) {
							tiles[i][j].setIcon(Game.mane);
						}
						if (Client.board[i][j] == 2) {

							tiles[i][j].setText(Integer.toString(Client.player2.getHp()));
							if (Client.player2.getFace() == 6)
								tiles[i][j].setIcon(Game.pright);
							else if (Client.player2.getFace() == 2)
								tiles[i][j].setIcon(Game.pdown);
							else if (Client.player2.getFace() == 4)
								tiles[i][j].setIcon(Game.pleft);
							else if (Client.player2.getFace() == 8)
								tiles[i][j].setIcon(Game.pup);

						}
						if (Client.board[i][j] == 3) {
							tiles[i][j].setText(Integer.toString(Client.player3.getHp()));
							if (Client.player3.getFace() == 6)
								tiles[i][j].setIcon(Game.eright);
							else if (Client.player3.getFace() == 2)
								tiles[i][j].setIcon(Game.edown);
							else if (Client.player3.getFace() == 4)
								tiles[i][j].setIcon(Game.eleft);
							else if (Client.player3.getFace() == 8)
								tiles[i][j].setIcon(Game.eup);
						}
						if (Client.board[i][j] == 4) {
							tiles[i][j].setIcon(Game.life);
						}
						if (Client.board[i][j] == 10) {
							tiles[i][j].setIcon(Game.dead);
						}

					}

				}

			}
		}, 0, 50);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * if keys pressed a request to server will be sent and server updates its board
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP)
			Client.send("Move%%up");

		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			Client.send("Move%%down");

		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			Client.send("Move%%left");

		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			Client.send("Move%%right");
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			Client.send("Shoot%%");
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
