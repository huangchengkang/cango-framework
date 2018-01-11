package com.cangoframework.task;

import java.util.ArrayList;

public class ParallelUnit
{
  private ArrayList preUnits = new ArrayList();
  private ExecuteUnit unit;

  public ParallelUnit(ExecuteUnit unit)
  {
    this.unit = unit;
  }

  public ParallelUnit(ExecuteUnit unit, ArrayList preUints)
  {
    this.unit = unit;
    this.preUnits = preUints;
  }

  public final String getName()
  {
    return this.unit.getName();
  }

  public final ExecuteUnit getExecuteUnit()
  {
    return this.unit;
  }

  public final int getExecuteStatus()
  {
    return this.unit.getExecuteStatus();
  }

  public final boolean addPreviousUnit(String unit, String executeStatus)
  {
    int iStatus = -1;

    if (executeStatus.matches("[0-9]*"))
      iStatus = Integer.parseInt(executeStatus);
    else if (executeStatus.equalsIgnoreCase("UNEXECUTE"))
      iStatus = 0;
    else if (executeStatus.equalsIgnoreCase("SUCCESSFUL"))
      iStatus = 1;
    else if (executeStatus.equalsIgnoreCase("FAILED"))
      iStatus = 2;
    else if (executeStatus.equalsIgnoreCase("WARNING"))
      iStatus = 3;

    return addPreviousUnit(unit, iStatus);
  }

  public final boolean addPreviousUnit(String unit, int executeStatus)
  {
    PreviousUnit pUnit = new PreviousUnit(this, executeStatus, unit);
    boolean b = this.preUnits.contains(pUnit);
    if (!(b))
      this.preUnits.add(pUnit);

    return b;
  }

  public final PreviousUnit[] getPreviousUnits()
  {
    PreviousUnit[] r = new PreviousUnit[this.preUnits.size()];
    return ((PreviousUnit[])(PreviousUnit[])this.preUnits.toArray(r));
  }

  public class PreviousUnit {
    private int executeStatus = -1;
    private String name;
    private  ParallelUnit pu;

    public PreviousUnit(ParallelUnit pu, String paramString) {
      this.pu= pu;
      this.name = paramString;
    }

    public PreviousUnit(ParallelUnit pu, int paramInt, String paramString) {
      this.pu= pu;
      this.executeStatus = paramInt;
      this.name = paramString;
    }

    public final int getExecuteStatus()
    {
      return this.executeStatus;
    }

    public String getName() {
      return this.name;
    }
  }
}