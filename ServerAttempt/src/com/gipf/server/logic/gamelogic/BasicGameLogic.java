package com.gipf.server.logic.gamelogic;

import com.gipf.server.logic.Game;
import com.gipf.server.logic.LogicController;
import com.gipf.server.logic.player.PlayerEvent;

public class BasicGameLogic extends GameLogic {

	public BasicGameLogic(Game game, LogicController controller) {
		super(game, controller);
	}

	public void playerEventPerformed(PlayerEvent e) {
		if (!this.game.getBoard().isValidMove(e.getFromPoint(), e.getToPoint())) {
			this.controller.sendMoveValidity(false);
			return;
		}
		this.controller.sendMoveValidity(true);
		this.game.getBoard().place(e.getPlayer().getStoneColor(), e.getFromPoint(), e.getToPoint());
		this.getCurrentPlayer().setStoneAccount(this.getCurrentPlayer().getStoneAccount() - 1);

		if (this.handleRows()) return;

		this.moveToNextPlayer();
		if (this.checkForWin()) {
			this.controller.sendWinLoseUpdate(this.returnWinner());
		}
	}
}