package hcs;

import java.util.HashMap;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.Mod;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.common.Mod.EventHandler;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

@Mod(modid = "HCS")
public class Main {
	private static Reflection reflect;
	private static boolean[] KeyStates = new boolean[256];
	private static HashMap<String, Boolean> functions = new HashMap<String, Boolean>();

	@EventHandler
	public void init(FMLInitializationEvent event) {
		reflect = new Reflection();
		MinecraftForge.EVENT_BUS.register(this);
	}

	// public Main() {
	// reflect = new Reflection();
	// MinecraftForge.EVENT_BUS.register(this);
	// }

	@ForgeSubscribe
	public void onGuiRender(RenderGameOverlayEvent.Text event) {
		if (checkKey(Keyboard.KEY_H)) {
			functions.put("wallhack", !functions.getOrDefault("wallhack", false));
		}
		if(checkKey(Keyboard.KEY_J)) {
			if(!functions.getOrDefault("fullbright", false)) {
				reflect.addNightVision();
			} else {
				reflect.removeNightVision();
			}
			functions.put("fullbright", !functions.getOrDefault("fullbright", false));
		}
	}

	@ForgeSubscribe
	public void onRender(RenderWorldLastEvent event) {
		if (functions.getOrDefault("wallhack", false)) {
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			for (Object player : reflect.playerEntities()) {
				if (player != reflect.player()) {
					reflect.setPositionAndUpdate(player, reflect.getServerPosX(player) / 32.0D,
							reflect.getServerPosY(player) / 32.0D, reflect.getServerPosZ(player) / 32.0D);
					reflect.renderEntity(player, event.partialTicks);
				}
			}
		}
	}

	private boolean checkKey(int key) {
		return reflect.currentScreen() != null ? false
				: (Keyboard.isKeyDown(key) != KeyStates[key] ? (KeyStates[key] = !KeyStates[key]) : false);
	}
}