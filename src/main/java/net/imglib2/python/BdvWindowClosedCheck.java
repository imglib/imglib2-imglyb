package net.imglib2.python;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BdvWindowClosedCheck extends WindowAdapter
{
	private boolean isClosed = false;

	public boolean isClosed()
	{
		return isClosed;
	}

	public boolean isOpen()
	{
		return !isClosed();
	}

	@Override
	public void windowClosing( final WindowEvent we )
	{
		this.isClosed = true;
	}

}
