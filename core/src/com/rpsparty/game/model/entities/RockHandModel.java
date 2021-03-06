package com.rpsparty.game.model.entities;

public class RockHandModel extends EntityModel {

    /**
     * construtor do modelo Rock Hand
     *
     * @param x coordenada x de RockHand
     * @param y coordenada y de RockHand
     * @param rotation rotacao de RockHand
     */
    public RockHandModel(float x, float y, float rotation) {
        super(x, y, rotation);
    }

    @Override
    public ModelType getType() {
        return ModelType.ROCKHAND;
    }
}
