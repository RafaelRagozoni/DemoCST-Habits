package codelets.habits;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExecutionHabits implements Habit{
    private final ArrayList<Habit> habits;
    private final String habitName;

    public ExecutionHabits(List<Habit> habits, String habitName){
        this.habits = new ArrayList<Habit>(habits);
        this.habitName = habitName;
    }

    public void addNewHabit(Habit habit){
        this.habits.add(habit);
    }

    public void addNewHabit(List<Habit> habits){
        this.habits.addAll(habits);
    }

    // Por enquanto isso fica sincrono mas no futuro passa pra async
    @Override
    public Idea exec(Idea idea) {
        List<Idea> newIdeas = new ArrayList<Idea>();

        for (Habit habit : this.habits) {
            newIdeas.add(habit.exec(idea));
        }
        
        return new Idea(this.habitName, newIdeas);
    }
}
