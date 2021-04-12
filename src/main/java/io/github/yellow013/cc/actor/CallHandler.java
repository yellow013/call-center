package io.github.yellow013.cc.actor;

import java.util.function.Function;

import io.github.yellow013.cc.msg.Call;
import io.github.yellow013.cc.msg.CallResult;

@FunctionalInterface
public interface CallHandler extends Function<Call, CallResult> {

	CallResult onCall(Call call);

	@Override
	default CallResult apply(Call t) {
		return onCall(t);
	}

}
