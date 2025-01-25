package com.antlarac.UiElements;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import static javax.swing.text.html.HTML.Tag.IMPLIED;
import static javax.swing.text.html.HTML.Tag.P;

public class CustomHTMLEditorKit extends HTMLEditorKit {

        private final ViewFactory viewFactory = new HTMLFactory() {
            @Override
            public View create(Element elem) {
                AttributeSet attrs = elem.getAttributes();
                Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
                Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
                if (o instanceof HTML.Tag) {
                    HTML.Tag kind = (HTML.Tag) o;
                    if (IMPLIED == kind) return new WrappableParagraphView(elem); // <pre>
                    if (P == kind) return new WrappableParagraphView(elem); // <p>
                }
                return super.create(elem);
            }
        };

        @Override
        public ViewFactory getViewFactory() {
            return this.viewFactory;
        }
    }


