package codelets.habits.perception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Thing;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClosestAppleDetectorHabit implements Habit {
    @Override 
    public Idea exec(Idea idea) {
        // get cis
        Idea cis = idea.get("cis");
        if (cis == null) {
            return null;
        }

        // get known
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

        Thing closest_apple=null;

        synchronized(known) {
            if (known.size() != 0){
                //Iterate over objects in vision, looking for the closest apple
                CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
                for (Thing t : myknown) {
                    String objectName=t.getName();
                    if(objectName.contains("PFood") && !objectName.contains("NPFood")) { //Then, it is an apple
                        if(closest_apple == null) {    
                            closest_apple = t;
                        }
                        else {
                            double Dnew = calculateDistance(t.getX1(), t.getY1(), (double)cis.get("position.x").getValue(), (double)cis.get("position.y").getValue());
                            double Dclosest= calculateDistance(closest_apple.getX1(), closest_apple.getY1(), (double)cis.get("position.x").getValue(), (double)cis.get("position.y").getValue());
                            if (Dnew<Dclosest) {
                                closest_apple = t;
                            }
                        }
                    }
                }
            }
        }

        return new Idea("closestApple", closest_apple);
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return(Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
    }
}
