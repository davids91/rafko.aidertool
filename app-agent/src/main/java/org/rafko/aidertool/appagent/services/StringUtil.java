package org.rafko.aidertool.appagent.services;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StringUtil {
    public static float getStringWidth(String input, Font fnt){
        final Text internal = new Text(input);
        internal.setFont(fnt);
        return (float) internal.getLayoutBounds().getWidth();
    }
}
