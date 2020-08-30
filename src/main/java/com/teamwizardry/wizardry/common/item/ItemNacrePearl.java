package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.StringConsts;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class ItemNacrePearl extends Item implements INacreProduct {

	public ItemNacrePearl(Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		colorableOnUpdate(stack, worldIn);

		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		colorableOnEntityItemUpdate(entity);

		return super.onEntityItemUpdate(stack, entity);
	}

	private String getNameType(@Nonnull ItemStack stack) {
		float quality = this.getQuality(stack);
		if (quality > 1)
			return "ancient";
		else if (quality == 1)
			return "apex";
		else if (quality > 0.8)
			return "potent";
		else if (quality > 0.6)
			return "decent";
		else if (quality > 0.4)
			return "flawed";
		else if (quality > 0.2)
			return "drained";
		return "wasted";
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (!stack.hasTag())
			return super.getTranslationKey(stack);
		return super.getTranslationKey(stack) + "." + getNameType(stack);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {

		items.add(new ItemStack(this));
		ItemStack stack = new ItemStack(this);
		stack.getOrCreateTag().putFloat(StringConsts.PURITY_OVERRIDE, 2f);
		items.add(stack);

		super.fillItemGroup(group, items);
	}
}
