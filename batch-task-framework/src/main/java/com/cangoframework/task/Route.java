package com.cangoframework.task;

public class Route
{
  private ExecuteUnit nextUnit;
  private int executeStatus = -1;

  public Route(int executeStatus, ExecuteUnit nextUnit)
  {
    this.nextUnit = nextUnit;
    this.executeStatus = executeStatus;
  }

  public Route(String executeStatus, ExecuteUnit nextUnit)
  {
    if (executeStatus.matches("[0-9]*"))
      this.executeStatus = Integer.parseInt(executeStatus);
    else if (executeStatus.equalsIgnoreCase("UNEXECUTE"))
      this.executeStatus = 0;
    else if (executeStatus.equalsIgnoreCase("SUCCESSFUL"))
      this.executeStatus = 1;
    else if (executeStatus.equalsIgnoreCase("FAILED"))
      this.executeStatus = 2;
    else if (executeStatus.equalsIgnoreCase("WARNING"))
      this.executeStatus = 3;

    this.nextUnit = nextUnit;
  }

  public final int executeStatus()
  {
    return this.executeStatus;
  }

  public final ExecuteUnit nextUnit()
  {
    return this.nextUnit;
  }
}