package com.cangoframework.task;

import java.util.EventObject;

public class TaskEvent extends EventObject
{
  private static final long serialVersionUID = 1L;
  public static final int TASK_START = 0;
  public static final int TASK_EXIT = 1;
  public static final int TARGET_START = 2;
  public static final int TARGET_EXIT = 3;
  public static final int UNIT_START = 4;
  public static final int UNIT_EXIT = 5;
  public static final int TARGET_ADDED = 6;
  public static final int TARGET_REMOVED = 7;
  public static final int UNIT_ADDED = 8;
  public static final int UNIT_REMOVED = 9;
  public static final int ROUTE_ADDED = 10;
  public static final int ROUTE_REMOVED = 11;
  private int type;
  private Target target;
  private ExecuteUnit unit;
  private Route route;

  public TaskEvent(Task source, int type, Target target, ExecuteUnit unit, Route route)
  {
    super(source);

    this.type = 0;
    this.target = null;
    this.unit = null;
    this.route = null;

    this.type = type;
    this.target = target;
    this.unit = unit;
    this.route = route;
  }

  public TaskEvent(Task source, int type)
  {
    this(source, type, null, null, null);
  }

  public TaskEvent(Task source, int type, Target target)
  {
    this(source, type, target, null, null);
  }

  public TaskEvent(Task source, int type, Target target, ExecuteUnit unit)
  {
    this(source, type, target, unit, null);
  }

  public int getType()
  {
    return this.type;
  }

  public final Route getRoute()
  {
    return this.route;
  }

  public final Target getTarget()
  {
    return this.target;
  }

  public final Task getTask()
  {
    return ((Task)getSource());
  }

  public final ExecuteUnit getUnit()
  {
    return this.unit;
  }
}