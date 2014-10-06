/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Queue;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class PlugIn extends AbstractUIPlugin {
	//The shared instance.
	private static PlugIn plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	public static List TEST_CLASS = new ArrayList();
	public static List TEST_NAME = new ArrayList();
	public static List TEST_DURATION = new ArrayList();
	private static Queue<Thread> _demoThreads;
	private static boolean _warnedBeforeExit = false;
	private static boolean _runningDemoThread = false;
	private static boolean _isDead = false;
	
	public PlugIn() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("GreenDebug.GreenDebugPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		
		_demoThreads = new LinkedList<Thread>();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		
		long totalDuration = 0;
		
		for (Iterator iter = TEST_DURATION.iterator(); iter.hasNext();) {
            Long duration = (Long) iter.next();
            totalDuration += duration.longValue();
        }
		
		for (int x = 0; x < TEST_CLASS.size(); x++) {
	        float duration = ((Long) TEST_DURATION.get(x)).floatValue(); 

	        if (duration / totalDuration > .03) {
		        System.out.println(TEST_CLASS.get(x) + "-" + TEST_NAME.get(x) + ": " + (duration / 1000) + " sec (" + (duration / totalDuration * 100) + "%)");
		    }
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static PlugIn getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = PlugIn.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * Adds a thread to the current demo.
	 * 
	 * @param t - The thread to add.
	 */
	public static void addDemoThread(Thread t) {
		_demoThreads.offer(t);
	}
	
	/**
	 * Runs the oldest thread in the current demo.
	 */
	public static void runDemoThread() {
		if (_runningDemoThread) return;
		
		if (getDemoThreadCount() == 0) {
			if (_warnedBeforeExit) {
				_isDead = true;
				System.exit(0);
			} else {
				_warnedBeforeExit = true;
				MessageDialog.openInformation(
						getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
						"End of Demo", "There are no more steps in the demo.");
			}
			
			return;
		}
		
		_runningDemoThread = true;
		_demoThreads.poll().run();
		_runningDemoThread = false;
	}

	/**
	 * @return The number of threads in the demo.
	 */
	public static int getDemoThreadCount() {
		return _demoThreads.size();
	}

	/**
	 * @return true if the demo is complete; false otherwise.
	 */
	public static boolean isDead() {
		return _isDead;
	}
}
