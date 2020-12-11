package CounterStrike;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Server {

	public static Player player2, player3;
	public static int height = 15, weight = 25;
	public static int[][] board = new int[height][weight];
	public static Timer t = new Timer();

	public static void main(String[] args) {
		System.out.println("Server started.");
		ServerSocket server = null;
		// create the main cells and mane
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < weight; j++) {
				if (i == 0 || i == height - 1 || j == 0 || j == weight - 1) {
					board[i][j] = 1;
				}
			}
		}
		putPlayer();
		putRandomMane();
		spawnLife();
		try {
			// wait for players to connect
			server = new ServerSocket(1337);
			System.out.println("Waiting for player 1");
			Socket socket1 = server.accept();
			System.out.println("Player 1 Connected");
			System.out.println("Waiting for player 2");
			Socket socket2 = server.accept();
			System.out.println("Player 2 Connected");
			MyThread t1 = new MyThread(socket1, 2);
			t1.start();
			MyThread t2 = new MyThread(socket2, 3);
			t2.start();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * put 2 players in random positions
	 */
	public static void putPlayer() {
		Random r = new Random();
		int x, y;
		while (true) {
			x = r.nextInt(height - 1);
			y = r.nextInt(weight - 1);
			if (board[x][y] == 0) {
				board[x][y] = 2;
				player2 = new Player(x, y);
				player2.setBullets(10);
				break;
			}
		}
		while (true) {
			x = r.nextInt(height - 1);
			y = r.nextInt(weight - 1);
			if (board[x][y] == 0) {
				board[x][y] = 3;
				player3 = new Player(x, y);
				player3.setBullets(10);
				break;
			}
		}
	}

	/**
	 * put some mane in random positions. the number of mane is 0.4*height*weight
	 */
	public static void putRandomMane() {
		Random r = new Random();
		int x, y;
		int s = (int) (0.2 * (height) * (weight));
		for (int i = 0; i < s; i++) {
			while (true) {
				x = r.nextInt(height - 1);
				y = r.nextInt(weight - 1);
				if (board[x][y] == 0) {
					board[x][y] = 1;
					break;
				}
			}
		}
	}

	/**
	 * spawn a extra life every 20 seconds in an empty cell
	 */
	public static void spawnLife() {
		Random r = new Random();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				while (true) {
					int x = r.nextInt(height - 1);
					int y = r.nextInt(weight - 1);
					if (board[x][y] == 0) {
						board[x][y] = 4;
						break;
					}
				}
			}
		}, 0, 20000);

	}

	static class MyThread extends Thread {
		private int p;

		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		ObjectOutputStream objOut;
		public long lastshoot2, reloadtime2, lastshoot3, reloadtime3;
		public boolean firstshoot2 = true, firstshoot3 = true;

		/**
		 * create a thread for each player
		 * 
		 * @param socket the client socket
		 * @param p      it could be 2 and 3. 2 for player 1 and 3 for player 2
		 */
		public MyThread(Socket socket, int p) {
			this.socket = socket;
			this.p = p;
		}

		/**
		 * method to send message to client through socket
		 * 
		 * @param str the message
		 */
		public void send(String str) {
			try {
				out.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * method to read a message from client through socket
		 * 
		 * @return the message that been sent by client
		 * @throws EOFException
		 */
		public String read() throws EOFException {
			String ans = "";
			try {
				ans = in.readUTF();
			} catch (IOException e) {
				try {
					socket.close();
				} catch (IOException e1) {
				}
			}
			return ans;
		}

		/**
		 * send board every 50 milliseconds (20 fps) to client
		 */
		public void sendBoard() {
			// Timer t = new Timer();
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						objOut = new ObjectOutputStream(socket.getOutputStream());
						// create a BoardPlayer that contains data of board and 2 players and sent it to
						// socket. this is because of not writing multiple object once on socket
						BoardPlayer bp = new BoardPlayer(board, player2, player3);
						objOut.writeObject(bp);
						objOut.flush();
					} catch (IOException e) {
						try {
							socket.close();
						} catch (IOException e1) {
						}
					}

				}
			}, 0, 50);
		}

		/**
		 * move players down. check if x+1 is empty or not then move it ro just change
		 * the face. if cell is extra life, add a life to player's health
		 */
		private void moveDown() {
			if (p == 2) {
				int x = player2.getX();
				int y = player2.getY();
				player2.setFace(2);
				if (board[x + 1][y] == 0 || board[x + 1][y] == 4) {
					if (board[x + 1][y] == 4)
						if (player2.getHp() < 4)
							player2.setHp(player2.getHp() + 1);
					board[x][y] = 0;
					board[x + 1][y] = 2;
					player2.setX(x + 1);
				}
			}
			if (p == 3) {
				int x = player3.getX();
				int y = player3.getY();
				player3.setFace(2);
				if (board[x + 1][y] == 0 || board[x + 1][y] == 4) {
					if (board[x + 1][y] == 4)
						if (player3.getHp() < 4)
							player3.setHp(player3.getHp() + 1);
					board[x][y] = 0;
					board[x + 1][y] = 3;
					player3.setX(x + 1);
				}

			}
		}

		/**
		 * move player up. check if x-1 is empty or not then move it ro just change the
		 * face. if cell is extra life, add a life to player's health
		 */
		private void moveUp() {
			if (p == 2) {
				int x = player2.getX();
				int y = player2.getY();
				player2.setFace(8);
				if (board[x - 1][y] == 0 || board[x - 1][y] == 4) {
					if (board[x - 1][y] == 4)
						if (player2.getHp() < 4)
							player2.setHp(player2.getHp() + 1);
					player2.setX(x - 1);
					board[x - 1][y] = 2;
					board[x][y] = 0;

				}
			}
			if (p == 3) {
				int x = player3.getX();
				int y = player3.getY();
				player3.setFace(8);
				if (board[x - 1][y] == 0 || board[x - 1][y] == 4) {
					if (board[x - 1][y] == 4)
						if (player3.getHp() < 4)
							player3.setHp(player3.getHp() + 1);
					player3.setX(x - 1);
					board[x - 1][y] = 3;
					board[x][y] = 0;
				}
			}
		}

		/**
		 * move player right. check if y+1 is empty or not then move it ro just change
		 * the face. if cell is extra life, add a life to player's health
		 */
		private void moveRight() {
			if (p == 2) {
				int x = player2.getX();
				int y = player2.getY();
				player2.setFace(6);
				if (board[x][y + 1] == 0 || board[x][y + 1] == 4) {
					if (board[x][y + 1] == 4)
						if (player2.getHp() < 4)
							player2.setHp(player2.getHp() + 1);
					board[x][y] = 0;
					board[x][y + 1] = 2;
					player2.setY(y + 1);
				}
			}
			if (p == 3) {
				int x = player3.getX();
				int y = player3.getY();
				player3.setFace(6);
				if (board[x][y + 1] == 0 || board[x][y + 1] == 4) {
					if (board[x][y + 1] == 4)
						if (player3.getHp() < 4)
							player3.setHp(player3.getHp() + 1);
					board[x][y] = 0;
					board[x][y + 1] = 3;
					player3.setY(y + 1);
				}
			}
		}

		/**
		 * move player left. check if y-1 is empty or not then move it ro just change
		 * the face. if cell is extra life, add a life to player's health
		 */
		private void moveLeft() {
			if (p == 2) {
				int x = player2.getX();
				int y = player2.getY();
				player2.setFace(4);
				if (board[x][y - 1] == 0 || board[x][y - 1] == 4) {
					if (board[x][y - 1] == 4)
						if (player2.getHp() < 4)
							player2.setHp(player2.getHp() + 1);
					board[x][y] = 0;
					board[x][y - 1] = 2;
					player2.setY(y - 1);
				}
			}
			if (p == 3) {
				int x = player3.getX();
				int y = player3.getY();
				player3.setFace(4);
				if (board[x][y - 1] == 0 || board[x][y - 1] == 4) {
					if (board[x][y - 1] == 4)
						if (player3.getHp() < 4)
							player3.setHp(player3.getHp() + 1);
					board[x][y] = 0;
					board[x][y - 1] = 3;
					player3.setY(y - 1);
				}
			}
		}

		/**
		 * if a player die. respawn it after 5 seconds in random position
		 * 
		 * @param p player 1 or player 2
		 */
		private void respawnPlayer(int p) {
			Random r = new Random();
			int x, y;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < weight; j++) {
					if (board[i][j] == 10) {
						board[i][j] = 0;
					}
				}
			}
			while (true) {
				x = r.nextInt(height - 1);
				y = r.nextInt(weight - 1);
				if (board[x][y] == 0) {
					if (p == 2) {
						player2.respawn();
						player2.setX(x);
						player2.setY(y);
						player2.setHp(3);
						board[x][y] = 2;
						player2.setBullets(10);
						break;
					}
					if (p == 3) {
						player3.respawn();
						player3.setX(x);
						player3.setY(y);
						player3.setHp(3);
						board[x][y] = 3;
						player3.setBullets(10);
						break;
					}
				}
			}
		}

		/**
		 * if player 2 get shot its hp will be decreased by 1
		 */
		private void player2GetShot() {
			player2.setHp(player2.getHp() - 1);
			if (player2.getHp() <= 0) {
				board[player2.getX()][player2.getY()] = 10;
				player2.die();
				// if player2 dies create a thread that wait 5 seconds and the call
				// respawnplayer
				Runnable runnable = () -> {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
					respawnPlayer(2);
				};
				Thread t = new Thread(runnable);
				t.start();
			}

		}

		/**
		 * if player 3 get shot its hp will be decreased by 1
		 */
		private void player3GetShot() {
			player3.setHp(player3.getHp() - 1);
			if (player3.getHp() <= 0) {
				player3.die();
				board[player3.getX()][player3.getY()] = 10;
				// if player2 dies create a thread that wait 5 seconds and the call
				// respawnplayer
				Runnable runnable = () -> {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
					respawnPlayer(3);
				};
				Thread t = new Thread(runnable);
				t.start();
			}

		}

		/**
		 * player shooting method that check the direction player facing and shoot.
		 */
		private void shoot() {
			if (p == 2) {
				// check for 1 second cooldown
				if (System.currentTimeMillis() - lastshoot2 < 1000 && firstshoot2 == false)
					return;
				// check for 3 second reload time
				if (System.currentTimeMillis() - reloadtime2 < 3000 && firstshoot2 == false) {
					return;
				}
				if (player2.getBullets() <= 0)
					player2.setBullets(10);
				int x = player2.getX();
				int y = player2.getY();
				int face = player2.getFace();
				player2.decBullets();
				if (face == 4) {
					for (int i = y - 1; i > 0; i--) {
						if (board[x][i] == 1)
							break;
						else if (board[x][i] == 3) {
							player3GetShot();
							break;
						} else if (board[x][i] == 4)
							continue;
					}
				} else if (face == 6) {
					for (int i = y + 1; i < weight; i++) {
						if (board[x][i] == 1)
							break;
						else if (board[x][i] == 3) {
							player3GetShot();
							break;
						} else if (board[x][i] == 4)
							continue;
					}
				} else if (face == 8) {
					for (int i = x - 1; i > 0; i--) {
						if (board[i][y] == 1)
							break;
						else if (board[i][y] == 3) {
							player3GetShot();
							break;
						} else if (board[i][y] == 4)
							continue;
					}
				} else if (face == 2) {
					for (int i = x + 1; i < height; i++) {
						if (board[i][y] == 1)
							break;
						else if (board[i][y] == 3) {
							player3GetShot();
							break;
						} else if (board[i][y] == 4)
							continue;
					}
				}
				lastshoot2 = System.currentTimeMillis();
				firstshoot2 = false;
				if (player2.getBullets() <= 0)
					reloadtime2 = System.currentTimeMillis();

			}
			if (p == 3)

			{
				if (System.currentTimeMillis() - lastshoot3 < 1000 && firstshoot3 == false)
					return;
				if (System.currentTimeMillis() - reloadtime3 < 3000 && firstshoot3 == false) {
					return;
				}
				if (player3.getBullets() <= 0)
					player3.setBullets(10);
				int x = player3.getX();
				int y = player3.getY();
				int face = player3.getFace();
				player3.decBullets();
				if (face == 4) {
					for (int i = y - 1; i > 0; i--) {
						if (board[x][i] == 1)
							break;
						else if (board[x][i] == 2) {
							player2GetShot();
							break;
						} else if (board[x][i] == 4)
							continue;
					}
				} else if (face == 6) {
					for (int i = y + 1; i < weight; i++) {
						if (board[x][i] == 1)
							break;
						else if (board[x][i] == 2) {
							player2GetShot();
							break;
						} else if (board[x][i] == 4)
							continue;
					}
				} else if (face == 8) {
					for (int i = x - 1; i > 0; i--) {
						if (board[i][y] == 1)
							break;
						else if (board[i][y] == 2) {
							player2GetShot();
							break;
						} else if (board[i][y] == 4)
							continue;
					}
				} else if (face == 2) {
					for (int i = x + 1; i < height; i++) {
						if (board[i][y] == 1)
							break;
						else if (board[i][y] == 2) {
							player2GetShot();
							break;
						} else if (board[i][y] == 4)
							continue;
					}
				}
				lastshoot3 = System.currentTimeMillis();
				firstshoot3 = false;
				if (player3.getBullets() <= 0)
					reloadtime3 = System.currentTimeMillis();
			}
		}

		public void run() {

			try {
				in = new DataInputStream(this.socket.getInputStream());
				out = new DataOutputStream(this.socket.getOutputStream());
				send("ready");
				sendBoard();

				// keep reading input from client
				while (!socket.isClosed()) {
					String[] inp = read().split("%%");
					if (inp[0].equals("Move")) {
						if (inp[1].equals("down"))
							moveDown();
						else if (inp[1].equals("up"))
							moveUp();
						else if (inp[1].equals("right"))
							moveRight();
						else if (inp[1].equals("left"))
							moveLeft();
					}
					if (inp[0].equals("Shoot")) {
						shoot();

					}
					if (inp[0].equals("PickLife")) {

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
