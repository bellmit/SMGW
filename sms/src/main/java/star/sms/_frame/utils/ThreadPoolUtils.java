package star.sms._frame.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author star
 */
public class ThreadPoolUtils {
	private final static Integer MAX = 5;

	//单例模式
	private static ThreadPoolUtils instance = null;
	
	//线程池
	private ExecutorService executor;
	
	private ThreadPoolUtils(){
		executor = Executors.newFixedThreadPool(MAX);
	}
	
	public synchronized static ThreadPoolUtils getInstance(){
		if(instance == null){
			instance = new ThreadPoolUtils();
		}
		return instance;
	}
	
	public ExecutorService getThreadPool(){
		return executor;
	}
}