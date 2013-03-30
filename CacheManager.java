import java.io.File;


public class CacheManager {
	public static final String clientRoot = System.getProperty("user.home") + "/DFCRoot/";
	
	public CacheManager() {
		File root = new File(clientRoot);
		if(!root.exists())
			root.mkdir();
		
		DiskWatcher dw = new DiskWatcher();
		Thread watcher = new Thread(dw);
		watcher.run();
	}
	
	public class DiskWatcher implements Runnable{

		@Override
		public void run() {
			
		}
		
	}
}
