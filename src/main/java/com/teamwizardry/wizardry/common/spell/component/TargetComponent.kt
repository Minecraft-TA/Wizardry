package com.teamwizardry.wizardry.common.spell.component;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;

public class TargetComponent implements ISpellComponent
{
    private final String name;
    private final Item item;
    
    public TargetComponent(String name, Item item)
    {
        this.name = name;
        this.item = item;
    }
    
    @Override public String getName() { return name; }

    @Override public List<Item> getItems() { return Arrays.asList(item); }
}
