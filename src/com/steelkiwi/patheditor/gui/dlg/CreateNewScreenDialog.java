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

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import com.steelkiwi.patheditor.consts.MsgConsts;

public class CreateNewScreenDialog extends JDialog {
	private static final long serialVersionUID = 3872337799925886779L;
	private final static String DIALOG_TITLE = "Create New Screen";
	
	public enum screenSizes {
		s240x320("s320x240", 320, 240),
		s400x240("s400x240", 400, 240),
		s432x240("s432x240", 432, 240),
		s480x320("s480x320", 480, 320),
		s640x480("s640x480", 640, 480),
		s800x480("s800x480", 800, 480),
		s854x480("s854x480", 854, 480),
		s960x540("s960x540", 960, 540),
		s960x640("s960x640", 960, 640),
		s976x600("s976x600", 976, 600),
		s1024x600("s1024x600", 1024, 600),
		s1024x720("s1024x720", 1024, 720),
		s1024x768("s1024x768", 1024, 768),
		s1232x800("s1232x800", 1232, 800),
		s1280x720("s1280x720", 1280, 720),
		s1280x768("s1280x768", 1280, 768),
		s1280x800("s1280x800", 1280, 800),
		s1366x768("s1366x768", 1366, 768);
		
		private String name;
		private int w;
		private int h;
		
		private screenSizes(String name, int w, int h) {
			this.name = name;
			this.w = w;
			this.h = h;
		}
		
		public String getName() {
			return name;
		}

		public int getW() {
			return w;
		}

		public int getH() {
			return h;
		}

		public static int getWidthByName(String cname) {
			for (int i=0; i<screenSizes.values().length; i++) {
				if (screenSizes.values()[i].getName().equals(cname)) {
					return screenSizes.values()[i].getW();
				}
			}
			return 0;
		}
		
		public static int getHeightByName(String cname) {
			for (int i=0; i<screenSizes.values().length; i++) {
				if (screenSizes.values()[i].getName().equals(cname)) {
					return screenSizes.values()[i].getH();
				}
			}
			return 0;
		}
	}
	
	private String sizeList[];
	
    private JButton createBtn;
    private JButton cancelBtn;
    private JComboBox<String> sizesComboBox;
    private JLabel screenNameLbl;
    private JLabel sizesNameLbl;
    private JSeparator jSeparator1;
    private JTextField screenNameTextField;
    
	public CreateNewScreenDialog(JFrame owner, final ICreateNewScreenHandler handler) {
		super(owner, DIALOG_TITLE);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		sizeList = new String[screenSizes.values().length];
		for (int i=0; i<screenSizes.values().length; i++) {
			sizeList[i] = screenSizes.values()[i].getName();
		}
		
		screenNameLbl = new JLabel();
		screenNameLbl.setText("Screen Name");
		
		screenNameTextField = new JTextField();
		
		sizesNameLbl = new JLabel();
		sizesNameLbl.setText("Available Screen Sizes");
		
		sizesComboBox = new JComboBox<String>();
		sizesComboBox.setModel(new DefaultComboBoxModel<String>(sizeList));
		
        jSeparator1 = new JSeparator();

        createBtn = new JButton();
        createBtn.setText("Create");
        createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String screenName = screenNameTextField.getText();
				String sizeName = (String) sizesComboBox.getSelectedItem(); 
				int w = screenSizes.getWidthByName(sizeName);
				int h = screenSizes.getHeightByName(sizeName);
				if ((screenName.length() <= 0) || (w <= 0) || (h <= 0)) {
					JOptionPane.showMessageDialog(CreateNewScreenDialog.this, MsgConsts.FILL_ALL_FIELDS, DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
					return;
				}
				handler.getNewScreenData(screenName, w, h);
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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(screenNameLbl)
                        .addGap(0, 85, Short.MAX_VALUE))
                    .addComponent(screenNameTextField))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(sizesComboBox, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
                    .addComponent(sizesNameLbl))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(createBtn)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelBtn)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(screenNameLbl)
                    .addComponent(sizesNameLbl, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(screenNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(sizesComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
	
	public interface ICreateNewScreenHandler {
		public void getNewScreenData(String name, int w, int h);
	}
}
