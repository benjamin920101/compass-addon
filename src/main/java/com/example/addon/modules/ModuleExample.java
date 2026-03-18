package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class ModuleExample extends Module {
    private static final Color GREEN = new Color(25, 225, 25);

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The size of the marker.")
        .defaultValue(2.0d)
        .range(0.5d, 10.0d)
        .build()
    );

    private final List<PlayerEntity> players = new ArrayList<>();

    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public ModuleExample() {
        super(AddonTemplate.CATEGORY, "world-origin", "An example module that highlights players with green boxes.");
    }

    @Override
    public void onActivate() {
        updatePlayers();
    }

    private void updatePlayers() {
        players.clear();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
                players.add((PlayerEntity) entity);
            }
        }
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (event.entity instanceof PlayerEntity && event.entity != mc.player) {
            if (!players.contains(event.entity)) {
                players.add((PlayerEntity) event.entity);
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        updatePlayers();
    }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        for (PlayerEntity player : players) {
            renderPlayerBox(event, player);
        }
    }

    private void renderPlayerBox(Render3DEvent event, PlayerEntity player) {
        double halfWidth = player.getWidth() / 2;
        double x = player.getX() - halfWidth;
        double y = player.getY();
        double z = player.getZ() - halfWidth;
        double xWidth = player.getBoundingBox().getLengthX();
        double zWidth = player.getBoundingBox().getLengthZ();
        double height = player.getBoundingBox().getLengthY();

        Box marker = new Box(x, y, z, x + xWidth, y + height, z + zWidth);
        marker.stretch(
            scale.get() * marker.getLengthX(),
            scale.get() * marker.getLengthY(),
            scale.get() * marker.getLengthZ()
        );

        event.renderer.box(marker, GREEN, GREEN, ShapeMode.Both, 0);
    }
}
