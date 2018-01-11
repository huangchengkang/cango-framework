package com.cangoframework.task;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParallelTable
{
  private Map units = new LinkedHashMap();
  private Target target = null;

  public ParallelTable(Target target)
  {
    this.target = target;
  }

  public Target getTarget() {
    return this.target;
  }

  public boolean isAllUnitRuning()
  {
    boolean isOver = true;
    ParallelUnit[] units = getUnits();
    for (int i = 0; i < units.length; ++i)
      if (units[i].getExecuteStatus() == -1)
        return false;


    return isOver;
  }

  public String getNotRunUnit()
  {
    boolean isOver = true;
    StringBuffer sbf = new StringBuffer("���е�Ԫû�����У�\n");
    ParallelUnit[] pUnits = getUnits();
    for (int i = 0; i < pUnits.length; ++i)
      if (pUnits[i].getExecuteStatus() == -1) {
        isOver = false;
        sbf.append("  unit=[" + pUnits[i].getName() + "]");
        sbf.append("\n");
      }


    if (isOver)
      return "���е�Ԫ�������гɹ���";

    return sbf.toString();
  }

  public ExecuteUnit getExecuteUnit()
  {
    ParallelUnit[] aunits = getUnits();
    for (int i = 0; i < aunits.length; ++i) {
      ExecuteUnit unit = this.target.getUnit(aunits[i].getName());
      ParallelUnit.PreviousUnit[] preUnits = aunits[i].getPreviousUnits();
      if ((preUnits.length < 1) && (unit.getExecuteStatus() == -1))
        return unit;

      boolean canRun = true;
      for (int j = 0; j < preUnits.length; ++j)
      {
        ExecuteUnit preUnit = this.target.getUnit(preUnits[j].getName());
        if (preUnit.getExecuteStatus() != preUnits[j].getExecuteStatus())
          canRun = false;
      }

      if ((canRun) && (unit.getExecuteStatus() == -1)) {
        return unit;
      }

    }

    return null;
  }

  public final ParallelUnit[] getUnits()
  {
    ParallelUnit[] r = new ParallelUnit[this.units.size()];
    return ((ParallelUnit[])(ParallelUnit[])this.units.values().toArray(r));
  }

  public final boolean addUnit(ExecuteUnit unit)
  {
    boolean b = this.units.containsKey(unit.getName());
    if (!(b))
      this.units.put(unit.getName(), new ParallelUnit(unit));

    return b;
  }

  public final boolean addPreviousUnit(ExecuteUnit unit, ExecuteUnit preUnit, String executeStatus)
  {
    ParallelUnit pUnit = null;
    boolean b = this.units.containsKey(unit.getName());
    if (!(b)) {
      this.units.put(unit.getName(), new ParallelUnit(unit));
    }
    else
      pUnit = (ParallelUnit)this.units.get(unit.getName());

    pUnit.addPreviousUnit(preUnit.getName(), executeStatus);
    return b;
  }

  public String toString()
  {
    StringBuffer sbf = new StringBuffer("<parallelTable ");
    sbf.append("target=");
    sbf.append(this.target.getName());
    sbf.append(">");
    sbf.append("\n");

    ParallelUnit[] pUnits = getUnits();
    for (int i = 0; i < pUnits.length; ++i) {
      sbf.append("    ");
      sbf.append("<unit name=");
      sbf.append(pUnits[i].getName());
      sbf.append(">");
      sbf.append("\n");

      ParallelUnit.PreviousUnit[] preUnits = pUnits[i].getPreviousUnits();
      if (preUnits != null)
        for (int j = 0; j < preUnits.length; ++j) {
          sbf.append("    ");
          sbf.append("    ");
          sbf.append("<preUnit unit=");
          sbf.append(preUnits[j].getName());
          sbf.append(" executeStatus=");
          sbf.append(preUnits[j].getExecuteStatus());
          sbf.append(">");
          sbf.append("\n");
        }

      sbf.append("    ");
      sbf.append("</unit>");
      sbf.append("\n");
    }

    return sbf.toString();
  }
}