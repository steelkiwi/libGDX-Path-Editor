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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import com.steelkiwi.patheditor.consts.MsgConsts;
import com.steelkiwi.patheditor.utils.ColorUtils;

public class CreatePathDialog extends JDialog {
	private static final long serialVersionUID = 6647166676498170997L;
	private final static String DIALOG_TITLE = "Create Path";
	
    private JButton createBtn;
    private JButton cancelBtn;
    private JLabel nameLbl;
    private JLabel cntLbl;
    private JLabel controlColorLbl;
    private JLabel segmentColorLbl;
    private JLabel selectColorLbl;
    private JSeparator jSeparator1;
    private JTextField nameTextField;
    private JTextField cntTextField;
    private JTextField controlColorTextField;
    private JTextField segmentColorTextField;
    private JTextField selectColorTextField;
    
    public CreatePathDialog(JFrame owner, final ICreatePathHandler handler) {
		super(owner, DIALOG_TITLE);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		nameLbl = new JLabel();
		nameLbl.setText("Path Name");
		
		nameTextField = new JTextField();
		
		cntLbl = new JLabel();
		cntLbl.setText("Segment Vertices Count");
		
		cntTextField = new JTextField();
		
		controlColorLbl = new JLabel();
		controlColorLbl.setText("Control Vertices Color");
		
		controlColorTextField = new JTextField();
		controlColorTextField.setText("#f000ff");
		controlColorTextField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color initColor = ColorUtils.hexToColor(controlColorTextField.getText());
				Color controlColor = JColorChooser.showDialog(CreatePathDialog.this, MsgConsts.COLOR_CONTROL_TITLE, initColor);
				if (controlColor != null) {
					controlColorTextField.setText(ColorUtils.colorToHex(controlColor));
				}
			}
		});
		
		segmentColorLbl = new JLabel();
	    segmentColorLbl.setText("Segment Vertices Color");
	
	    segmentColorTextField = new JTextField();
	    segmentColorTextField.setText("#0000ff");
	    segmentColorTextField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color initColor = ColorUtils.hexToColor(segmentColorTextField.getText());
				Color controlColor = JColorChooser.showDialog(CreatePathDialog.this, MsgConsts.COLOR_SEGMENT_TITLE, initColor);
				if (controlColor != null) {
					segmentColorTextField.setText(ColorUtils.colorToHex(controlColor));
				}
			}
		});
	    
	    selectColorLbl = new JLabel();
	    selectColorLbl.setText("Selected Vertices Color");
	    
	    selectColorTextField = new JTextField();
	    selectColorTextField.setText("#00ff00");
	    selectColorTextField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color initColor = ColorUtils.hexToColor(selectColorTextField.getText());
				Color controlColor = JColorChooser.showDialog(CreatePathDialog.this, MsgConsts.COLOR_SELECT_TITLE, initColor);
				if (controlColor != null) {
					selectColorTextField.setText(ColorUtils.colorToHex(controlColor));
				}
			}
		});
	    
	    jSeparator1 = new JSeparator();
	    
	    createBtn = new JButton();
	    createBtn.setText("Create");
	    createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameTextField.getText();
				int cnt = Integer.parseInt(cntTextField.getText());
				String controlColor = controlColorTextField.getText();
				String segmentColor = segmentColorTextField.getText();
				String selectColor  = selectColorTextField.getText();
				if ((name.length() <= 0) || (cnt <= 0) || (controlColor.length() <= 0) || (segmentColor.length() <= 0) || (selectColor.length() <= 0)) {
					JOptionPane.showMessageDialog(CreatePathDialog.this, MsgConsts.FILL_ALL_FIELDS, DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				handler.getPathData(name, cnt, controlColor, segmentColor, selectColor);
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
	                .addComponent(jSeparator1, GroupLayout.Alignment.TRAILING)
	                .addComponent(nameTextField)
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(cntLbl)
	                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(cntTextField, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE))
	                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                    .addGap(0, 0, Short.MAX_VALUE)
	                    .addComponent(createBtn)
	                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                    .addComponent(cancelBtn))
	                .addGroup(layout.createSequentialGroup()
	                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
	                        .addComponent(controlColorLbl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(segmentColorLbl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                    .addGap(18, 18, 18)
	                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                        .addComponent(controlColorTextField)
	                        .addComponent(segmentColorTextField)))
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(nameLbl)
	                    .addGap(0, 0, Short.MAX_VALUE))
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(selectColorLbl)
	                    .addGap(18, 18, 18)
	                    .addComponent(selectColorTextField)))
	            .addContainerGap())
	    );
	    layout.setVerticalGroup(
	        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup()
	            .addContainerGap()
	            .addComponent(nameLbl)
	            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	            .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(cntTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(cntLbl))
	            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(controlColorTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(controlColorLbl))
	            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(segmentColorTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(segmentColorLbl))
	            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(selectColorTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(selectColorLbl))
	            .addGap(18, 18, 18)
	            .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
	            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(createBtn)
	                .addComponent(cancelBtn))
	            .addGap(18, 18, 18))
	    );
	
	    pack();
    }
    
    public interface ICreatePathHandler {
		public void getPathData(String name, int pointsCnt, String controlColor, String segmentColor, String selectColor);
	}
}
