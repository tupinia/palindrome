import java.util.ArrayList;
import java.util.List;

public class Stat {

	private long start;
	private long end;
	private long totalTasks;
	private double averageExecutionTime;
	private long maxExecutionTime;
	private long totalExecutionTime;
	private List<Long> palindromes = new ArrayList<>();
	
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public long getTotalTasks() {
		return totalTasks;
	}
	public void setTotalTasks(long totalTasks) {
		this.totalTasks = totalTasks;
	}
	public double getAverageExecutionTime() {
		return averageExecutionTime;
	}
	public void setAverageExecutionTime(double averageExecutionTime) {
		this.averageExecutionTime = averageExecutionTime;
	}
	public long getMaxExecutionTime() {
		return maxExecutionTime;
	}
	public void setMaxExecutionTime(long maxExecutionTime) {
		this.maxExecutionTime = maxExecutionTime;
	}
	public long getTotalExecutionTime() {
		return totalExecutionTime;
	}
	public void setTotalExecutionTime(long totalExecutionTime) {
		this.totalExecutionTime = totalExecutionTime;
	}
	public List<Long> getPalindromes() {
		return palindromes;
	}
	public void setPalindromes(List<Long> palindromes) {
		this.palindromes = palindromes;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Stat [start=");
		builder.append(start);
		builder.append(", end=");
		builder.append(end);
		builder.append(", totalTasks=");
		builder.append(totalTasks);
		builder.append(", averageExecutionTime=");
		builder.append(averageExecutionTime);
		builder.append(", maxExecutionTime=");
		builder.append(maxExecutionTime);
		builder.append(", totalExecutionTime=");
		builder.append(totalExecutionTime);
		builder.append(", palindromes=");
		builder.append(palindromes.size());
		builder.append("]");
		return builder.toString();
	}

	
}
