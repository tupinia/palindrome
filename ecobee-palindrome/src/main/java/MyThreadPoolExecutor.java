import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class that extends {@link ThreadPoolExecutor} in order to override beforeExecute and afterExecute methods 
 * @author MCH
 *
 */
public class MyThreadPoolExecutor extends ThreadPoolExecutor {

	private int maxTimeout = 30;
	private volatile ConcurrentHashMap<Long, ThreadStat> map = new ConcurrentHashMap<Long, ThreadStat>();
	
    // holds in-progress tasks
    private final Map<Runnable, Boolean> inProgress = new ConcurrentHashMap<Runnable,Boolean>(); 
	
    // holds startime of the currently executing task. 
    // Each thread has its own copy of this variable
    private final ThreadLocal<Long> startTime = new ThreadLocal<Long>(); 
   
    private volatile long maxTime; 		// in ms
    private volatile long totalTime; 	// in ms
    
    private volatile long totalTasks;	
	
	public int getMaxTimeout() {
		return maxTimeout;
	}

	public void setMaxTimeout(int maxTimeout) {
		this.maxTimeout = maxTimeout;
	}

	public ConcurrentHashMap<Long, ThreadStat> getMap() {
		return map;
	}	
	
	public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	// Called by each thread before executing a task
	protected void beforeExecute(Thread t, Runnable r) {

		super.beforeExecute(t, r);
		
        inProgress.put(r, Boolean.TRUE);
        
        // record the start time
        startTime.set(new Long(System.currentTimeMillis())); 		
	}

	protected void afterExecute(Runnable r, Throwable t) {
		
		// calculate the time taken by the task to execute
        long time = System.currentTimeMillis() - startTime.get().longValue();
        
        synchronized (this) { 
        	// update the stats of the pool
            totalTime += time;
            if (maxTime < time) maxTime = time;
            ++totalTasks;
        } 
        
        inProgress.remove(r);		
        
        super.afterExecute(r, t);
	}

	
    /**
     * @return - returns the currently executing tasks
     */    
    public Set<Runnable> getInProgressTasks() {
        return Collections.unmodifiableSet(inProgress.keySet());
    }
 
    /**
     * @return - returns the total tasks executed till that moment
     */
    public synchronized long getTotalTasks() {
        return totalTasks;
    }
    
    /**
     * @return - returns the average execution time of tasks in ms
     */
    public synchronized double getAverageExecutionTime() {
    	return (totalTasks == 0) ? 0 : totalTime / (double) totalTasks;
    }
 
    /**
     * @return - returns the average execution time of tasks in ms
     */
    public synchronized long getMaxExecutionTime() {
        return maxTime;
    }
    
    /**
     * @return - returns the total execution time of tasks in ms
     */
    public synchronized long getTotalExecutionTime() {
        return totalTime;
    }	
}
