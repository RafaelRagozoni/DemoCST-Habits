package codelets.habits.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Thing;

public class ForageHabit implements Habit {
    @Override 
    public Idea exec(Idea idea) {
        // get legs action idea
        Idea comm_idea = idea.get("legsAction");

        // get knownApples
        List<Thing> known = Collections.synchronizedList(new ArrayList<Thing>());
        Idea knownApples_idea = idea.get("knownApples");
        if (knownApples_idea != null && knownApples_idea.getValue() instanceof List<?>) {
            try {
                known = (List<Thing>) knownApples_idea.getValue();
            } catch (ClassCastException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (known.size() == 0) {
            JSONObject message=new JSONObject();
            try {
                message.put("ACTION", "FORAGE");
                //activation=1.0;
                return new Idea("legsAction", message.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
