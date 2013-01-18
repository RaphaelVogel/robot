package core;
import java.util.ArrayList;
import java.util.List;

public class Action {

	private String verb;
	private List<String> parameters = new ArrayList<String>();
	
	public static Action create(String path){
		Action action = new Action();
		String[] split = path.substring(1).split("/");
		action.setVerb(split[0]);
		for(int i=1; i<split.length; i++){
			action.addParameter(split[i]);
		}
		return action;
	}
	
	public String getVerb() {
		return verb;
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	private void addParameter(String parameter){
		parameters.add(parameter);
	}
	
	private void setVerb(String verb){
		this.verb = verb;
	}

}
