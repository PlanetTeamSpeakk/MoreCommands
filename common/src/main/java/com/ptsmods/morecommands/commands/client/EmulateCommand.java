package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.callbacks.KeyEvent;
import com.ptsmods.morecommands.api.callbacks.MouseEvent;
import com.ptsmods.morecommands.arguments.KeyArgumentType;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.mixin.client.accessor.MixinMouseAccessor;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EmulateCommand extends ClientCommand {
	private enum Type {
		MOUSE("button", () -> IntegerArgumentType.integer(0, GLFW.GLFW_MOUSE_BUTTON_LAST), (ctx, argName) -> ctx.getArgument(argName, Integer.class)),
		KEYBOARD("key", KeyArgumentType::key, KeyArgumentType::getKey);

		public final String argName;
		public final Supplier<ArgumentType<?>> argSupplier;
		public final Function<CommandContext<?>, Integer> getter;
		private int id = 0;

		<T> Type(String argName, Supplier<ArgumentType<T>> argSupplier, BiFunction<CommandContext<?>, String, Integer> getter) {
			this.argName = argName;
			this.argSupplier = argSupplier::get;
			this.getter = ctx -> getter.apply(ctx, argName);
		}

		public int getNextId() {
			return id++;
		}
	}

	private static final List<EmulateTask> pendingTasks = new ArrayList<>();
	private static final List<EmulateTask> tasks = new ArrayList<>();
	private static boolean ignoreMouse = false;
	private static final List<Integer> ignoreKeys = new ArrayList<>();
	private static final Object lock = new Object();

	public void preinit() {
		ClientTickEvent.CLIENT_POST.register(client -> {
			pendingTasks.forEach(EmulateTask::start);
			tasks.addAll(pendingTasks);
			pendingTasks.clear();
		});
		MouseEvent.EVENT.register((button, action, mods) -> {
			if (!ignoreMouse)
				// Remove all mouse tasks when the actual player presses a button on the mouse.
				synchronized (lock) {
					tasks.removeIf(task -> {
						boolean b = task.type == Type.MOUSE;
						if (b) task.onRemove();
						return b;
					});
				}
			return ignoreMouse = false;
		});
		KeyEvent.EVENT.register((key, scancode, action, mods) -> {
			if (ignoreKeys.contains(key))
				synchronized (lock) {
					tasks.removeIf(task -> {
						boolean b = task.type == Type.KEYBOARD && task.key == key;
						if (b) task.onRemove();
						return b;
					});
					ignoreKeys.remove((Integer) key);
				}
			return false;
		});
	}

	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		LiteralArgumentBuilder<ClientCommandSource> emulate = cLiteral("emulate");
		for (Type type : Type.values()) {
			emulate.then(cLiteral(type.name().toLowerCase()).then(cLiteral("clear").executes(ctx -> {
				int size = tasks.size();
				synchronized (lock) {
					tasks.removeIf(task -> {
						boolean b = task.type == type;
						if (b) task.onRemove();
						return b;
					});
				}
				int delta = size - tasks.size();
				sendMsg("Removed " + SF + delta + DF + " task" + (delta == 1 ? "" : "s") + ".");
				return delta;
			})).then(cLiteral("add")
					.then(cLiteral("interval")
							.then(cArgument("interval", IntegerArgumentType.integer(50))
									.executes(ctx -> executeTasksAddInterval(type, ctx, 0, -1))
									.then(cArgument(type.argName, type.argSupplier.get())
											.executes(ctx -> executeTasksAddInterval(type, ctx, ctx.getArgument(type.argName, Integer.class), -1))
											.then(cArgument("count", IntegerArgumentType.integer(-1))
													.executes(ctx -> executeTasksAddInterval(type, ctx, type.getter.apply(ctx), ctx.getArgument("count", Integer.class)))))))
					.then(cLiteral("hold")
							.executes(ctx -> executeTasksAddHold(type, 0, -1))
							.then(cArgument(type.argName, type.argSupplier.get())
									.executes(ctx -> executeTasksAddHold(type, ctx.getArgument(type.argName, Integer.class), -1))
									.then(cArgument("holdtime", IntegerArgumentType.integer(20))
											.executes(ctx -> executeTasksAddHold(type, type.getter.apply(ctx), ctx.getArgument("holdtime", Integer.class)))))))
					.then(cLiteral("remove")
							.then(cArgument("id", IntegerArgumentType.integer(0))
									.executes(ctx -> {
										int id = ctx.getArgument("id", Integer.class);
										AtomicReference<EmulateTask> task = new AtomicReference<>();
										synchronized (lock) {
											boolean b = tasks.removeIf(task0 -> {
												boolean b0 = task0.type == type && task0.id == id;
												if (b0) task.set(task0);
												return b0;
											});
											sendMsg(b ? Formatting.RED + "No task with that id could be found." : "Task " + SF + id + " (" + task.get().toString() + ") " + DF + "has been removed.");
											return b ? 1 : 0;
										}
									})))
					.then(cLiteral("list")
							.executes(ctx -> {
								List<EmulateTask> typeTasks = tasks.stream().filter(task -> task.type == type).collect(Collectors.toList());
								if (typeTasks.isEmpty())
									sendMsg("There are no tasks of type " + SF + type.name().toLowerCase() + DF + " yet.");
								else {
									sendMsg("The following tasks are currently running:");
									typeTasks.forEach(task -> sendMsg("  " + task.id + ": " + task.toString()));
								}
								return typeTasks.size();
							})));
		}
		dispatcher.register(emulate.then(cLiteral("clear").executes(ctx -> {
			tasks.forEach(EmulateTask::onRemove);
			tasks.clear();
			sendMsg("All tasks have been cleared.");
			return 1;
		})));
	}

	private int executeTasksAddInterval(Type type, CommandContext<ClientCommandSource> ctx, int button, int count) {
		return executeTasksAdd(new EmulateTask(type, ctx.getArgument("interval", Integer.class), button, false, count));
	}

	private int executeTasksAddHold(Type type, int button, int time) {
		return executeTasksAdd(new EmulateTask(type, 33, button, true, time));
	}

	private int executeTasksAdd(EmulateTask task) {
		pendingTasks.add(task);
		sendMsg("The task has been added with id " + SF + task.id + DF + ", to remove it, type " + SF + "/emulate " + task.type.name().toLowerCase() + " remove " + task.id + DF + ".");
		return tasks.size();
	}

	private static class EmulateTask {
		private final Type type;
		private final int id;
		private final int interval, key;
		private final boolean hold;
		private final int time;
		private int count;
		private long starttime = -1;

		private EmulateTask(Type type, int interval, int key, boolean hold, int count) {
			this.type = type;
			this.id = type.getNextId();
			this.interval = interval;
			this.key = key;
			this.hold = hold;
			this.count = this.time = count;
		}

		public void start() {
			starttime = System.currentTimeMillis();
			execute(1);
			if (hold && time > 0)
				MoreCommands.execute(() -> {
					try {
						Thread.sleep(time);
					} catch (InterruptedException e) {
						log.catching(e);
					}
					synchronized (lock) {
						tasks.remove(EmulateTask.this);
					}
					onRemove();
				});
		}

		private void onRemove() {
			count = 1;
			execute(0);
		}

		private void execute(int action) {
			MoreCommands.execute(() -> {
				switch (type) {
					case MOUSE:
						Mouse mouse = MinecraftClient.getInstance().mouse;
						MinecraftClient.getInstance().execute(() -> {
							ignoreMouse = true;
							((MixinMouseAccessor) mouse).callOnMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), key, action, 0);
						});
						if (!hold) MinecraftClient.getInstance().execute(() -> {
							ignoreMouse = true;
							((MixinMouseAccessor) mouse).callOnMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), key, 0, 0);
						});
						break;
					case KEYBOARD:
						Keyboard keyboard = MinecraftClient.getInstance().keyboard;
						keyboard.onKey(MinecraftClient.getInstance().getWindow().getHandle(), key, 0, action, 0);
						if (!hold) keyboard.onKey(MinecraftClient.getInstance().getWindow().getHandle(), key, 0, 0, 0);
						break;
				}
				if ((!hold || type == Type.KEYBOARD) && action != 0 && --count != 0 && (!hold || System.currentTimeMillis() < starttime + time - interval)) {
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						log.catching(e);
						return;
					}
					execute(hold ? 2 : 1);
				} else synchronized (lock) {
					tasks.remove(this);
				}
			});
		}

		@Override
		public String toString() {
			return "button = " + key + (hold ? ", time = " + time + " ms" : ", interval = " + interval + " ms, count = " + count);
		}
	}
}
