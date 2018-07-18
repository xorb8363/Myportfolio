package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class RegisterPanel extends JPanel {
	
	static JLabel regName[] = new JLabel[10];
	static JLabel regValue[] = new JLabel[10];
	Font lbfont = new Font("맑은 고딕", Font.BOLD, 12);
	
	
	RegisterPanel() {
		setLayout(null);
		setSize(200,945);
		setVisible(true);
		setLocation(1080,25);
		setBackground(Color.white);
		
		JLabel title = new JLabel("변수 목록",JLabel.CENTER);
		title.setBounds(10,25,120,30);
		add(title);
		
		for(int i=0; i<regValue.length; i++) {
			regName[i] = new JLabel("",JLabel.CENTER);
			regName[i].setVisible(true);
			regName[i].setSize(120, 30);
			regName[i].setBackground(Color.white);
			regName[i].setOpaque(true);
			add(regName[i]);
			
			regValue[i] = new JLabel("",JLabel.CENTER);
			regValue[i].setVisible(true);
			regValue[i].setSize(120, 30);
			regValue[i].setBackground(Color.white);
			regValue[i].setOpaque(true);
			add(regValue[i]);
		}
		
		for(int i=0, j=70; i<regName.length; i++, j+=80) {
			regName[i].setLocation(10, j);
			regName[i].setBorder(new LineBorder(Color.gray,2));
			
			regValue[i].setLocation(10, j+30);
			regValue[i].setBorder(new LineBorder(Color.gray,2));
		}
		for(int i=0; i<getComponentCount(); i++)
			getComponent(i).setFont(lbfont);

	}
	
	//변수=값 꼴 스트링을 레이블에 차례차례 저장
	static public void setRegister(String input) {
		String [] arr = input.split("=");
		String getRegText;
		
		for(int i=0; i<regName.length; i++) {
			regName[i].setBackground(Color.white);
			regValue[i].setBackground(Color.white);
		}
		
		for(int i=0; i<regName.length; i++) {
			getRegText = regName[i].getText();
			if(getRegText.equals(arr[0])) {
				regValue[i].setText(arr[1]);
				regValue[i].setBackground(Color.yellow);
				break;
			}
			if(getRegText.equals("")) {
				regName[i].setText(arr[0]);
				regValue[i].setText(arr[1]);
				regName[i].setBackground(Color.yellow);
				regValue[i].setBackground(Color.yellow);
				break;
			}
		}
	}
	
	//재시작시 레이블 초기화
	static public void initRegister() {
		for(int i=0; i<regName.length; i++) {
			regName[i].setBackground(Color.white);
			regValue[i].setBackground(Color.white);
			regName[i].setText("");
			regValue[i].setText("");
		}
	}
}
