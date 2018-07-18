package Compiler;

import java.util.regex.Pattern;

import gui.RegisterPanel;

public class compiler {
	
	public compiler() {
		for(int i=0; i<res.length; i++)
			res[i] = new register();
		for(int i=0; i<resArray.length; i++)
			resArray[i] = new registerArray();
	}
	
	protected void process(String input) {
		String res = calcProcessor(input);
		RegisterPanel.setRegister(res);
	}
	
	protected void declare(String input) {
		input = input.replaceAll(" ", "");
		input = input.trim();
		
		String tmp = commaSeparation(input);
		
		sepExpression(tmp);
		String res = setRegister(tmp);
		
		RegisterPanel.setRegister(res);
		
		if(input.length() != tmp.length())
			declare(input.substring(tmp.length()+1, input.length()));
	}
	
	protected int ineqCompile(String input) {
		return ineqProcessor(input);
	}
	
	private String commaSeparation(String input) {
		if(!input.contains(","))
			return input;
		String [] output = input.split(",");
		return output[0];
	}
	
	private void sepExpression(String input) { // 수식 구분하여 토큰화하고 레지스터에 변수명 등록
		int interval = 0;
		int interval_end = 0;
		input = input.replaceAll(" ", "");
		input = input.trim();
		for(int i = 0; i<input.length(); i++) {
			if(operator.contains(String.valueOf(input.charAt(i)))) {
				if(!Pattern.matches("^[0-9]*$", input.substring(interval,interval_end))) 
					sepTokenization(input.substring(interval,interval_end));
					interval = i+1;
					interval_end = i+1;
			} else if(bracket.contains(String.valueOf(input.charAt(i)))) {
				if(!Pattern.matches("^[0-9]*$", input.substring(interval,interval_end))) 
					sepTokenization(input.substring(interval,interval_end));
				interval = i+1;
				interval_end++;
			} else {
				interval_end++;
			}
		}
		if(interval<input.length())
			if(!operator.contains(String.valueOf(input.charAt(interval))))
				if(!Pattern.matches("^[0-9]*$", input.substring(interval,interval_end)))
					sepTokenization(input.substring(interval,interval_end));
		
		//예외 추가해야함 : 연산자가 두개이상 연달아 나올시, 연산자 뒤에 변수나 정수가 없을시, 연산자의 위치 오류 등
	}
	
	private String setRegister(String input) {
		String leftValue = "";
		String rightValue = "";
		int interval = 0;
		int interval_end = 0; 
		
		input = input.replaceAll(" ", "");
		input = input.trim();
		
		for(int i=0; i<input.length(); i++) {
			if(input.charAt(i) == '=') {
				leftValue = input.substring(interval,interval_end);
				break;
			} else 
				interval_end++;
		}
		
		rightValue = input.substring(++interval_end,input.length());

		if(leftValue.contains("[") && leftValue.contains("]")) {
			String varName = getNameToArrayToken(leftValue);
			int indexNumber = getSizeToArrayToken(leftValue);
			
			for(int i=0; i<resArray.length; i++) {
				if(varName.equals(resArray[i].getVarName())) {
					resArray[i].setMaxSize(indexNumber-1);
					for(int j=0; j<indexNumber; j++) 
						resArray[i].getMemoryList().add(0);
				}
			}
		}
		else 
			for(int i=0; i<res.length; i++) {
				if(leftValue.equals(res[i].getVarName())) {
					if(!Pattern.matches("^[0-9]*$", rightValue)) {
						for(int j=0; j<res.length; j++) {
							if(rightValue.equals(res[j].getVarName()))
								res[i].setMemory(res[j].getMemory());
						}
					} else
						res[i].setMemory(Integer.parseInt(rightValue));
					return res[i].getVarName() + "=" + res[i].getMemory();
					
				}
			}
		return "error=error";
	}
	
