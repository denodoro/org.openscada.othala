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

package org.openscada.da.rcp.LocalTestServer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.rcp.LocalTestServer.preferences.PreferenceConstants;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.da.rcp.LocalTestServer";

    // The shared instance
    private static Activator plugin;

    private ServerManager serverManager;

    /**
     * The constructor
     */
    public Activator ()
    {
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.serverManager = new ServerManager ();

        super.start ( context );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.serverManager.dispose ();

        plugin = null;
        super.stop ( context );
    }

    public ServerManager getServerManager ()
    {
        return this.serverManager;
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault ()
    {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor ( final String path )
    {
        return imageDescriptorFromPlugin ( PLUGIN_ID, path );
    }

    protected int getPort ( final String preferenceName )
    {
        return getPreferenceStore ().getInt ( preferenceName );
    }

    public void startLocalServer () throws Exception
    {
        final org.openscada.da.server.test.Hive testHive = new org.openscada.da.server.test.Hive ();

        try
        {
            exportServer ( testHive, getPort ( PreferenceConstants.P_PORT_TEST ), "Local test server" );
        }
        catch ( final Throwable e )
        {
            logger.error ( "failed to start", e );
            notifyServerError ( e );
        }

    }

    public void startLocalSimServer () throws Exception
    {
        final org.openscada.da.core.server.Hive hive = new org.openscada.da.server.simulation.component.Hive ();
        exportServer ( hive, getPort ( PreferenceConstants.P_PORT_SIM ), "Local simulation server" );
    }

    private void exportServer ( final Hive hive, final int port, final String description ) throws Exception
    {
        final List<ConnectionInformation> endpoints = new LinkedList<ConnectionInformation> ();

        endpoints.add ( ConnectionInformation.fromURI ( "da:net://0.0.0.0:" + port ) );
        endpoints.add ( ConnectionInformation.fromURI ( "da:net://0.0.0.0:" + port + "?socketImpl=VMPIPE" ) );

        this.serverManager.startServer ( hive, endpoints, description );
    }

    private void notifyServerError ( final Throwable t )
    {
        final Shell shell = getWorkbench ().getActiveWorkbenchWindow ().getShell ();
        final IStatus status = new OperationStatus ( IStatus.ERROR, PLUGIN_ID, 0, "Server execution failed", t );

        if ( !shell.isDisposed () )
        {
            shell.getDisplay ().asyncExec ( new Runnable () {

                @Override
                public void run ()
                {
                    if ( !shell.isDisposed () )
                    {
                        ErrorDialog.openError ( shell, null, "Server execution failed", status );
                    }
                }
            } );
        }
    }
}
