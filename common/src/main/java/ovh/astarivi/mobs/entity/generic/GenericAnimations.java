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
    /// Eat grass to heal up
    EAT("eat"),
    /// Has a random chance to play when the animal is idle.
    INVESTIGATE("investigate");

    private final RawAnimation animation;
    private final String name;

    GenericAnimations(String animationName) {
        this.name = animationName;
        this.animation = RawAnimation.begin().thenPlay(animationName);
    }

    public RawAnimation getRawAnimation() {
        return this.animation;
    }

    public String getName() {
        return name;
    }
}
