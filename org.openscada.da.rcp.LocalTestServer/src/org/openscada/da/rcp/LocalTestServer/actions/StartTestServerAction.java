/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.rcp.LocalTestServer.actions;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.openscada.da.rcp.LocalTestServer.Activator;
import org.openscada.da.rcp.LocalTestServer.AlreadyStartedException;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartTestServerAction implements IWorkbenchWindowActionDelegate
{

    private final static Logger logger = LoggerFactory.getLogger ( StartTestServerAction.class );

    private IWorkbenchWindow window = null;

    public void dispose ()
    {
    }

    public void init ( final IWorkbenchWindow window )
    {
        this.window = window;
    }

    public void run ( final IAction action )
    {
        logger.debug ( "Try to start local server" );

        IStatus status = null;
        try
        {
            Activator.getDefault ().startLocalServer ();
        }
        catch ( final ClassNotFoundException e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Unable to find hive class", e );
        }
        catch ( final InstantiationException e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Unable to instantiate hive class", e );
        }
        catch ( final IllegalAccessException e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Access violation accessing hive class", e );
        }
        catch ( final IOException e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "IO Error", e );
        }
        catch ( final AlreadyStartedException e )
        {
            status = new OperationStatus ( IStatus.WARNING, Activator.PLUGIN_ID, 0, "Local server was already started", e );
        }
        catch ( final ConfigurationError e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Configuration error", e );
        }
        catch ( final XmlException e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "XML Error", e );
        }
        catch ( final Throwable e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Unknown error", e );
        }
        if ( status != null )
        {
            ErrorDialog.openError ( this.window.getShell (), null, "Failed to start local server", status );
        }
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        // we don't care about a selection
    }

}
