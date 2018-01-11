package com.cangoframework.task.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.cangoframework.task.ExecuteUnit;
import com.cangoframework.task.Target;
import com.cangoframework.task.Task;
import com.cangoframework.task.TaskObject;
import com.cangoframework.task.util.Property;

public class TaskPropertyTableModel extends AbstractTableModel
{
  private static final long serialVersionUID = 1L;
  private TaskObject taskObject;
  private ArrayList properties;

  public TaskPropertyTableModel()
  {
    this.taskObject = null;

    this.properties = new ArrayList(); }

  public int getRowCount() {
    return this.properties.size();
  }

  public int getColumnCount() {
    return 2;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Property p = (Property)this.properties.get(rowIndex);
    return ((columnIndex == 0) ? p.getKey() : p.getValue());
  }

  public TaskObject getTaskObject()
  {
    return this.taskObject;
  }

  public void setTaskObject(TaskObject taskObject)
  {
    this.taskObject = taskObject;
    refreshPropeties();
    fireTableDataChanged();
  }

  private void refreshPropeties() {
    this.properties.clear();
    if (this.taskObject == null) return;
    this.properties.add(new Property("name", this.taskObject.getName()));
    this.properties.add(new Property("describe", this.taskObject.getDescribe()));
    if (this.taskObject instanceof Task) {
      Task o = (Task)this.taskObject;
      this.properties.add(new Property("parallelRun", String.valueOf(o.isParallelRun())));
      this.properties.add(new Property("traceOn", String.valueOf(o.isTraceOn())));
      this.properties.add(new Property("targetCount", String.valueOf(o.getTargetCount())));
    } else if (this.taskObject instanceof Target) {
      Target o = (Target)this.taskObject;
      this.properties.add(new Property("enabled", String.valueOf(o.isEnabled())));
      this.properties.add(new Property("unitCount", String.valueOf(o.getUnitCount())));
      this.properties.add(new Property("currentUnit", o.getCurrentUnit()));
    } else {
      ExecuteUnit o = (ExecuteUnit)this.taskObject;
      this.properties.add(new Property("allowManualExecute", String.valueOf(o.isAllowManualExecute())));
      this.properties.add(new Property("routCount", String.valueOf(o.getRouteCount())));
    }
    String[] p = this.taskObject.getProperties();
    for (int i = 0; i < p.length; ++i)
      this.properties.add(new Property(p[i], this.taskObject.getProperty(p[i])));
  }
}