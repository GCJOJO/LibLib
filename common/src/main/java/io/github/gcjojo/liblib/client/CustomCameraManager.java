package io.github.gcjojo.liblib.client;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class CustomCameraManager {
    @Getter
    @Setter
    private static boolean isActive = false;
    @Getter
    @Setter
    private static Vec3 position = new Vec3(0, 0, 0);
    @Getter
    @Setter
    private static Vector3f rotation = new Vector3f(0, 0, 0);
}
