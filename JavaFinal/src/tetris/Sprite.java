package tetris;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import tetris.Game;

/**
 * Represents a spritesheet.
 *
 * @author maartenhus
 */
public class Sprite
{
	private BufferedImage image;
	private int x;
	private int y;
	private int width;
	private int height;

	public Sprite(String img)
	{
		URL imageUrl = Game.class.getResource(img);

		try
		{
			image = ImageIO.read(imageUrl);
			height = image.getHeight();
			width = image.getWidth();
		}
		catch(IOException e){}
	}
	
	public static BufferedImage loadImage(String img)
	{
		URL imageURL = Game.class.getResource(img);
		BufferedImage image = null;
		try
		{
			image = ImageIO.read(imageURL);
		} catch(IOException e) 
		{
			e.printStackTrace();
			System.out.println("Image not found at: " + img);
			image = null;
		}
		return image;
	}
	
	public void setSprite(Sprite sprite)
	{
		this.x = (int)sprite.getX();
		this.y = (int)sprite.getY();
		this.width = (int)sprite.getWidth();
		this.height = (int)sprite.getHeight();
	}
	public boolean isImageNull()
	{
		if (image == null) return true;
		else return false;
	}
	public synchronized BufferedImage getImage()
	{
		return image.getSubimage(0, 0, width, height);
	}
	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getY()
	{
		return y;
	}

	public int getHeight()
	{
		return image.getHeight();
	}

	public int getWidth()
	{
		return image.getWidth();
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}
}