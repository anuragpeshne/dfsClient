import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Console implements ActionListener{
	private JFrame frame;
	private JPanel panel;
	private JPanel lowerPanel;
	JButton refreshButton;
	JTextArea logScreen;
	Client client;
	JTextField commandIp;
	private JButton exeCommand;
	
	public Console(Client c) {
		this.client = c;
		frame = new JFrame("Console");
		panel = new JPanel();
		logScreen = new JTextArea(15,25);
		logScreen.setEditable(false);
		
		refreshButton = new JButton("Refesh");
		refreshButton.addActionListener(new refreshListener());
		exeCommand = new JButton("Change");
		exeCommand.addActionListener(new exeComListener());
		lowerPanel = new JPanel();
		commandIp = new JTextField();
		commandIp.setSize(100, 10);
		commandIp.setText("filename username 4");
		lowerPanel.add(commandIp);
		lowerPanel.add(exeCommand);
		lowerPanel.add(refreshButton);
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		frame.getContentPane().add(BorderLayout.EAST, logScreen);
		frame.getContentPane().add(BorderLayout.CENTER,panel);
		frame.getContentPane().add(BorderLayout.SOUTH,lowerPanel);
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
		this.client.cacheManager.monitor = false;
		this.logScreen.append(pressedButton.getName() + " requested...\n");
		System.out.println("Get File" + pressedButton.getName());
		client.getFile(pressedButton.getName());
		while(!client.cacheManager.eventDetected);
		this.client.cacheManager.monitor = true;
	}
	
	public void generateButtons() {
		panel.removeAll();
		panel.revalidate();
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
	}
	public class refreshListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			generateButtons();
			commandIp.setText("filename username 4");
		}
		
	}
	public class exeComListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String commandPara = commandIp.getText();
			String[] brkCom = commandPara.split(" ");
			if(brkCom[0].length() > 0 && brkCom[1].length() > 0 && brkCom[2].length() > 0) {
				String command = "PERMIT " + Console.this.client.token + " " + commandPara;
				Console.this.client.writer.println(command);
				Console.this.client.writer.flush();
				Console.this.logScreen.append("Permission change for " + brkCom[1] + " requested.\n");
				try {
					String response = Console.this.client.reader.readLine();
					String[] responseS = response.split(" ");
					if(responseS[0].compareTo("PERMIT") == 0)
						if(responseS[1].compareTo("200") == 0)
							Console.this.logScreen.append("Changed permissions successfully.\n");
						else
							Console.this.logScreen.append("Unable to change permission.\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
				Console.this.logScreen.append("Invalid Request.\n");
		}
		
	}
}
