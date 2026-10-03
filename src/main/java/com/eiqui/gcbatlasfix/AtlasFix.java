package com.eiqui.gcbatlasfix;

import com.eiqui.gcbatlasfix.mixin.SpriteContentsAccessor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.TextureFilteringMethod;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** 아틀라스에 다 안 들어가면 바닐라 외 스프라이트를 절반씩 줄여 들어갈 때까지 반복한다. */
public final class AtlasFix {
    public static final Logger LOGGER = LoggerFactory.getLogger("gcbatlasfix");
    private static final int MAX_PASSES = 6;

    private AtlasFix() {
    }

    public static List<SpriteContents> fit(Identifier atlas, List<SpriteContents> sprites, int maxSize, int mipLevel) {
        int anisotropy = anisotropy();
        if (fits(sprites, maxSize, mipLevel, anisotropy)) {
            return sprites;
        }
        Config config = Config.INSTANCE;
        List<SpriteContents> current = new ArrayList<>(sprites);
        for (int pass = 1; pass <= MAX_PASSES; pass++) {
            int halved = 0;
            for (int i = 0; i < current.size(); i++) {
                SpriteContents sprite = current.get(i);
                SpriteContents half = halve(sprite, config);
                if (half != null) {
                    current.set(i, half);
                    sprite.close();
                    halved++;
                }
            }
            boolean ok = fits(current, maxSize, mipLevel, anisotropy);
            LOGGER.warn("[GCB Atlas Fix] {}: {}x{} 안에 안 들어가서 스프라이트 {}개를 1/{} 크기로 줄임 → {}",
                    atlas, maxSize, maxSize, halved, 1 << pass, ok ? "들어감" : "아직 넘침");
            if (ok || halved == 0) {
                break;
            }
        }
        return current;
    }

    private static boolean fits(List<SpriteContents> sprites, int maxSize, int mipLevel, int anisotropy) {
        Stitcher<SpriteContents> stitcher = new Stitcher<>(maxSize, maxSize, mipLevel, anisotropy);
        for (SpriteContents sprite : sprites) {
            stitcher.registerSprite(sprite);
        }
        try {
            stitcher.stitch();
        } catch (StitcherException e) {
            return false;
        }
        return stitcher.getWidth() <= maxSize && stitcher.getHeight() <= maxSize; // 바닐라 expand 는 너비가 최대에 닿으면 높이를 최대보다 크게 늘리기도 해서 예외 없이 넘침
    }

    private static int anisotropy() {
        var options = Minecraft.getInstance().options;
        return options.textureFiltering().get() == TextureFilteringMethod.ANISOTROPIC ? options.maxAnisotropyBit().get() : 0;
    }

    /** 한 프레임을 가로세로 절반으로. 바닐라·너무 작은·홀수 크기는 null */
    private static SpriteContents halve(SpriteContents sprite, Config config) {
        if (config.keeps(sprite.name().getNamespace())) return null;
        int w = sprite.width(), h = sprite.height();
        if (w / 2 < config.minSpriteSize || h / 2 < config.minSpriteSize || w % 2 != 0 || h % 2 != 0) return null;
        NativeImage src = ((SpriteContentsAccessor) sprite).gcbatlasfix$getOriginalImage();
        NativeImage dst = new NativeImage(src.getWidth() / 2, src.getHeight() / 2, false);
        for (int y = 0; y < dst.getHeight(); y++) {
            for (int x = 0; x < dst.getWidth(); x++) {
                dst.setPixel(x, y, average(src.getPixel(2 * x, 2 * y), src.getPixel(2 * x + 1, 2 * y),
                        src.getPixel(2 * x, 2 * y + 1), src.getPixel(2 * x + 1, 2 * y + 1)));
            }
        }
        SpriteMetadata meta = (SpriteMetadata) sprite;
        return new SpriteContents(sprite.name(), new FrameSize(w / 2, h / 2), dst,
                meta.gcbatlasfix$animation(), meta.gcbatlasfix$additional(), meta.gcbatlasfix$texture());
    }

    /** 2×2 평균. 투명 픽셀 색이 번지지 않게 색은 알파 가중 */
    private static int average(int a, int b, int c, int d) {
        int[] px = {a, b, c, d};
        int alpha = 0, r = 0, g = 0, bl = 0;
        for (int p : px) {
            int al = p >>> 24;
            alpha += al;
            r += ((p >> 16) & 0xFF) * al;
            g += ((p >> 8) & 0xFF) * al;
            bl += (p & 0xFF) * al;
        }
        if (alpha == 0) return 0;
        return ((alpha / 4) << 24) | ((r / alpha) << 16) | ((g / alpha) << 8) | (bl / alpha);
    }
}
