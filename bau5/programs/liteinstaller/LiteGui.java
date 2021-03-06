package bau5.programs.liteinstaller;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * @author bau5
 *
 */
@SuppressWarnings("serial")
public class LiteGui extends JFrame implements TreeSelectionListener
{
	private LiteInstaller core = LiteInstaller.instance;
	public JTextArea logArea;
	private JPanel mainPanel;
	private JTree mainTree;
	private JTree mcTree;
	
	public LiteGui(Point p){

		this.setTitle("LiteInstaller");
		this.setSize(800, 475);
		
		if(core.getLogArea() != null)
			logArea = core.getLogArea();
		else
			 logArea= new JTextArea(22,30);
		
		if(p != null){
			this.setLocation(p);
		}else{
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		    this.setLocation(x, y);
		}
			
		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		logArea.setEditable(false);
		
		JScrollPane logFieldScroller = new JScrollPane(logArea);
		logFieldScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		logFieldScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		mainPanel = new JPanel();
		JPanel viewPanel = new JPanel();
		JPanel logPanel = new JPanel();
		logPanel.add(logFieldScroller);
		JTabbedPane jtp = new JTabbedPane();		
		JButton begin = new JButton("Move");
		begin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				moveFiles();
				refresh();
			}
		});
		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				refresh();
			}
		});
		JButton moveMC = new JButton("Move MC");
		moveMC.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				moveMC();
				refresh();
			}
		});
		moveMC.setToolTipText("This will move any prexisting Minecraft files.");
		mainTree = makeTree(viewPanel);
		mcTree = makeMCTree(viewPanel);
		JScrollPane treeScroller = new JScrollPane(mainTree);
		JScrollPane treeScroller2 = new JScrollPane(mcTree);
		treeScroller.setBorder(BorderFactory.createTitledBorder("File Structure View"));
		treeScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		treeScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScroller.setPreferredSize(new Dimension(350, 350));
		treeScroller2.setBorder(BorderFactory.createTitledBorder("Minecraft Directory"));
		treeScroller2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		treeScroller2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScroller2.setPreferredSize(new Dimension(350, 350));
		viewPanel.add(treeScroller);
		viewPanel.add(treeScroller2);
		viewPanel.setLayout(new FlowLayout());
		viewPanel.doLayout();
		jtp.addTab("Viewer", viewPanel);
		jtp.addTab("Log", logPanel);
		mainPanel.add(jtp);
		mainPanel.add(begin);
		mainPanel.add(refresh);
		mainPanel.add(moveMC);
		this.add(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	protected void refresh() {
		core.fh.initFileTree();
		core.fh.initMCFileTree();
		core.setLogArea(this.logArea);
		Point tempLoc = core.gui.getLocationOnScreen();
		core.gui.dispose();
		core.gui = new LiteGui(tempLoc);
	}

	public void moveFiles(){
		if(!core.fh.checkIfModded()) {
			core.gui.log("Minecraft appears not to be modded. Moving anyways.");
			core.fh.moveAll();
		}else {
			core.fh.moveAll();
		}
	}
	public void moveMC(){
		core.fh.moveMC();
		core.dialog(2);
	}
	@Override
	public void valueChanged(TreeSelectionEvent arg0) 
	{
		
	}
	
	public void log(String message){
		logArea.append(message +"\n");
	}
	
	public JTree makeTree(JPanel parent){
		JTree tree = null;
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Files");
		for(File[] fs : core.fh.fileList){
			if(fs[0].getParentFile().getName().equalsIgnoreCase("files"))
				createNodes(top, fs);
		}
		tree = new JTree(top);
		return tree;
	}
	public JTree makeMCTree(JPanel parent){
		JTree tree = null;
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Files");
		for(File[] fs : core.fh.mcFileList){
			if(fs[0].getParentFile().getName().equalsIgnoreCase(".minecraft"))
				createNodes(top, fs);
		}
		tree = new JTree(top);
		return tree;
	}
	
	public JPanel getMainPanel(){
		return mainPanel;
	}
	public void createNodes(DefaultMutableTreeNode top, File[] files){
		DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode file = null;

        category = new DefaultMutableTreeNode(files[0].getParent().substring(files[0].getParent().lastIndexOf('\\') +1), true);
        top.add(category);
        
        for(File f : files){
        	if(f.isDirectory()){
        		if(f.list().length != 0)
        			createNodes(category, f.listFiles());
        		this.log("Directory found: " +f.getName());
        	}else{
        		file = new DefaultMutableTreeNode(f.getName());
        		category.add(file);
        	}
        }
	}
}