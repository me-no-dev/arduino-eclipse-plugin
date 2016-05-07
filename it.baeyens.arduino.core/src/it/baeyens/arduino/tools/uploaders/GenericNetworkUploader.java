package it.baeyens.arduino.tools.uploaders;

import it.baeyens.arduino.common.Const;

import java.io.IOException;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.envvar.EnvironmentVariable;
import org.eclipse.cdt.core.envvar.IContributedEnvironment;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariableManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class GenericNetworkUploader implements IRealUpload {
  private String mycConf;
  private String myUploadTool;
  private MessageConsole myConsole;
  private String myAdderss;
  private String myPort;
  private String myPassword;

  /**
 * @param Project unused here 
 */
GenericNetworkUploader(IProject Project, String cConf, String UploadTool, MessageConsole Console, String address, String port, String password) {
    this.mycConf = cConf;
    this.myUploadTool = UploadTool;
    this.myConsole = Console;
    this.myAdderss = address;
    this.myPort = port;
    this.myPassword = password;
  }

  @Override
  public boolean uploadUsingPreferences(IFile hexFile, IProject project, boolean usingProgrammer, IProgressMonitor monitor) {
    IEnvironmentVariableManager envManager = CCorePlugin.getDefault().getBuildEnvironmentManager();
    IContributedEnvironment contribEnv = envManager.getContributedEnvironment();
    ICProjectDescription prjDesc = CoreModel.getDefault().getProjectDescription(project);
    ICConfigurationDescription configurationDescription = prjDesc.getConfigurationByName(this.mycConf);

    IEnvironmentVariable var = new EnvironmentVariable(Const.ENV_KEY_SERIAL_PORT, this.myAdderss);
    contribEnv.addVariable(var, configurationDescription);
    var = new EnvironmentVariable(Const.ENV_KEY_NETWORK_PORT, this.myPort);
    contribEnv.addVariable(var, configurationDescription);
    var = new EnvironmentVariable(Const.ENV_KEY_NETWORK_PASSWORD, this.myPassword);
    contribEnv.addVariable(var, configurationDescription);

    //MessageConsoleStream output = this.myConsole.newMessageStream();
    //output.println("Tool: "+this.myUploadTool);
    //output.println("Address: "+this.myAdderss);
    //output.println("Port: "+this.myPort);
    //output.println("Auth: "+ this.myPassword);

    String command = "";
    try {
	command = envManager.getVariable("A.TOOLS." + this.myUploadTool.toUpperCase() + ".UPLOAD.NETWORK_PATTERN", configurationDescription, true).getValue();
    } catch (Exception e) {
	//ignore
    }
    
    if(command.contentEquals("")){
      try {
	  command = envManager.getVariable("A.TOOLS." + this.myUploadTool.toUpperCase() + ".UPLOAD.PATTERN", configurationDescription, true).getValue();
      } catch (Exception e){
	  //ignore
      }
    }
    
    //output.println("Command: "+ command);
    
    try {
      GenericLocalUploader.RunConsoledCommand(this.myConsole, command, new SubProgressMonitor(monitor, 1));
    } catch (IOException e1) {
      e1.printStackTrace();
      return false;
    }
    return true;
  }

}