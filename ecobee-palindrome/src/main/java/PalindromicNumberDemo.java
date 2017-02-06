import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PalindromicNumberDemo {
	
	// Init vars. Will be overriden into the main method
	private static long rangeItemsStart = 0;
	private static long rangeItemsEnd = 100000000;
	private static int nThreads = 1;
	private static int maxTimeout = 30;
	private static boolean writeReportOnDisk = false;
	
	private static void showUsage() {
		System.err.println("java -jar palindrome.jar rangeItemsStart rangeItemsEnd nThreads maxTimeout writeReportOnDisk (optional)");
		System.err.println("\trangeItemsStart, Start range of numbers");
		System.err.println("\trangeItemsEnd, End range of numbers");
		System.err.println("\tnThreads, Size of the executor pool");
		System.err.println("\ttimeout, Time is seconds before the program throws a timeout");
		System.err.println("\twriteReportOnDisk - optional, write report on disk (i/o) instead of console output");
	}
	
	/**
	 * Executing the exercise.
	 * 
	 * @param args
	 *            Command line arguments
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) 
			throws IllegalArgumentException, InterruptedException, ExecutionException, IOException {
		
		long timeStart = System.currentTimeMillis();
		
		if (args != null && args.length < 4) {
			System.err.println("Wrong input parameters. Usage:");
			showUsage();
			System.exit(99);
		} else {
			try {
				if (args[0] != null)  rangeItemsStart = Long.parseLong(args[0]);
				if (args[1] != null)  rangeItemsEnd = Long.parseLong(args[1]);
				if (args[2] != null)  nThreads = Integer.parseInt(args[2]);
				if (args[3] != null)  maxTimeout = Integer.parseInt(args[3]);
				
				// Optional
				if (args.length == 5) writeReportOnDisk = Boolean.parseBoolean(args[4]);
			} catch (IllegalArgumentException iae) {
				System.err.println("Invalid input parameter");
				showUsage();
				System.exit(99);
			}
		}
		
		// Check inputs
		if (rangeItemsEnd < rangeItemsStart) throw new IllegalArgumentException("Range values error");
		if (nThreads <= 0) throw new IllegalArgumentException("Size of the executor pool must be > 0");
		if (maxTimeout < 0) throw new IllegalArgumentException("Timeout must be > 0");
		
		if (writeReportOnDisk) {
			System.out.println("Executing palindrome exercise with inputs:");
			System.out.println("\telements=" + rangeItemsStart + " to " + rangeItemsEnd);
			System.out.println("\tnThreads=" + nThreads);
			System.out.println("\tmaxTimeout=" + maxTimeout);
			System.out.println("\twriteReportOnDisk=" + writeReportOnDisk);
		}
		
		// GO
		List<Long> palindromes = new ArrayList<>();
		MyThreadPoolExecutor pool = new MyThreadPoolExecutor(nThreads,
                 nThreads, Long.MAX_VALUE,
                 TimeUnit.SECONDS, 
                 new LinkedBlockingQueue());
		
		Callable c = new ThreadPoolExecutorTask(rangeItemsStart, rangeItemsEnd,
				maxTimeout, pool);
		ExecutorService executor = pool;
		Future<Integer> f = executor.submit(c);
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

		// Get result from the pool
		ThreadStat ths = ThreadPoolExecutorTask.threadStat;
		palindromes = ths.getList();
		// Sort arraylist
		if (palindromes != null) {
			Collections.sort(palindromes);
		}
		 
        // shutdown the pool
        executor.shutdown();
        
        // Create object Stat for reporting
        Stat stat = new Stat();
        stat.setTotalTasks(pool.getTotalTasks());
        stat.setAverageExecutionTime(pool.getAverageExecutionTime());
        stat.setMaxExecutionTime(pool.getMaxExecutionTime());
        stat.setTotalExecutionTime(pool.getTotalExecutionTime());
        stat.setStart(timeStart);
        stat.setEnd(System.currentTimeMillis());
        stat.setPalindromes(palindromes);
        
        // print the execution stats if file report only
        if (writeReportOnDisk) {
//	   		 for (Long pal : palindromes) {
//	   			 System.out.println(pal);
//	   		 }
	        System.out.println("----------------------------------");
	        System.out.println("Total tasks executed = " + pool.getTotalTasks());
	        System.out.println("Average execution time = " + pool.getAverageExecutionTime() + " ms");
	        System.out.println("Max execution time = " + pool.getMaxExecutionTime() + " ms");
	        System.out.println("----------------------------------");
        }
		
		doWriteStat(stat);
		
		if (writeReportOnDisk) {
			long timeNeeded = System.currentTimeMillis() - timeStart;
			System.out.println(stat.getPalindromes().size() + " palindromes calculated in " + timeNeeded + " ms");
		}
		
		if (ths.isCompleted() == false) {
			System.err.println("");
			System.err.println("Execution TIMEOUT");
			System.exit(99);
		}		
	}	
	
    private static class ThreadPoolExecutorTask implements Callable<ThreadStat>, Runnable {
        private long first;
        private long last;
        private int timeout;
        private ThreadPoolExecutor tpe;
        public static volatile ThreadStat threadStat = new ThreadStat();
//        public static volatile List<Long> count = new ArrayList<>();

        public ThreadPoolExecutorTask(long first, long last, int timeout, ThreadPoolExecutor tpe) {
            this.first = first;
            this.last = last;
            this.timeout = timeout;
            this.tpe = tpe;
        }

        public void run() {
        	
			long startTime = System.currentTimeMillis();
			long endTime = startTime + timeout * 1000;
			while (true) {

				List<Long> palindromes = new ArrayList<Long>();
				for (long i = first; i <= last; i++) {

//					System.out.println(i);
					if (isPalindrome(String.valueOf(i)) && isPalindrome(Long.toBinaryString(i))) {
//						System.out.println("Palindrome ok " +i + " binary " + Long.toBinaryString(i) + " ====== " + isPalindrome(Long.toBinaryString(i)));
						palindromes.add(i);
					}

					// Check timeout
					if (System.currentTimeMillis() > endTime) {
						System.err.println("Timeout for thread " + Thread.currentThread().getId());
						threadStat.setCompleted(false);
						Thread.currentThread().interrupt();
						break;
					}
				}

				threadStat.getList().addAll(palindromes);
				
				break;
			}
        }

        public ThreadStat call() throws Exception {
        	
        	// Calculate partitions
    		long partitionSize = (rangeItemsEnd - rangeItemsStart) / nThreads;
    		
    		long start = rangeItemsStart;
    		long end = start;
    		for (int i = 1; i <= nThreads; i++) {

    			end = start + partitionSize;
    			if (end > rangeItemsEnd)
    				end = rangeItemsEnd;
    			
    			// Last iteration ?
    			if (i == nThreads) {
    				end = rangeItemsEnd;
    			}

    			tpe.execute(new ThreadPoolExecutorTask(start, end, timeout, tpe));
    			
    			start += partitionSize + 1;
    		}  
    		
            tpe.shutdown();
            return threadStat;
        }
    }	
	
	private static void doWriteStat(Stat stat)
			throws IOException {
		File f;
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try {
			if (writeReportOnDisk) {
				f = File.createTempFile("ecobee_", ".txt");
				System.out.println("File report is located at " + f.getAbsolutePath());
				fos = new FileOutputStream(f);
				bw = new BufferedWriter(new OutputStreamWriter(fos));
			} else {
				// console output
			     bw = new BufferedWriter(new OutputStreamWriter(new
			             FileOutputStream(java.io.FileDescriptor.out), "ASCII"), 512);
			}		

			bw.write("Palindromes:");
			bw.newLine();			
			
			for (Long palindrome : stat.getPalindromes()) {
				String output = palindrome + " binary: "
						+ Long.toBinaryString(palindrome);
				// System.out.println("\t\t" + output + " - " +

				bw.write("\t" + output);
				bw.newLine();
			}
			
			bw.newLine();
//			Performance (millis): max: #, mean: #
			StringBuffer sb = new StringBuffer();
			sb.append("Performance (millis): max: ");
			sb.append(stat.getMaxExecutionTime());
			sb.append(", mean: ");
			sb.append(stat.getAverageExecutionTime());			
			bw.write(sb.toString());			
			bw.newLine();
			bw.write("Palindromes computed: " + stat.getPalindromes().size());
			bw.newLine();
			bw.write("Tasks run: " + stat.getTotalTasks());
			bw.newLine();
			bw.write("Duration: " + (System.currentTimeMillis() - stat.getStart()) + " millis.");	
			
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
	
	////////////////////////////////////////////
	// UTILITIES
	////////////////////////////////////////////
	
	public static boolean isPalindrome(String s) {
	  int n = s.length();
	  for (int i = 0; i < (n/2); ++i) {
	     if (s.charAt(i) != s.charAt(n - i - 1)) {
	         return false;
	     }
	  }

	  return true;
	}	
	
	////////////////////////////////////////////
	// END UTILITIES
	////////////////////////////////////////////	

}
