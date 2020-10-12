package samples.render;

import java.awt.Rectangle;

import samples.Sample;
import samples.camera.entity.Camera;
import samples.input.InputHandler;
import samples.object.entity.GameObject;
import samples.system.GameSystem;

/**
 * The rendering interface to render GameObjects on a display.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2020
 *
 */
public interface IRenderer extends GameSystem {

	/**
	 * Add an object to the render pipeline.
	 * 
	 * @param object the GameObject to be added to the renderering pipeline. It will
	 *               be sorted regarding its layer and priority.
	 */
	void addObject(GameObject object);

	/**
	 * the active Camera from the Renderer.
	 * 
	 * @return
	 */
	Camera getActiveCamera();

	/**
	 * Render the image according to already updated objects.
	 * 
	 * @param game    the parent Game running this service.
	 * @param realFps the real frame per seconds value to be rendered in debug mode
	 *                (if requested)
	 */
	void render(Sample game, long realFps);

	/**
	 * Add a KeyListener to the java JFrame.
	 * 
	 * @param ih the InputHandler to support.
	 */
	void addKeyListener(InputHandler ih);

	/**
	 * Retrieve the viewport to display.
	 * 
	 * @return a simple Rectangle corresponding to the view port to be rendered.
	 */
	Rectangle getViewport();

	/**
	 * Set the display debug mode.
	 * 
	 * @param debug
	 */
	void setDebug(int debug);
}