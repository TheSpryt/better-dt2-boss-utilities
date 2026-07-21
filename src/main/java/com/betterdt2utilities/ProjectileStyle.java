package com.betterdt2utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Prayer-disable projectile styles, as (range, magic) spot-anim id pairs. A boss
 * set to its own style is left untouched; every other entry is a real range/magic
 * dual attack from another boss. IDs cross-checked against the Projectile Override
 * plugin (Loze-Put/projectile-override).
 */
@Getter
@AllArgsConstructor
public enum ProjectileStyle
{
	Akkha("Akkha", 2255, 2253),
	Cerberus("Cerberus", 1245, 1242),
	CorruptedHunllef("Corrupted Hunllef", 1712, 1708),
	DemonicGorilla("Demonic Gorilla", 1302, 1304),
	Doom("Doom of Mokhaiotl", 3380, 3379),
	Hueycoatl("Hueycoatl", 2972, 2975),
	Hunllef("Hunllef", 1711, 1707),
	Hydra("Hydra", 1663, 1662),
	Inferno("Jal-Ak (Inferno blob)", 1378, 1380),
	KalphiteQueen("Kalphite Queen", 288, 280),
	KreeArra("Kree'arra", 1199, 1200),
	Leviathan("Leviathan", 2487, 2489),
	Manticore("Manticore", 2683, 2681),
	Nightmare("Nightmare", 1766, 1764),
	Olm("Olm spheres", 1343, 1341),
	Muspah("Phantom Muspah", 2329, 2327),
	Scurrius("Scurrius", 2642, 2640),
	Sotetseg("Sotetseg", 1607, 1606),
	TormentedDemon("Tormented Demon", 2857, 2853),
	Vardorvis("Vardorvis", 2521, 2520),
	WardensP2("Wardens (phase 2)", 2241, 2224),
	WardensP3("Wardens (phase 3)", 2206, 2208),
	Whisperer("Whisperer", 2444, 2445),
	Zulrah("Zulrah", 1044, 1046);

	private final String displayName;
	private final int range;
	private final int magic;

	@Override
	public String toString()
	{
		return displayName;
	}
}
