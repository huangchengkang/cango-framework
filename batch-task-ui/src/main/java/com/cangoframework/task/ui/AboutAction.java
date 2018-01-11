package com.cangoframework.task.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AboutAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private JFrame parentWindow;
	private String message;

	public AboutAction() {
		this.parentWindow = null;
		this.message = null;
	}

	public void actionPerformed(ActionEvent e) {
		if (this.message == null) {
			this.message = "<html><h2>Work Task Product</h2><h3>Version 1.0,build 20170729</h3><hr><h3>&copy;Copyright 2017 Product Technology Co., Ltd.</h3>www.product.com</html>";
		}
		JOptionPane.showMessageDialog(this.parentWindow, this.message, "About Work Task", -1);
	}

	public final JFrame getParentWindow() {
		return this.parentWindow;
	}

	public final void setParentWindow(JFrame parentWindow) {
		this.parentWindow = parentWindow;
	}
}