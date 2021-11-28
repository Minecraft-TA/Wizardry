package com.teamwizardry.wizardry

import com.teamwizardry.wizardry.common.init.*
import com.teamwizardry.wizardry.proxy.IProxy
import com.teamwizardry.wizardry.proxy.ServerProxy
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

const val MODID = "wizardry"

fun getID(path: String): Identifier { return Identifier(MODID, path) }

fun makeLogger(cls: Class<*>): Logger { return LogManager.getLogger(cls) }

val LOGGER = makeLogger(Wizardry::class.java)
var PROXY: IProxy = ServerProxy()

object Wizardry : ModInitializer {
    override fun onInitialize() {
        ModTags.init()
        ModFluids.init()
        ModItems.init()
        ModBlocks.init()
        ModSounds.init()
        ModPatterns.init()
    }
}