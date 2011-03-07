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

package org.openscada.da.rcp.LocalTestServer.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.openscada.da.rcp.LocalTestServer.Activator;

public class StylesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public StylesPreferencePage ()
    {
        super ( FieldEditorPreferencePage.GRID );
        setPreferenceStore ( Activator.getDefault ().getPreferenceStore () );
        setDescription ( "Settings for the testing servers. Please not that changes will only take effect after restarting!" );
    }

    @Override
    protected void createFieldEditors ()
    {
        IntegerFieldEditor editor;
        editor = new IntegerFieldEditor ( PreferenceConstants.P_PORT_SIM, "Simulation Server Port", getFieldEditorParent () );
        editor.setValidRange ( 0, Short.MAX_VALUE );
        addField ( editor );

        editor = new IntegerFieldEditor ( PreferenceConstants.P_PORT_TEST, "Test Server Port", getFieldEditorParent () );
        editor.setValidRange ( 0, Short.MAX_VALUE );
        addField ( editor );
    }

    public void init ( final IWorkbench workbench )
    {
    }

}