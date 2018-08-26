package edu.test.wac.client;

public abstract class VisibleTimer {
	/* 
	private boolean running;
	  private boolean cancel;
	  private AnimationCallback callback = new AnimationCallback() {
	    public void execute(double v) {
	      if(!VisibleTimer.this.cancel) {
	        if(VisibleTimer.this.startTime < 0.0D) {
	          VisibleTimer.this.startTime = v;
	        } else if(v - VisibleTimer.this.startTime >= (double)VisibleTimer.this.delayMillis) {
	          VisibleTimer.this.run();
	          if(!VisibleTimer.this.repeated) {
	            VisibleTimer.this.running = false;
	            return;
	          }

	          VisibleTimer.this.startTime = v;
	        }

	        AnimationScheduler.get().requestAnimationFrame(VisibleTimer.this.callback);
	      }
	    }
	  };
	  private double startTime = -1.0D;
	  private boolean repeated = false;
	  private int delayMillis;

	  public VisibleTimer() {
	  }

	  public void cancel() {
	    this.cancel = true;
	  }

	  public abstract void run();

	  private void start(int delayMillis, boolean repeated) {
	    if(delayMillis < 0) {
	      throw new IllegalArgumentException("must be non-negative");
	    } else {
	      if(this.isRunning()) {
	        this.cancel();
	      }

	      this.cancel = false;
	      this.startTime = -1.0D;
	      this.running = true;
	      this.delayMillis = delayMillis;
	      this.repeated = repeated;
	      AnimationScheduler.get().requestAnimationFrame(this.callback);
	    }
	  }

	  public void schedule(int delayMillis) {
	    this.start(delayMillis, false);
	  }

	  public void scheduleRepeating(int periodMillis) {
	    this.start(periodMillis, true);
	  }

	  public boolean isRunning() {
	    return this.running;
	  }
	  */
	}