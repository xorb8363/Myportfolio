package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.EmptyStackException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.*;

import Compiler.*;

/*
 * 이 소스코드의 중요한 점은 JComponent들을 동적 생성/소멸 과정을 하기 위하여
 * 각 컴포넌트들에게 이름을 부여, 그 이름을 통하여 접근하고 생성 소멸을 한다.
 * 네이밍 방식은 (객체종류)_(x좌표)_(y좌표)이며   ex) shape_0_1
 * 소스코드를 이해하기 위해서는 이 탐색방식에 익숙해져야한다.
 */

public class Algorithm extends JFrame {

	String[] shapeName = { "declare", "process", "output", "condition", "delete", "end" };
	String[] lineArrow = { "vertical", "horizontal", "line1", "line2", "line3", "line4" };
	String[] rightArrow = {};
	String[] directionName = { "right", "left", "down", "up", "idle" };
	String[] ynName = { "YES", "NO", "DEL" };
	String[] executeSpeed = { "하나씩", "느리게", "보통", "빠르게", "즉시" };
	String[] Start_End = { "start"};
	//String[] Start_End = { "start", "end" };
	// 도형 도우미
	JLabel selectShape;
	String shapeString = "";

	// 방향 도우미
	JLabel selectArrow;
	String arrowString = "";

	myModalDialog dialog;
	Timer timer;
	String exeSpeedString = "";
	Font lbfont = new Font("맑은 고딕", Font.BOLD, 12);
	

	// 최대 행과 열
	public final int ROW_NUMBER = 8;
	public final int COLUMN_NUMBER = 3;
	Point startPoint;

	// 사용자가 UI 조작시 실시간으로 저장할 배열의 종류
	boolean isUsingBlock[][];
	Direction directionLR[][] = new Direction[ROW_NUMBER][COLUMN_NUMBER - 1];
	Direction directionUD[][] = new Direction[ROW_NUMBER - 1][COLUMN_NUMBER];
	Shape shape[][] = new Shape[ROW_NUMBER][COLUMN_NUMBER];
	
	Border yellowBorder = BorderFactory.createLineBorder(Color.yellow,3);
	Border redBorder = BorderFactory.createLineBorder(Color.red,3);
	

	Boolean playCheck;

	static JLabel nowPoint;

	// 사용할 컴파일러 모듈
	compiler comp;
	controller cont;

