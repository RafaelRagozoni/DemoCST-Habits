package codelets.habits.sensors;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import java.awt.Polygon;
import ws3dproxy.model.Creature;

public class InnerSenseHabit implements Habit {
    private Creature c;
    private Idea cis;

    public InnerSenseHabit(Creature nc, Idea cis) {
        this.c = nc;
        this.cis = cis;
    }
    
    @Override 
    public Idea exec(Idea idea) {
        cis.get("position.x").setValue(c.getPosition().getX());
        cis.get("position.y").setValue(c.getPosition().getY());
        cis.get("pitch").setValue(c.getPitch());
        cis.get("fuel").setValue(c.getFuel());
        Polygon pol = c.getFOV();
        Idea poli = cis.get("FOV");
        poli.get("bounds.x").setValue(pol.getBounds().getX());
        poli.get("bounds.y").setValue(pol.getBounds().getY());
        poli.get("bounds.width").setValue(pol.getBounds().getWidth());
        poli.get("bounds.height").setValue(pol.getBounds().getHeight());
        poli.get("npoints").setValue(pol.npoints);
        Idea points = poli.get("points");
        for (int i=0;i<pol.npoints;i++) {
            Idea p = Idea.createIdea("points.["+i+"]","("+pol.xpoints[i]+","+pol.ypoints[i]+")", Idea.guessType("Property", null,1.0,0.5));
            p.add(Idea.createIdea("points.["+i+"].x",pol.xpoints[i], Idea.guessType("Property", null,1.0,0.5)));
            p.add(Idea.createIdea("points.["+i+"].y",pol.ypoints[i], Idea.guessType("Property", null,1.0,0.5)));
            if (points.get("["+i+"]") == null) points.add(p);
        }

        return cis;
    }
}
