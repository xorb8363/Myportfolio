package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class viewManagement extends JFrame {
	static Login login;
	static Algorithm algorithm;
	
	viewManagement() {
		login = new Login();
		algorithm = new Algorithm();
		
		login.setVisible(true);
		algorithm.setVisible(false);
		
		//Container contentPane = getContentPane();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void changeView(String keyword) {
		if(keyword.equals("LOGIN")) {
			login.setVisible(true);
			algorithm.setVisible(false);
		} else if(keyword.equals("ALGORITHM")) {
			login.setVisible(false);
			algorithm.setVisible(true);
		}
	}
	
	public static void main(String args[]) {
		viewManagement vm = new viewManagement();
	}
}
