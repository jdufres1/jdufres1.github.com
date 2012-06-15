package tetris;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class BlockUnit extends Rectangle
{
	private static final long serialVersionUID = 1L;
	private int row;
	private int col;
	private TetrisBlock parentBlock;
	private String color;
	private static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	
	public BlockUnit(TetrisBlock parentBlock, BlockUnit template)
	{
		this.parentBlock = parentBlock;
		row = template.getRow();
		col = template.getCol();
		color = "ghost";
		x = (int) template.getX();
		y = (int) template.getY();
		width = (int) template.getWidth();
		height = (int) template.getHeight();
	}
	
	public BlockUnit(TetrisBlock parentBlock, int row, int col)
	{
		this.parentBlock = parentBlock;
		this.row = row + parentBlock.getRow();
		this.col = col + parentBlock.getCol();
		this.color = parentBlock.getColor();
		x = col*25 + parentBlock.getX();
		y = row*25 + parentBlock.getY();
		width = 25;
		height = 25;
	}

	public void draw(Graphics g)
	{
		g.drawImage(images.get(color), x, y, null);
	}
	
	public void setStatic()
	{
	/*	row = parentBlock.getRow() + row;
		col = parentBlock.getCol() + col;*/
		parentBlock = null;
	}
	
	public void move(String direction)
	{
		if (direction == "down")
		{
			y += 25;
			row += 1;
		}
		if (direction == "up")
		{
			y -= 25;
			row -= 1;
		}
		if (direction == "left")
		{
			x -= 25;
			col -= 1;
		}
		if (direction == "right")
		{
			x += 25;
			col += 1;
		}
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public TetrisBlock getParentBlock() {
		return parentBlock;
	}

	public void setParentBlock(TetrisBlock parentBlock) {
		this.parentBlock = parentBlock;
	}
	public int getCol()
	{
		return col;
	}
	public void setCol(int col)
	{
		int tempCol = this.col;
		this.col = col;
		x = col = (tempCol - col) * 25;
	}
	public int getRow()
	{
		return row;
	}
	public void setRow(int row)
	{
		int tempRow = this.row;
		this.row = row;
		y = (tempRow - row) * 25;
	}
	public int getRelativeRow()
	{
		try 
		{
			return row - parentBlock.getRow();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			return row; 
		}
	}
	public void setRelativeRow(int row)
	{
		try
		{
			this.row = row + parentBlock.getRow();
			y = row*25 + parentBlock.getY();
		}
		catch (NullPointerException e){}
	}
	public int getRelativeCol()
	{
		try 
		{
			return col - parentBlock.getCol();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			return col; 
		}
	}
	public void setRelativeCol(int col)
	{
		try
		{
			this.col = col + parentBlock.getCol();
			x = col*25 + parentBlock.getX();
		}
		catch (NullPointerException e){}
	}
	public static void initSprites()
	{
		images.put("red", Sprite.loadImage("images/units/redblock.png"));
		images.put("orange", Sprite.loadImage("images/units/orangeblock.png"));
		images.put("yellow", Sprite.loadImage("images/units/yellowblock.png"));
		images.put("green", Sprite.loadImage("images/units/greenblock.png"));
		images.put("cyan", Sprite.loadImage("images/units/cyanblock.png"));
		images.put("blue", Sprite.loadImage("images/units/blueblock.png"));
		images.put("magenta", Sprite.loadImage("images/units/magentablock.png"));
		images.put("ghost", Sprite.loadImage("images/units/ghostblock.png"));
	}
}
