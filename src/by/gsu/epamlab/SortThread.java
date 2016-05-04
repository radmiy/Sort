package by.gsu.epamlab;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class SortThread implements Callable<List<Integer>> {
	private List<Integer> sort;
	
	public void setSort(List<Integer> sort) {
		this.sort = sort;
	}

	@Override
	public List<Integer> call() throws Exception {
		quickSort(0, sort.size() - 1);
		return sort;
	}
	
	private void quickSort(int start, int end) {
        if (start >= end)
            return;
        int startIndex = start;
        int endIndex = end;
        int currentIndex = startIndex - (startIndex - endIndex) / 2;
        
        while (startIndex < endIndex) {
            while (sort.get(startIndex) <= sort.get(currentIndex) && startIndex < currentIndex) {
                startIndex++;
            }
            while (sort.get(currentIndex) <= sort.get(endIndex) && endIndex > currentIndex) {
                endIndex--;
            }
            if (startIndex < endIndex) {
            		Collections.swap(sort, startIndex, endIndex);
                if (startIndex == currentIndex)
                    currentIndex = endIndex;
                else if (endIndex == currentIndex)
                    currentIndex = startIndex;
            }
        }
        try{
        	quickSort(start, currentIndex);
        	quickSort(currentIndex+1, end);
        }catch (Exception e) {
        	System.out.println("startIndex=" + startIndex + " endIndex=" + endIndex);
        	throw new RuntimeException();
        }
    }
}
