package com.gipf.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClient {

	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	
	private Thread receive;

	public ServerClient(Socket socket) throws IOException {
		this.socket = socket;
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
		this.receive = new Thread() {
			public void run() {
				String input;
				try {
					while((input = in.readLine()) != null) {
						System.out.println(input);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		this.receive.start();
	}
	
	public synchronized void send(String send) {
		this.out.println(send);
	}
	
	public synchronized void receive(String string) {
		this.out.println(string);
	}

	public Socket getSocket() {
		return this.socket;
	}
	
}
