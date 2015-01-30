/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2009 NextApp, Inc.
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

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.TextArea;
import nextapp.echo2.app.text.TextComponent;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.FocusSupport;
import nextapp.echo2.webcontainer.PartialUpdateManager;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.image.ImageRenderSupport;
import nextapp.echo2.webcontainer.partialupdate.BorderUpdate;
import nextapp.echo2.webcontainer.partialupdate.ColorUpdate;
import nextapp.echo2.webcontainer.partialupdate.InsetsUpdate;
import nextapp.echo2.webcontainer.propertyrender.AlignmentRender;
import nextapp.echo2.webcontainer.propertyrender.FillImageRender;
import nextapp.echo2.webcontainer.propertyrender.BorderRender;
import nextapp.echo2.webcontainer.propertyrender.ColorRender;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webcontainer.propertyrender.FontRender;
import nextapp.echo2.webcontainer.propertyrender.InsetsRender;
import nextapp.echo2.webcontainer.propertyrender.LayoutDirectionRender;
import nextapp.echo2.webrender.ClientProperties;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.servermessage.WindowUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.util.DomUtil;

/**
 * Abstract base synchronization peer for the built-in
 * <code>nextapp.echo2.app.text.TextComponent</code> -derived components.
 * <p>
 * This class should not be extended or used by classes outside of the Echo
 * framework.
 */
