package CounterStrike;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Client {
	public static Socket socket;
	public static DataInputStream in;
	public static DataOutputStream out;
	public static ObjectInputStream objIn;
	public static int[][] board;
	public static Player player2, player3;

	public Client() {
		try {
			// try to connect to server
			socket = new Socket("127.0.0.1", 1337);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			read();
			MyThread t = new MyThread();
			t.start();
		} catch (IOException e) {
			close();
		}
	}

	/**
	 * method to close the socket
	 */
	public static void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * write some string on socket to server
	 * 
	 * @param str string to send
	 */
	public static void send(String str) {
		try {
			out.writeUTF(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * listen in socket to server
	 * 
	 * @return the string that server wrote in socket
	 */
	public static String read() {
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

	static class MyThread extends Thread {
		public void run() {
			while (!socket.isClosed()) {
				try {
					objIn = new ObjectInputStream(socket.getInputStream());
					// always read a boardPlayer that contains a board and 2 player on socket
					BoardPlayer obj = (BoardPlayer) objIn.readObject();
					board = obj.getBoard();
					player2 = obj.getPlayer2();
					player3 = obj.getPlayer3();
				} catch (IOException | ClassNotFoundException e) {
				}
			}
		}
	}
}
