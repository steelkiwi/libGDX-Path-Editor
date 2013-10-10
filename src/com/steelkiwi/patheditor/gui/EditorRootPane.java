/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.steelkiwi.patheditor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.badlogic.gdx.math.Vector3;
import com.steelkiwi.patheditor.consts.MenuConsts;
import com.steelkiwi.patheditor.consts.MenuConsts.TREE_NODE_TYPE;
import com.steelkiwi.patheditor.consts.MsgConsts;
import com.steelkiwi.patheditor.consts.ResourcesConsts;
import com.steelkiwi.patheditor.gdx.SplineBuilder.renderMode;
import com.steelkiwi.patheditor.path.Path;
import com.steelkiwi.patheditor.proj.ProjectData;
import com.steelkiwi.patheditor.proj.ProjectDataConverter;
import com.steelkiwi.patheditor.proj.ScreenData;
import com.steelkiwi.patheditor.widgets.GdxImage;
import com.steelkiwi.patheditor.widgets.GdxPath;

public class EditorRootPane extends JFrame implements IProjectHandler {
	private static final long serialVersionUID = -3021481019247627930L;

	private final static String WINDOW_TITLE = "libGDX Path Editor";

	private IMenuHandler menuHandler;
	private JMenuItem newProjectItem;
	private JMenuItem openProjectItem;
	private JMenuItem saveProjectItem;
	private JMenuItem closeProjectItem;
	private JMenuItem addScreen;
	private JMenuItem addBG;
	private JCheckBoxMenuItem addVertex;
	private JCheckBoxMenuItem editVertex;
	private JCheckBoxMenuItem insertVertex;
	private JCheckBoxMenuItem removeVertex;
	private JMenuItem clearPath;
	
