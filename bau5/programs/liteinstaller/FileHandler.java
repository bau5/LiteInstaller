package bau5.programs.liteinstaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

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
	
	public FileHandler()
	{
		initFileTree();
		initMCFileTree();
		mcDir = new File(pcMCPath);
	}

	public void moveAll() {
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
					copyFile(f, target);
				}
			}
		}
	}
	
	public void moveFiles(File files[], String name)
	{
		if(files.length > 0){
			name = files[0].getParent();
			name = name.substring(name.lastIndexOf("\\") + 1);
		}
		if(files.length <= 0){
			core.gui.log(name +" is empty, skipping.");
			return;
		}
		File destFolder = new File(pcMCPath + "/" +name + "/");
		File destFile = null;
		if(destFolder.exists()){
			core.gui.log("Destination for " +name +" found, initiating move.");
			if(destFolder.list().length != 0){
				core.gui.log("Mods folder already populated, results unpredictable.");
			}
			main: for(File f : files){
				System.out.println(f.getName());
				for(String str : destFolder.list()){
					if(str.equalsIgnoreCase(f.getName())){
						core.gui.log("File \"" +str +"\" already exists in " +name +", skipping.");
						continue main;
					}
				}
				destFile = new File(pcMCPath +"/" +name +"/" +f.getName());
				copyFile(f, destFile);
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
			core.dialogError(0);
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
			System.out.println("No Files Found.");
			base.mkdir();
			core.dialogError(0);
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
			fileList.add(temp);
			if(core.gui != null)
				core.gui.log("Directory \"" +f.getName() +"\" added.");
			for(File fi : temp){
				if(fi.isDirectory()){
					processDirectory(fi);
				}
			}
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
}
