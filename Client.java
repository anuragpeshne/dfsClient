import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {
	public static final String serverIp = "127.0.0.1";
	public static final int serverPort = 8080;
	
	private static final String username = "root";
	private static final String password = "root123";
	private Socket sock;
	PrintWriter writer;
	BufferedReader reader;
	String token;
	
	public static void main(String[] args) {
		CacheManager cacheManager = new CacheManager();
		Client client = new Client();
		client.authenticate();
	}
	
	public Client() {
		this.setupNetworking();
	}
	
	private void setupNetworking() {
		try {
			sock = new Socket(serverIp, serverPort);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void authenticate() {
		boolean authenticated = false;
		String req = "CONNECT" + " " + username + " " + password;
		while(!authenticated) {
			writer.println(req);
			try {
				String response = reader.readLine();
				String[] responses = response.split(" ");
				if(responses[0].compareTo("CONNECT") == 0)
					if(responses[1].compareTo("200") == 0)
						this.token = responses[2];
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
