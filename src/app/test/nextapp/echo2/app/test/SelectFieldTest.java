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

package nextapp.echo2.app.test;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import junit.framework.TestCase;

/**
 * Unit tests for the <code>nextapp.echo2.app.SelectField</code> component.
 */
public class SelectFieldTest extends TestCase {

    private class ActionHandler implements ActionListener {
        
        private ActionEvent lastEvent;
        
        public void actionPerformed(ActionEvent e) {
            lastEvent = e;
        }
    }
    
    /**
     * Ensures that invoking <code>getSelectedItem</code> on a 
     * <code>SelectField</code> with no selected items returns null.
     */
    public void testGetSelectedItemNull() {
        SelectField selectField = new SelectField();
        assertNull(selectField.getSelectedItem());
    }
    
    /**
     * Test Adding/Removing <code>ActionListener</code>s and
     * receiving events.
     */
    public void testActionListeners() {
        SelectField selectField = new SelectField();
        selectField.setActionCommand("action!");
        
        assertFalse(selectField.hasActionListeners());
        
        ActionHandler actionHandler = new ActionHandler();
        selectField.addActionListener(actionHandler);

        assertTrue(selectField.hasActionListeners());
        
        selectField.processInput(SelectField.INPUT_ACTION, null);
        assertNotNull(actionHandler.lastEvent);
        assertEquals(selectField, actionHandler.lastEvent.getSource());
        assertEquals("action!", actionHandler.lastEvent.getActionCommand());

        selectField.removeActionListener(actionHandler);

        assertFalse(selectField.hasActionListeners());
    }
    
    /**
     * Test property accessors and mutators.
     */
    public void testProperties() {
        SelectField selectField = new SelectField();
        selectField.setActionCommand("action!");
        selectField.setBorder(TestConstants.BORDER_THICK_ORANGE);
        selectField.setHeight(TestConstants.EXTENT_30_PX);
        selectField.setInsets(TestConstants.INSETS_1234);
        selectField.setRolloverEnabled(true);
        selectField.setRolloverBackground(Color.GREEN);
        selectField.setRolloverFont(TestConstants.MONOSPACE_12);
        selectField.setRolloverForeground(Color.YELLOW);
        selectField.setWidth(TestConstants.EXTENT_100_PX);
        assertEquals("action!", selectField.getActionCommand());
        assertEquals(TestConstants.BORDER_THICK_ORANGE, selectField.getBorder());
        assertEquals(Color.GREEN, selectField.getRolloverBackground());
        assertEquals(true, selectField.isRolloverEnabled());
        assertEquals(TestConstants.MONOSPACE_12, selectField.getRolloverFont());
        assertEquals(Color.YELLOW, selectField.getRolloverForeground());
        assertEquals(TestConstants.EXTENT_30_PX, selectField.getHeight());
        assertEquals(TestConstants.INSETS_1234, selectField.getInsets());
        assertEquals(TestConstants.EXTENT_100_PX, selectField.getWidth());
    }
}
