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

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{

    // Actions - important to allocate these only in makeActions, and then use
    // them
    // in the fill methods. This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction exitAction;

    private IWorkbenchAction aboutAction;

    private IWorkbenchAction newWindowAction;

    private IContributionItem showViews = null;

    private IContributionItem newWizards = null;

    public ApplicationActionBarAdvisor ( final IActionBarConfigurer configurer )
    {
        super ( configurer );
    }

    @Override
    protected void makeActions ( final IWorkbenchWindow window )
    {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml
        // file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        this.exitAction = ActionFactory.QUIT.create ( window );
        register ( this.exitAction );

        this.aboutAction = ActionFactory.ABOUT.create ( window );
        register ( this.aboutAction );

        this.newWindowAction = ActionFactory.OPEN_NEW_WINDOW.create ( window );
        register ( this.newWindowAction );

        register ( ActionFactory.NEW_WIZARD_DROP_DOWN.create ( window ) );
        register ( ActionFactory.NEW.create ( window ) );
        register ( ActionFactory.INTRO.create ( window ) );

        this.showViews = ContributionItemFactory.VIEWS_SHORTLIST.create ( window );
        this.newWizards = ContributionItemFactory.NEW_WIZARD_SHORTLIST.create ( window );

        register ( ActionFactory.NEW_EDITOR.create ( window ) );
        register ( ActionFactory.PREFERENCES.create ( window ) );

    }

    @Override
    protected void fillMenuBar ( final IMenuManager menuBar )
    {
        final MenuManager fileMenu = new MenuManager ( "&File", IWorkbenchActionConstants.M_FILE );
        final MenuManager windowMenu = new MenuManager ( "&Window", IWorkbenchActionConstants.M_WINDOW );
        final MenuManager helpMenu = new MenuManager ( "&Help", IWorkbenchActionConstants.M_HELP );
        final MenuManager fileNewMenu = new MenuManager ( "&New", IWorkbenchActionConstants.NEW_EXT );
        final MenuManager windowNewMenu = new MenuManager ( "Show &View", IWorkbenchActionConstants.SHOW_EXT );

        menuBar.add ( fileMenu );
        // Add a group marker indicating where action set menus will appear.
        menuBar.add ( new GroupMarker ( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menuBar.add ( windowMenu );
        menuBar.add ( helpMenu );

        // File
        fileMenu.add ( this.newWindowAction );
        fileMenu.add ( new Separator () );
        fileMenu.add ( fileNewMenu );
        fileMenu.add ( getAction ( ActionFactory.NEW_EDITOR.getId () ) );
        fileMenu.add ( new GroupMarker ( IWorkbenchActionConstants.OPEN_EXT ) );

        fileMenu.add ( new Separator () );
        fileMenu.add ( this.exitAction );

        fileNewMenu.add ( this.newWizards );

        // Window
        windowNewMenu.add ( this.showViews );
        windowMenu.add ( windowNewMenu );
        windowMenu.add ( getAction ( ActionFactory.PREFERENCES.getId () ) );

        // Help
        helpMenu.add ( this.aboutAction );
        helpMenu.add ( getAction ( ActionFactory.INTRO.getId () ) );
    }

    @Override
    protected void fillCoolBar ( final ICoolBarManager coolBar )
    {
        final IToolBarManager toolbar = new ToolBarManager ( SWT.FLAT | SWT.RIGHT );
        coolBar.add ( new ToolBarContributionItem ( toolbar, "main" ) );
        toolbar.add ( getAction ( ActionFactory.NEW_WIZARD_DROP_DOWN.getId () ) );
    }

}
