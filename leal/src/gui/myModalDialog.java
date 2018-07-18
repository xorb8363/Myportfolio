package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class myModalDialog extends JDialog {
	JTextField tf = new JTextField(20);
	JButton okButton = new JButton("OK");
	Font lbfont = new Font("맑은 고딕", 25, 20);

	public myModalDialog(JFrame frame, String title) {
		super(frame, title, true);
		
		Dimension frameSize = this.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		setLayout(new FlowLayout());
		add(tf);
		add(okButton);
		tf.setFont(lbfont);
		setSize(500, 100);

		tf.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					setVisible(false);
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
		});

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	String getInput() {
		String tmp = tf.getText();
		tf.setText("");
		if (tmp.length() == 0)
			return null;
		else
			return tmp;
	}
}