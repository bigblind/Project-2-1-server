package com.gipf.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import com.gipf.server.logic.LogicController;

public class Server {

	private ArrayList<ServerClient> clients;
	private ServerConsole console;
	private ServerSocket socket;

	private int port;

	private LogicController controller;
	private String logic;

	public Server(int port) {
		this.port = port;
	}
	
	private void init() {
		this.console = new ServerConsole(this);
		this.console.setVisible(true);
		this.clients = new ArrayList<ServerClient>();
		this.console.append("Welcome to console v1.0");
		this.console.append("Please select game mode using commands:");
		this.console.append("/mode basic   /mode standard");
	}

	public void start() {
		try {
			this.console.append("Server is starting.");
			this.socket = new ServerSocket(this.port);
			this.console.append("Waiting for clients to connect.");
			this.connectClients();
			this.console.append("All clients connected!");

			this.controller = new LogicController(this, logic);
			this.controller.sendClientInit();
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
		while (this.clients.size() < 2) {
			try {
				this.clients.add(new ServerClient(this.socket.accept(), this, this.clients.size() + 1));
				this.console.append("Connection with client established.");
			} catch (IOException e) {
				this.console.append("Connection with client failed.");
				e.printStackTrace();
			}
		}
	}

	public void disconnectClient(int id) {
		this.clients.remove(id - 1);
	}

	public synchronized void sendToAll(String send) {
		for (int i = 0; i < this.clients.size(); i++) {
			this.clients.get(i).send(send);
		}
	}

	public synchronized void sendToClient(String send, int index) {
		this.clients.get(index).send(send);
	}

	public void clientInput(String text, int id) {
		this.controller.clientInput(text, id);
	}

	public void consoleInput(String string) {
		if (string.equals("/quit")) {
			this.quit();
		} else if (string.equals("/start")) {
			if (!logic.equals("")) start();
		} else if (string.equals("/mode basic")) {
			this.logic = "basic";
			this.console.append("Basic game mode selected.");
		} else if (string.equals("/mode standard")) {
			this.logic = "standard";
			this.console.append("Standard game mode selected.");
		} else {
			this.console.append("Invalid console input!");
		}
	}

	public ServerConsole getConsole() {
		return this.console;
	}

	public static void main(String[] args) {
		int port = 2603;

		Server server = new Server(port);
		server.init();
	}
}
