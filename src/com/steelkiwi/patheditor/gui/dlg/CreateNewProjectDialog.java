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

package com.steelkiwi.patheditor.gui.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

public class CreateNewProjectDialog extends JDialog {
	private static final long serialVersionUID = -3972661193101362209L;
	private final static String DIALOG_TITLE = "Create New project";
	
    private JButton chooseBtn;
    private JButton createBtn;
    private JButton cancelBtn;
    private JLabel projNameLbl;
    private JLabel folderNameLbl;
    private JSeparator jSeparator1;
    private JTextField projNameTextField;
    private JTextField folderNameTextField;
    
	public CreateNewProjectDialog(JFrame owner, final ICreateNewProjectHandler handler) {
		super(owner, DIALOG_TITLE);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		
        projNameLbl = new JLabel();
        projNameLbl.setText("Project Name");
        
        projNameTextField = new JTextField();
        
        folderNameLbl = new JLabel();
        folderNameLbl.setText("Project Folder");
        
        folderNameTextField = new JTextField();
        
        chooseBtn = new JButton();
        chooseBtn.setText("Choose");
        chooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = fileChooser.showOpenDialog(CreateNewProjectDialog.this);
				if (status == JFileChooser.APPROVE_OPTION) {
					folderNameTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
        
        jSeparator1 = new JSeparator();
        
        createBtn = new JButton();
        createBtn.setText("Create");
        createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String projName = projNameTextField.getText();
				String folder = folderNameTextField.getText();
				if ((projName.length() <= 0) || (folder.length() <= 0)) {
					JOptionPane.showMessageDialog(CreateNewProjectDialog.this, "Please, fill all fields", DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
					return;
				}
/*				if (!FileUtils.checkDirIsEmpty(folder)) {
					JOptionPane.showMessageDialog(CreateNewProjectDialog.this, "Selected directory is not empty", DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
					return;
				}*/
				handler.getNewProjectData(projName, folder);
				dispose();
			}
		});
        
        cancelBtn = new JButton();
        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addComponent(projNameTextField, GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(projNameLbl)
                            .addComponent(folderNameLbl))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 247, Short.MAX_VALUE)
                        .addComponent(createBtn)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(folderNameTextField)
                        .addGap(16, 16, 16)
                        .addComponent(chooseBtn)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projNameLbl)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(folderNameLbl, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(folderNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(chooseBtn))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(createBtn)
                    .addComponent(cancelBtn))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
	}
	
	public interface ICreateNewProjectHandler {
		public void getNewProjectData(String name, String folder);
	}
}
