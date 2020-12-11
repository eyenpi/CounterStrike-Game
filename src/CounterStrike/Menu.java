package CounterStrike;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Menu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu frame = new Menu();
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
	public Menu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 233, 277);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		// Game class will run if Single player been clicked
		JButton btnNewButton = new JButton("Single Player");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Game.main(null);
				dispose();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnNewButton.setBounds(10, 10, 196, 59);
		contentPane.add(btnNewButton);
		// connect player 2 to sever
		JButton btnMultiplayer = new JButton("Multiplayer");
		btnMultiplayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Runnable runnable2 = () -> {
					OnlineGame.main(null);
				};
				Thread t2 = new Thread(runnable2);
				t2.start();

			}
		});
		// Server runs and 1 client connect to it
		btnMultiplayer.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnMultiplayer.setBounds(10, 79, 196, 59);
		contentPane.add(btnMultiplayer);

		JButton btnServer = new JButton("Server");
		btnServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Runnable runnable = () -> {
					Server.main(null);
				};
				Thread t = new Thread(runnable);
				t.start();
				Runnable runnable2 = () -> {
					OnlineGame.main(null);
				};
				Thread t2 = new Thread(runnable2);
				t2.start();
			}
		});
		btnServer.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnServer.setBounds(10, 148, 196, 59);
		contentPane.add(btnServer);

		JLabel lblNewLabel = new JLabel("Use Arrow Keys To Move");
		lblNewLabel.setBounds(10, 217, 196, 13);
		contentPane.add(lblNewLabel);

		JLabel lblUseSpaceTo = new JLabel("Use SPACE To Shoot");
		lblUseSpaceTo.setBounds(10, 229, 196, 13);
		contentPane.add(lblUseSpaceTo);
	}
}