public abstract class TextComponentPeer 
implements ActionProcessor, ComponentSynchronizePeer, DomUpdateSupport, FocusSupport, ImageRenderSupport, PropertyUpdateProcessor {

    private static final String IMAGE_ID_BACKGROUND = "background";

    /**
     * Service to provide supporting JavaScript library.
     */
    static final Service TEXT_COMPONENT_SERVICE = JavaScriptService.forResource("Echo.TextComponent",
            "/nextapp/echo2/webcontainer/resource/js/TextComponent.js");

    static {
        WebRenderServlet.getServiceRegistry().add(TEXT_COMPONENT_SERVICE);
    }
    
    /**
     * A <code>PartialUpdateParticipant</code> to update the text of
     * a text component.
     */
    private class TextUpdate
    implements PartialUpdateParticipant {
    
        /**
         * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#canRenderProperty(nextapp.echo2.webcontainer.RenderContext, 
         *      nextapp.echo2.app.update.ServerComponentUpdate)
         */
        public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
            return true;
        }
    
        /**
         * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#renderProperty(
         *      nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate)
         */
        public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
            TextComponent textComponent = (TextComponent) update.getParent();
            String elementId = ContainerInstance.getElementId(textComponent);
            ServerMessage serverMessage = rc.getServerMessage();
            Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
                    "EchoTextComponent.MessageProcessor", "set-text", new String[0], new String[0]);
            Element itemElement = serverMessage.getDocument().createElement("item");
            itemElement.setAttribute("eid", elementId);
            itemElement.setAttribute("text", textComponent.getText());
            itemizedUpdateElement.appendChild(itemElement);
            
        }
    }

    private PartialUpdateManager partialUpdateManager;

    /**
     * Default constructor.
     */
    public TextComponentPeer() {
        partialUpdateManager = new PartialUpdateManager();
        partialUpdateManager.add(TextComponent.PROPERTY_FOREGROUND,
                new ColorUpdate(TextComponent.PROPERTY_FOREGROUND, null, ColorUpdate.CSS_COLOR));
        partialUpdateManager.add(TextComponent.PROPERTY_BACKGROUND,
                new ColorUpdate(TextComponent.PROPERTY_BACKGROUND, null, ColorUpdate.CSS_BACKGROUND_COLOR));
        partialUpdateManager.add(TextComponent.PROPERTY_BORDER,
                new BorderUpdate(TextComponent.PROPERTY_BORDER, null, BorderUpdate.CSS_BORDER));
        partialUpdateManager.add(TextComponent.PROPERTY_INSETS,
                new InsetsUpdate(TextComponent.PROPERTY_INSETS, null, InsetsUpdate.CSS_PADDING));
        partialUpdateManager.add(TextComponent.TEXT_CHANGED_PROPERTY, new TextUpdate());
    }

    /**
     * Creates a base <code>CssStyle</code> for properties common to text
     * components.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param textComponent the text component
     * @return the style
     */
    protected CssStyle createBaseCssStyle(RenderContext rc, TextComponent textComponent) {
        CssStyle cssStyle = new CssStyle();

        boolean renderEnabled = textComponent.isRenderEnabled();

        Border border;
        Color foreground, background;
        Font font;
        FillImage backgroundImage;
        if (!renderEnabled) {
            // Retrieve disabled style information.
            background = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_BACKGROUND);
            backgroundImage = (FillImage) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_BACKGROUND_IMAGE);
            border = (Border) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_BORDER);
            font = (Font) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_FONT);
            foreground = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_FOREGROUND);

            // Fallback to normal styles.
            if (background == null) {
                background = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND);
                if (backgroundImage == null) {
                    // Special case:
                    // Disabled background without disabled background image will render disabled background instead of
                    // normal background image.
                    backgroundImage = (FillImage) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
                }
            }
            if (border == null) {
                border = (Border) textComponent.getRenderProperty(TextComponent.PROPERTY_BORDER);
            }
            if (font == null) {
                font = (Font) textComponent.getRenderProperty(TextComponent.PROPERTY_FONT);
            }
            if (foreground == null) {
                foreground = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_FOREGROUND);
            }
        } else {
            border = (Border) textComponent.getRenderProperty(TextComponent.PROPERTY_BORDER);
            foreground = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_FOREGROUND);
            background = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND);
            font = (Font) textComponent.getRenderProperty(TextComponent.PROPERTY_FONT);
            backgroundImage = (FillImage) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
        }
        
        Alignment alignment = (Alignment) textComponent.getRenderProperty(TextComponent.PROPERTY_ALIGNMENT);
        if (alignment != null) {
            int horizontalAlignment = AlignmentRender.getRenderedHorizontal(alignment, textComponent);
            switch (horizontalAlignment) {
            case Alignment.LEFT:
                cssStyle.setAttribute("text-align", "left");
                break;
            case Alignment.CENTER:
                cssStyle.setAttribute("text-align", "center");
                break;
            case Alignment.RIGHT:
                cssStyle.setAttribute("text-align", "right");
                break;
            }
        }
        
        LayoutDirectionRender.renderToStyle(cssStyle, textComponent.getLayoutDirection(), textComponent.getLocale());
        BorderRender.renderToStyle(cssStyle, border);
        ColorRender.renderToStyle(cssStyle, foreground, background);
        FontRender.renderToStyle(cssStyle, font);
        FillImageRender.renderToStyle(cssStyle, rc, this, textComponent, IMAGE_ID_BACKGROUND, backgroundImage, 
                FillImageRender.FLAG_DISABLE_FIXED_MODE);
        
        InsetsRender.renderToStyle(cssStyle, "padding", (Insets) textComponent.getRenderProperty(TextComponent.PROPERTY_INSETS));
        
        Extent width = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_WIDTH);
        Extent height = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_HEIGHT);

        if (width != null) {
            cssStyle.setAttribute("width", ExtentRender.renderCssAttributeValue(width));
        }

        if (height != null) {
            cssStyle.setAttribute("height", ExtentRender.renderCssAttributeValue(height));
        }
        return cssStyle;
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(nextapp.echo2.app.Component)
     */
    public String getContainerId(Component child) {
        throw new UnsupportedOperationException("Component does not support children.");
    }

    /**
     * @see nextapp.echo2.webcontainer.image.ImageRenderSupport#getImage(nextapp.echo2.app.Component,
     *      java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        if (IMAGE_ID_BACKGROUND.equals(imageId)) {
            FillImage backgroundImage;
            if (component.isRenderEnabled()) {
                backgroundImage = (FillImage) component.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
            } else {
                backgroundImage = (FillImage) component.getRenderProperty(TextComponent.PROPERTY_DISABLED_BACKGROUND_IMAGE);
                if (backgroundImage == null) {
                    backgroundImage = (FillImage) component.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
                }
            }
            if (backgroundImage == null) {
                return null;
            } else {
                return backgroundImage.getImage();
            }
        } else {
            return null;
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance, 
     *      nextapp.echo2.app.Component, org.w3c.dom.Element)
     */
    public void processAction(ContainerInstance ci, Component component, Element actionElement) {
        ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, TextComponent.INPUT_ACTION, null);
    }

    /**
     * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(
     *      nextapp.echo2.webcontainer.ContainerInstance,
     *      nextapp.echo2.app.Component, org.w3c.dom.Element)
     */
    public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
        String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
        if (TextComponent.TEXT_CHANGED_PROPERTY.equals(propertyName)) {
            String propertyValue = DomUtil.getElementText(propertyElement);
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    TextComponent.TEXT_CHANGED_PROPERTY, propertyValue);
        } else if (TextComponent.PROPERTY_HORIZONTAL_SCROLL.equals(propertyName)) {
            try {
                Extent propertyValue = new Extent(Float.valueOf(
                        propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE)).intValue());
                ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                        TextComponent.PROPERTY_HORIZONTAL_SCROLL, propertyValue);
            } catch (Exception e) {}
        } else if (TextComponent.PROPERTY_VERTICAL_SCROLL.equals(propertyName)) {
            try {
                Extent propertyValue = new Extent(Float.valueOf(
                        propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE)).intValue());
                ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                        TextComponent.PROPERTY_VERTICAL_SCROLL, propertyValue);
            } catch (Exception e) {}
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
     *      nextapp.echo2.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        Element domAddElement = DomUpdate.renderElementAdd(rc.getServerMessage());
        DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
        renderHtml(rc, update, htmlFragment, component);
        DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, targetId, htmlFragment);
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate,
     *      nextapp.echo2.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        rc.getServerMessage().addLibrary(TEXT_COMPONENT_SERVICE.getId());
        renderDisposeDirective(rc, (TextComponent) component);
    }

    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to
     * dispose the state of a text component, performing tasks such as
     * registering event listeners on the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param textComponent the <code>TextComponent<code>
     */
    public void renderDisposeDirective(RenderContext rc, TextComponent textComponent) {
        String elementId = ContainerInstance.getElementId(textComponent);
        ServerMessage serverMessage = rc.getServerMessage();
        Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE,
                "EchoTextComponent.MessageProcessor", "dispose", new String[0], new String[0]);
        Element itemElement = serverMessage.getDocument().createElement("item");
        itemElement.setAttribute("eid", elementId);
        itemizedUpdateElement.appendChild(itemElement);
    }

    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to
     * initialize the state of a text component, performing tasks such as
     * registering event listeners on the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param textComponent the <code>TextComponent<code>
     */
    public void renderInitDirective(RenderContext rc, TextComponent textComponent) {
        Extent horizontalScroll = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_HORIZONTAL_SCROLL);
        Extent verticalScroll = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_VERTICAL_SCROLL);
        String elementId = ContainerInstance.getElementId(textComponent);
        ServerMessage serverMessage = rc.getServerMessage();

        Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
                "EchoTextComponent.MessageProcessor", "init", new String[0], new String[0]);
        Element itemElement = serverMessage.getDocument().createElement("item");
        itemElement.setAttribute("eid", elementId);
        if (horizontalScroll != null && horizontalScroll.getValue() != 0) {
            itemElement.setAttribute("horizontal-scroll", ExtentRender.renderCssAttributePixelValue(horizontalScroll, "0"));
        }
        if (verticalScroll != null && verticalScroll.getValue() != 0) {
            itemElement.setAttribute("vertical-scroll", ExtentRender.renderCssAttributePixelValue(verticalScroll, "0"));
        }
        if (textComponent instanceof TextArea) {
            Integer maximumLength = (Integer) textComponent.getProperty(TextComponent.PROPERTY_MAXIMUM_LENGTH);
            if (maximumLength != null) {
                itemElement.setAttribute("maximum-length", maximumLength.toString());
            }
        }
        if (textComponent instanceof TextArea && rc.getContainerInstance().getClientProperties().getBoolean(
                ClientProperties.QUIRK_TEXTAREA_CONTENT)) {
            String value = textComponent.getText();
            if (value != null) {
                itemElement.setAttribute("text", value);
            }
        }
        if (!textComponent.isRenderEnabled()) {
            itemElement.setAttribute("enabled", "false");
        }
        if (textComponent.hasActionListeners()) {
            itemElement.setAttribute("server-notify", "true");
        }

        itemizedUpdateElement.appendChild(itemElement);
    }

    /**
     * @see nextapp.echo2.webcontainer.FocusSupport#renderSetFocus(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.Component)
     */
    public void renderSetFocus(RenderContext rc, Component component) {
        WindowUpdate.renderSetFocus(rc.getServerMessage(), ContainerInstance.getElementId(component));
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        boolean fullReplace = false;
        if (update.hasUpdatedProperties()) {
            if (!partialUpdateManager.canProcess(rc, update)) {
                fullReplace = true;
            }
        }

        if (fullReplace) {
            // Perform full update.
            DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
            renderAdd(rc, update, targetId, update.getParent());
        } else {
            partialUpdateManager.process(rc, update);
        }

        return false;
    }
}