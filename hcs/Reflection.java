package hcs;

import java.util.List;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import com.google.common.collect.Lists;

public class Reflection {
	private HashMap<String, Field> fields = new HashMap();
	private HashMap<String, Method> methods = new HashMap();
	private HashMap<String, Object> objects = new HashMap();

	public Reflection() {
		try {
			Class Minecraft = Class.forName("cpw.mods.fml.client.FMLClientHandler").getDeclaredMethod("getClient")
					.getReturnType();
			Field theMinecraft = null;
			for (Field f : Minecraft.getDeclaredFields()) {
				if (f.getType().equals(Minecraft)) {
					theMinecraft = f;
				}
			}
			theMinecraft.setAccessible(true);
			Object mc = theMinecraft.get(new Object[0]);

			Field thePlayer = null;
			Class EntityPlayerSP = null;
			for (Method m : Class.forName("net.minecraftforge.client.ForgeHooksClient").getDeclaredMethods()) {
				if (m.getModifiers() == 9 && m.getParameterCount() == 2 && m.getReturnType().equals(float.class)) {
					EntityPlayerSP = m.getParameterTypes()[0]; // getOffsetFOV
				}
			}
			for (Field f : Minecraft.getDeclaredFields()) {
				Class c = f.getType();
				if (c.getDeclaredFields().length >= 13 && c.getDeclaredMethods().length >= 20 && c.getModifiers() >= 1
						&& c.getConstructors().length >= 1) {
					if (c.getSuperclass() != null) {
						if (f.getType().getSuperclass().equals(EntityPlayerSP)) {
							thePlayer = f;
						}
					}
				}
			}

			Class EntityLivingBase = thePlayer.getType().getSuperclass().getSuperclass().getSuperclass()
					.getSuperclass();
			Class Entity = EntityLivingBase.getSuperclass();

			Class World = null;
			for (Method m : Class.forName("cpw.mods.fml.common.registry.GameRegistry").getDeclaredMethods()) {
				if (m.getModifiers() == 9 && m.getParameterCount() == 5 && m.getReturnType().equals(void.class)) {
					World = m.getParameterTypes()[2];
				}
			}

			Field worldObj = null;
			for (Field f : Entity.getDeclaredFields()) {
				if (f.getType().equals(World)) {
					worldObj = f;
				}
			}

			Field playerEntities = worldObj.getType().getDeclaredFields()[8];

			Class Render = null;
			for (Method m : Class.forName("cpw.mods.fml.client.registry.RenderingRegistry").getDeclaredMethods()) {
				if (m.getModifiers() == 9 && m.getParameterCount() == 2 && m.getReturnType().equals(void.class)) {
					if (!m.getParameterTypes()[0].equals(int.class)) {
						Render = m.getParameterTypes()[1]; // registerEntityRenderingHandler
					}
				}
			}

			Class RenderManager = null;// renderManager
			for (Method m : Render.getDeclaredMethods()) {
				if (m.getModifiers() == 1 && m.getParameterCount() == 1 && m.getReturnType().equals(void.class)) {
					for (Field f : Render.getDeclaredFields()) {
						Class type = f.getType();
						if (type.equals(m.getParameterTypes()[0])) {
							for (Field field : type.getDeclaredFields()) {
								if (field.getType().equals(type)) {
									RenderManager = type;
								}
							}
						}
					}
				}
			}

			Field renderManagerInstance = null;// instance
			for (Field f : RenderManager.getDeclaredFields()) {
				if (f.getType().equals(RenderManager)) {
					renderManagerInstance = f;
				}
			}

			Method renderEntity = null;
			for (Method m : RenderManager.getDeclaredMethods()) {
				if (m.getParameterCount() == 2 && m.getReturnType().equals(void.class)) {
					if (m.getParameterTypes()[1].equals(float.class)) {
						renderEntity = m; // renderEntity
					}
				}
			}

			Field serverPosX = null;
			Field serverPosY = null;
			Field serverPosZ = null;

			List<Field> serverPos = Lists.newArrayList();
			for (Field f : Entity.getDeclaredFields()) {
				if (f.getModifiers() == 1 && f.getDeclaredAnnotations().length == 1) {
					serverPos.add(f);
				}
			}

			serverPosX = serverPos.get(0);
			serverPosY = serverPos.get(1);
			serverPosZ = serverPos.get(2);

			Method setPositionAndUpdate = null;
			for (Method m : EntityLivingBase.getDeclaredMethods()) {
				if (m.getModifiers() == 1 && m.getReturnType().equals(void.class) && m.getParameterCount() == 3) {
					Class[] parametrs = m.getParameterTypes();
					if (parametrs[0].equals(double.class) && parametrs[1].equals(double.class)
							&& parametrs[2].equals(double.class)) {
						setPositionAndUpdate = m;
					}
				}
			}

			Class GuiScreen = null;
			for (Method m : Class.forName("cpw.mods.fml.client.FMLClientHandler").getDeclaredMethods()) {
				if (m.getParameterCount() == 2 && m.getReturnType().equals(void.class)) {
					if (m.getParameterTypes()[0].equals(EntityPlayerSP.getSuperclass().getSuperclass())) {
						GuiScreen = m.getParameterTypes()[1];
					}
				}
			}

			Field currentScreen = null;
			for (Field f : Minecraft.getDeclaredFields()) {
				if (f.getType().equals(GuiScreen)) {
					currentScreen = f;
				}
			}

			Class PotionEffect = null;
			for (Method m : EntityLivingBase.getDeclaredMethods()) {
				if (m.getModifiers() == 1 && m.getParameterCount() == 1 && m.getReturnType().equals(void.class)) {
					Class c = m.getParameterTypes()[0];
					if(c.getDeclaredConstructors().length == 4) {
						PotionEffect = c;
					}
				}
			}
			
			Object NightVisionEffect = PotionEffect.getDeclaredConstructor(new Class[] {int.class, int.class}).newInstance(new Object[] {16, Integer.MAX_VALUE}); 
			
			Field activePotionsMap = null;
			for(Field f : EntityLivingBase.getDeclaredFields()) {
				if(f.getType().equals(HashMap.class)) {
					activePotionsMap = f;
				}
			}
			activePotionsMap.setAccessible(true);
			Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(activePotionsMap, activePotionsMap.getModifiers() & ~Modifier.FINAL);
            
			fields.put("player", thePlayer);
			fields.put("worldObj", worldObj);
			fields.put("playerEntities", playerEntities);
			fields.put("serverPosX", serverPosX);
			fields.put("serverPosY", serverPosY);
			fields.put("serverPosZ", serverPosZ);
			fields.put("currentScreen", currentScreen);
			fields.put("activePotionsMap", activePotionsMap);

			methods.put("renderEntity", renderEntity);
			methods.put("setPositionAndUpdate", setPositionAndUpdate);

			objects.put("mc", mc);
			objects.put("renderManager", renderManagerInstance.get(new Object[0]));
			objects.put("NightVisionEffect", NightVisionEffect);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Object mc() {
		return objects.get("mc");
	}

	public Object player() {
		try {
			return fields.get("player").get(mc());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Object[0];
	}

	public Object world() {
		try {
			return fields.get("worldObj").get(player());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Object[0];
	}

	public Object currentScreen() {
		try {
			return fields.get("currentScreen").get(mc());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Object[0];
	}

	public Object renderManager() {
		try {
			return objects.get("renderManager");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Object[0];
	}

	public int getServerPosX(Object entity) {
		try {
			return fields.get("serverPosX").getInt(entity);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public int getServerPosY(Object entity) {
		try {
			return fields.get("serverPosY").getInt(entity);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public int getServerPosZ(Object entity) {
		try {
			return fields.get("serverPosZ").getInt(entity);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public List playerEntities() {
		try {
			return (List) fields.get("playerEntities").get(world());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return Lists.newArrayList();
	}

	public void renderEntity(Object entity, float partialTicks) {
		try {
			methods.get("renderEntity").invoke(renderManager(), new Object[] { entity, partialTicks });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setPositionAndUpdate(Object entity, double posX, double posY, double posZ) {
		try {
			methods.get("setPositionAndUpdate").invoke(entity, new Object[] { posX, posY, posZ });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void addNightVision() {
		try {
			HashMap activePotionsMap = (HashMap) fields.get("activePotionsMap").get(player());
			activePotionsMap.put(16, objects.get("NightVisionEffect"));
			fields.get("activePotionsMap").set(player(), activePotionsMap);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void removeNightVision() {
		try {
			HashMap activePotionsMap = (HashMap) fields.get("activePotionsMap").get(player());
			activePotionsMap.remove(16);
			fields.get("activePotionsMap").set(player(), activePotionsMap);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}