package com.teamwizardry.wizardry.common.spell;

import java.util.List;

import com.teamwizardry.wizardry.api.spell.ISpellComponent;
import com.teamwizardry.wizardry.api.spell.Pattern;

import net.minecraft.item.Item;

public class Module implements ISpellComponent
{
    // Identifying data - must be unique
    protected final Pattern pattern;
    protected final String name;
    protected final List<Item> items;

    // Modifier and Usage Metadata
    protected final String element;

    public Module(Pattern pattern, String name, List<Item> items, String element)
    {
        this.pattern = pattern;
        this.name = name;
        this.items = items;
        this.element = element;
    }

    public Pattern getPattern()
    { return pattern; }

    @Override public String getName()
    { return name; }

    @Override public List<Item> getItems()
    { return items; }

    public String getElement()
    { return element; }

    public String toString()
    {
        return pattern.getRegistryName() + ":" + name + " = [" + items + ", " + element + "]";
    }
}