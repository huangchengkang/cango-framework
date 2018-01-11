package com.cangoframework.task.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cangoframework.task.ExecuteUnit;
import com.cangoframework.task.Target;
import com.cangoframework.task.Task;
import com.cangoframework.task.TaskObject;
import com.cangoframework.task.util.Property;

public class TaskWindow extends JFrame {
	private static Logger logger = LoggerFactory.getLogger(TaskWindow.class);
	private static final long serialVersionUID = 1L;
	private Task task = null;
	private JMenuBar mbMain = null;
	private JMenu mTaskAction = null;
	private JMenu mHelp = null;
	private JMenuItem miLoadTask = null;
	private JMenuItem miRunTaskObject = null;
	private JPanel jContentPane = null;
	private JTextArea console = null;
	private JScrollPane consolePane = null;
	private JScrollPane taskPane = null;
	private JTree taskTree = null;
	private JToolBar toolBar = null;
	private JButton btnLoadTask = null;
	private RunTaskObjectAction runTaskObjectAction = null;
	private LoadTaskAction loadTaskAction = null;
	private JButton btnRunTaskObject = null;
	private AboutAction aboutAction = null;
	private UsageAction usageAction = null;
	private UsageTaskAction usageTaskAction = null;
	private JSplitPane centerPane = null;
	private JScrollPane taskPropertyPane = null;
	private JTable taskPropertyTable = null;
	private TaskPropertyTableModel taskPropertyTableModel = null;
	private JMenuItem miAbout = null;
	private JMenuItem miUsage = null;
	private JMenuItem miUsageTask = null;
	private JProgressBar taskProgress = null;

	public TaskWindow() {
		initialize();
		// 给控制台添加弹出菜单
		addConsolePopMenu();
		// 给Tree增加弹出菜单
		addTreePopMenu();
	}

	private void initialize() {
		setDefaultCloseOperation(3);
		setContentPane(getJContentPane());
		setJMenuBar(getMbMain());
		setSize(new Dimension(900, 600));
		this.taskProgress.setIndeterminate(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(TaskWindow.class.getResource("/images/icon.png")));
	}

	public JMenuBar getMbMain() {
		if (this.mbMain == null) {
			this.mbMain = new JMenuBar();
			this.mbMain.setPreferredSize(new Dimension(273, 21));
			this.mbMain.add(getMTaskAction());
			this.mbMain.add(getMHelp());
		}
		return this.mbMain;
	}

	public JMenu getMTaskAction() {
		if (this.mTaskAction == null) {
			this.mTaskAction = new JMenu();
			this.mTaskAction.setText("功能菜单");
			this.mTaskAction.add(getMiLoadTask());
			this.mTaskAction.add(getMiRunTaskObject());
//			this.mTaskAction.add(new JSeparator());// 分割线
//			this.mTaskAction.add(setTreeRowHigth());
		}
		return this.mTaskAction;
	}

	public JMenu getMHelp() {
		if (this.mHelp == null) {
			this.mHelp = new JMenu();
			this.mHelp.setText("使用帮助");
			this.mHelp.add(getMiUsage());
			this.mHelp.add(getMiUsageTask());
			this.mHelp.add(getMiAbout());
		}
		return this.mHelp;
	}

	public JMenuItem getMiLoadTask() {
		if (this.miLoadTask == null) {
			this.miLoadTask = new JMenuItem();
			this.miLoadTask.setAction(getLoadTaskAction());
			this.miLoadTask.setText("加载任务");
			this.miLoadTask.setActionCommand("LoadTask");
		}
		return this.miLoadTask;
	}

	private JMenuItem getPopMiLoadTask() {
		JMenuItem popMiLoadTask = new JMenuItem();
		popMiLoadTask.setAction(getLoadTaskAction());
		popMiLoadTask.setText("加载任务");
		popMiLoadTask.setActionCommand("LoadTask");
		return popMiLoadTask;
	}

	public JMenuItem getMiRunTaskObject() {
		if (this.miRunTaskObject == null) {
			this.miRunTaskObject = new JMenuItem();
			this.miRunTaskObject.setAction(getRunTaskObjectAction());
			this.miRunTaskObject.setText("执行所选任务模块");
			this.miRunTaskObject.setActionCommand("RunTaskObject");
		}
		return this.miRunTaskObject;
	}

