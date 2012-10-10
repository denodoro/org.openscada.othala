/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.ui.test.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.ui.connection.AbstractConnectionDiscoverer;
import org.openscada.core.ui.connection.ConnectionDescriptor;
import org.openscada.da.rcp.LocalTestServer.Activator;
import org.openscada.da.rcp.LocalTestServer.ServerManager;
import org.openscada.da.rcp.LocalTestServer.ServerManager.Entry;
import org.openscada.da.rcp.LocalTestServer.ServerManager.ServerListener;

public class ServerManagerDiscoverer extends AbstractConnectionDiscoverer implements ServerListener
{

    private final ServerManager serverManager;

    public ServerManagerDiscoverer ()
    {
        this.serverManager = Activator.getDefault ().getServerManager ();
        this.serverManager.addListener ( this );
    }

    @Override
    public synchronized void dispose ()
    {
        this.serverManager.removeListener ( this );
        super.dispose ();
    }

    private ConnectionDescriptor convert ( final String description, final ConnectionInformation connectionInformation )
    {
        return new ConnectionDescriptor ( connectionInformation, null, description );
    }

    private final Map<Entry, ConnectionDescriptor[]> descMap = new HashMap<ServerManager.Entry, ConnectionDescriptor[]> ();

    @Override
    public void serverAdded ( final Entry entry )
    {
        serverRemoved ( entry );

        final Set<ConnectionDescriptor> descriptors = new HashSet<ConnectionDescriptor> ();
        for ( final ConnectionInformation info : entry.getEndpoints () )
        {
            descriptors.add ( convert ( entry.getDescription (), info ) );
        }

        final ConnectionDescriptor[] added = descriptors.toArray ( new ConnectionDescriptor[0] );
        this.descMap.put ( entry, added );
        fireDiscoveryUpdate ( added, null );
    }

    @Override
    public void serverRemoved ( final Entry entry )
    {
        final ConnectionDescriptor[] desc = this.descMap.remove ( entry );
        if ( desc != null && desc.length > 0 )
        {
            fireDiscoveryUpdate ( null, desc );
        }
    }
}
