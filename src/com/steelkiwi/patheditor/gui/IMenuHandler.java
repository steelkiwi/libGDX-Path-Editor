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
import com.steelkiwi.patheditor.gui.dlg.CreatePathDialog;
import com.steelkiwi.patheditor.gui.dlg.CreatePathDialog.ICreatePathHandler;
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
		
		if (arg0.getActionCommand().equals(MenuConsts.addVertex)) {
			showCreatePathDialog();
			return;
		}
		
		// ==============================================================
		// path
		// ==============================================================
		
		if (arg0.getActionCommand().equals(MenuConsts.moveVertex)) {
			rootPane.onPathVertexEdit();
			return;
		}
		
		if (arg0.getActionCommand().equals(MenuConsts.insertVertex)) {
			rootPane.onPathVertexInsert();
			return;
		}
		
		if (arg0.getActionCommand().equals(MenuConsts.removeVertex)) {
			rootPane.onPathVertexRemove();
			return;
		}
		
		if (arg0.getActionCommand().equals(MenuConsts.clearPath)) {
			rootPane.onPathClear();
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
				rootPane.onLeafSwitched((String)data.getData(), path);
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
		CreateImageDialog dlg = new CreateImageDialog(rootPane, rootPane.getProjectHomdeDir(), new ICreateImageHandler() {
			@Override
			public void getImageData(String name, String path, float scaleCoef) {
				rootPane.onBGImageAdded(name, path, scaleCoef);
			}
		});
		SwingHelper.setDialogWindowToCenter(dlg);
		dlg.setModal(true);
		dlg.setVisible(true);
	}
	
	private void showCreatePathDialog() {
		if (rootPane.getGlPanel().getGdxApp().isPathInit()) { 
			rootPane.onPathVertexAdd();
			return; 
		}
		
		CreatePathDialog dlg = new CreatePathDialog(rootPane, new ICreatePathHandler() {
			@Override
			public void getPathData(String name, int pointsCnt, String controlColor, String segmentColor, String selectColor) {
				rootPane.onPathCreate(name, pointsCnt, controlColor, segmentColor, selectColor);
				rootPane.onPathVertexAdd();
			}
		});
		SwingHelper.setDialogWindowToCenter(dlg);
		dlg.setModal(true);
		dlg.setVisible(true);
	}
}
