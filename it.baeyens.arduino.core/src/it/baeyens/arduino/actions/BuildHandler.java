package it.baeyens.arduino.actions;

import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.PlatformUI;

import it.baeyens.arduino.common.Common;
import it.baeyens.arduino.common.Const;
import it.baeyens.arduino.listeners.ProjectExplorerListener;

/**
 * This id a handler to connect the plugin.xml to the code for building the code
 * This method forces a save all before building
 * 
 * @author jan
 * 
 */
class BuildJobHandler extends Job {
    IProject myBuildProject = null;

    public BuildJobHandler(String name) {
	super(name);
    }

    public BuildJobHandler(IProject buildProject) {
	super(Messages.BuildHandler_Build_Code_of_project + buildProject.getName());
	this.myBuildProject = buildProject;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
	try {
	    this.myBuildProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);

	} catch (CoreException e) {
	    Common.log(new Status(IStatus.ERROR, Const.CORE_PLUGIN_ID, Messages.BuildHandler_Failed_to_build, e));
	}
	return Status.OK_STATUS;
    }
}

public class BuildHandler extends AbstractHandler {
    private Job mBuildJob = null;

    public Job getJob() {
	return this.mBuildJob;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	IProject SelectedProjects[] = ProjectExplorerListener.getSelectedProjects();
	switch (SelectedProjects.length) {
	case 0:
	    Common.log(new Status(IStatus.ERROR, Const.CORE_PLUGIN_ID, Messages.BuildHandler_No_Project_found));
	    break;
	default:
	    PlatformUI.getWorkbench().saveAllEditors(false);
	    for (int curProject = 0; curProject < SelectedProjects.length; curProject++) {
		this.mBuildJob = new BuildJobHandler(SelectedProjects[curProject]);
		this.mBuildJob.setPriority(Job.INTERACTIVE);
		this.mBuildJob.schedule();
	    }

	}
	return null;
    }

}
