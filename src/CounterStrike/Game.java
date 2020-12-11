package CounterStrike;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int height = 15, weight = 25;
	private JLabel[][] tiles = new JLabel[height][weight];

	// 0 for main cells, 1 for mane, 2 for player, 3 for robots, 4 for extra life
	private int[][] board = new int[height][weight];

	public static ImageIcon pleft, pright, pup, pdown, eleft, eright, eup, edown, main, mane, life, dead, shot;
	private Player player;
	private int robotnum = 3;
	private Player[] robots;
	private long starttime, lastshoot, reloadtime;
	private boolean firstshoot = true;
	private Timer t = new Timer();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Game frame = new Game();
					frame.setExtendedState(MAXIMIZED_BOTH);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Game() {
		starttime = System.currentTimeMillis();
		readImages();
		addKeyListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setBounds(100, 100, 900, 500);
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(height, weight, 2, 2));
		setContentPane(contentPane);

		// create the main cells of game
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
				if (i == 0 || i == height - 1 || j == 0 || j == weight - 1) {
					tiles[i][j].setIcon(mane);
					board[i][j] = 1;
				} else {
					tiles[i][j].setIcon(main);
				}
			}
		}
		putPlayer();
		putRandomMane();
		putRandomRobots();
		moveRobots();
		spawnLife();
	}

	/**
	 * create ImageIcons for JLabels
	 */
	public static void readImages() {
		pleft = new ImageIcon(
				new ImageIcon("Images/pleft.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		pright = new ImageIcon(
				new ImageIcon("Images/pright.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		pup = new ImageIcon(new ImageIcon("Images/pup.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		pdown = new ImageIcon(
				new ImageIcon("Images/pdown.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		eleft = new ImageIcon(
				new ImageIcon("Images/eleft.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		eright = new ImageIcon(
				new ImageIcon("Images/eright.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		eup = new ImageIcon(new ImageIcon("Images/eup.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		edown = new ImageIcon(
				new ImageIcon("Images/edown.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		main = new ImageIcon(new ImageIcon("Images/main.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		mane = new ImageIcon(new ImageIcon("Images/mane.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		life = new ImageIcon(new ImageIcon("Images/life.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		dead = new ImageIcon(new ImageIcon("Images/dead.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
		shot = new ImageIcon(new ImageIcon("Images/shot.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
	}

	/**
	 * create a player and put it in random place in board
	 */
	private void putPlayer() {
		Random r = new Random();
		int x, y;
		while (true) {
			x = r.nextInt(height - 1);
			y = r.nextInt(weight - 1);
			if (board[x][y] == 0) {
				board[x][y] = 2;
				tiles[x][y].setIcon(pright);
				player = new Player(x, y);
				tiles[x][y].setText(Integer.toString(player.getHp()));
				player.setBullets(10);
				break;
			}
		}
	}

	/**
	 * find empty cells and put a mane number of mane is 0.4*height*weight
	 */
	public void putRandomMane() {
		Random r = new Random();
		int x, y;
		int s = (int) (0.2 * (height) * (weight));
		for (int i = 0; i < s; i++) {
			while (true) {
				x = r.nextInt(height - 1);
				y = r.nextInt(weight - 1);
				if (board[x][y] == 0) {
					board[x][y] = 1;
					tiles[x][y].setIcon(mane);
					break;
				}
			}
		}
	}

	/**
	 * find empty cells and put a robot number of robots stored in robotnum variable
	 */
	private void putRandomRobots() {
		Random r = new Random();
		robots = new Player[robotnum];
		int x, y;
		for (int i = 0; i < robotnum; i++) {
			while (true) {
				x = r.nextInt(height - 1);
				y = r.nextInt(weight - 1);
				if (board[x][y] == 0) {
					board[x][y] = 3;
					tiles[x][y].setIcon(eright);
					robots[i] = new Player(x, y);
					tiles[x][y].setText(Integer.toString(robots[i].getHp()));
					break;
				}
			}
		}
	}

	/**
	 * create a timertask and run every 1 second. for every robot create a random
	 * face then move it to random face that created
	 */
	private void moveRobots() {
		Random r = new Random();
		// Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				for (int i = 0; i < robotnum; i++) {
					if (robots[i].isDead() == true)
						continue;
					if (robotShoot(robots[i]) == true)
						continue;
					int x = robots[i].getX();
					int y = robots[i].getY();
					int dir = r.nextInt(4);
					if (dir == 0) {
						robots[i].setFace(2);
						if (board[x + 1][y] == 0 || board[x + 1][y] == 4) {
							// if the cells is extra life add to robot's life
							if (board[x + 1][y] == 4)
								if (robots[i].getHp() < 4)
									robots[i].setHp(robots[i].getHp() + 1);
							board[x][y] = 0;
							tiles[x][y].setIcon(main);
							board[x + 1][y] = 3;
							tiles[x + 1][y].setIcon(edown);
							robots[i].setX(x + 1);
							tiles[x][y].setText("");
							tiles[x + 1][y].setText(Integer.toString(robots[i].getHp()));

						} else {
							tiles[x][y].setIcon(edown);
						}

					} else if (dir == 1) {
						robots[i].setFace(4);
						if (board[x][y - 1] == 0 || board[x][y - 1] == 4) {
							if (board[x][y - 1] == 4)
								if (robots[i].getHp() < 4)
									robots[i].setHp(robots[i].getHp() + 1);
							board[x][y] = 0;
							tiles[x][y].setIcon(main);
							board[x][y - 1] = 3;
							tiles[x][y - 1].setIcon(eleft);
							robots[i].setY(y - 1);
							tiles[x][y].setText("");
							tiles[x][y - 1].setText(Integer.toString(robots[i].getHp()));

						} else {
							tiles[x][y].setIcon(eleft);
						}
					} else if (dir == 2) {
						robots[i].setFace(8);
						if (board[x - 1][y] == 0 || board[x - 1][y] == 4) {
							if (board[x - 1][y] == 4)
								if (robots[i].getHp() < 4)
									robots[i].setHp(robots[i].getHp() + 1);
							board[x][y] = 0;
							tiles[x][y].setIcon(main);
							board[x - 1][y] = 3;
							tiles[x - 1][y].setIcon(eup);
							robots[i].setX(x - 1);
							tiles[x][y].setText("");
							tiles[x - 1][y].setText(Integer.toString(robots[i].getHp()));

						} else {
							tiles[x][y].setIcon(eup);
						}
					} else if (dir == 3) {
						robots[i].setFace(6);
						if (board[x][y + 1] == 0 || board[x][y + 1] == 4) {
							if (board[x][y + 1] == 4)
								if (robots[i].getHp() < 4)
									robots[i].setHp(robots[i].getHp() + 1);
							board[x][y] = 0;
							tiles[x][y].setIcon(main);
							board[x][y + 1] = 3;
							tiles[x][y + 1].setIcon(eright);
							robots[i].setY(y + 1);
							tiles[x][y].setText("");
							tiles[x][y + 1].setText(Integer.toString(robots[i].getHp()));

						} else {
							tiles[x][y].setIcon(eright);
						}
					}
				}
			}
		}, 0, 1000);
	}

	/**
	 * spawn a extra life (4 in board) every 20 seconds
	 */
	public void spawnLife() {
		Random r = new Random();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				while (true) {
					int x = r.nextInt(height - 1);
					int y = r.nextInt(weight - 1);
					if (board[x][y] == 0) {
						board[x][y] = 4;
						tiles[x][y].setIcon(life);
						break;
					}
				}
			}
		}, 0, 20000);

	}

	/**
	 * move player down. check if x+1 is empty or not then move it ro just change
	 * the face. if cell is extra life, add a life to player's health
	 */
	private void moveDown() {
		int x = player.getX();
		int y = player.getY();
		player.setFace(2);
		if (board[x + 1][y] == 0 || board[x + 1][y] == 4) {
			if (board[x + 1][y] == 4)
				if (player.getHp() < 4)
					player.setHp(player.getHp() + 1);
			board[x][y] = 0;
			tiles[x][y].setIcon(main);
			board[x + 1][y] = 2;
			tiles[x + 1][y].setIcon(pdown);
			player.setX(x + 1);
			tiles[x][y].setText("");
			tiles[x + 1][y].setText(Integer.toString(player.getHp()));

		} else {
			tiles[x][y].setIcon(pdown);
		}
	}

	/**
	 * move player up. check if x-1 is empty or not then move it ro just change the
	 * face. if cell is extra life, add a life to player's health
	 */
	private void moveUp() {
		int x = player.getX();
		int y = player.getY();
		player.setFace(8);
		if (board[x - 1][y] == 0 || board[x - 1][y] == 4) {
			if (board[x - 1][y] == 4)
				if (player.getHp() < 4)
					player.setHp(player.getHp() + 1);
			board[x][y] = 0;
			tiles[x][y].setIcon(main);
			board[x - 1][y] = 2;
			tiles[x - 1][y].setIcon(pup);
			player.setX(x - 1);
			tiles[x][y].setText("");
			tiles[x - 1][y].setText(Integer.toString(player.getHp()));

		} else {
			tiles[x][y].setIcon(pup);
		}
	}

	/**
	 * move player right. check if y+1 is empty or not then move it ro just change
	 * the face. if cell is extra life, add a life to player's health
	 */
	private void moveRight() {
		int x = player.getX();
		int y = player.getY();
		player.setFace(6);
		if (board[x][y + 1] == 0 || board[x][y + 1] == 4) {
			if (board[x][y + 1] == 4)
				if (player.getHp() < 4)
					player.setHp(player.getHp() + 1);
			board[x][y] = 0;
			tiles[x][y].setIcon(main);
			board[x][y + 1] = 2;
			tiles[x][y + 1].setIcon(pright);
			player.setY(y + 1);
			tiles[x][y].setText("");
			tiles[x][y + 1].setText(Integer.toString(player.getHp()));

		} else {
			tiles[x][y].setIcon(pright);
		}
	}

	/**
	 * move player left. check if y-1 is empty or not then move it ro just change
	 * the face. if cell is extra life, add a life to player's health
	 */
	private void moveLeft() {
		int x = player.getX();
		int y = player.getY();
		player.setFace(4);
		if (board[x][y - 1] == 0 || board[x][y - 1] == 4) {
			if (board[x][y - 1] == 4)
				if (player.getHp() < 4)
					player.setHp(player.getHp() + 1);
			board[x][y] = 0;
			tiles[x][y].setIcon(main);
			board[x][y - 1] = 2;
			tiles[x][y - 1].setIcon(pleft);
			player.setY(y - 1);
			tiles[x][y].setText("");
			tiles[x][y - 1].setText(Integer.toString(player.getHp()));

		} else {
			tiles[x][y].setIcon(pleft);
		}
	}

	/**
	 * robot shot can be triggered if the player been in direction that robots are
	 * facing.
	 * 
	 * @param robot check for each robot object
	 * @return true if shot hit player and false if not
	 */
	private boolean robotShoot(Player robot) {
		int x = robot.getX();
		int y = robot.getY();
		tiles[x][y].setIcon(shot);
		if (robot.getFace() == 6) {
			for (int i = y + 1; i < weight; i++) {
				if (board[x][i] == 1)
					break;
				else if (board[x][i] == 2) {
					playerGetShot();
					return true;
				} else if (board[x][i] == 4)
					continue;
			}

		} else if (robot.getFace() == 8) {
			for (int i = x - 1; i > 0; i--) {
				if (board[i][y] == 1)
					break;
				else if (board[i][y] == 2) {
					playerGetShot();
					return true;
				} else if (board[i][y] == 4)
					continue;
			}

		} else if (robot.getFace() == 4) {
			for (int i = y - 1; i > 0; i--) {
				if (board[x][i] == 1)
					break;
				else if (board[x][i] == 2) {
					playerGetShot();
					return true;
				} else if (board[x][i] == 4)
					continue;
			}
		} else if (robot.getFace() == 2) {
			for (int i = x + 1; i < height; i++) {
				if (board[i][y] == 1)
					break;
				else if (board[i][y] == 2) {
					playerGetShot();
					return true;
				} else if (board[i][y] == 4)
					continue;
			}

		}
		return false;

	}

	/**
	 * player shooting method that check the direction player facing and shoot.
	 */
	private void shoot() {
		// check for 1 second cooldown
		if (System.currentTimeMillis() - lastshoot < 1000 && firstshoot == false)
			return;
		// check for 3 second reload time
		if (System.currentTimeMillis() - reloadtime < 3000 && firstshoot == false) {
			return;
		}
		if (player.getBullets() <= 0)
			player.setBullets(10);
		int x = player.getX();
		int y = player.getY();
		tiles[x][y].setIcon(shot);
		int face = player.getFace();
		player.decBullets();
		if (face == 4) {
			for (int i = y - 1; i > 0; i--) {
				if (board[x][i] == 1)
					break;
				else if (board[x][i] == 3) {
					robotGetShot(x, i);
					break;
				} else if (board[x][i] == 4)
					continue;
			}
		} else if (face == 6) {
			for (int i = y + 1; i < weight; i++) {
				if (board[x][i] == 1)
					break;
				else if (board[x][i] == 3) {
					robotGetShot(x, i);
					break;
				} else if (board[x][i] == 4)
					continue;
			}
		} else if (face == 8) {
			for (int i = x - 1; i > 0; i--) {
				if (board[i][y] == 1)
					break;
				else if (board[i][y] == 3) {
					robotGetShot(i, y);
					break;
				} else if (board[i][y] == 4)
					continue;
			}
		} else if (face == 2) {
			for (int i = x + 1; i < height; i++) {
				if (board[i][y] == 1)
					break;
				else if (board[i][y] == 3) {
					robotGetShot(i, y);
					break;
				} else if (board[i][y] == 4)
					continue;
			}
		}
		lastshoot = System.currentTimeMillis();
		firstshoot = false;
		// store the reload time
		if (player.getBullets() <= 0)
			reloadtime = System.currentTimeMillis();
		if (checkWin() == true) {
			long time = System.currentTimeMillis() - starttime;
			JOptionPane.showMessageDialog(this, "You win in " + time / 1000 + " seconds.");
			t.cancel();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			dispose();
			Menu.main(null);
		}

	}

	/**
	 * a method to checking win
	 * 
	 * @return true if player hp is 0 and false if player hp is greater than 0
	 */
	private boolean checkWin() {
		for (Player i : robots) {
			if (i.getHp() > 0)
				return false;
		}
		return true;
	}

	/**
	 * if player get shot by robot, player's hp will be decreased by 1 and player
	 * can die
	 */
	private void playerGetShot() {
		player.setHp(player.getHp() - 1);
		if (player.getHp() <= 0) {
			player.die();
			long time = System.currentTimeMillis() - starttime;
			JOptionPane.showMessageDialog(this, "You lose in " + time / 1000 + " seconds.");
			t.cancel();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			dispose();
			Menu.main(null);
		}
	}

	/**
	 * if robot get shot by player, robots' hp will be decreased by 1 and they can
	 * die
	 */
	private void robotGetShot(int x, int y) {
		for (Player i : robots) {
			if (i.getX() == x && i.getY() == y) {
				i.setHp(i.getHp() - 1);
				if (i.getHp() <= 0) {
					i.die();
					tiles[x][y].setIcon(dead);
					tiles[x][y].setText("");
				}
			}
		}
	}

	/**
	 * keylistener method to find which key is pressed
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP)
			moveUp();

		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			moveDown();

		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			moveLeft();

		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			moveRight();
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			shoot();

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
