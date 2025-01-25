package com.antlarac.UiElements;
import javax.swing.text.Element;
import javax.swing.text.View;

public class WrappableParagraphView extends javax.swing.text.html.ParagraphView {

        public WrappableParagraphView(Element elem) {
            super(elem);
        }

        @Override
        public float getMinimumSpan(int axis) {
            return View.X_AXIS == axis ? 0 : super.getMinimumSpan(axis);
        }
    }
