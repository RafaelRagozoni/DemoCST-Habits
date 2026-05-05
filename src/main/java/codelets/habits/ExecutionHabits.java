package codelets.habits;

import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ExecutionHabits - A composite Habit that encapsulates multiple habits
 * 
 * DESIGN PATTERN:
 * - Receives a combined Idea with multiple named children (e.g., "vision", "knownApples", "cis")
 * - Each individual habit extracts the specific Idea children it needs by name: idea.get("vision")
 * - All habits receive the SAME input Idea, allowing them to share data
 * - Collects all habit outputs and returns them grouped
 * 
 * EXAMPLE:
 * Input Idea structure:
 *   Idea {
 *     "vision": [Thing, Thing, ...],        // extracted by AppleDetectorHabit
 *     "knownApples": [Thing, Thing, ...],   // extracted by AppleDetectorHabit & ClosestAppleDetectorHabit
 *     "cis": {...}                           // extracted by ClosestAppleDetectorHabit
 *   }
 * 
 * HOW IT WORKS:
 * 1. HabitExecutionerCodelet reads from multiple input MemoryObjects (e.g., visionMO, knownApplesMO)
 * 2. HabitExecutionerCodelet combines them into a single Idea with named children
 * 3. ExecutionHabits.exec() receives this combined Idea
 * 4. Each habit calls exec(idea) and extracts what it needs via idea.get("name")
 * 5. Outputs are collected and returned as a list
 */
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

    /**
     * Executes all habits sequentially with the same input Idea.
     * Each habit extracts its required data by name from the input Idea.
     * 
     * IMPORTANT: Returns a COMBINED Idea with all habit outputs as direct children,
     * NOT a grouped/nested structure. This allows HabitExecutionerCodelet to find
     * output Ideas by name (e.g., "vision", "knownApples", "closestApple", etc.)
     * 
     * @param idea Combined Idea with named children like "vision", "knownApples", "cis", etc.
     * @return Combined Idea with all habit outputs as direct children
     *         Example: Idea { "vision": [...], "knownApples": [...], "closestApple": ... }
     */
    @Override
    public Idea exec(Idea idea) {
        if (idea == null) return null;

        Idea outputRoot = new Idea(habitName);
        for (Habit habit : this.habits) {
            try {
                Idea habitOutput = habit.exec(idea);
                if (habitOutput != null) {
                    outputRoot.add(habitOutput);
                }
            } catch (Exception e) {
                System.err.println("[ExecutionHabits." + habitName + "] Error in " + habit.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        return outputRoot;
    }
}
