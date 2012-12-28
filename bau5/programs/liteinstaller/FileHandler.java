package bau5.programs.liteinstaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author bau5
 * 
 */
public class FileHandler 
{
	private LiteInstaller core = LiteInstaller.instance;
	public File coremods[];
	public File configs[];
	public File mods[];
	public File mcDir;
	
	public ArrayList<File[]> fileList = null;
	public ArrayList<File[]> mcFileList = null;
	
	private String pcMCPath = System.getProperty("user.home") +"/appdata/roaming/.minecraft/";
	public String LIPath   = System.getProperty("user.home") +"/appdata/roaming/.liteinstaller/";
	
	public FileHandler()
	{
		initFileTree();
		initMCFileTree();
		mcDir = new File(pcMCPath);
	}

	public void moveAll() {
		
		for(File[] entry : fileList){
			System.out.println(entry[0].getParent());
		}
		String dirTarget = null;
		File target = null;
		for(File[] entry : fileList){
			second : for(File f : entry){
				if(f.isDirectory()){
					core.gui.log("Into directory: " +f.getName());
					dirTarget = new String(pcMCPath + (f.getAbsolutePath().substring(f.getAbsolutePath().indexOf("files") +6)));
					System.out.println(dirTarget);
					target = new File(dirTarget);
					if(!target.exists()){
						core.gui.log("Target directory doesn't exist. Creating.");
						target.mkdir();
					}
					continue second;
				}else{
					dirTarget = new String(pcMCPath + (f.getAbsolutePath().substring(f.getAbsolutePath().indexOf("files") +6)));
					target = new File(dirTarget);
					core.gui.log("Target is: " +target.getAbsolutePath());
					if(target.exists()){
						System.out.println("Target exists, cancelling move." +target.getName());
					}else
						copyFile(f, target);
				}
			}
		}
	}
	public void moveAllMC(File dir) {		
		String dirTarget = null;
		File target = null;
		initMCFileTree();
		System.out.println(mcFileList.size());
		for(File[] entry : mcFileList){
			second : for(File f : entry){
				if(f.isDirectory()){
					core.gui.log("Into directory: " +f.getName());
					dirTarget = new String(LIPath +"/backups/" +dir.getName() +"/" +(f.getAbsolutePath().substring(f.getAbsolutePath().indexOf(".minecraft") +11)));
					System.out.println(dirTarget);
					target = new File(dirTarget);
					if(!target.exists()){
						core.gui.log("Target directory doesn't exist. Creating.");
						target.mkdir();
					}
					continue second;
				}else{
					dirTarget = new String(LIPath +"/backups/" +dir.getName() +"/" +(f.getAbsolutePath().substring(f.getAbsolutePath().indexOf(".minecraft") +11)));
					target = new File(dirTarget);
					core.gui.log("Target is: " +target.getAbsolutePath());
					copyFile(f, target);
				}
			}
		}
	}
	
	public void initFileTree(){
		fileList = new ArrayList<File[]>();
		File base = new File(System.getProperty("user.dir") + "/files/");
		File[] files = null;
		files = base.listFiles();
		if(!base.exists() || files == null || files.length == 0){
			System.out.println("No Files Found.");
			base.mkdir();
			core.dialog(0);
			return;
		}else{
			fileList.add(files);
			for(File f : files){
				if(f.isDirectory()){
					processDirectory(f);
				}else {
					if(core.gui != null)
						core.gui.log("Miscellaneous file found, \"" +f.getName() +"\".");
				}
			}
		}
	}
	public void initMCFileTree(){
		mcFileList = new ArrayList<File[]>();
		File base = new File(pcMCPath);
		File[] files = null;
		files = base.listFiles();
		if(!base.exists() || files == null || files.length == 0){
			System.out.println("No files found in Minecraft directory.");
			base.mkdir();
			core.dialog(3);
			return;
		}else{
			mcFileList.add(files);
			for(File f : files){
				if(f.isDirectory()){
					processDirectory(f);
				}else {
					if(core.gui != null)
						core.gui.log("Miscellaneous file found, \"" +f.getName() +"\".");
				}
			}
		}
	}
	
	public void processDirectory(File f){
		File[] temp = null;
		temp = f.listFiles();
		if(temp != null && temp.length > 0){
			if(f.getName().equalsIgnoreCase("mods"))
				mods = temp;
			else if(f.getName().equalsIgnoreCase("coremods"))
				coremods = temp;
			else if(f.getName().equalsIgnoreCase("config"))
				configs = temp;
			if(!temp[0].getAbsolutePath().contains(".minecraft"))
				fileList.add(temp);
			if(core.gui != null)
				core.gui.log("Directory \"" +f.getName() +"\" added.");
			for(File fi : temp){
				if(fi.isDirectory()){
					processDirectory(fi);
				}
			}
			if(temp[0].getAbsolutePath().contains(".minecraft"))
				mcFileList.add(temp);
		}
	}	
	
	public boolean hasMods()
	{
		if(mods == null)
			return false;
		return (mods.length > 0);
	}
	public boolean hasCoreMods()
	{
		if(mods == null)
			return false;
		return (coremods.length > 0);
	}
	public boolean hasConfigs()
	{
		if(mods == null)
			return false;
		return (configs.length > 0);
	}
	public boolean checkIfModded()
	{
		if(!mcDir.exists()){
			core.gui.log("Minecraft folder not found.");
			return false;
		}else {
			for(String str : mcDir.list()){
				if(str.equalsIgnoreCase("mods") || str.equalsIgnoreCase("coremods")){
					core.gui.log("Minecraft folder found and is modded.");
					return true;
				}
			}
			core.gui.log("Minecraft folder found but is not modded.");
			return false;
		}
	}
	
	public void copyFile(File sourceFile, File dest)
	{
		FileChannel source;
		FileChannel destination;
		try{
			source = new FileInputStream(sourceFile).getChannel();
			if(!dest.getParentFile().exists())
				dest.getParentFile().mkdir();
			destination = new FileOutputStream(dest).getChannel();
			
			long count = 0;
			long size = source.size();
			while((count += destination.transferFrom(source, count, size - count)) < size);
			source.close();
			destination.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void moveMC() {
		File mc = new File(pcMCPath);
		File LIDir = new File(LIPath );
		File dest = new File(LIPath +"/backups/");
		if(!mc.exists()){
			core.gui.log("Minecraft folder does not exist. Run once.");
			core.dialog(1);
			return;
		}
		if(!LIDir.exists()){
			core.gui.log("Making home folder.");
			LIDir.mkdir();
		}
		if(!dest.exists()){
			core.gui.log("Making backup folder.");
			dest.mkdir();
		}
		Date d = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy'_'hh-mm-ss");
		File newMC = new File(LIPath +"/backups/" +ft.format(d) +"/");
		core.gui.log("Creating backup at: " +newMC.getPath());
		
		if(newMC.mkdir()){
			moveAllMC(newMC);
		}
		deleteDirectory(mc);
	}

	private void deleteDirectory(File mc) 
	{
		for(File f : mc.listFiles()){
			if(f.isDirectory()){
				clearDirectory(f);
				f.delete();
			}else
				f.delete();
		}
	}
	private void clearDirectory(File dir){
		for(File f : dir.listFiles()){
			if(!f.isDirectory())
				f.delete();
			else{
				clearDirectory(f);
				f.delete();
			}
		}
	}
}
