import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class Console implements ActionListener{
	private JFrame frame;
	private JPanel panel;
	JTextArea logScreen;
	Client client;
	
	public Console(Client c) {
		this.client = c;
		frame = new JFrame("Console");
		panel = new JPanel();
		logScreen = new JTextArea(15,25);
		logScreen.setEditable(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		frame.getContentPane().add(BorderLayout.EAST, logScreen);
		frame.getContentPane().add(BorderLayout.CENTER,panel);
		frame.setAlwaysOnTop(true);
		frame.setSize(500,600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void start() {
		this.generateButtons();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JButton pressedButton = (JButton) arg0.getSource();
		this.logScreen.append(pressedButton.getName() + " requested...\n");
		System.out.println("Get File" + pressedButton.getName());
		client.getFile(pressedButton.getName());
	}
	
	public void generateButtons() {
		String contentList = client.listDir("/");
		contentList = contentList.trim();
		String[] contentArray = contentList.split(",");
		JButton[] buttonSet = new JButton[contentArray.length];
		int i = 0;
		for(String butName : contentArray) {
			if(butName.compareTo("") != 0) {
				buttonSet[i] = new JButton(butName);
				buttonSet[i].setName(butName);
				buttonSet[i].addActionListener(this);
				panel.add(buttonSet[i]);
				i++;
			}
		}
		panel.revalidate();
	}
}
