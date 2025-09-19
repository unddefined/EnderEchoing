package com.unddefined.enderechoing.client.particles;

import com.mojang.serialization.MapCodec;
import com.unddefined.enderechoing.server.registry.ParticlesRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;

public class DirectlyMovingDustOptions implements ParticleOptions {
    public final int lifetime;
    public final float red, green, blue, size;
    public static final MapCodec<DirectlyMovingDustOptions> CODEC = MapCodec.unit(new DirectlyMovingDustOptions(0, 0f, 0f, 0f, 0f));

    public DirectlyMovingDustOptions(int lifetime, float red, float green, float blue,float size) {
        this.lifetime = lifetime;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.size = size;
    }

    public static final StreamCodec<ByteBuf, DirectlyMovingDustOptions> STREAM_CODEC = StreamCodec.of(
            (buf, options) -> {
                buf.writeInt(options.lifetime);
                buf.writeFloat(options.red);
                buf.writeFloat(options.green);
                buf.writeFloat(options.blue);
                buf.writeFloat(options.size);
            },
            buf -> new DirectlyMovingDustOptions(buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat())
    );

    @Override
    public ParticleType<?> getType() {
        return ParticlesRegistry.DIRECT_MOVING_DUST.get();
    }

    // 添加getter方法

    public int lifetime() {
        return lifetime;
    }


    public float red() {
        return red;
    }

    public float green() {
        return green;
    }

    public float blue() {
        return blue;
    }
}