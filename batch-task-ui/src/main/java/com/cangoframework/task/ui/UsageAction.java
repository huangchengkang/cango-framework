package com.cangoframework.task.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class UsageAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  private String usageMessage;
  private JFrame parentWindow;
  JDialog usageDialog;

  public UsageAction()
  {
    this.usageMessage = null;

    this.parentWindow = null;

    this.usageDialog = null;
  }

  public void actionPerformed(ActionEvent e) {
    getUsageDialog().setVisible(true);
  }

  private JDialog getUsageDialog()
  {
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

  private String getUsageMessage()
  {
    if (this.usageMessage == null) {
      StringBuffer sb = new StringBuffer();
      sb.append("<html><body bgcolor='#EFEFEF'>").append("<center><h2>WorkTask运行平台使用说明</h2></center>").append("<h3>一、程序概述</h3>").append("<font size=3><p>本程序是WorkTask运行平台(Work Task Product)基本运行平台。分为命令行运行模式和的图形界面运行模式两种形式。").append("命令行模式下通过名行参数指定的任务文件，自动按照任务中的路由定义运行任务，这种模式用于无人值守的环境下自动处理任务。").append("图形界面运行模式提供交互式的装载任务和运行任务两种功能。交互情况下，对任务的运行可以进行更多的控制，").append("可以交互式的加载不同的任务文件，并且可以反复运行WorkTask结构的三个层面的任务对象。程序界面分为3个主要部分：</p>").append("<ul>").append("<li>命令功能区：位于窗口的上部，包括菜单和按钮条，是执行程序命令的入口区域。本区域还包含执行进度条，用于表示程序的运行状态；</li>").append("<li>任务对象区：位于窗口的左侧，以树状形式显示当前装载的任务对象，第一层节点为当前的任务的对象(Task)，第二层为任务对象下的目标对象(Target),第三层为执行任务的最小执行对象单元(Unit)；</li>").append("<li>信息输出区：位于窗口的右侧侧，上半部分是当前选中的对象已经装载的属性，下版本部分是标准输出设备重定向的输出区。</li>").append("</ul></font>").append("<h3>二、图形界面使用</h3>").append("<font size=3><p>1、任务加载。有两种方式可以加载任务：").append("<ol>").append("<li>启动时自动加载。如果启动程序的环境中包含了任务定义文件，程序自动装载该任务；</li>").append("<li>手工加载。在命令功能区选择菜单“任务操作->装载任务”，或者点击按“装载任务”按钮,程序弹出选择任务文件对话框，确定后转载选择的任务文件。</li>").append("</ol>").append("2、运行任务对象。任务对象区域分层次显示三种类型的任务对象，交互界面下，可以任意的运行其中的一个。").append("所有对象任务的运行方法都是一样的，首先选择任务对象节点，然后在命令功能区选择菜单“任务操作->运行任务模块”，或者点击按“运行任务模块”按钮。").append("<ol>").append("<li>任务(Task)：选择任务对象树图的第一层节点。即选择了任务，此时运行的结果和命令完成运行效果一样。程序根据任务的并行参数设置自动运行每个目标；</li>").append("<li>目标(Target)：选择任务对象树图的第二层节点。即选择了目标，此时按照目标已经定义的路由运行其下面的各个单元，和自动运行情况下此目标的运行情况一样；</li>").append("<li>单元(Unit)：选择任务对象树图的第二层节点。即选择了运行单元，运行单元可以单独的运行，和任务定义中的路由没有关系，但用户必须自己保证能够按照合适的逻辑运行各个单元。注意只有任务定义中单元的属性allowManualExecute设置为true才可以单独运行。</li>").append("</ol>").append("3、其他功能。").append("<ol>").append("<li>选择帮助菜单项下的选项，可以显示本帮助信息和程序版本；").append("<li>选中每个任务对象，右侧的属性区会显示此任务对象的属性信息；").append("<li>系统控制台重定向到了窗口的右侧下半部分，输出到屏幕的信息会显示再此区域。").append("</ol></font>").append("<h3>三、关于程序启动</h3>").append("<font size=3><p>任务运行其的一般启动命令格式：“java com.product.worktask.TaskRunner task=task-define-file.xml gui=true/false”").append("<p>例如：C:>java com.product.worktask.TaskRunner task=etc/task.xml gui=false").append("<p>are参数缺失的时候，程序默认为etc/are.xml，task参数缺失，程序默认为从are获取taskFile属性来确定任务文件，gui参数缺省是false，即不运行图形界面。").append("<p>如果程序不能确定有效的任务文件，在命令行模式(gui=false)下自动退出，在图形界面(gui=true)下，启动图形界面等待手工加载任务。").append("<p>").append("</font><p><hr><center><font size=3 color=green><b>&copy;Copyright 2007 WorkTask Technology Co., Ltd.</b></font></center><p>&nbsp;</body></html>");
      this.usageMessage = sb.toString();
    }
    return this.usageMessage;
  }

  public final JFrame getParentWindow()
  {
    return this.parentWindow;
  }

  public final void setParentWindow(JFrame parentWindow)
  {
    this.parentWindow = parentWindow;
  }
}