	Algorithm() {
		super("LEAL 프로그램");

		JLayeredPane contentPane = getLayeredPane();
		contentPane.setLayout(null);
		setSize(1280, 960);
		setResizable(false);
		Point startPoint = new Point(0, 1);
		dialog = new myModalDialog(this, "값 입력 창");
		dialog.setResizable(false);
		isUsingBlock = new boolean[ROW_NUMBER][COLUMN_NUMBER];
		isUsingBlock[0][1] = true; // start point
		Dimension frameSize = this.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		playCheck = false;
		
		 ImageIcon icon1 = new ImageIcon("src/images/LEAL.png");
	      super.getIconImage();
	      setIconImage(icon1.getImage());

		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem OpenItem = new JMenuItem("Open");
		OpenItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				JFileChooser fileChooser = new JFileChooser();

				fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "//save"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("leal 파일", "leal");
				fileChooser.addChoosableFileFilter(filter);
				if (cmd.equals("Open")) {
					int result = fileChooser.showOpenDialog(null);
					System.out.println(result);

					if (result == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();

						for (int i = 0; i < ROW_NUMBER; i++)
							for (int j = 0; j < COLUMN_NUMBER; j++)
								isUsingBlock[i][j] = false;
						isUsingBlock[0][1] = true;

						for (int i = 0; i < ROW_NUMBER; i++)
							for (int j = 0; j < COLUMN_NUMBER; j++)
								shape[i][j].initShape();
						for (int i = 0; i < contentPane.getComponentCount(); i++) {
							String Stmp = contentPane.getComponent(i).getName();
							if (Stmp != null) {
								if (Stmp.contains("shape"))
									contentPane.remove(i--);
								if (Stmp.contains("label"))
									contentPane.remove(i--);
								if (Stmp.contains("YN"))
									contentPane.remove(i--);
							}
						}

						for (int i = 0; i < ROW_NUMBER; i++)
							for (int j = 0; j < COLUMN_NUMBER - 1; j++)
								directionLR[i][j].initDirection();
						for (int i = 0; i < ROW_NUMBER - 1; i++)
							for (int j = 0; j < COLUMN_NUMBER; j++)
								directionUD[i][j].initDirection();
						for (int i = 0; i < contentPane.getComponentCount(); i++) {
							String Stmp = contentPane.getComponent(i).getName();
							if (Stmp != null) {
								if (Stmp.contains("direction")) {
									ImageIcon icon = new ImageIcon("src/images/idle.png");
									Image img = icon.getImage();
									img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
									icon.setImage(img);
									((JButton) contentPane.getComponent(i)).setIcon(icon);
									contentPane.getComponent(i).setVisible(false);
								}
							}
						}
						RegisterPanel.initRegister();
						comp.initRegister();
						startPoint.x = 0;
						startPoint.y = 1;
						nowPoint.setVisible(false);

						BufferedReader reader = null;
						String line = null;
						String authen = "!!$$LEAL_PROGRAM_EXTENSION_AUTHENTICATION$$!!";
						String version = "ALPHA";
						boolean isOk = true;

						try {
							reader = new BufferedReader(new FileReader(selectedFile));
							line = reader.readLine();
							if (!line.equals(authen)) {
								JOptionPane.showMessageDialog(null, "LEAL 알고리듬 파일이 아닙니다.", "열기 오류",
										JOptionPane.ERROR_MESSAGE);
								isOk = false;
							}

							line = reader.readLine();
							if (!line.contains(version))
								JOptionPane.showMessageDialog(null, "주의 - " + version + "버전의 파일이 아닙니다.", "경고",
										JOptionPane.WARNING_MESSAGE);

							if (isOk) {
								String checkpart = "";
								int i = 0;
								while ((line = reader.readLine()) != null) {
									if (line.equals("!!START_isUsingBlock")) {
										checkpart = line;
										i = 0;
										continue;
									}
									if (line.equals("!!START_shape")) {
										checkpart = line;
										i = 0;
										continue;
									}
									if (line.equals("!!START_directionLR")) {
										checkpart = line;
										i = 0;
										continue;
									}
									if (line.equals("!!START_directionUD")) {
										checkpart = line;
										i = 0;
										continue;
									}

									if (checkpart.equals("!!START_isUsingBlock")) {
										String[] str = line.split("/");
										for (int j = 0; j < COLUMN_NUMBER; j++) {
											if (str[j].equals("true"))
												isUsingBlock[i][j] = true;
											else if (str[j].equals("false"))
												isUsingBlock[i][j] = false;
										}
										i++;
									}
									if (checkpart.equals("!!START_shape")) {
										String[] str = line.split("/");
										for (int j = 0; j < COLUMN_NUMBER; j++) {
											String[] str2 = str[j].split("@");
											if (!str2[0].equals("0"))
												for (int n = 0; n < contentPane.getComponentCount(); n++) {
													String tmp = contentPane.getComponent(n).getName();
													if (tmp != null && tmp.contains("block")) {
														Point p = new Point();
														p = underBarSplit(tmp);
														if (p.x == i && p.y == j) {
															Point btnLocation = new Point();
															btnLocation = contentPane.getComponent(n).getLocation();
															// btn create task

															shape[i][j].fType = Integer.parseInt(str2[0]);

															ImageIcon icon = new ImageIcon("src/images/"
																	+ Shape.toStringfType(shape[i][j].fType) + ".png");
															Image img = icon.getImage();
															img = img.getScaledInstance(180, 72, Image.SCALE_SMOOTH);
															icon.setImage(img);

															JButton btn = new JButton(icon);
															btn.setBackground(Color.white);
															btn.setName("shape_" + i + "_" + j);
															btn.setBorderPainted(false);

															btn.setBounds(btnLocation.x - 80, btnLocation.y - 21, 180,
																	72);
															btn.setVisible(true);
															contentPane.add(btn, new Integer(1));

															btn.addActionListener(new ActionListener() {
																public void actionPerformed(ActionEvent e) {
																	/*
																	 * 도형버튼을 눌러서 값 변경, 값은 새로운 Label을 계층구조로 생성하여 나타냄. 중복시
																	 * 기존의 레이블 객체를 찾아내어 값을 변경하는 과정을 통해 수정기능 구현 삭제 미구현
																	 * 상태라 주석처리
																	 */

																	if (shapeString != null
																			&& !shapeString.equals("delete")) {
																		String tmp = btn.getName().replaceAll("shape",
																				"label");
																		JLabel JLtmp = new JLabel();
																		Boolean overLap = false;
																		Point p = new Point();
																		p = underBarSplit(tmp);

																		if (shape[p.x][p.y].isShape()) {
																			for (int i = 0; i < contentPane
																					.getComponentCount(); i++) {
																				String Stmp = contentPane
																						.getComponent(i).getName();
																				if (Stmp != null)
																					if (Stmp.equals(tmp)) {
																						JLtmp = (JLabel) contentPane
																								.getComponent(i);
																						overLap = true;
																					}
																			}
																			if (!overLap) {
																				JLtmp = new JLabel("", JLabel.CENTER);
																				JLtmp.setBounds(btn.getBounds());
																				JLtmp.setName(tmp);
																			}

																			dialog.tf.setText(JLtmp.getText());
																			dialog.setVisible(true);
																			JLtmp.setText(dialog.getInput());
																			shape[p.x][p.y].text = JLtmp.getText();
																			contentPane.requestFocus();

																			contentPane.add(JLtmp, new Integer(2));
																			contentPane.revalidate();
																			contentPane.repaint();
																		}
																	} else if (shapeString != null
																			&& shapeString.equals("delete")) {

																		String tmp = btn.getName();
																		String tmp2 = btn.getName().replaceAll("shape",
																				"label");
																		Point p = new Point();
																		p = underBarSplit(tmp);

																		for (int i = 0; i < contentPane
																				.getComponentCount(); i++) {
																			String Stmp = contentPane.getComponent(i)
																					.getName();
																			if (Stmp != null) {
																				if (Stmp.equals(tmp))
																					contentPane.remove(i);
																				else if (Stmp.equals(tmp2))
																					contentPane.remove(i);
																				else if (Stmp.contains("YN"))
																					contentPane.remove(i);
																			}
																		}

																		shape[p.x][p.y].fType = 0x00;
																		shape[p.x][p.y].text = "";

																		boolean aroundCheck = true;
																		boolean aroundCheck2 = true;
																		boolean aroundCheck3 = true;
																		boolean aroundCheck4 = true;

																		// 화살표 객체 off
																		if (p.x > 0) {
																			isUsingBlock[p.x - 1][p.y] = false;
																			if (shape[p.x - 1][p.y].fType == 0x00) {
																				aroundCheck = false;
																				directionUD[p.x - 1][p.y]
																						.initDirection();
																				for (int i = 0; i < contentPane
																						.getComponentCount(); i++) {
																					tmp = contentPane.getComponent(i)
																							.getName();
																					if (tmp != null && tmp
																							.contains("directionUD")) {
																						Point p2 = new Point();
																						p2 = underBarSplit(tmp);
																						if (p.x - 1 == p2.x
																								&& p.y == p2.y) {
																							ImageIcon icon = new ImageIcon(
																									"src/images/idle.png");
																							Image img = icon.getImage();
																							img = img.getScaledInstance(
																									20, 20,
																									Image.SCALE_SMOOTH);
																							icon.setImage(img);
																							((JButton) contentPane
																									.getComponent(i))
																											.setIcon(
																													icon);
																						}
																					}
																				}
																			}
																		} else
																			aroundCheck = false;

																		if (p.x < ROW_NUMBER - 1) {
																			isUsingBlock[p.x + 1][p.y] = false;
																			if (shape[p.x + 1][p.y].fType == 0x00) {
																				aroundCheck2 = false;
																				directionUD[p.x][p.y].initDirection();
																				for (int i = 0; i < contentPane
																						.getComponentCount(); i++) {
																					tmp = contentPane.getComponent(i)
																							.getName();
																					if (tmp != null && tmp
																							.contains("directionUD")) {
																						Point p2 = new Point();
																						p2 = underBarSplit(tmp);
																						if (p.x == p2.x
																								&& p.y == p2.y) {
																							ImageIcon icon = new ImageIcon(
																									"src/images/idle.png");
																							Image img = icon.getImage();
																							img = img.getScaledInstance(
																									20, 20,
																									Image.SCALE_SMOOTH);
																							icon.setImage(img);
																							((JButton) contentPane
																									.getComponent(i))
																											.setIcon(
																													icon);
																						}
																					}
																				}
																			}
																		} else
																			aroundCheck2 = false;
																		if (p.y > 0) {
																			isUsingBlock[p.x][p.y - 1] = false;
																			if (shape[p.x][p.y - 1].fType == 0x00) {
																				aroundCheck3 = false;
																				directionLR[p.x][p.y - 1]
																						.initDirection();
																				for (int i = 0; i < contentPane
																						.getComponentCount(); i++) {
																					tmp = contentPane.getComponent(i)
																							.getName();
																					if (tmp != null && tmp
																							.contains("directionLR")) {
																						Point p2 = new Point();
																						p2 = underBarSplit(tmp);
																						if (p.x == p2.x
																								&& p.y - 1 == p2.y) {
																							ImageIcon icon = new ImageIcon(
																									"src/images/idle.png");
																							Image img = icon.getImage();
																							img = img.getScaledInstance(
																									20, 20,
																									Image.SCALE_SMOOTH);
																							icon.setImage(img);
																							((JButton) contentPane
																									.getComponent(i))
																											.setIcon(
																													icon);
																						}
																					}
																				}
																			}
																		} else
																			aroundCheck3 = false;
																		if (p.y < COLUMN_NUMBER - 1) {
																			isUsingBlock[p.x][p.y + 1] = false;
																			if (shape[p.x][p.y + 1].fType == 0x00) {
																				aroundCheck4 = false;
																				directionLR[p.x][p.y].initDirection();
																				for (int i = 0; i < contentPane
																						.getComponentCount(); i++) {
																					tmp = contentPane.getComponent(i)
																							.getName();
																					if (tmp != null && tmp
																							.contains("directionLR")) {
																						Point p2 = new Point();
																						p2 = underBarSplit(tmp);
																						if (p.x == p2.x
																								&& p.y == p2.y) {
																							ImageIcon icon = new ImageIcon(
																									"src/images/idle.png");
																							Image img = icon.getImage();
																							img = img.getScaledInstance(
																									20, 20,
																									Image.SCALE_SMOOTH);
																							icon.setImage(img);
																							((JButton) contentPane
																									.getComponent(i))
																											.setIcon(
																													icon);
																						}
																					}
																				}
																			}
																		} else
																			aroundCheck4 = false;
																		// block 객체 off

																		if (p.x > 1)
																			if (shape[p.x - 2][p.y].fType != 0x00)
																				isUsingBlock[p.x - 1][p.y] = true;
																		if (p.x > 0 && p.y > 0)
																			if (shape[p.x - 1][p.y - 1].fType != 0x00) {
																				isUsingBlock[p.x - 1][p.y] = true;
																				isUsingBlock[p.x][p.y - 1] = true;
																			}
																		if (p.x > 0 && p.y < COLUMN_NUMBER - 1)
																			if (shape[p.x - 1][p.y + 1].fType != 0x00) {
																				isUsingBlock[p.x - 1][p.y] = true;
																				isUsingBlock[p.x][p.y + 1] = true;
																			}
																		if (p.x < ROW_NUMBER - 2)
																			if (shape[p.x + 2][p.y].fType != 0x00)
																				isUsingBlock[p.x + 1][p.y] = true;
																		if (p.x < ROW_NUMBER - 1 && p.y > 0)
																			if (shape[p.x + 1][p.y - 1].fType != 0x00) {
																				isUsingBlock[p.x][p.y - 1] = true;
																				isUsingBlock[p.x + 1][p.y] = true;
																			}
																		if (p.x < ROW_NUMBER - 1
																				&& p.y < COLUMN_NUMBER - 1)
																			if (shape[p.x + 1][p.y + 1].fType != 0x00) {
																				isUsingBlock[p.x][p.y + 1] = true;
																				isUsingBlock[p.x + 1][p.y] = true;
																			}
																		if (!aroundCheck && !aroundCheck2
																				&& !aroundCheck3 && !aroundCheck4)
																			isUsingBlock[p.x][p.y] = false;
																		else
																			isUsingBlock[p.x][p.y] = true;

																		for (int i = 0; i < contentPane
																				.getComponentCount(); i++) {
																			String Stmp = contentPane.getComponent(i)
																					.getName();
																			if (Stmp != null) {
																				if (Stmp.contains("shape")) {
																					p = new Point();
																					p = underBarSplit(Stmp);
																					if (shape[p.x][p.y].fType != 0x00)
																						isUsingBlock[p.x][p.y] = false;
																				}

																			}
																		}

																		for (int i = 0; i < contentPane
																				.getComponentCount(); i++) {
																			tmp = contentPane.getComponent(i).getName();
																			p = new Point();
																			if (tmp != null
																					&& tmp.contains("directionLR")) {
																				p = underBarSplit(tmp);
																				if (!directionLR[p.x][p.y].isUsing())
																					contentPane.getComponent(i)
																							.setVisible(false);
																			}
																			if (tmp != null
																					&& tmp.contains("directionUD")) {
																				p = underBarSplit(tmp);
																				if (!directionUD[p.x][p.y].isUsing())
																					contentPane.getComponent(i)
																							.setVisible(false);
																			}
																		}

																		boolean nothingCheck = true;
																		
																		for(int i=0; i<ROW_NUMBER; i++) 
																			for(int j=0; j<COLUMN_NUMBER; j++)
																				if(isUsingBlock[i][j])
																					nothingCheck = false;
																		
																		
																		if(nothingCheck)
																			isUsingBlock[0][1] = true;

																		selectShape.setText("");
																		shapeString = "";

																		contentPane.revalidate();
																		contentPane.repaint();
																	}
																}
															});
															if (!str2[1].equals("null")) {
																JLabel JLb = new JLabel("", JLabel.CENTER);
																JLb.setName(btn.getName().replaceAll("shape", "label"));
																JLb.setBounds(btn.getBounds());
																JLb.setText(str2[1]);

																contentPane.add(JLb, new Integer(2));
																shape[i][j].text = str2[1];
																n++;
															}
														}
													}
												}
										}
										i++;
									}
									if (checkpart.equals("!!START_directionLR")) {
										String[] str = line.split("/");
										for (int j = 0; j < COLUMN_NUMBER - 1; j++) {
											String[] str2 = str[j].split(",");
											for (int n = 0; n < contentPane.getComponentCount(); n++) {
												String tmp = contentPane.getComponent(n).getName();
												if (tmp != null && tmp.contains("directionLR")) {
													Point p = new Point();
													p = underBarSplit(tmp);
													if (p.x == i && p.y == j) {
														directionLR[i][j].isOn = Boolean.parseBoolean(str2[0]);
														directionLR[i][j].isOrtho = Integer.parseInt(str2[1]);
														directionLR[i][j].ifYes = Boolean.parseBoolean(str2[2]);
														directionLR[i][j].ifNo = Boolean.parseBoolean(str2[3]);

														ImageIcon icon = new ImageIcon();

														if (directionLR[i][j].isOrtho == 1)
															icon = new ImageIcon("src/images/right.png");
														else if (directionLR[i][j].isOrtho == 0)
															icon = new ImageIcon("src/images/left.png");
														else
															icon = new ImageIcon("src/images/idle.png");

														if (directionLR[i][j].ifYes || directionLR[i][j].ifNo) {
															String sTmp = contentPane.getComponent(n).getName()
																	.replaceAll("direction", "YN");
															JLabel JLtmp = new JLabel("", JLabel.CENTER);
															JLtmp.setName(sTmp);
															Rectangle r = new Rectangle();
															r = contentPane.getComponent(n).getBounds();
															r.x = r.x - 5;
															r.y = r.y + 20;
															r.width = r.width + 10;
															JLtmp.setBounds(r);

															if (directionLR[i][j].ifYes)
																JLtmp.setText("YES");
															else if (directionLR[i][j].ifNo)
																JLtmp.setText("NO");
															JLtmp.setVisible(true);
															contentPane.add(JLtmp, new Integer(2));
															n++;
														}

														Image img = icon.getImage();
														img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
														icon.setImage(img);

														((JButton) contentPane.getComponent(n)).setIcon(icon);
													}
												}
											}
										}
										i++;
									}
									if (checkpart.equals("!!START_directionUD")) {
										String[] str = line.split("/");
										for (int j = 0; j < COLUMN_NUMBER; j++) {
											String[] str2 = str[j].split(",");
											for (int n = 0; n < contentPane.getComponentCount(); n++) {
												String tmp = contentPane.getComponent(n).getName();
												if (tmp != null && tmp.contains("directionUD")) {
													Point p = new Point();
													p = underBarSplit(tmp);
													if (p.x == i && p.y == j) {
														directionUD[i][j].isOn = Boolean.parseBoolean(str2[0]);
														directionUD[i][j].isOrtho = Integer.parseInt(str2[1]);
														directionUD[i][j].ifYes = Boolean.parseBoolean(str2[2]);
														directionUD[i][j].ifNo = Boolean.parseBoolean(str2[3]);

														ImageIcon icon = new ImageIcon();

														if (directionUD[i][j].isOrtho == 1)
															icon = new ImageIcon("src/images/down.png");
														else if (directionUD[i][j].isOrtho == 0)
															icon = new ImageIcon("src/images/up.png");
														else
															icon = new ImageIcon("src/images/idle.png");

														if (directionUD[i][j].ifYes || directionUD[i][j].ifNo) {
															String sTmp = contentPane.getComponent(n).getName()
																	.replaceAll("direction", "YN");
															JLabel JLtmp = new JLabel("", JLabel.CENTER);
															JLtmp.setName(sTmp);
															Rectangle r = new Rectangle();
															r = contentPane.getComponent(n).getBounds();
															r.x = r.x + 20;
															// r.y = r.y+20;
															r.width = r.width + 10;
															JLtmp.setBounds(r);

															if (directionUD[i][j].ifYes)
																JLtmp.setText("YES");
															else if (directionUD[i][j].ifNo)
																JLtmp.setText("NO");

															JLtmp.setVisible(true);
															contentPane.add(JLtmp, new Integer(2));
															n++;
														}

														Image img = icon.getImage();
														img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
														icon.setImage(img);

														((JButton) contentPane.getComponent(n)).setIcon(icon);
													}
												}
											}
										}
										i++;
									}
								}
							}
							for (int i = 0; i < contentPane.getComponentCount(); i++) {
								String tmp = contentPane.getComponent(i).getName();
								Point p = new Point();
								if (tmp != null && tmp.contains("directionLR")) {
									p = underBarSplit(tmp);
									if (directionLR[p.x][p.y].isUsing())
										contentPane.getComponent(i).setVisible(true);
								}
								if (tmp != null && tmp.contains("directionUD")) {
									p = underBarSplit(tmp);
									if (directionUD[p.x][p.y].isUsing())
										contentPane.getComponent(i).setVisible(true);
								}
							}

							contentPane.revalidate();
							contentPane.repaint();
						} catch (IOException ioe) {
							ioe.printStackTrace();
						} finally {
							// I/O 자원 해제.
							if (reader != null)
								try {
									reader.close();
								} catch (Exception ex) {
								}
						}
					}

				}
			}
		});

		JMenuItem SaveItem = new JMenuItem("Save");
		SaveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "//save"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("leal 파일", "leal");
				fileChooser.addChoosableFileFilter(filter);

				int userSelection = fileChooser.showSaveDialog(null);

				if (userSelection == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fileChooser.getSelectedFile();
					try {
						FileWriter fout = new FileWriter(fileToSave.getAbsolutePath());
						fout.write("!!$$LEAL_PROGRAM_EXTENSION_AUTHENTICATION$$!!");
						fout.write(System.getProperty("line.separator"));
						fout.write("VER.ALPHA");
						fout.write(System.getProperty("line.separator"));

						fout.write("!!START_isUsingBlock");
						fout.write(System.getProperty("line.separator"));
						for (int i = 0; i < ROW_NUMBER; i++) {
							for (int j = 0; j < COLUMN_NUMBER; j++)
								fout.write(isUsingBlock[i][j] + "/");
							fout.write(System.getProperty("line.separator"));
						}

						fout.write("!!START_shape");
						fout.write(System.getProperty("line.separator"));
						for (int i = 0; i < ROW_NUMBER; i++) {
							for (int j = 0; j < COLUMN_NUMBER; j++) {
								fout.write(shape[i][j].fType + "@");
								if (shape[i][j].text.equals(""))
									fout.write("null/");
								else
									fout.write(shape[i][j].text + "/");
							}
							fout.write(System.getProperty("line.separator"));
						}

						fout.write("!!START_directionLR");
						fout.write(System.getProperty("line.separator"));
						for (int i = 0; i < ROW_NUMBER; i++) {
							for (int j = 0; j < COLUMN_NUMBER - 1; j++) {
								fout.write(directionLR[i][j].isOn + "," + directionLR[i][j].isOrtho + ","
										+ directionLR[i][j].ifYes + "," + directionLR[i][j].ifNo + "/");
							}
							fout.write(System.getProperty("line.separator"));
						}

						fout.write("!!START_directionUD");
						fout.write(System.getProperty("line.separator"));
						for (int i = 0; i < ROW_NUMBER - 1; i++) {
							for (int j = 0; j < COLUMN_NUMBER; j++) {
								fout.write(directionUD[i][j].isOn + "," + directionUD[i][j].isOrtho + ","
										+ directionUD[i][j].ifYes + "," + directionUD[i][j].ifNo + "/");
							}
							fout.write(System.getProperty("line.separator"));
						}

						fout.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println("Save as file: " + fileToSave.getAbsolutePath());
				}
			}
		});
		
	
	      JMenuItem ExitItem = new JMenuItem("Exit");
	      ExitItem.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {
	            JOptionPane.showMessageDialog( ExitItem, "정말로 종료하시겠습니까?", "leal종료", JOptionPane.WARNING_MESSAGE);
	            System.exit(0);
	         }
	         
	      });
	  	OpenItem.setAccelerator(KeyStroke.getKeyStroke('O',Event.CTRL_MASK)); //crt1 + O
	      SaveItem.setAccelerator(KeyStroke.getKeyStroke('S',Event.CTRL_MASK)); //Ctr1 + S
	      ExitItem.setAccelerator(KeyStroke.getKeyStroke('D',Event.CTRL_MASK)); // Ctrl + D  

		fileMenu.add(OpenItem);
		fileMenu.add(SaveItem);
		fileMenu.add(ExitItem);
		mb.add(fileMenu);
		setJMenuBar(mb);

		comp = new compiler();
		cont = new controller();
		nowPoint = new JLabel();
		nowPoint.setBackground(Color.white);
		nowPoint.setBorder(yellowBorder);
		nowPoint.setOpaque(true);
		nowPoint.setSize(220, 88);
		nowPoint.setVisible(false);
		contentPane.add(nowPoint, new Integer(0));

		JPanel centerJP = new JPanel();
		centerJP.setBackground(Color.white);
		centerJP.setOpaque(true);
		centerJP.setBounds(300, 25, 780, 960);
		contentPane.add(centerJP, new Integer(0));

		for (int i = 0; i < ROW_NUMBER; i++)
			for (int j = 0; j < COLUMN_NUMBER - 1; j++)
				directionLR[i][j] = new Direction();

		for (int i = 0; i < ROW_NUMBER - 1; i++)
			for (int j = 0; j < COLUMN_NUMBER; j++)
				directionUD[i][j] = new Direction();

		for (int i = 0; i < ROW_NUMBER; i++)
			for (int j = 0; j < COLUMN_NUMBER; j++)
				shape[i][j] = new Shape();

		JLabel speedJL = new JLabel("실행속도 :", JLabel.CENTER);
		speedJL.setBounds(800, 40, 60, 30);
		contentPane.add(speedJL, new Integer(2));

		JComboBox speedCombo = new JComboBox(executeSpeed);
		speedCombo.setBounds(870, 40, 70, 30);
		contentPane.add(speedCombo, new Integer(2));
		speedCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				exeSpeedString = (String) cb.getSelectedItem();
			}
		});

		selectShape = new JLabel("", JLabel.CENTER);
		JLabel seshp = new JLabel("도형 도우미", JLabel.CENTER);
		seshp.setBounds(20, 60, 200, 40);
		contentPane.add(seshp, new Integer(1));
		selectShape.setBounds(20, 80, 200, 40);
		contentPane.add(selectShape, new Integer(1));

		JPanel shapeJP = new JPanel();
		shapeJP.setSize(300, 1000);
		shapeJP.setBackground(Color.white);
		shapeJP.setLayout(null);
		shapeJP.setLocation(0, 25);
		shapeJP.setVisible(true);
		contentPane.add(shapeJP);

		// 시작과 끝을 나타내는 Label
		for (int i = 0, j = 35; i < Start_End.length; i++, j += 755) {
			ImageIcon icon = new ImageIcon("src/images/" + Start_End[i] + ".png");
			Image img = icon.getImage();
			img = img.getScaledInstance(180, 72, Image.SCALE_SMOOTH);
			icon.setImage(img);

			JLabel JLb = new JLabel(icon);
			JLb.setBackground(Color.white);
			JLb.setBounds(520, j, 180, 72);
			JLb.setName(Start_End[i]);
			
			icon = new ImageIcon("src/images/down.png");
			img = icon.getImage();
			img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			icon.setImage(img);
			
			JLabel JLb2 = new JLabel(icon);
			JLb2.setBackground(Color.white);
			JLb2.setBounds(602, j+75, 20, 20);

			contentPane.add(JLb, new Integer(2));
			contentPane.add(JLb2, new Integer(2));
		}

		// 좌측에 알고리듬 도형 버튼을 그리는 작업
		for (int i = 0, j = 100; i < shapeName.length; i++, j += 100) {
			ImageIcon icon = new ImageIcon("src/images/" + shapeName[i] + ".png");
			Image img = icon.getImage();
			img = img.getScaledInstance(180, 72, Image.SCALE_SMOOTH);
			icon.setImage(img);

			JButton btn = new JButton(icon);
			btn.setBackground(Color.white);
			btn.setBorderPainted(false);
			btn.setBounds(20, j, 200, 80);
			btn.setName(shapeName[i]);
			btn.setRolloverEnabled(true);

			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectShape.setText(btn.getName());
					shapeString = btn.getName();

					selectArrow.setText("");
					arrowString = "";

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("block") && blockCheck(tmp)) {
							if (!shapeString.equals("delete"))
								contentPane.getComponent(i).setVisible(true);
							else if (shapeString.equals("delete"))
								contentPane.getComponent(i).setVisible(false);
						}
					}
					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						Point p = new Point();
						if (tmp != null && tmp.contains("directionLR")) {
							p = underBarSplit(tmp);
							if (!directionLR[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
						if (tmp != null && tmp.contains("directionUD")) {
							p = underBarSplit(tmp);
							if (!directionUD[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
					}

					contentPane.revalidate();
					contentPane.repaint();
				}
			});

			shapeJP.add(btn, new Integer(2));
		}

		// 방향 도우미 표시창
		selectArrow = new JLabel("", JLabel.CENTER);
		selectArrow.setVisible(true);
		JLabel searr = new JLabel("방향 도우미", JLabel.CENTER);
		searr.setBounds(20, 720, 200, 40);
		searr.setVisible(true);
		shapeJP.add(searr, new Integer(1));
		selectArrow.setBounds(20, 740, 200, 40);
		shapeJP.add(selectArrow, new Integer(1));

		// 방향 도우미 중에서 상하좌우 화살표 버튼을 만드는 작업
		for (int i = 0; i < directionName.length; i++) {
			ImageIcon icon = new ImageIcon("src/images/" + directionName[i] + ".png");
			Image img = icon.getImage();
			img = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
			icon.setImage(img);

			JButton btn = new JButton(icon);
			btn.setBackground(Color.white);
			btn.setBounds(25 + 40 * i, 790, 30, 30);
			btn.setName(directionName[i]);
			btn.setRolloverEnabled(true);
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					selectArrow.setText(btn.getName());
					arrowString = btn.getName();

					selectShape.setText("");
					shapeString = "";

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("block") && blockCheck(tmp))
							contentPane.getComponent(i).setVisible(false);
					}
					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						Point p = new Point();
						if (tmp != null && tmp.contains("directionLR")) {
							p = underBarSplit(tmp);
							if (!directionLR[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
						if (tmp != null && tmp.contains("directionUD")) {
							p = underBarSplit(tmp);
							if (!directionUD[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
					}

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (btn.getName().equals("left") || btn.getName().equals("right"))
							if (tmp != null && tmp.contains("directionLR") && directionLRCheck(tmp))
								contentPane.getComponent(i).setVisible(true);
						if (btn.getName().equals("up") || btn.getName().equals("down"))
							if (tmp != null && tmp.contains("directionUD") && directionUDCheck(tmp))
								contentPane.getComponent(i).setVisible(true);
					}
					shapeJP.revalidate();
					shapeJP.repaint();
				}
			});
			shapeJP.add(btn, new Integer(1));
		}

		contentPane.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				String KeyName = "";
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					KeyName = "left";
				if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					KeyName = "right";
				if (e.getKeyCode() == KeyEvent.VK_UP)
					KeyName = "up";
				if (e.getKeyCode() == KeyEvent.VK_DOWN)
					KeyName = "down";

				if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
					selectArrow.setText(KeyName);
					arrowString = KeyName;

					selectShape.setText("");
					shapeString = "";

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("block") && blockCheck(tmp))
							contentPane.getComponent(i).setVisible(false);
					}
					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						Point p = new Point();
						if (tmp != null && tmp.contains("directionLR")) {
							p = underBarSplit(tmp);
							if (!directionLR[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
						if (tmp != null && tmp.contains("directionUD")) {
							p = underBarSplit(tmp);
							if (!directionUD[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
					}

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("directionLR") && directionLRCheck(tmp))
							contentPane.getComponent(i).setVisible(true);
					}
					shapeJP.revalidate();
					shapeJP.repaint();
				}
				if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
					selectArrow.setText(KeyName);
					arrowString = KeyName;

					selectShape.setText("");
					shapeString = "";

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("block") && blockCheck(tmp))
							contentPane.getComponent(i).setVisible(false);
					}
					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						Point p = new Point();
						if (tmp != null && tmp.contains("directionLR")) {
							p = underBarSplit(tmp);
							if (!directionLR[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
						if (tmp != null && tmp.contains("directionUD")) {
							p = underBarSplit(tmp);
							if (!directionUD[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
					}

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("directionUD") && directionUDCheck(tmp))
							contentPane.getComponent(i).setVisible(true);
					}
					shapeJP.revalidate();
					shapeJP.repaint();
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
		});

		// 방향 도우미 중 화살표 아래에 YES NO를 그리는 작업
		for (int i = 0; i < ynName.length; i++) {
			JButton btn = new JButton(ynName[i]);
			btn.setBounds(30 + 60 * i, 840, 60, 20);
			btn.setVisible(true);
			btn.setName(ynName[i]);
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					selectArrow.setText(btn.getName());
					arrowString = btn.getName();

					selectShape.setText("");
					shapeString = "";

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("block") && blockCheck(tmp))
							contentPane.getComponent(i).setVisible(false);
					}
					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						Point p = new Point();
						if (tmp != null && tmp.contains("directionLR")) {
							p = underBarSplit(tmp);
							if (!directionLR[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
						if (tmp != null && tmp.contains("directionUD")) {
							p = underBarSplit(tmp);
							if (!directionUD[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
					}
					shapeJP.revalidate();
					shapeJP.repaint();
				}
			});
			shapeJP.add(btn, new Integer(1));
		}

		JPanel arrowJP1 = new JPanel();
		arrowJP1.setSize(300, 1000);
		arrowJP1.setBackground(Color.white);
		arrowJP1.setLayout(null);
		arrowJP1.setLocation(0, 25);
		arrowJP1.setVisible(false);
		contentPane.add(arrowJP1, new Integer(1));

		// 연결선 도형 버튼을 만드는 작업
		for (int i = 0, j = 100; i < lineArrow.length; i++, j += 100) {
			ImageIcon icon = new ImageIcon("src/images/" + lineArrow[i] + ".png");
			Image img = icon.getImage();
			img = img.getScaledInstance(180, 72, Image.SCALE_SMOOTH);
			icon.setImage(img);

			JButton btn = new JButton(icon);
			btn.setBackground(Color.white);
			btn.setBorderPainted(false);
			btn.setBounds(20, j, 200, 80);
			btn.setName(lineArrow[i]);
			btn.setRolloverEnabled(true);

			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectShape.setText(btn.getName());
					shapeString = btn.getName();

					selectArrow.setText("");
					arrowString = "";

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("block") && blockCheck(tmp))
							contentPane.getComponent(i).setVisible(true);

					}
					arrowJP1.revalidate();
					arrowJP1.repaint();
				}
			});

			arrowJP1.add(btn, new Integer(2));
		}
		JPanel arrowJP2 = new JPanel();
		arrowJP2.setSize(300, 1000);
		arrowJP2.setBackground(Color.white);
		arrowJP2.setLayout(null);
		arrowJP2.setLocation(0, 25);
		arrowJP2.setVisible(false);
		contentPane.add(arrowJP2, new Integer(1));

		for (int i = 0, j = 100; i < rightArrow.length; i++, j += 100) {
			ImageIcon icon = new ImageIcon("src/images/" + rightArrow[i] + ".png");
			Image img = icon.getImage();
			img = img.getScaledInstance(180, 72, Image.SCALE_SMOOTH);
			icon.setImage(img);

			JButton btn = new JButton(icon);
			btn.setBackground(Color.white);
			btn.setBorderPainted(false);
			btn.setBounds(20, j, 200, 80);
			btn.setName(rightArrow[i]);
			btn.setRolloverEnabled(true);

			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectShape.setText(btn.getName());
					shapeString = btn.getName();

					selectArrow.setText("");
					arrowString = "";

					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("block") && blockCheck(tmp))
							contentPane.getComponent(i).setVisible(true);

					}
					arrowJP2.revalidate();
					arrowJP2.repaint();
				}
			});

			arrowJP2.add(btn, new Integer(2));
		}

		JButton shapeBtn = new JButton("도형");
		JButton lineArrowBtn = new JButton("선분");
		JButton rightArrowBtn = new JButton("Tmp");

		shapeBtn.setBounds(30, 40, 60, 20);
		shapeBtn.setVisible(true);
		shapeBtn.setBackground(Color.lightGray);
		shapeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapeJP.setVisible(true);
				arrowJP1.setVisible(false);
				arrowJP2.setVisible(false);

				shapeString = "";
				selectShape.setText("");
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					String tmp = contentPane.getComponent(i).getName();
					if (tmp != null && tmp.contains("block"))
						contentPane.getComponent(i).setVisible(false);
				}
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					String tmp = contentPane.getComponent(i).getName();
					Point p = new Point();
					if (tmp != null && tmp.contains("directionLR")) {
						p = underBarSplit(tmp);
						if (!directionLR[p.x][p.y].isUsing())
							contentPane.getComponent(i).setVisible(false);
					}
					if (tmp != null && tmp.contains("directionUD")) {
						p = underBarSplit(tmp);
						if (!directionUD[p.x][p.y].isUsing())
							contentPane.getComponent(i).setVisible(false);
					}
				}
				selectArrow.setText("");
				arrowString = "";
				shapeBtn.setBackground(Color.lightGray);
				lineArrowBtn.setBackground(null);
				rightArrowBtn.setBackground(null);
				contentPane.requestFocus();
			}
		});
		contentPane.add(shapeBtn, new Integer(2));

		lineArrowBtn.setBounds(90, 40, 60, 20);
		lineArrowBtn.setVisible(true);
		lineArrowBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapeJP.setVisible(false);
				arrowJP1.setVisible(true);
				arrowJP2.setVisible(false);

				shapeString = "";
				selectShape.setText("");
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					String tmp = contentPane.getComponent(i).getName();
					if (tmp != null && tmp.contains("block"))
						contentPane.getComponent(i).setVisible(false);
				}
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					String tmp = contentPane.getComponent(i).getName();
					Point p = new Point();
					if (tmp != null && tmp.contains("directionLR")) {
						p = underBarSplit(tmp);
						if (!directionLR[p.x][p.y].isUsing())
							contentPane.getComponent(i).setVisible(false);
					}
					if (tmp != null && tmp.contains("directionUD")) {
						p = underBarSplit(tmp);
						if (!directionUD[p.x][p.y].isUsing())
							contentPane.getComponent(i).setVisible(false);
					}
				}
				selectArrow.setText("");
				arrowString = "";
				shapeBtn.setBackground(null);
				lineArrowBtn.setBackground(Color.lightGray);
				rightArrowBtn.setBackground(null);

			}
		});
		contentPane.add(lineArrowBtn, new Integer(2));

		rightArrowBtn.setBounds(150, 40, 60, 20);
		rightArrowBtn.setVisible(true);
		rightArrowBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapeJP.setVisible(false);
				arrowJP1.setVisible(false);
				arrowJP2.setVisible(true);

				shapeString = "";
				selectShape.setText("");
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					String tmp = contentPane.getComponent(i).getName();
					if (tmp != null && tmp.contains("block"))
						contentPane.getComponent(i).setVisible(false);
				}
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					String tmp = contentPane.getComponent(i).getName();
					Point p = new Point();
					if (tmp != null && tmp.contains("directionLR")) {
						p = underBarSplit(tmp);
						if (!directionLR[p.x][p.y].isUsing())
							contentPane.getComponent(i).setVisible(false);
					}
					if (tmp != null && tmp.contains("directionUD")) {
						p = underBarSplit(tmp);
						if (!directionUD[p.x][p.y].isUsing())
							contentPane.getComponent(i).setVisible(false);
					}
				}
				selectArrow.setText("");
				arrowString = "";
				shapeBtn.setBackground(null);
				lineArrowBtn.setBackground(null);
				rightArrowBtn.setBackground(Color.lightGray);
			}
		});
		contentPane.add(rightArrowBtn, new Integer(2));

		// 팬의 빈 곳에 우클릭을 누를 때 선택을 취소하는 리스너
		contentPane.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					shapeString = "";
					selectShape.setText("");
					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						if (tmp != null && tmp.contains("block"))
							contentPane.getComponent(i).setVisible(false);
					}
					for (int i = 0; i < contentPane.getComponentCount(); i++) {
						String tmp = contentPane.getComponent(i).getName();
						Point p = new Point();
						if (tmp != null && tmp.contains("directionLR")) {
							p = underBarSplit(tmp);
							if (!directionLR[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
						if (tmp != null && tmp.contains("directionUD")) {
							p = underBarSplit(tmp);
							if (!directionUD[p.x][p.y].isUsing())
								contentPane.getComponent(i).setVisible(false);
						}
					}
					selectArrow.setText("");
					arrowString = "";
					contentPane.requestFocus();

				}

			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		// 도형을 선택했을때 찍을 위치를 나타내는 플러스 버튼
		for (int i = 0; i < ROW_NUMBER; i++) {
			for (int j = 0; j < COLUMN_NUMBER; j++) {
				ImageIcon icon = new ImageIcon("src/images/plus_black.png");
				Image img = icon.getImage();
				img = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
				icon.setImage(img);
				JButton btn = new JButton(icon);
				btn.setName("block_" + i + "_" + j);

				btn.setBounds(380 + j * 220, 150 + i * 100, 30, 30);
				btn.setBorderPainted(false);
				btn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// 도형을 찍음 -> 플러스 버튼을 찍음 -> 기존의 플러스 버튼 삭제 & 새로운 도형 버튼 객체 생성함
						if (shapeString != null) {
							Point point = new Point();
							point = btn.getLocation();

							ImageIcon icon = new ImageIcon("src/images/" + shapeString + ".png");
							Image img = icon.getImage();
							img = img.getScaledInstance(180, 72, Image.SCALE_SMOOTH);
							icon.setImage(img);

							JButton newBtn = new JButton(icon);
							newBtn.setBackground(Color.white);
							newBtn.setName(btn.getName().replaceAll("block", "shape"));
							newBtn.setBorderPainted(false);
							Point pTmp = new Point();
							pTmp = underBarSplit(newBtn.getName());

							shape[pTmp.x][pTmp.y].setfType(shapeString);

							// 버튼이 생설될 때 다음에 실행 가능한 화살표 객체와 찍기 가능한 플러스 버튼 표시
							if (pTmp.y > 0)
								isUsingBlock[pTmp.x][pTmp.y - 1] = true;
							if (pTmp.y < COLUMN_NUMBER - 1)
								isUsingBlock[pTmp.x][pTmp.y + 1] = true;
							if (pTmp.x < ROW_NUMBER - 1)
								isUsingBlock[pTmp.x + 1][pTmp.y] = true;
							if (pTmp.x > 0)
								isUsingBlock[pTmp.x - 1][pTmp.y] = true;

							if (pTmp.x > 0)
								directionUD[pTmp.x - 1][pTmp.y].isOn = true;
							if (pTmp.x < ROW_NUMBER - 1)
								directionUD[pTmp.x][pTmp.y].isOn = true;
							if (pTmp.y > 0)
								directionLR[pTmp.x][pTmp.y - 1].isOn = true;
							if (pTmp.y < COLUMN_NUMBER - 1)
								directionLR[pTmp.x][pTmp.y].isOn = true;

							newBtn.setBounds(point.x - 80, point.y - 21, 180, 72);
							newBtn.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									/*
									 * 도형버튼을 눌러서 값 변경, 값은 새로운 Label을 계층구조로 생성하여 나타냄. 중복시 기존의 레이블 객체를 찾아내어 값을 변경하는
									 * 과정을 통해 수정기능 구현 삭제 미구현 상태라 주석처리
									 */

									if (shapeString != null && !shapeString.equals("delete")) {
										String tmp = newBtn.getName().replaceAll("shape", "label");
										JLabel JLtmp = new JLabel();
										Boolean overLap = false;
										Point p = new Point();
										p = underBarSplit(tmp);

										if (shape[p.x][p.y].isShape()) {
											for (int i = 0; i < contentPane.getComponentCount(); i++) {
												String Stmp = contentPane.getComponent(i).getName();
												if (Stmp != null)
													if (Stmp.equals(tmp)) {
														JLtmp = (JLabel) contentPane.getComponent(i);
														overLap = true;
													}
											}
											if (!overLap) {
												JLtmp = new JLabel("", JLabel.CENTER);
												JLtmp.setBounds(newBtn.getBounds());
												JLtmp.setName(tmp);
											}

											dialog.tf.setText(JLtmp.getText());
											dialog.setVisible(true);
											JLtmp.setText(dialog.getInput());
											shape[p.x][p.y].text = JLtmp.getText();
											contentPane.requestFocus();

											contentPane.add(JLtmp, new Integer(2));
											contentPane.revalidate();
											contentPane.repaint();
										}
									} else if (shapeString != null && shapeString.equals("delete")) {

										String tmp = newBtn.getName();
										String tmp2 = newBtn.getName().replaceAll("shape", "label");
										Point p = new Point();
										p = underBarSplit(tmp);

										for (int i = 0; i < contentPane.getComponentCount(); i++) {
											String Stmp = contentPane.getComponent(i).getName();
											if (Stmp != null) {
												if (Stmp.equals(tmp))
													contentPane.remove(i);
												else if (Stmp.equals(tmp2))
													contentPane.remove(i);
											}
										}

										shape[p.x][p.y].fType = 0x00;
										shape[p.x][p.y].text = "";

										boolean aroundCheck = true;
										boolean aroundCheck2 = true;
										boolean aroundCheck3 = true;
										boolean aroundCheck4 = true;

										// 화살표 객체 off
										if (p.x > 0) {
											isUsingBlock[p.x - 1][p.y] = false;
											if (shape[p.x - 1][p.y].fType == 0x00) {
												aroundCheck = false;
												directionUD[p.x - 1][p.y].initDirection();
												for (int i = 0; i < contentPane.getComponentCount(); i++) {
													tmp = contentPane.getComponent(i).getName();
													if (tmp != null && tmp.contains("directionUD")) {
														Point p2 = new Point();
														p2 = underBarSplit(tmp);
														if (p.x - 1 == p2.x && p.y == p2.y) {
															ImageIcon icon = new ImageIcon("src/images/idle.png");
															Image img = icon.getImage();
															img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
															icon.setImage(img);
															((JButton) contentPane.getComponent(i)).setIcon(icon);
														}
													}
												}
											}
										} else
											aroundCheck = false;

										if (p.x < ROW_NUMBER - 1) {
											isUsingBlock[p.x + 1][p.y] = false;
											if (shape[p.x + 1][p.y].fType == 0x00) {
												aroundCheck2 = false;
												directionUD[p.x][p.y].initDirection();
												for (int i = 0; i < contentPane.getComponentCount(); i++) {
													tmp = contentPane.getComponent(i).getName();
													if (tmp != null && tmp.contains("directionUD")) {
														Point p2 = new Point();
														p2 = underBarSplit(tmp);
														if (p.x == p2.x && p.y == p2.y) {
															ImageIcon icon = new ImageIcon("src/images/idle.png");
															Image img = icon.getImage();
															img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
															icon.setImage(img);
															((JButton) contentPane.getComponent(i)).setIcon(icon);
														}
													}
												}
											}
										} else
											aroundCheck2 = false;
										if (p.y > 0) {
											isUsingBlock[p.x][p.y - 1] = false;
											if (shape[p.x][p.y - 1].fType == 0x00) {
												aroundCheck3 = false;
												directionLR[p.x][p.y - 1].initDirection();
												for (int i = 0; i < contentPane.getComponentCount(); i++) {
													tmp = contentPane.getComponent(i).getName();
													if (tmp != null && tmp.contains("directionLR")) {
														Point p2 = new Point();
														p2 = underBarSplit(tmp);
														if (p.x == p2.x && p.y - 1 == p2.y) {
															ImageIcon icon = new ImageIcon("src/images/idle.png");
															Image img = icon.getImage();
															img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
															icon.setImage(img);
															((JButton) contentPane.getComponent(i)).setIcon(icon);
														}
													}
												}
											}
										} else
											aroundCheck3 = false;
										if (p.y < COLUMN_NUMBER - 1) {
											isUsingBlock[p.x][p.y + 1] = false;
											if (shape[p.x][p.y + 1].fType == 0x00) {
												aroundCheck4 = false;
												directionLR[p.x][p.y].initDirection();
												for (int i = 0; i < contentPane.getComponentCount(); i++) {
													tmp = contentPane.getComponent(i).getName();
													if (tmp != null && tmp.contains("directionLR")) {
														Point p2 = new Point();
														p2 = underBarSplit(tmp);
														if (p.x == p2.x && p.y == p2.y) {
															ImageIcon icon = new ImageIcon("src/images/idle.png");
															Image img = icon.getImage();
															img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
															icon.setImage(img);
															((JButton) contentPane.getComponent(i)).setIcon(icon);
														}
													}
												}
											}
										} else
											aroundCheck4 = false;
										// block 객체 off

										if (p.x > 1)
											if (shape[p.x - 2][p.y].fType != 0x00)
												isUsingBlock[p.x - 1][p.y] = true;
										if (p.x > 0 && p.y > 0)
											if (shape[p.x - 1][p.y - 1].fType != 0x00) {
												isUsingBlock[p.x - 1][p.y] = true;
												isUsingBlock[p.x][p.y - 1] = true;
											}
										if (p.x > 0 && p.y < COLUMN_NUMBER - 1)
											if (shape[p.x - 1][p.y + 1].fType != 0x00) {
												isUsingBlock[p.x - 1][p.y] = true;
												isUsingBlock[p.x][p.y + 1] = true;
											}
										if (p.x < ROW_NUMBER - 2)
											if (shape[p.x + 2][p.y].fType != 0x00)
												isUsingBlock[p.x + 1][p.y] = true;
										if (p.x < ROW_NUMBER - 1 && p.y > 0)
											if (shape[p.x + 1][p.y - 1].fType != 0x00) {
												isUsingBlock[p.x][p.y - 1] = true;
												isUsingBlock[p.x + 1][p.y] = true;
											}
										if (p.x < ROW_NUMBER - 1 && p.y < COLUMN_NUMBER - 1)
											if (shape[p.x + 1][p.y + 1].fType != 0x00) {
												isUsingBlock[p.x][p.y + 1] = true;
												isUsingBlock[p.x + 1][p.y] = true;
											}
										if (!aroundCheck && !aroundCheck2 && !aroundCheck3 && !aroundCheck4)
											isUsingBlock[p.x][p.y] = false;
										else
											isUsingBlock[p.x][p.y] = true;

										for (int i = 0; i < contentPane.getComponentCount(); i++) {
											String Stmp = contentPane.getComponent(i).getName();
											if (Stmp != null) {
												if (Stmp.contains("shape")) {
													p = new Point();
													p = underBarSplit(Stmp);
													if (shape[p.x][p.y].fType != 0x00)
														isUsingBlock[p.x][p.y] = false;
												}

											}
										}

										for (int i = 0; i < contentPane.getComponentCount(); i++) {
											tmp = contentPane.getComponent(i).getName();
											p = new Point();
											if (tmp != null && tmp.contains("directionLR")) {
												p = underBarSplit(tmp);
												if (!directionLR[p.x][p.y].isUsing())
													contentPane.getComponent(i).setVisible(false);
											}
											if (tmp != null && tmp.contains("directionUD")) {
												p = underBarSplit(tmp);
												if (!directionUD[p.x][p.y].isUsing())
													contentPane.getComponent(i).setVisible(false);
											}
										}

										isUsingBlock[0][1] = true;

										selectShape.setText("");
										shapeString = "";

										contentPane.revalidate();
										contentPane.repaint();

									}
								}
							});
							contentPane.add(newBtn, new Integer(1));

							for (int i = 0; i < contentPane.getComponentCount(); i++) {
								String Stmp = contentPane.getComponent(i).getName();
								if (Stmp != null) {
									if (Stmp.contains("shape")) {
										Point p = new Point();
										p = underBarSplit(Stmp);
										if (shape[p.x][p.y].fType != 0x00)
											isUsingBlock[p.x][p.y] = false;
									}

								}
							}

							btn.setVisible(false);
							selectShape.setText("");
							shapeString = "";

							for (int i = 0; i < contentPane.getComponentCount(); i++) {
								String tmp = contentPane.getComponent(i).getName();
								if (tmp != null)
									if (tmp.contains("block")) {
										contentPane.getComponent(i).setVisible(false);
									}
							}

							contentPane.revalidate();
							contentPane.repaint();
						}
					}
				});
				btn.setVisible(false);
				contentPane.add(btn, new Integer(1));
			}
		}
		// 좌우 화살표 버튼 정보
		for (int i = 0; i < ROW_NUMBER; i++) {
			for (int j = 0; j < COLUMN_NUMBER - 1; j++) {
				ImageIcon icon = new ImageIcon("src/images/idle.png");
				Image img = icon.getImage();
				img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
				icon.setImage(img);
				JButton btn = new JButton(icon);
				btn.setBorderPainted(false);
				btn.setName("directionLR_" + i + "_" + j);
				btn.setBackground(Color.white);
			

				// 방향 도우미 버튼을 누름 -> 화살표 칸에 방향을 지정하는 과정
				// right, left, idle, YES, NO, DEL 여섯개의 경우
				btn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (arrowString.equals("right")) {
							String tmp = btn.getName();
							Point point = new Point(underBarSplit(tmp));

							directionLR[point.x][point.y].isOrtho = 1;
							ImageIcon icon = new ImageIcon("src/images/right.png");
							Image img = icon.getImage();
							img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
							icon.setImage(img);
							btn.setIcon(icon);
						} else if (arrowString.equals("left")) {
							String tmp = btn.getName();
							Point point = new Point(underBarSplit(tmp));

							directionLR[point.x][point.y].isOrtho = 0;
							ImageIcon icon = new ImageIcon("src/images/left.png");
							Image img = icon.getImage();
							img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
							icon.setImage(img);
							btn.setIcon(icon);
						} else if (arrowString.equals("idle")) {
							String tmp = btn.getName();
							Point point = new Point(underBarSplit(tmp));
							ImageIcon icon = new ImageIcon("src/images/idle.png");
							Image img = icon.getImage();
							img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
							icon.setImage(img);
							btn.setIcon(icon);
							directionLR[point.x][point.y].isOrtho = -1;

							tmp = btn.getName().replaceAll("direction", "YN");
							for (int i = 0; i < contentPane.getComponentCount(); i++) {
								String Stmp = contentPane.getComponent(i).getName();
								if (tmp.equals(Stmp))
									contentPane.remove(i);
							}
							Point p = new Point();
							p = underBarSplit(tmp);
							directionLR[p.x][p.y].ifYes = false;
							directionLR[p.x][p.y].ifNo = false;
						} else if (arrowString.equals("YES") || arrowString.equals("NO")) {
							String tmp = btn.getName().replaceAll("direction", "YN");
							JLabel JLtmp = new JLabel();
							Boolean overLap = false;
							for (int i = 0; i < contentPane.getComponentCount(); i++) {
								String Stmp = contentPane.getComponent(i).getName();
								if (Stmp != null)
									if (Stmp.equals(tmp)) {
										JLtmp = (JLabel) contentPane.getComponent(i);
										overLap = true;
									}
							}
							if (!overLap) {
								JLtmp = new JLabel("", JLabel.CENTER);
								Rectangle r = new Rectangle();
								r = btn.getBounds();
								r.x = r.x - 5;
								r.y = r.y + 20;
								r.width = r.width + 10;
								JLtmp.setBounds(r);
								JLtmp.setName(tmp);
							}
							Point p = new Point();
							p = underBarSplit(tmp);

							if (arrowString.equals("YES")) {
								directionLR[p.x][p.y].ifYes = true;
								directionLR[p.x][p.y].ifNo = false;
							} else if (arrowString.equals("NO")) {
								directionLR[p.x][p.y].ifYes = false;
								directionLR[p.x][p.y].ifNo = true;
							}

							JLtmp.setVisible(true);
							JLtmp.setText(arrowString);

							contentPane.add(JLtmp, new Integer(2));
						} else if (arrowString.equals("DEL")) {
							String tmp = btn.getName().replaceAll("direction", "YN");
							for (int i = 0; i < contentPane.getComponentCount(); i++) {
								String Stmp = contentPane.getComponent(i).getName();
								if (tmp.equals(Stmp))
									contentPane.remove(i);
							}
							Point p = new Point();
							p = underBarSplit(tmp);
							directionLR[p.x][p.y].ifYes = false;
							directionLR[p.x][p.y].ifNo = false;
						}
						for (int i = 0; i < contentPane.getComponentCount(); i++) {
							String tmp = contentPane.getComponent(i).getName();
							Point p = new Point();
							if (tmp != null && tmp.contains("directionLR")) {
								p = underBarSplit(tmp);
								if (!directionLR[p.x][p.y].isUsing())
									contentPane.getComponent(i).setVisible(false);
							}
						}
						selectArrow.setText("");
						arrowString = "";
						contentPane.requestFocus();
						contentPane.revalidate();
						contentPane.repaint();
					}
				});

				btn.setBounds(490 + j * 220, 155 + i * 100, 20, 20);
				btn.setVisible(false);
				contentPane.add(btn, new Integer(1));
			}
		}
		// 위아래 화살표 버튼 정보
		// 좌우와 거의 비슷하다.
		for (int i = 0; i < ROW_NUMBER - 1; i++) {
			for (int j = 0; j < COLUMN_NUMBER; j++) {
				ImageIcon icon = new ImageIcon("src/images/idle.png");
				Image img = icon.getImage();
				img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
				icon.setImage(img);
				JButton btn = new JButton(icon);
				btn.setName("directionUD_" + i + "_" + j);
				btn.setBorderPainted(false);

				btn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (arrowString.equals("down")) {
							String tmp = btn.getName();
							Point point = new Point(underBarSplit(tmp));

							directionUD[point.x][point.y].isOrtho = 1;
							ImageIcon icon = new ImageIcon("src/images/down.png");
							Image img = icon.getImage();
							img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
							icon.setImage(img);
							btn.setIcon(icon);
						} else if (arrowString.equals("up")) {
							String tmp = btn.getName();
							Point point = new Point(underBarSplit(tmp));

							directionUD[point.x][point.y].isOrtho = 0;
							ImageIcon icon = new ImageIcon("src/images/up.png");
							Image img = icon.getImage();
							img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
							icon.setImage(img);
							btn.setIcon(icon);
						} else if (arrowString.equals("idle")) {
							String tmp = btn.getName();
							Point point = new Point(underBarSplit(tmp));
							ImageIcon icon = new ImageIcon("src/images/idle.png");
							Image img = icon.getImage();
							img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
							icon.setImage(img);
							btn.setIcon(icon);
							directionUD[point.x][point.y].isOrtho = -1;
							tmp = btn.getName().replaceAll("direction", "YN");
							for (int i = 0; i < contentPane.getComponentCount(); i++) {
								String Stmp = contentPane.getComponent(i).getName();
								if (tmp.equals(Stmp))
									contentPane.remove(i);
							}
							Point p = new Point();
							p = underBarSplit(tmp);
							directionUD[p.x][p.y].ifYes = false;
							directionUD[p.x][p.y].ifNo = false;
						} else if (arrowString.equals("YES") || arrowString.equals("NO")) {
							String tmp = btn.getName().replaceAll("direction", "YN");
							JLabel JLtmp = new JLabel();
							Boolean overLap = false;
							for (int i = 0; i < contentPane.getComponentCount(); i++) {
								String Stmp = contentPane.getComponent(i).getName();
								if (Stmp != null)
									if (Stmp.equals(tmp)) {
										JLtmp = (JLabel) contentPane.getComponent(i);
										overLap = true;
									}
							}
							if (!overLap) {
								JLtmp = new JLabel("", JLabel.CENTER);
								Rectangle r = new Rectangle();
								r = btn.getBounds();
								r.x = r.x + 20;
								// r.y = r.y+20;
								r.width = r.width + 10;
								JLtmp.setBounds(r);
								JLtmp.setName(tmp);
							}
							Point p = new Point();
							p = underBarSplit(tmp);

							if (arrowString.equals("YES")) {
								directionUD[p.x][p.y].ifYes = true;
								directionUD[p.x][p.y].ifNo = false;
							} else if (arrowString.equals("NO")) {
								directionUD[p.x][p.y].ifYes = false;
								directionUD[p.x][p.y].ifNo = true;
							}

							JLtmp.setVisible(true);
							JLtmp.setText(arrowString);

							contentPane.add(JLtmp, new Integer(2));
						} else if (arrowString.equals("DEL")) {
							String tmp = btn.getName().replaceAll("direction", "YN");
							for (int i = 0; i < contentPane.getComponentCount(); i++) {
								String Stmp = contentPane.getComponent(i).getName();
								if (tmp.equals(Stmp))
									contentPane.remove(i);
							}
							Point p = new Point();
							p = underBarSplit(tmp);
							directionUD[p.x][p.y].ifYes = false;
							directionUD[p.x][p.y].ifNo = false;
						}
						for (int i = 0; i < contentPane.getComponentCount(); i++) {
							String tmp = contentPane.getComponent(i).getName();
							Point p = new Point();
							if (tmp != null && tmp.contains("directionUD")) {
								p = underBarSplit(tmp);
								if (!directionUD[p.x][p.y].isUsing())
									contentPane.getComponent(i).setVisible(false);
							}
						}
						selectArrow.setText("");
						arrowString = "";
						contentPane.requestFocus();
						contentPane.revalidate();
						contentPane.repaint();

					}

				});

				btn.setBounds(382 + j * 220, 205 + i * 100, 20, 20);
				btn.setVisible(false);
				contentPane.add(btn, new Integer(1));
			}
		}

		RegisterPanel registerJP = new RegisterPanel();

		contentPane.add(registerJP);
		// 플레이 버튼
		ImageIcon playIcon = new ImageIcon("src/images/play.png");

		Image playImg = playIcon.getImage();
		playImg = playImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		playIcon.setImage(playImg);
		JButton playButton = new JButton(playIcon);
		playButton.setBounds(950, 40, 30, 30);
		contentPane.add(playButton, new Integer(2));

		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i = 0;
				if (!playCheck) {
					playCheck = true;
					System.out.println("test");

					if (exeSpeedString.equals("즉시")) {
						if (startPoint.x == 0 && startPoint.y == 1) {
							RegisterPanel.initRegister();
							comp.initRegister();
							playCheck = false;
						}
						nowPoint.setBorder(yellowBorder);
						while (true) {
							Point p = new Point();
							System.out.println(startPoint);

							nowPoint.setVisible(true);
							nowPoint.setLocation(280 + startPoint.y * 220, 122 + startPoint.x * 100);

							try {
								p = playShape(startPoint);
								startPoint.x = p.x;
								startPoint.y = p.y;
							} catch (StringIndexOutOfBoundsException e) {
								nowPoint.setBorder(redBorder);
							} catch (ArrayIndexOutOfBoundsException e) {
								nowPoint.setBorder(redBorder);
							} catch (EmptyStackException e) {
								nowPoint.setBorder(redBorder);
							} catch (NumberFormatException e) {
								nowPoint.setBorder(redBorder);
							} catch (NullPointerException e) {
								nowPoint.setBorder(redBorder);
							}

							if (startPoint.x == 0 && startPoint.y == 1)
								break;

						}
					} else if (exeSpeedString.equals("빠르게")) {

						timer = new Timer(100, new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e1) {
								// TODO Auto-generated method stub
								Point p = new Point();
								System.out.println(startPoint);
								nowPoint.setBorder(yellowBorder);
								nowPoint.setVisible(true);
								nowPoint.setLocation(280 + startPoint.y * 220, 122 + startPoint.x * 100);

								try {
									p = playShape(startPoint);
									startPoint.x = p.x;
									startPoint.y = p.y;
								} catch (StringIndexOutOfBoundsException e) {
									nowPoint.setBorder(redBorder);
								} catch (ArrayIndexOutOfBoundsException e) {
									nowPoint.setBorder(redBorder);
								} catch (EmptyStackException e) {
									nowPoint.setBorder(redBorder);
								} catch (NumberFormatException e) {
									nowPoint.setBorder(redBorder);
								} catch (NullPointerException e) {
									nowPoint.setBorder(redBorder);
								}

								if (startPoint.x == 0 && startPoint.y == 1) {
									timer.stop();
									playCheck = false;
								}
									
							}

						});
						timer.start();
						// timer.setRepeats(false);
						// timer.setInitialDelay(0);


					} else if (exeSpeedString.equals("보통")) {

						timer = new Timer(500, new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e1) {
								// TODO Auto-generated method stub
								Point p = new Point();
								System.out.println(startPoint);
								nowPoint.setBorder(yellowBorder);
								nowPoint.setVisible(true);
								nowPoint.setLocation(280 + startPoint.y * 220, 122 + startPoint.x * 100);

								try {
									p = playShape(startPoint);
									startPoint.x = p.x;
									startPoint.y = p.y;
								} catch (StringIndexOutOfBoundsException e) {
									nowPoint.setBorder(redBorder);
								} catch (ArrayIndexOutOfBoundsException e) {
									nowPoint.setBorder(redBorder);
								} catch (EmptyStackException e) {
									nowPoint.setBorder(redBorder);
								} catch (NumberFormatException e) {
									nowPoint.setBorder(redBorder);
								} catch (NullPointerException e) {
									nowPoint.setBorder(redBorder);
								}

								if (startPoint.x == 0 && startPoint.y == 1) {
									timer.stop();
									playCheck = false;
								}
							}

						});
						timer.start();
						// timer.setRepeats(false);
						// timer.setInitialDelay(0);

					} else if (exeSpeedString.equals("느리게")) {

						timer = new Timer(1000, new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e1) {
								// TODO Auto-generated method stub
								Point p = new Point();
								System.out.println(startPoint);
								nowPoint.setBorder(yellowBorder);
								nowPoint.setVisible(true);
								nowPoint.setLocation(280 + startPoint.y * 220, 122 + startPoint.x * 100);

								try {
									p = playShape(startPoint);
									startPoint.x = p.x;
									startPoint.y = p.y;
								} catch (StringIndexOutOfBoundsException e) {
									nowPoint.setBorder(redBorder);
								} catch (ArrayIndexOutOfBoundsException e) {
									nowPoint.setBorder(redBorder);
								} catch (EmptyStackException e) {
									nowPoint.setBorder(redBorder);
								} catch (NumberFormatException e) {
									nowPoint.setBorder(redBorder);
								} catch (NullPointerException e) {
									nowPoint.setBorder(redBorder);
								}

								if (startPoint.x == 0 && startPoint.y == 1) {
									timer.stop();
									playCheck = false;
								}

							}

						});
						timer.start();
						// timer.setRepeats(false);
						// timer.setInitialDelay(0);
					}

					else {
						Point p = new Point();
						System.out.println(startPoint);
						if (startPoint.x == 0 && startPoint.y == 1) {
							RegisterPanel.initRegister();
							comp.initRegister();
						}
						nowPoint.setVisible(true);
						nowPoint.setLocation(280 + startPoint.y * 220, 122 + startPoint.x * 100);
						try {
							p = playShape(startPoint);
							startPoint.x = p.x;
							startPoint.y = p.y;
						} catch (StringIndexOutOfBoundsException e) {
							nowPoint.setBorder(redBorder);
						} catch (ArrayIndexOutOfBoundsException e) {
							nowPoint.setBorder(redBorder);
						} catch (EmptyStackException e) {
							nowPoint.setBorder(redBorder);
						} catch (NumberFormatException e) {
							nowPoint.setBorder(redBorder);
						} catch (NullPointerException e) {
							nowPoint.setBorder(redBorder);
						}
						playCheck = false;
					}
				}
			}
		});

		JButton stopButton = new JButton("중지");
		stopButton.setBounds(990, 40, 60, 30);
		stopButton.setVisible(true);
		contentPane.add(stopButton, new Integer(2));
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegisterPanel.initRegister();
				comp.initRegister();
				startPoint.x = 0;
				startPoint.y = 1;
				nowPoint.setVisible(false);
				if(timer != null && timer.isRunning())
					timer.stop();

				contentPane.requestFocus();
				contentPane.revalidate();
				contentPane.repaint();
				playCheck = false;
			}
		});

		JButton resetButton = new JButton("리셋");
		resetButton.setBounds(300, 40, 60, 20);
		resetButton.setVisible(true);
		contentPane.add(resetButton, new Integer(2));
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < ROW_NUMBER; i++)
					for (int j = 0; j < COLUMN_NUMBER; j++)
						isUsingBlock[i][j] = false;
				isUsingBlock[0][1] = true;

				for (int i = 0; i < ROW_NUMBER; i++)
					for (int j = 0; j < COLUMN_NUMBER; j++)
						shape[i][j].initShape();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					String Stmp = contentPane.getComponent(i).getName();
					if (Stmp != null) {
						if (Stmp.contains("shape"))
							contentPane.remove(i--);
						if (Stmp.contains("label"))
							contentPane.remove(i--);
						if (Stmp.contains("YN"))
							contentPane.remove(i--);
					}
				}

				for (int i = 0; i < ROW_NUMBER; i++)
					for (int j = 0; j < COLUMN_NUMBER - 1; j++)
						directionLR[i][j].initDirection();
				for (int i = 0; i < ROW_NUMBER - 1; i++)
					for (int j = 0; j < COLUMN_NUMBER; j++)
						directionUD[i][j].initDirection();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					String Stmp = contentPane.getComponent(i).getName();
					if (Stmp != null) {
						if (Stmp.contains("direction")) {
							ImageIcon icon = new ImageIcon("src/images/idle.png");
							Image img = icon.getImage();
							img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
							icon.setImage(img);
							((JButton) contentPane.getComponent(i)).setIcon(icon);
							contentPane.getComponent(i).setVisible(false);
						}
					}
				}
				RegisterPanel.initRegister();
				comp.initRegister();
				startPoint.x = 0;
				startPoint.y = 1;
				nowPoint.setVisible(false);

				contentPane.requestFocus();
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		for(int i=0; i<contentPane.getComponentCount(); i++)
			contentPane.getComponent(i).setFont(lbfont);
		for(int i=0; i<shapeJP.getComponentCount(); i++)
			shapeJP.getComponent(i).setFont(lbfont);
		
		contentPane.setFocusable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	boolean blockCheck(String input) {
		String[] tmp = input.split("_");
		if (!tmp[0].equals("block"))
			System.out.println("블록이 아님 - 오류클래스 blockCheck");
		int i = Integer.parseInt(tmp[1]);
		int j = Integer.parseInt(tmp[2]);

		return isUsingBlock[i][j];
	}

	boolean directionLRCheck(String input) {
		String[] tmp = input.split("_");
		if (!tmp[0].equals("directionLR"))
			System.out.println("LR방향이 아님 - 오류클래스 directionLRCheck");
		int i = Integer.parseInt(tmp[1]);
		int j = Integer.parseInt(tmp[2]);

		return directionLR[i][j].isOn;
	}

	boolean directionUDCheck(String input) {
		String[] tmp = input.split("_");
		if (!tmp[0].equals("directionUD"))
			System.out.println("LR방향이 아님 - 오류클래스 directionUDCheck");
		int i = Integer.parseInt(tmp[1]);
		int j = Integer.parseInt(tmp[2]);

		return directionUD[i][j].isOn;
	}

	Point underBarSplit(String input) {
		String[] str = input.split("_");
		int i = Integer.parseInt(str[1]);
		int j = Integer.parseInt(str[2]);

		Point p = new Point(i, j);

		return p;
	}

	Point playShape(Point inputP) {
		Point outputP = new Point(-1, -1);
		int cond = -1;
		if (shape[inputP.x][inputP.y].fType == Shape.CONDITION) {
			cond = cont.playIneq(shape[inputP.x][inputP.y], comp);

		} else
			cont.playShape(shape[inputP.x][inputP.y], comp);
		boolean left = true, right = true, up = true, down = true;

		if (inputP.y == 0)
			left = false;
		if (inputP.y == COLUMN_NUMBER - 1)
			right = false;
		if (inputP.x == 0)
			up = false;
		if (inputP.x == ROW_NUMBER - 1)
			down = false;

		if (cond == 1) {
			if (left && directionLR[inputP.x][inputP.y - 1].isOrtho == 0 && directionLR[inputP.x][inputP.y - 1].ifYes) {
				outputP.x = inputP.x;
				outputP.y = inputP.y - 1;
			}
			if (right && directionLR[inputP.x][inputP.y].isOrtho == 1 && directionLR[inputP.x][inputP.y].ifYes) {
				outputP.x = inputP.x;
				outputP.y = inputP.y + 1;
			}
			if (up && directionUD[inputP.x - 1][inputP.y].isOrtho == 0 && directionUD[inputP.x - 1][inputP.y].ifYes) {
				outputP.x = inputP.x - 1;
				outputP.y = inputP.y;
			}
			if (down && directionUD[inputP.x][inputP.y].isOrtho == 1 && directionUD[inputP.x][inputP.y].ifYes) {
				outputP.x = inputP.x + 1;
				outputP.y = inputP.y;
			}
		} else if (cond == 0) {
			if (left && directionLR[inputP.x][inputP.y - 1].isOrtho == 0 && directionLR[inputP.x][inputP.y - 1].ifNo) {
				outputP.x = inputP.x;
				outputP.y = inputP.y - 1;
			}
			if (right && directionLR[inputP.x][inputP.y].isOrtho == 1 && directionLR[inputP.x][inputP.y].ifNo) {
				outputP.x = inputP.x;
				outputP.y = inputP.y + 1;
			}
			if (up && directionUD[inputP.x - 1][inputP.y].isOrtho == 0 && directionUD[inputP.x - 1][inputP.y].ifNo) {
				outputP.x = inputP.x - 1;
				outputP.y = inputP.y;
			}
			if (down && directionUD[inputP.x][inputP.y].isOrtho == 1 && directionUD[inputP.x][inputP.y].ifNo) {
				outputP.x = inputP.x + 1;
				outputP.y = inputP.y;
			}
		} else if (cond == -1) {
			if (left && directionLR[inputP.x][inputP.y - 1].isOrtho == 0) {
				outputP.x = inputP.x;
				outputP.y = inputP.y - 1;
			}
			if (right && directionLR[inputP.x][inputP.y].isOrtho == 1) {
				outputP.x = inputP.x;
				outputP.y = inputP.y + 1;
			}
			if (up && directionUD[inputP.x - 1][inputP.y].isOrtho == 0) {
				outputP.x = inputP.x - 1;
				outputP.y = inputP.y;
			}
			if (down && directionUD[inputP.x][inputP.y].isOrtho == 1) {
				outputP.x = inputP.x + 1;
				outputP.y = inputP.y;
			}
		}
		if (outputP.x == -1 && outputP.y == -1) {
			System.out.println("종료 / 초기화");
			outputP.x = 0;
			outputP.y = 1;
		}

		return outputP;
	}
}
