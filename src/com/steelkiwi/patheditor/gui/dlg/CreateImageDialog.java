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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.steelkiwi.patheditor.consts.MsgConsts;

public class CreateImageDialog extends JDialog {
	private static final long serialVersionUID = 3215665740764037880L;
	private final static String DIALOG_TITLE = "Create Image";
	
	public enum widgetType { IMAGE };
	
    private JButton chooseBtn;
    private JButton createBtn;
    private JButton cancelBtn;
    private JLabel nameLbl;
    private JLabel pathLbl;
    private JLabel coefLbl;
    private JSeparator jSeparator1;
    private JTextField nameTextField;
    private JTextField pathTextField;
    private JTextField coefTextField;
    
	public CreateImageDialog(JFrame owner, final ICreateImageHandler handler) {
		super(owner, DIALOG_TITLE);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		
        nameLbl = new JLabel();
        nameLbl.setText("Image Name");
        
        nameTextField = new JTextField();
        
        pathLbl = new JLabel();
        pathLbl.setText("Texture location");
        
        pathTextField = new JTextField();
        
        chooseBtn = new JButton();
        chooseBtn.setText("Choose");
        chooseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int status = fileChooser.showOpenDialog(CreateImageDialog.this);
				if (status == JFileChooser.APPROVE_OPTION) {
					pathTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
        
        coefLbl = new JLabel();
        coefLbl.setText("Image scale coefficient");
        
        coefTextField = new JTextField();
        coefTextField.setText("1.0");
        
        jSeparator1 = new JSeparator();
        
        createBtn = new JButton();
        createBtn.setText("Create");
        createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameTextField.getText();
				String path = pathTextField.getText();
				float coef = Float.parseFloat(coefTextField.getText());
				if ((name.length() <= 0) || (path.length() <= 0) || (coef <= 0f)) {
					JOptionPane.showMessageDialog(CreateImageDialog.this, MsgConsts.FILL_ALL_FIELDS, DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					Texture temp = new Texture(Gdx.files.absolute(path));
					temp.dispose();
				}
				catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(CreateImageDialog.this, MsgConsts.NOT_A_TEXTURE, DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				handler.getImageData(name, path, coef);
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
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 247, Short.MAX_VALUE)
                        .addComponent(createBtn)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelBtn))
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(pathTextField)
                        .addGap(16, 16, 16)
                        .addComponent(chooseBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(nameLbl)
                            .addComponent(pathLbl))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(coefLbl)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(coefTextField)))
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
                .addComponent(pathLbl, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(pathTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(chooseBtn))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(coefTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(coefLbl))
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
	
	public interface ICreateImageHandler {
		public void getImageData(String name, String path, float scaleCoef);
	}
}
