package codelets.habits.behaviors;

import java.awt.Point;
import java.awt.geom.Point2D;
import org.json.JSONException;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Thing;

public class GoToClosestAppleHabit implements Habit {
    private int creatureBasicSpeed;
	private double reachDistance;

    public GoToClosestAppleHabit(int creatureBasicSpeed, int reachDistance) {
        this.creatureBasicSpeed = creatureBasicSpeed;
		this.reachDistance = reachDistance;
    }

    @Override 
    public Idea exec(Idea idea) {
        // get legs action idea
        Idea comm_idea = idea.get("legsAction");

        // get cis
        Idea cis = idea.get("cis");
        if (cis == null) {
            return null;
        }

        // get closestApple
        Thing closestApple = null;
        Idea closestApple_idea = idea.get("closestApple");
        if (closestApple_idea != null && closestApple_idea.getValue() instanceof Thing) {
            closestApple = (Thing) closestApple_idea.getValue();
        }

        // Find distance between creature and closest apple
        //If far, go towards it
        //If close, stops

        if (closestApple != null) {
            double appleX=0;
            double appleY=0;
            try {
                appleX = closestApple.getCenterPosition().getX();
                appleY = closestApple.getCenterPosition().getY();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
                
            double selfX=(double)cis.get("position.x").getValue();
            double selfY=(double)cis.get("position.y").getValue();

            Point2D pApple = new Point();
            pApple.setLocation(appleX, appleY);

            Point2D pSelf = new Point();
            pSelf.setLocation(selfX, selfY);

            double distance = pSelf.distance(pApple);
            //JSONObject message=new JSONObject();
            Idea message = Idea.createIdea("message","", Idea.guessType("Property",null,1.0,0.5));
            try {
                if (distance>reachDistance) { //Go to it
                    message.add(Idea.createIdea("ACTION","GOTO", Idea.guessType("Property",null,1.0,0.5)));
                    message.add(Idea.createIdea("X",(int)appleX, Idea.guessType("Property",null,1.0,0.5)));
                    message.add(Idea.createIdea("Y",(int)appleY, Idea.guessType("Property",null,1.0,0.5)));
                    message.add(Idea.createIdea("SPEED",creatureBasicSpeed, Idea.guessType("Property",null,1.0,0.5)));
                    // activation=1.0;
                } else {//Stop
                    message.add(Idea.createIdea("ACTION","GOTO", Idea.guessType("Property",null,1.0,0.5)));
                    message.add(Idea.createIdea("X",(int)appleX, Idea.guessType("Property",null,1.0,0.5)));
                    message.add(Idea.createIdea("Y",(int)appleY, Idea.guessType("Property",null,1.0,0.5)));
                    message.add(Idea.createIdea("SPEED",0, Idea.guessType("Property",null,1.0,0.5)));
                    // activation=0.5;
                }
                return new Idea("legsAction", toJson(message));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }	
        } else {
            return null;
        }
    }

    String toJson(Idea i) {
        String q = "\"";
        String out = "{";
        String val;
        int ii=0;
        for (Idea il : i.getL()) {
            if (il.getL().isEmpty()) {
                if (il.isNumber()) val = il.getValue().toString();
                else val = q+il.getValue()+q;
            }
            else val = toJson(il);
            if (ii == 0) out += q+il.getName()+q+":"+val;
            else out += ","+q+il.getName()+q+":"+val;
            ii++;
        }
        out += "}";
        return out;
    }
}
