package com.steelkiwi.patheditor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.steelkiwi.patheditor.consts.MenuConsts;
import com.steelkiwi.patheditor.consts.MenuConsts.TREE_NODE_TYPE;
import com.steelkiwi.patheditor.consts.MsgConsts;
import com.steelkiwi.patheditor.proj.ProjectData;
import com.steelkiwi.patheditor.proj.ProjectDataConverter;
import com.steelkiwi.patheditor.proj.ScreenData;
import com.steelkiwi.patheditor.widgets.GdxImage;

public class EditorRootPane extends JFrame implements IProjectHandler {
	private static final long serialVersionUID = -3021481019247627930L;

	private final static String WINDOW_TITLE = "Steelkiwi Game Editor";

	private IMenuHandler menuHandler;
	private JMenuItem newProjectItem;
	private JMenuItem openProjectItem;
	private JMenuItem saveProjectItem;
	private JMenuItem closeProjectItem;
	private JMenuItem addScreen;
	private JMenuItem addBG;
	private JMenuItem addPath;
	
	private JButton newProjectButton;
	private JButton openProjectButton;
	private JButton saveProjectButton;
	private JButton closeProjectButton;
	private JButton addScreenButton;
	private JButton addBGButton;
	private JButton addPathButton;
	
	private JTree projectTree;
	private JScrollPane projectTreeScroll;
	
	private LwjglPanel glPanel;
	private JLabel mouseInfoLabel;
	private JLabel memoryInfoLabel;
	
	private ProjectData projectData;
	
