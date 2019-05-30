package com.ptsmods.morecommands.miscellaneous;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.ParameterizedType;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public interface IGameRule<T> {

	/**
	 * This should return the name of the gamerule in question in camel case. (E.g.
	 * wildLimit)
	 *
	 * @return The name of the gamerule.
	 */
	public String getName();

	/**
	 * The value type of the gamerule, i.e. what values are accepted.
	 *
	 * @return This gamerule's type.
	 */
	public ValueType getType();

	/**
	 * The default value to set this gamerule to if it does not yet exist.
	 *
	 * @return The default value to set this gamerule to if it does not yet exist.
	 */
	public T getDefaultValue();

	public default Class<T> getClassType() {
		try {
			return (Class<T>) Class.forName(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].toString());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Gets this gamerule's value in the given world, the default implementation of
	 * this method should work for all ValueTypes, but you may want to override it
	 * for {@link ValueType#FUNCTION}.
	 *
	 * @param world
	 * @return This gamerule's value in the given world.
	 */
	public default T getValue(World world) {
		return (T) (getType() == ValueType.NUMERICAL_VALUE ? world.getGameRules().getInt(getName()) : getType() == ValueType.BOOLEAN_VALUE ? world.getGameRules().getBoolean(getName()) : world.getGameRules().getString(getName()));
	}

	/**
	 * This method gets called whenever the value of the gamerule has been updated,
	 * might change just a variable, might generate an entire structure. <br>
	 * This method gets ran on the {@link Ticker} with a type of
	 * {@link net.minecraftforge.fml.common.gameevent.TickEvent.Type#WORLD WORLD}.
	 *
	 * @param world    The world it was changed on.
	 * @param oldValue The value it was before it changed.
	 * @param newValue The value it was changed to.
	 */
	public void onUpdate(WorldServer world, T oldValue, T newValue);

	/**
	 * This method gets called when the gamerule is created the first time, might
	 * write something to the world's NBT data or whatever. <br>
	 * This method gets called for every world!
	 *
	 * @param server The MinecraftServer instance it was registered on.
	 * @param world  The World it was registered on.
	 */
	public void onCreateWorld(MinecraftServer server, World world);

	/**
	 * This method gets called when the gamerule is registered. This method will get
	 * called even if onCreate is already called, although it does get called after
	 * onCreate. <br>
	 * This method gets called for every world!
	 *
	 * @param server The MinecraftServer instance it exists on.
	 * @param world  The World it exists on.
	 * @param value  The current value of the gamerule.
	 */
	public void initWorld(MinecraftServer server, World world, T value);

	/**
	 * This method gets called when the gamerule is registered. This method will get
	 * called even if onCreate is already called, although it does get called after
	 * onCreate. <br>
	 * This method gets called once.
	 *
	 * @param server The MinecraftServer instance it exists on.
	 * @param world  The World it exists on.
	 * @param value  The current value of the gamerule.
	 */
	public void initServer(MinecraftServer server);

	/**
	 * When an IGameRule is instantiated, any fields in its class annotated with
	 * this annotation will be set to either the instance of the IGameRule class it
	 * is in, or to the instance of IGameRule with the given name (cannot be a
	 * vanilla gamerule).
	 *
	 * @author PlanetTeamSpeak
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface Inject {
		String value() default "";
	}

}