	private void sepTokenization(String input) { //토큰화된 변수 레지스터에 등록하기
		//배열의 경우
		if(input.contains("[") && input.contains("]")) { // []중에 하나가 빠지면 예외처리해야함
			String VarName = getNameToArrayToken(input);
			int maxSize = getSizeToArrayToken(input);
			
			for(int i=0; i<resArray.length; i++) {
				if(resArray[i].getVarName() == null) {
					resArray[i].setVarName(VarName);
					resArray[i].setMaxSize(maxSize);
					System.out.println("변수명 : " + resArray[i].getVarName() + " 배열크기 : " + resArray[i].getMaxSize());
					break;
				} else if(VarName.equals(resArray[i].getVarName()))
					break;
			}
		//일반 변수의 경우
		} else { 
			for(int i=0; i<res.length; i++) {
				if(res[i].getVarName() == null) {
					res[i].setVarName(input);
					break;
				} else if(input.equals(res[i].getVarName())) {
					break;
				}
			}
		}
		//예외 추가해야함 : 변수의 갯수 초과시	
	}
	
	private int getSizeToArrayToken(String input) { //배열 토큰에서 배열갯수 분리, int형 리턴
		int interval = 0;
		int interval_end = 0;
		String maxSize = "";
		
		for(int i=0; i<input.length(); i++) {
			if(arrayBracket.contains(String.valueOf(input.charAt(i)))) {
				maxSize = input.substring(++interval_end,input.length()-1);
				break;
			}
			else 
				interval_end++;
		}
		if(!Pattern.matches("^[0-9]*$", maxSize)) {
			for(int i=0; i<res.length; i++) {
				if(maxSize.equals(res[i].getVarName()))
						maxSize = Integer.toString(res[i].getMemory());
				
			}
		}
		return Integer.parseInt(maxSize);
	}
	
	private String getNameToArrayToken(String input) { //배열 토큰에서 배열이름 분리, string형 리턴
		int interval = 0;
		int interval_end = 0;
		String VarName = "";
		
		for(int i=0; i<input.length(); i++) {
			if(arrayBracket.contains(String.valueOf(input.charAt(i)))) {
				VarName = input.substring(interval, interval_end++);
				break;
			}
			else 
				interval_end++;
		}
		return VarName;
	}

	private String calcProcessor(String inpix) {
		String leftValue = "";
		String postpix = "";
		int interval = 0;
		int interval_end = 0; 
		
		inpix = inpix.replaceAll(" ", "");
		inpix = inpix.trim();
		
		for(int i=0; i<inpix.length(); i++) {
			if(inpix.charAt(i) == '=') {
				leftValue = inpix.substring(interval,interval_end);
				break;
			} else 
				interval_end++;
		}
		
		postpix = calcPreProcessor(inpix.substring(interval_end,inpix.length()));
		String [] arr = postpix.split(" ");
		postpix = "";
		
		for(int i=0; i<arr.length; i++) {
			if(arr[i].contains("[") && arr[i].contains("]")) {
				String varName = getNameToArrayToken(arr[i]);
				int indexNumber = getSizeToArrayToken(arr[i]);
				for(int j=0; j<resArray.length; j++) {
					if(varName.equals(resArray[i].getVarName())) {
						arr[j] = Integer.toString(resArray[i].getMemoryList().get(indexNumber));
						break;
					}
				}
			} else {
				for(int j=0; j<res.length; j++) {
					if(arr[i].equals(res[j].getVarName())) 
						arr[i] = Integer.toString(res[j].getMemory());
					else if(res[j].getVarName() == null)
						break;
				}
			}
		}
		
		for(int j=0; j<arr.length; j++)
			postpix = postpix + arr[j] + " ";
		
		if(leftValue.contains("[") && leftValue.contains("]")) { // 배열변수일 때
			String VarName = getNameToArrayToken(leftValue);
			int indexNumber = getSizeToArrayToken(leftValue);
			
			for(int i=0; i<resArray.length; i++) {
				if(VarName.equals(resArray[i].getVarName())) {
					resArray[i].getMemoryList().set(indexNumber, calculator.evaluate(postpix));
					System.out.println(resArray[i].getMemoryList());
				}
			}
		} else
			for(int i=0; i<res.length; i++) { //우변 결과 좌변에 대입
			if(leftValue.equals(res[i].getVarName())) { //일반 변수일 때
				res[i].setMemory(calculator.evaluate(postpix));
				return res[i].getVarName() +"="+ res[i].getMemory(); //테스트용
			}
		}
		return ""; 
	}	
	
