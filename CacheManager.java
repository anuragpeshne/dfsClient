import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class CacheManager {
	public static final String clientRoot = System.getProperty("user.home") + "/DFCRoot/";
	
	public CacheManager() {
		File root = new File(clientRoot);
		if(!root.exists())
			root.mkdir();
		
		DiskWatcher dw = new DiskWatcher();
		Thread watcher = new Thread(dw);
		watcher.run();
		/*
		try {
			Desktop.getDesktop().open(root);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public class DiskWatcher implements Runnable{

		@Override
		public void run() {
			System.out.println("in thread");
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
}
