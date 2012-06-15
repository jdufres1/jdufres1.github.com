package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class TetrisPanel extends JPanel implements KeyListener
{
	private static final long serialVersionUID = 1L;
	private final BufferedImage background = Sprite.loadImage("images/background2.png");
	public final static int BGX = 50;
	public final static int BGY = 50;
	// One second = 1000000000
	private long tickTime = 1000000000;
	private long timeSinceTick = 0;
	private TetrisBlock currentBlock;
	private TetrisBlock nextBlock;
	private TetrisBlock savedBlock = null;
	private boolean blockSaved = false;
	private NextBlockPanel nextBlockPanel;
	private SavedBlockPanel savedBlockPanel;
	private LevelPanel levelPanel;
	private BlockUnit[][] staticBlocks;
	private Rectangle[] solids = new Rectangle[5];
	private Game game;
	private int points;
	private PointPanel pointPanel;
	private int level = 1;
	private int rowsDestroyedThisLevel = 0;
	private boolean playStarted = false;
	
	public static Color transparent = new Color(255,255,255,0);
	
	public TetrisPanel(final Game game)
	{
		setSize(450,565);
		setLayout(null);
		this.game = game;
		
		JMenuBar menuBar = new JMenuBar();
		final JMenu menu = new JMenu("Menu");
			JMenuItem restartButton = new JMenuItem("Restart");
			restartButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					endGame();
				}
			});
			JMenuItem howToPlayButton = new JMenuItem("Help");
			howToPlayButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					game.help();
				}
			});
		menu.add(restartButton);
		menu.addSeparator();
		menu.add(howToPlayButton);
		menuBar.add(menu);
		game.setJMenuBar(menuBar);
		menu.setEnabled(false);
		
		final JButton playButton = new JButton();
		playButton.setBorderPainted(false);
		BufferedImage playImg = null;
		try
		{
			playImg = ImageIO.read(getClass().getResource("images/PlayLabel.png"));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			System.out.println("Play Icon image not found.");
		}
		ImageIcon playIcon = new ImageIcon(playImg);
		playButton.setIcon(playIcon);
		int width = playIcon.getIconWidth();
		int height = playIcon.getIconHeight();
		playButton.setLocation(45, 208);
		playButton.setSize(width, height);
		playButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				startPlaying();
				playStarted = true;
				playButton.setVisible(false);
				game.setPaused(false);
				menu.setEnabled(true);
			}
		});
		add(playButton);
		
		// There's a lot of image loading that goes on in these constructors, so I want it done BEFORE 
		// the player presses the button to start playing.
		nextBlockPanel = new NextBlockPanel();
		savedBlockPanel = new SavedBlockPanel();
		pointPanel = new PointPanel();
		levelPanel = new LevelPanel();
	}
	public void startPlaying()
	{
		game.addKeyListener(this);
		staticBlocks = new BlockUnit[20][10];
		BlockUnit.initSprites();
		currentBlock = new TetrisBlock((int) (Math.random() * 100 % 7), this);
		nextBlock();
		solids[0] = new Rectangle(0, 0, BGX, getHeight());
		solids[1] = new Rectangle(0, -1, BGX + 75, BGY);
		solids[2] = new Rectangle(BGX + 175, -1, getWidth() - BGX - 175, BGY);
		solids[3] = new Rectangle(0, BGY + 500, getWidth(), 5);
		solids[4] = new Rectangle(300, 0, getWidth() - 300, getHeight() );
		
		setLayout(null);
		
		nextBlockPanel.setBackground(transparent);
		nextBlockPanel.setSize(new Dimension(100,75));
		nextBlockPanel.setLocation(BGX + 275, 50);
		add(nextBlockPanel);
		
		savedBlockPanel.setBackground(transparent);
		savedBlockPanel.setSize(new Dimension(100,100));
		savedBlockPanel.setLocation(BGX + 275, 150);
		add(savedBlockPanel);
		
		pointPanel.setBackground(transparent);
		pointPanel.setSize(new Dimension(100, 75));
		pointPanel.setLocation(BGX + 275, 275);
		add(pointPanel);
		
		levelPanel.setBackground(transparent);
		levelPanel.setSize(new Dimension(100,75));
		levelPanel.setLocation(BGX + 275, 350);
		add(levelPanel);
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this);
		if (playStarted)
		{
			Graphics2D g2 = (Graphics2D)g.create();
			for (int i = 0; i < staticBlocks.length; i++)
			{
				for (int j = 0; j < staticBlocks[i].length; j++)
				{
					if (staticBlocks[i][j] != null)
						staticBlocks[i][j].draw(g);
				}
			}
			currentBlock.draw(g);
			currentBlock.drawGhost(g);
			if (game.isDebug())
			{
				g2.setColor(Color.red);
				for (int i = 0; i < solids.length; i++)
				{
					g2.draw(solids[i]);
				}
				for (int i = 0; i < staticBlocks.length; i++)
				{
					for (int j = 0; j < staticBlocks[i].length; j++)
					{
						if (staticBlocks[i][j] != null)
						{
							Rectangle rect = new Rectangle((int)staticBlocks[i][j].getX(), (int)staticBlocks[i][j].getY(), 25, 25);
							g2.draw(rect);
						}
					}
				}
			}
			if (game.isPaused())
			{
				g.setColor(Color.white);
				g.drawString("Game Paused", BGX + 87, BGY + 100);
				g.drawString("Press ESC to Unpause", BGX + 62, BGY + 125);
			}
		}
	}
	public void update(double delta)
	{
		if (playStarted)
		{
			timeSinceTick += delta;
			if (timeSinceTick >= tickTime)
			{
				currentBlock.update();
				timeSinceTick = 0;
			}
		}
	}
	public void levelUp()
	{
		level++;
		tickTime *= .9;
		rowsDestroyedThisLevel = 0;
	}
	public void nextBlock()
	{
		int nextShape = (int) (Math.random() * 100);
		nextShape = nextShape % 7;
		nextBlock = new TetrisBlock(nextShape, this);
		nextBlockPanel.changeBlock();
	}
	public void dropNextBlock()
	{
		BlockUnit[] units = currentBlock.getUnits();
		ArrayList<Integer> rowsToDestroy = new ArrayList<Integer>();
		for(int i = 0; i < units.length; i++)
		{
			units[i].setStatic();
			staticBlocks[units[i].getRow()][units[i].getCol()] = units[i];
			if (isFull(units[i].getRow()))
				if (!rowsToDestroy.contains(units[i].getRow()))
					rowsToDestroy.add(units[i].getRow());
		}
		if (!rowsToDestroy.isEmpty())
			destroyRows(rowsToDestroy);
		currentBlock = nextBlock;
		nextBlock();
	}
	public boolean checkCollision(Rectangle[] unitRects)
	{
		for (int i = 0; i < unitRects.length; i++)
		{
			Rectangle rect = unitRects[i];
			for (Rectangle solid : solids)
			{
				if (rect.intersects(solid))
					return true;
			}
			for (int j = 0; j < staticBlocks.length; j++)
			{
				for (int k = 0; k < staticBlocks[j].length; k++)
				{
					if (staticBlocks[j][k] != null)
						if (rect.intersects(staticBlocks[j][k]))
							return true;
				}
			}
		}
		
		return false;
	}
	public TetrisBlock getCurrentBlock()
	{
		return currentBlock;
	}
	public BlockUnit[][] getStaticBlocks()
	{
		return staticBlocks;
	}
	
	public boolean isFull(int row)
	{
		for (BlockUnit unit : staticBlocks[row])
		{
			if (unit == null)
			{
				return false;
			}
		}
		return true;
	}
	
	public void destroyRows(ArrayList<Integer> rowsToDestroy)
	{
		// Sort the rows so that the lowest row is first. (row 19 is lowest, row 0 is highest)
		points += 10 * Math.pow(2.0, rowsToDestroy.size());
		rowsDestroyedThisLevel += rowsToDestroy.size();
		if (rowsDestroyedThisLevel >= 2*level)
			levelUp();
		if (rowsToDestroy.size() > 1)
		{
			for (int i = 1; i < rowsToDestroy.size(); i++)
			{
				int key = rowsToDestroy.get(i);
				int j = i - 1;
				while (j >= 0 && rowsToDestroy.get(j) > key)
				{
					rowsToDestroy.set(j + 1, rowsToDestroy.get(j));
					j--;
				}
				rowsToDestroy.set(j + 1, key);
			}
		}
		
		//Destroy every row.
		for (int i = 0; i < rowsToDestroy.size(); i++)
		{
			for (int j = 0; j < staticBlocks[rowsToDestroy.get(i)].length; j++)
			{
				staticBlocks[rowsToDestroy.get(i)][j] = null;
			}
		}
		int numMoves = 0;
		int bottomRowToMove = rowsToDestroy.get(0);
		while (numMoves < rowsToDestroy.size())
		{
			for (int i = bottomRowToMove; i > 0; i--)
			{
				for (int j = 0; j < staticBlocks[i].length; j++)
				{
					if (staticBlocks[i][j] != null)
					{
						staticBlocks[i][j].move("down");
						staticBlocks[i + 1][j] = staticBlocks[i][j];
						staticBlocks[i][j] = null;
					}
				}
			}
			bottomRowToMove++;
			numMoves++;
		}
	}
	
	public void endGame()
	{
		game.endGame();
	}
	
	public int getPoints()
	{
		return points;
	}
	
	public int getLevel()
	{
		return level;
	}

	public void keyPressed(KeyEvent evt)
	{
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_DOWN)
		{
			currentBlock.move("down");
		}
		else if (key == KeyEvent.VK_RIGHT)
		{
			currentBlock.move("right");
		}
		else if (key == KeyEvent.VK_LEFT)
		{
			currentBlock.move("left");
		}
		else if (key == KeyEvent.VK_CONTROL || key == KeyEvent.VK_ALT)
		{
			currentBlock.rotateLeft();
		}
		else if (key == KeyEvent.VK_INSERT || key == KeyEvent.VK_SHIFT)
		{
			currentBlock.rotateRight();
		}
		else if (key == KeyEvent.VK_D)
		{
			if (game.isDebug())
			{
				currentBlock = nextBlock;
				nextBlock();
			}
		}
		else if (key == KeyEvent.VK_S)
		{
			if (!blockSaved)
			{
				savedBlock = currentBlock;
				savedBlockPanel.changeBlock();
				currentBlock = nextBlock;
				nextBlock();
				blockSaved = true;
			}
			else
			{
				TetrisBlock temp = currentBlock;
				currentBlock = new TetrisBlock(savedBlock.getShape().ordinal(), this);
				savedBlock = temp;
				savedBlockPanel.changeBlock();
			}
		}
		else if (key == KeyEvent.VK_SPACE)
		{
			currentBlock.hardDrop();
		}
		else if (key == KeyEvent.VK_ESCAPE)
		{
			if (game.isPaused())
				game.setPaused(false);
			else game.setPaused(true);
			repaint();
		}
	}
	public void keyReleased(KeyEvent evt) {}
	public void keyTyped(KeyEvent evt) {}
	
	private class NextBlockPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private BufferedImage nextBlockIMG;
		private HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
		
		public NextBlockPanel()
		{
			imageInit();
		}
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.drawImage(nextBlockIMG, 0, 0, this);
		}
		
		public void changeBlock()
		{
			nextBlockIMG = images.get(nextBlock.getShape().name());
		}
		private void imageInit()
		{
			images.put("IBLOCK", loadImage("I"));
			images.put("JBLOCK", loadImage("J"));
			images.put("LBLOCK", loadImage("L"));
			images.put("SBLOCK", loadImage("S"));
			images.put("ZBLOCK", loadImage("Z"));
			images.put("TBLOCK", loadImage("T"));
			images.put("SQBLOCK", loadImage("SQ"));
		}
		private BufferedImage loadImage(String path)
		{
			String imgPath = "images/smallBlocks/SMALL" + path + "BLOCK.png";
			BufferedImage img = null;
			try {
				img = ImageIO.read(getClass().getResource(imgPath));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Image not found at " + imgPath);
			}
			return img;
		}
	}
	
	private class SavedBlockPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private BufferedImage savedBlockIMG;
		private HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
		
		public SavedBlockPanel()
		{
			imageInit();
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if (savedBlockIMG != null)
			{
				g.drawImage(savedBlockIMG, 0, 0, this);
			}
			else super.paintComponent(g);
		}
		public void changeBlock()
		{
			if (savedBlock != null)
				savedBlockIMG = images.get(savedBlock.getShape().name());
			else savedBlockIMG = images.get("bg");
		}
		private void imageInit()
		{
			images.put("IBLOCK", loadImage("I"));
			images.put("JBLOCK", loadImage("J"));
			images.put("LBLOCK", loadImage("L"));
			images.put("SBLOCK", loadImage("S"));
			images.put("ZBLOCK", loadImage("Z"));
			images.put("TBLOCK", loadImage("T"));
			images.put("SQBLOCK", loadImage("SQ"));
			images.put("bg", loadImage(null));
		}
		private BufferedImage loadImage(String path)
		{
			String imgPath;
			if (path != null)
				imgPath = "images/smallBlocks/SMALL" + path + "BLOCK.png";
			else 
				imgPath = "images/savebg.png";
			BufferedImage img = null;
			try {
				img = ImageIO.read(getClass().getResource(imgPath));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Image not found at " + imgPath);
			}
			return img;
		}
	}
	
	private class PointPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		BufferedImage[] images;
		public PointPanel()
		{
			images = new BufferedImage[10];
			for (int i = 0; i < images.length; i++)
			{
				images[i] = loadImage(i);
			}
		}
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			double j = 4;
			int pointsToDraw = points;
			for (int i = 0; i < 5; i++)
			{
				int x = i*20 + 1; 
				int y = 2;
				int divisor = (int) Math.pow(10.0, j);
				int numToDraw = pointsToDraw / divisor;
				pointsToDraw = pointsToDraw % divisor;
				j--;
				g.drawImage(images[numToDraw], x, y, this);
			}
		}
		private BufferedImage loadImage(int num)
		{
			String imgPath = "images/numbers/" + num + ".png";
			BufferedImage img = null;
			try {
				img = ImageIO.read(getClass().getResource(imgPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Image not found at " + imgPath);
			}
			return img;
		}
	}
	
	private class LevelPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private static final int numWidth = 20;
		BufferedImage[] images;
		public LevelPanel()
		{
			images = new BufferedImage[10];
			for (int i = 0; i < images.length; i++)
			{
				images[i] = loadImage(i);
			}
		}
		
		public void paintComponent(Graphics g)
		{
			int tempLevel = level;
			int length = 0;
			while (tempLevel != 0)
			{
				tempLevel = tempLevel / 10;
				length++;
			}
			tempLevel = level;
			int tempLength = length - 1;
			int x = (getWidth() - (numWidth*length)) / 2 + 1;
			for (int i = 0; i < length; i++)
			{
				int numToDraw = (int) (tempLevel / Math.pow(10.0, tempLength));
				tempLevel = (int) (tempLevel % Math.pow(10.0, tempLength));
				tempLength--;
				g.drawImage(images[numToDraw], x + i*20, 2, this);
			}
		}
		
		private BufferedImage loadImage(int num)
		{
			String imgPath = "images/numbers/" + num + ".png";
			BufferedImage img = null;
			try {
				img = ImageIO.read(getClass().getResource(imgPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Image not found at " + imgPath);
			}
			return img;
		}
	}
}