	private JButton newProjectButton;
	private JButton openProjectButton;
	private JButton saveProjectButton;
	private JButton closeProjectButton;
	private JButton addScreenButton;
	private JButton addBGButton;
	private JToggleButton addVertexButton;
	private JToggleButton editVertexButton;
	private JToggleButton insertVertexButton;
	private JToggleButton removeVertexButton;
	private JButton clearPathButton;
	
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
		glPanel.getGdxApp().setScreen(null, glPanel.getCanvasWidth(), glPanel.getCanvasHeight(), this, -1);
		
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
		addTreeNode(projectData.getScreens().get(projectData.getScreens().size()-1));
		screenCreatedMenuBarState();
		screenCreatedToolBarState();
		setScreen(projectData.getScreens().get(projectData.getScreens().size()-1));
	}
	
	@Override
	public void onScreenSwitched(ScreenData scrData, TreePath path) {
		selectTreeNode(path);
		setScreen(scrData);
	}
	
	private void setScreen(ScreenData scrData) {
		glPanel.getGdxApp().setScreen(scrData, glPanel.getCanvasWidth(), glPanel.getCanvasHeight(), this, getSelectedNodeIndex(getSelectedNode()));
	}
	
	@Override
	public void onRootSwitched() {
		selectRoot();
		glPanel.getGdxApp().setScreen(null, glPanel.getCanvasWidth(), glPanel.getCanvasHeight(), this, -1);
	}
	
	// ==============================================================
	// screen elements creation
	// ==============================================================
	
	@Override
	public void onBGImageAdded(String name, String path, float scaleCoef) {
		glPanel.getGdxApp().onAddBGTexture(name, path, scaleCoef);
		createBGImageData(glPanel.getGdxApp().getBGImage(), getSelectedNodeIndex(getSelectedNode()));
		
		if (screenHasBGImage()) {
			updateTreeLeaf(getSelectedNode(), name, TREE_NODE_TYPE.BG);
		}
		else {
			addTreeLeaf(getSelectedNode(), name, TREE_NODE_TYPE.BG);
		}
	}

	@Override
	public void onLeafSwitched(String name, TreePath path) {
		selectTreeNode(path);
		ScreenData scrData = getNodeScreenData(getLeafParentNode(path));
		setScreen(scrData);
	}
	
	private boolean screenHasBGImage() {
		DefaultMutableTreeNode node;
		NodeData data;
		boolean hasBG = false;
		for (int i=0; i<getSelectedNode().getChildCount(); i++) {
			node = (DefaultMutableTreeNode) getSelectedNode().getChildAt(i);
			data = (NodeData) node.getUserObject();
			if (data.getType().equals(TREE_NODE_TYPE.BG)) {
				hasBG = true;
			}
		}
		return hasBG;
	}
	
	// ==============================================================
	// path
	// ==============================================================

	@Override
	public void onPathCreate(String name, int pointsCnt, String controlColor, String segmentColor, String selectColor) {
		glPanel.getGdxApp().onAddPath(name, pointsCnt, controlColor, segmentColor, selectColor, this, getSelectedNodeIndex(getSelectedNode()));
		createPathData(glPanel.getGdxApp().getPath(), getSelectedNodeIndex(getSelectedNode()));
		
		if (screenHasPath()) {
			updateTreeLeaf(getSelectedNode(), name, TREE_NODE_TYPE.PATH);
		}
		else {
			addTreeLeaf(getSelectedNode(), name, TREE_NODE_TYPE.PATH);
		}
	}
	
	private boolean screenHasPath() {
		DefaultMutableTreeNode node;
		NodeData data;
		boolean hasPath = false;
		for (int i=0; i<getSelectedNode().getChildCount(); i++) {
			node = (DefaultMutableTreeNode) getSelectedNode().getChildAt(i);
			data = (NodeData) node.getUserObject();
			if (data.getType().equals(TREE_NODE_TYPE.PATH)) {
				hasPath = true;
			}
		}
		return hasPath;
	}
	
	@Override
	public void onPathVertexAdd() {
		renderMode mode = glPanel.getGdxApp().getPathMode();
		if ((mode == null) || (mode != renderMode.ADD)) {
			addVertex.setSelected(true);
			addVertexButton.setSelected(true);
		}
		else if (mode == renderMode.ADD) {
			addVertex.setSelected(false);
			addVertexButton.setSelected(false);
		}
		editVertex.setSelected(false);
		insertVertex.setSelected(false);
		removeVertex.setSelected(false);
		editVertexButton.setSelected(false);
		insertVertexButton.setSelected(false);
		removeVertexButton.setSelected(false);

		glPanel.getGdxApp().setPathMode(renderMode.ADD);
	}
	
	@Override
	public void onPathVertexEdit() {
		renderMode mode = glPanel.getGdxApp().getPathMode();
		if ((mode == null) || (mode != renderMode.EDIT)) {
			editVertex.setSelected(true);
			editVertexButton.setSelected(true);
		}
		else if (mode == renderMode.EDIT) {
			editVertex.setSelected(false);
			editVertexButton.setSelected(false);
		}
		addVertex.setSelected(false);
		insertVertex.setSelected(false);
		removeVertex.setSelected(false);
		addVertexButton.setSelected(false);
		insertVertexButton.setSelected(false);
		removeVertexButton.setSelected(false);
		
		glPanel.getGdxApp().setPathMode(renderMode.EDIT);
	}

	@Override
	public void onPathVertexInsert() {
		renderMode mode = glPanel.getGdxApp().getPathMode();
		if ((mode == null) || (mode != renderMode.INSERT)) {
			insertVertex.setSelected(true);
			insertVertexButton.setSelected(true);
		}
		else if (mode == renderMode.INSERT) {
			insertVertex.setSelected(false);
			insertVertexButton.setSelected(false);
		}
		addVertex.setSelected(false);
		editVertex.setSelected(false);
		removeVertex.setSelected(false);
		addVertexButton.setSelected(false);
		editVertexButton.setSelected(false);
		removeVertexButton.setSelected(false);
		
		glPanel.getGdxApp().setPathMode(renderMode.INSERT);
	}

	@Override
	public void onPathVertexRemove() {
		renderMode mode = glPanel.getGdxApp().getPathMode();
		if ((mode == null) || (mode != renderMode.REMOVE)) {
			removeVertex.setSelected(true);
			removeVertexButton.setSelected(true);
		}
		else if (mode == renderMode.REMOVE) {
			removeVertex.setSelected(false);
			removeVertexButton.setSelected(false);
		}
		addVertex.setSelected(false);
		editVertex.setSelected(false);
		insertVertex.setSelected(false);
		addVertexButton.setSelected(false);
		editVertexButton.setSelected(false);
		insertVertexButton.setSelected(false);
		
		glPanel.getGdxApp().setPathMode(renderMode.REMOVE);
	}

	@Override
	public void onPathClear() {
		addVertex.setSelected(false);
		editVertex.setSelected(false);
		insertVertex.setSelected(false);
		removeVertex.setSelected(false);
		addVertexButton.setSelected(false);
		editVertexButton.setSelected(false);
		insertVertexButton.setSelected(false);
		removeVertexButton.setSelected(false);
		
		glPanel.getGdxApp().onClearPath();
		glPanel.getGdxApp().setPathMode(null);
	}

	@Override
	public void onPathUpdated(int screenIndex, ArrayList<Vector3> controlPath, Path path) {
		updatePathData(screenIndex, controlPath, path);
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
		newProjectItem.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_newProject)));
		menu.add(newProjectItem);
		
		openProjectItem = new JMenuItem(MenuConsts.openProject);
		openProjectItem.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_openProject)));
		openProjectItem.addActionListener(menuHandler);
		menu.add(openProjectItem);
		
		saveProjectItem = new JMenuItem(MenuConsts.saveProhect);
		saveProjectItem.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_saveProject)));
		saveProjectItem.addActionListener(menuHandler);
		menu.add(saveProjectItem);
		
		closeProjectItem = new JMenuItem(MenuConsts.closeProject);
		closeProjectItem.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_closeProject)));
		closeProjectItem.addActionListener(menuHandler);
		menu.add(closeProjectItem);
		
		menu.add(new JSeparator());
		
		JMenuItem exitItem = new JMenuItem(MenuConsts.exit);
		exitItem.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_exit)));
		exitItem.addActionListener(menuHandler);
		menu.add(exitItem);
		
		return menu;
	}
	
	private JMenu createAddScreenMenu(){
		JMenu menu = new JMenu(MenuConsts.screen);
		
		addScreen = new JMenuItem(MenuConsts.addScreen);
		addScreen.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_addScreen)));
		addScreen.addActionListener(menuHandler);
		menu.add(addScreen);
		
		return menu;
	}
	
	private JMenu createAddBGMenu(){
		JMenu menu = new JMenu(MenuConsts.bg);
		
		addBG = new JMenuItem(MenuConsts.addBG);
		addBG.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_addBG)));
		addBG.addActionListener(menuHandler);
		menu.add(addBG);
		
		return menu;
	}
	
	private JMenu createAddPathMenu(){
		JMenu menu = new JMenu(MenuConsts.path);
		
		addVertex = new JCheckBoxMenuItem(MenuConsts.addVertex);
		addVertex.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_addVertex)));
		addVertex.addActionListener(menuHandler);
		menu.add(addVertex);
		
		editVertex = new JCheckBoxMenuItem(MenuConsts.moveVertex);
		editVertex.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_editVertex)));
		editVertex.addActionListener(menuHandler);
		menu.add(editVertex);
		
		insertVertex = new JCheckBoxMenuItem(MenuConsts.insertVertex);
		insertVertex.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_insertVertex)));
		insertVertex.addActionListener(menuHandler);
		menu.add(insertVertex);
		
		removeVertex = new JCheckBoxMenuItem(MenuConsts.removeVertex);
		removeVertex.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_removeVertex)));
		removeVertex.addActionListener(menuHandler);
		menu.add(removeVertex);
		
		menu.add(new JSeparator());
		
		clearPath = new JMenuItem(MenuConsts.clearPath);
		clearPath.setIcon(new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_menu_clearPath)));
		clearPath.addActionListener(menuHandler);
		menu.add(clearPath);
		
		return menu;
	}
	
	private void noProjectMenuBarState() {
		newProjectItem.setEnabled(true);
		openProjectItem.setEnabled(true);
		saveProjectItem.setEnabled(false);
		closeProjectItem.setEnabled(false);
		addScreen.setEnabled(false);
		addBG.setEnabled(false);
		addVertex.setEnabled(false);
		editVertex.setEnabled(false);
		insertVertex.setEnabled(false);
		removeVertex.setEnabled(false);
		clearPath.setEnabled(false);
	}
	
	private void projectOpenedMenuBarState() {
		newProjectItem.setEnabled(true);
		openProjectItem.setEnabled(true);
		saveProjectItem.setEnabled(true);
		closeProjectItem.setEnabled(true);
		addScreen.setEnabled(true);
		addBG.setEnabled(false);
		addVertex.setEnabled(false);
		editVertex.setEnabled(false);
		insertVertex.setEnabled(false);
		removeVertex.setEnabled(false);
		clearPath.setEnabled(false);
	}
	
	private void screenCreatedMenuBarState() {
		newProjectItem.setEnabled(true);
		openProjectItem.setEnabled(true);
		saveProjectItem.setEnabled(true);
		closeProjectItem.setEnabled(true);
		addScreen.setEnabled(true);
		addBG.setEnabled(true);
		addVertex.setEnabled(true);
		editVertex.setEnabled(true);
		insertVertex.setEnabled(true);
		removeVertex.setEnabled(true);
		clearPath.setEnabled(true);
	}
	
	// ==============================================================
	// toolbar
	// ==============================================================
	
	private JToolBar createToolbar() {
		JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		
		ImageIcon newProjectIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_newProject));
		newProjectButton = new JButton(newProjectIcon);
		newProjectButton.setActionCommand(MenuConsts.newProject);
		newProjectButton.setToolTipText(MenuConsts.newProject);
		newProjectButton.addActionListener(menuHandler);
		
		ImageIcon openProjectIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_openProject));
		openProjectButton = new JButton(openProjectIcon);
		openProjectButton.setActionCommand(MenuConsts.openProject);
		openProjectButton.setToolTipText(MenuConsts.openProject);
		openProjectButton.addActionListener(menuHandler);
		
		ImageIcon saveProjectIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_saveProject));
		saveProjectButton = new JButton(saveProjectIcon);
		saveProjectButton.setActionCommand(MenuConsts.saveProhect);
		saveProjectButton.setToolTipText(MenuConsts.saveProhect);
		saveProjectButton.addActionListener(menuHandler);
		
		ImageIcon closeProjectIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_closeProject));
		closeProjectButton = new JButton(closeProjectIcon);
		closeProjectButton.setActionCommand(MenuConsts.closeProject);
		closeProjectButton.setToolTipText(MenuConsts.closeProject);
		closeProjectButton.addActionListener(menuHandler);
		
		ImageIcon addScreenIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_addScreen));
		addScreenButton = new JButton(addScreenIcon);
		addScreenButton.setActionCommand(MenuConsts.addScreen);
		addScreenButton.setToolTipText(MenuConsts.addScreen);
		addScreenButton.addActionListener(menuHandler);
		
		ImageIcon addBGIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_addBG));
		addBGButton = new JButton(addBGIcon);
		addBGButton.setActionCommand(MenuConsts.addBG);
		addBGButton.setToolTipText(MenuConsts.addBG);
		addBGButton.addActionListener(menuHandler);
		
		ImageIcon addVertexIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_addVertex));
		addVertexButton = new JToggleButton(addVertexIcon);
		addVertexButton.setActionCommand(MenuConsts.addVertex);
		addVertexButton.setToolTipText(MenuConsts.addVertex);
		addVertexButton.addActionListener(menuHandler);
		
		ImageIcon editVertexIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_editVertex));
		editVertexButton = new JToggleButton(editVertexIcon);
		editVertexButton.setActionCommand(MenuConsts.moveVertex);
		editVertexButton.setToolTipText(MenuConsts.moveVertex);
		editVertexButton.addActionListener(menuHandler);
		
		ImageIcon insertVertexIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_insertVertex));
		insertVertexButton = new JToggleButton(insertVertexIcon);
		insertVertexButton.setActionCommand(MenuConsts.insertVertex);
		insertVertexButton.setToolTipText(MenuConsts.insertVertex);
		insertVertexButton.addActionListener(menuHandler);
		
		ImageIcon removeVertexIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_removeVertex));
		removeVertexButton = new JToggleButton(removeVertexIcon);
		removeVertexButton.setActionCommand(MenuConsts.removeVertex);
		removeVertexButton.setToolTipText(MenuConsts.removeVertex);
		removeVertexButton.addActionListener(menuHandler);
		
		ImageIcon clearPathIcon = new ImageIcon(this.getClass().getResource(ResourcesConsts.ic_toolbar_clearPath));
		clearPathButton = new JButton(clearPathIcon);
		clearPathButton.setActionCommand(MenuConsts.clearPath);
		clearPathButton.setToolTipText(MenuConsts.clearPath);
		clearPathButton.addActionListener(menuHandler);
		
		toolBar.add(newProjectButton);
		toolBar.add(openProjectButton);
		toolBar.add(saveProjectButton);
		toolBar.add(closeProjectButton);
		toolBar.addSeparator(new Dimension(10, 32));
		toolBar.add(addScreenButton);
		toolBar.addSeparator(new Dimension(10, 32));
		toolBar.add(addBGButton);
		toolBar.addSeparator(new Dimension(10, 32));
		toolBar.add(addVertexButton);
		toolBar.add(editVertexButton);
		toolBar.add(insertVertexButton);
		toolBar.add(removeVertexButton);
		toolBar.add(clearPathButton);
		
		return toolBar;
	}
	
	private void noProjectToolBarState() {
		newProjectButton.setEnabled(true);
		openProjectButton.setEnabled(true);
		saveProjectButton.setEnabled(false);
		closeProjectButton.setEnabled(false);
		addScreenButton.setEnabled(false);
		addBGButton.setEnabled(false);
		addVertexButton.setEnabled(false);
		editVertexButton.setEnabled(false);
		insertVertexButton.setEnabled(false);
		removeVertexButton.setEnabled(false);
		clearPathButton.setEnabled(false);
	}
	
	private void projectOpenedToolBarState() {
		newProjectButton.setEnabled(true);
		openProjectButton.setEnabled(true);
		saveProjectButton.setEnabled(true);
		closeProjectButton.setEnabled(true);
		addScreenButton.setEnabled(true);
		addBGButton.setEnabled(false);
		addVertexButton.setEnabled(false);
		editVertexButton.setEnabled(false);
		insertVertexButton.setEnabled(false);
		removeVertexButton.setEnabled(false);
		clearPathButton.setEnabled(false);
	}
	
	private void screenCreatedToolBarState() {
		newProjectButton.setEnabled(true);
		openProjectButton.setEnabled(true);
		saveProjectButton.setEnabled(true);
		closeProjectButton.setEnabled(true);
		addScreenButton.setEnabled(true);
		addBGButton.setEnabled(true);
		addVertexButton.setEnabled(true);
		editVertexButton.setEnabled(true);
		insertVertexButton.setEnabled(true);
		removeVertexButton.setEnabled(true);
		clearPathButton.setEnabled(true);
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
	
	private void addTreeLeaf(DefaultMutableTreeNode node, String name, TREE_NODE_TYPE type) {
		DefaultMutableTreeNode root = getTreeRoot();
		if (root == null) { return; }
		if (node == null) { return; }
		
		NodeData data = new NodeData();
		data.setType(type);
		data.setData(name);
		
		node.add(new DefaultMutableTreeNode(data));
		
		((DefaultTreeModel)projectTree.getModel()).reload(root);
		expandProjectTree();
		projectTree.setSelectionRow(1 + root.getIndex(node) + node.getChildCount());
	}
	
	private void updateTreeLeaf(DefaultMutableTreeNode node, String name, TREE_NODE_TYPE type) {
		DefaultMutableTreeNode root = getTreeRoot();
		if (root == null) { return; }
		if (node == null) { return; }
		
		NodeData data = (NodeData)((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject();
		if (!data.getType().equals(type)) { return; }
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
			DefaultMutableTreeNode node;
			for (int i=0; i<projectData.getScreens().size(); i++) {
				addTreeNode(projectData.getScreens().get(i));
				node = getSelectedNode();
				if (projectData.getScreens().get(i).getBgImage() != null) {
					addTreeLeaf(node, projectData.getScreens().get(i).getBgImage().name, TREE_NODE_TYPE.BG);
				}
				if (projectData.getScreens().get(i).getPath() != null) {
					addTreeLeaf(node, projectData.getScreens().get(i).getPath().getName(), TREE_NODE_TYPE.PATH);
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
	
	private void createPathData(GdxPath path, int screenIndex) {
		if (projectData == null) { return; }
		if (projectData.getScreens() == null) { return; }
		if ((screenIndex < 0) || (screenIndex >= projectData.getScreens().size())) { return; }
		
		if (path != null) {
			path.setXmlPath(ProjectDataConverter.genPathXMLPath(ProjectDataConverter.getScreenDir(projectData.getScreens().get(screenIndex).getXmlPath()), path.getName()));
			path.setJsonPath(ProjectDataConverter.genPathJSONPath(ProjectDataConverter.getScreenDir(projectData.getScreens().get(screenIndex).getJsonPath()), path.getName()));
		}
		
		projectData.getScreens().get(screenIndex).setPath(path);
	}
	
	private void updatePathData(int screenIndex, ArrayList<Vector3> controlPath, Path path) {
		if (projectData == null) { return; }
		if (projectData.getScreens() == null) { return; }
		if ((screenIndex < 0) || (screenIndex >= projectData.getScreens().size())) { return; }
		if (screenIndex != getSelectedNodeIndex(getSelectedNode())) { return; }
		
		GdxPath gdxPath = projectData.getScreens().get(screenIndex).getPath();
		if (gdxPath != null) {
			gdxPath.setControlPath(controlPath);
			gdxPath.setPath(path);
		}
	}
	
	public String getProjectHomdeDir() {
		return (projectData != null && projectData.getPath() != null) ? projectData.getPath() : "";
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
