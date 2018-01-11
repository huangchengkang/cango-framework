package com.cangoframework.task;

import java.util.EventListener;

public abstract interface TaskEventListener extends EventListener
{
  public abstract void taskStart(TaskEvent paramTaskEvent);

  public abstract void taskExit(TaskEvent paramTaskEvent);

  public abstract void targetStart(TaskEvent paramTaskEvent);

  public abstract void targetExit(TaskEvent paramTaskEvent);

  public abstract void unitStart(TaskEvent paramTaskEvent);

  public abstract void unitExit(TaskEvent paramTaskEvent);

  public abstract void targetAdded(TaskEvent paramTaskEvent);

  public abstract void targetRemoved(TaskEvent paramTaskEvent);

  public abstract void unitAdded(TaskEvent paramTaskEvent);

  public abstract void unitRemoved(TaskEvent paramTaskEvent);

  public abstract void routeAdded(TaskEvent paramTaskEvent);

  public abstract void routeRemoved(TaskEvent paramTaskEvent);
}