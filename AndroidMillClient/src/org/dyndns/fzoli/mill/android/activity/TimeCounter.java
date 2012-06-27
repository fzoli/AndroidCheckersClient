package org.dyndns.fzoli.mill.android.activity;

import java.util.Timer;
import java.util.TimerTask;

public class TimeCounter {

	private int start;
	private int from;
	private Timer timer;
	private TimerTask task;
	
	private final TimeCounterTask COUNTER;
	
	public interface TimeCounterTask {
		
		void hit(int hour, int minute, int second, int time);
		
	}
	
	public int getStart() {
		return start;
	}
	
	public TimeCounter(TimeCounterTask counter) {
		if (counter == null) throw new NullPointerException();
		COUNTER = counter;
	}
	
	private void prepare() {
		stop();
		timer = new Timer();
	}
	
	private void schedule() {
		if (from < 0) from = 0;
		start = from;
		final boolean inverse = from != 0;
		task = new TimerTask() {
			
			@Override
			public void run() {
				int hour = (int)(from / 3600.0);
				int minute = (int)(from / 60.0) - hour * 60;
				int second = from - minute * 60 - hour * 3600;
				COUNTER.hit(hour, minute, second, from);
				if (inverse) {
					from--;
					if (from < 0) cancel();
				}
				else {
					from++;
				}
			}
			
		};
		timer.schedule(task, 0, 1000);
	}
	
	private void cancel() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	public void start() {
		prepare();
		schedule();
	}
	
	public void start(int from) {
		prepare();
		this.from = from;
		schedule();
	}
	
	public void pause() {
		cancel();
	}
	
	public void resume() {
		cancel();
		timer = new Timer();
		schedule();
	}
	
	public void stop() {
		from = start = 0;
		cancel();
	}
}