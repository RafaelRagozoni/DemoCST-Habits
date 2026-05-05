/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *    Klaus Raizer, Andre Paraense, Ricardo Ribeiro Gudwin
 *****************************************************************************/

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.representation.idea.HabitExecutionerCodelet;
import br.unicamp.cst.representation.idea.Habit;
import br.unicamp.cst.representation.idea.Idea;
import codelets.habits.sensors.InnerSenseHabit;
import codelets.habits.sensors.VisionHabit;
import codelets.habits.perception.ClosestAppleDetectorHabit;
import codelets.habits.perception.AppleDetectorHabit;
import codelets.habits.behaviors.GoToClosestAppleHabit;
import codelets.habits.behaviors.EatClosestAppleHabit;
import codelets.habits.behaviors.ForageHabit;
import codelets.habits.motor.LegsActionHabit;
import codelets.habits.motor.HandsActionHabit;
import codelets.habits.ExecutionHabits;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ws3dproxy.model.Thing;
/**
 *
 * @author rgudwin
 */
public class AgentMind extends Mind {

    public MemoryContainer mc;
    public MemoryObject moi;
    public MemoryObject moo;

    private static int creatureBasicSpeed=3;
    private static int reachDistance=50;
    public ArrayList<Codelet> behavioralCodelets = new ArrayList<Codelet>();
    
