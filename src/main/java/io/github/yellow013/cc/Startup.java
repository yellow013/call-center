package io.github.yellow013.cc;

import java.io.IOException;

import io.github.yellow013.cc.component.CallDealer;
import io.github.yellow013.cc.component.CallProducer;
import io.github.yellow013.cc.component.ResultReceiver;
import io.github.yellow013.cc.util.Threads;

/**
 *  
 *
 */
public class Startup {

	public static void main(String[] args) {
		try (CallDealer dealer = new CallDealer();
				ResultReceiver receiver = new ResultReceiver();
				CallProducer producer = new CallProducer();) {
			dealer.start();
			receiver.start();
			producer.start();
			Threads.join();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
