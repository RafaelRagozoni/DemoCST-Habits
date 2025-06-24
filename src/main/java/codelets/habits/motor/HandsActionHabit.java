package codelets.habits.motor;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Creature;

public class HandsActionHabit implements Habit {
    private String previousHandsAction="";
    private Creature c;
    static Logger log = Logger.getLogger(HandsActionHabit.class.getCanonicalName());

    public HandsActionHabit(Creature nc) {
        this.c = nc;
    }

    @Override 
    public Idea exec(Idea idea) {
        // get hands action
        String command = null;
        Idea command_idea = idea.get("handsAction");
        if (command_idea != null && command_idea.getValue() instanceof String) {
            command = (String) command_idea.getValue();
        }
        if (command == null) command = "";
        
        if(!command.equals("") && (!command.equals(previousHandsAction))) {
			JSONObject jsonAction;
			try {
				jsonAction = new JSONObject(command);
				if (jsonAction.has("ACTION") && jsonAction.has("OBJECT")) {
					String action=jsonAction.getString("ACTION");
					String objectName=jsonAction.getString("OBJECT");

					if (action.equals("PICKUP")) {
                        try {
                            c.putInSack(objectName);
                        } catch (Exception e) {
                            e.printStackTrace();                        
                        } 
						log.info("Sending Put In Sack command to agent:****** "+objectName+"**********");
					}
					if (action.equals("EATIT")) {
                        try {
                            c.eatIt(objectName);
                        } catch (Exception e) {
                            e.printStackTrace();                   
                        }
						log.info("Sending Eat command to agent:****** "+objectName+"**********");							
					}
					if (action.equals("BURY")) {
                        try {
                            c.hideIt(objectName);
                        } catch (Exception e) {
                            e.printStackTrace();                         
                        }
						log.info("Sending Bury command to agent:****** "+objectName+"**********");							
					}
                }
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    }
		previousHandsAction = command;

        return null;
    }
}