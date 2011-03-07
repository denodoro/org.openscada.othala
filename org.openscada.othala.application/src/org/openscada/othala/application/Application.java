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

package org.openscada.othala.application;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication
{

    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    public Object start ( final IApplicationContext context ) throws Exception
    {
        final Display display = PlatformUI.createDisplay ();
        try
        {
            final int returnCode = PlatformUI.createAndRunWorkbench ( display, new ApplicationWorkbenchAdvisor () );
            if ( returnCode == PlatformUI.RETURN_RESTART )
            {
                return IApplication.EXIT_RESTART;
            }
            else
            {
                return IApplication.EXIT_OK;
            }
        }
        finally
        {
            display.dispose ();
        }

    }

    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop ()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench ();
        if ( workbench == null )
        {
            return;
        }
        final Display display = workbench.getDisplay ();
        display.syncExec ( new Runnable () {
            public void run ()
            {
                if ( !display.isDisposed () )
                {
                    workbench.close ();
                }
            }
        } );
    }
}