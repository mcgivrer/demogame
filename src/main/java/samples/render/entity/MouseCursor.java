package samples.render.entity;

import java.awt.Color;
import java.awt.image.BufferedImage;

import samples.Sample;
import samples.object.entity.GameObject;

public class MouseCursor extends GameObject {

	public MouseCursor(String name) {
		super(name);
		color = Color.WHITE;
		width = 16;
		height = 16;
		type = GameObjectType.OTHER;
	}

	public void setCursorImage(BufferedImage image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.type = GameObjectType.IMAGE;
	}

	@Override
	public void update(Sample ga, double elapsed) {
		bbox.update(this);
	}
}