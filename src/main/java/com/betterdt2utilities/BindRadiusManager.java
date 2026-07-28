/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.NPC;

@Singleton
public class BindRadiusManager
{
	// The Whisperer's real-arena melee-chase form. She only takes this id while
	// closing in after a special, so its mere presence is the phase trigger - this
	// mirrors how the Radius Markers plugin scopes the ring to exactly this window.
	private static final Set<Integer> BIND_NPC_IDS = ImmutableSet.of(12205);

	@Inject
	private Client client;

	public NPC getBindNpc()
	{
		for (NPC npc : client.getNpcs())
		{
			if (BIND_NPC_IDS.contains(npc.getId()))
			{
				return npc;
			}
		}
		return null;
	}

	public boolean isBindActive()
	{
		return getBindNpc() != null;
	}
}
