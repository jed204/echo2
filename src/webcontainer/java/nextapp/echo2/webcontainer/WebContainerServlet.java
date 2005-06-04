/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2005 NextApp, Inc.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

package nextapp.echo2.webcontainer;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.webrender.ServiceRegistry;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.SessionExpiredService;

/**
 * Web container <code>HttpServlet</code> implementation.
 * An Echo application should provide an derivative of this
 * class which is registered in the web application
 * deployment descriptor.
 */
public abstract class WebContainerServlet extends WebRenderServlet {

    /**
     * Default constructor.
     */
    public WebContainerServlet() {
        super();
        ServiceRegistry serviceRegistry = getServiceRegistry();
        
        //BUGBUG.  This method of registering services is AWFUL....need automatic discovery like everything else,
        // especially considering 90% of Echo2 services are global.
        serviceRegistry.add(NewInstanceService.INSTANCE);
        serviceRegistry.add(AsyncMonitorService.INSTANCE);
        serviceRegistry.add(SessionExpiredService.INSTANCE);
        serviceRegistry.add(ContainerSynchronizeService.INSTANCE);
        serviceRegistry.add(WindowHtmlService.INSTANCE);
    }
    
    /**
     * Creates a new <code>ApplicationInstance</code> for visitor to an 
     * application.
     * 
     * @return a new <code>ApplicationInstance</code>
     */
    public abstract ApplicationInstance newApplicationInstance();
}
