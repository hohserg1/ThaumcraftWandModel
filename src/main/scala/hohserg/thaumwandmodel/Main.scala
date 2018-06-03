package hohserg.thaumwandmodel

import hohserg.thaumwandmodel.client.ItemRendererManager
import hohserg.thaumwandmodel.items._
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.common.{Mod, SidedProxy}

@Mod(name="ThaumcraftWandModel",modid = Main.thaumcraftWandModelModId, version="1.0",modLanguage = "scala")
object Main {
  @SidedProxy(clientSide = "hohserg.thaumwandmodel.ClientProxy",serverSide = "hohserg.thaumwandmodel.ServerProxy")
  var proxy:CommonProxy=_

  final val thaumcraftWandModelModId="thaumcraftwandmodel"

  @Mod.EventHandler def preinit(event: FMLPreInitializationEvent): Unit = {
    proxy.preinit(event)
  }

  @Mod.EventHandler def init(event: FMLInitializationEvent): Unit = {
    proxy.init(event)
  }

  @Mod.EventHandler def postinit(event: FMLPostInitializationEvent): Unit = {
    proxy.postinit(event)

  }

}
class ServerProxy extends CommonProxy{}

class ClientProxy extends CommonProxy{

  override def init(event: FMLInitializationEvent): Unit = {
    super.init(event)
    val itemRendererManager=new ItemRendererManager
    MinecraftForge.EVENT_BUS.register(itemRendererManager)
    ItemRendererManager.registerItemRenderer(ItemWandCasting,ItemRendererManager.wandModelLocation)
    ItemRendererManager.registerTexture(ItemRendererManager.textureCap)
    ItemRendererManager.registerTexture(ItemRendererManager.textureRod)
  }

}

class CommonProxy{

  lazy val tab=new CreativeTabs(Main.thaumcraftWandModelModId) {
    override def getTabIconItem: Item = ItemWandCasting
  }

  def preinit(event: FMLPreInitializationEvent): Unit = {
    val name=ItemWandCasting.getClass.getSimpleName.dropRight(1).toLowerCase
    ItemWandCasting.setUnlocalizedName(name).setCreativeTab(tab)
    GameRegistry.register(ItemWandCasting.setRegistryName(name))
  }

  def init(event: FMLInitializationEvent): Unit = {

  }

  def postinit(event: FMLPostInitializationEvent): Unit = {

  }
}