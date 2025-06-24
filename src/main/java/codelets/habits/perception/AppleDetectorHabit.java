package codelets.habits.perception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Thing;

public class AppleDetectorHabit implements Habit {
    @Override 
    public Idea exec(Idea idea) {
        // get vision
        List<Thing> vision = Collections.synchronizedList(new ArrayList<Thing>());
        Idea vision_idea = idea.get("vision");
        if (vision_idea != null && vision_idea.getValue() instanceof List<?>) {
            try {
                vision = (List<Thing>) vision_idea.getValue();
            } catch (ClassCastException e) {
                e.printStackTrace();
                return null;
            }
        }

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

        synchronized(vision) {
            for (Thing t : vision) {
                boolean found = false;
                synchronized(known) {
                    CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
                    for (Thing e : myknown)
                        if (t.getName().equals(e.getName())) {
                            found = true;
                            break;
                        }
                    if (found == false && t.getName().contains("PFood") && !t.getName().contains("NPFood")) known.add(t);
                }
            }
        }

        return new Idea("knownApples", known);
    }
}
