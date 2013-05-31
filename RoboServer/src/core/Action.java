package core;
import java.util.ArrayList;
import java.util.List;

public class Action {

	private String handler;
	private List<String> parameters = new ArrayList<String>();
	
	public static Action create(String path){
		Action action = new Action();
		String[] split = path.substring(1).split("/");
		action.setHandler(split[0]);
		for(int i=1; i<split.length; i++){
			action.addParameter(split[i]);
		}
		return action;
	}
	
	public String getHandler() {
		return handler;
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	private void addParameter(String parameter){
		parameters.add(parameter);
	}
	
	private void setHandler(String handler){
		this.handler = handler;
	}

}
