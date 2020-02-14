/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * DemoGame
 * 
 * @year 2019
 */
package core.gfx;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * TheAnimation component is used to switch between frames according to a
 * sequence of images and wait some frame delay.
 * 
 * @author Frédéric Delorme <fredric.delorme@gmail.com>
 *
 */
public class Animation {

	public List<BufferedImage> frameImages = new ArrayList<>();
	public List<Integer> frameTime = new ArrayList<>();
	public double elapsed;
	public int frameIndex = 0;

	
	public  Animation(){
		reset();
	}
	
	/**
	 * Compute next frame of animation for the MapObject.
	 * 
	 * @param mo      the MapObject to be updated.
	 * @param elapsed the elapsed time since previous call.
	 * @return the MapObject with frame and imageBuffer updated.
	 */
	public BufferedImage animate(double elapsed) {
		if (this.elapsed>(frameTime.get(frameIndex) * elapsed)) {
			frameIndex++;
			if (frameIndex == frameImages.size()) {
				frameIndex = 0;
			}
			this.elapsed = 0;
		}
		this.elapsed += elapsed;
		return frameImages.get(frameIndex);
	}
	
	public void reset() {
		this.elapsed=0;
		this.frameIndex=0;
	}
}
