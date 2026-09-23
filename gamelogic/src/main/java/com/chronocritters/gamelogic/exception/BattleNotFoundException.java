package com.chronocritters.gamelogic.exception;

/**
 * Battles live in memory, so a restart or the cleanup sweep can take one away
 * while a client still holds its id. That is a gone resource rather than a bad
 * request, and the client shows the player a message instead of a blank redirect.
 */
public class BattleNotFoundException extends RuntimeException {
    public BattleNotFoundException(String battleId) {
        super("Battle '" + battleId + "' is no longer active");
    }
}
