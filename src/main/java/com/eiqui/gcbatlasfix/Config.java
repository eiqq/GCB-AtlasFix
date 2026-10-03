package com.eiqui.gcbatlasfix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** config/gcbatlasfix.json. 없으면 기본값으로 만든다. */
public final class Config {
    /** 아틀라스 한 변의 최대 크기. 드라이버가 더 크다고 답해도 이 값으로 자른다 */
    public int maxAtlasSize = 16384;
    /** 이보다 작아지는 스프라이트는 더 줄이지 않는다(한 프레임 기준) */
    public int minSpriteSize = 16;
    /** 축소하지 않을 네임스페이스(바닐라) */
    public String[] keepNamespaces = {"minecraft"};

    public static final Config INSTANCE = load();

    private static Config load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("gcbatlasfix.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Config config = new Config();
        try {
            if (Files.exists(path)) {
                try (Reader r = Files.newBufferedReader(path)) {
                    Config read = gson.fromJson(r, Config.class);
                    if (read != null) config = read;
                }
            }
            try (Writer w = Files.newBufferedWriter(path)) {
                gson.toJson(config, w);
            }
        } catch (Exception e) {
            AtlasFix.LOGGER.warn("[GCB Atlas Fix] config 읽기 실패, 기본값 사용", e);
        }
        return config;
    }

    public boolean keeps(String namespace) {
        for (String ns : keepNamespaces) {
            if (ns.equals(namespace)) return true;
        }
        return false;
    }
}
