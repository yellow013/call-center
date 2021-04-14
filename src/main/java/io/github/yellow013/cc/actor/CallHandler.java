package io.github.yellow013.cc.actor;

import java.util.function.Predicate;

import io.github.yellow013.cc.msg.Call;

@FunctionalInterface
public interface CallHandler extends Predicate<Call> {

	boolean onCall(Call call);

	@Override
	default boolean test(Call t) {
		return onCall(t);
	}

}
