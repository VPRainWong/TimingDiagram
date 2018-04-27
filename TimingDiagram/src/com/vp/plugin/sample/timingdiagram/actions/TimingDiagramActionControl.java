package com.vp.plugin.sample.timingdiagram.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramTypeConstants;
import com.vp.plugin.diagram.ITimingDiagramUIModel;
import com.vp.plugin.diagram.shape.ITimingFrameUIModel;
import com.vp.plugin.model.IDurationConstraint;
import com.vp.plugin.model.ILifeLine;
import com.vp.plugin.model.IStateCondition;
import com.vp.plugin.model.ITimeInstance;
import com.vp.plugin.model.ITimeMessage;
import com.vp.plugin.model.ITimeUnit;
import com.vp.plugin.model.ITimingFrame;
import com.vp.plugin.model.factory.IModelElementFactory;

public class TimingDiagramActionControl implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		// create blank timing diagram
		DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();
		ITimingDiagramUIModel diagram = (ITimingDiagramUIModel) diagramManager.createDiagram(IDiagramTypeConstants.DIAGRAM_TYPE_TIMING_DIAGRAM);
		
		// create a timing frame model
		ITimingFrame frame = IModelElementFactory.instance().createTimingFrame();
		frame.setName("sd UserAccepted");
		// create a timing frame shape
		ITimingFrameUIModel frameShape = (ITimingFrameUIModel) diagramManager.createDiagramElement(diagram, frame);
		frameShape.setBounds(100, 100, 450, 370);
		
		// create an array to store all the time units
		ITimeUnit[] timeUnits = new ITimeUnit[16];
		// create 16 time units on the timing frame
		for (int i = 0; i <= 15; i++) {
			ITimeUnit timeUnit = IModelElementFactory.instance().createTimeUnit();
			timeUnit.setName(i+"");
			frame.addTimeUnit(timeUnit);
			timeUnits[i] = timeUnit;
		}			
		
		// create the first lifeline in the timing frame
		ILifeLine lifelineUser = IModelElementFactory.instance().createLifeLine();
		lifelineUser.setName("User");
		frame.addLifeLine(lifelineUser);
		
		// create 3 states for the user lifeline
		IStateCondition stateWaitAccess = IModelElementFactory.instance().createStateCondition();
		stateWaitAccess.setName("WaitAccess");
		lifelineUser.addStateCondition(stateWaitAccess);
		
		IStateCondition stateWaitCard = IModelElementFactory.instance().createStateCondition();
		stateWaitCard.setName("WaitCard");
		lifelineUser.addStateCondition(stateWaitCard);
		
		IStateCondition stateIdle = IModelElementFactory.instance().createStateCondition();
		stateIdle.setName("Idle");		
		lifelineUser.addStateCondition(stateIdle);
		
		// create the time messages, we will then 
		// set its start and end when working 
		// on the time instance of the lifelines  
		ITimeMessage timeMsgCode = IModelElementFactory.instance().createTimeMessage();
		timeMsgCode.setName("Code");
		frame.addTimeMessage(timeMsgCode);
		ITimeMessage timeMsgCardOut = IModelElementFactory.instance().createTimeMessage();
		timeMsgCardOut.setName("CardOut");		
		frame.addTimeMessage(timeMsgCardOut);
		ITimeMessage timeMsgOK = IModelElementFactory.instance().createTimeMessage();
		timeMsgOK.setName("OK");
		frame.addTimeMessage(timeMsgOK);
		
		// create duration constraint and specify its name
		IDurationConstraint durationConstraint = IModelElementFactory.instance().createDurationConstraint();
		durationConstraint.setName("{d..3*d}");
		// add duration constraint to user lifeline 
		lifelineUser.addDurationConstraint(durationConstraint);
		
		// create time instance on user lifeline
		// from time 0 to 3 it shoul be on Idle state
		for (int i = 0; i <= 3; i++) {
			ITimeInstance timeInstance = IModelElementFactory.instance().createTimeInstance();
			timeInstance.setStateCondition(stateIdle);
			timeInstance.setTimeUnit(timeUnits[i]);			
			lifelineUser.addTimeInstance(timeInstance);
			
			// specify the start of the Code message
			// at time instance #3
			if (i == 3) {
				timeMsgCode.setStartTime(timeInstance);
			}
		}
		
