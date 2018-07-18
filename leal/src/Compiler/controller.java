package Compiler;
import java.util.Scanner;

import gui.*;

import java.io.*;

public class controller {
	public void playShape(Shape Shape, compiler Comp) {
		//ftype이 2,3면 부등호컴파일
		
		if(Shape.fType == Shape.DECLARE) //데이터 블록이면 선언
			Comp.declare(Shape.text);
		else if(Shape.fType == Shape.PROCESS)  //프로세스 블록이면 처리
			Comp.process(Shape.text);
	}
	
	public int playIneq(Shape Shape, compiler Comp) {
		return Comp.ineqCompile(Shape.text);
	}
	
}
