package bau5.programs.liteinstaller;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * 
 * @author bau5
 *
 */
public class LiteInstaller 
{
	private JTextArea log;
	public static LiteInstaller instance;
	public FileHandler fh;
	public LiteGui gui;
	
	public void init(){
		fh = new FileHandler();
		gui = new LiteGui(null);
		gui.log("Mods: " +fh.hasMods());
		gui.log("CoreMods: " +fh.hasCoreMods());
		gui.log("Configs: " +fh.hasConfigs());		
	}
	
	public static void main(String[] args){
		LiteInstaller li = new LiteInstaller();
		instance = li;
		li.init();
	}
	
	public void dialog(int code) {
		JFrame dialogFrame = new JFrame();
		dialogFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		switch(code){
		case 0: JOptionPane.showMessageDialog(dialogFrame, "No files found, place them in the files directory.", "Error", JOptionPane.ERROR_MESSAGE);
			break;
		case 1: JOptionPane.showMessageDialog(dialogFrame, "Minecraft folder not found, run Minecraft once.", "MC Not Found", JOptionPane.ERROR_MESSAGE);
			break;
		case 2: JOptionPane.showMessageDialog(dialogFrame, "Minecraft folder backed up to " +fh.LIPath +".", "Backup Notice", JOptionPane.INFORMATION_MESSAGE);
			break;
		case 3: JOptionPane.showMessageDialog(dialogFrame, "Minecraft folder empty, no files to display.", "Notice", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public JTextArea getLogArea() 
	{
		return log;
	}
	public void setLogArea(JTextArea jta) 
	{
		log = jta;
	}
}
