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

package nextapp.echo2.webcontainer.syncpeer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.LayoutData;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.layout.TableCellLayoutData;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.PropertyRenderRegistry;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.SynchronizePeer;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webcontainer.propertyrender.BorderRender;
import nextapp.echo2.webcontainer.propertyrender.CellLayoutDataRender;
import nextapp.echo2.webcontainer.propertyrender.ColorRender;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webcontainer.propertyrender.FontRender;
import nextapp.echo2.webcontainer.propertyrender.InsetsRender;
import nextapp.echo2.webrender.clientupdate.DomUpdate;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.server.ClientProperties;

/**
 * Synchronization peer for <code>nextapp.echo2.app.Table</code> components.
 * <p>
 * This class should not be extended or used by classes outside of the
 * Echo framework.
 */
public class TablePeer 
implements DomUpdateSupport, SynchronizePeer {

    protected PropertyRenderRegistry propertyRenderRegistry;
    
    /**
     * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
     */
    public String getContainerId(Component child) {
        return ContainerInstance.getElementId(child.getParent()) + "_cell_" + child.getParent().indexOf(child);
    }
    
    /**
     * Returns the <code>TableCellLayoutData</code> of the given child,
     * or null if it does not provide <code>TableCellLayoutData</code>.
     * 
     * @param child the child component
     * @return the layout data
     */
    private TableCellLayoutData getLayoutData(Component child) {
        LayoutData layoutData = (LayoutData) child.getRenderProperty(Table.PROPERTY_LAYOUT_DATA);
        if (layoutData instanceof TableCellLayoutData) {
            return (TableCellLayoutData) layoutData;
        } else {
            return null;
        }
    }
    
    /**
     * @see nextapp.echo2.webcontainer.SynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String, nextapp.echo2.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        Element contentElement = DomUpdate.createDomAdd(rc.getServerMessage(), targetId);
        renderHtml(rc, update, contentElement, component);
    }
    
    /**
     * Renders a child component.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param update the update
     * @param parentElement the HTML element which should contain the child
     * @param child the child component to render
     */
    private void renderAddChild(RenderContext rc, ServerComponentUpdate update, Element parentElement, Component child) {
        SynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
        if (syncPeer instanceof DomUpdateSupport) {
            ((DomUpdateSupport) syncPeer).renderHtml(rc, update, parentElement, child);
        } else {
            syncPeer.renderAdd(rc, update, getContainerId(child), child);
        }
    }
    
    /**
     * @see nextapp.echo2.webcontainer.SynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, nextapp.echo2.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
    }
    
    /**
     * @see nextapp.echo2.webcontainer.DomUpdateSupport#renderHtml(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, org.w3c.dom.Element, nextapp.echo2.app.Component)
     */
    public void renderHtml(RenderContext rc, ServerComponentUpdate update, Element parentElement, Component component) {
        Table table = (Table) component;
        Border border = (Border) table.getRenderProperty(Table.PROPERTY_BORDER);
        Extent borderSize = border == null ? null : border.getSize();

        String elementId = ContainerInstance.getElementId(table);
        
        Document document = parentElement.getOwnerDocument();
        Element tableElement = document.createElement("table");
        tableElement.setAttribute("id", elementId);

        CssStyle tableCssStyle = new CssStyle();
        tableCssStyle.setAttribute("border-collapse", "collapse");
        
        Insets tableInsets = (Insets) table.getRenderProperty(Table.PROPERTY_INSETS);
        String defaultInsetsAttributeValue = tableInsets == null ? "0px" : InsetsRender.renderCssAttributeValue(tableInsets);
        
        ColorRender.renderToStyle(tableCssStyle, component);
        FontRender.renderToStyle(tableCssStyle, component);
        BorderRender.renderToStyle(tableCssStyle, border);
        if (borderSize != null) {
            if (!rc.getContainerInstance().getClientProperties().getBoolean(
                    ClientProperties.QUIRK_CSS_BORDER_COLLAPSE_MARGIN)) {
                tableCssStyle.setAttribute("margin", ExtentRender.renderCssAttributeValueHalf(borderSize));
            }
        }
        ExtentRender.renderToStyle(tableCssStyle, "width", (Extent) table.getProperty(Table.PROPERTY_WIDTH));
        tableElement.setAttribute("style", tableCssStyle.renderInline());
        
        parentElement.appendChild(tableElement);
        
        Element tbodyElement = document.createElement("tbody");
        tbodyElement.setAttribute("id", elementId + "_tbody");
        tableElement.appendChild(tbodyElement);

        if (table.isHeaderVisible()) {
            renderRow(rc, update, tbodyElement, table, Table.HEADER_ROW, defaultInsetsAttributeValue);
        }
        
        int rows = table.getModel().getRowCount();
        for (int rowIndex = 0; rowIndex < rows; ++rowIndex) {
            renderRow(rc, update, tbodyElement, table, rowIndex, defaultInsetsAttributeValue);
        }
    }
    
    /**
     * Renders a single row of a table.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param update the <code>ServerComponentUpdate</code> being processed
     * @param tbodyElement the <code>tbody</code> element to which to append 
     *        the rendered content
     * @param table the <code>Table</code> being rendered
     * @param rowIndex the row to render
     * @param defaultInsetsAttributeValue the default CSS padding attribute value
     */
    private void renderRow(RenderContext rc, ServerComponentUpdate update, Element tbodyElement, Table table, int rowIndex,
            String defaultInsetsAttributeValue) {
        Document document = tbodyElement.getOwnerDocument();
        
        Element trElement = document.createElement("tr");
        tbodyElement.appendChild(trElement);
        String elementId = ContainerInstance.getElementId(table);
        
        int columns = table.getColumnModel().getColumnCount();
        for (int columnIndex = 0; columnIndex < columns; ++columnIndex) {
            Component childComponent = table.getCellComponent(columnIndex, rowIndex);
            Element tdElement = document.createElement("td");
            tdElement.setAttribute("id", elementId + "_cell_" + childComponent.getId());
            
            CssStyle tdCssStyle = new CssStyle();
            BorderRender.renderToStyle(tdCssStyle, (Border) table.getRenderProperty(Table.PROPERTY_BORDER));
            CellLayoutDataRender.renderToStyle(tdCssStyle, getLayoutData(childComponent), defaultInsetsAttributeValue);
            tdElement.setAttribute("style", tdCssStyle.renderInline());
            
            trElement.appendChild(tdElement);
            renderAddChild(rc, update, tdElement, childComponent);
        }
    }
    
    /**
     * @see nextapp.echo2.webcontainer.SynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        DomUpdate.createDomRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
        renderAdd(rc, update, targetId, update.getParent());
        return true;
    }
}