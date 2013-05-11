package handler;

import java.util.logging.Logger;
import com.tinkerforge.BrickServo;
import core.Constants;

public class CollisionHandler extends Thread{

	private Logger logger = Logger.getLogger(CollisionHandler.class.getName());
	private boolean shouldRun = true;
	
	public void stopCollisionDetection() throws Exception{
		shouldRun = false;
	}
	
	public void run(){
		BrickServo servoBrick = StackHandler.getServoBrick();
		while(shouldRun){
			try {
				servoBrick.setPosition(Constants.servo4, (short)6500);
				servoBrick.enable(Constants.servo4);
				Thread.sleep(400);
				servoBrick.setPosition(Constants.servo4, (short)-6500);
				servoBrick.enable(Constants.servo4);
				Thread.sleep(400);
			} catch (Exception e) {
				logger.severe("CollisionDetection start failed");
			}
		}
	}

}
