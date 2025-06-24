package codelets.habits.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.awt.Point;
import java.awt.geom.Point2D;
import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Thing;
import java.util.concurrent.CopyOnWriteArrayList;

public class EatClosestAppleHabit implements Habit {
	private double reachDistance;
    private Thing closestApple = null;
    private List<Thing> known = Collections.synchronizedList(new ArrayList<Thing>());

    public EatClosestAppleHabit(int reachDistance) {
		this.reachDistance = reachDistance;
    }

    @Override 
    public Idea exec(Idea idea) {
        // get cis
        Idea cis = idea.get("cis");
        if (cis == null) {
            return null;
        }

        // get closestApple
        Idea closestApple_idea = idea.get("closestApple");
        if (closestApple_idea != null && closestApple_idea.getValue() instanceof Thing) {
            closestApple = (Thing) closestApple_idea.getValue();
        }

        // get knownApples
        Idea knownApples_idea = idea.get("knownApples");
        if (knownApples_idea != null && knownApples_idea.getValue() instanceof List<?>) {
            try {
                known = (List<Thing>) knownApples_idea.getValue();
            } catch (ClassCastException e) {
                e.printStackTrace();
                return null;
            }
        }

        String appleName="";
		//Find distance between closest apple and self
		//If closer than reachDistance, eat the apple
		
		if (closestApple != null) {
			double appleX=0;
			double appleY=0;
			try {
				appleX=closestApple.getCenterPosition().getX();
				appleY=closestApple.getCenterPosition().getY();
                appleName = closestApple.getName();                                
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			JSONObject message=new JSONObject();
			try {
				if (distance<=reachDistance) { //eat it		
                    //System.out.println("EatClosestAppleHabit: Eating apple: " + appleName);			
					message.put("OBJECT", appleName);
					message.put("ACTION", "EATIT");
                    DestroyClosestApple();
                    //Idea knownRet = new Idea("knownApples", known);
                    Idea handsRet = new Idea("handsAction", message.toString());
					//knownRet.add(handsRet);
                    return handsRet;
                    // activation=1.0;
				} else {
					//Idea knownRet = new Idea("knownApples", known);
                    Idea handsRet = new Idea("handsAction", ""); //nothing
					//knownRet.add(handsRet);
                    return handsRet;
                    // activation=0.0;
				}
				
                //System.out.println(message);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
                return null;
			}
		} else {
			//Idea knownRet = new Idea("knownApples", known);
            Idea handsRet = new Idea("handsAction", ""); //nothing
            //knownRet.add(handsRet);
            return handsRet;
            // activation=0.0;
		}
    //System.out.println("Before: "+known.size()+ " "+known);
    //System.out.println("After: "+known.size()+ " "+known);
	//System.out.println("EatClosestApple: "+ handsMO.getInfo());	
    }

    public void DestroyClosestApple() {
        int r = -1;
        int i = 0;
        synchronized(known) {
            CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);  
            for (Thing t : myknown) {
                if (closestApple != null) 
                    if (t.getName().equals(closestApple.getName())) r = i;
                i++;
            }   
            if (r != -1) known.remove(r);
            closestApple = null;
        }
    }
}
