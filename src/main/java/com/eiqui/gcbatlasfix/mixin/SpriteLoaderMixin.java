package com.eiqui.gcbatlasfix.mixin;

import com.eiqui.gcbatlasfix.AtlasFix;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {
    @Shadow
    @Final
    private Identifier location;
    @Shadow
    @Final
    private int maxSupportedTextureSize;

    @ModifyVariable(method = "stitch", at = @At("HEAD"), argsOnly = true)
    private List<SpriteContents> gcbatlasfix$fit(List<SpriteContents> sprites, @Local(argsOnly = true) int mipLevel) {
        return AtlasFix.fit(location, sprites, maxSupportedTextureSize, mipLevel);
    }
}
