package tetris;

import java.awt.Graphics;
import java.awt.Rectangle;

public class TetrisBlock 
{
	public static enum BlockShape { LBLOCK, JBLOCK, SBLOCK, ZBLOCK, TBLOCK, IBLOCK, SQBLOCK };
	private int x;
	private int y;
	private int row;
	private int col;
	private String color;
	private BlockShape shape;
	private BlockUnit[] units;
	private TetrisPanel owner;
	
	public TetrisBlock()
	{
		
	}
	
	public TetrisBlock(TetrisBlock template)
	{
		// Constructor for ghost blocks
		owner = template.getOwner();
		shape = template.getShape();
		row = template.getRow();
		col = template.getCol();
		x = template.getX();
		y = template.getY();
		units = new BlockUnit[4];
		BlockUnit[] templateUnits = template.getUnits();
		for (int i = 0; i < units.length; i++)
		{
			units[i] = new BlockUnit(this, templateUnits[i]);
		}
	}
	
	public TetrisBlock(int shapeIndex, TetrisPanel owner)
	{
		this.owner = owner;
		this.shape = BlockShape.values()[shapeIndex];
		row = -2;
		col = 3;//(int) (Math.random() * 100 % 7);
		x = col*25 + TetrisPanel.BGX;
		y = row*25 + TetrisPanel.BGY;
		if (shape == BlockShape.LBLOCK)
		{
			color = "orange";
			units = new BlockUnit[4];
			units[0] = new BlockUnit(this, 0, 0);
			units[1] = new BlockUnit(this, 0, 1);
			units[2] = new BlockUnit(this, 0, 2);
			units[3] = new BlockUnit(this, 1, 0);
		}
		else if (shape == BlockShape.JBLOCK)
		{
			color = "blue";
			units = new BlockUnit[4];
			units[0] = new BlockUnit(this, 0, 0);
			units[1] = new BlockUnit(this, 0, 1);
			units[2] = new BlockUnit(this, 0, 2);
			units[3] = new BlockUnit(this, 1, 2);
		} 
		else if (shape == BlockShape.SBLOCK)
		{
			color = "green";
			units = new BlockUnit[4];
			units[0] = new BlockUnit(this, 0, 1);
			units[1] = new BlockUnit(this, 0, 2);
			units[2] = new BlockUnit(this, 1, 0);
			units[3] = new BlockUnit(this, 1, 1);
		}
		else if (shape == BlockShape.ZBLOCK)
		{
			color = "yellow";
			units = new BlockUnit[4];
			units[0] = new BlockUnit(this, 0, 0);
			units[1] = new BlockUnit(this, 0, 1);
			units[2] = new BlockUnit(this, 1, 1);
			units[3] = new BlockUnit(this, 1, 2);
		}
		else if (shape == BlockShape.TBLOCK)
		{
			color = "magenta";
			units = new BlockUnit[4];
			units[0] = new BlockUnit(this, 0, 0);
			units[1] = new BlockUnit(this, 0, 1);
			units[2] = new BlockUnit(this, 0, 2);
			units[3] = new BlockUnit(this, 1, 1);
		}
		else if (shape == BlockShape.IBLOCK)
		{
			color = "cyan";
			units = new BlockUnit[4];
			units[0] = new BlockUnit(this, 0, 0);
			units[1] = new BlockUnit(this, 0, 1);
			units[2] = new BlockUnit(this, 0, 2);
			units[3] = new BlockUnit(this, 0, 3);
		}
		else if (shape == BlockShape.SQBLOCK)
		{
			color = "red";
			units = new BlockUnit[4];
			units[0] = new BlockUnit(this, 0, 0);
			units[1] = new BlockUnit(this, 0, 1);
			units[2] = new BlockUnit(this, 1, 0);
			units[3] = new BlockUnit(this, 1, 1);
		}
	}
	
	public void update()
	{
		Rectangle[] unitRects = new Rectangle[units.length];
		for (int i = 0; i < units.length; i++)
			unitRects[i] = new Rectangle((int)units[i].getX(), (int)units[i].getY() + 25, 25, 25);
		if (owner.checkCollision(unitRects))
		{
			if (row < 0)
			{
				owner.endGame();
				return;
			}
			owner.dropNextBlock();
		}
		move("down");
	}
	
