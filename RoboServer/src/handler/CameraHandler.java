package handler;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickServo;

import core.Action;
import core.Connection;
import core.Handler;

public class CameraHandler extends Handler {
	
	private short servo2 = (short)2; // camera left-right
	private short servo3 = (short)3; // camera up-down
	
	private BrickServo servoBrick = Connection.getInstance().getServoBrick();
	private final short CAMERA_SPEED = 5000;

	@Override
	public void serve(Action action, HttpServletRequest request, HttpServletResponse response) {
		// /Camera/<direction>
		List<String> parameters = action.getParameters();
		if(parameters.size() != 1){
			sendResponse(HttpServletResponse.SC_BAD_REQUEST, "Wrong URL format, use /Camera/<direction>", response);
			return;
		}
		String direction = parameters.get(0);
		Object resultString = null;
		try {
			Method method = this.getClass().getDeclaredMethod(direction);
			resultString = method.invoke(this);
		} catch (Exception e) {
			sendResponse(HttpServletResponse.SC_BAD_REQUEST, "Could not call method Camera."+direction, response);
			return;
		}
		sendResponse(HttpServletResponse.SC_OK, (String)resultString, response);
		return;
	}
	
	@SuppressWarnings("unused")
	private String left(){
		servoBrick.setPosition(servo2, CAMERA_SPEED);
        servoBrick.enable(servo2);
		return "Move camera left";
	}

	@SuppressWarnings("unused")
	private String right(){
		servoBrick.setPosition(servo2, (short)(CAMERA_SPEED * (-1)));
        servoBrick.enable(servo2);
		return "Move camera right";
	}

	@SuppressWarnings("unused")
	private String up(){
		servoBrick.setPosition(servo3, CAMERA_SPEED);
        servoBrick.enable(servo2);
		return "Move camera up";
	}
	
	@SuppressWarnings("unused")
	private String down(){
		servoBrick.setPosition(servo3, (short)(CAMERA_SPEED * (-1)));
        servoBrick.enable(servo2);
		return "Move camera down";
	}
}
