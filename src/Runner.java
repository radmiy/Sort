import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import by.gsu.epamlab.MergeThread;
import by.gsu.epamlab.SortThread;

public class Runner {

	public static void main(String[] args) {
		List<Integer> randomValuesList = new ArrayList<Integer>();
		Random random = new Random();
		final int ARRAY_SIZE = 1000000;
		final int THREADS_NUMBER = 150;
		/* more scatter works best (RANDOM_RANGE = NUMBER_ARRAY)*/
		final int RANDOM_RANGE = 100;
		
		System.out.println("Create array of " + ARRAY_SIZE + " elements.");
		for(int index = 0; index < ARRAY_SIZE; index++) {
			randomValuesList.add(random.nextInt(RANDOM_RANGE));
		}
		
		List<Integer> systemSortArray = new ArrayList<>(randomValuesList);
		System.out.println("DONE!");
		System.out.println();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss.SSS");
		Date start = new Date();
		Collections.sort(systemSortArray);
		Date stop = new Date();
		Date resume = new Date(stop.getTime() - start.getTime());
		System.out.println("Collections.sort ran " + simpleDateFormat.format(resume));
	
		ExecutorService executorService = Executors.newCachedThreadPool();
		List<Callable<List<Integer>>> sortThreads = new ArrayList<Callable<List<Integer>>>();
		
		int min = ARRAY_SIZE / THREADS_NUMBER;
		for(int threadNum = 0; threadNum < ARRAY_SIZE; threadNum+=min) {
			min = threadNum + min > randomValuesList.size() ? randomValuesList.size() - threadNum : min;
			
			List<Integer> run = new ArrayList<Integer>(randomValuesList.subList(threadNum, threadNum + min));
			SortThread sortThread = new SortThread();
			sortThread.setSort(run);
			sortThreads.add(sortThread);
		}
		
		List<Integer> sortingValuesList = new ArrayList<>(ARRAY_SIZE);
		
		start = new Date();
		System.out.println();
		
		List<Future<List<Integer>>> futures = new ArrayList<>(sortThreads.size());
		
		try {
			futures = executorService.invokeAll(sortThreads);
			for(Future<List<Integer>> future : futures) {
				sortingValuesList.addAll(future.get());
			}
			
			while(futures.size() != 1) {
				int threadsNum = 0;
				List<Callable<List<Integer>>> mergeThreads = new ArrayList<Callable<List<Integer>>>();
				List<Integer> first = null;
				for(Future<List<Integer>> future : futures) {
					if(threadsNum % 2 == 0) {
						first = future.get();
					}else {
						MergeThread mergeThread = new MergeThread();
						mergeThread.setFirst(first);
						mergeThread.setSecond(future.get());
						mergeThreads.add(mergeThread);
					}
					threadsNum++;
				}
				
				if(threadsNum % 2 == 1) {
					MergeThread mergeThread = new MergeThread();
					mergeThread.setFirst(first);
					mergeThread.setSecond(first);
					mergeThreads.add(mergeThread);
				}
				
				futures = executorService.invokeAll(mergeThreads);
			}
			stop = new Date();
			resume = new Date(stop.getTime() - start.getTime());
			System.out.println("Sorting random values ran " + simpleDateFormat.format(resume));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		try {
			List<Integer> resultList = futures.get(0).get();
			for(int index = 0; index < systemSortArray.size(); index++) {
				int systemValuesArray = systemSortArray.get(index);
				int randomValuesArray = resultList.get(index);
				if(randomValuesArray != systemValuesArray) {
					System.out.println("Error!!! Not sorted! " + index);
					break;
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}finally {
			executorService.shutdown();
		}
	}
}
