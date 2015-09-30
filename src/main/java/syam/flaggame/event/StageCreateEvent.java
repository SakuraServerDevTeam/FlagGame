/**
 * FlagGame - Package: syam.flaggame.event Created: 2012/10/13 21:48:16
 */
package syam.flaggame.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import syam.flaggame.game.Stage;

/**
 * StageCreateEvent (StageCreateEvent.java)
 * 
 * @author syam(syamn)
 */
public class StageCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;

    private final CommandSender creator;
    private final Stage stage;

    public StageCreateEvent(CommandSender creator, Stage stage) {
        this.creator = creator;
        this.stage = stage;
    }

    public CommandSender getCreator() {
        return this.creator;
    }

    public Stage getStage() {
        return this.stage;
    }

    public String getStageName() {
        if (this.stage == null) return null;
        return this.stage.getName();
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    /* ******************** */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
