package by.gsu.epamlab;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MergeThread implements Callable<List<Integer>> {
	private List<Integer> first;
	private List<Integer> second;
	
	public void setFirst(List<Integer> first) {
		this.first = first;
	}

	public void setSecond(List<Integer> second) {
		this.second = second;
	}

	@Override
	public List<Integer> call() throws Exception {
		if(first == second) {
			return first;
		}
		
		//index for first list
		int index_1 = 0;
		//index for second list
		int index_2 = 0;
		
		List<Integer> result = new ArrayList<Integer>(first.size() + second.size());
		while(index_1 < first.size() && index_2 < second.size()) {
			if(first.get(index_1) < second.get(index_2)) {
				result.add(first.get(index_1));
				index_1++; 
			}else {
				if(first.get(index_1) > second.get(index_2)) {
					result.add(second.get(index_2));
					index_2++;
				} else {
					result.add(first.get(index_1));
					result.add(second.get(index_2));
					index_1++;
					index_2++;
				}
			}
		}
		
		if(index_1 < first.size()) {
			result.addAll(first.subList(index_1, first.size()));
		}else {
			result.addAll(second.subList(index_2, second.size()));
		}
		
		return result;
	}

}
