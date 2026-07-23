package cn.oyzh.easydameng.fx.svg.glyph;// package cn.oyzh.easydameng.fx.svg.glyph;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2025-02-14
 */
public class WarningSVGGlyph extends SVGGlyph {

    public WarningSVGGlyph() {
        this.setUrl("/font/warning.svg");
    }

    public WarningSVGGlyph(String size) {
        this();
        this.setSizeStr(size);
    }
}
