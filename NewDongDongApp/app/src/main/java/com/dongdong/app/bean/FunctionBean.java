package com.dongdong.app.bean;

public class FunctionBean {

	private String name;
	private int iconId;
	private int funcId;
	private int sequence;

	public FunctionBean() {
	}

	public FunctionBean(String name, int funcId, int sequence) {
		this.name = name;
		this.funcId = funcId;
		this.sequence = sequence;
	}

	public FunctionBean(int iconId, int sequence) {
		// this.name = name;
		this.iconId = iconId;
		this.sequence = sequence;
	}

	public int getFuncId() {
		return funcId;
	}

	public void setFuncId(int funcId) {
		this.funcId = funcId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@Override
	public String toString() {
		return "(name:" + name + ",sequence:" + sequence + ",iconId:"
				+ iconId+")";
	}
}