	public EditorRootPane() {
		super(WINDOW_TITLE);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!saveCurrentProject()) { return; }
				glPanel.getGdxApp().dispose();
			}
		});
		setSize(getScreenSize());
		setExtendedState(JFrame.MAXIMIZED_BOTH);  
		
		menuHandler = new IMenuHandler(this);
		setJMenuBar(createMenuBar());
		noProjectMenuBarState();
		
		JToolBar toolBar = createToolbar();
		noProjectToolBarState();
		
		createNoProjectTree();
		
		IUIHandler uiHandler = new IUIHandler() {
			@Override
			public void updateMouseInfo(float zoom, float x, float y) {
				updateStatusMouseInfo(zoom, x, y);
			}
			
			@Override
			public void updateMemoryInfo(String msg) {
				updateStatusMemoryInfo(msg);
			}
		};
		glPanel = new LwjglPanel(uiHandler);
		
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, projectTreeScroll, glPanel);
        add(splitPane);
        
        JSplitPane tbSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toolBar, splitPane);
        add(tbSplitPane);
        
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(getWidth(), 24));
        statusPanel.setLayout(new BorderLayout());
        mouseInfoLabel = new JLabel("");
        statusPanel.add(mouseInfoLabel, BorderLayout.LINE_START);
        memoryInfoLabel = new JLabel("Memory consumption: ");
        statusPanel.add(memoryInfoLabel, BorderLayout.LINE_END);
	}

	// ==============================================================
	// project management
	// ==============================================================
	
	@Override
	public void onProjectCreated(String name, String path) {
		if (!saveCurrentProject()) { return; }
		
		createProjectData(name, path);
		
		projectOpenedMenuBarState();
		projectOpenedToolBarState();
		createTreeRoot(name, path);
	}

	@Override
	public void onProjectOpened(String path) {
		if (!saveCurrentProject()) { return; }
		
		try {
			projectData = ProjectDataConverter.openProject(path);
		} catch (Exception e) {
			e.printStackTrace();
			projectData = null;
			noProjectMenuBarState();
			noProjectToolBarState();
			createNoProjectTreeRoot();
			onRootSwitched();
			JOptionPane.showMessageDialog(this, MsgConsts.PROJECT_OPEN_ERROR, "Open Project", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (projectData == null) {
			noProjectMenuBarState();
			noProjectToolBarState();
			createNoProjectTreeRoot();
			onRootSwitched();
			JOptionPane.showMessageDialog(this, MsgConsts.PROJECT_OPEN_ERROR, "Open Project", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		projectOpenedMenuBarState();
		projectOpenedToolBarState();
		if (projectData.hasScreens()) {
			screenCreatedMenuBarState();
			screenCreatedToolBarState();
		}
		loadProjectTree();
		
		onScreenSwitched(projectData.getScreens().get(0),
						 projectTree.getPathForRow(1));
	}

	private boolean saveCurrentProject() {
		if (projectData == null) { return true; }
		
		int r = JOptionPane.showConfirmDialog(EditorRootPane.this, MsgConsts.PROJECT_SAVING, "Save Project", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (r == JOptionPane.OK_OPTION) {
			if (!onProjectedSaved(false)) {
				JOptionPane.showMessageDialog(this, MsgConsts.PROJECT_NOT_OPENED_SAVE_ERROR, "Save Project", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}	
		return true;
	}
	
	@Override
	public boolean onProjectedSaved(boolean showSaveSuccessMsg) {
		if (projectData == null) { return true; }
		
		try {
			ProjectDataConverter.saveProject(projectData);
			if (showSaveSuccessMsg) {
				JOptionPane.showMessageDialog(this, MsgConsts.PROJECT_SAVE_SUCCESS, "Save Project", JOptionPane.INFORMATION_MESSAGE);
			}	
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, MsgConsts.PROJECT_SAVE_ERROR, "Save Project", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	@Override
	public void onProjectClosed() {
		if (!saveCurrentProject()) { return; }
		
		projectData = null;
		glPanel.getGdxApp().setScreen(null, glPanel.getCanvasWidth(), glPanel.getCanvasHeight());
		
		noProjectMenuBarState();
		noProjectToolBarState();
		createNoProjectTreeRoot();
	}

	// ==============================================================
	// screens creation and selection
	// ==============================================================
	
	@Override
	public void onScreenAdded(String name, int w, int h) {
		createScreenData(name, w, h);
		setScreen(projectData.getScreens().get(projectData.getScreens().size()-1));
		
		screenCreatedMenuBarState();
		screenCreatedToolBarState();
		addTreeNode(projectData.getScreens().get(projectData.getScreens().size()-1));
	}
	
	@Override
	public void onScreenSwitched(ScreenData scrData, TreePath path) {
		setScreen(scrData);
		selectTreeNode(path);
	}
	
	private void setScreen(ScreenData scrData) {
		glPanel.getGdxApp().setScreen(scrData, glPanel.getCanvasWidth(), glPanel.getCanvasHeight());
	}
	
	@Override
	public void onRootSwitched() {
		glPanel.getGdxApp().setScreen(null, glPanel.getCanvasWidth(), glPanel.getCanvasHeight());
		selectRoot();
	}
	
	// ==============================================================
	// screen elements creation
	// ==============================================================
	
	@Override
	public void onBGImageAdded(String name, String path, float scaleCoef) {
		glPanel.getGdxApp().onAddBGTexture(name, path, scaleCoef);
		createBGImageData(glPanel.getGdxApp().getBGImage(), getSelectedNodeIndex(getSelectedNode()));
		
		if (getSelectedNode().getChildCount() <= 0) { //TODO add leaf types: bg, path
			addTreeLeaf(getSelectedNode(), name);
		}
		else {
			updateTreeLeaf(getSelectedNode(), name);
		}
	}

	@Override
	public void onPathAdded(String name) {
		glPanel.getGdxApp().onAddPath();
		//TODO create path data and add to current screen data, add tree leaf
	}

	@Override
	public void onLeafSwitched(String name, TreePath path) {
		ScreenData scrData = getNodeScreenData(getLeafParentNode(path));
		setScreen(scrData);
		selectTreeNode(path);
	}
	
	// ==============================================================
	// main menu
	// ==============================================================

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createProjectMenu());
		menuBar.add(createAddScreenMenu());
		menuBar.add(createAddBGMenu());
		menuBar.add(createAddPathMenu());
		return menuBar;
	}
	
	private JMenu createProjectMenu() {
		JMenu menu = new JMenu(MenuConsts.project);
		
		newProjectItem = new JMenuItem(MenuConsts.newProject);
		newProjectItem.addActionListener(menuHandler);
		newProjectItem.setIcon(new ImageIcon("data/menu/newProject_menu.png"));
		menu.add(newProjectItem);
		
		openProjectItem = new JMenuItem(MenuConsts.openProject);
		openProjectItem.setIcon(new ImageIcon("data/menu/openProject_menu.png"));
		openProjectItem.addActionListener(menuHandler);
		menu.add(openProjectItem);
		
		saveProjectItem = new JMenuItem(MenuConsts.saveProhect);
		saveProjectItem.setIcon(new ImageIcon("data/menu/save_menu.png"));
		saveProjectItem.addActionListener(menuHandler);
		menu.add(saveProjectItem);
		
		closeProjectItem = new JMenuItem(MenuConsts.closeProject);
		closeProjectItem.setIcon(new ImageIcon("data/menu/closeProject_menu.png"));
		closeProjectItem.addActionListener(menuHandler);
		menu.add(closeProjectItem);
		
		menu.add(new JSeparator());
		
		JMenuItem exitItem = new JMenuItem(MenuConsts.exit);
		exitItem.setIcon(new ImageIcon("data/menu/exit_menu.png"));
		exitItem.addActionListener(menuHandler);
		menu.add(exitItem);
		
		return menu;
	}
	
	private JMenu createAddScreenMenu(){
		JMenu menu = new JMenu(MenuConsts.screen);
		
		addScreen = new JMenuItem(MenuConsts.addScreen);
		addScreen.setIcon(new ImageIcon("data/menu/addScreen_menu.png"));
		addScreen.addActionListener(menuHandler);
		menu.add(addScreen);
		
		return menu;
	}
	
	private JMenu createAddBGMenu(){
		JMenu menu = new JMenu(MenuConsts.bg);
		
		addBG = new JMenuItem(MenuConsts.addBG);
		addBG.setIcon(new ImageIcon("data/menu/addImage_menu.png"));
		addBG.addActionListener(menuHandler);
		menu.add(addBG);
		
		return menu;
	}
	
	private JMenu createAddPathMenu(){
		JMenu menu = new JMenu(MenuConsts.path);
		
		addPath = new JMenuItem(MenuConsts.addPath);
		addPath.setIcon(new ImageIcon("data/menu/addPath_menu.png"));
		addPath.addActionListener(menuHandler);
		menu.add(addPath);
		
		return menu;
	}
	
	private void noProjectMenuBarState() {
		newProjectItem.setEnabled(true);
		openProjectItem.setEnabled(true);
		saveProjectItem.setEnabled(false);
		closeProjectItem.setEnabled(false);
		addScreen.setEnabled(false);
		addBG.setEnabled(false);
		addPath.setEnabled(false);
	}
	
	private void projectOpenedMenuBarState() {
		newProjectItem.setEnabled(true);
		openProjectItem.setEnabled(true);
		saveProjectItem.setEnabled(true);
		closeProjectItem.setEnabled(true);
		addScreen.setEnabled(true);
		addBG.setEnabled(false);
		addPath.setEnabled(false);
	}
	
	private void screenCreatedMenuBarState() {
		newProjectItem.setEnabled(true);
		openProjectItem.setEnabled(true);
		saveProjectItem.setEnabled(true);
		closeProjectItem.setEnabled(true);
		addScreen.setEnabled(true);
		addBG.setEnabled(true);
		addPath.setEnabled(true);
	}
	
	// ==============================================================
	// toolbar
	// ==============================================================
	
	private JToolBar createToolbar() {
		JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		
		ImageIcon newProjectIcon = new ImageIcon("data/toolbar/newProject.png");
		newProjectButton = new JButton(newProjectIcon);
		newProjectButton.setActionCommand(MenuConsts.newProject);
		newProjectButton.setToolTipText(MenuConsts.newProject);
		newProjectButton.addActionListener(menuHandler);
		
		ImageIcon openProjectIcon = new ImageIcon("data/toolbar/openProject.png");
		openProjectButton = new JButton(openProjectIcon);
		openProjectButton.setActionCommand(MenuConsts.openProject);
		openProjectButton.setToolTipText(MenuConsts.openProject);
		openProjectButton.addActionListener(menuHandler);
		
		ImageIcon saveProjectIcon = new ImageIcon("data/toolbar/save.png");
		saveProjectButton = new JButton(saveProjectIcon);
		saveProjectButton.setActionCommand(MenuConsts.saveProhect);
		saveProjectButton.setToolTipText(MenuConsts.saveProhect);
		saveProjectButton.addActionListener(menuHandler);
		
		ImageIcon closeProjectIcon = new ImageIcon("data/toolbar/closeProject.png");
		closeProjectButton = new JButton(closeProjectIcon);
		closeProjectButton.setActionCommand(MenuConsts.closeProject);
		closeProjectButton.setToolTipText(MenuConsts.closeProject);
		closeProjectButton.addActionListener(menuHandler);
		
		ImageIcon addScreenIcon = new ImageIcon("data/toolbar/addScreen.png");
		addScreenButton = new JButton(addScreenIcon);
		addScreenButton.setActionCommand(MenuConsts.addScreen);
		addScreenButton.setToolTipText(MenuConsts.addScreen);
		addScreenButton.addActionListener(menuHandler);
		
		ImageIcon addBGIcon = new ImageIcon("data/toolbar/addImage.png");
		addBGButton = new JButton(addBGIcon);
		addBGButton.setActionCommand(MenuConsts.addBG);
		addBGButton.setToolTipText(MenuConsts.addBG);
		addBGButton.addActionListener(menuHandler);
		
		ImageIcon addPathIcon = new ImageIcon("data/toolbar/addPath.png");
		addPathButton = new JButton(addPathIcon);
		addPathButton.setActionCommand(MenuConsts.addPath);
		addPathButton.setToolTipText(MenuConsts.addPath);
		addPathButton.addActionListener(menuHandler);
		
		toolBar.add(newProjectButton);
		toolBar.add(openProjectButton);
		toolBar.add(saveProjectButton);
		toolBar.add(closeProjectButton);
		toolBar.addSeparator(new Dimension(10, 32));
		toolBar.add(addScreenButton);
		toolBar.addSeparator(new Dimension(10, 32));
		toolBar.add(addBGButton);
		toolBar.addSeparator(new Dimension(10, 32));
		toolBar.add(addPathButton);
		
		return toolBar;
	}
	
	private void noProjectToolBarState() {
		newProjectButton.setEnabled(true);
		openProjectButton.setEnabled(true);
		saveProjectButton.setEnabled(false);
		closeProjectButton.setEnabled(false);
		addScreenButton.setEnabled(false);
		addBGButton.setEnabled(false);
		addPathButton.setEnabled(false);
	}
	
	private void projectOpenedToolBarState() {
		newProjectButton.setEnabled(true);
		openProjectButton.setEnabled(true);
		saveProjectButton.setEnabled(true);
		closeProjectButton.setEnabled(true);
		addScreenButton.setEnabled(true);
		addBGButton.setEnabled(false);
		addPathButton.setEnabled(false);
	}
	
	private void screenCreatedToolBarState() {
		newProjectButton.setEnabled(true);
		openProjectButton.setEnabled(true);
		saveProjectButton.setEnabled(true);
		closeProjectButton.setEnabled(true);
		addScreenButton.setEnabled(true);
		addBGButton.setEnabled(true);
		addPathButton.setEnabled(true);
	}
	
	// ==============================================================
	// project tree
	// ==============================================================
	
	private void createNoProjectTree() {
		projectTree = new JTree();
		createNoProjectTreeRoot();
		projectTree.addMouseListener(mouseListener);
		projectTreeScroll = new JScrollPane(projectTree);
	}
	
	private void createNoProjectTreeRoot() {
		NodeData data = new NodeData();
		data.setType(TREE_NODE_TYPE.ROOT);
		data.setData("[No Project]");
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(data);
		projectTree.setModel(new DefaultTreeModel(root));
	}
	
	private void createProjectTree() {
		createTreeRoot(projectData.getName(), projectData.getPath());
	}
	
	private void createTreeRoot(String name, String path) {
		NodeData data = new NodeData();
		data.setType(TREE_NODE_TYPE.ROOT);
		data.setData(String.format("%s [%s]", name, path));
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(data);
		projectTree.setModel(new DefaultTreeModel(root));
	}
	
	private void addTreeNode(ScreenData scrData) {
		DefaultMutableTreeNode root = getTreeRoot(); 
		if (root == null) { return; }
		
		NodeData data = new NodeData();
		data.setType(TREE_NODE_TYPE.SCREEN);
		data.setData(scrData);
		
		root.add(new DefaultMutableTreeNode(data));
		
		((DefaultTreeModel)projectTree.getModel()).reload(root);
		expandProjectTree();
		projectTree.setSelectionRow(projectTree.getRowCount()-1);
	}
	
	private void addTreeLeaf(DefaultMutableTreeNode node, String name) {
		DefaultMutableTreeNode root = getTreeRoot();
		if (root == null) { return; }
		if (node == null) { return; }
		
		NodeData data = new NodeData();
		data.setType(TREE_NODE_TYPE.BG);
		data.setData(name);
		
		node.add(new DefaultMutableTreeNode(data));
		
		((DefaultTreeModel)projectTree.getModel()).reload(root);
		expandProjectTree();
		projectTree.setSelectionRow(1 + root.getIndex(node) + node.getChildCount());
	}
	
	private void updateTreeLeaf(DefaultMutableTreeNode node, String name) {
		DefaultMutableTreeNode root = getTreeRoot();
		if (root == null) { return; }
		if (node == null) { return; }
		
		NodeData data = (NodeData)((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject();
		data.setData(name);
		
		((DefaultMutableTreeNode) node.getChildAt(0)).setUserObject(data);
		
		((DefaultTreeModel)projectTree.getModel()).reload(root);
		expandProjectTree();
		projectTree.setSelectionRow(1 + root.getIndex(node) + node.getChildCount());
	}
	
	private void expandProjectTree(){
		for (int i=projectTree.getRowCount(); i>=0; i--) {
			projectTree.expandRow(i);
		}
	}
	
	private void loadProjectTree() {
		createProjectTree();
		if (projectData.hasScreens()) {
			for (int i=0; i<projectData.getScreens().size(); i++) {
				addTreeNode(projectData.getScreens().get(i));
				if (projectData.getScreens().get(i).getBgImage() != null) {
					addTreeLeaf(getSelectedNode(), projectData.getScreens().get(i).getBgImage().name);
				}
			}
		}
	}
	
	private void selectRoot() {
		projectTree.setSelectionRow(0);
	}

	private void selectTreeNode(TreePath path) {
		projectTree.setSelectionPath(path);
	}

	private DefaultMutableTreeNode getTreeRoot() {
		return (DefaultMutableTreeNode)((DefaultTreeModel)projectTree.getModel()).getRoot();
	}
	
	private DefaultMutableTreeNode getSelectedNode() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) projectTree.getSelectionPath().getLastPathComponent();
		if (node.isLeaf() && !(((NodeData) node.getUserObject()).getData() instanceof ScreenData)) {
			return (DefaultMutableTreeNode) node.getParent();
		}
		return node;
	}
	
	private int getSelectedNodeIndex(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode root = getTreeRoot();
		if (root == null) { return -1; }
		if (node == null) { return -1; }
		return root.getIndex(node);
	}
	
	private DefaultMutableTreeNode getLeafParentNode(TreePath path) {
		Object[] parentPath = path.getParentPath().getPath();
		return (DefaultMutableTreeNode) parentPath[parentPath.length-1];
	}
	
	private ScreenData getNodeScreenData(DefaultMutableTreeNode node) {
		return (ScreenData)(((NodeData) node.getUserObject()).getData());
	}
	
	// ==============================================================
	// status panel
	// ==============================================================
	
	private void updateStatusMouseInfo(float zoom, float x, float y) {
		mouseInfoLabel.setText(String.format("zoom: %d%%; mouse at (%d, %d)", (int)(zoom*100), (int)x, (int)y));
	}
	
	private void updateStatusMemoryInfo(String msg) {
		memoryInfoLabel.setText("Memory consumption: " + msg);
	}
	
	// ==============================================================
	// log project structure
	// ==============================================================
	
	private void createProjectData(String name, String path) {
		projectData = new ProjectData();
		projectData.setName(name);
		projectData.setPath(path);
	}
	
	private void createScreenData(String name, int w, int h) {
		if (projectData == null) { return; }
		if (projectData.getScreens() == null) { return; }
		
		ScreenData screenData = new ScreenData();
		screenData.setName(name);
		screenData.setWidth(w);
		screenData.setHeight(h);
		screenData.setXmlPath(ProjectDataConverter.genScreenXMLPath(projectData.getPath(), name));
		screenData.setJsonPath(ProjectDataConverter.genScreenJSONPath(projectData.getPath(), name));

		projectData.getScreens().add(screenData);
	}
	
	private void createBGImageData(GdxImage bgImage, int screenIndex) {
		if (projectData == null) { return; }
		if (projectData.getScreens() == null) { return; }
		if ((screenIndex < 0) || (screenIndex >= projectData.getScreens().size())) { return; }
		
		projectData.getScreens().get(screenIndex).setBgImage(bgImage);
	}
	
	// ==============================================================
	// getters & setters
	// ==============================================================

	private Dimension getScreenSize(){
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public LwjglPanel getGlPanel() {
		return glPanel;
	}
	
	// =========================================================
	// inner classes
	// =========================================================
	
	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			TreePath path = projectTree.getPathForLocation(e.getX(), e.getY());
			if ((path != null) && (e.getClickCount() == 1)) {
				menuHandler.onTreeNodeSelected(path);
			}
		}
	};
}