	public void draw(Graphics g)
	{
		for (int i = 0; i < units.length; i++)
			units[i].draw(g);
	}
	public void drawGhost(Graphics g)
	{
		TetrisBlock ghost = new TetrisBlock(this);
		ghost.hardDrop();
		for (int i = 0; i < ghost.getUnits().length; i++)
			ghost.getUnits()[i].draw(g);
	}
	public void hardDrop()
	{
		while(move("down")) {}
	}
	public boolean move(String direction)
	{
		Rectangle[] unitRects = new Rectangle[units.length];
		if (direction == "down")
		{
			for (int i = 0; i < units.length; i++)
				unitRects[i] = new Rectangle((int)units[i].getX(), (int)units[i].getY() + 25, 25, 25);
			if (owner.checkCollision(unitRects))
			{
				return false;
			}
			y += 25;
			row += 1;
		}
		if (direction == "up")
		{
			for (int i = 0; i < units.length; i++)
				unitRects[i] = new Rectangle((int)units[i].getX(), (int)units[i].getY() - 25, 25, 25);
			if (owner.checkCollision(unitRects)) return false;
			y -= 25;
			row -= 1;
		}
		if (direction == "left")
		{
			for (int i = 0; i < units.length; i++)
				unitRects[i] = new Rectangle((int)units[i].getX() - 25, (int)units[i].getY(), 25, 25);
			if (owner.checkCollision(unitRects)) return false;
			x -= 25;
			col -= 1;
		}
		if (direction == "right")
		{
			for (int i = 0; i < units.length; i++)
				unitRects[i] = new Rectangle((int)units[i].getX() + 25, (int)units[i].getY(), 25, 25);
			if (owner.checkCollision(unitRects)) return false;
			x += 25;
			col += 1;
		}
		for (BlockUnit unit : units)
		{
			unit.move(direction);
		}
		return true;
	}
	
	public void rotateLeft()
	{
		if (shape == BlockShape.SQBLOCK)
			return;
		else
		{
			Rectangle[] unitRects = new Rectangle[units.length];
			for (int i = 0; i < units.length; i++)
			{
				int tempRow = units[i].getRelativeRow() + 1;
				int tempCol = units[i].getRelativeCol();
				int tempRow2 = tempRow;
				tempRow = 1 - tempCol;
				tempCol = tempRow2;
				unitRects[i] = new Rectangle(tempCol*25 + x, tempRow*25 + y, 25, 25);
			}
			if (owner.checkCollision(unitRects)) return;
			for (int i = 0; i < units.length; i++)
			{
				int tempRow = units[i].getRelativeRow() + 1;
				int tempCol = units[i].getRelativeCol();
				units[i].setRelativeRow(1 - tempCol);
				units[i].setRelativeCol(tempRow);
			}
		}
	}
	
	public void rotateRight()
	{
		if (shape == BlockShape.SQBLOCK)
			return;
		else
		{
			Rectangle[] unitRects = new Rectangle[units.length];
			for (int i = 0; i < units.length; i++)
			{
				int tempRow = units[i].getRelativeRow() + 1;
				int tempCol = units[i].getRelativeCol();
				int tempRow2 = tempRow;
				tempRow = tempCol - 1;
				tempCol = 2 - tempRow2;
				unitRects[i] = new Rectangle(tempCol*25 + x, tempRow*25 + y, 25, 25);
			}
			if (owner.checkCollision(unitRects)) return;
			for (int i = 0; i < units.length; i++)
			{
				int tempRow = units[i].getRelativeRow() + 1;
				int tempCol = units[i].getRelativeCol();
				units[i].setRelativeRow(tempCol - 1);
				units[i].setRelativeCol(2 - tempRow);
			}
		}
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public BlockShape getShape() {
		return shape;
	}
	public void setShape(BlockShape shape) {
		this.shape = shape;
	}
	public BlockUnit[] getUnits() {
		return units;
	}
	public void setUnits(BlockUnit[] units) {
		this.units = units;
	}
	public String getColor()
	{
		return color;
	}
	public TetrisPanel getOwner()
	{
		return owner;
	}
	public int getRow() {
		return row;
	}
	public int getNumRows()
	{
		int numRows = 0;
		for (int i = 0; i < units.length; i++)
		{
			if (units[i].getRow() - row > numRows)
				numRows = units[i].getRow() - col;
		}
		return numRows;
	}
	public int getNumCols()
	{
		int numCols = 0;
		for (int i = 0; i < units.length; i++)
		{
			if (units[i].getCol() - col > numCols)
				numCols = units[i].getCol() - col;
		}
		return numCols;
	}
	public int getCol() 
	{
		return col;
	}
}
