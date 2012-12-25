package bau5.programs.liteinstaller;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * 
 * @author bau5
 *
 */
public class LiteInstaller 
{
	public static LiteInstaller instance;
	public FileHandler fh;
	public LiteGui gui;
	
	public void init(){
		fh = new FileHandler();
		gui = new LiteGui();
		gui.log("Mods: " +fh.hasMods());
		gui.log("CoreMods: " +fh.hasCoreMods());
		gui.log("Configs: " +fh.hasConfigs());		
	}
	
	public static void main(String[] args){
		LiteInstaller li = new LiteInstaller();
		instance = li;
		li.init();
	}
	
	public void dialogError(int errorCode) {
		JFrame dialogFrame = new JFrame();
		switch(errorCode){
		case 0: JOptionPane.showMessageDialog(dialogFrame, "No files found, place them in the files directory.", "Error", JOptionPane.ERROR_MESSAGE);
			break;
		}
	}
}
