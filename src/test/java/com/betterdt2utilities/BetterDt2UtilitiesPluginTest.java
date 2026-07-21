package com.betterdt2utilities;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BetterDt2UtilitiesPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BetterDt2UtilitiesPlugin.class);
		RuneLite.main(args);
	}
}
