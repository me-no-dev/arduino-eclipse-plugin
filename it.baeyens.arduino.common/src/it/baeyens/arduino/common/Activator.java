package it.baeyens.arduino.common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

abstract class FamilyJob extends Job {
    static final String MY_FAMILY = "myJobFamily"; //$NON-NLS-1$

    public FamilyJob(String name) {
	super(name);
    }

    @Override
    public boolean belongsTo(Object family) {
	return family == MY_FAMILY;
    }

}

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "it.baeyens.arduino.common"; //$NON-NLS-1$

    private static Activator instance;

    /**
     * The constructor
     */
    public Activator() {
	// no activator code needed
    }

    public static Activator getDefault() {
	return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
     * BundleContext )
     */
    @Override
    public void start(BundleContext context) throws Exception {
	super.start(context);
	instance = this;

	// add required properties for Arduino serial port on linux, if not
	// defined
	if (Platform.getOS().equals(Platform.OS_LINUX) && System.getProperty(Const.ENV_KEY_GNU_SERIAL_PORTS) == null) {
	    System.setProperty(Const.ENV_KEY_GNU_SERIAL_PORTS, Const.ENV_VALUE_GNU_SERIAL_PORTS_LINUX);
	}
	remind();
	return;

    }

    static void remind() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
     * BundleContext )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
	IJobManager jobMan = Job.getJobManager();
	jobMan.cancel(FamilyJob.MY_FAMILY);
	jobMan.join(FamilyJob.MY_FAMILY, null);
	instance = null;
	super.stop(context);
    }

    static boolean isInternetReachable() {
	return true;
    }
}
