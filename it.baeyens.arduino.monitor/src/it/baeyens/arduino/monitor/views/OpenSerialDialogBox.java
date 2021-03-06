package it.baeyens.arduino.monitor.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import it.baeyens.arduino.common.Const;
import it.baeyens.arduino.common.InstancePreferences;
import it.baeyens.arduino.common.Common;

public class OpenSerialDialogBox extends Dialog {
    @Override
    protected void okPressed() {
	// I need to save these values in local variables as the GUI stuff is
	// deleted after he close
	this.SelectedRate = Integer.parseInt(this.BaudRates.getCombo().getText());
	this.SelectedPort = this.SerialPorts.getCombo().getText();
	this.SelectedDtr = this.DtrCheckbox.getSelection();
	InstancePreferences.setGlobalValue(Const.KEY_SERIAL_RATE, this.BaudRates.getCombo().getText());
	InstancePreferences.setGlobalValue(Const.KEY_SERIAL_PORT, this.SelectedPort);
	super.okPressed();
    }

    private ComboViewer SerialPorts;
    private ComboViewer BaudRates;
    private Button DtrCheckbox;
    // private ComboViewer LineEndings;
    private String SelectedPort;
    private int SelectedRate;
    private boolean SelectedDtr;

    protected OpenSerialDialogBox(Shell parentShell) {
	super(parentShell);

    }

    @Override
    protected Control createDialogArea(Composite parent) {
	GridLayout layout = new GridLayout();
	layout.numColumns = 2;
	// layout.horizontalAlignment = GridData.FILL;
	parent.setLayout(layout);

	// The text fields will grow with the size of the dialog
	GridData gridData = new GridData();
	gridData.grabExcessHorizontalSpace = true;
	gridData.horizontalAlignment = GridData.FILL;

	// Create the serial port combo box to allow to select a serial port
	Label label1 = new Label(parent, SWT.NONE);
	label1.setText(Messages.OpenSerialDialogBox_Serial_port_to_connect_to);

	// If there are no comports allow to provide one
	String[] comPorts = Common.listComPorts();
	if (comPorts.length == 0) {
	    this.SerialPorts = new ComboViewer(parent, SWT.DROP_DOWN);
	} else {
	    this.SerialPorts = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
	}
	this.SerialPorts.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
	this.SerialPorts.setContentProvider(new ArrayContentProvider());
	this.SerialPorts.setLabelProvider(new LabelProvider());
	this.SerialPorts.setInput(comPorts);

	// Create baud rate selection combo box to select the baud rate
	Label label2 = new Label(parent, SWT.NONE);
	label2.setText(Messages.OpenSerialDialogBox_Select_the_baut_rate);
	this.BaudRates = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
	this.BaudRates.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
	this.BaudRates.setContentProvider(new ArrayContentProvider());
	this.BaudRates.setLabelProvider(new LabelProvider());
	this.BaudRates.setInput(Common.listBaudRates());

	this.BaudRates.getCombo().setText(InstancePreferences.getGlobalString(Const.KEY_SERIAL_RATE, Const.EMPTY_STRING));
	this.SerialPorts.getCombo().setText(InstancePreferences.getGlobalString(Const.KEY_SERIAL_PORT, Const.EMPTY_STRING));
	
	this.DtrCheckbox = new Button(parent, SWT.CHECK);
	this.DtrCheckbox.setText(Messages.OpenSerialDialogBox_Dtr);
	this.DtrCheckbox.setSelection(true);
	
	return parent;

    }

    public String GetComPort() {
	return this.SelectedPort;
    }

    public int GetBaudRate() {
	return this.SelectedRate;
    }

	public boolean GetDtr() {
	return this.SelectedDtr;
	}

}
