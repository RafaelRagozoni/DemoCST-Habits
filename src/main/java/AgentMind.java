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
        visionMO=createMemoryObject("VISION",vision_list_idea);
        registerMemory(visionMO,"Sensory");

        // InnerSense
        //CreatureInnerSense cis = new CreatureInnerSense();
        Idea cis = Idea.createIdea("cis","", Idea.guessType("AbstractObject",null,1.0,0.5));
        cis.add(Idea.createIdea("cis.pitch", 0D, Idea.guessType("Property", null,1.0,0.5)));
        cis.add(Idea.createIdea("cis.fuel", 0D, Idea.guessType("Property", null,1.0,0.5)));
        Idea position = Idea.createIdea("cis.position","", Idea.guessType("Property",null,1.0,0.5));
        position.add(Idea.createIdea("cis.position.x",0D,Idea.guessType("QualityDimension",null,1.0,0.5)));
        position.add(Idea.createIdea("cis.position.y",0D,Idea.guessType("QualityDimension",null,1.0,0.5)));
        cis.add(position);
        Idea fov = Idea.createIdea("cis.FOV","", Idea.guessType("Property", null,1.0,0.5));
        Idea bounds = Idea.createIdea("cis.FOV.bounds","", Idea.guessType("Property", null,1.0,0.5));
        bounds.add(Idea.createIdea("cis.FOV.bounds.x",null, Idea.guessType("Property", null,1.0,0.5)));
        bounds.add(Idea.createIdea("cis.FOV.bounds.y",null, Idea.guessType("Property", null,1.0,0.5)));
        bounds.add(Idea.createIdea("cis.FOV.bounds.height",null, Idea.guessType("Property", null,1.0,0.5)));
        bounds.add(Idea.createIdea("cis.FOV.bounds.width",null, Idea.guessType("Property", null,1.0,0.5)));
        fov.add(bounds);
        fov.add(Idea.createIdea("cis.FOV.npoints",0, Idea.guessType("Property", null,1.0,0.5)));
        fov.add(Idea.createIdea("cis.FOV.points","", Idea.guessType("Property", null,1.0,0.5)));
        cis.add(fov);
        innerSenseMO=createMemoryObject("INNER", cis);
        registerMemory(innerSenseMO,"Sensory");

        // ClosestApple
        Thing closestApple = null;
        Idea closestApple_idea = Idea.createIdea("closestApple",closestApple, Idea.guessType("AbstractObject",null,1.0,0.5));
        closestAppleMO=createMemoryObject("CLOSEST_APPLE", closestApple_idea);
        registerMemory(closestAppleMO,"Perceptual");

        // KnownApples
        List<Thing> knownApples_list = Collections.synchronizedList(new ArrayList<Thing>());
        Idea knownApples_list_idea = Idea.createIdea("knownApples",knownApples_list, Idea.guessType("AbstractObject",null,1.0,0.5));
        knownApplesMO=createMemoryObject("KNOWN_APPLES", knownApples_list_idea);
        registerMemory(knownApplesMO,"Perceptual");

        // Legs
        legsMO=createMemoryObject("LEGS");
        registerMemory(legsMO,"Motor");

        // Hands
        handsMO=createMemoryObject("HANDS", "");
        registerMemory(handsMO,"Motor");

        // Declare Memory Containers
        MemoryContainer visionMC;
        MemoryContainer innerSenseMC;
        MemoryContainer appleDetectorMC;
        MemoryContainer closestAppleDetectorMC;
        MemoryContainer goToClosestAppleMC;
        MemoryContainer eatClosestAppleMC;
        MemoryContainer forageMC;
        MemoryContainer legsActionMC;
        MemoryContainer handsActionMC;

        // Initialize Memory Containers
        visionMC = createMemoryContainer("VisionHabitsMemory");
        innerSenseMC = createMemoryContainer("InnerSenseHabitsMemory");
        appleDetectorMC = createMemoryContainer("AppleDetectorHabitsMemory");
        closestAppleDetectorMC = createMemoryContainer("ClosestAppleDetectorHabitsMemory");
        goToClosestAppleMC = createMemoryContainer("GoToClosestAppleHabitsMemory");
        eatClosestAppleMC = createMemoryContainer("EatClosestAppleHabitsMemory");
        forageMC = createMemoryContainer("ForageHabitsMemory");
        legsActionMC = createMemoryContainer("LegsActionHabitsMemory");
        handsActionMC = createMemoryContainer("HandsActionHabitsMemory");

        // Create Sensor Habits
        Idea vh = new Idea("VisionHabit");
        Habit visionHabit = new VisionHabit(env.c);
        vh.setValue(visionHabit);
        vh.setScope(2);
        visionMC.setI(vh);
        HabitExecutionerCodelet visionHEC = new HabitExecutionerCodelet();
        visionHEC.setName("visionHEC");
        visionHEC.addInput(visionMC);
        visionHEC.addOutput(visionMO); // This is the output memory object
        // visionHEC.setPublishSubscribe(true);
        insertCodelet(visionHEC);
        registerCodelet(visionHEC,"Sensory");

        Idea ish = new Idea("InnerSenseHabit");
        Habit innerSenseHabit = new InnerSenseHabit(env.c, cis);
        ish.setValue(innerSenseHabit);
        ish.setScope(2);
        innerSenseMC.setI(ish);
        HabitExecutionerCodelet innerSenseHEC = new HabitExecutionerCodelet();
        innerSenseHEC.setName("innerSenseHEC");
        innerSenseHEC.addInput(innerSenseMC);
        innerSenseHEC.addOutput(innerSenseMO); // This is the output memory object
        // innerSenseHEC.setPublishSubscribe(true);
        insertCodelet(innerSenseHEC);
        registerCodelet(innerSenseHEC,"Sensory");

        // Create Perception Codelets
        Idea adh = new Idea("AppleDetectorHabit");
        Habit appleDetectorHabit = new AppleDetectorHabit();
        adh.setValue(appleDetectorHabit);
        adh.setScope(2);
        appleDetectorMC.setI(adh);
        HabitExecutionerCodelet appleDetectorHEC = new HabitExecutionerCodelet();
        appleDetectorHEC.setName("appleDetectorHEC");
        appleDetectorHEC.addInput(appleDetectorMC);
        appleDetectorHEC.addInput(knownApplesMO);
        appleDetectorHEC.addInput(visionMO);
        appleDetectorHEC.addOutput(knownApplesMO); // This is the output memory object
        // appleDetectorHEC.setPublishSubscribe(true);
        insertCodelet(appleDetectorHEC);
        registerCodelet(appleDetectorHEC,"Perception");

        Idea cadh = new Idea("ClosestAppleDetectorHabit");
        Habit closestAppleDetectorHabit = new ClosestAppleDetectorHabit();
        cadh.setValue(closestAppleDetectorHabit);
        cadh.setScope(2);
        closestAppleDetectorMC.setI(cadh);
        HabitExecutionerCodelet closestAppleDetectorHEC = new HabitExecutionerCodelet();
        closestAppleDetectorHEC.setName("closestAppleDetectorHEC");
        closestAppleDetectorHEC.addInput(closestAppleDetectorMC);
        closestAppleDetectorHEC.addInput(innerSenseMO);
        closestAppleDetectorHEC.addInput(knownApplesMO);
        closestAppleDetectorHEC.addOutput(closestAppleMO); // This is the output memory object
        // closestAppleDetectorHEC.setPublishSubscribe(true);
        insertCodelet(closestAppleDetectorHEC);
        registerCodelet(closestAppleDetectorHEC,"Perception");

        // Create Behavior Codelets
        Idea gtcah = new Idea("GoToClosestAppleHabit");
        Habit goToClosestAppleHabit = new GoToClosestAppleHabit(creatureBasicSpeed, reachDistance);
        gtcah.setValue(goToClosestAppleHabit);
        gtcah.setScope(2);
        goToClosestAppleMC.setI(gtcah);
        HabitExecutionerCodelet goToClosestAppleHEC = new HabitExecutionerCodelet();
        goToClosestAppleHEC.setName("goToClosestAppleHEC");
        goToClosestAppleHEC.addInput(goToClosestAppleMC);
        goToClosestAppleHEC.addInput(closestAppleMO);
        goToClosestAppleHEC.addInput(innerSenseMO);
        goToClosestAppleHEC.addInput(legsMO);
        goToClosestAppleHEC.addOutput(legsMO); // This is the output memory object
        // goToClosestAppleHEC.setPublishSubscribe(true);
        insertCodelet(goToClosestAppleHEC);
        registerCodelet(goToClosestAppleHEC,"Behavioral");
        behavioralCodelets.add(goToClosestAppleHEC);

        Idea ecah = new Idea("EatClosestAppleHabit");
        Habit eatClosestAppleHabit = new EatClosestAppleHabit(reachDistance);
        ecah.setValue(eatClosestAppleHabit);
        ecah.setScope(2);
        eatClosestAppleMC.setI(ecah);
        HabitExecutionerCodelet eatClosestAppleHEC = new HabitExecutionerCodelet();
        eatClosestAppleHEC.setName("eatClosestAppleHEC");
        eatClosestAppleHEC.setTimeStep(50);
        eatClosestAppleHEC.addInput(eatClosestAppleMC);
        eatClosestAppleHEC.addInput(closestAppleMO);
        eatClosestAppleHEC.addInput(innerSenseMO);
        eatClosestAppleHEC.addInput(knownApplesMO);
        eatClosestAppleHEC.addOutput(handsMO); // This is the output memory object
        // eatClosestAppleHEC.addOutput(knownApplesMO); // This is the output memory object
        // eatClosestAppleHEC.setPublishSubscribe(true);
        insertCodelet(eatClosestAppleHEC);
        registerCodelet(eatClosestAppleHEC,"Behavioral");
        behavioralCodelets.add(eatClosestAppleHEC);

        Idea fh = new Idea("ForageHabit");
        Habit forageHabit = new ForageHabit();
        fh.setValue(forageHabit);
        fh.setScope(2);
        forageMC.setI(fh);
        HabitExecutionerCodelet forageHEC = new HabitExecutionerCodelet();
        forageHEC.setName("forageHEC");
        forageHEC.addInput(forageMC);
        forageHEC.addInput(knownApplesMO);
        forageHEC.addInput(legsMO);
        forageHEC.addOutput(legsMO); // This is the output memory object
        // forageHEC.setPublishSubscribe(true);
        insertCodelet(forageHEC);
        registerCodelet(forageHEC,"Behavioral");
        behavioralCodelets.add(forageHEC);

        // Create Actuator Codelets
        Idea lah = new Idea("LegsActionHabit");
        Habit legsActionHabit = new LegsActionHabit(env.c);
        lah.setValue(legsActionHabit);
        lah.setScope(2);
        legsActionMC.setI(lah);
        HabitExecutionerCodelet legsActionHEC = new HabitExecutionerCodelet();
        legsActionHEC.setName("legsActionHEC");
        legsActionHEC.addInput(legsActionMC);
        legsActionHEC.addInput(legsMO);
        // legsActionHEC.setPublishSubscribe(true);
        insertCodelet(legsActionHEC);
        registerCodelet(legsActionHEC,"Motor");

        Idea hah = new Idea("HandsActionHabit");
        Habit handsActionHabit = new HandsActionHabit(env.c);
        hah.setValue(handsActionHabit);
        hah.setScope(2);
        handsActionMC.setI(hah);
        HabitExecutionerCodelet handsActionHEC = new HabitExecutionerCodelet();
        handsActionHEC.setName("handsActionHEC");
        handsActionHEC.addInput(handsActionMC);
        handsActionHEC.addInput(handsMO);
        // handsActionHEC.setPublishSubscribe(true);
        insertCodelet(handsActionHEC);
        registerCodelet(handsActionHEC,"Motor");

        // sets a time step for running the codelets to avoid heating too much your machine
        for (Codelet c : this.getCodeRack().getAllCodelets())
            c.setTimeStep(200);
        
        start();  
    }
}