package canfilter;
import java.util.ArrayList;

public class FilterData {

	/**
	 * 
	 */
	public ArrayList<Integer> data; //ArrayList containing filters
	private int capacity;
	
	public FilterData() {
		data = new ArrayList<Integer>(10);
		capacity = 10;
	}
	
	public void add(Integer filter) {
		if (!findFilter(filter)) {
			if (data.size() >= capacity) {
				capacity *= 2;
				data.ensureCapacity(capacity);
			}
			insert(filter);
		}
	}
	
	public void insert(int filter) {	
        int left, right, mid;
        left = 0;
        right = data.size();

        while(left < right) {
            mid = (left + right)/2;
            if(data.get(mid) > filter) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        data.add(left, new Integer(filter));	
	}
	
	public boolean delete(Integer filter) {
		boolean foundFilter = false;
		for(int i = 0; i < data.size(); i++) {
			if(data.get(i).equals(filter)) {
				foundFilter = true;
				data.remove(i);
			}
		}
		return foundFilter;
	}
	
	public boolean findFilter(int filter) {
        int left, right, mid;
        left = 0;
        right = data.size();

        while(left < right) {
            mid = (left + right)/2;
            if(data.get(mid).intValue() == filter) {
            	return true;
            } else if(data.get(mid).intValue() > filter) {
                right = mid;
            } else {
                left = mid + 1;
			}
		}
		return false;
	}
	
	public void clear() {
		data.clear();
		data.ensureCapacity(10);
	}
}