	public JPanel getJContentPane() {
		if (this.jContentPane == null) {
			this.jContentPane = new JPanel();
			this.jContentPane.setLayout(new BorderLayout());
			/*
			 * 原先的代码 this.jContentPane.add(getCenterPane(), "Center");
			 * this.jContentPane.add(getToolBar(), "North");
			 * this.jContentPane.add(getTaskPane(), "West");
			 */
			{/* 我改写的代码 at 2016/09/21 by kancy */
				JPanel panel = new JPanel();
				JSplitPane sp = new JSplitPane();
				sp.setOneTouchExpandable(true);
				panel.setLayout(new BorderLayout());
				panel.add(sp, "Center");
				sp.setLeftComponent(getTaskPane());
				sp.setRightComponent(getCenterPane());
				this.jContentPane.add(panel, "Center");
				this.jContentPane.add(getToolBar(), "North");
			}
		}
		return this.jContentPane;
	}

	public JTextArea getConsole() {
		if (this.console == null) {
			this.console = new JTextArea();
			this.console.setEditable(false);
			PrintStream consoleStream = new PrintStream(new OutputStream() {

				@Override
				public void write(int b) throws IOException {

				}

				public void write(byte[] b, int off, int off2) {
					console.append(new String(b, off, off2));

					int pos = 0;
					try {
						pos = console.getLineStartOffset(console.getLineCount() - 1);
					} catch (BadLocationException e) {
						pos = 0;
					}
					console.select(pos, pos);
				}

			});
			System.setErr(consoleStream);
			System.setOut(consoleStream);
			System.out.println("Welcome to use work task product...\n");
		}
		return this.console;
	}

	public JScrollPane getConsolePane() {
		if (this.consolePane == null) {
			this.consolePane = new JScrollPane();
			this.consolePane.setViewportView(getConsole());
		}
		return this.consolePane;
	}

	public JScrollPane getTaskPane() {
		if (this.taskPane == null) {
			this.taskPane = new JScrollPane();
			this.taskPane.setPreferredSize(new Dimension(200, 0));
			this.taskPane.setViewportView(getTaskTree());
			this.taskPane.setWheelScrollingEnabled(true);
		}
		return this.taskPane;
	}

	public JTree getTaskTree() {
		if (this.taskTree == null) {
			this.taskTree = new JTree(new DefaultMutableTreeNode("Task load failed"));
			this.taskTree.addTreeSelectionListener(new TreeSelectionListener() {

				public void valueChanged(TreeSelectionEvent e) {
					TreePath p = e.getNewLeadSelectionPath();
					taskObjectSelectChanged(p);
				}
			});
		}
		this.taskTree.setRowHeight(23);
		this.taskTree.setFont(new Font("宋体", 0, 14));
		return this.taskTree;
	}

	public void taskObjectSelectChanged(TreePath p) {
		String tName = null;
		String uName = null;
		ExecuteUnit selectedUnit = null;
		Target selectedTarget = null;
		if (p != null) {
			this.runTaskObjectAction.setEnabled(true);
			Object[] objs = ((DefaultMutableTreeNode) p.getLastPathComponent()).getUserObjectPath();
			if (objs.length == 3) {
				tName = ((Property) objs[1]).getKey().toString();
				uName = ((Property) objs[2]).getKey().toString();
				selectedTarget = this.task.getTarget(tName);
				selectedUnit = selectedTarget.getUnit(uName);
				this.runTaskObjectAction.setTaskObject(selectedUnit);
				this.taskPropertyTableModel.setTaskObject(selectedUnit);
				this.runTaskObjectAction.setEnabled(selectedUnit.isAllowManualExecute());
			} else if (objs.length == 2) {
				tName = ((Property) objs[1]).getKey().toString();
				selectedTarget = this.task.getTarget(tName);
				this.runTaskObjectAction.setTaskObject(selectedTarget);
				this.taskPropertyTableModel.setTaskObject(selectedTarget);
			} else if (objs.length == 1) {
				this.runTaskObjectAction.setTaskObject(this.task);
				this.taskPropertyTableModel.setTaskObject(this.task);
			}
		}
	}

