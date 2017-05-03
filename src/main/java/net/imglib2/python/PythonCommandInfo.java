package net.imglib2.python;

import org.scijava.command.Command;
import org.scijava.command.CommandInfo;

public class PythonCommandInfo extends CommandInfo
{

	private final Command command;

	public PythonCommandInfo( final String name, final Command command )
	{
		super( name );
		this.command = command;
	}

	@Override
	public Command createInstance()
	{
		return command;
	}


}