//		 from time 3 to 10 it should be on WaitCard state
		for (int i = 3; i <= 10; i++) {
			ITimeInstance timeInstance = IModelElementFactory.instance().createTimeInstance();
			timeInstance.setStateCondition(stateWaitCard);
			timeInstance.setTimeUnit(timeUnits[i]);			
			lifelineUser.addTimeInstance(timeInstance);	
			// specify the start of the duration constraint
			if (i == 3) {
				durationConstraint.setStartTime(timeInstance);
			}
			// specify the end of the duration constraint
			if (i == 10) {
				durationConstraint.setEndTime(timeInstance);
				// specify the end of the Card Out message
				// at time instance #10
				timeMsgCardOut.setEndTime(timeInstance);
			}			
		}
		
		// from time 10 to 13 it should be on WaitAccess state
		for (int i = 10; i <= 13; i++) {
			ITimeInstance timeInstance = IModelElementFactory.instance().createTimeInstance();
			timeInstance.setStateCondition(stateWaitAccess);
			timeInstance.setTimeUnit(timeUnits[i]);			
			lifelineUser.addTimeInstance(timeInstance);	
			if (i == 13) {
				// specify the end of the OK message
				// at time instance #13
				timeMsgOK.setEndTime(timeInstance);
			}
		}
		
		// from time 13 to 15 it should be on Idle Access state
		for (int i = 13; i <= 15; i++) {
			ITimeInstance timeInstance = IModelElementFactory.instance().createTimeInstance();
			timeInstance.setStateCondition(stateIdle);
			timeInstance.setTimeUnit(timeUnits[i]);			
			lifelineUser.addTimeInstance(timeInstance);	
			if (i == 13) {
				// add a time constraint to time instance #13
				timeInstance.setTimeConstraint("{t..t+3}");
			}						
		}

		// create the second lifeline and its states
		ILifeLine lifelineACSystem = IModelElementFactory.instance().createLifeLine();
		lifelineACSystem.setName("ACSystem");
		frame.addLifeLine(lifelineACSystem);
		
		IStateCondition stateNoCard = IModelElementFactory.instance().createStateCondition();
		stateNoCard.setName("NoCard");		
		lifelineACSystem.addStateCondition(stateNoCard);
		
		IStateCondition stateHasCard = IModelElementFactory.instance().createStateCondition();
		stateHasCard.setName("HasCard");		
		lifelineACSystem.addStateCondition(stateHasCard);

		// create time instance on ACSystem lifeline
		// from time 0 to 5 it should be on NoCard state
		for (int i = 0; i <= 5; i++) {
			ITimeInstance timeInstance = IModelElementFactory.instance().createTimeInstance();
			timeInstance.setStateCondition(stateNoCard);
			timeInstance.setTimeUnit(timeUnits[i]);
			lifelineACSystem.addTimeInstance(timeInstance);
			// specify the end of the Code message
			// at time instance #5
			if (i == 5) {
				timeMsgCode.setEndTime(timeInstance);
			}
		}
		
		// from time 5 to 11 it should be on HasCard state
		for (int i = 5; i <= 11; i++) {
			ITimeInstance timeInstance = IModelElementFactory.instance().createTimeInstance();
			timeInstance.setStateCondition(stateHasCard);
			timeInstance.setTimeUnit(timeUnits[i]);
			lifelineACSystem.addTimeInstance(timeInstance);		
			
			// specify the start of the CardOut message
			// at time instance #8
			if (i == 8) {
				timeMsgCardOut.setStartTime(timeInstance);
			}
			// specify the start of the OK message
			// at time instance #10
			if (i == 10) {
				timeMsgOK.setStartTime(timeInstance);
			}
		}
		
		// from time 11 to 15 it should be on NoCard state
		for (int i = 11; i <= 15; i++) {
			ITimeInstance timeInstance = IModelElementFactory.instance().createTimeInstance();
			timeInstance.setStateCondition(stateNoCard);
			timeInstance.setTimeUnit(timeUnits[i]);
			lifelineACSystem.addTimeInstance(timeInstance);
		}
		
		// show up the diagram		
		diagramManager.openDiagram(diagram);				

	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
		
	}

}
