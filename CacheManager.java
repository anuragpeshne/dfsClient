import java.awt.Desktop;
import java.io.File;
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
}
