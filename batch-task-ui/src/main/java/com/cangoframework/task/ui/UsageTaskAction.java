package com.cangoframework.task.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class UsageTaskAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private String usageMessage;
	private JFrame parentWindow;
	JDialog usageDialog;

	public UsageTaskAction() {
		this.usageMessage = null;

		this.parentWindow = null;

		this.usageDialog = null;
	}

	public void actionPerformed(ActionEvent e) {
		getUsageDialog().setVisible(true);
	}

	private JDialog getUsageDialog() {
		if (this.usageDialog == null) {
			this.usageDialog = new JDialog(this.parentWindow);
			JRootPane p = this.usageDialog.getRootPane();
			p.setLayout(new BorderLayout());
			JTextPane tp = new JTextPane();
			tp.setEditable(false);
			tp.setContentType("text/html;");
			tp.setText(getUsageMessage());
			tp.select(0, 0);
			JScrollPane sp = new JScrollPane();
			sp.setViewportView(tp);
			p.add(sp, "Center");
			this.usageDialog.setSize(740, 480);
			this.usageDialog.setLocationRelativeTo(parentWindow);
		}
		return this.usageDialog;
	}

	private String getUsageMessage() {
		if (this.usageMessage == null) {
			StringBuffer sb = new StringBuffer();
			try {
				sb.append("<html><body bgcolor='#EFEFEF'>").append("<center><h2>WorkTask运行平台配置介绍</h2></center>").append("<h3>一、任务配置</h3>")
						.append(getHtmlContent(
								this.getClass().getResourceAsStream("/com/product/worktask/ui/taskinfo.xml")))
						.append("<hr><center><font size=3 color=green><b>&copy;Copyright 2007 WorkTask Technology Co., Ltd.</b></font></center><p>&nbsp;</body></html>");
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.usageMessage = sb.toString();
		}
		return this.usageMessage;
	}

	/**
	 * 文件以行为单位，转化为String
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static String getHtmlContent(InputStream in) throws Exception {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			if (in != null) {
				br = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\r\n");
				}
			}
		} finally {
			if (br != null)
				br.close();
		}
		return sb.toString().replace("<", "&lt;").replace(">", "&gt;")
				.replace(" ", "&nbsp;").replace("\r\n", "<br/>")
				.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
	}

	public final JFrame getParentWindow() {
		return this.parentWindow;
	}

	public final void setParentWindow(JFrame parentWindow) {
		this.parentWindow = parentWindow;
	}
}