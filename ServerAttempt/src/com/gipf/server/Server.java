package com.gipf.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import com.gipf.server.logic.LogicController;
import com.gipf.server.logic.board.Board;
import com.gipf.server.logic.player.Player;
import com.gipf.server.logic.player.PlayerEvent;
import com.gipf.server.logic.utils.Point;

public class Server {

	private ArrayList<ServerClient> clients;
	private ServerConsole console;
	private ServerSocket socket;
	
	private int port;

	private LogicController controller;

	public Server(int port) {
		this.console = new ServerConsole(this);
		this.console.setVisible(true);
		this.clients = new ArrayList<ServerClient>();
		this.port = port;
		
		this.controller = new LogicController(this);
	}

	public void start() {
		try {
			this.console.append("Server is starting.");
			this.socket = new ServerSocket(this.port);
			this.console.append("Console initialised.");
			this.console.append("Waiting for clients to connect.");
			this.connectClients();
			this.console.append("All clients connected!");
			
			this.controller = new LogicController(this);
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

	public synchronized void sendToAll(String send) {
		for (int i = 0; i < this.clients.size(); i++) {
			this.clients.get(i).send(send);
		}
	}
	
	public synchronized void sentToClient(String send, int index) {
		this.clients.get(index).send(send);
	}
	
	public void clientInput(String text, int id) {
		this.controller.clientInput(text, id);
	}

	public void consoleInput(String string) {
		if (string.equals("quit")) {
			quit();
		} else {
			sendToAll(string);
		}
	}

	public static void main(String[] args) {
		int port = 3620;

		Server server = new Server(port);
//		server.start();
		PlayerEvent e = new PlayerEvent(new Point(0,0), new Point(1,1), new Player(Board.BLACK_VALUE));
		server.clientInput(e.toString(), 2);
	}
}
