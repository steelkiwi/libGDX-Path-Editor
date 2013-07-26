package com.steelkiwi.patheditor.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.steelkiwi.patheditor.consts.MenuConsts;
import com.steelkiwi.patheditor.gui.dlg.CreateImageDialog;
import com.steelkiwi.patheditor.gui.dlg.CreateImageDialog.ICreateImageHandler;
import com.steelkiwi.patheditor.gui.dlg.CreateNewProjectDialog;
import com.steelkiwi.patheditor.gui.dlg.CreateNewProjectDialog.ICreateNewProjectHandler;
import com.steelkiwi.patheditor.gui.dlg.CreateNewScreenDialog;
import com.steelkiwi.patheditor.gui.dlg.CreateNewScreenDialog.ICreateNewScreenHandler;
import com.steelkiwi.patheditor.proj.ScreenData;
import com.steelkiwi.patheditor.utils.SwingHelper;

public class IMenuHandler implements ActionListener, ITreeHandler {

	private EditorRootPane rootPane;
	
	public IMenuHandler(EditorRootPane rootPane) {
		this.rootPane = rootPane;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("action performed " + arg0.getActionCommand());
		
		// ==============================================================
		// project
		// ==============================================================
		
		if (arg0.getActionCommand().equals(MenuConsts.newProject)) {
			showCreateNewProjectDialog();
			return;
		}
		
		if (arg0.getActionCommand().equals(MenuConsts.openProject)) {
			showOpenProjectDialog();
			return;
		}
		
		if (arg0.getActionCommand().equals(MenuConsts.saveProhect)) {
			rootPane.onProjectedSaved(true);
			return;
		}
		
		if (arg0.getActionCommand().equals(MenuConsts.closeProject)) {
			rootPane.onProjectClosed();
			return;
		}
		
		if (arg0.getActionCommand().equals(MenuConsts.exit)) {
			WindowEvent windowClosing = new WindowEvent(rootPane, WindowEvent.WINDOW_CLOSING);
			rootPane.dispatchEvent(windowClosing);
			return;
		}
		
		// ==============================================================
		// add
		// ==============================================================
		
		if (arg0.getActionCommand().equals(MenuConsts.addScreen)) {
			showCreateNewScreenDialog();
			return;
		}
		
		if (arg0.getActionCommand().equals(MenuConsts.addBG)) {
			showSelectTextureDialog();
			return;
		}
	}

	@Override
	public void onTreeNodeSelected(TreePath path) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		NodeData data = (NodeData) node.getUserObject();
		switch (data.getType()) {
			case ROOT: {
				rootPane.onRootSwitched();
				break;
			}
			case SCREEN: {
				rootPane.onScreenSwitched((ScreenData)data.getData(), path);
				break;
			}
			case BG: {
				rootPane.onLeafSwitched((String)data.getData(), path);
				break;
			}
			case PATH: {
				//TODO
				break;
			}
		}
	}
	
	// ==============================================================
	// dialogs
	// ==============================================================
	
	private void showCreateNewProjectDialog() {
		CreateNewProjectDialog dlg = new CreateNewProjectDialog(rootPane, new ICreateNewProjectHandler() {
			@Override
			public void getNewProjectData(String name, String folder) {
				rootPane.onProjectCreated(name, folder);
			}
		});
		SwingHelper.setDialogWindowToCenter(dlg);
		dlg.setModal(true);
		dlg.setVisible(true);
	}
	
	private void showOpenProjectDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int status = fileChooser.showOpenDialog(rootPane);
		if (status == JFileChooser.APPROVE_OPTION) {
			rootPane.onProjectOpened(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void showCreateNewScreenDialog() {
		CreateNewScreenDialog dlg = new CreateNewScreenDialog(rootPane, new ICreateNewScreenHandler() {
			@Override
			public void getNewScreenData(String name, int w, int h) {
				rootPane.onScreenAdded(name, w, h);
			}
		});
		SwingHelper.setDialogWindowToCenter(dlg);
		dlg.setModal(true);
		dlg.setVisible(true);
	}
	
	private void showSelectTextureDialog() {
		CreateImageDialog dlg = new CreateImageDialog(rootPane, new ICreateImageHandler() {
			@Override
			public void getImageData(String name, String path, float scaleCoef) {
				rootPane.onBGImageAdded(name, path, scaleCoef);
			}
		});
		SwingHelper.setDialogWindowToCenter(dlg);
		dlg.setModal(true);
		dlg.setVisible(true);
	}
}
