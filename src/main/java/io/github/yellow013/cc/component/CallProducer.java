package io.github.yellow013.cc.component;

import io.github.yellow013.cc.util.Threads;

public class CallProducer implements Runnable{
	
	
	
	public CallProducer() {
		
	}
	
	@Override
	public void run() {
		for(;;) {
			
		}
	}
	
	public void start() {
		Threads.startNewThread(this);
	}

}
