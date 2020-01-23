package com.ptsmods.morecommands.miscellaneous;

import java.lang.reflect.Field;
import java.util.Set;

import com.ptsmods.morecommands.commands.vanish.Commandvanish;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.WorldServer;

public class ModEntityTracker extends EntityTracker {

	private static final Field	trackedEntityHashTable;
	private static final Field	entries;

	static {
		Field f = null;
		Field f0 = null;
		try {
			f = Reference.getFieldMapped(EntityTracker.class, "trackedEntityHashTable", "field_72794_c");
			f.setAccessible(true);
			f0 = Reference.getFieldMapped(EntityTracker.class, "entries", "field_72793_b");
			f0.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		trackedEntityHashTable = f;
		entries = f0;
	}

	public ModEntityTracker(WorldServer theWorldIn) {
		super(theWorldIn);
	}

	public IntHashMap<EntityTrackerEntry> getTable() {
		try {
			return (IntHashMap) trackedEntityHashTable.get(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Set<EntityTrackerEntry> getEntries() {
		try {
			return (Set) entries.get(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void untrack(Entity entityIn) {
		if (entityIn instanceof EntityPlayerMP && Commandvanish.vanished.contains(entityIn.getUniqueID().toString())) {
			// The super method also removes the entity from all entity tracker entries,
			// meaning the entity will no longer receive any movement packets, let's fix
			// that here.
			// The super method has been copied here, but the code that removes all tracked
			// entities from the entity has been removed and only the code that does vice
			// versa has remained.
			EntityTrackerEntry entitytrackerentry1 = getTable().removeObject(entityIn.getEntityId());
			if (entitytrackerentry1 != null) {
				getEntries().remove(entitytrackerentry1);
				entitytrackerentry1.sendDestroyEntityPacketToTrackedPlayers();
			}
		} else super.untrack(entityIn);
	}

}
