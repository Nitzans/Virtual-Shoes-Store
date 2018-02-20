package main.java.bgu.spl.app;

import main.java.bgu.spl.mics.Broadcast;
import main.java.bgu.spl.mics.MicroService;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class TimeService extends MicroService{
	private final static Logger LOGGER = Logger.getGlobal();

	private final int speed;
	private final int duration;
	private Timer tick = new Timer();
	private int countTick = 1;
	private TimerTask task = new TimerTask() {

		public void run() {
			if (duration>=countTick){
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ TICK TOCK! |"+countTick+"| ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");		
				sendBroadcast(new TickBroadcast(countTick));
				countTick++;
			}
			else{
				tick.cancel();
				Broadcast end = new TerminateBroadcast("end");
				sendBroadcast(end);
				terminate();
			}
		}
	};

	public TimeService(int spd, int dur) {
		super("timer");
		speed = spd;
		duration = dur;
		countTick = 1;
	}

	public int getTick() {
		return countTick;
	}
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, end ->{
			LOGGER.info("\t"+getName()+" is terminating");
			terminate();
		});
		tick.schedule(task, speed, speed);
	}
}