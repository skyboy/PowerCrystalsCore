package powercrystals.core.net;

import cpw.mods.fml.common.FMLLog;

import powercrystals.core.client.CustomEffectRenderer;

import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy
{
	public ClientProxy(){}
	
	@Override
	public void overrideParticleRenderer()
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		FMLLog.severe("Replacing EffectRenderer");
		minecraft.effectRenderer = new CustomEffectRenderer();
	}
}
