package ovh.astarivi.mobs.entity.generic;

import software.bernie.geckolib.animation.RawAnimation;

public enum GenericAnimations {
    /// Played when the entity is idling.
    IDLE("idle"),
    /// Default walking animation. This animation can be sped-up.
    WALK("walk"),
    /// If an animal can attack, this is the animation that plays
    /// after a successful hit.
    ATTACK("attack"),
    /// Has a random chance to play when the animal is idle.
    INVESTIGATE("investigate");

    private final RawAnimation animation;

    GenericAnimations(String animationName) {
        this.animation = RawAnimation.begin().thenPlay(animationName);
    }

    public RawAnimation getRawAnimation() {
        return this.animation;
    }
}
