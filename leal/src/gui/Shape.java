package gui;

public class Shape {
	public String text;
	public int fType; // process=1 decision=2 data=4
	
	Shape() {
		text = "";
		fType = 0x00;
	}
	boolean isShape() {
		if(this.fType < 0x0100)
			return true;
		else
			return false;
	}
	void initShape() {
		this.fType = 0x00;
		this.text = "";
	}
	void setfType(String input) {
		if(input.equals("declare"))
			this.fType = DECLARE;
		else if (input.equals("process"))
			this.fType = PROCESS;
		else if (input.equals("output"))
			this.fType = OUTPUT;
		else if (input.equals("condition"))
			this.fType = CONDITION;
		else if (input.equals("vertical"))
			this.fType = VERTICAL;
		else if (input.equals("horizontal"))
			this.fType = HORIZONTAL;
		else if (input.equals("line1"))
			this.fType = LINE1;
		else if (input.equals("line2"))
			this.fType = LINE2;
		else if (input.equals("line3"))
			this.fType = LINE3;
		else if (input.equals("line4"))
			this.fType = LINE4;
		else if (input.equals("end"))
			this.fType = END;
		else
			System.out.println("WARNING - Shape 일치값 없음");
	}
	public static String toStringfType(int fType) {
		if(fType == PROCESS)
			return "process";
		if(fType == CONDITION)
			return "condition";
		if(fType == OUTPUT)
			return "output";
		if(fType == DECLARE)
			return "declare";
		if(fType == VERTICAL)
			return "vertical";
		if(fType == HORIZONTAL)
			return "horizontal";
		if(fType == LINE1)
			return "line1";
		if(fType == LINE2)
			return "line2";
		if(fType == LINE3)
			return "line3";
		if(fType == LINE4)
			return "line4";
		if(fType == END)
			return "end";
		else
			return "ERROR OCCURED";
	}
	//도형은 0x0001 ~ 0x00FF까지
	public static final int PROCESS = 0x0001;
	public static final int CONDITION = 0x0002;
	public static final int OUTPUT = 0x0003;
	public static final int DECLARE = 0x0004;
	
	//화살표는 0x0100 ~ 0xFF00까지
	public static final int VERTICAL = 0x0100;
	public static final int HORIZONTAL = 0x0200;
	public static final int LINE1 = 0x0300;
	public static final int LINE2 = 0x0400;
	public static final int LINE3 = 0x0500;
	public static final int LINE4 = 0x0600;
	public static final int END = 0x0700;
}