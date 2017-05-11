package net.imglib2.python;

import org.scijava.command.Command;
import org.scijava.command.CommandInfo;

public class PythonCommandInfo extends CommandInfo
{

	private final RunnableCommand command;

	public PythonCommandInfo( final RunnableCommand command )
	{
		super( RunnableCommand.class );
		this.command = command;
	}

	@Override
	public Command createInstance()
	{
		return command;
	}

	public static class RunnableCommand implements Command
	{

		private final Runnable r;

		public RunnableCommand( final Runnable r )
		{
			super();
			this.r = r;
		}

		@Override
		public void run()
		{
			r.run();
		}

	}


}