	public Task getTask() {
		return this.task;
	}

	public void setTask(Task task) {
		clearTask();
		if (task == null)
			return;
		this.task = task;
		setTitle("Work Task Product 1.0 - " + task.getDescribe());

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.taskTree.getModel().getRoot();
		root.setUserObject(getNodeData(task));
		Target[] t = task.getTargets();
		TreePath openPath = null;
		for (int i = 0; i < t.length; ++i) {
			DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(getNodeData(t[i]));
			root.add(tNode);
			if (i == 0)
				openPath = new TreePath(new DefaultMutableTreeNode[] { root, tNode });

			ExecuteUnit[] u = t[i].getUnits();
			for (int j = 0; j < u.length; ++j)
				if (u[j].getProperty("displayInGUI", true))
					tNode.add(new DefaultMutableTreeNode(getNodeData(u[j])));

		}

		if (openPath != null)
			this.taskTree.expandPath(openPath);
		try {
			this.taskTree.updateUI();
		} catch (Exception ex) {
		}
	}

	private void clearTask() {
		this.task = null;
		setTitle("Work Task Product 1.0 - Task load failed");

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.taskTree.getModel().getRoot();
		root.removeAllChildren();
		this.runTaskObjectAction.setEnabled(false);
		this.runTaskObjectAction.setTaskObject(null);
	}

	private Property getNodeData(TaskObject o) {
		Property p = new Property(o.getName(), o.getDescribe());
		p.setDisplayFormat((byte) 1);
		return p;
	}

	public JToolBar getToolBar() {
		if (this.toolBar == null) {
			this.toolBar = new JToolBar();
			this.toolBar.setBorder(LineBorder.createGrayLineBorder());
			this.toolBar.setPreferredSize(new Dimension(234, 30));
			this.toolBar.add(getBtnLoadTask());
			this.toolBar.add(getBtnRunTaskObject());
			this.toolBar.addSeparator();
			this.toolBar.addSeparator(new Dimension(10, 10));
			this.toolBar.add(getTaskProgress());
			this.toolBar.addSeparator(new Dimension(10, 10));
		}
		return this.toolBar;
	}

	public JButton getBtnLoadTask() {
		if (this.btnLoadTask == null) {
			this.btnLoadTask = new JButton();
			this.btnLoadTask.setAction(getLoadTaskAction());
			this.btnLoadTask.setText("装载任务");
			btnLoadTask.setIcon(new ImageIcon(TaskWindow.class.getResource("/images/open.png")));
		}
		return this.btnLoadTask;
	}

	public JButton getBtnRunTaskObject() {
		if (this.btnRunTaskObject == null) {
			this.btnRunTaskObject = new JButton();
			this.btnRunTaskObject.setAction(getRunTaskObjectAction());
			this.btnRunTaskObject.setText("运行所选任务模块");
			this.btnRunTaskObject.setIcon(new ImageIcon(TaskWindow.class.getResource("/images/run.png")));
		}
		return this.btnRunTaskObject;
	}

	public JSplitPane getCenterPane() {
		if (this.centerPane == null) {
			this.centerPane = new JSplitPane();
			this.centerPane.setOrientation(0);
			this.centerPane.setDividerSize(6);
			this.centerPane.setDividerLocation(120);
			this.centerPane.setContinuousLayout(true);
			this.centerPane.setTopComponent(getTaskPropertyPane());
			this.centerPane.setBottomComponent(getConsolePane());
			this.centerPane.setOneTouchExpandable(true);
		} 
		return this.centerPane;
	}

	public JScrollPane getTaskPropertyPane() {
		if (this.taskPropertyPane == null) {
			this.taskPropertyPane = new JScrollPane();
			this.taskPropertyPane.setViewportView(getTaskPropertyTable());
		}
		return this.taskPropertyPane;
	}

