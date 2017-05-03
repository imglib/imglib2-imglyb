package net.imglib2.python;

import org.scijava.command.Command;

public class PythonCommand implements Command
{

	private final Runnable runnable;

	public PythonCommand( final Runnable runnable )
	{
		super();
		this.runnable = runnable;
	}

	@Override
	public void run()
	{
		runnable.run();
	}

	public static < R extends Runnable > PythonCommand makeCommand( final R runnable )
	{
		return new PythonCommand( runnable );
	}

}
