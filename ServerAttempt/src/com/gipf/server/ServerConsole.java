package com.gipf.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ServerConsole extends JFrame {

	private static final long serialVersionUID = 677145013503464621L;

	private final JPanel contentPane;
	private final JTextField txtInput;
	private final JTextArea txtrOutput;
	private final JButton btnSend;

	public ServerConsole(final Server server) {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 700, 400);
		this.contentPane = new JPanel();
		this.contentPane.setBackground(Color.WHITE);
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		this.contentPane.setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.contentPane.add(scrollPane, BorderLayout.CENTER);

		this.txtrOutput = new JTextArea();
		this.txtrOutput.setEditable(false);
		this.txtrOutput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.txtrOutput.setFont(new Font("Segoe UI", 0, 14));
		scrollPane.setViewportView(this.txtrOutput);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(20, 40));
		panel.setBorder(BorderFactory.createEmptyBorder());
		this.contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		this.txtInput = new JTextField();
		this.txtInput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		panel.add(this.txtInput, BorderLayout.CENTER);
		this.txtInput.setColumns(10);
		this.txtInput.setFont(new Font("Segoe UI", 0, 14));

		this.btnSend = new JButton("send");
		this.btnSend.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.GRAY));
		this.btnSend.setPreferredSize(new Dimension(100, 40));
		this.btnSend.setContentAreaFilled(false);
		this.btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = txtInput.getText();
				if (!text.equals("")) {
					txtrOutput.append(text + "\n");
					txtInput.setText("");
					Thread handle = new Thread() {
						public void run() {
							server.consoleInput(text);
						}
					};
					handle.start();
				}
			}
		});
		panel.add(this.btnSend, BorderLayout.EAST);

		this.getRootPane().setDefaultButton(btnSend);
	}

	public void append(String string) {
		this.txtrOutput.append(string + "\n");
	}
}
