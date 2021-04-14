package io.github.yellow013.cc.actor;

import java.util.function.Consumer;

import io.github.yellow013.cc.msg.Call;

@FunctionalInterface
public interface CallHandler extends Consumer<Call> {

	void onCall(Call call);

	@Override
	default void accept(Call t) {
		onCall(t);
	}

}
