package edu.csc413.tankgame;

import edu.csc413.tankgame.model.*;
import edu.csc413.tankgame.view.*;

import java.util.*;
import java.awt.event.ActionEvent;

public class GameDriver {
    private final MainView mainView;
    private final RunGameView runGameView;
    private final GameWorld gameWorld;

    public GameDriver() {
        mainView = new MainView(this::startMenuActionPerformed);
        runGameView = mainView.getRunGameView();
        gameWorld = new GameWorld();
    }

    public void start() {
        mainView.setScreen(MainView.Screen.START_GAME_SCREEN);
    }

    private void startMenuActionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case StartMenuView.START_BUTTON_ACTION_COMMAND -> runGame();
            case StartMenuView.EXIT_BUTTON_ACTION_COMMAND -> mainView.closeGame();
            default -> throw new RuntimeException("Unexpected action command: " + actionEvent.getActionCommand());
        }
    }

    private void runGame() {
        mainView.setScreen(MainView.Screen.RUN_GAME_SCREEN);
        Runnable gameRunner = () -> {
            setUpGame();
            while (updateGame()) {
                runGameView.repaint();
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            }
            mainView.setScreen(MainView.Screen.END_MENU_SCREEN);
            resetGame();
        };
        new Thread(gameRunner).start();
    }

    /**
     * setUpGame is called once at the beginning when the game is started. Entities that are present from the start
     * should be initialized here, with their corresponding sprites added to the RunGameView.
     */
    private void setUpGame() {
        // TODO: Implement.
        Tank playerTank = new playerTank(
                Constants.PLAYER_TANK_ID,
                Constants.PLAYER_TANK_INITIAL_X,
                Constants.PLAYER_TANK_INITIAL_Y,
                Constants.PLAYER_TANK_INITIAL_ANGLE
        );
        Tank aiTank = new aiTank (
                Constants.AI_TANK_1_ID,
                Constants.AI_TANK_1_INITIAL_X,
                Constants.AI_TANK_1_INITIAL_Y,
                Constants.AI_TANK_1_INITIAL_ANGLE
        );

        List<WallInformation> wallInfos = WallInformation.readWalls();
        int id = 0;
        for(WallInformation wallInfo: wallInfos){
            //Create a Wall Entity, add it to gameWorld
            runGameView.addSprite(
                    "wall-"+ id++, wallInfo.getImageFile(), wallInfo.getX(), wallInfo.getY(), 0.0);
        }

        gameWorld.addEntity(playerTank);
        gameWorld.addEntity(aiTank);

        gameWorld.moveEntitiesToAdd();

        runGameView.addSprite(playerTank.getId(),
                RunGameView.PLAYER_TANK_IMAGE_FILE,
                playerTank.getX(),
                playerTank.getY(),
                playerTank.getAngle());
        runGameView.addSprite(aiTank.getId(),
                RunGameView.AI_TANK_IMAGE_FILE,
                aiTank.getX(),
                aiTank.getY(),
                aiTank.getAngle());

    }

    /**
     * updateGame is repeatedly called in the gameplay loop. The code in this method should run a single frame of the
     * game. As long as it returns true, the game will continue running. If the game should stop for whatever reason
     * (e.g. the player tank being destroyed, escape being pressed), it should return false.
     */
    private boolean updateGame() {
        // TODO: Implement.
        for(Entity entity: gameWorld.getEntities()){
            entity.move(gameWorld);
            entity.checkBounds(gameWorld);
        }
        for(Entity entity: gameWorld.getEntitiesToAdd()){
            runGameView.addSprite(
                    entity.getId(), RunGameView.SHELL_IMAGE_FILE,entity.getX(), entity.getY(), entity.getAngle());
        }
        gameWorld.moveEntitiesToAdd();

        //A bunch of other things like collision detection, bounce checking, etc

        for(Entity entity: gameWorld.getEntities()){
            runGameView.setSpriteLocationAndAngle(entity.getId(), entity.getX(), entity.getY(), entity.getAngle());
        }
        for(Entity entity: gameWorld.getEntitiesToRemove()){
            runGameView.removeSprite(entity.getId());
        }
        gameWorld.moveEntitiesToRemove();
        return true;
    }
    private boolean areEntitiesColliding(Entity entityA, Entity entityB){
        return true;
        //return true if both entities are colliding, false otherwise
    }
    private void handleCollision(Entity entityA, Entity entityB){
        if(entityA instanceof Tank && entityB instanceof Tank){
            //handle tank-tank collision
            //treat 1 tank as stationary
        }
        else if(entityA instanceof Tank && entityB instanceof Shell){
            //handle tank-shell collision
            //shell should disappear, tank -hp
        }
    }
    /**
     * resetGame is called at the end of the game once the gameplay loop exits. This should clear any existing data from
     * the game so that if the game is restarted, there aren't any things leftover from the previous run.
     */
    private void resetGame() {
        // TODO: Implement. reset all the data
        runGameView.reset();
    }

    public static void main(String[] args) {
        GameDriver gameDriver = new GameDriver();
        gameDriver.start();
    }
}
