package handler;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickServo;

import core.Action;
import core.Constants;
import core.Handler;


public class DriveHandler extends Handler{
	private short speed;
	
	private final static short SPEED_FACTOR = 900;
	
	public void serve(Action action, HttpServletRequest request, HttpServletResponse response) {
		// /Drive/<direction>/<speed>
		List<String> parameters = action.getParameters();
		if(parameters.size() != 2){
			sendTextResponse(HttpServletResponse.SC_BAD_REQUEST, "Wrong URL format, use /Drive/<direction>/<speed>", response);
			return;
		}
		int inputSpeed = Integer.valueOf(parameters.get(1));
		if(inputSpeed > 10){
			sendTextResponse(HttpServletResponse.SC_BAD_REQUEST, "Speed is to high. Maximum of 10 allowed", response);
			return;
		}
		this.speed = (short)(inputSpeed*SPEED_FACTOR);
		String direction = parameters.get(0); 
		Object resultString = null;
		try {
			Method method = this.getClass().getDeclaredMethod(direction);
			resultString = method.invoke(this);
		} catch (Exception e) {
			sendTextResponse(HttpServletResponse.SC_BAD_REQUEST, "Could not call method Drive."+direction, response);
			return;
		}
		sendTextResponse(HttpServletResponse.SC_OK, (String)resultString, response);
		return;
	}
	
	public String stop() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setPosition(Constants.servo0And1, (short)0);
        servoBrick.enable(Constants.servo0And1);
		return "Robot stopped";
	}
	
	public String forward() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setPosition(Constants.servo1, (short)(speed * -1));
		servoBrick.setPosition(Constants.servo0, (short)((speed+130) * -1));
        servoBrick.enable(Constants.servo0And1);
		return "Move forward with speed " + speed/SPEED_FACTOR;
	}
	
	public String backward() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setPosition(Constants.servo1, (short)(speed));
		servoBrick.setPosition(Constants.servo0, (short)(speed-190));
        servoBrick.enable(Constants.servo0And1);
		return "Move backward with speed " + speed/SPEED_FACTOR;
	}
	
	public String turnLeft() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setPosition(Constants.servo0, (short)(speed * -1));
		servoBrick.setPosition(Constants.servo1, speed);
		return "Turn left with speed " + speed/SPEED_FACTOR;
	}
	
	public String turnRight() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setPosition(Constants.servo1, (short)(speed * -1));
		servoBrick.setPosition(Constants.servo0, speed);
		return "Turn right with speed " + speed/SPEED_FACTOR;
	}
	
	public String backwardLeft() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setPosition(Constants.servo1, (short)(speed/1.6));
		servoBrick.setPosition(Constants.servo0, speed);
		return "Move backward left with speed " + speed/SPEED_FACTOR;
	}
	
	public String backwardRight() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setPosition(Constants.servo1, speed);
		servoBrick.setPosition(Constants.servo0, (short)(speed/1.6));
		return "Move backward right with speed " + speed/SPEED_FACTOR;
	}
	
	public String forwardLeft() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setPosition(Constants.servo1, (short)(speed * -1/1.6));
		servoBrick.setPosition(Constants.servo0, (short)(speed * -1));
		return "Move forward left with speed " + speed/SPEED_FACTOR;
	}
	
	public String forwardRight() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setPosition(Constants.servo1, (short)(speed * -1));
		servoBrick.setPosition(Constants.servo0, (short)(speed * -1/1.6));
		return "Move forward right with speed " + speed/SPEED_FACTOR;
	}
}
