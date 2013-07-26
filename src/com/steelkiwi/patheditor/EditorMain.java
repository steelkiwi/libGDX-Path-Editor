package com.steelkiwi.patheditor;

import java.awt.EventQueue;

import com.steelkiwi.patheditor.gui.EditorRootPane;

public class EditorMain {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new EditorRootPane().setVisible(true);
			}
		});
	}
}