package io.github.yellow013.cc.actor;

import io.github.yellow013.cc.msg.Call;
import io.github.yellow013.cc.msg.CallResult;

public class Employees implements CallHandler {

	private final String name;

	public Employees(int no) {
		this.name = "employees[" + no + "]";
	}

	@Override
	public CallResult onCall(Call call) {
		
		return null;
	}

}
