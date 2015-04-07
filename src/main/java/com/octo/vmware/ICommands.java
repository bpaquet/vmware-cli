package com.octo.vmware;

import com.octo.vmware.commands.AddDisk;
import com.octo.vmware.commands.AutostartDisable;
import com.octo.vmware.commands.AutostartEnable;
import com.octo.vmware.commands.AutostartInfo;
import com.octo.vmware.commands.CancelTask;
import com.octo.vmware.commands.ClearFinishedTasks;
import com.octo.vmware.commands.CopyVm;
import com.octo.vmware.commands.DeleteFromDisk;
import com.octo.vmware.commands.ListDatastores;
import com.octo.vmware.commands.ListResourcePools;
import com.octo.vmware.commands.ListTasks;
import com.octo.vmware.commands.ListVms;
import com.octo.vmware.commands.MountVmwareTools;
import com.octo.vmware.commands.MoveIntoResourcePool;
import com.octo.vmware.commands.PowerOff;
import com.octo.vmware.commands.PowerOn;
import com.octo.vmware.commands.RebootGuest;
import com.octo.vmware.commands.ReconfigureNetworkCards;
import com.octo.vmware.commands.RemoveDisk;
import com.octo.vmware.commands.Reset;
import com.octo.vmware.commands.SetCpu;
import com.octo.vmware.commands.SetGuestOs;
import com.octo.vmware.commands.SetMem;
import com.octo.vmware.commands.Show;
import com.octo.vmware.commands.ShutdownGuest;
import com.octo.vmware.commands.UnMountVmwareTools;
import com.octo.vmware.commands.Revert;

public interface ICommands {

	public static final ICommand[] COMMANDS = { new ListVms(), new Show(), new PowerOff(), new PowerOn(),
			new ShutdownGuest(), new RebootGuest(), new Reset(), new MountVmwareTools(), new UnMountVmwareTools(),
			new MoveIntoResourcePool(), new ListResourcePools(), new AutostartInfo(), new AutostartEnable(),
			new AutostartDisable(), new DeleteFromDisk(), new ListTasks(), new CancelTask(), new CopyVm(),
			new ClearFinishedTasks(), new SetCpu(), new SetMem(), new SetGuestOs(), new ReconfigureNetworkCards(),
			new ListDatastores(), new AddDisk(), new RemoveDisk(), new Revert()};

}
