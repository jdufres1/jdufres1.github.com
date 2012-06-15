package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Game extends JApplet implements Runnable
{
	private static final long serialVersionUID = 1L;
	private TetrisPanel content;
	private boolean debug = false;
	private boolean paused = true;
	private boolean running = false;
	private Thread thread;

	public void init()
	{
		content = new TetrisPanel(this);
		setContentPane(content);
		setFocusable(true);
		setVisible(true);
		validate();
		thread = new Thread(this);
		thread.start();
	}

	public void run()
	{ 
		long lastLoopTime = System.nanoTime(); 
		final long OPTIMAL_TIME = 1000000000 / 30; //60fps is optimal
		long lastFpsTime = 0;
		long fps = 0;
		// keep looping round til the game ends 
		running = true;
		while (running)   
		{     
			if (!paused)
			{
				// work out how long its been since the last update, this  
				// will be used to calculate how far the entities should   
				// move this loop      
				long now = System.nanoTime();   
				long updateLength = now - lastLoopTime; 
				lastLoopTime = now;  
			//	double delta = updateLength / ((double)OPTIMAL_TIME);  
				// update the frame counter  
				lastFpsTime += updateLength;   
				fps++;       
				// update our FPS counter if a second has passed since 
				// we last recorded     
				if (lastFpsTime >= 1000000000)   
				{      
					System.out.println("(FPS: "+fps+")");   
					lastFpsTime = 0;     
					fps = 0;   
				}          
				// update the game logic     
				content.update(updateLength);
				// draw everyting   
				content.repaint();
				//content.repaint();
				// System.out.println(lastLoopTime + " " + System.nanoTime() + " " + (System.nanoTime() - lastLoopTime) + " " + OPTIMAL_TIME);
				if ((System.nanoTime() - lastLoopTime) > OPTIMAL_TIME)
				{
					// Do not sleep. We are already late.
				}
				else 
				{
					try {
						Thread.sleep( ( OPTIMAL_TIME - (System.nanoTime() - lastLoopTime) ) / 1000000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public boolean isDebug()
	{
		return debug;
	}
	
	public void help()
	{
		paused = true;
		final JPanel helpPanel = new JPanel();
		helpPanel.setSize(300,200);
		helpPanel.setLocation((getWidth() - 300) / 2, (getHeight() - 200) / 2);
		helpPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		JLabel helpLabel = new JLabel();
		helpLabel.setText("<html>" +
							  "How to Play: <br>" +
							  "Move Right, Left, or Down: Arrow Keys<br>" +
							  "Rotate Right: INSERT or SHIFT<br>" +
							  "Rotate Left: CTRL or ALT<br><br>" +
							  "Save a Piece or<br>" +
							  "&nbsp;&nbsp;&nbsp;Use a Saved Piece: S" +
							  "</html>");
		helpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		helpLabel.setPreferredSize(new Dimension(300,125));
		JButton playButton = new JButton("Go Back");
		playButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				remove(helpPanel);
				paused = false;
				validate();
			}
		});
		helpPanel.add(helpLabel);
		helpPanel.add(playButton);

		add(helpPanel, 0);
		validate();
	}
	
	public void endGame()
	{
		running = false;
		
		JPanel gameOverPanel = new JPanel();
		gameOverPanel.setSize(300,200);
		gameOverPanel.setLocation((getWidth() - 300) / 2, (getHeight() - 100) / 2);
		gameOverPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		JLabel gameOverLabel = new JLabel();
		gameOverLabel.setText("<html>Game Over!" + 
							  "<br>Score: " + content.getPoints() + 
							  "<br>Level: " + content.getLevel() + "</html>");
		gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gameOverLabel.setPreferredSize(new Dimension(300,100));
		JButton newGameButton = new JButton("Play Again");
		newGameButton.addActionListener(new ButtonHandler(this));
		gameOverPanel.add(gameOverLabel);
		gameOverPanel.add(newGameButton);

		add(gameOverPanel, 0);
		validate();
	}
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	private class ButtonHandler implements ActionListener
	{
		private Game game;
		public ButtonHandler(Game game)
		{
			this.game = game;
		}
		public void actionPerformed(ActionEvent e) 
		{	
			content = new TetrisPanel(game);
			game.setContentPane(content);
			thread = new Thread(game);
			thread.start();
			validate();
		}
	}

	
}
