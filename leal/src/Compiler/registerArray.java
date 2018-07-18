package Compiler;

import java.util.*;

public class registerArray {
	private List<Integer> memoryList;//arraylist
	private int maxSize;//maxsize
	private String varName;//varname
	
	registerArray() {
		this.memoryList = null;
		this.varName = null;
		this.memoryList = new ArrayList<Integer>();
	}

	public List<Integer> getMemoryList() {
		return memoryList;
	}

	public void setMemoryList(List<Integer> memoryList) {
		this.memoryList = memoryList;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}
}
