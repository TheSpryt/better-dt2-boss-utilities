/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, TheSpryt
 * All rights reserved.
 */
package com.betterdt2utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Renderable;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.callback.Hooks;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class SoulManager
{
	public enum SoulType
	{
		VITA("Health"),
		MORS("Damage"),
		SANITAS("Sanity"),
		ORATIO("Prayer");

		private final String label;

		SoulType(String label)
		{
			this.label = label;
		}

		public String label()
		{
			return label;
		}
	}

	// The Whisperer sanity meter (0-100%), driving the Sanitas unhide threshold.
	private static final int SANITY_VARBIT = 15064;

	@Inject
	private Client client;

	@Inject
	private BetterDt2UtilitiesConfig config;

	@Inject
	private Hooks hooks;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	// NPC index -> soul type, learned from the overhead chant and remembered.
	private final Map<Integer, SoulType> soulTypes = new HashMap<>();
	// NPC indices that are the ends of their line - the Venator shot targets.
	private final Set<Integer> targetIndices = new HashSet<>();

	// The player's stats as the soul phase began. The unhide thresholds are judged
	// against these, not live stats, so souls do not pop back in as prayer/sanity
	// drain while the player is already in the phase killing them.
	private boolean soulPhaseActive;
	private int entryHp = -1;
	private int entryPrayer = -1;
	private int entrySanity = -1;

	public void startUp()
	{
		hooks.registerRenderableDrawListener(drawListener);
	}

	public void shutDown()
	{
		hooks.unregisterRenderableDrawListener(drawListener);
		soulTypes.clear();
		targetIndices.clear();
	}

	public boolean isSoul(NPC npc)
	{
		String name = npc.getName();
		return name != null && name.toLowerCase(Locale.ROOT).contains("soul");
	}

	public SoulType typeOf(NPC npc)
	{
		return soulTypes.get(npc.getIndex());
	}

	public boolean isTarget(NPC npc)
	{
		return targetIndices.contains(npc.getIndex());
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		Actor actor = event.getActor();
		if (!(actor instanceof NPC) || !isSoul((NPC) actor))
		{
			return;
		}
		SoulType type = parseType(event.getOverheadText());
		if (type != null)
		{
			soulTypes.put(((NPC) actor).getIndex(), type);
			if (config.soulsRenameSpeech())
			{
				actor.setOverheadText(type.label());
			}
		}
	}

	private SoulType parseType(String text)
	{
		if (text == null)
		{
			return null;
		}
		String t = text.toLowerCase(Locale.ROOT);
		if (t.contains("vita"))
		{
			return SoulType.VITA;
		}
		if (t.contains("mors"))
		{
			return SoulType.MORS;
		}
		if (t.contains("sanitas"))
		{
			return SoulType.SANITAS;
		}
		if (t.contains("oratio"))
		{
			return SoulType.ORATIO;
		}
		return null;
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		soulTypes.remove(event.getNpc().getIndex());
		targetIndices.remove(event.getNpc().getIndex());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING
			|| event.getGameState() == GameState.LOGIN_SCREEN
			|| event.getGameState() == GameState.HOPPING)
		{
			soulTypes.clear();
			targetIndices.clear();
			soulPhaseActive = false;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		List<NPC> souls = new ArrayList<>();
		for (NPC npc : client.getNpcs())
		{
			if (isSoul(npc))
			{
				souls.add(npc);
			}
		}

		// Capture the entry stats the moment the phase begins, and forget them once
		// all souls are gone.
		if (!souls.isEmpty() && !soulPhaseActive)
		{
			soulPhaseActive = true;
			entryHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
			entryPrayer = client.getBoostedSkillLevel(Skill.PRAYER);
			entrySanity = sanityPercent();
		}
		else if (souls.isEmpty())
		{
			soulPhaseActive = false;
		}

		recomputeTargets(souls);
	}

	// Group the souls into lines by adjacency and mark the end soul(s) of each line
	// as the Venator target - shooting an end ricochets through all three, while the
	// middle only reaches one neighbour. For an L the two ends both work; for a
	// straight line the two ends work and the middle does not.
	private void recomputeTargets(List<NPC> souls)
	{
		targetIndices.clear();
		List<List<NPC>> lines = clusterLines(souls);
		for (List<NPC> line : lines)
		{
			if (line.size() <= 1)
			{
				line.forEach(n -> targetIndices.add(n.getIndex()));
				continue;
			}
			for (NPC n : line)
			{
				if (neighbourCount(n, line) <= 1)
				{
					targetIndices.add(n.getIndex());
				}
			}
		}
	}

	private List<List<NPC>> clusterLines(List<NPC> souls)
	{
		List<List<NPC>> lines = new ArrayList<>();
		Set<Integer> seen = new HashSet<>();
		for (NPC start : souls)
		{
			if (seen.contains(start.getIndex()))
			{
				continue;
			}
			List<NPC> line = new ArrayList<>();
			List<NPC> queue = new ArrayList<>();
			queue.add(start);
			seen.add(start.getIndex());
			while (!queue.isEmpty())
			{
				NPC cur = queue.remove(queue.size() - 1);
				line.add(cur);
				for (NPC other : souls)
				{
					if (!seen.contains(other.getIndex()) && adjacent(cur, other))
					{
						seen.add(other.getIndex());
						queue.add(other);
					}
				}
			}
			lines.add(line);
		}
		return lines;
	}

	private int neighbourCount(NPC npc, List<NPC> line)
	{
		int n = 0;
		for (NPC other : line)
		{
			if (other != npc && adjacent(npc, other))
			{
				n++;
			}
		}
		return n;
	}

	private boolean adjacent(NPC a, NPC b)
	{
		WorldPoint wa = a.getWorldLocation();
		WorldPoint wb = b.getWorldLocation();
		if (wa == null || wb == null || wa.getPlane() != wb.getPlane())
		{
			return false;
		}
		int dx = Math.abs(wa.getX() - wb.getX());
		int dy = Math.abs(wa.getY() - wb.getY());
		return dx <= 1 && dy <= 1 && (dx + dy) > 0;
	}

	private boolean shouldDraw(Renderable renderable, boolean drawingUI)
	{
		return !(renderable instanceof NPC) || !isHidden((NPC) renderable);
	}

	public boolean isHidden(NPC npc)
	{
		if (!isSoul(npc))
		{
			return false;
		}
		// Show only shot targets overrides everything else: hide every non-target,
		// show every target, ignoring the type toggles and unhide thresholds.
		if (config.soulsShowOnlyTarget())
		{
			return !targetIndices.contains(npc.getIndex());
		}
		SoulType type = soulTypes.get(npc.getIndex());
		if (type == null)
		{
			return false;
		}

		// Additive: a soul is shown if its hide toggle is off OR its unhide threshold
		// was passed on entry. So a passed threshold only forces its own type visible;
		// it never hides the types you have chosen to keep shown.
		if (belowUnhideThreshold(type))
		{
			return false;
		}

		switch (type)
		{
			case VITA:
				return config.soulsHideVita();
			case MORS:
				return config.soulsHideMors();
			case SANITAS:
				return config.soulsHideSanitas();
			case ORATIO:
				return config.soulsHideOratio();
			default:
				return false;
		}
	}

	private boolean belowUnhideThreshold(SoulType type)
	{
		switch (type)
		{
			case VITA:
			{
				int t = config.soulsUnhideVitaHp();
				return t > 0 && entryHp >= 0 && entryHp <= t;
			}
			case ORATIO:
			{
				int t = config.soulsUnhideOratioPrayer();
				return t > 0 && entryPrayer >= 0 && entryPrayer <= t;
			}
			case SANITAS:
			{
				int t = config.soulsUnhideSanitasSanity();
				return t > 0 && entrySanity >= 0 && entrySanity <= t;
			}
			default:
				return false;
		}
	}

	private int sanityPercent()
	{
		return SANITY_VARBIT < 0 ? -1 : client.getVarbitValue(SANITY_VARBIT);
	}
}
