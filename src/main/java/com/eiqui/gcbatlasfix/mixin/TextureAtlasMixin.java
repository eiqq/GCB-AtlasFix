package com.eiqui.gcbatlasfix.mixin;

import com.eiqui.gcbatlasfix.Config;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** 구형 NVIDIA 는 proxy 탐색에 32768 이 된다고 답하고 실제 생성은 실패해서, 설정값으로 자른다 */
@Mixin(TextureAtlas.class)
public class TextureAtlasMixin {
    @ModifyReturnValue(method = "maxSupportedTextureSize", at = @At("RETURN"))
    private int gcbatlasfix$cap(int original) {
        return Math.min(original, Config.INSTANCE.maxAtlasSize);
    }
}
