package org.bukkit.event.entity;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a human entity's food level changes
 */
public class FoodLevelChangeEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private int level;

    public FoodLevelChangeEvent(final HumanEntity what, final int level) {
        super(what);
        this.level = level;
    }

    @Override
    public HumanEntity getEntity() {
        return (HumanEntity) entity;
    }

    /**
     * Gets the resultant food level that the entity involved in this event should be set to.
     * <p>
     * Where 20 is a full food bar and 0 is an empty one.
     *
     * @return The resultant food level
     */
    public int getFoodLevel() {
        return level;
    }

    /**
     * Sets the resultant food level that the entity involved in this event should be set to
     *
     * @param level the resultant food level that the entity involved in this event should be set to
     */
    public void setFoodLevel(int level) {
        int level1 = level;
        if (level1 > 20) level1 = 20;
        else if (level1 < 0) level1 = 0;

        this.level = level1;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
