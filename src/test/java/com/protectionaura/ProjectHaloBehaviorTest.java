package com.protectionaura;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Model;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.events.ConfigChanged;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests plugin state logic without starting a client or logging into an account. */
public class ProjectHaloBehaviorTest
{
	private ProjectHaloPlugin plugin;
	private Prayer prayer;
	private GameState state = GameState.LOGGED_IN;
	private boolean dead;
	private boolean modelAvailable = true;
	private Player local;
	private Player other;
	private final ProjectHaloConfig config = new ProjectHaloConfig() {};

	@Before
	public void setUp() throws Exception
	{
		Model model = proxy(Model.class, (object, method, args) -> defaultValue(method.getReturnType()));
		local = proxy(Player.class, (object, method, args) -> {
			if (method.getName().equals("getModel")) return modelAvailable ? model : null;
			if (method.getName().equals("isDead")) return dead;
			return defaultValue(method.getReturnType());
		});
		other = proxy(Player.class, (object, method, args) -> {
			if (method.getName().equals("getModel")) return model;
			return defaultValue(method.getReturnType());
		});
		Client client = proxy(Client.class, (object, method, args) -> {
			switch (method.getName())
			{
				case "getGameState": return state;
				case "getLocalPlayer": return local;
				case "getVarbitValue": return prayer != null && ((Integer) args[0]) == prayer.getVarbit() ? 1 : 0;
				default: return defaultValue(method.getReturnType());
			}
		});
		plugin = new ProjectHaloPlugin();
		inject("client", client);
		inject("config", config);
		ConfigChanged changed = new ConfigChanged();
		changed.setGroup(ProjectHaloConfig.GROUP);
		plugin.onConfigChanged(changed);
	}

	@Test
	public void protectionPrayersUseConfiguredColorsAndOpacity()
	{
		assertPrayer(Prayer.PROTECT_FROM_MELEE, config.meleeColor());
		assertPrayer(Prayer.PROTECT_FROM_MISSILES, config.rangeColor());
		assertPrayer(Prayer.PROTECT_FROM_MAGIC, config.magicColor());
	}

	@Test
	public void disablingPrayerRemovesAura()
	{
		prayer = Prayer.PROTECT_FROM_MELEE;
		refresh();
		assertNotNull(plugin.getAuraColor(local));
		prayer = null;
		refresh();
		assertNull(plugin.getAuraColor(local));
	}

	@Test
	public void otherPlayersAndUnavailableModelsHaveNoAura()
	{
		prayer = Prayer.PROTECT_FROM_MAGIC;
		refresh();
		assertNull(plugin.getAuraColor(other));
		assertNull(plugin.getAuraColor(null));
		dead = true;
		assertNull(plugin.getAuraColor(local));
		dead = false;
		modelAvailable = false;
		assertNull(plugin.getAuraColor(local));
	}

	@Test
	public void bulkVarpUpdateRefreshesPrayer()
	{
		prayer = Prayer.PROTECT_FROM_MELEE;
		refresh();
		prayer = null;
		VarbitChanged changed = new VarbitChanged();
		changed.setVarbitId(-1);
		plugin.onVarbitChanged(changed);
		assertNull("A bulk varp change should not leave an old aura cached", plugin.getAuraColor(local));
	}

	@Test
	public void varplayerUpdateActivatesAndSwitchesPrayer()
	{
		VarbitChanged changed = new VarbitChanged();
		changed.setVarbitId(-1);
		prayer = Prayer.PROTECT_FROM_MELEE;
		plugin.onVarbitChanged(changed);
		assertEquals(config.meleeColor().getRGB() & 0xffffff, plugin.getAuraColor(local).getRGB() & 0xffffff);
		prayer = Prayer.PROTECT_FROM_MAGIC;
		plugin.onVarbitChanged(changed);
		assertEquals(config.magicColor().getRGB() & 0xffffff, plugin.getAuraColor(local).getRGB() & 0xffffff);
	}

	@Test
	public void varplayerUpdateWhileLoggedOutDoesNotCreateAura()
	{
		state = GameState.LOGIN_SCREEN;
		prayer = Prayer.PROTECT_FROM_MELEE;
		VarbitChanged changed = new VarbitChanged();
		changed.setVarbitId(-1);
		plugin.onVarbitChanged(changed);
		assertNull(plugin.getAuraColor(local));
	}

	private void assertPrayer(Prayer active, Color expected)
	{
		prayer = active;
		refresh();
		Color actual = plugin.getAuraColor(local);
		assertNotNull(actual);
		assertEquals(expected.getRGB() & 0xffffff, actual.getRGB() & 0xffffff);
		assertEquals(153, actual.getAlpha());
	}

	private void refresh()
	{
		VarbitChanged changed = new VarbitChanged();
		changed.setVarbitId(Prayer.PROTECT_FROM_MELEE.getVarbit());
		plugin.onVarbitChanged(changed);
	}

	private void inject(String name, Object value) throws Exception
	{
		Field field = ProjectHaloPlugin.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(plugin, value);
	}

	private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler)
	{
		return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler));
	}

	private static Object defaultValue(Class<?> type)
	{
		if (type == boolean.class) return false;
		if (type == int.class) return 0;
		if (type == long.class) return 0L;
		if (type == float.class) return 0f;
		if (type == double.class) return 0d;
		return null;
	}
}
