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
	Console console;
	CacheManager cacheManager;
	
	public static void main(String[] args) {
		Client client = new Client();
	}
	
	public Client() {
		cacheManager = new CacheManager();
		console = new Console(this);
		this.setupNetworking();
		this.authenticate();
		console.start();
	}
	
	private void setupNetworking() {
		try {
			sock = new Socket(serverIp, serverPort);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			console.logScreen.append("Connected to "+ Client.serverIp +":"+ Client.serverPort+"\n");
		} catch (IOException e) {
			console.logScreen.append("Connection Failed!\n");
			e.printStackTrace();
		}
	}
	private void authenticate() {
		boolean authenticated = false;
		String req = "CONNECT" + " " + username + " " + password;
		while(!authenticated) {
			writer.println(req);
			writer.flush();
			try {
				String response = reader.readLine();
				String[] responses = response.split(" ");
				if(responses[0].compareTo("CONNECT") == 0)
					if(responses[1].compareTo("200") == 0) {
						this.token = responses[2];
						this.console.logScreen.append("Authentication Successful\n");
						authenticated = true;
					}
					else if(responses[2].compareTo("401") == 0) {
						this.console.logScreen.append("Authentication Failed\n");
						break;
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void getFile(String filename) {
		String req = "GET " + this.token + " " + filename;
		writer.println(req);
		writer.flush();
		String response = null;
		try {
			response = reader.readLine();
			String[] responseS = response.split(" ");
			if(responseS[0].compareTo("GET") == 0)
				if(responseS[1].compareTo("200") == 0) {
					cacheManager.writeToDisk(filename,this.collectResponse());
					this.console.logScreen.append(filename + " received\n");
				}
				else
					this.console.logScreen.append("Some error in getting file\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String listDir(String dirName) {
		String req = "LIST " + this.token + " " + dirName;
		writer.println(req);
		writer.flush();
		String response = null;
		try {
			response = reader.readLine();
			String[] responseS = response.split(" ");
			if(responseS[0].compareTo("LIST") == 0)
				if(responseS[1].compareTo("200") == 0)
					return(this.collectResponse());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.console.logScreen.append("Unknown Response from server\n");
		return null;
	}
	private String collectResponse() {
		String response = "", buffer;
		try {
			while((buffer = reader.readLine()).compareTo("$$EOF$$") != 0) {
				response += buffer + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
