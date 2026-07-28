/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import java.util.Locale;
import javax.inject.Singleton;
import net.runelite.api.NPC;

@Singleton
public class TentacleManager
{
	public boolean isTentacle(NPC npc)
	{
		String name = npc.getName();
		return name != null && name.toLowerCase(Locale.ROOT).contains("tentacle");
	}
}
