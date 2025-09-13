package com.rinko1231.armordamagelimit.mixin;

import com.rinko1231.armordamagelimit.config.ArmorProtectionConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class ArmorDurabilityMixin {
    @Shadow @Final public NonNullList<ItemStack> armor;

    @Inject(method = "hurtArmor", at = @At("HEAD"), cancellable = true)
    private void modifyArmorDurability(DamageSource source, float amount, int[] slots, CallbackInfo ci) {
        if (!(amount <= 0.0F)) {
            // Calculate the tiered damage based on original hit damage
            int tierDamage = calculateTierDamage(amount);
            
            for (int i : slots) {
                ItemStack armorItem = armor.get(i);
                
                if (!armorItem.isEmpty() && armorItem.getItem() instanceof ArmorItem &&
                    (!source.is(DamageTypeTags.IS_FIRE) || !armorItem.getItem().isFireResistant())) {
                    
                    String itemId = BuiltInRegistries.ITEM.getKey(armorItem.getItem()).toString();
                    
                    // Check if item is blacklisted
                    if (!ArmorProtectionConfig.itemProtectionBlacklist.get().contains(itemId)) {
                        // Apply tiered damage
                        if (tierDamage > 0) {
                            armorItem.hurtAndBreak(tierDamage, ((Inventory) (Object) this).player, 
                                (player) -> player.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, i)));
                        }
                    } else {
                        // Apply normal vanilla damage to blacklisted items
                        float vanillaDamage = amount / 4.0F;
                        int normalDamage = Math.max(1, (int) vanillaDamage);
                        
                        armorItem.hurtAndBreak(normalDamage, ((Inventory) (Object) this).player, 
                            (player) -> player.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, i)));
                    }
                }
            }
        }
        ci.cancel();
    }
    
    /**
     * Calculate damage based on configurable tiered system
     */
    private int calculateTierDamage(float originalDamage) {
        if (originalDamage <= ArmorProtectionConfig.tier1Threshold.get()) {
            return ArmorProtectionConfig.tier1Damage.get();
        } else if (originalDamage <= ArmorProtectionConfig.tier2Threshold.get()) {
            return ArmorProtectionConfig.tier2Damage.get();
        } else {
            return ArmorProtectionConfig.tier3Damage.get();
        }
    }
}