	private String calcPreProcessor(String input) {
		String result = "";
		int interval = 0;
		int interval_end = 0;
		
		for(int i=interval; i<input.length(); i++) {
			if(operator.contains(String.valueOf(input.charAt(i))) || bracket.contains(String.valueOf(input.charAt(i)))) {
				result = result + input.substring(interval,interval_end) + " " + input.charAt(i) + " ";
				interval = i+1;
				interval_end = i+1;
			} else {
				interval_end++;
			}
		}
		if(interval<input.length())
			if(!operator.contains(String.valueOf(input.charAt(interval))) || !bracket.contains(String.valueOf(input.charAt(interval))))
				result = result + input.substring(interval,interval_end);
		result = result.replaceAll("  ", " ");
		result = result.trim();
		
		return result;
	}
	
	private int ineqProcessor(String input) {
		String leftValue = "";
		String rightValue = "";
		String center = "";
		
		int result;
		int interval = 0;
		int interval_end = 0;
		
		input = input.replaceAll(" ", "");
		input = input.trim();
		
		for(int i=0; i<input.length(); i++) {
			if(input.charAt(i) == '<' || input.charAt(i) == '>') {
				leftValue = input.substring(interval,interval_end);
				break;
			} else 
				interval_end++;
		}
		center = input.substring(interval_end++, interval_end);
		rightValue = input.substring(interval_end, input.length());
		
		leftValue = calcPreProcessor(leftValue);
		rightValue = calcPreProcessor(rightValue);
		
		String leftArr [] = leftValue.split(" ");
		String rightArr [] = rightValue.split(" ");
		leftValue = rightValue = "";
		
		for(int i=0; i<res.length; i++) {
			if(res[i].getVarName() != null) {
				for(int j=0; j<leftArr.length; j++) {
					if(leftArr[j].equals(res[i].getVarName()))
						leftArr[j] = Integer.toString(res[i].getMemory());
				}
				for(int j=0; j<rightArr.length; j++) {
					if(rightArr[j].equals(res[i].getVarName()))
						rightArr[j] = Integer.toString(res[i].getMemory());
				}
			} else 
				break;
		}
		for(int i=0; i<resArray.length; i++) {
			if(resArray[i].getVarName() != null) {
				for(int j=0; j<leftArr.length; j++) {
					
				}
			}
		}
		
		for(int j=0; j<leftArr.length; j++)
			leftValue = leftValue + leftArr[j] + " ";
		for(int j=0; j<rightArr.length; j++)
			rightValue = rightValue + rightArr[j] + " ";
	
		System.out.print(calculator.evaluate(leftValue)+ center + calculator.evaluate(rightValue));//테스트문
		result = calculator.compare(calculator.evaluate(leftValue), center, calculator.evaluate(rightValue));

		if(result==0)
			System.out.println(" false"); //테스트문
		else if(result == 1)
			System.out.println(" true");
		
		return result;
	}
	
	public void initRegister() {
		for(int i=0; i<res.length; i++) {
			res[i].setMemory(0);
			res[i].setVarName(null);
		}
	}
	
	final String operator = "+-/*="; // 연산자 집합
	final String ineq = "<>";
	final String bracket = "()"; // 괄호 집합
	final String arrayBracket = "[]";
	
	private register[] res = new register[10]; //가상의 레지스터 10개
	private registerArray[] resArray = new registerArray[5];
	
}
