package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.mixin.client.accessor.MixinMapColorAccessor;
import org.apache.commons.lang3.ArrayUtils;

import javax.imageio.ImageIO;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MapPicCommand extends ClientCommand {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private BlockPos c1 = null, c2 = null;

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("mappic")
                .executes(ctx -> {
                    ItemStack lookingAt = getMapLookingAt();
                    if (getPlayer().getMainHandItem().getItem() == Items.FILLED_MAP || lookingAt != null) {
                        MapItemSavedData state = getMapState(lookingAt == null ? getPlayer().getMainHandItem() : lookingAt);
                        if (state == null) sendMsg(ChatFormatting.RED + "Could not find a mapstate for the map you're holding or looking at, has it been downloaded yet?");
                        else {
                            try {
                                writeImage(getImage(state, 0));
                            } catch (IOException e) {
                                log.catching(e);
                                sendMsg(ChatFormatting.RED + "The image could not be saved.");
                                return 0;
                            }
                            sendMsg("The image has been saved.");
                            return 1;
                        }
                    } else sendMsg(ChatFormatting.RED + "You must be holding a filled map or looking at an itemframe containing one.");
                    return 0;
                })
                .then(cLiteral("stitch")
                        .executes(ctx -> {
                            if (c1 == null || c2 == null) sendMsg(ChatFormatting.RED + "Not all corners have been set yet, please set them with /mappic stitch c1 and /mappic stitch c2.");
                            else if (!is2d(c1, c2)) sendMsg(ChatFormatting.RED + "The selected region is not 2D. Both corners must be on the same X, Y or Z value.");
                            else {
                                List<Tuple<MapItemSavedData, Integer>> states = getMapsIn(c1, c2);
                                int[] deltas = getDeltas(c1, c2);
                                int width = Math.abs(deltas[0] == 0 ? deltas[2] : deltas[0]) + 1;
                                int height = states.size() / width;
                                BufferedImage[][] images = new BufferedImage[height][width];
                                for (int y = 0; y < height; y++)
                                    for (int x = 0; x < width; x++) {
                                        Tuple<MapItemSavedData, Integer> pair = states.get(deltas[2] == 0 ? x * height + y : y * width + x);
                                        images[y][x] = getImage(pair.getA(), pair.getB());
                                    }
                                try {
                                    writeImage(stitch(images));
                                } catch (IOException e) {
                                    log.catching(e);
                                    sendMsg(ChatFormatting.RED + "The image could not be saved.");
                                    return 0;
                                }
                                sendMsg("The image has been saved.");
                                return 1;
                            }
                            return 0;
                        })
                        .then(cLiteral("c1")
                                .executes(ctx -> executeCorner(0)))
                        .then(cLiteral("c2")
                                .executes(ctx -> executeCorner(1)))));
    }

    @Override
    public String getDocsPath() {
        return "/map-pic";
    }

    private int executeCorner(int corner) {
        if (getMapLookingAt() != null) {
            HitResult result = MoreCommands.getRayTraceTarget(getPlayer(), 160d, false, true);
            if (result instanceof EntityHitResult && ((EntityHitResult) result).getEntity() instanceof ItemFrame && ((ItemFrame) ((EntityHitResult) result).getEntity()).getItem().getItem() == Items.FILLED_MAP) {
                BlockPos pos = new BlockPos(result.getLocation().x() < 0 ? Math.ceil(result.getLocation().x()) : result.getLocation().x(), result.getLocation().y(), result.getLocation().z() < 0 ? Math.ceil(result.getLocation().z()) : result.getLocation().z());
                if (corner == 0) c1 = pos;
                else c2 = pos;
                sendMsg(SF + "Corner " + (corner == 0 ? "1" : "2") + DF + " has been set to " + SF + String.join(DF + ", " + SF, "" + pos.getX(), "" + pos.getY(), "" + pos.getZ()) + DF + ".");
                return 1;
            } else sendMsg(ChatFormatting.RED + "You are not looking at an itemframe holding a filled map.");
        } else sendMsg(ChatFormatting.RED + "You do not appear to be looking at a map.");
        return 0;
    }

    private void writeImage(BufferedImage image) throws IOException {
        if (!new File("maps/").exists()) new File("maps/").mkdir();
        ImageIO.write(image, "png", new File("maps/map_" + format.format(new Date()) + ".png"));
    }

    private MapItemSavedData getMapState(ItemStack stack) {
        return stack.getItem() == Items.FILLED_MAP ? getWorld().getMapData(MapItem.makeKey(Objects.requireNonNull(MapItem.getMapId(stack)))) : null;
    }

    private MapItemSavedData getMapState(BlockPos pos) {
        ItemFrame frame = getFrame(pos);
        return frame == null ? null : getMapState(frame.getItem());
    }

    private int getRotation(BlockPos pos) {
        ItemFrame frame = getFrame(pos);
        return frame == null ? -1 : frame.getRotation();
    }

    private ItemFrame getFrame(BlockPos pos) {
        for (Entity e : getWorld().entitiesForRendering())
            if (e instanceof ItemFrame && (int) e.getX() == pos.getX() && (int) e.getY() == pos.getY() && (int) e.getZ() == pos.getZ())
                return ((ItemFrame) e).getItem().getItem() == Items.FILLED_MAP ? (ItemFrame) e : null;
        return null;
    }

    private boolean is2d(BlockPos from, BlockPos to) {
        return ArrayUtils.contains(getDeltas(from, to), 0);
    }

    private int[] getDeltas(BlockPos from, BlockPos to) {
        int dx = from.getX()-to.getX();
        int dy = from.getY()-to.getY();
        int dz = from.getZ()-to.getZ();
        return new int[] {dx, dy, dz};
    }

    private List<Tuple<MapItemSavedData, Integer>> getMapsIn(BlockPos from, BlockPos to) {
        List<Tuple<MapItemSavedData, Integer>> maps = new ArrayList<>();
        if (is2d(from, to)) {
            int[] deltas = getDeltas(from, to);
            int dx = deltas[0];
            int dy = deltas[1];
            int dz = deltas[2];
            for (int x = from.getX(); dx >= 0 ? x <= from.getX()+dx : x >= from.getX()+dx; x += dx >= 0 ? 1 : -1)
                for (int y = from.getY(); dy >= 0 ? y <= from.getY()+dy : y >= from.getY()+dy; y += dy >= 0 ? 1 : -1)
                    for (int z = from.getZ(); dz >= 0 ? z <= from.getZ()+dz : z >= from.getZ()+dz; z += dz >= 0 ? 1 : -1) {
                        BlockPos pos = new BlockPos(from.getX() - (x - from.getX()), from.getY() - (y - from.getY()), from.getZ() - (z - from.getZ()));
                        maps.add(new Tuple<>(getMapState(pos), getRotation(pos)));
                    }
        }
        return maps;
    }

    private BufferedImage stitch(BufferedImage[][] images) {
        int totalWidth = 0;
        for (BufferedImage img : images[0])
            totalWidth += img.getWidth();
        int totalHeight = 0;
        for (BufferedImage[] imgs : images)
            totalHeight += imgs[0].getHeight();
        BufferedImage img = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        int y = 0;
        for (BufferedImage[] yimgs : images) {
            int x = 0;
            for (BufferedImage ximg : yimgs) {
                g.drawImage(ximg, x, y, null);
                x += ximg.getWidth();
            }
            y += yimgs[0].getHeight();
        }
        g.dispose();
        return img;
    }

    private BufferedImage getImage(MapItemSavedData state, int rotation) {
        rotation %= 4;
        int dim = (int) Math.sqrt(state == null ? 16384 : state.colors.length);
        BufferedImage img = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        if (state == null) return img;
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.dispose();
        for (int i = 0; i < state.colors.length; i++) {
            int colour = Byte.toUnsignedInt(state.colors[i]);
            int ac = colour % 4;
            MaterialColor mcolour = MixinMapColorAccessor.getMaterialColors()[(colour-ac)/4];
            if (mcolour != MaterialColor.NONE) img.setRGB(i % dim, i / dim, getRGB(mcolour, ac));
        }
        while (rotation != 0) {
            img = rotateClockwise90(img);
            rotation -= 1;
        }
        return img;
    }

    private BufferedImage rotateClockwise90(BufferedImage src) {
        int height = src.getWidth();
        int width = src.getHeight();
        BufferedImage dest = new BufferedImage(width, height, src.getType());
        Graphics2D g = dest.createGraphics();
        g.translate((height - width) / 2, (height - width) / 2);
        g.rotate(Math.PI / 2, height / 2f, width / 2f);
        g.drawRenderedImage(src, null);
        g.dispose();
        return dest;
    }

    private int getRGB(MaterialColor color, int shade) {
        // The colour used for rendering (MaterialColor#getRenderColor) returns a value
        // that's not RGB, but this is, so we use this instead.
        Color c = new Color(color.col);
        int i = shade == 0 ? 180 : shade == 1 ? 220 : shade == 2 ? 255 : 135;
        return new Color(c.getRed()*i/255,  c.getGreen()*i/255, c.getBlue()*i/255).getRGB();
    }

    private ItemStack getMapLookingAt() {
        HitResult result = MoreCommands.getRayTraceTarget(getPlayer(), 160d, false, true);
        if (result instanceof EntityHitResult) {
            EntityHitResult eresult = (EntityHitResult) result;
            if (eresult.getEntity() instanceof ItemFrame) {
                ItemStack stack = ((ItemFrame) eresult.getEntity()).getItem();
                if (stack.getItem() == Items.FILLED_MAP) return stack;
            }
        }
        return null;
    }

}