	public JTable getTaskPropertyTable() {
		if (this.taskPropertyTable == null) {
			this.taskPropertyTable = new JTable();
			this.taskPropertyTable.setModel(getTaskPropertyTableModel());
			TableColumnModel clm = this.taskPropertyTable.getColumnModel();
			TableColumn cl = clm.getColumn(0);
			cl.setHeaderValue("属性名称");
			clm.getColumn(1).setHeaderValue("属性值");
		}
		return this.taskPropertyTable;
	}

	public TaskPropertyTableModel getTaskPropertyTableModel() {
		if (this.taskPropertyTableModel == null)
			this.taskPropertyTableModel = new TaskPropertyTableModel();

		return this.taskPropertyTableModel;
	}

	public JMenuItem getMiAbout() {
		if (this.miAbout == null) {
			this.miAbout = new JMenuItem();
			this.miAbout.setAction(getAboutAction());
			this.miAbout.setText("关于me");
		}
		return this.miAbout;
	}

	public JMenuItem getMiUsage() {
		if (this.miUsage == null) {
			this.miUsage = new JMenuItem();
			this.miUsage.setAction(getUsageAction());
			this.miUsage.setText("使用方法");
		}
		return this.miUsage;
	}
	public JMenuItem getMiUsageTask() {
		if (this.miUsageTask == null) {
			this.miUsageTask = new JMenuItem();
			this.miUsageTask.setAction(getUsageTaskAction());
			this.miUsageTask.setText("配置介绍");
		}
		return this.miUsageTask;
	}

	public JProgressBar getTaskProgress() {
		if (this.taskProgress == null) {
			this.taskProgress = new JProgressBar();
			this.taskProgress.setSize(new Dimension(200, 14));
		}
		return this.taskProgress;
	}

	/**
	 * 关于-事件
	 * 
	 * @return
	 */
	public AboutAction getAboutAction() {
		if (this.aboutAction == null) {
			this.aboutAction = new AboutAction();
			this.aboutAction.setParentWindow(this);
		}
		return this.aboutAction;
	}

	/**
	 * 使用说明-事件
	 * 
	 * @return
	 */
	public UsageAction getUsageAction() {
		if (this.usageAction == null) {
			this.usageAction = new UsageAction();
			this.usageAction.setParentWindow(this);
		}
		return this.usageAction;
	}
	
	/**
	 * 使用说明-事件
	 * 
	 * @return
	 */
	public UsageTaskAction getUsageTaskAction() {
		if (this.usageTaskAction == null) {
			this.usageTaskAction = new UsageTaskAction();
			this.usageTaskAction.setParentWindow(this);
		}
		return this.usageTaskAction;
	}

	/**
	 * 加载Task 文件 - 事件
	 * 
	 * @return
	 */
	public LoadTaskAction getLoadTaskAction() {
		if (this.loadTaskAction == null) {
			this.loadTaskAction = new LoadTaskAction(this);
			this.loadTaskAction.setProgressBar(getTaskProgress());
		}
		return this.loadTaskAction;
	}

	/**
	 * 运行Task - 事件
	 * 
	 * @return
	 */
	public RunTaskObjectAction getRunTaskObjectAction() {
		if (this.runTaskObjectAction == null) {
			this.runTaskObjectAction = new RunTaskObjectAction();
			this.runTaskObjectAction.setProgressBar(getTaskProgress());
		}
		return this.runTaskObjectAction;
	}

	private void addConsolePopMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(console, popupMenu);

		JMenuItem pop_clear = new JMenuItem("清空控制台");
		pop_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				console.setText(null);
			}
		});
		popupMenu.add(pop_clear);

		JSeparator separator = new JSeparator();
		popupMenu.add(separator);

		JMenuItem pop_copy = new JMenuItem("\u590D\u5236");
		pop_copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				console.copy();
			}
		});
		popupMenu.add(pop_copy);

		JMenuItem pop_zt = new JMenuItem("复制全部");
		pop_zt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				console.selectAll();
				console.copy();
			}
		});
		popupMenu.add(pop_zt);

		JMenuItem pol_alls = new JMenuItem("\u5168\u9009");
		pol_alls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				console.selectAll();
			}
		});
		popupMenu.add(pol_alls);
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	/**
	 * Tree添加菜单
	 */
	private void addTreePopMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(taskTree, popupMenu);
		popupMenu.add(getPopMiLoadTask());
	}

}