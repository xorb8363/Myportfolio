package Compiler;

public class register {
	private int memory; //레지스터 저장공간
	private String varName; //변수이름

	register() {
		this.varName = null;
		this.memory = 0;
	}
	
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
}
