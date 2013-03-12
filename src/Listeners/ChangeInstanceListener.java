package Listeners;

import GUI.MobiDGui;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

/**
 * Event for when change the name of the instance
 * */
public class ChangeInstanceListener implements mxIEventListener {

	MobiDGui mobiDGui;
	
	public ChangeInstanceListener(MobiDGui mobiDGui) {
		this.mobiDGui = mobiDGui;
	}

	@Override
	public void invoke(Object arg0, mxEventObject arg1) {
		System.out.println("ChangeInstanceListener: You make one change in some instance!!");

	}

}
