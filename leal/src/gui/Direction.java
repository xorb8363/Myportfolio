package gui;

public class Direction {
	public boolean isOn;
	public int isOrtho;
	public boolean ifYes;
	public boolean ifNo;
	
	Direction() {
		this.isOn = false;
		this.isOrtho = -1; // -1 : idle,,,,,,,,,,, 0 : 오른 -> 왼 / 아래 -> 위,,,,,,,,,, 1 : 왼 -> 오른 / 위 -> 아래
		this.ifYes = false;
		this.ifNo = false;
	}
	boolean isUsing() {
		if(this.isOrtho == -1)
			return false;
		else
			return true;
	}
	public void initDirection() {
		this.isOn = false;
		this.isOrtho = -1;
		this.ifYes = false;
		this.ifNo = false;
	}
}
