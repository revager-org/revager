package org.revager.tools;

import com.itextpdf.text.BaseColor;

import java.awt.*;

public class UpdateTools {
    public static Color baseColorToColor(BaseColor baseColor){
        return new Color(baseColor.getRGB());
    }
}
