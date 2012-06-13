package org.openscada.da.rcp.LocalTestServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.net.Exporter;

public class ServerManager
{
    public class Entry
    {
        private final List<ConnectionInformation> endpoints;

        private final String description;

        private final Hive hive;

        private final List<Exporter> exporters = new LinkedList<Exporter> ();

        public Entry ( final String description, final Hive hive, final List<ConnectionInformation> endpoints ) throws Exception
        {
            this.description = description;
            this.hive = hive;
            this.endpoints = new LinkedList<ConnectionInformation> ();

            hive.start ();

            for ( final ConnectionInformation endpoint : endpoints )
            {
                final Exporter exporter = new Exporter ( hive, endpoint );
                this.endpoints.addAll ( exporter.start () );
                this.exporters.add ( exporter );
            }
        }

        public List<ConnectionInformation> getEndpoints ()
        {
            return this.endpoints;
        }

        public String getDescription ()
        {
            return this.description;
        }

        public void dispose ()
        {
            for ( final Exporter exporter : this.exporters )
            {
                try
                {
                    exporter.stop ();
                }
                catch ( final Exception e )
                {
                    // FIXME: log
                }
            }

            try
            {
                this.hive.stop ();
            }
            catch ( final Exception e )
            {
                // FIXME: log exception
            }

            this.exporters.clear ();
        }
    }

    public interface ServerListener
    {
        public void serverAdded ( Entry entry );

        public void serverRemoved ( Entry entry );
    }

    private final Set<ServerListener> listeners = new LinkedHashSet<ServerManager.ServerListener> ();

    private final Map<Hive, Entry> entries = new HashMap<Hive, Entry> ();

    public ServerManager ()
    {
    }

    public void startServer ( final Hive hive, final List<ConnectionInformation> connectionInformation, final String description ) throws Exception
    {
        if ( this.entries.containsKey ( hive ) )
        {
            throw new AlreadyStartedException ();
        }

        final Entry entry = new Entry ( description, hive, connectionInformation );
        this.entries.put ( hive, entry );
        fireAdd ( entry );
    }

    public void stopServer ( final Hive hive )
    {
        final Entry entry = this.entries.remove ( hive );
        if ( entry != null )
        {
            entry.dispose ();
            fireRemove ( entry );
        }
    }

    protected void fireAdd ( final Entry entry )
    {
        for ( final ServerListener listener : this.listeners )
        {
            listener.serverAdded ( entry );
        }
    }

    protected void fireRemove ( final Entry entry )
    {
        for ( final ServerListener listener : this.listeners )
        {
            listener.serverRemoved ( entry );
        }
    }

    public void addListener ( final ServerListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            // transmit cache
            for ( final Entry entry : this.entries.values () )
            {
                listener.serverAdded ( entry );
            }
        }
    }

    public void removeListener ( final ServerListener listener )
    {
        this.listeners.remove ( listener );
    }

    public void dispose ()
    {
        final Collection<Entry> entries = new ArrayList<ServerManager.Entry> ( this.entries.values () );
        for ( final Entry entry : entries )
        {
            entry.dispose ();
        }

        entries.clear ();
        this.listeners.clear ();
    }
}
