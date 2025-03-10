package ovh.astarivi.mobs.entity.generic;

public enum GenericControllers {
    WALK("walk_controller"),
    ATTACK("attack_controller"),
    ACTION("action_controller");

    private final String name;

    GenericControllers(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
