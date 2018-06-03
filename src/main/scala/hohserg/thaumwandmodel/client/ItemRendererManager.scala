package hohserg.thaumwandmodel.client

import hohserg.thaumwandmodel.Main
import hohserg.thaumwandmodel.Main.thaumcraftWandModelModId
import hohserg.thaumwandmodel.client.baked.WandModel
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model._
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.{ModelBakeEvent, TextureStitchEvent}
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.mutable.ListBuffer

object ItemRendererManager{
  val textureCap=new ResourceLocation(Main.thaumcraftWandModelModId+":rods_and_caps/wand_cap_thaumium")
  val textureRod=new ResourceLocation(Main.thaumcraftWandModelModId+":rods_and_caps/wand_rod_silverwood")
  val wandModelLocation = new ModelResourceLocation(thaumcraftWandModelModId+":ItemWandCasting", "inventory")

  def registerItemRenderer(item: Item, itemModelResourceLocation: ModelResourceLocation): Unit = {
    Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0, itemModelResourceLocation)
    ModelLoader.setCustomModelResourceLocation(item, 0, itemModelResourceLocation)
  }

  private val forRegister=new ListBuffer[ResourceLocation]
  def registerTexture(resourceLocation: ResourceLocation): Unit = forRegister+=resourceLocation
}

class ItemRendererManager {
  @SubscribeEvent
  def stitcherEventPre(event:TextureStitchEvent.Pre) {
    ItemRendererManager.forRegister.foreach(event.getMap.registerSprite)
  }

  @SubscribeEvent
  def bakeModel(event: ModelBakeEvent): Unit = {
    //event.getModelRegistry.putObject(itemModelResourceLocation, customModel)

    val `object` = event.getModelRegistry.getObject(ItemRendererManager.wandModelLocation)
    `object` match {
      case existingModel: IBakedModel =>
        val customModel = new WandModel(existingModel)
        event.getModelRegistry.putObject(ItemRendererManager.wandModelLocation, customModel)
      case _ =>
    }
  }
}
