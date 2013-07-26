package com.steelkiwi.patheditor.utils;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;

public class SwingHelper {

	public static void setDialogWindowToCenter(JDialog dialog){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setBounds((screenSize.width-dialog.getWidth())/2, (screenSize.height-dialog.getHeight())/2, dialog.getWidth(), dialog.getHeight());
	}
	
}