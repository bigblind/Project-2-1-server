package com.gipf.server.logic;

import com.gipf.server.Server;
import com.gipf.server.logic.gamelogic.GameLogic;
import com.gipf.server.logic.player.PlayerEvent;
import com.gipf.server.logic.utils.Point;

public class LogicController {

	private Server server;
	
	private GameLogic logic;
	private Game game;

	public LogicController(Server server) {
		this.server = server;
	}
	
	public void clientInput(String text, int id) {
		if (text.contains("PlayerEvent")) {
			System.out.println(text);
			int x1 = Integer.parseInt(text.split("x = ")[1]);
			int y1 = Integer.parseInt(text.split("y = ")[1]);
			int x2 = Integer.parseInt(text.split("x = ")[2]);
			int y2 = Integer.parseInt(text.split("y = ")[2]);
			
			if (id == 1) {
				PlayerEvent pe = new PlayerEvent(new Point(x1, y1), new Point(x2, y2), game.getPlayerOne());
				this.logic.playerEventPerformed(pe);
			} else {
				PlayerEvent pe = new PlayerEvent(new Point(x1, y1), new Point(x2, y2), game.getPlayerTwo());
				this.logic.playerEventPerformed(pe);
			}
		}
	}
}
