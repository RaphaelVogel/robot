package handler;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickServo;

import core.Action;
import core.Constants;
import core.Handler;

public class CameraHandler extends Handler {
	
	private final static short CAMERA_MAX_POSITION = 9000;
	private final static short CAMERA_MIN_POSITION = -9000;
	private final static short DIRECTION_INCREMENT = 1500;
	
	private short currentLeftRightCameraPosition = 0;
	private short currentUpDownCameraPosition = 0;
	
	@Override
	public void serve(Action action, HttpServletRequest request, HttpServletResponse response) {
		// /Camera/<direction>
		List<String> parameters = action.getParameters();
		if(parameters.size() != 1){
			sendTextResponse(HttpServletResponse.SC_BAD_REQUEST, "Wrong URL format, use /Camera/<direction>", response);
			return;
		}
		String direction = parameters.get(0);
		Object resultString = null;
		try {
			Method method = this.getClass().getDeclaredMethod(direction);
			resultString = method.invoke(this);
		} catch (Exception e) {
			sendTextResponse(HttpServletResponse.SC_BAD_REQUEST, "Could not call method Camera."+direction, response);
			return;
		}
		sendTextResponse(HttpServletResponse.SC_OK, (String)resultString, response);
		return;
	}
	
	public String initCamera() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		servoBrick.setDegree(Constants.servo2, CAMERA_MIN_POSITION, CAMERA_MAX_POSITION);
		servoBrick.setDegree(Constants.servo3, CAMERA_MIN_POSITION, CAMERA_MAX_POSITION);
		center();
		return "Camera initialized";
	}
	
	public String left() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		if(currentLeftRightCameraPosition+DIRECTION_INCREMENT > CAMERA_MAX_POSITION){
			return "Camera cannot move further left";
		}
		currentLeftRightCameraPosition += DIRECTION_INCREMENT;
		servoBrick.setPosition(Constants.servo2, currentLeftRightCameraPosition);
        servoBrick.enable(Constants.servo2);
		return "Move camera left";
	}

	public String center() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		currentLeftRightCameraPosition = 0;
		currentUpDownCameraPosition = 0;
		servoBrick.setPosition(Constants.servo2And3, (short)0);
        servoBrick.enable(Constants.servo2And3);
		return "Center camera";
	}
	
	public String right() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		if(currentLeftRightCameraPosition-DIRECTION_INCREMENT < CAMERA_MIN_POSITION){
			return "Camera cannot move further right";
		}
		currentLeftRightCameraPosition -= DIRECTION_INCREMENT;
		servoBrick.setPosition(Constants.servo2, currentLeftRightCameraPosition);
        servoBrick.enable(Constants.servo2);
		return "Move camera right";
	}

	public String down() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		if(currentUpDownCameraPosition-DIRECTION_INCREMENT < CAMERA_MIN_POSITION){
			return "Camera cannot move further down";
		}
		currentUpDownCameraPosition -= DIRECTION_INCREMENT;
		servoBrick.setPosition(Constants.servo3, currentUpDownCameraPosition);
        servoBrick.enable(Constants.servo3);
		return "Move camera down";
	}
	
	public String up() throws Exception{
		BrickServo servoBrick = RoboStackHandler.getServoBrick();
		if(currentUpDownCameraPosition+DIRECTION_INCREMENT > CAMERA_MAX_POSITION){
			return "Camera cannot move further up";
		}
		currentUpDownCameraPosition += DIRECTION_INCREMENT;
		servoBrick.setPosition(Constants.servo3, currentUpDownCameraPosition);
        servoBrick.enable(Constants.servo3);
		return "Move camera up";
	}
}
