package com.eiqui.gcbatlasfix;

import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionType;

import java.util.List;
import java.util.Optional;

/** 축소한 스프라이트를 같은 메타데이터로 다시 만들려고 생성자 인자를 기억해 둔다 */
public interface SpriteMetadata {
    Optional<AnimationMetadataSection> gcbatlasfix$animation();

    List<MetadataSectionType.WithValue<?>> gcbatlasfix$additional();

    Optional<TextureMetadataSection> gcbatlasfix$texture();
}
