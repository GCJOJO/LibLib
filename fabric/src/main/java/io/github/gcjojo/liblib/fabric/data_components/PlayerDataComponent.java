package io.github.gcjojo.liblib.fabric.data_components;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ladysnake.cca.api.v3.component.ComponentV3;

public class PlayerDataComponent implements ComponentV3 {
    private CompoundTag additionalData = new CompoundTag();

    public CompoundTag getAdditionalData() {
        return this.additionalData;
    }
    
    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        this.additionalData = tag.getCompound("AdditionalData");
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        tag.put("AdditionalData", this.additionalData);
    }
}
