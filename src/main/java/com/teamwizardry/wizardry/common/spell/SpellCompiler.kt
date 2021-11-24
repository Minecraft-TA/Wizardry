package com.teamwizardry.wizardry.common.spell;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.teamwizardry.wizardry.common.spell.component.ComponentRegistry;
import com.teamwizardry.wizardry.common.spell.component.EffectChain;
import com.teamwizardry.wizardry.common.spell.component.ISpellComponent;
import com.teamwizardry.wizardry.common.spell.component.Modifier;
import com.teamwizardry.wizardry.common.spell.component.ModuleEffect;
import com.teamwizardry.wizardry.common.spell.component.ModuleShape;
import com.teamwizardry.wizardry.common.spell.component.ShapeChain;
import com.teamwizardry.wizardry.common.spell.component.TargetComponent;
import com.teamwizardry.wizardry.common.spell.component.TargetType;
import com.teamwizardry.wizardry.configs.ServerConfigs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SpellCompiler
{
    private ShapeChain firstShape;
    private ShapeChain currentShape;
    private EffectChain currentEffect;
    
    private int modifierCount = 0;
    
    public ShapeChain compileSpell(ItemStack... items)
    {
        return this.compileSpell(Arrays.asList(items));
    }
    
    public ShapeChain compileSpell(List<ItemStack> items)
    {
        List<ISpellComponent> components = processItems(items);
        ShapeChain spell = compile(components);
        return spell;
    }
    
    private List<ISpellComponent> processItems(List<ItemStack> items)
    {
        List<ISpellComponent> components = new LinkedList<>();
        
        LinkedList<Item> flattened = items.stream()
                                          .flatMap(stack -> IntStream.range(0, stack.getCount())
                                                                     .mapToObj(n -> stack.getItem()))
                                                                     .collect(Collectors.toCollection(LinkedList::new));
        while (!flattened.isEmpty())
        {
            ISpellComponent component = ComponentRegistry.getComponentForItems(flattened);
            if (component == null)
                flattened.remove();
            else for (int i = 0; i < component.getItems().size(); i++)
                flattened.remove();
            components.add(component);
        }
        
        return components;
    }
    
    private ShapeChain compile(List<ISpellComponent> components)
    {
        for (ISpellComponent component : components)
        {
            if (component instanceof ModuleShape)
                handleShape((ModuleShape)component);
            else if (component instanceof ModuleEffect)
                handleEffect((ModuleEffect)component);
            else if (component instanceof Modifier)
                handleModifier((Modifier)component);
            else if (component instanceof TargetComponent)
                handleTarget((TargetComponent)component);
        }
        
        return firstShape;
    }
    
    private void handleShape(ModuleShape shape)
    {
        ShapeChain next = new ShapeChain(shape);
        if (firstShape == null)
        {
            firstShape = currentShape = next;
        }
        else
        {
            currentShape.setNext(next);
            currentShape = next;
        }
        modifierCount = 0;
    }
    
    private void handleEffect(ModuleEffect effect)
    {
        if (firstShape == null) // Spells have to start with shapes!
            return;
        currentEffect = new EffectChain(effect);
        currentShape.addEffect(currentEffect);
        modifierCount = 0;
    }
    
    private void handleModifier(Modifier modifier)
    {
        if (modifierCount++ > ServerConfigs.maxModifiers)
            return;
        if (firstShape == null) // Spells have to start with shapes!
            return;
        if (currentEffect == null)
            currentShape.addModifier(modifier);
        else currentEffect.addModifier(modifier);
    }
    
    private void handleTarget(TargetComponent target)
    {
        if (firstShape == null) // Spells have to start with shapes!
            return;
        TargetType targetType = TargetType.ALL;
        if (target == ComponentRegistry.getEntityTarget())
            targetType = TargetType.ENTITY;
        else if (target == ComponentRegistry.getBlockTarget())
            targetType = TargetType.BLOCK;
        if (currentEffect == null)
            currentShape.setTarget(targetType);
        else currentEffect.setTarget(targetType);
    }
    
    private SpellCompiler() {}
    
    public static SpellCompiler get()
    {
        return new SpellCompiler();
    }
}
