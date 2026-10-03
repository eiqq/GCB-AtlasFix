package com.eiqui.gcbatlasfix.mixin;

import com.eiqui.gcbatlasfix.SpriteMetadata;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(SpriteContents.class)
public class SpriteContentsMixin implements SpriteMetadata {
    @Unique
    private Optional<AnimationMetadataSection> gcbatlasfix$animation = Optional.empty();
    @Unique
    private List<MetadataSectionType.WithValue<?>> gcbatlasfix$additional = List.of();
    @Unique
    private Optional<TextureMetadataSection> gcbatlasfix$texture = Optional.empty();

    @Inject(method = "<init>(Lnet/minecraft/resources/Identifier;Lnet/minecraft/client/resources/metadata/animation/FrameSize;Lcom/mojang/blaze3d/platform/NativeImage;Ljava/util/Optional;Ljava/util/List;Ljava/util/Optional;)V", at = @At("TAIL"))
    private void gcbatlasfix$remember(Identifier name, FrameSize frameSize, NativeImage image, Optional<AnimationMetadataSection> animation,
                                      List<MetadataSectionType.WithValue<?>> additional, Optional<TextureMetadataSection> texture, CallbackInfo ci) {
        gcbatlasfix$animation = animation;
        gcbatlasfix$additional = additional;
        gcbatlasfix$texture = texture;
    }

    @Override
    public Optional<AnimationMetadataSection> gcbatlasfix$animation() {
        return gcbatlasfix$animation;
    }

    @Override
    public List<MetadataSectionType.WithValue<?>> gcbatlasfix$additional() {
        return gcbatlasfix$additional;
    }

    @Override
    public Optional<TextureMetadataSection> gcbatlasfix$texture() {
        return gcbatlasfix$texture;
    }
}
