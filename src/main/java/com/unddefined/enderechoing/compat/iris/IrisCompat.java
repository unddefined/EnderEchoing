package com.unddefined.enderechoing.compat.iris;

public final class IrisCompat {
    private IrisCompat() {}

    public static boolean isShaderPackInUse() {
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object instance = api.getMethod("getInstance").invoke(null);
            return (boolean) api.getMethod("isShaderPackInUse").invoke(instance);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }
}
