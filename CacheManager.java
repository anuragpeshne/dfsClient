import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;


public class CacheManager {
	public static final String clientRoot = System.getProperty("user.home") + "/DFCRoot/";
	private Client client;
	
	public CacheManager(Client c) {
		this.client = c;
		File root = new File(clientRoot);
		if(!root.exists())
			root.mkdir();
		
		DiskWatcher dw = new DiskWatcher();
		Thread watcher = new Thread(dw);
		watcher.run();
		try {
			Desktop.getDesktop().open(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class DiskWatcher implements Runnable{
		final FileSystem fs = FileSystems.getDefault();
		
		public void run() {
			WatchService ws;
			try {
				ws = fs.newWatchService();
				Path pth = Paths.get(CacheManager.clientRoot);
				pth.register(ws, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
				boolean loop = true;
				WatchKey wk = null;
				WatchEvent<?> lastEvent = null;
				do {
						wk = ws.take();
						for(WatchEvent<?> event : wk.pollEvents()) {
							//WatchEvent.Kind<?> kind = event.kind();
							//Path eventPath = (Path) event.context();
							lastEvent = event;
						}
						wk.reset();
						WatchEvent.Kind<?> kind = lastEvent.kind();
						Path eventPath = (Path) lastEvent.context();
						System.out.println(eventPath.getFileName() + "->" + kind.toString());
						String fileName = eventPath.getFileName().toString();
						if(fileName.charAt(0) != '.') {
							if(kind.toString().compareTo("ENTRY_CREATE") == 0) {
								CacheManager.this.newFileCreated(fileName);
							}
							else if(kind.toString().compareTo("ENTRY_DELETE") == 0) {
								CacheManager.this.newFileDeleted(fileName);
							}
							else if(kind.toString().compareTo("ENTRY_MODIFY") == 0) {
								CacheManager.this.newFileEdited(fileName);
							}
						}
				} while(loop);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public void writeToDisk(String filename, String response) {
		File f = new File(clientRoot + filename);
		if(f.exists())
			f.delete();
		try {
			f.createNewFile();
			FileWriter fw = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(fw);
			writer.write(response);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void newFileEdited(String fileName) {
		this.newFileCreated(fileName);
	}


	public void newFileDeleted(String fileName) {
		this.client.fileDeleted(fileName);
	}


	public void newFileCreated(String fileName) {
		File newFile = new File(clientRoot + fileName);
		if(newFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(newFile));
				String buffer, content = "";
				while((buffer = reader.readLine()) != null) {
					content += buffer + "\n";
				}
				reader.close();
				this.client.putFile(fileName, content);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
