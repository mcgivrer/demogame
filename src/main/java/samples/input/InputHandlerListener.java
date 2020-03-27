package samples.input;

import java.awt.event.KeyListener;

/**
 * This InputHandlerListener interface will serve all the systems which want to
 * consume input events.
 */
public interface InputHandlerListener extends KeyListener {
    /**
     * The input method is design to let the registered GameSystem to process input
     * during its own pace. For GameObject during update or for Sample during loop.
     * 
     * @param ih
     */
    public void input(InputHandler ih);

}
