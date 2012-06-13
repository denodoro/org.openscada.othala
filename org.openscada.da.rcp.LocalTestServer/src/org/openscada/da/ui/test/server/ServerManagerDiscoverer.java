package org.openscada.da.ui.test.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.ui.connection.AbstractConnectionDiscoverer;
import org.openscada.core.ui.connection.ConnectionDescriptor;
import org.openscada.core.ui.connection.ConnectionDiscoverer;
import org.openscada.da.rcp.LocalTestServer.Activator;
import org.openscada.da.rcp.LocalTestServer.ServerManager;
import org.openscada.da.rcp.LocalTestServer.ServerManager.Entry;
import org.openscada.da.rcp.LocalTestServer.ServerManager.ServerListener;

public class ServerManagerDiscoverer extends AbstractConnectionDiscoverer implements ConnectionDiscoverer, ServerListener
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
