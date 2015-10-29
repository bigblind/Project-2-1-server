package com.gipf.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClient {

	private final Server server;
	private int id;
	
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	
	private Thread receive;

	public ServerClient(Socket socket, final Server server, int id) throws IOException {
		this.server = server;
		this.id = id;
		this.socket = socket;
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
		this.receive = new Thread() {
			public void run() {
				String input;
				try {
					while((input = in.readLine()) != null) {
						receive(input);
					}
				} catch (IOException e) {
					server.disconnectClient(id);
					server.getConsole().append("Client " + id + " disconnected!");
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
		this.server.clientInput(string, this.id);
	}

	public Socket getSocket() {
		return this.socket;
	}
	
}
