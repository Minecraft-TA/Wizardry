package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.entity.EntityHallowedSpirit;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 8/21/2016.
 */
public class RenderHallowedSpirit extends RenderLiving<EntityHallowedSpirit> {

    public static final ResourceLocation SPIRIT_TEX = new ResourceLocation(Wizardry.MODID, "textures/entity/hallowed_spirit.png");

    public RenderHallowedSpirit(RenderManager renderManager, ModelBase modelBase) {
        super(renderManager, modelBase, 0f);
    }

    @Override
    public boolean canRenderName(EntityHallowedSpirit entity) {
        return false;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHallowedSpirit entity) {
        return SPIRIT_TEX;
    }
}