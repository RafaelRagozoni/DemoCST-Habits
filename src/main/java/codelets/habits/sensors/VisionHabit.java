package codelets.habits.sensors;

import java.util.List;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;

public class VisionHabit implements Habit {
    private Creature c;

    public VisionHabit(Creature nc) {
        this.c = nc;
    }

    @Override 
    public Idea exec(Idea idea) {
        c.updateState();
             
        List<Thing> lt = c.getThingsInVision();
        return new Idea("vision", lt);
    }
}
