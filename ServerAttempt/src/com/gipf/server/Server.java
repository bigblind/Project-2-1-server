package com.gipf.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

	private ArrayList<ServerClient> clients;
	private ServerConsole console;
	private ServerSocket socket;

	private int port;

	public Server(int port) {
		this.console = new ServerConsole(this);
		this.console.setVisible(true);
		this.clients = new ArrayList<ServerClient>();
		this.port = port;
	}

	public void start() {
		try {
			this.console.append("Server is starting.");
			this.socket = new ServerSocket(this.port);
			this.console.append("Console initialised.");
			this.console.append("Waiting for clients to connect.");
			this.connectClients();
			this.console.append("All clients connected!");
		} catch (IOException e) {
			this.console.append("Server start has failed");
			e.printStackTrace();
		}
	}

	public void quit() {
		this.sendToAll("quit");
		try {
			this.socket.close();
			this.console.append("Server has closed.");
			System.exit(0);
		} catch (IOException e) {
			this.console.append("Server couldn't close.");
			e.printStackTrace();
		}
	}

	private void connectClients() {
		while (clients.size() < 2) {
			try {
				this.clients.add(new ServerClient(this.socket.accept()));
				this.console.append("Connection with client established.");
			} catch (IOException e) {
				this.console.append("Connection with client failed.");
				e.printStackTrace();
			}
		}
	}

	public synchronized void sendToAll(String send) {
		for (int i = 0; i < this.clients.size(); i++) {
			this.clients.get(i).send(send);
		}
	}

	public void consoleInput(String string) {
		System.out.println(string);
		if (string.equals("quit")) {
			quit();
		} else {
			sendToAll(string);
		}
	}

	public static void main(String[] args) {
		int port = 3620;

		Server server = new Server(port);
		server.start();
	}
}
