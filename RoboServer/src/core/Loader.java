package core;

import handler.ChargerStackHandler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tinkerforge.BrickletDualRelay;

public class Loader extends Thread {

	private Logger logger = Logger.getLogger(core.Loader.class.getName());
	private boolean startLoading = true;
	private Timer timer = new Timer();
	private long chargeTime = 1000 * 60 * 90;
	private int count = 0;
	
	public void stopLoading(){
		startLoading = false;
	}
	
	@Override
	public void run(){
		BrickletDualRelay relais = ChargerStackHandler.getDualRelaisBricklet();
		while(count < 2){
			count++;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					stopLoading();
				}
			}, chargeTime);
		
			// charger loop
			while(startLoading){
				try {
					relais.setMonoflop((short)1, true, 4000);
					sleep(3000);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Could not load. Relais monoflop failed");
				}
			}
			startLoading = true;
			try {
				sleep(3000);
			} catch (InterruptedException e) {
			}
		}
	}
}