    public AgentMind(Environment env) {
        super();

        // Create CodeletGroups and MemoryGroups for organizing Codelets and Memories
        // Codelets
        createCodeletGroup("Sensory");
        createCodeletGroup("Perception");
        createCodeletGroup("Behavioral");
        createCodeletGroup("Motor");
        // Memories
        createMemoryGroup("Sensory");
        createMemoryGroup("Perceptual");
        createMemoryGroup("Motor");

        // Declare Memory Objects
        Memory visionMO;
        Memory innerSenseMO;
        Memory closestAppleMO;
        Memory knownApplesMO;
        Memory legsMO;
        Memory handsMO;

        //Initialize Memory Objects
        
        // Vision
        List<Thing> vision_list = Collections.synchronizedList(new ArrayList<Thing>());
        Idea vision_list_idea = Idea.createIdea("vision",vision_list, Idea.guessType("AbstractObject",null,1.0,0.5));
        visionMO=createMemoryObject("vision",vision_list_idea);
        registerMemory(visionMO,"Sensory");

        // InnerSense
        // Child names must match what InnerSenseHabit and other habits call via idea.get("path").
        // Using new Idea() (not createIdea) for children to avoid static-repo aliasing on short names.
        Idea cis = Idea.createIdea("cis","", Idea.guessType("AbstractObject",null,1.0,0.5));
        cis.add(new Idea("pitch", 0D, Idea.guessType("Property", null,1.0,0.5)));
        cis.add(new Idea("fuel", 0D, Idea.guessType("Property", null,1.0,0.5)));
        Idea position = new Idea("position","", Idea.guessType("Property",null,1.0,0.5));
        position.add(new Idea("x", 0D, Idea.guessType("QualityDimension",null,1.0,0.5)));
        position.add(new Idea("y", 0D, Idea.guessType("QualityDimension",null,1.0,0.5)));
        cis.add(position);
        Idea fov = new Idea("FOV","", Idea.guessType("Property", null,1.0,0.5));
        Idea bounds = new Idea("bounds","", Idea.guessType("Property", null,1.0,0.5));
        bounds.add(new Idea("x", null, Idea.guessType("Property", null,1.0,0.5)));
        bounds.add(new Idea("y", null, Idea.guessType("Property", null,1.0,0.5)));
        bounds.add(new Idea("height", null, Idea.guessType("Property", null,1.0,0.5)));
        bounds.add(new Idea("width", null, Idea.guessType("Property", null,1.0,0.5)));
        fov.add(bounds);
        fov.add(new Idea("npoints", 0, Idea.guessType("Property", null,1.0,0.5)));
        fov.add(new Idea("points","", Idea.guessType("Property", null,1.0,0.5)));
        cis.add(fov);
        innerSenseMO=createMemoryObject("cis", cis);
        registerMemory(innerSenseMO,"Sensory");

        // ClosestApple
        Thing closestApple = null;
        Idea closestApple_idea = Idea.createIdea("closestApple",closestApple, Idea.guessType("AbstractObject",null,1.0,0.5));
        closestAppleMO=createMemoryObject("closestApple", closestApple_idea);
        registerMemory(closestAppleMO,"Perceptual");

        // KnownApples
        List<Thing> knownApples_list = Collections.synchronizedList(new ArrayList<Thing>());
        Idea knownApples_list_idea = Idea.createIdea("knownApples",knownApples_list, Idea.guessType("AbstractObject",null,1.0,0.5));
        knownApplesMO=createMemoryObject("knownApples", knownApples_list_idea);
        registerMemory(knownApplesMO,"Perceptual");

        // Legs
        legsMO=createMemoryObject("legsAction");
        registerMemory(legsMO,"Motor");

        // Hands
        handsMO=createMemoryObject("handsAction", "");
        registerMemory(handsMO,"Motor");

        // Declare Memory Containers
        MemoryContainer sensoryMC;
        MemoryContainer perceptionMC;
        MemoryContainer behaviorMC;
        MemoryContainer motorMC;

        // Initialize Memory Containers
        sensoryMC = createMemoryContainer("SensoryHabits");
        perceptionMC = createMemoryContainer("PerceptionHabits");
        behaviorMC = createMemoryContainer("BehavioralHabits");
        motorMC = createMemoryContainer("MotorHabits");

        // Initialize Execution Habits
        ExecutionHabits sensoryExecutionHabits;
        ExecutionHabits perceptionExecutionHabits;
        ExecutionHabits behaviorExecutionHabits;
        ExecutionHabits motorExecutionHabits;

        // Create Sensor Habits
        Idea vh = new Idea("VisionHabit");
        Habit visionHabit = new VisionHabit(env.c);
        vh.setValue(visionHabit);
        vh.setScope(2);
        
        Idea ish = new Idea("InnerSenseHabit");
        Habit innerSenseHabit = new InnerSenseHabit(env.c, cis);
        ish.setValue(innerSenseHabit);
        ish.setScope(2);

        List<Habit> sensoryHabits = new ArrayList<Habit>(List.of(visionHabit, innerSenseHabit));
        sensoryExecutionHabits = new ExecutionHabits(sensoryHabits, "Sensory");

        Idea sensoryIdea = new Idea("Sensory");
        sensoryIdea.setValue(sensoryExecutionHabits);
        sensoryIdea.setScope(2);
        sensoryMC.setI(sensoryIdea);
        HabitExecutionerCodelet sensoryHEC = new HabitExecutionerCodelet();
        sensoryHEC.setName("Sensory");
        sensoryHEC.addInput(sensoryMC);
        sensoryHEC.addOutput(visionMO);
        sensoryHEC.addOutput(innerSenseMO);
        insertCodelet(sensoryHEC);
        registerCodelet(sensoryHEC,"Sensory");

        // Create Perception Codelets
        Idea adh = new Idea("AppleDetectorHabit");
        Habit appleDetectorHabit = new AppleDetectorHabit();
        adh.setValue(appleDetectorHabit);
        adh.setScope(2);

        Idea cadh = new Idea("ClosestAppleDetectorHabit");
        Habit closestAppleDetectorHabit = new ClosestAppleDetectorHabit();
        cadh.setValue(closestAppleDetectorHabit);
        cadh.setScope(2);
        
        List<Habit> perceptionHabits = new ArrayList<Habit>(List.of(appleDetectorHabit, closestAppleDetectorHabit));
        perceptionExecutionHabits = new ExecutionHabits(perceptionHabits, "Perception");

        Idea perceptionIdea = new Idea("Perception");
        perceptionIdea.setValue(perceptionExecutionHabits);
        perceptionIdea.setScope(2);
        perceptionMC.setI(perceptionIdea);
        HabitExecutionerCodelet perceptionHEC = new HabitExecutionerCodelet();
        perceptionHEC.setName("Perception");
        perceptionHEC.addInput(perceptionMC);
        perceptionHEC.addInput(knownApplesMO);
        perceptionHEC.addInput(innerSenseMO);
        perceptionHEC.addInput(visionMO);
        perceptionHEC.addOutput(knownApplesMO);
        perceptionHEC.addOutput(closestAppleMO);
        insertCodelet(perceptionHEC);
        registerCodelet(perceptionHEC,"Perception");

        // Create Behavior Codelets
        Idea gtcah = new Idea("GoToClosestAppleHabit");
        Habit goToClosestAppleHabit = new GoToClosestAppleHabit(creatureBasicSpeed, reachDistance);
        gtcah.setValue(goToClosestAppleHabit);
        gtcah.setScope(2);
        
        Idea ecah = new Idea("EatClosestAppleHabit");
        Habit eatClosestAppleHabit = new EatClosestAppleHabit(reachDistance);
        ecah.setValue(eatClosestAppleHabit);
        ecah.setScope(2);
        
        Idea fh = new Idea("ForageHabit");
        Habit forageHabit = new ForageHabit();
        fh.setValue(forageHabit);
        fh.setScope(2);

        List<Habit> behaviorHabits = new ArrayList<Habit>(List.of(goToClosestAppleHabit, eatClosestAppleHabit, forageHabit));
        behaviorExecutionHabits = new ExecutionHabits(behaviorHabits, "Behavioral");

        Idea behaviorIdea = new Idea("Behavioral");
        behaviorIdea.setValue(behaviorExecutionHabits);
        behaviorIdea.setScope(2);
        behaviorMC.setI(behaviorIdea);

        HabitExecutionerCodelet behaviorHEC = new HabitExecutionerCodelet();

        behaviorHEC.setName("Behavioral");
        behaviorHEC.addInput(behaviorMC);
        behaviorHEC.addInput(closestAppleMO);
        behaviorHEC.addInput(innerSenseMO);
        behaviorHEC.addInput(legsMO);
        behaviorHEC.addInput(knownApplesMO);
        behaviorHEC.addOutput(legsMO);
        behaviorHEC.addOutput(handsMO);
        insertCodelet(behaviorHEC);
        registerCodelet(behaviorHEC,"Behavioral");

        // Create Actuator Codelets
        Idea lah = new Idea("LegsActionHabit");
        Habit legsActionHabit = new LegsActionHabit(env.c);
        lah.setValue(legsActionHabit);
        lah.setScope(2);    
        
        Idea hah = new Idea("HandsActionHabit");
        Habit handsActionHabit = new HandsActionHabit(env.c);
        hah.setValue(handsActionHabit);
        hah.setScope(2);

        List<Habit> motorHabits = new ArrayList<Habit>(List.of(legsActionHabit, handsActionHabit));
        motorExecutionHabits = new ExecutionHabits(motorHabits, "Motor");

        Idea motorIdea = new Idea("Motor");
        motorIdea.setValue(motorExecutionHabits);
        motorIdea.setScope(2);
        motorMC.setI(motorIdea);

        HabitExecutionerCodelet motorHEC = new HabitExecutionerCodelet();        
        motorHEC.setName("Motor");
        motorHEC.addInput(motorMC);
        motorHEC.addInput(legsMO);
        motorHEC.addInput(handsMO);
        motorHEC.addOutput(legsMO);
        motorHEC.addOutput(handsMO);
        insertCodelet(motorHEC);
        registerCodelet(motorHEC,"Motor");
        
        // sets a time step for running the codelets to avoid heating too much your machine
        for (Codelet c : this.getCodeRack().getAllCodelets())
            c.setTimeStep(200);
        
        start();  
    }
}