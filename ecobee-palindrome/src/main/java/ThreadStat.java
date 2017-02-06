import java.util.ArrayList;
import java.util.List;

public class ThreadStat {

	private boolean completed = true;
	private List<Long> list = new ArrayList<>();
	
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public List<Long> getList() {
		return list;
	}
	public void setList(List<Long> list) {
		this.list = list;
	}
	
}
