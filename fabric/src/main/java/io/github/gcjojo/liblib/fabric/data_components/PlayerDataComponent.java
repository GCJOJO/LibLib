package io.github.gcjojo.liblib.fabric.data_components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;

public class PlayerDataComponent implements ComponentV3 {
    private CompoundTag additionalData = new CompoundTag();

    public CompoundTag getAdditionalData() {
        return this.additionalData;
    }
    
    @Override
    public void readFromNbt(CompoundTag tag) {
        this.additionalData = tag.getCompound("AdditionalData");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("AdditionalData", this.additionalData);
    }

}
