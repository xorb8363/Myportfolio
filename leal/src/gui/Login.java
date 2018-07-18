package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Login extends JFrame {
	Login() {
		Container contentPane = getContentPane();
		contentPane.setLayout(null);
		setSize(1000,700);
		setResizable(false);
		
		Dimension frameSize = this.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
		
		ImageIcon icon = new ImageIcon("src/images/background.png");
	      Image img = icon.getImage();
	      img = img.getScaledInstance(1000, 1000, Image.SCALE_SMOOTH);
	      icon.setImage(img);
	      
	      JLabel JLb = new JLabel(icon);
	      JLb.setBackground(Color.white);
	      JLb.setBounds(0,0,1000,980);
	      
	      
	      contentPane.add(JLb, new Integer(2));
	      
	      JButton b = new JButton("START!");
	      MyActionListener listener = new MyActionListener();
	      b.setBackground(new Color(255,204,0));
	      b.addActionListener(listener);
	      b.setSize(120,30);
	      b.setLocation(440,500);
	      contentPane.add(b);
	      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton b = (JButton)e.getSource();
			if(b.getText().equals("START!")) {
				viewManagement.changeView("ALGORITHM");
			}
		}
	}
}
